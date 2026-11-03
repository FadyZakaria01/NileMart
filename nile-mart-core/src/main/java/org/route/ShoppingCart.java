package org.route;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
@Data
@Component
@Scope("prototype")
public class ShoppingCart {
    private final String cartNumber;
    private  Customer customer;
    private final Map<String, Integer> lines = new HashMap<>();

    private final ProductService productService;
    private final int maxQuantityPerProduct;

    public ShoppingCart(

            ProductService productService,
            @Value("${store.max-quantity-per-product}") int maxQuantityPerProduct) {

        this.cartNumber = UUID.randomUUID().toString();
        this.productService = productService;
        this.maxQuantityPerProduct = maxQuantityPerProduct;
    }

    public void addItem(String sku, int qty) {
        if (qty < 0) {
            throw new StoreException("qty should be greater than 0");
        }

        Product product = productService.findBySku(sku)
                .orElseThrow(() ->
                        new StoreException("Product not found: " + sku));

        if (product.getStockQuantity() <= 0) {
            throw new StoreException("Product is out of stock");
        }

        int currentQuantity = lines.getOrDefault(sku, 0);
        int newQuantity = currentQuantity + qty;

        if (newQuantity > maxQuantityPerProduct) {
            throw new IllegalArgumentException(
                    "Maximum quantity per product is " + maxQuantityPerProduct);
        }
        lines.put(sku, newQuantity);
    }

    public BigDecimal subTotal() {
        BigDecimal subTotal = BigDecimal.valueOf(0);
        for (Map.Entry<String, Integer> entry : lines.entrySet()) {
            String sku = entry.getKey();
            int quantity = entry.getValue();
            subTotal = subTotal.add(productService.getPrice(sku, quantity));
        }
        return subTotal;
    }


    public boolean isEmpty() {
        return lines.isEmpty();
    }

    public void clear() {
        lines.clear();
    }

}
