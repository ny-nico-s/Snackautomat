package snackautomat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VendingMachine {
    private final List<Product> products = new ArrayList<>();
    private final String secretKey;
    private final String restockKey;
    private boolean initialized;

    public VendingMachine(String secretKey, String restockKey) {
        this.secretKey = secretKey;
        this.restockKey = restockKey;
    }

    public boolean checkRestockKey(String input) {
        return input != null && input.equals(restockKey);
    }

    public void restockAll(String keyInput) {
        if (!checkRestockKey(keyInput)) {
            throw new IllegalArgumentException("Wrong restock key.");
        }
        for (Product product : products) {
            product.refill();
        }
    }

    public void initialFill(String secretKeyInput) {
        checkSecretKeyOrThrow(secretKeyInput);

        if (initialized) {
            return;
        }

        // Cans
        products.add(new Product("C1", "Fresh",           2.00, 15));
        products.add(new Product("C2", "Pineapple",       2.00, 15));
        products.add(new Product("C3", "Cola",            2.50, 15));
        products.add(new Product("C4", "Red Bull",        2.50, 15));
        products.add(new Product("C5", "Tiger",           2.00, 15));
        products.add(new Product("C6", "Coconut Water",   2.50, 15));
        // Bottles
        products.add(new Product("B1", "Soda",            1.50, 15));
        products.add(new Product("B2", "Sparkling Water", 1.50, 15));
        products.add(new Product("B3", "Orange Juice",    2.50, 15));
        products.add(new Product("B4", "Lemonade",        2.00, 15));
        // Snacks
        products.add(new Product("S1", "Sweet Candy",     1.50, 15));
        products.add(new Product("S2", "Chocolate Bar",   2.00, 15));
        products.add(new Product("S3", "Twix",            2.00, 15));
        products.add(new Product("S4", "Mint Chips",      1.80, 15));
        products.add(new Product("S5", "Chips",           1.80, 15));
        products.add(new Product("S6", "Cheese Crackers", 1.80, 15));
        products.add(new Product("S7", "Strawberry Mix",  1.50, 15));
        products.add(new Product("S8", "Star Snack",      1.80, 15));
        initialized = true;
    }

    public boolean checkSecretKey(String input) {
        return input != null && input.equals(secretKey);
    }

    public List<Product> getProducts() {
        return Collections.unmodifiableList(products);
    }

    public Product getProduct(String productId) {
        for (Product product : products) {
            if (product.getProductId().equals(productId)) {
                return product;
            }
        }
        throw new IllegalArgumentException("Unknown product id: " + productId);
    }

    public void restock(String secretKeyInput, String productId, int amount) {
        checkSecretKeyOrThrow(secretKeyInput);
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0.");
        }
        getProduct(productId).restock(amount);
    }

    public void changePrice(String secretKeyInput, String productId, double newPrice) {
        checkSecretKeyOrThrow(secretKeyInput);
        getProduct(productId).setPrice(newPrice);
    }

    public void swapProduct(String secretKeyInput, String productId, String newName, double newPrice, int newStock) {
        checkSecretKeyOrThrow(secretKeyInput);
        Product product = getProduct(productId);
        product.setName(newName);
        product.setPrice(newPrice);
        product.setStock(newStock);
    }

    public boolean isInitialized() {
        return initialized;
    }

    private void checkSecretKeyOrThrow(String input) {
        if (!checkSecretKey(input)) {
            throw new IllegalArgumentException("Wrong secret key.");
        }
    }
}
