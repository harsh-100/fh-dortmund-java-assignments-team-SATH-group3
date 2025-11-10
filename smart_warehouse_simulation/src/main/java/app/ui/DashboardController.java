package app.ui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import robots.Robot;
import tasks.TaskManager;
import warehouse.Warehouse;
import storage.StorageUnit;

import java.io.IOException;
import java.util.List;

public class DashboardController {

    @FXML
    private VBox contentArea;

    @FXML
    private Label simStatusLabel;

    @FXML
    private Label pendingTasksLabel;

    @FXML
    private Label completedTasksLabel;

    @FXML
    private ListView<String> robotListView;

    private Warehouse warehouse;
    private TaskManager taskManager;
    private StorageUnit storageUnit;
    private Timeline updater;
    private boolean taskManagerListenerRegistered = false;

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
        startPollingIfReady();
    }

    public void setTaskManager(TaskManager taskManager) {
        this.taskManager = taskManager;
        startPollingIfReady();
        if (!taskManagerListenerRegistered) {
            registerTaskManagerListeners();
            taskManagerListenerRegistered = true;
        }
    }

    private void registerTaskManagerListeners() {
        if (this.taskManager == null) return;
        this.taskManager.addListener(new TaskManager.TaskListener() {
            @Override
            public void onPendingCountChanged(int newPending) {
                Platform.runLater(() -> pendingTasksLabel.setText("Pending tasks: " + newPending));
            }

            @Override
            public void onCompletedCountChanged(int newCompleted) {
                Platform.runLater(() -> completedTasksLabel.setText("Completed tasks: " + newCompleted));
            }
        });
    }

    public void setStorageUnit(StorageUnit storageUnit) {
        this.storageUnit = storageUnit;
    }

    private void startPollingIfReady() {
        if (this.warehouse == null || this.taskManager == null) return;
        if (this.updater != null) return;

        ObservableList<String> items = FXCollections.observableArrayList();
        robotListView.setItems(items);

        updater = new Timeline(new KeyFrame(Duration.millis(500), evt -> {
            try {
                updateUI();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
        updater.setCycleCount(Timeline.INDEFINITE);
        updater.play();
    }

    private void updateUI() {
        // Run on JavaFX thread
        Platform.runLater(() -> {
            // simulation status
            boolean running = warehouse.isSimulationRunning();
            simStatusLabel.setText("Simulation: " + (running ? "running" : "stopped"));

            // tasks
            int pending = taskManager.getPendingTasks().size();
            int completed = taskManager.getCompletedTasksList().size();
            pendingTasksLabel.setText("Pending tasks: " + pending);
            completedTasksLabel.setText("Completed tasks: " + completed);

            // robots
            List<Robot> robots = warehouse.getRobots();
            ObservableList<String> items = robotListView.getItems();
            items.clear();
            for (Robot r : robots) {
                String taskId = (r.getCurrentTask() != null) ? r.getCurrentTask().getId() : "-";
                String s = String.format("%s | state=%s | battery=%.1f | pos=(%d,%d) | task=%s",
                        r.getID(), r.getState(), r.getBattery(), r.getLocation().x, r.getLocation().y, taskId);
                items.add(s);
            }
        });
    }

    @FXML
    private void showDashboard() {
        // Restore the original dashboard controls (they may have been replaced
        // when loading other views). We re-add the existing labeled nodes and
        // the ListView that the controller owns so the dashboard UI becomes
        // visible again.
        try {
            // Create a small title node for the robots section
            javafx.scene.control.Label robotsTitle = new javafx.scene.control.Label("Robots:");
            robotsTitle.setStyle("-fx-font-weight:bold");

            contentArea.getChildren().setAll(simStatusLabel, pendingTasksLabel, completedTasksLabel, robotsTitle, robotListView);
            // Refresh contents immediately
            updateUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openAGVPanel() {
        loadIntoContent("/fxml/AGVPanel.fxml");
    }

    @FXML
    private void openInventory() {
        loadIntoContent("/fxml/Inventory.fxml");
    }

    @FXML
    private void openLogs() {
        loadIntoContent("/fxml/Logs.fxml");
    }

    @FXML
    private void runScenarioA() {
        if (warehouse != null && taskManager != null) {
            app.demo.ScenarioRunner.runScenarioA(warehouse, taskManager);
        }
    }

    @FXML
    private void runScenarioB() {
        if (warehouse != null && taskManager != null) {
            app.demo.ScenarioRunner.runScenarioB(warehouse, taskManager);
        }
    }

    @FXML
    private void runScenarioC() {
        if (warehouse != null && taskManager != null) {
            app.demo.ScenarioRunner.runScenarioC(warehouse, taskManager);
        }
    }

    private void loadIntoContent(String resource) {
        try {
            FXMLLoader f = new FXMLLoader(getClass().getResource(resource));
            Node n = f.load();
            Object controller = f.getController();

            // Provide references to controllers that accept them
            if (controller instanceof AGVPanelController) {
                ((AGVPanelController) controller).setWarehouse(this.warehouse);
                ((AGVPanelController) controller).setTaskManager(this.taskManager);
            }

            if (controller instanceof InventoryController) {
                ((InventoryController) controller).setTaskManager(this.taskManager);
                ((InventoryController) controller).setStorageUnit(this.storageUnit);
            }

            if (controller instanceof LogsController) {
                // future: pass logging service
            }

            contentArea.getChildren().setAll(n);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
