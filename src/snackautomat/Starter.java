package snackautomat;

public class Starter {

    public static void main(String[] args) {
        VendingMachine machine = new VendingMachine("refill");
        machine.initialFill("refill");

        Customer customer = new Customer(0, false);
        CustomerUIFX.launchApp(customer, machine);
    }
}
