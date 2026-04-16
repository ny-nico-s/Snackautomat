package snackautomat;

public class Starter {

    public static void main(String[] args) {
<<<<<<< HEAD
        VendingMachine machine = new VendingMachine("refill");
        machine.initialFill("refill");
=======
        VendingMachine machine = new VendingMachine("admin123", "refill");
        machine.initialFill("admin123");
>>>>>>> main

        Customer customer = new Customer(0, false);
        CustomerUIFX.launchApp(customer, machine);
    }
}
