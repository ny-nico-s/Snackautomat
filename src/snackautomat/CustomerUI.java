package snackautomat;

import java.util.List;
import javax.swing.JOptionPane;

public class CustomerUI {

    private final Customer customer;
    private final VendingMachine vendingMachine;

    public CustomerUI(Customer customer, VendingMachine vendingMachine) {
        this.customer = customer;
        this.vendingMachine = vendingMachine;
    }

    public void start() {
        String[] menuOptions = {
            "Geld einwerfen",
            "Produkt wählen",
            "Kauf abbrechen",
            "Geld zurückgeben",
            "Eingeworfenes Geld anzeigen",
            "Session zurücksetzen",
            "Beenden"
        };

        while (true) {
            int choice = JOptionPane.showOptionDialog(
                null,
                "Was möchten Sie tun?\nEingeworfen: CHF " + String.format("%.2f", customer.getInsertedMoney()),
                "Snackautomat",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                menuOptions,
                menuOptions[0]
            );

            if      (choice == 0) insertMoney();
            else if (choice == 1) selectProduct();
            else if (choice == 2) cancelPurchase();
            else if (choice == 3) returnMoney();
            else if (choice == 4) showInsertedMoney();
            else if (choice == 5) resetSession();
            else break;
        }
    }

    private void insertMoney() {
        String input = JOptionPane.showInputDialog(null, "Betrag einwerfen (CHF):", "Geld einwerfen", JOptionPane.PLAIN_MESSAGE);
        if (input == null) return;
        try {
            double amount = Double.parseDouble(input.replace(",", "."));
            if (amount <= 0) throw new NumberFormatException();
            customer.insertMoney(amount);
            JOptionPane.showMessageDialog(null,
                "Eingeworfen: CHF " + String.format("%.2f", amount) +
                "\nTotal im Automaten: CHF " + String.format("%.2f", customer.getInsertedMoney()));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Ungültiger Betrag. Bitte eine positive Zahl eingeben.");
        }
    }

    private void selectProduct() {
        String[] labels = buildProductLabels();
        if (labels.length == 0) {
            JOptionPane.showMessageDialog(null, "Keine Produkte verfügbar.");
            return;
        }

        String selected = (String) JOptionPane.showInputDialog(
            null,
            "Produkt wählen:",
            "Produktauswahl",
            JOptionPane.PLAIN_MESSAGE,
            null,
            labels,
            labels[0]
        );
        if (selected == null) return;

        Product product = findProduct(selected);

        if (!product.isAvailable()) {
            JOptionPane.showMessageDialog(null, product.getName() + " ist leider ausverkauft.");
            return;
        }

        customer.selectProduct(product.getName(), product.getPrice());

        if (customer.hasSufficientFunds()) {
            product.reduceStock();
            JOptionPane.showMessageDialog(null,
                "Gewählt: " + customer.selectedProduct +
                "\nPreis: CHF " + String.format("%.2f", customer.productPrice) +
                "\nRückgeld: CHF " + String.format("%.2f", customer.getChange()));
            customer.resetSession();
        } else {
            JOptionPane.showMessageDialog(null,
                "Nicht genug Geld eingeworfen!\n" +
                "Preis: CHF " + String.format("%.2f", customer.productPrice) +
                "\nEingeworfen: CHF " + String.format("%.2f", customer.getInsertedMoney()) +
                "\nEs fehlen: CHF " + String.format("%.2f", customer.productPrice - customer.getInsertedMoney()));
        }
    }

    private void cancelPurchase() {
        customer.cancelPurchase();
        JOptionPane.showMessageDialog(null,
            "Kauf abgebrochen.\nRückgabe: CHF " + String.format("%.2f", customer.getInsertedMoney()));
        customer.resetSession();
    }

    private void returnMoney() {
        double amount = customer.getInsertedMoney();
        if (amount <= 0) {
            JOptionPane.showMessageDialog(null, "Kein Geld eingeworfen.");
            return;
        }
        customer.resetSession();
        JOptionPane.showMessageDialog(null,
            "Rückgabe: CHF " + String.format("%.2f", amount));
    }

    private void showInsertedMoney() {
        JOptionPane.showMessageDialog(null,
            "Aktuell eingeworfen: CHF " + String.format("%.2f", customer.getInsertedMoney()));
    }

    private void resetSession() {
        int confirm = JOptionPane.showConfirmDialog(null,
            "Session wirklich zurücksetzen?", "Bestätigen", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            customer.resetSession();
            JOptionPane.showMessageDialog(null, "Session zurückgesetzt.");
        }
    }

    private String[] buildProductLabels() {
        List<Product> products = vendingMachine.getProducts();
        String[] labels = new String[products.size()];
        for (int i = 0; i < products.size(); i++) {
            Product p = products.get(i);
            labels[i] = p.getName() + " — CHF " + String.format("%.2f", p.getPrice());
        }
        return labels;
    }

    private Product findProduct(String label) {
        for (Product p : vendingMachine.getProducts()) {
            if (label.startsWith(p.getName())) return p;
        }
        return vendingMachine.getProducts().get(0);
    }
}
