package snackautomat;

public class Product {
<<<<<<< HEAD
    public static final int MAX_STOCK = 10;
=======
    public static final int MAX_STOCK = 20;
>>>>>>> main

    private final String productId;
    private String name;
    private double price;
    private int stock;

    public Product(String productId, String name, double price, int stock) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.stock = Math.min(stock, MAX_STOCK);
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
        if (stock > 0) {
            stock--;
        }
    }

    public void restock(int amount) {
        if (amount > 0) {
            stock = Math.min(stock + amount, MAX_STOCK);
        }
    }

    public void refill() {
        stock = MAX_STOCK;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        if (price > 0) {
            this.price = price;
        }
    }

    public void setStock(int stock) {
        if (stock >= 0) {
            this.stock = Math.min(stock, MAX_STOCK);
        }
    }

    public boolean isAvailable() {
        return stock > 0;
    }
}
