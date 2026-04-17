package snackautomat;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CustomerUIFX extends Application {

    // Static references — JavaFX controls its own Application instantiation,
    // so customer and vendingMachine are passed in before launch() is called.
    private static Customer customer;
    private static VendingMachine vendingMachine;

    private static final Map<String, String> PRODUCT_IMAGES = new HashMap<>();
    static {
        PRODUCT_IMAGES.put("Fresh",           "01_can_green.png");
        PRODUCT_IMAGES.put("Pineapple",       "02_can_pineapple.png");
        PRODUCT_IMAGES.put("Cola",            "03_can_cola.png");
        PRODUCT_IMAGES.put("Red Bull",        "04_can_energy.png");
        PRODUCT_IMAGES.put("Tiger",           "05_can_striped.png");
        PRODUCT_IMAGES.put("Coconut Water",   "06_can_coconut.png");
        PRODUCT_IMAGES.put("Soda",            "07_bottle_soda.png");
        PRODUCT_IMAGES.put("Sparkling Water", "08_bottle_sparkling.png");
        PRODUCT_IMAGES.put("Orange Juice",    "09_bottle_juice_orange.png");
        PRODUCT_IMAGES.put("Lemonade",        "10_bottle_yellow.png");
        PRODUCT_IMAGES.put("Sweet Candy",     "11_snack_sweet.png");
        PRODUCT_IMAGES.put("Chocolate Bar",   "12_snack_brown.png");
        PRODUCT_IMAGES.put("Twix",            "13_snack_red_white.png");
        PRODUCT_IMAGES.put("Mint Chips",      "14_snack_green.png");
        PRODUCT_IMAGES.put("Chips",           "15_snack_teal.png");
        PRODUCT_IMAGES.put("Cheese Crackers", "16_snack_yellow.png");
        PRODUCT_IMAGES.put("Strawberry Mix",  "17_snack_pink.png");
        PRODUCT_IMAGES.put("Star Snack",      "18_item_18.png");
    }

    private final List<String> sessionPurchases = new ArrayList<>();

    // Live UI nodes updated after each action
    private Label  balanceLabel;
    private HBox   purchaseBar;
    private GridPane stockGrid;

    // Called from Starter instead of Application.launch() directly
    public static void launchApp(Customer c, VendingMachine vm) {
        customer = c;
        vendingMachine = vm;
        launch();
    }

    // ── Scene setup ───────────────────────────────────────────────────────────

    @Override
    public void start(Stage stage) {
        stage.setTitle("Snackautomat");

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: #f0f0f0;");

        root.setLeft(buildLeftPanel());
        root.setCenter(buildCenterPanel());
        root.setRight(buildRightPanel());

        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.show();
    }

    // ── LEFT: vending machine image ───────────────────────────────────────────

    private VBox buildLeftPanel() {
        VBox box = new VBox();
        box.setAlignment(Pos.TOP_CENTER);
        box.setPadding(new Insets(0, 15, 0, 0));

        ImageView machine = loadImageView("machine_full.png", 220, 0);
        if (machine != null) box.getChildren().add(machine);

        return box;
    }

    // ── CENTER: balance + session inventory + action buttons ──────────────────

    private VBox buildCenterPanel() {
        VBox box = new VBox(12);
        box.setAlignment(Pos.TOP_LEFT);
        box.setPrefWidth(400);

        Label title = new Label("Snackautomat");
        title.setFont(Font.font("SansSerif", FontWeight.BOLD, 22));

        balanceLabel = new Label();
        balanceLabel.setFont(Font.font("SansSerif", 14));
        updateBalanceLabel();

        // Scrollable row of purchased product images
        Label purchaseTitle = new Label("Gekaufte Produkte:");
        purchaseTitle.setFont(Font.font("SansSerif", FontWeight.SEMI_BOLD, 12));

        purchaseBar = new HBox(8);
        purchaseBar.setAlignment(Pos.CENTER_LEFT);
        purchaseBar.setPadding(new Insets(4));

        ScrollPane purchaseScroll = new ScrollPane(purchaseBar);
        purchaseScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        purchaseScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        purchaseScroll.setPrefHeight(80);
        purchaseScroll.setFitToHeight(true);
        purchaseScroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        box.getChildren().addAll(
            title,
            balanceLabel,
            new Separator(),
            purchaseTitle,
            purchaseScroll,
            new Separator(),
            buildButtonGrid()
        );
        return box;
    }

    private GridPane buildButtonGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(8);

        String[][]   labels  = {
            { "Geld einwerfen",              "Produkt wählen"       },
            { "Kauf abbrechen",              "Geld zurückgeben"     },
            { "Eingeworfenes Geld anzeigen", "Session zurücksetzen" },
            { "Nachfüllen",                  "Beenden"              }
        };
        Runnable[][] actions = {
            { this::insertMoney,      this::selectProduct  },
            { this::cancelPurchase,   this::returnMoney    },
            { this::showInsertedMoney,this::resetSession   },
            { this::restockWithKey,   this::exitApp        }
        };

        for (int row = 0; row < labels.length; row++) {
            for (int col = 0; col < 2; col++) {
                Button btn = new Button(labels[row][col]);
                btn.setMaxWidth(Double.MAX_VALUE);
                btn.setPrefHeight(36);
                Runnable action = actions[row][col];
                btn.setOnAction(e -> action.run());
                GridPane.setHgrow(btn, Priority.ALWAYS);
                grid.add(btn, col, row);
            }
        }

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.ALWAYS);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(col1, col2);
        return grid;
    }

    // ── RIGHT: live stock counter ─────────────────────────────────────────────

    private VBox buildRightPanel() {
        VBox box = new VBox(8);
        box.setAlignment(Pos.TOP_LEFT);
        box.setPadding(new Insets(0, 0, 0, 20));

        Label title = new Label("Bestand");
        title.setFont(Font.font("SansSerif", FontWeight.BOLD, 14));

        stockGrid = new GridPane();
        stockGrid.setHgap(14);
        stockGrid.setVgap(3);
        refreshStockGrid();

        box.getChildren().addAll(title, new Separator(), stockGrid);
        return box;
    }

    // Rebuilds only the stock grid — called after every purchase or restock
    private void refreshStockGrid() {
        stockGrid.getChildren().clear();
        int row = 0;
        for (Product p : vendingMachine.getProducts()) {
            Label name  = new Label(p.getName());
            name.setFont(Font.font("SansSerif", 12));

            Label count = new Label(p.getStock() + " / " + Product.MAX_STOCK);
            count.setFont(Font.font("SansSerif", FontWeight.BOLD, 12));

            if      (p.getStock() == 0)  count.setTextFill(Color.RED);
            else if (p.getStock() <= 5)  count.setTextFill(Color.DARKORANGE);
            else                          count.setTextFill(Color.web("#008800"));

            stockGrid.add(name,  0, row);
            stockGrid.add(count, 1, row);
            row++;
        }
    }

    // ── Actions ───────────────────────────────────────────────────────────────

    private void insertMoney() {
        TextInputDialog dlg = new TextInputDialog();
        dlg.setTitle("Geld einwerfen");
        dlg.setHeaderText(null);
        dlg.setContentText("Betrag einwerfen (CHF):");
        dlg.showAndWait().ifPresent(input -> {
            try {
                double amount = Double.parseDouble(input.replace(",", "."));
                if (amount <= 0) throw new NumberFormatException();
                customer.insertMoney(amount);
                updateBalanceLabel();
                info("Eingeworfen: CHF " + fmt(amount)
                   + "\nTotal im Automaten: CHF " + fmt(customer.getInsertedMoney()));
            } catch (NumberFormatException e) {
                error("Ungültiger Betrag. Bitte eine positive Zahl eingeben.");
            }
        });
    }

    private void selectProduct() {
        List<Product> products = vendingMachine.getProducts();
        if (products.isEmpty()) { info("Keine Produkte verfügbar."); return; }

        List<String> labels = products.stream()
            .map(p -> p.getName() + " — CHF " + fmt(p.getPrice()))
            .toList();

        ChoiceDialog<String> dlg = new ChoiceDialog<>(labels.get(0), labels);
        dlg.setTitle("Produktauswahl");
        dlg.setHeaderText(null);
        dlg.setContentText("Produkt wählen:");

        Optional<String> result = dlg.showAndWait();
        result.ifPresent(selected -> {
            Product product = findProduct(selected);

            if (!product.isAvailable()) {
                info(product.getName() + " ist leider ausverkauft.");
                return;
            }

            customer.selectProduct(product.getName(), product.getPrice());

            if (customer.hasSufficientFunds()) {
                product.reduceStock();
                sessionPurchases.add(product.getName());
                printReceipt(customer.selectedProduct, customer.productPrice,
                             customer.getInsertedMoney(), customer.getChange());
                customer.completePurchase();
                updateBalanceLabel();
                refreshPurchaseBar();
                refreshStockGrid();
            } else {
                info("Nicht genug Geld eingeworfen!\n"
                   + "Preis:       CHF " + fmt(customer.productPrice) + "\n"
                   + "Eingeworfen: CHF " + fmt(customer.getInsertedMoney()) + "\n"
                   + "Es fehlen:   CHF " + fmt(customer.productPrice - customer.getInsertedMoney()));
                customer.clearSelection();
            }
        });
    }

    private void printReceipt(String productName, double price, double paid, double change) {
        String text =
            "=============================\n" +
            "        SNACKAUTOMAT         \n" +
            "=============================\n" +
            "Produkt:     " + productName  + "\n" +
            "Preis:       CHF " + fmt(price)   + "\n" +
            "-----------------------------\n" +
            "Eingeworfen: CHF " + fmt(paid)    + "\n" +
            "Rückgeld:    CHF " + fmt(change)  + "\n" +
            "=============================\n" +
            "  Danke für Ihren Einkauf!   \n" +
            "=============================";

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Kassenbon");
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.showAndWait();
    }

    private void cancelPurchase() {
        customer.clearSelection();
        updateBalanceLabel();
        info("Kauf abgebrochen.\nIhr Guthaben: CHF " + fmt(customer.getInsertedMoney()));
    }

    private void returnMoney() {
        double amount = customer.getInsertedMoney();
        if (amount <= 0) { info("Kein Geld eingeworfen."); return; }
        customer.resetSession();
        updateBalanceLabel();
        info("Rückgabe: CHF " + fmt(amount));
    }

    private void showInsertedMoney() {
        info("Aktuell eingeworfen: CHF " + fmt(customer.getInsertedMoney()));
    }

    private void resetSession() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Bestätigen");
        confirm.setHeaderText(null);
        confirm.setContentText("Session wirklich zurücksetzen?");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                customer.resetSession();
                sessionPurchases.clear();
                updateBalanceLabel();
                refreshPurchaseBar();
                refreshStockGrid();
                info("Session zurückgesetzt.");
            }
        });
    }

    private void restockWithKey() {
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Code eingeben");

        Dialog<String> dlg = new Dialog<>();
        dlg.setTitle("Nachfüllen");
        dlg.setHeaderText(null);
        dlg.getDialogPane().setContent(passwordField);
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dlg.setResultConverter(btn -> btn == ButtonType.OK ? passwordField.getText() : null);

        dlg.showAndWait().ifPresent(input -> {
            if (input == null || input.isEmpty()) return;
            if (vendingMachine.checkRestockKey(input)) {
                vendingMachine.restockAll(input);
                refreshStockGrid();
                info("Alle Produkte wurden auf " + Product.MAX_STOCK + " Stück aufgefüllt.");
            } else {
                error("Falscher Code.");
            }
        });
    }

    private void exitApp() {
        Platform.exit();
    }

    // ── UI helpers ────────────────────────────────────────────────────────────

    private void updateBalanceLabel() {
        balanceLabel.setText("Eingeworfen: CHF " + fmt(customer.getInsertedMoney()));
    }

    private void refreshPurchaseBar() {
        purchaseBar.getChildren().clear();
        for (String name : sessionPurchases) {
            VBox item = new VBox(3);
            item.setAlignment(Pos.CENTER);

            String filename = PRODUCT_IMAGES.get(name);
            if (filename != null) {
                ImageView iv = loadImageView(filename, 0, 40);
                if (iv != null) item.getChildren().add(iv);
            }
            Label nameLabel = new Label(name);
            nameLabel.setTextFill(Color.BLACK);
            item.getChildren().add(nameLabel);
            purchaseBar.getChildren().add(item);
        }
    }

    private Product findProduct(String label) {
        for (Product p : vendingMachine.getProducts()) {
            if (label.startsWith(p.getName())) return p;
        }
        return vendingMachine.getProducts().get(0);
    }

    // width=0 or height=0 means "unconstrained on that axis"
    private ImageView loadImageView(String filename, double width, double height) {
        InputStream stream = getClass()
            .getResourceAsStream("/snackautomat/resources/images/" + filename);
        if (stream == null) return null;
        ImageView iv = new ImageView(new Image(stream));
        iv.setPreserveRatio(true);
        iv.setSmooth(true);
        if (width  > 0) iv.setFitWidth(width);
        if (height > 0) iv.setFitHeight(height);
        return iv;
    }

    private void info(String message) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(message);
        a.showAndWait();
    }

    private void error(String message) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText(null);
        a.setContentText(message);
        a.showAndWait();
    }

    private String fmt(double v) {
        return String.format("%.2f", v);
    }
}
