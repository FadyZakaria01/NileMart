package org.route;

import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class OrderRegistry {

    private int orderCounter = 0;
    private final List<Order> orders = new ArrayList<>();

    public void save(Order order) {
        orders.add(order);
    }
    public List<Order> getOrders() {
        return Collections.unmodifiableList(orders);
    }

    @PreDestroy
    public void printEndOfDay() {
        BigDecimal revenue=orders.stream().map(Order::total).reduce(BigDecimal.ZERO, BigDecimal::add);

        System.out.printf("End of day: %d orders, revenue %,.2f EGP.%n", orders.size(), revenue);
    }

    public String generateOrderNumber() {

        orderCounter++;

        String date = LocalDate.now()
                .format(DateTimeFormatter.BASIC_ISO_DATE);

        return String.format("NM-%s-%05d", date, orderCounter);
    }
}
