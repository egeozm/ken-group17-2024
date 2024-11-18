package src.main;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class GUI extends Application {
    @Override
    public void start(Stage primaryStage) {
        // Set up the primary stage
        primaryStage.setTitle("JavaFX GUI Example");

        // Create a GridPane layout
        GridPane grid = new GridPane();
        grid.setHgap(10); // Horizontal gap between elements
        grid.setVgap(10); // Vertical gap between elements

        // Create a Label
        Label nameLabel = new Label("Enter your name:");
        grid.add(nameLabel, 0, 0); // Column 0, Row 0

        // Create a TextField for user input
        TextField nameField = new TextField();
        grid.add(nameField, 1, 0); // Column 1, Row 0

        // Create a Button
        Button submitButton = new Button("Submit");
        grid.add(submitButton, 1, 1); // Column 1, Row 1

        // Create a Label for the output
        Label outputLabel = new Label();
        grid.add(outputLabel, 0, 2, 2, 1); // Span across two columns

        // Add an action to the button
        submitButton.setOnAction(e -> {
            String name = nameField.getText(); // Get the text from the TextField
            outputLabel.setText("Hello, " + name + "!"); // Update the output label
        });

        // Create a Scene with the layout
        Scene scene = new Scene(grid, 300, 200);

        // Set the scene on the stage and show it
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args); // Launch the JavaFX application
    }
}