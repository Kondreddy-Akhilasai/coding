import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Calculator extends Application {

    private TextField display = new TextField();
    private String operator = "";
    private double num1 = 0.0;
    private boolean startNewNumber = true; // Flag to clear display on new input after operator

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        display.setEditable(false);
        display.setAlignment(Pos.CENTER_RIGHT);
        display.setPrefHeight(50);
        display.setText("0");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(display, 0, 0, 4, 1);

        String[] buttons = {
            "7", "8", "9", "/",
            "4", "5", "6", "*",
            "1", "2", "3", "-",
            "0", ".", "=", "+",
            "C"
        };

        int row = 1;
        int col = 0;

        for (String label : buttons) {
            Button btn = new Button(label);
            btn.setPrefSize(60, 60);
            btn.setOnAction(e -> handleButton(label));
            grid.add(btn, col, row);

            col++;
            if (col == 4) {
                col = 0;
                row++;
            }
        }

        Scene scene = new Scene(grid, 270, 400);
        stage.setTitle("Calculator");
        stage.setScene(scene);
        stage.show();
    }

    private void handleButton(String value) {
        switch (value) {
            case "C":
                display.setText("0");
                operator = "";
                num1 = 0.0;
                startNewNumber = true;
                break;
            case "+": case "-": case "*": case "/":
                if (!operator.isEmpty()) {
                    // If operator already set, calculate intermediate result first
                    if (!startNewNumber) {
                        double num2 = Double.parseDouble(display.getText());
                        num1 = calculate(num1, num2, operator);
                        display.setText(String.valueOf(num1));
                    }
                } else {
                    num1 = Double.parseDouble(display.getText());
                }
                operator = value;
                startNewNumber = true;
                break;
            case "=":
                if (operator.isEmpty() || startNewNumber) {
                    // No operator or no second operand to calculate
                    return;
                }
                double num2 = Double.parseDouble(display.getText());
                double result = calculate(num1, num2, operator);
                display.setText(result == Double.NaN ? "Error" : String.valueOf(result));
                operator = "";
                startNewNumber = true;
                break;
            case ".":
                if (startNewNumber) {
                    display.setText("0.");
                    startNewNumber = false;
                } else if (!display.getText().contains(".")) {
                    display.appendText(".");
                }
                break;
            default: // digits
                if (startNewNumber || display.getText().equals("0")) {
                    display.setText(value);
                    startNewNumber = false;
                } else {
                    display.appendText(value);
                }
                break;
        }
    }

    private double calculate(double a, double b, String op) {
        return switch (op) {
            case "+" -> a + b;
            case "-" -> a - b;
            case "*" -> a * b;
            case "/" -> b != 0 ? a / b : Double.NaN;
            default -> 0;
        };
    }
}
