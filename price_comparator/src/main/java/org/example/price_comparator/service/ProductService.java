package org.example.price_comparator.service;

import lombok.RequiredArgsConstructor;
import org.example.price_comparator.model.*;
import org.example.price_comparator.repository.AlertRepository;
import org.example.price_comparator.repository.ProductRepository;
import org.example.price_comparator.repository.StoreProductRepository;
import org.example.price_comparator.utils.Notification;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final StoreProductRepository storeProductRepository;
    private final AlertRepository alertRepository;
    private final SmtpService smtpService;
    //i believe it is more helpful to find the best overall price, not the biggest discount.
    private StoreProduct findBestDiscount(Product product) {
        List<Product> products;
        List<StoreProduct> storeProducts = storeProductRepository.findAllByProduct(product);
        float minPrice = Float.MAX_VALUE;
        StoreProduct bestDiscount = null;
        for (StoreProduct sp : storeProducts) {
            float price = sp.getPrice();
            Optional<Discount> discount = sp.getCurrentDiscount();
            if(discount.isPresent()) {
            if(price<minPrice){
                minPrice = price;
                bestDiscount=sp;
            }
            }
        }
        return bestDiscount;
    }

    //considered that a user wants to check the best price for some items. i don't think they need to see all the existing items
    public Notification<List<BasketDTO>> bestDiscounts(List<Product> products){
        List<BasketDTO> optimisedProducts = new ArrayList<>();
        for(Product product : products) {
            StoreProduct bestDeal = findBestDiscount(product);
            BasketDTO basketDTO = new BasketDTO();
            basketDTO.setProduct(product.getName());
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

    public Notification<Map<String,List<BasketDTO>>> optimiseBasket(List<Product> products){
        Map<String,List<BasketDTO>> optimisedProducts = new HashMap<>();
        for(Product product : products) {
            StoreProduct bestDeal = findBestDiscount(product);
            BasketDTO basketDTO = new BasketDTO();
            basketDTO.setProduct(product.getName());
            basketDTO.setStore(bestDeal.getStore().getName());
            basketDTO.setBasePrice(bestDeal.getPrice());
            basketDTO.setDiscount(basketDTO.getDiscount());
            //
            optimisedProducts.get(basketDTO.getStore()).add(basketDTO);
        }
        Notification<Map<String,List<BasketDTO>>> notification = new Notification<>();
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
                basketDTO.setProduct(sp.getProduct().getName());
                newlyDiscounted.add(basketDTO);
                }
            }

        }
        notification.setResult(newlyDiscounted);
        notification.setSuccessMessage("Newly discounted");
        return notification;
    }

    public Notification<StoreProductDTO> dynamicHistory(Product product){
        Notification<StoreProductDTO> notification = new Notification<>();
        StoreProductDTO foundProduct = new StoreProductDTO();
        StoreProduct storeProduct = new StoreProduct();
        try{
            storeProduct = storeProductRepository.findByProduct(product);
            foundProduct.setProductName(storeProduct.getProduct().getName());
            foundProduct.setDiscounts(storeProduct.getDiscounts());
            foundProduct.setCurrency(storeProduct.getCurrency());
            notification.setResult(foundProduct);
            notification.setSuccessMessage("Dynamic history");
        }
        catch(Exception e){
            notification.setResult(null);
            notification.addError(e.getMessage());
        }
        return notification;
    }

    public Notification<AlertDTO> setAlert(String email, Product product, float targetPrice){
    Notification<AlertDTO> notification = new Notification<>();

    AlertDTO alertDTO = new AlertDTO();
    Alert alert = new Alert();
    alert.setEmail(email);
    alert.setProduct(product);
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

}
