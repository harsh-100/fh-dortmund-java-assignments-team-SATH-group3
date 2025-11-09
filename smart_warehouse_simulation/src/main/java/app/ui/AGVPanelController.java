package app.ui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import robots.Robot;
import storage.Item;
import tasks.TaskManager;
import tasks.Tasks;
import warehouse.Warehouse;

import java.util.List;

public class AGVPanelController {

    @FXML
    private Label statusLabel;

    @FXML
    private ListView<String> robotListView;

    @FXML
    private TextField itemIdField;

    @FXML
    private TextField itemNameField;

    @FXML
    private TextField itemWeightField;

    @FXML
    private Button addTaskButton;

    private Warehouse warehouse;
    private TaskManager taskManager;
    private Timeline updater;

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
        startUpdaterIfReady();
    }

    public void setTaskManager(TaskManager taskManager) {
        this.taskManager = taskManager;
        startUpdaterIfReady();
    }

    private void startUpdaterIfReady() {
        if (warehouse == null || taskManager == null) return;
        if (updater != null) return;

        ObservableList<String> items = FXCollections.observableArrayList();
        robotListView.setItems(items);

        updater = new Timeline(new KeyFrame(Duration.millis(500), e -> refreshRobots()));
        updater.setCycleCount(Timeline.INDEFINITE);
        updater.play();
    }

    private void refreshRobots() {
        Platform.runLater(() -> {
            try {
                List<Robot> robots = warehouse.getRobots();
                ObservableList<String> items = robotListView.getItems();
                items.clear();
                for (Robot r : robots) {
                    String taskId = (r.getCurrentTask() != null) ? r.getCurrentTask().getId() : "-";
                    String s = String.format("%s | %s | batt=%.1f | pos=(%d,%d) | task=%s",
                            r.getID(), r.getState(), r.getBattery(), r.getLocation().x, r.getLocation().y, taskId);
                    items.add(s);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    @FXML
    private void startSimulation() {
        if (warehouse != null) {
            warehouse.startSimulation();
            statusLabel.setText("Simulation started");
        } else {
            statusLabel.setText("Warehouse not initialized");
        }
    }

    @FXML
    private void stopSimulation() {
        if (warehouse != null) {
            warehouse.stopSimulation();
            statusLabel.setText("Simulation stopped");
        } else {
            statusLabel.setText("Warehouse not initialized");
        }
    }

    @FXML
    private void addManualTask() {
        if (taskManager == null) {
            statusLabel.setText("TaskManager not available");
            return;
        }

        String itemId = itemIdField.getText();
        String itemName = itemNameField.getText();
        double weight = 0.0;
        try {
            weight = Double.parseDouble(itemWeightField.getText());
        } catch (NumberFormatException e) {
            // ignore and keep 0.0
        }

        if (itemId == null || itemId.isBlank()) itemId = "UI-" + System.currentTimeMillis();
        if (itemName == null || itemName.isBlank()) itemName = "ManualItem";

        Item it = new Item(itemId, itemName, weight);
        Tasks t = new Tasks("UI-" + System.currentTimeMillis(), it);
        taskManager.addTask(t);
        statusLabel.setText("Added task: " + t.getId());

        // clear fields
        itemIdField.clear();
        itemNameField.clear();
        itemWeightField.clear();
    }
}


