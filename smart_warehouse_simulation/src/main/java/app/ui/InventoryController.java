package app.ui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import storage.Item;
import storage.Order;
import storage.StorageUnit;
import tasks.TaskManager;

import java.io.IOException;
import java.util.List;
import app.model.OrdersStore;
// persistence handled by StorageUnitsStore now
import app.model.StorageUnitsStore;
 

public class InventoryController {

    @FXML
    private Label inventorySummary;

    @FXML
    private ListView<String> itemsListView;

    @FXML
    private TextField itemIdField;

    @FXML
    private TextField itemNameField;

    @FXML
    private TextField itemWeightField;

    @FXML
    private TextField removeItemIdField;

    private TaskManager taskManager;
    private StorageUnit storageUnit;
    private ObservableList<String> itemsViewList = FXCollections.observableArrayList();
    private final OrdersStore ordersStore = OrdersStore.getInstance();
    private final StorageUnitsStore unitsStore = StorageUnitsStore.getInstance();

    @FXML
    private javafx.scene.control.ComboBox<String> unitCombo;

    @FXML
    public void initialize() {
        // init unit combo
        unitCombo.getItems().clear();
        for (StorageUnit su : unitsStore.getUnits()) unitCombo.getItems().add(su.getId());
        if (!unitCombo.getItems().isEmpty()) unitCombo.getSelectionModel().select(0);

        // allow multiple selection for creating orders
        itemsListView.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.MULTIPLE);
        // populate initial items view
        refreshAllItemsView();
    }

    public void setTaskManager(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public void setStorageUnit(StorageUnit storageUnit) {
        this.storageUnit = storageUnit;
        refreshView();
    }

    private void refreshView() {
        Platform.runLater(() -> {
            itemsViewList.clear();
            if (storageUnit != null) {
                List<Item> items = storageUnit.getItems();
                for (Item it : items) {
                    itemsViewList.add(it.toString());
                }
                inventorySummary.setText("Items: " + items.size());
            } else {
                inventorySummary.setText("No storage unit");
            }
            itemsListView.setItems(itemsViewList);
        });
    }

    @FXML
    private void addItem() {
        // add item into the selected storage unit from combo
        String selectedUnitId = unitCombo.getSelectionModel().getSelectedItem();
        if (selectedUnitId == null) {
            inventorySummary.setText("No storage unit selected");
            return;
        }
        StorageUnit target = null;
        for (StorageUnit su : unitsStore.getUnits()) if (su.getId().equals(selectedUnitId)) { target = su; break; }
        if (target == null) { inventorySummary.setText("Selected unit not found"); return; }

        String userId = itemIdField.getText();
        String id = userId;
        String name = itemNameField.getText();
        double w = 0.0;
        try { w = Double.parseDouble(itemWeightField.getText()); } catch (Exception ex) {}
        // ensure unique id across all units
        boolean duplicate = unitsStore.getUnits().stream().flatMap(u -> u.getItems().stream()).anyMatch(i -> i.getId().equals(userId));
        if (userId == null || userId.isBlank() || duplicate) {
            id = "I-" + System.currentTimeMillis();
        }
        if (name == null || name.isBlank()) name = "Item";
        Item it = new Item(id, name, w);
        boolean ok = target.addItems(it);
        inventorySummary.setText(ok ? "Item added to " + target.getId() : "Item not added - full");
        itemIdField.clear(); itemNameField.clear(); itemWeightField.clear();
        unitsStore.persist();
        refreshAllItemsView();
    }

    @FXML
    private void removeItem() {
        String id = removeItemIdField.getText();
        if (id == null || id.isBlank()) return;
        boolean ok = false;
        for (StorageUnit su : unitsStore.getUnits()) {
            if (su.removeItems(id)) { ok = true; break; }
        }
        inventorySummary.setText(ok ? "Item removed" : "Item not found");
        removeItemIdField.clear();
        unitsStore.persist();
        refreshAllItemsView();
    }

    @FXML
    private void createOrderFromSelected() {
        if (taskManager == null) {
            inventorySummary.setText("TaskManager missing");
            return;
        }
        Order order = new Order("ORDER-UI-" + System.currentTimeMillis());
        // Collect selected items by parsing their string representation and matching id
        var sel = itemsListView.getSelectionModel().getSelectedItems();
        if (sel == null || sel.isEmpty()) {
            inventorySummary.setText("No items selected");
            return;
        }
        // find selected items across all storage units
        for (String s : sel) {
            String id = s.split(":", 2)[0].trim();
            boolean found = false;
            for (StorageUnit su : unitsStore.getUnits()) {
                for (Item it : su.getItems()) {
                    if (it.getId().equals(id)) {
                        // annotate item with its storage unit so tasks can use the position
                        try { it.setStorageUnitId(su.getId()); } catch (Throwable ignore) {}
                        order.addItem(it);
                        found = true;
                        break;
                    }
                }
                if (found) break;
            }
        }
        try {
            taskManager.createTasksFromOrders(order);
            // publish to OrdersStore and persist
            ordersStore.addOrder(order);
            inventorySummary.setText("Order created: " + order.getId());
            // green notification
            inventorySummary.setStyle("-fx-background-color: #d4edda; -fx-text-fill: #155724; -fx-padding:6px;");
            // clear selection
            itemsListView.getSelectionModel().clearSelection();
            // schedule style reset
            javafx.concurrent.Task<Void> reset = new javafx.concurrent.Task<>() {
                @Override
                protected Void call() throws Exception {
                    Thread.sleep(1500);
                    return null;
                }
            };
            reset.setOnSucceeded(e -> inventorySummary.setStyle(""));
            new Thread(reset).start();
        } catch (IOException e) {
            inventorySummary.setText("Failed to create tasks: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void refreshAllItemsView() {
        Platform.runLater(() -> {
            itemsViewList.clear();
            for (StorageUnit su : unitsStore.getUnits()) {
                for (Item it : su.getItems()) {
                    itemsViewList.add(String.format("%s | unit=%s", it.toString(), su.getId()));
                }
            }
            itemsListView.setItems(itemsViewList);
            inventorySummary.setText("Total items: " + itemsViewList.size());
        });
    }
}

