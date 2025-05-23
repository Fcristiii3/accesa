package org.example.price_comparator.service;

import lombok.RequiredArgsConstructor;
import org.example.price_comparator.model.*;
import org.example.price_comparator.repository.AlertRepository;
import org.example.price_comparator.repository.ProductRepository;
import org.example.price_comparator.repository.StoreProductRepository;
import org.example.price_comparator.repository.StoreRepository;
import org.example.price_comparator.utils.Notification;
import org.hibernate.event.spi.SaveOrUpdateEvent;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.*;
///find a way to communicate between frontend and backend the products
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final StoreProductRepository storeProductRepository;
    private final AlertRepository alertRepository;
    private final SmtpService smtpService;
    private final StoreRepository storeRepository;
    //i believe it is more helpful to find the best overall price, not the biggest discount.
    private StoreProduct findBestDiscount(Product product) {
        List<Product> products;
        List<StoreProduct> storeProducts = storeProductRepository.findAllByProduct(product);
        for (StoreProduct storeProduct : storeProducts) {
            //System.out.println(storeProduct.getId()+" "+storeProduct.getCurrency()+" "+storeProduct.getPrice()+" "+storeProduct.getProduct().getProductId()+" "+storeProduct.getStore().getStoreId());
        }
        float minPrice = Float.MAX_VALUE;
        StoreProduct bestDiscount = null;
        for (StoreProduct sp : storeProducts) {
            float price = sp.getPrice();
            if(price<minPrice){
                minPrice = price;
                bestDiscount=sp;
            }

        }
        return bestDiscount;
    }

    //considered that a user wants to check the best price for some items. i don't think they need to see all the existing items
    public Notification<List<BasketDTO>> bestDiscounts(List<String> products){
        List<BasketDTO> optimisedProducts = new ArrayList<>();
        List<Product> actualProduct = new ArrayList<>();
        for (String product : products) {
            try {
                Optional<Product> maybeProduct = productRepository.findById(product);
                maybeProduct.ifPresent(actualProduct::add);
            }
            catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
        for(Product product : actualProduct) {
            StoreProduct bestDeal = findBestDiscount(product);

            System.out.println(bestDeal);
            BasketDTO basketDTO = new BasketDTO();
            basketDTO.setProduct(String.valueOf(product.getProductId()));
            //basketDTO.setProductName(product.getName());
            basketDTO.setStore(bestDeal.getStore().getName());
            if(bestDeal.hasActiveDiscount())basketDTO.setDiscount(basketDTO.getDiscount());
            basketDTO.setBasePrice(bestDeal.getPrice());
            optimisedProducts.add(basketDTO);
        }
        Notification<List<BasketDTO>> notification = new Notification<>();
        notification.setResult(optimisedProducts);
        notification.setSuccessMessage("Best price for given items");
        return notification;
    }

    public Notification<Map<String,List<BasketDTO>>> optimiseBasket(List<String> productIDs){
        Map<String,List<BasketDTO>> optimisedProducts = new HashMap<>();
        List<Product> products = new ArrayList<>();
        Notification<Map<String,List<BasketDTO>>> notification = new Notification<>();
        for(String productID : productIDs){
            try{
                Optional<Product> maybeProduct = productRepository.findById(productID);
                maybeProduct.ifPresent(products::add);
            }
            catch (Exception e){
                notification.addError("Couldn't find product " + productID);
            }
        }
        for(Product product : products) {
            StoreProduct bestDeal = findBestDiscount(product);
            BasketDTO basketDTO = new BasketDTO();
            basketDTO.setProduct(String.valueOf(product.getProductId()));
            //basketDTO.setProductName(product.getName());
            basketDTO.setStore(bestDeal.getStore().getName());
            basketDTO.setBasePrice(bestDeal.getPrice());
            basketDTO.setDiscount(basketDTO.getDiscount());
            System.out.println(basketDTO.getProduct());
            System.out.println(basketDTO.getStore());

            List<BasketDTO> existingBasket = optimisedProducts.get(basketDTO.getStore());
            if(existingBasket==null){
                existingBasket = new ArrayList<>();
                existingBasket.add(basketDTO);
            }
            else{
                existingBasket.add(basketDTO);
            }
            optimisedProducts.put(basketDTO.getStore(), existingBasket);
        }

        notification.setResult(optimisedProducts);
        notification.setSuccessMessage("Optimised basket");
        return notification;
    }

    public Notification<List<BasketDTO>> newDiscounts(){
        Notification<List<BasketDTO>> notification = new Notification<>();

        List<StoreProduct> storeProducts = storeProductRepository.findAll();
        List<BasketDTO> newlyDiscounted = new ArrayList<>();
        Date now = new Date();
        for (StoreProduct sp: storeProducts) {
            if(sp.hasActiveDiscount()) {
                BasketDTO basketDTO = new BasketDTO();
                Date ago24 = new Date(now.getTime() - 24 * 60 * 60 * 1000);
                if(sp.getCurrentDiscount().get().getFrom_date().after(ago24)) {
                basketDTO.setDiscount(sp.getCurrentDiscount().get().getPercentage_of_discount());
                basketDTO.setBasePrice(sp.getPrice());
                basketDTO.setStore(sp.getStore().getName());
                basketDTO.setProduct(String.valueOf(sp.getProduct().getProductId()));
                //basketDTO.setProductName(sp.getProduct().getName());
                newlyDiscounted.add(basketDTO);
                }
            }

        }
        notification.setResult(newlyDiscounted);
        notification.setSuccessMessage("Newly discounted");
        return notification;
    }

    public Notification<StoreProductDTO> dynamicHistory(String productID,String shopID){
        Notification<StoreProductDTO> notification = new Notification<>();
        StoreProductDTO foundProduct = new StoreProductDTO();
        StoreProduct storeProduct;
        Optional<Product> maybeProduct;
        Optional<Store> maybeshop;
        try{
            maybeProduct = productRepository.findById(productID);
            maybeshop = storeRepository.findById(shopID);
        }
        catch (Exception e){
            notification.addError("Shop or product is invalid");
            notification.setResult(null);
            return notification;
        }
        try{
            storeProduct = storeProductRepository.findByProductAndStore(maybeProduct.get(),maybeshop.get());
            foundProduct.setProductID(String.valueOf(storeProduct.getProduct().getProductId()));
            foundProduct.setDiscounts(storeProduct.getDiscounts());
            foundProduct.setCurrency(storeProduct.getCurrency());
            foundProduct.setPrice(storeProduct.getPrice());
            foundProduct.setStoreName(storeProduct.getStore().getName());
            notification.setResult(foundProduct);
            notification.setSuccessMessage("Dynamic history");
        }
        catch(Exception e){
            notification.setResult(null);
            notification.addError(e.getMessage());
        }
        return notification;
    }

    public Notification<AlertDTO> setAlert(String email, String productID, float targetPrice){
    Notification<AlertDTO> notification = new Notification<>();
    Optional<Product> maybeProduct;
    try{
    maybeProduct = productRepository.findById(productID);
    }
    catch (Exception e){
        notification.addError("Couldn't find product " + productID);
        notification.setResult(null);
        return notification;
    }
    AlertDTO alertDTO = new AlertDTO();
    Alert alert = new Alert();
    alert.setEmail(email);
    alert.setProduct(maybeProduct.get());
    alert.setTargetPrice(targetPrice);
    try{
        alertRepository.save(alert);
    }
    catch(Exception e){
        notification.setResult(null);
        notification.addError(e.getMessage());
    }
    alertDTO.setEmail(email);
    notification.setResult(alertDTO);
    notification.setSuccessMessage("Alert set");
    return notification;
    }

    @Scheduled(fixedRate = 36000000)
    public void checkAlerts(){
        List<Alert> alerts = alertRepository.findAll();
        Optional<StoreProduct> product;
        for(Alert alert : alerts){
            try {
                product = Optional.ofNullable(storeProductRepository.findByProduct(alert.getProduct()));
                if(product.isPresent()){
                    if(alert.getTargetPrice()<product.get().getPrice()){
                        smtpService.sendAlert(alert.getEmail(),product.get().getProduct().getName(),product.get().getPrice(),product.get().getStore().getName());
                    }
                }
            }
            catch (Exception e) {
                System.out.println(e);
            }

        }

    }

    public Notification<List<StoreProductDTO>> findSubstitutes(String product){
        Optional<Product> actualProduct;
        Notification<List<StoreProductDTO>> notification = new Notification<>();
        try{
            actualProduct = productRepository.findById(product);
        }
        catch(Exception e){
            notification.setResult(null);
            notification.addError(e.getMessage());
            return notification;
        }
        List<StoreProduct> substitutes = storeProductRepository.findAllByProduct_Category(actualProduct.get().getCategory());
        List<StoreProductDTO> subs = substitutes.stream()
                .sorted(Comparator.comparing(StoreProduct::getPricePerKGorL))
                .map(sp -> new StoreProductDTO(
                        sp.getProduct().getName(),
                        sp.getPrice(),
                        sp.getCurrency(),
                        sp.getDiscounts(),
                        sp.getStore().getName()
                ))
                .toList();

        notification.setResult(subs);
        notification.setSuccessMessage("Substitutes");
        return notification;
    }

}
