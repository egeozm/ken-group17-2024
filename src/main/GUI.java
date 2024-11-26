package src.main;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import src.main.dataHandle.Course;
import src.main.dataHandle.CourseManager;
import src.main.GraphManager;

import java.util.Objects;

public class GUI extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Title Label
        Label titleLabel = new Label("Chart Displayer");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2a9d8f;");

        // Dropdown for graph types
        Label graphTypeLabel = new Label("Select Graph Type:");
        ComboBox<String> graphDropdown = new ComboBox<>();
        graphDropdown.getItems().addAll("Bar Chart", "Histograms", "Scatter Plot");
        graphDropdown.setValue("Bar Chart");

        // Dropdown for subject 1
        Label subjectLabel1 = new Label("Select Subject:");
        ComboBox<String> subjectDropdown1 = new ComboBox<>();
        CourseManager courses = CourseManager.getInstance();
        courses.loadCurrentGrades();
        courses.loadGraduateGrades();
        for (Course course : courses.getCurrentCourses()) {
            subjectDropdown1.getItems().add(course.getName());
        }
        subjectDropdown1.setValue("*Pick a Course*");

        // Dropdown for subject 2 (used only for Scatter Plot)
        Label subjectLabel2 = new Label("Select Subject:");
        ComboBox<String> subjectDropdown2 = new ComboBox<>();
        subjectDropdown2.setVisible(false);
        subjectLabel2.setVisible(false);
        subjectDropdown2.setValue("*Pick a Course*");

        // Event listener for graph type dropdown
        graphDropdown.valueProperty().addListener((observable, oldValue, newValue) -> {
            if ("Scatter Plot".equals(newValue)) {
                subjectDropdown2.setVisible(true);
                subjectLabel2.setVisible(true);
                subjectDropdown2.getItems().clear();
                for (Course course : courses.getCurrentCourses()) {
                    subjectDropdown2.getItems().add(course.getName());
                }
            } else {
                subjectDropdown2.setVisible(false);
                subjectLabel2.setVisible(false);
            }
        });

        // Submit Button
        Button submitButton = new Button("Submit");
        submitButton.setStyle("-fx-background-color: #e76f51; -fx-text-fill: white; -fx-font-weight: bold;");
        submitButton.setOnAction(e -> {
            String selectedGraph = graphDropdown.getValue();

            switch (selectedGraph) {
                case "Scatter Plot":
                    String subject1 = subjectDropdown1.getValue();
                    String subject2 = subjectDropdown2.getValue();
                    if (!Objects.equals(subject1, "*Pick a Course*") && !Objects.equals(subject2, "*Pick a Course*")) {
                        primaryStage.setScene(GraphManager.createScatterPlotScene(primaryStage, subject1, subject2));
                    } else {
                        showAlert("Please select both subjects.");
                    }
                    break;

//                case "Bar Chart":
//                    subject1 = subjectDropdown1.getValue();
//                    if (!Objects.equals(subject1, "*Pick a Course*")) {
//                        primaryStage.setScene(GraphManager.createBarChartScene(primaryStage, subject1));
//                    } else {
//                        showAlert("Please select a subject.");
//                    }
//                    break;

//                case "Histograms":
//                    subject1 = subjectDropdown1.getValue();
//                    if (!Objects.equals(subject1, "*Pick a Course*")) {
//                        primaryStage.setScene(GraphManager.createHistogramScene(primaryStage, subject1));
//                    } else {
//                        showAlert("Please select a subject.");
//                    }
//                    break;

                default:
                    showAlert("Invalid graph type selected.");
            }
        });

        // Layout
        VBox layout = new VBox(15);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        layout.getChildren().addAll(
                titleLabel,
                graphTypeLabel, graphDropdown,
                subjectLabel1, subjectDropdown1,
                subjectLabel2, subjectDropdown2,
                submitButton
        );

        // Set the scene
        Scene scene = new Scene(layout, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Chart Displayer");
        primaryStage.show();
    }

    // Method to return to the main scene
    public Scene createMainScene(Stage primaryStage) {
        start(primaryStage);
        return primaryStage.getScene();
    }

    // Alert method for invalid input
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}