package org.route;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class CheckoutService {
    ProductService  productService;
    ShippingQuoteService shippingQuoteService;
    TaxCalculator TaxCalculator;
    OrderRegistry orderRegistry;
    List<NotificationSender> notificationSenders;

    public Order placeOrder(ShoppingCart cart,Governorate governorate){
        if (cart.isEmpty()){
            throw new StoreException("Cart is empty");
        }

        for (Map.Entry<String, Integer> entry : cart.getLines().entrySet()) {

            String sku = entry.getKey();
            int quantity = entry.getValue();

            Product product = productService.findBySku(sku)
                    .orElseThrow(() -> new StoreException("Product not found: " + sku));

            if (product.getStockQuantity() < quantity) {
                throw new StoreException("Insufficient stock for product: " + sku);
            }
        }
        BigDecimal subtotal = cart.subTotal();
        BigDecimal vat = TaxCalculator.calculateVatRate(subtotal);
        BigDecimal shippingFee = shippingQuoteService.quote(governorate, subtotal);
        BigDecimal total = subtotal.add(vat).add(shippingFee);

        for (Map.Entry<String, Integer> entry : cart.getLines().entrySet()) {
            productService.reduceStock(entry.getKey(), entry.getValue());
        }

        String orderNumber = orderRegistry.generateOrderNumber();

        Order order = new Order(
                orderNumber,
                cart.getCustomer(),
                governorate,
                Map.copyOf(cart.getLines()),
                subtotal,
                vat,
                shippingFee,
                total
        );

        orderRegistry.save(order);
        String message = String.format("Your order %s has been placed successfully. Total: %s EGP", orderNumber, total);

        for (NotificationSender sender : notificationSenders) {
            sender.send(cart.getCustomer(), message);
        }
        cart.clear();
        return order;
    }


}
