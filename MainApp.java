import javafx.application.Application;
import javafx.beans.property.BooleanPr    operty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.*;
import java.util.*;

public class MainApp extends Application {
    private final String DB_URL = "jdbc:mysql://localhost:3306/testdb";
    private final String DB_USER = "root";
    private final String DB_PASSWORD = "dbms123";
    private final List<String> ALLOWED_TABLES = List.of("EMP", "DEPT", "BONUS", "SALGRADE", "FifthTable");

    private VBox operationPane = new VBox(10);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("JavaFX + MySQL CRUD App");
        ComboBox<String> operations = new ComboBox<>();
        operations.getItems().addAll("Create Table", "Insert", "Select", "Update", "Delete");
        operations.setPromptText("Choose Operation");
        operations.setOnAction(e -> handleOperation(operations.getValue()));

        VBox root = new VBox(15);
        root.setStyle("-fx-padding: 20; -fx-font-size: 14;");
        root.getChildren().addAll(operations, operationPane);

        primaryStage.setScene(new Scene(root, 900, 700));
        primaryStage.show();
    }

    private void handleOperation(String operation) {
        operationPane.getChildren().clear();
        switch (operation) {
            case "Create Table":
                showCreateTableForm();
                break;
            case "Insert":
                showInsertForm();
                break;
            case "Select":
                showSelectTable();
                break;
            case "Update":
                showUpdateForm();
                break;
            case "Delete":
                showDeleteForm();
                break;
        }
    }

    private void showCreateTableForm() {
        TextField tableNameField = new TextField();
        tableNameField.setPromptText("Enter Table Name");
        TextArea columnDefsArea = new TextArea();
        columnDefsArea.setPromptText("Enter Column Definitions (e.g., id INT PRIMARY KEY, name VARCHAR(100))");
        Button createButton = new Button("Create Table");

        createButton.setOnAction(e -> {
            String tableName = tableNameField.getText().trim();
            String columnDefs = columnDefsArea.getText().trim();
            if (tableName.isEmpty() || columnDefs.isEmpty()) {
                showAlert("Error", "Table name and column definitions cannot be empty.");
                return;
            }
            String createSQL = "CREATE TABLE IF NOT EXISTS " + tableName + " (" + columnDefs + ")";
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(createSQL);
                showAlert("Success", "Table '" + tableName + "' created successfully.");
            } catch (SQLException ex) {
                showAlert("Error", "Failed to create table: " + ex.getMessage());
            }
        });

        operationPane.getChildren().addAll(new Label("Table Name:"), tableNameField,
                new Label("Column Definitions:"), columnDefsArea, createButton);
    }

    private void showDeleteForm() {
        operationPane.getChildren().clear();
        ComboBox<String> tableSelector = new ComboBox<>();
        tableSelector.setPromptText("Select Table to Delete Rows From");
        tableSelector.getItems().addAll(ALLOWED_TABLES);
        VBox tableContainer = new VBox(10);
        Button deleteButton = new Button("Delete Selected Rows");
        tableSelector.setOnAction(e -> {
            tableContainer.getChildren().clear();
            String selectedTable = tableSelector.getValue();
            TableView<RowWrapper> tableView = createTableViewForDeleteWithCheckbox(selectedTable);
            if (tableView != null) {
                tableContainer.getChildren().add(tableView);
                deleteButton.setOnAction(event -> {
                    ObservableList<RowWrapper> allRows = tableView.getItems();
                    List<RowWrapper> selectedRows = new ArrayList<>();
                    for (RowWrapper wrapper : allRows) {
                        if (wrapper.selected.get()) {
                            selectedRows.add(wrapper);
                        }
                    }
                    if (selectedRows.isEmpty()) {
                        showAlert("Warning", "No rows selected.");
                        return;
                    }
                    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                        for (RowWrapper wrapper : selectedRows) {
                            Map<String, Object> row = wrapper.data;
                            StringBuilder whereClause = new StringBuilder("WHERE ");
                            List<String> conditions = new ArrayList<>();
                            for (Map.Entry<String, Object> entry : row.entrySet()) {
                                String col = entry.getKey();
                                Object val = entry.getValue();
                                if (val == null) {
                                    conditions.add(col + " IS NULL");
                                } else {
                                    conditions.add(col + "='" + val.toString().replace("'", "''") + "'");
                                }
                            }
                            whereClause.append(String.join(" AND ", conditions));
                            String deleteQuery = "DELETE FROM " + selectedTable + " " + whereClause;
                            try (Statement stmt = conn.createStatement()) {
                                stmt.executeUpdate(deleteQuery);
                            }
                        }
                        showAlert("Success", "Selected rows deleted successfully.");
                        tableContainer.getChildren().set(0, createTableViewForDeleteWithCheckbox(selectedTable));
                    } catch (SQLException ex) {
                        showAlert("Error", "Deletion failed: " + ex.getMessage());
                    }
                });
            }
        });
        operationPane.getChildren().addAll(new Label("Select Table:"), tableSelector, tableContainer, deleteButton);
    }

    private TableView<RowWrapper> createTableViewForDeleteWithCheckbox(String tableName) {
        TableView<RowWrapper> tableView = new TableView<>();
        tableView.setEditable(true);
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName)) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            TableColumn<RowWrapper, Boolean> selectColumn = new TableColumn<>("Select");
            selectColumn.setCellValueFactory(cellData -> cellData.getValue().selected);
            selectColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectColumn));
            selectColumn.setEditable(true);
            tableView.getColumns().add(selectColumn);

            List<String> columnNames = new ArrayList<>();
            for (int i = 1; i <= columnCount; i++) {
                columnNames.add(metaData.getColumnName(i));
            }

            for (int i = 0; i < columnCount; i++) {
                final int colIndex = i;
                TableColumn<RowWrapper, String> column = new TableColumn<>(columnNames.get(i));
                column.setCellValueFactory(data -> {
                    Object value = data.getValue().data.get(columnNames.get(colIndex));
                    return new javafx.beans.property.SimpleStringProperty(value == null ? "" : value.toString());
                });
                tableView.getColumns().add(column);
            }

            ObservableList<RowWrapper> rows = FXCollections.observableArrayList();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 0; i < columnCount; i++) {
                    row.put(columnNames.get(i), rs.getObject(i + 1));
                }
                rows.add(new RowWrapper(row));
            }
            tableView.setItems(rows);

        } catch (SQLException ex) {
            showAlert("Error", "Failed to load table: " + ex.getMessage());
            return null;
        }
        return tableView;
    }

    public static class RowWrapper {
        BooleanProperty selected = new SimpleBooleanProperty(false);
        Map<String, Object> data;

        RowWrapper(Map<String, Object> data) {
            this.data = data;
        }
    }

    private void showInsertForm() {
        operationPane.getChildren().clear();

        Label selectTableLabel = new Label("Select Table to Insert Into:");
        ComboBox<String> tableSelector = new ComboBox<>();
        tableSelector.getItems().addAll(ALLOWED_TABLES);
        tableSelector.setPromptText("Choose Table");

        VBox formBox = new VBox(10);
        formBox.setPadding(new Insets(10));

        Button insertButton = new Button("Insert Record");
        insertButton.setDisable(true);

        tableSelector.setOnAction(e -> {
            formBox.getChildren().clear();
            String selectedTable = tableSelector.getValue();
            if (selectedTable != null) {
                try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                     Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT * FROM " + selectedTable + " LIMIT 1")) {

                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    Map<String, TextField> inputFields = new LinkedHashMap<>();

                    for (int i = 1; i <= columnCount; i++) {
                        String colName = metaData.getColumnName(i);
                        boolean isAutoIncrement = metaData.isAutoIncrement(i);

                        if (isAutoIncrement) continue;

                        Label lbl = new Label(colName + ":");
                        TextField tf = new TextField();
                        tf.setPromptText("Enter " + colName);
                        formBox.getChildren().add(new HBox(10, lbl, tf));
                        inputFields.put(colName, tf);
                    }
                    insertButton.setDisable(false);
                    insertButton.setOnAction(ev -> {
                        StringBuilder columnsPart = new StringBuilder();
                        StringBuilder valuesPart = new StringBuilder();
                        try (Connection insertConn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                            PreparedStatement pstmt;
                            int count = 0;
                            for (Map.Entry<String, TextField> entry : inputFields.entrySet()) {
                                if (count > 0) {
                                    columnsPart.append(", ");
                                    valuesPart.append(", ");
                                }
                                columnsPart.append(entry.getKey());
                                valuesPart.append("?");
                                count++;
                            }
                            String insertSQL = "INSERT INTO " + selectedTable + " (" + columnsPart + ") VALUES (" + valuesPart + ")";
                            pstmt = insertConn.prepareStatement(insertSQL);
                            int index = 1;
                            for (TextField tf : inputFields.values()) {
                                String val = tf.getText().trim();
                                if (val.isEmpty()) {
                                    pstmt.setNull(index, Types.VARCHAR);
                                } else {
                                    pstmt.setString(index, val);
                                }
                                index++;
                            }
                            int rowsInserted = pstmt.executeUpdate();
                            if (rowsInserted > 0) {
                                showAlert("Success", "Record inserted successfully.");
                                inputFields.values().forEach(TextField::clear);
                            } else {
                                showAlert("Warning", "No record inserted.");
                            }
                        } catch (SQLException ex) {
                            showAlert("Error", "Insert failed: " + ex.getMessage());
                        }
                    });
                } catch (SQLException ex) {
                    showAlert("Error", "Failed to fetch table metadata: " + ex.getMessage());
                }
            }
        });
        operationPane.getChildren().addAll(selectTableLabel, tableSelector, formBox, insertButton);
    }

    private void showSelectTable() {
        operationPane.getChildren().clear();
        Label selectLabel = new Label("Select Tables to View Data:");
        ListView<CheckBox> tableCheckList = new ListView<>();
        tableCheckList.setPrefHeight(150);
        for (String tableName : ALLOWED_TABLES) {
            CheckBox cb = new CheckBox(tableName);
            tableCheckList.getItems().add(cb);
        }
        Button loadDataButton = new Button("Load Selected Tables Data");
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        VBox dataBox = new VBox(15);

        loadDataButton.setOnAction(e -> {
            dataBox.getChildren().clear();
            for (CheckBox cb : tableCheckList.getItems()) {
                if (cb.isSelected()) {
                    TableView<Map<String, Object>> tableView = loadTableData(cb.getText());
                    if (tableView != null) {
                        Label lbl = new Label("Data from table: " + cb.getText());
                        dataBox.getChildren().addAll(lbl, tableView);
                    }
                }
            }
        });

        scrollPane.setContent(dataBox);

        operationPane.getChildren().addAll(selectLabel, tableCheckList, loadDataButton, scrollPane);
    }

    private TableView<Map<String, Object>> loadTableData(String tableName) {
        TableView<Map<String, Object>> tableView = new TableView<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName)) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            List<String> columnNames = new ArrayList<>();
            for (int i = 1; i <= columnCount; i++) {
                columnNames.add(metaData.getColumnName(i));
            }

            for (int i = 0; i < columnCount; i++) {
                final int colIndex = i;
                TableColumn<Map<String, Object>, String> col = new TableColumn<>(columnNames.get(i));
                col.setCellValueFactory(cellData -> {
                    Object value = cellData.getValue().get(columnNames.get(colIndex));
                    return new javafx.beans.property.SimpleStringProperty(value == null ? "" : value.toString());
                });
                tableView.getColumns().add(col);
            }

            ObservableList<Map<String, Object>> data = FXCollections.observableArrayList();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 0; i < columnCount; i++) {
                    row.put(columnNames.get(i), rs.getObject(i + 1));
                }
                data.add(row);
            }
            tableView.setItems(data);

        } catch (SQLException ex) {
            showAlert("Error", "Failed to load table '" + tableName + "': " + ex.getMessage());
            return null;
        }
        return tableView;
    }

    private void showUpdateForm() {
        operationPane.getChildren().clear();
        Label tableLabel = new Label("Select Table to Update:");
        ComboBox<String> tableSelector = new ComboBox<>();
        tableSelector.getItems().addAll(ALLOWED_TABLES);
        tableSelector.setPromptText("Choose Table");

        VBox updateFormBox = new VBox(10);
        updateFormBox.setPadding(new Insets(10));
        Button updateButton = new Button("Update Record");
        updateButton.setDisable(true);

        tableSelector.setOnAction(e -> {
            updateFormBox.getChildren().clear();
            String selectedTable = tableSelector.getValue();
            if (selectedTable == null) return;

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM " + selectedTable + " LIMIT 1")) {

                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                Map<String, TextField> inputFields = new LinkedHashMap<>();

                for (int i = 1; i <= columnCount; i++) {
                    String colName = metaData.getColumnName(i);
                    Label lbl = new Label(colName + ":");
                    TextField tf = new TextField();
                    tf.setPromptText("Enter " + colName);
                    updateFormBox.getChildren().add(new HBox(10, lbl, tf));
                    inputFields.put(colName, tf);
                }
                updateButton.setDisable(false);

                updateButton.setOnAction(ev -> {
                    // We assume the first field is the primary key for WHERE clause
                    if (inputFields.isEmpty()) {
                        showAlert("Error", "No columns to update.");
                        return;
                    }
                    String primaryKeyCol = inputFields.keySet().iterator().next();
                    String primaryKeyVal = inputFields.get(primaryKeyCol).getText().trim();
                    if (primaryKeyVal.isEmpty()) {
                        showAlert("Error", "Primary key value cannot be empty.");
                        return;
                    }
                    StringBuilder setClause = new StringBuilder();
                    int count = 0;
                    for (Map.Entry<String, TextField> entry : inputFields.entrySet()) {
                        String col = entry.getKey();
                        String val = entry.getValue().getText().trim();
                        if (col.equals(primaryKeyCol)) continue; // skip primary key in set clause

                        if (count > 0) setClause.append(", ");
                        setClause.append(col).append("=");
                        if (val.isEmpty()) {
                            setClause.append("NULL");
                        } else {
                            setClause.append("'").append(val.replace("'", "''")).append("'");
                        }
                        count++;
                    }
                    if (count == 0) {
                        showAlert("Warning", "No fields to update.");
                        return;
                    }
                    String updateSQL = "UPDATE " + selectedTable + " SET " + setClause + " WHERE " + primaryKeyCol + "='" + primaryKeyVal.replace("'", "''") + "'";
                    try (Connection updateConn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                         Statement updateStmt = updateConn.createStatement()) {
                        int rows = updateStmt.executeUpdate(updateSQL);
                        if (rows > 0) {
                            showAlert("Success", "Record updated successfully.");
                        } else {
                            showAlert("Warning", "No records updated. Check if primary key exists.");
                        }
                    } catch (SQLException ex) {
                        showAlert("Error", "Update failed: " + ex.getMessage());
                    }
                });

            } catch (SQLException ex) {
                showAlert("Error", "Failed to fetch table metadata: " + ex.getMessage());
            }
        });

        operationPane.getChildren().addAll(tableLabel, tableSelector, updateFormBox, updateButton);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}