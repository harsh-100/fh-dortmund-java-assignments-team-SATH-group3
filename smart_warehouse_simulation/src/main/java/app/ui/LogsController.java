package app.ui;

import exceptions.ExceptionStore;
import exceptions.ExceptionStore.ExceptionRecord;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;
import logging.LogService;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

public class LogsController {

    @FXML
    private ComboBox<String> logFileCombo;

    @FXML
    private TextArea logsArea;

    @FXML
    private CheckBox autoRefresh;

    @FXML
    private ListView<ExceptionRecord> exceptionsList;

    @FXML
    private TextArea stacktraceArea;

    private final LogService logService = new LogService("logs");
    private Timeline refresher;

    @FXML
    public void initialize() {
        // populate available log files
        List<String> files = logService.listLogFiles();
        logFileCombo.getItems().addAll(files);

        // try to select today's system log by default
        String todayName = String.format("SYSTEM-%s.log", LocalDate.now());
        if (files.contains(todayName)) logFileCombo.getSelectionModel().select(todayName);
        else if (!files.isEmpty()) logFileCombo.getSelectionModel().select(0);

        logFileCombo.setOnAction(evt -> refreshLogs());

        // exceptions list
        exceptionsList.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV == null) stacktraceArea.clear();
            else stacktraceArea.setText(newV.stacktrace);
        });

        loadExceptions();

        // auto-refresh timeline
        refresher = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            if (autoRefresh.isSelected()) refreshLogs();
            loadExceptions();
        }));
        refresher.setCycleCount(Timeline.INDEFINITE);
        refresher.play();

        // initial load
        refreshLogs();
    }

    @FXML
    public void onRefresh() {
        refreshLogs();
        loadExceptions();
    }

    private void loadExceptions() {
        List<ExceptionRecord> recs = ExceptionStore.getInstance().recent(200);
        Platform.runLater(() -> {
            exceptionsList.getItems().setAll(recs);
        });
    }

    private void refreshLogs() {
        String sel = logFileCombo.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        List<String> lines = logService.readLastLines(sel, 1000);
        StringBuilder sb = new StringBuilder();
        for (String l : lines) {
            sb.append(l).append('\n');
        }
        Platform.runLater(() -> {
            logsArea.setText(sb.toString());
            logsArea.setScrollTop(Double.MAX_VALUE);
        });
    }

}
