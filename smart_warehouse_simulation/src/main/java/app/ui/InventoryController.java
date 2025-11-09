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
        if (storageUnit == null) {
            inventorySummary.setText("No storage unit");
            return;
        }
        String id = itemIdField.getText();
        String name = itemNameField.getText();
        double w = 0.0;
        try { w = Double.parseDouble(itemWeightField.getText()); } catch (Exception ex) {}
        if (id == null || id.isBlank()) id = "I-" + System.currentTimeMillis();
        if (name == null || name.isBlank()) name = "Item";
        Item it = new Item(id, name, w);
        boolean ok = storageUnit.addItems(it);
        inventorySummary.setText(ok ? "Item added" : "Item not added - full");
        itemIdField.clear(); itemNameField.clear(); itemWeightField.clear();
        refreshView();
    }

    @FXML
    private void removeItem() {
        if (storageUnit == null) {
            inventorySummary.setText("No storage unit");
            return;
        }
        String id = removeItemIdField.getText();
        if (id == null || id.isBlank()) return;
        boolean ok = storageUnit.removeItems(id);
        inventorySummary.setText(ok ? "Item removed" : "Item not found");
        removeItemIdField.clear();
        refreshView();
    }

    @FXML
    private void createOrderFromSelected() {
        if (storageUnit == null || taskManager == null) {
            inventorySummary.setText("Storage or TaskManager missing");
            return;
        }
        Order order = new Order("ORDER-UI-" + System.currentTimeMillis());
        // Collect selected items by parsing their string representation and matching id
        var sel = itemsListView.getSelectionModel().getSelectedItems();
        if (sel == null || sel.isEmpty()) {
            inventorySummary.setText("No items selected");
            return;
        }
        List<Item> storageItems = storageUnit.getItems();
        for (String s : sel) {
            // s is Item.toString() like id:name(weightkg)
            String id = s.split(":", 2)[0];
            for (Item it : storageItems) {
                if (it.getId().equals(id)) {
                    order.addItem(it);
                    break;
                }
            }
        }
        try {
            taskManager.createTasksFromOrders(order);
            inventorySummary.setText("Order created: " + order.getId());
        } catch (IOException e) {
            inventorySummary.setText("Failed to create tasks: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

