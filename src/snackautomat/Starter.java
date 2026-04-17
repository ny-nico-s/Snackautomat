package snackautomat;

public class Starter {

    public static void main(String[] args) {
        VendingMachine machine = new VendingMachine("admin123", "refill");
        machine.initialFill("admin123");

        Customer customer = new Customer(0, false);
        CustomerUIFX.launchApp(customer, machine);
    }
}
