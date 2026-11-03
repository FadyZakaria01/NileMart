package org.route;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository repository;
    private final PricingService pricingService;

    public ProductService(@Qualifier("inMemoryProductRepository") ProductRepository repository,
                          PricingService pricingService) {
        this.repository = repository;
        this.pricingService = pricingService;
    }

    public BigDecimal getPrice(String sku, int qty) {
        Product product = repository.findBySku(sku)
                .filter(Product::isActive)
                .orElseThrow(() -> new RuntimeException("Product isn't available."));
        return pricingService.getPrice(product, qty);
    }

    public Optional<Product> findBySku(String sku) {
        return Optional.of(repository.findBySku(sku)
                .orElseThrow(() ->
                        new StoreException("Product not found: " + sku)));
    }

    public List<Product> findAll() {
        return repository.findAll();
    }

    public void reduceStock(String sku, int qty) {
        Optional<Product> product = repository.findBySku(sku);

        if (qty <= 0) {
            throw new StoreException("Quantity must be positive");
        }

        if (product.get().getStockQuantity() < qty) {
            throw new StoreException("Insufficient stock: " + sku);
        }
        product.get().setStockQuantity(product.get().getStockQuantity() - qty);

    }

}
