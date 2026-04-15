package snackautomat;

public class Starter {

    public static void main(String[] args) {
        VendingMachine machine = new VendingMachine("admin123");
        machine.initialFill("admin123");

        Customer customer = new Customer(0, false);
        CustomerUI ui = new CustomerUI(customer, machine);
        ui.start();
    }
}
