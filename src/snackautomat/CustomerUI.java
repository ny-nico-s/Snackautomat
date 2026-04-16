package snackautomat;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class CustomerUI {

    private static final Map<String, String> PRODUCT_IMAGES = new HashMap<>();
    static {
        // Cans
        PRODUCT_IMAGES.put("Fresh",           "01_can_green.png");
        PRODUCT_IMAGES.put("Pineapple",       "02_can_pineapple.png");
        PRODUCT_IMAGES.put("Cola",            "03_can_cola.png");
        PRODUCT_IMAGES.put("Red Bull",        "04_can_energy.png");
        PRODUCT_IMAGES.put("Tiger",           "05_can_striped.png");
        PRODUCT_IMAGES.put("Coconut Water",   "06_can_coconut.png");
        // Bottles
        PRODUCT_IMAGES.put("Soda",            "07_bottle_soda.png");
        PRODUCT_IMAGES.put("Sparkling Water", "08_bottle_sparkling.png");
        PRODUCT_IMAGES.put("Orange Juice",    "09_bottle_juice_orange.png");
        PRODUCT_IMAGES.put("Lemonade",        "10_bottle_yellow.png");
        // Snacks
        PRODUCT_IMAGES.put("Sweet Candy",     "11_snack_sweet.png");
        PRODUCT_IMAGES.put("Chocolate Bar",   "12_snack_brown.png");
        PRODUCT_IMAGES.put("Twix",            "13_snack_red_white.png");
        PRODUCT_IMAGES.put("Mint Chips",      "14_snack_green.png");
        PRODUCT_IMAGES.put("Chips",           "15_snack_teal.png");
        PRODUCT_IMAGES.put("Cheese Crackers", "16_snack_yellow.png");
        PRODUCT_IMAGES.put("Strawberry Mix",  "17_snack_pink.png");
        PRODUCT_IMAGES.put("Star Snack",      "18_item_18.png");
    }

    private final Customer customer;
    private final VendingMachine vendingMachine;
    private final ImageIcon machineIcon;
    private final List<String> sessionPurchases = new ArrayList<>();

    public CustomerUI(Customer customer, VendingMachine vendingMachine) {
        this.customer = customer;
        this.vendingMachine = vendingMachine;
        this.machineIcon = loadScaledIcon("machine_full.png", 300, -1);
    }

    public void start() {
        String[] menuOptions = {
            "Geld einwerfen",
            "Produkt wählen",
            "Kauf abbrechen",
            "Geld zurückgeben",
            "Eingeworfenes Geld anzeigen",
            "Session zurücksetzen",
            "Nachfüllen",
            "Beenden"
        };

        while (true) {
            int choice = JOptionPane.showOptionDialog(
                null,
                buildMenuPanel(),
                "Snackautomat",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                machineIcon,
                menuOptions,
                menuOptions[0]
            );

            if      (choice == 0) insertMoney();
            else if (choice == 1) selectProduct();
            else if (choice == 2) cancelPurchase();
            else if (choice == 3) returnMoney();
            else if (choice == 4) showInsertedMoney();
            else if (choice == 5) resetSession();
            else if (choice == 6) restockWithKey();
            else break;
        }
    }

    private JPanel buildMenuPanel() {
        JPanel root = new JPanel(new BorderLayout(20, 0));

        // --- LEFT: status + session inventory ---
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JLabel status = new JLabel("Was möchten Sie tun?   |   Eingeworfen: CHF "
                + String.format("%.2f", customer.getInsertedMoney()));
        status.setAlignmentX(Component.LEFT_ALIGNMENT);
        left.add(status);

        if (!sessionPurchases.isEmpty()) {
            left.add(Box.createVerticalStrut(10));

            JPanel inventoryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
            inventoryPanel.setBorder(BorderFactory.createTitledBorder("Gekaufte Produkte"));
            inventoryPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

            for (String name : sessionPurchases) {
                JPanel item = new JPanel();
                item.setLayout(new BoxLayout(item, BoxLayout.Y_AXIS));

                String filename = PRODUCT_IMAGES.get(name);
                if (filename != null) {
                    ImageIcon icon = loadScaledIcon(filename, -1, 35);
                    if (icon != null) {
                        JLabel img = new JLabel(icon);
                        img.setAlignmentX(Component.CENTER_ALIGNMENT);
                        item.add(img);
                    }
                }

                JLabel nameLabel = new JLabel(name);
                nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                item.add(nameLabel);
                inventoryPanel.add(item);
            }

            left.add(inventoryPanel);
        }

        root.add(left, BorderLayout.CENTER);

        // --- RIGHT: live stock counter ---
        JPanel stockGrid = new JPanel(new GridLayout(0, 2, 12, 3));
        stockGrid.setBorder(BorderFactory.createTitledBorder("Bestand"));

        for (Product p : vendingMachine.getProducts()) {
            JLabel nameLabel  = new JLabel(p.getName());
            JLabel countLabel = new JLabel(p.getStock() + " / " + Product.MAX_STOCK);

            if (p.getStock() == 0) {
                countLabel.setForeground(Color.RED);
            } else if (p.getStock() <= 5) {
                countLabel.setForeground(Color.ORANGE);
            } else {
                countLabel.setForeground(new Color(0, 140, 0));
            }

            stockGrid.add(nameLabel);
            stockGrid.add(countLabel);
        }

        root.add(stockGrid, BorderLayout.EAST);

        return root;
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
            sessionPurchases.add(product.getName());
            printReceipt(customer.selectedProduct, customer.productPrice, customer.getInsertedMoney(), customer.getChange());
            customer.completePurchase();
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
        customer.clearSelection();
        JOptionPane.showMessageDialog(null,
            "Kauf abgebrochen.\nIhr Guthaben: CHF " + String.format("%.2f", customer.getInsertedMoney()));
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
            sessionPurchases.clear();
            JOptionPane.showMessageDialog(null, "Session zurückgesetzt.");
        }
    }

    private void restockWithKey() {
        String input = JOptionPane.showInputDialog(null,
            "Nachfüll-Code eingeben:", "Nachfüllen", JOptionPane.PLAIN_MESSAGE);
        if (input == null) return;
        if (vendingMachine.checkRestockKey(input)) {
            vendingMachine.restockAll(input);
            JOptionPane.showMessageDialog(null,
                "Alle Produkte wurden auf " + Product.MAX_STOCK + " Stück aufgefüllt.",
                "Nachfüllen erfolgreich", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null,
                "Falscher Code.", "Fehler", JOptionPane.ERROR_MESSAGE);
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

    private ImageIcon loadScaledIcon(String filename, int width, int height) {
        java.net.URL url = getClass().getResource("/snackautomat/resources/images/" + filename);
        if (url == null) return null;
        ImageIcon raw = new ImageIcon(url);
        Image scaled = raw.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }
}
