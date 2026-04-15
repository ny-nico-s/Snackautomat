package snackautomat;

import javax.swing.JOptionPane;

public class Starter {

    // Hardcoded products until inventory class is available
    static final String[] PRODUCT_NAMES   = {"Chips", "Schoggi", "Wasser", "Sandwich"};
    static final double[] PRODUCT_PRICES  = {2.50,     1.80,      1.00,     4.50};

    public static void main(String[] args) {
        Customer customer = new Customer(0, false);

        String[] menuOptions = {
            "Geld einwerfen",
            "Produkt wählen",
            "Kauf abbrechen",
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

            if (choice == 0) {
                // Insert money
                String input = JOptionPane.showInputDialog(null, "Betrag einwerfen (CHF):", "Geld einwerfen", JOptionPane.PLAIN_MESSAGE);
                if (input != null) {
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

            } else if (choice == 1) {
                // Select product
                String selected = (String) JOptionPane.showInputDialog(
                    null,
                    "Produkt wählen:",
                    "Produktauswahl",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    buildProductLabels(),
                    buildProductLabels()[0]
                );
                if (selected != null) {
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

            } else if (choice == 2) {
                // Cancel purchase
                customer.cancelPurchase();
                JOptionPane.showMessageDialog(null,
                    "Kauf abgebrochen.\nRückgabe: CHF " + String.format("%.2f", customer.getInsertedMoney()));
                customer.resetSession();

            } else if (choice == 3) {
                // Show inserted money
                JOptionPane.showMessageDialog(null,
                    "Aktuell eingeworfen: CHF " + String.format("%.2f", customer.getInsertedMoney()));

            } else if (choice == 4) {
                // Reset session
                int confirm = JOptionPane.showConfirmDialog(null,
                    "Session wirklich zurücksetzen?", "Bestätigen", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    customer.resetSession();
                    JOptionPane.showMessageDialog(null, "Session zurückgesetzt.");
                }

            } else {
                // Beenden or dialog closed
                break;
            }
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
