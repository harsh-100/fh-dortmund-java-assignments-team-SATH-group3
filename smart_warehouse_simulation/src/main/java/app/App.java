package app;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX entry point that loads the Dashboard FXML. Controllers are lightweight stubs
 * that can be extended to call backend services.
 */
public class App extends Application {

    @Override
    public void start(Stage stage) {
        try {
            // create shared TaskManager and Warehouse and pass them to controllers
            tasks.TaskManager tm = null;
            try {
                tm = new tasks.TaskManager("TM-UI");
            } catch (Exception e) {
                e.printStackTrace();
            }

            warehouse.Warehouse wh;
            if (tm != null) {
                wh = new warehouse.Warehouse(tm);
            } else {
                wh = new warehouse.Warehouse();
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Dashboard.fxml"));
            Parent root = loader.load();

            // prepare a sample storage unit and items for the Inventory UI
            storage.StorageUnit su = new storage.StorageUnit("SU-UI-1", 20, new java.awt.Point(2,2));
            su.addItems(new storage.Item("I-UI-1", "Widget-UI", 1.0));
            su.addItems(new storage.Item("I-UI-2", "Gadget-UI", 2.5));

            // inject backend references into controller
            Object controller = loader.getController();
            if (controller instanceof app.ui.DashboardController) {
                app.ui.DashboardController dc = (app.ui.DashboardController) controller;
                dc.setTaskManager(tm);
                dc.setWarehouse(wh);
                dc.setStorageUnit(su);
            }

            stage.setScene(new Scene(root, 800, 600));
            stage.setTitle("Smart Warehouse Demo");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
