package snackautomat;

import javax.swing.JOptionPane;

public class CustomerUI {

    // Hardcoded products until inventory class is available
    static final String[] PRODUCT_NAMES  = {"Chips", "Schoggi", "Wasser", "Sandwich"};
    static final double[] PRODUCT_PRICES = {2.50,     1.80,      1.00,     4.50};

    private final Customer customer;

    public CustomerUI(Customer customer) {
        this.customer = customer;
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
        String selected = (String) JOptionPane.showInputDialog(
            null,
            "Produkt wählen:",
            "Produktauswahl",
            JOptionPane.PLAIN_MESSAGE,
            null,
            buildProductLabels(),
            buildProductLabels()[0]
        );
        if (selected == null) return;

        int index = findProductIndex(selected);
        customer.selectProduct(PRODUCT_NAMES[index], PRODUCT_PRICES[index]);

        if (customer.hasSufficientFunds()) {
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

    private void printReceipt(String productName, double price, double paid, double change) {
        String receipt =
                "=============================\n" +
                        "        SNACKAUTOMAT         \n" +
                        "=============================\n" +
                        "Produkt:    " + productName + "\n" +
                        "Preis:      CHF " + String.format("%.2f", price) + "\n" +
                        "-----------------------------\n" +
                        "Eingeworfen: CHF " + String.format("%.2f", paid) + "\n" +
                        "Rückgeld:    CHF " + String.format("%.2f", change) + "\n" +
                        "=============================\n" +
                        "   Danke für Ihren Einkauf!  \n" +
                        "=============================";

        JOptionPane.showMessageDialog(null, receipt, "Kassenbon", JOptionPane.PLAIN_MESSAGE);
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

    private static String[] buildProductLabels() {
        String[] labels = new String[PRODUCT_NAMES.length];
        for (int i = 0; i < PRODUCT_NAMES.length; i++) {
            labels[i] = PRODUCT_NAMES[i] + " — CHF " + String.format("%.2f", PRODUCT_PRICES[i]);
        }
        return labels;
    }

    private static int findProductIndex(String label) {
        for (int i = 0; i < PRODUCT_NAMES.length; i++) {
            if (label.startsWith(PRODUCT_NAMES[i])) return i;
        }
        return 0;
    }
}
