package org.route;

import org.springframework.stereotype.Component;

@Component
public class EmailNotificationSender implements NotificationSender {
    @Override
    public void send(Customer customer, String message) {

        if (customer.email() == null) {
            System.out.println("Customer email is null");
            return;
        }
        System.out.println("Sending Email to: " + customer.email());
        System.out.println("Message: " + message);
    }
}
