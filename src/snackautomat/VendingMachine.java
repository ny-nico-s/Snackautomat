package snackautomat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VendingMachine {
    private final List<Product> products = new ArrayList<>();
    private final String secretKey;
    private double insertedMoney;
    private boolean initialized;

    public VendingMachine(String secretKey) {
        this.secretKey = secretKey;
    }

    public void initialFill(String secretKeyInput) {
        checkSecretKeyOrThrow(secretKeyInput);

        if (initialized) {
            return;
        }

        products.add(new Product("A1", "Twix", 2.00, 5));
        products.add(new Product("A2", "Cola", 2.50, 5));
        products.add(new Product("A3", "Red Bull", 2.50, 5));
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

    public void insertMoney(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0.");
        }

        insertedMoney += amount;
    }

    public double buyProduct(String productId) {
        Product product = getProduct(productId);

        if (product.getStock() <= 0) {
            throw new IllegalStateException("Product is sold out.");
        }

        if (insertedMoney < product.getPrice()) {
            throw new IllegalStateException("Not enough money inserted.");
        }

        product.reduceStock();

        double change = insertedMoney - product.getPrice();
        insertedMoney = 0;
        return change;
    }

    public double cancelPurchase() {
        double moneyToReturn = insertedMoney;
        insertedMoney = 0;
        return moneyToReturn;
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

    public double getInsertedMoney() {
        return insertedMoney;
    }

    public boolean isInitialized() {
        return initialized;
    }

    private void checkSecretKeyOrThrow(String input) {
        if (!checkSecretKey(input)) {
            throw new IllegalArgumentException("Wrong secret key.");
        }
    }

    public static class Product {
        private final String productId;
        private String name;
        private double price;
        private int stock;

        public Product(String productId, String name, double price, int stock) {
            this.productId = productId;
            this.name = name;
            setPrice(price);
            setStock(stock);
        }

        public String getProductId() {
            return productId;
        }

        public String getName() {
            return name;
        }

        public double getPrice() {
            return price;
        }

        public int getStock() {
            return stock;
        }

        public void reduceStock() {
            stock--;
        }

        public void restock(int amount) {
            stock += amount;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setPrice(double price) {
            if (price <= 0) {
                throw new IllegalArgumentException("Price must be greater than 0.");
            }

            this.price = price;
        }

        public void setStock(int stock) {
            if (stock < 0) {
                throw new IllegalArgumentException("Stock must not be negative.");
            }

            this.stock = stock;
        }
    }
}