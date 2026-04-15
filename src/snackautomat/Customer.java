package snackautomat;

public class Customer {
    double insertedMoney;
    boolean cancelled;
    double productPrice;
    String selectedProduct;

    public Customer(double insertedMoney, boolean cancelled) {
        this.insertedMoney = insertedMoney;
        this.cancelled = cancelled;
    }

    public void insertMoney(double amount) {
        this.insertedMoney += amount;
    }

    public void selectProduct(String name, double price) {
        this.selectedProduct = name;
        this.productPrice = price;
    }

    // placeholder — will be wired to inventory later
    public void selectProduct() {}

    public void cancelPurchase() {
        this.cancelled = true;
    }

    public double getChange() {
        return insertedMoney - productPrice;
    }

    // Deducts the product price and clears the selection — inserted money balance carries over.
    public void completePurchase() {
        this.insertedMoney -= this.productPrice;
        this.cancelled = false;
        this.productPrice = 0;
        this.selectedProduct = null;
    }

    // Clears the current product selection without touching the money.
    public void clearSelection() {
        this.cancelled = false;
        this.productPrice = 0;
        this.selectedProduct = null;
    }

    public void resetSession() {
        this.insertedMoney = 0;
        this.cancelled = false;
        this.productPrice = 0;
        this.selectedProduct = null;
    }

    public double getInsertedMoney() {
        return insertedMoney;
    }

    public boolean hasSufficientFunds() {
        return insertedMoney >= productPrice;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setInsertedMoney(double insertedMoney) {
        this.insertedMoney = insertedMoney;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
