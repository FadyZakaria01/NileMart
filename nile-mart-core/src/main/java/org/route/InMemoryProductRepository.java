package org.route;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Component
public class InMemoryProductRepository implements ProductRepository{

    private final HashMap<String, Product> store = new HashMap<>();

    @PostConstruct
    void saveProducts() {
        Product p1 = Product.builder()
                .name("Galaxy S25")
                .sku("PH-SAM-S25")
                .price(BigDecimal.valueOf(10000))
                .category("Phone")
                .isActive(true)
                .build();
        Product p2 = Product.builder()
                .name("ASUS TUF F16")
                .sku("LAP-ASUS-F16")
                .price(BigDecimal.valueOf(30000))
                .category("Laptop")
                .isActive(true)
                .build();

        store.put(p1.getSku(), p1);
        store.put(p2.getSku(), p2);
    }

    @PreDestroy
    public void closed() {
        System.out.println("Catalog closed");
    }

    @Override
    public Optional<Product> findBySku(String sku) {
        return Optional.ofNullable(store.get(sku));
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(store.values());
    }

}
