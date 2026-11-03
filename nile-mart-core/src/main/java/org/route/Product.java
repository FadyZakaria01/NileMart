package org.route;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Builder
public class Product {
    private String name;
    private String sku;
    private BigDecimal price;
    private  int stockQuantity;
    private String category;
    private boolean isActive;
   }
