package snackautomat;

public class Product {
    private String privateId;
    private String name;
    private double price;
    private int quantity;


    public Product(String privateId, String name, double price, int stock) {
        this.privateId = privateId;
        this.name = name;
        this.price = price;
        this.quantity = stock;
    }

    public String getPrivateId() {
        return privateId;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void reduceQuantity() {
        if (quantity > 0) {
            quantity--;
        }
    }

    public void restock(int amount) {
        if (amount > 0) {
            quantity += amount;
        }
    }

    public void setPrice(double price) {
        if (price > 0) {
            this.price = price;
        }
    }

    public boolean isAvailable() {
        return quantity > 0;
    }
}

