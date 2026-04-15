package snackautomat;

public class Starter {

    public static void main(String[] args) {
        Customer customer = new Customer(0, false);
        CustomerUI ui = new CustomerUI(customer);
        ui.start();
    }
}
