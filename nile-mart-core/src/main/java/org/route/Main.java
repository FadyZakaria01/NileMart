package org.route;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static int cartCounter = 0;
    void main(){

        var context = new AnnotationConfigApplicationContext(AppConfig.class);
        ObjectProvider<ShoppingCart> cartProvider = context.getBeanProvider(ShoppingCart.class);
        Map<Integer, ShoppingCart> carts = new HashMap<>();

        while (true) {

            showMenu();

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {

                case 1:
                    showProducts(context.getBean(ProductService.class));
                    break;

                case 2:
                    createCart(context, carts);
                    break;

                case 3:
                    addProductToCart(carts);
                    break;

                case 4:
                    showCart(carts);
                    break;

                case 5:
                    checkoutCart(carts,context.getBean(CheckoutService.class));
                    break;

                case 0:
                    System.out.println("Goodbye!");
                    return;

                default:
                    System.out.println("Invalid choice!");
            }
        }
    }
    public static void showMenu() {

        System.out.println("========== NILEMART ==========");
        System.out.println("1. Show products");
        System.out.println("2. Create a cart");
        System.out.println("3. Add a product to a cart");
        System.out.println("4. Show a cart");
        System.out.println("5. Check out a cart");
        System.out.println("0. Exit");

        System.out.print("Enter your choice: ");
    }
    public static void showProducts(ProductService productService) {

        List<Product> products = productService.findAll();

        System.out.println("\n========== PRODUCTS ==========");

        System.out.printf("%-15s %-25s %-12s %-10s%n",
                "SKU", "Name", "Price", "Stock");

        System.out.println("---------------------------------------------------------------");

        for (Product product : products) {

            System.out.printf("%-15s %-25s %-12s %-10d%n",
                    product.getSku(),
                    product.getName(),
                    product.getPrice(),
                    product.getStockQuantity());
        }

        System.out.println("===============================================================");
    }
    public static void createCart(AnnotationConfigApplicationContext context, Map<Integer, ShoppingCart> carts){
        System.out.print("Enter customer full name: ");
        String name = scanner.nextLine();

        System.out.print("Enter customer phone: ");
        String phone = scanner.nextLine();

        System.out.print("Enter customer email: ");
        String email = scanner.nextLine();

        Customer customer = new Customer(name, phone, email);

        ShoppingCart cart = context.getBean(ShoppingCart.class);

        cart.setCustomer(customer);

        carts.put(cartCounter, cart);

        System.out.println("Cart created successfully!");
        System.out.println("Cart ID: " + cartCounter);

        cartCounter++;
    }
    private static void addProductToCart(Map<Integer, ShoppingCart> carts) {

        System.out.print("Enter cart ID: ");
        int cartId = scanner.nextInt();
        scanner.nextLine();

        ShoppingCart cart = carts.get(cartId);

        if (cart == null) {
            System.out.println("Cart not found!");
            return;
        }

        System.out.print("Enter product SKU: ");
        String sku = scanner.nextLine();

        System.out.print("Enter quantity: ");
        int quantity = scanner.nextInt();
        scanner.nextLine();

        try {
            cart.addItem(sku, quantity);
            System.out.println("Product added successfully!");

        } catch (StoreException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    public static void showCart(Map<Integer, ShoppingCart> carts) {
        System.out.println("Enter cart ID: ");
        int cartId = scanner.nextInt();
        scanner.nextLine();
        ShoppingCart cart = carts.get(cartId);
        if (cart == null) {
            System.out.println("Cart not found!");
            return;
        }
        System.out.println("\n========== CART DETAILS ==========");
        System.out.println("Cart Number: " + cart.getCartNumber());
        System.out.println("Customer: " + cart.getCustomer().fullName());
        System.out.println("Phone: " + cart.getCustomer().phone());
        System.out.println("Email: " + cart.getCustomer().email());


        if (cart.getLines().isEmpty()) {
            System.out.println("Cart is empty!");
            return;
        }

        System.out.printf("%-18s %-10s%n", "Product SKU", "Quantity");
        System.out.println("--------------------------------");

        for (Map.Entry<String, Integer> entry : cart.getLines().entrySet()) {

            System.out.printf("%-18s %-10d%n",
                    entry.getKey(),
                    entry.getValue());
        }

        System.out.println("--------------------------------");
        System.out.println("Subtotal: " + cart.subTotal() + " EGP");
        System.out.println("=================================");

    }
    public static void checkoutCart(Map<Integer, ShoppingCart> carts,CheckoutService checkoutService) {
        System.out.println("Enter cart ID: ");
        int cartId = scanner.nextInt();
        scanner.nextLine();
        ShoppingCart cart = carts.get(cartId);
        if (cart == null) {
            System.out.println("Cart not found!");
            return;
        }

        System.out.println("\nAvailable Governorates:");

        for (Governorate governorate : Governorate.values()) {
            System.out.println(governorate.ordinal() + 1
                    + ". " + governorate);
        }

        System.out.print("Choose governorate: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice < 1 || choice > Governorate.values().length) {
            System.out.println("Invalid governorate!");
            return;
        }

        Governorate governorate = Governorate.values()[choice - 1];

        try {
            Order order = checkoutService.placeOrder(cart, governorate);

            System.out.println("\n========== ORDER SUCCESS ==========");
            System.out.println("Order Number: " + order.orderNumber());
            System.out.println("Customer: " + order.customer().fullName());
            System.out.println("Subtotal: " + order.subtotal() + " EGP");
            System.out.println("VAT: " + order.vat() + " EGP");
            System.out.println("Shipping: " + order.shippingFee() + " EGP");
            System.out.println("Total: " + order.total() + " EGP");
            System.out.println("===================================");

        } catch (StoreException e) {
            System.out.println("Checkout failed: " + e.getMessage());
        }

    }

}
