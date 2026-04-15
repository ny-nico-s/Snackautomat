package snackautomat;

public class Customer {
    double insertedMoney;
    boolean cancelled;


    public void insertMoney(double insertedMoney) {}
    public void selectProduct() {}
    public void cancelPurchase() {}
    public double getChange() { return 0; }
    public void resetSession(){}
    public void getInsertedMoney() {}
    public boolean hasSufficientFunds() { return false; }

    //constructor for fields
    public Customer(double insertedMoney, boolean cancelled) {
        this.insertedMoney = insertedMoney;
        this.cancelled = cancelled;
    }

    //right now redundant, because everything is still set to public
    //getters
    public boolean isCancelled() {
        return cancelled;
    }

    //setters
    public void setInsertedMoney(double insertedMoney) {
        this.insertedMoney = insertedMoney;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    
}
