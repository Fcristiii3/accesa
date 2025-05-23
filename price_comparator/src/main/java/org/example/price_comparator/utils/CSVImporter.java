
package org.example.price_comparator.utils;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import lombok.RequiredArgsConstructor;
import org.example.price_comparator.constant.MeasuringUnit;
import org.example.price_comparator.model.Discount;
import org.example.price_comparator.model.Product;
import org.example.price_comparator.model.Store;
import org.example.price_comparator.model.StoreProduct;
import org.example.price_comparator.repository.DiscountRepository;
import org.example.price_comparator.repository.ProductRepository;
import org.example.price_comparator.repository.StoreProductRepository;
import org.example.price_comparator.repository.StoreRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CSVImporter implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final StoreProductRepository storeProductRepository;
    private final DiscountRepository discountRepository;

    @Override
    public void run(String... args) throws Exception {
        importStoreData("lidl", "2025-05-08");
        importStoreData("kaufland", "2025-05-08");
        importStoreData("profi", "2025-05-08");
    }



    private void importStoreData(String storeName, String date) throws IOException, CsvValidationException {
        Store store = storeRepository.findByName(storeName).orElseGet(() -> {
            Store s = new Store();
            s.setName(storeName);
            return storeRepository.save(s);
        });
        String file = "C:/Jobs/accesa/accesa/price_comparator/src/main/java/org/example/price_comparator/data/" + storeName + "_" + date + ".csv";

        FileReader fileReader = new FileReader(file);

        CSVParser parser = new CSVParserBuilder()
                .withSeparator(';')
                .build();

        CSVReader reader = new CSVReaderBuilder(fileReader)
                .withCSVParser(parser)
                .build();

            String[] line;
            reader.readNext();
            while ((line = reader.readNext()) != null) {
                Product product = productRepository.findBycsvID(line[0]);
                if (product == null) {
                    product = new Product();
                }
                product.setCsvID(line[0]);
                product.setName(line[1]);
                product.setCategory(line[2]);
                product.setBrand(line[3]);
                product.setPackageQuantity(Float.parseFloat(line[4]));
                product.setUnit(MeasuringUnit.valueOf(line[5].toUpperCase()));
                productRepository.save(product);

                StoreProduct sp = new StoreProduct();
                sp = storeProductRepository.findByProductAndStore(product, store);
                if(sp == null)sp=new StoreProduct();
                sp.setProduct(product);
                sp.setStore(store);
                sp.setPrice(Float.parseFloat(line[6]));
                sp.setCurrency(line[7]);
                storeProductRepository.save(sp);

            }

        String file2 = "C:/Jobs/accesa/accesa/price_comparator/src/main/java/org/example/price_comparator/data/" + storeName + "_discounts_" + date + ".csv";

        FileReader fileReader2 = new FileReader(file2);

        CSVParser parser2 = new CSVParserBuilder()
                .withSeparator(';')
                .build();

        CSVReader reader2 = new CSVReaderBuilder(fileReader2)
                .withCSVParser(parser2)
                .build();

        String[] line2;

        File discountsFile = new File(file2);
        if (discountsFile.exists()) {
            reader.readNext();
            while ((line2 = reader2.readNext()) != null) {
                    Product product = productRepository.findBycsvID(line2[0]);
                    if (product == null) continue;

                    Optional<StoreProduct> spOpt = Optional.ofNullable(storeProductRepository.findByProductAndStore(product, store));
                    if (spOpt.isEmpty()) continue;

                    Discount discount = new Discount();
                    discount.setStoreProduct(spOpt.get());
                    discount.setFrom_date(Date.valueOf(line2[6]));
                    discount.setTo_date(Date.valueOf(line2[7]));
                    discount.setPercentage_of_discount(Float.parseFloat(line2[8]));

                    discountRepository.save(discount);

            }

        }
    }
}
