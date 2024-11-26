package src.main;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import src.main.dataHandle.Course;
import src.main.dataHandle.CourseManager;

import java.util.List;

public class GraphManager {

    //Implementation of the scatter plot diagram below
    public static Scene createScatterPlotScene(Stage primaryStage, String subject1, String subject2) {
        // Title and Back Button
        Label titleLabel = new Label("Scatter Plot: " + subject1 + " vs " + subject2);
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: #e76f51; -fx-text-fill: white; -fx-font-weight: bold;");
        backButton.setOnAction(e -> primaryStage.setScene(new GUI().createMainScene(primaryStage))); // Return to the main scene

        // X-Axisand Y-Axis (Grades for Both subjects)
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel(subject1 + " Grades");
        xAxis.setTickLabelFont(Font.font("Arial", 12));
        xAxis.setMinorTickVisible(false);
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel(subject2 + " Grades");
        yAxis.setTickLabelFont(Font.font("Arial", 12));
        yAxis.setMinorTickVisible(false);

        // Making the Scatter Chart
        ScatterChart<Number, Number> scatterChart = new ScatterChart<>(xAxis, yAxis);
        XYChart.Series<Number, Number> series1 = new XYChart.Series<>();
        series1.setName(subject1); // Set legend name for the first series
        XYChart.Series<Number, Number> series2 = new XYChart.Series<>();
        series2.setName(subject2); // Set legend name for the second series

        // Accession the data
        CourseManager courses = CourseManager.getInstance();
        courses.loadGraduateGrades();
        Course course1 = courses.getCourseByName(subject1);
        Course course2 = courses.getCourseByName(subject2);

        if (course1 != null && course2 != null) {
            List<Double> grades1 = course1.getGrades();
            List<Double> grades2 = course2.getGrades();

            if (!grades1.isEmpty() && !grades2.isEmpty()) {
                // Use the smaller dataset size to avoid out-of-bounds errors
                int dataSize = Math.min(grades1.size(), grades2.size());

                // Since data set large using a max display of 500 points to improve readability
                int samplingRate = Math.max(1, dataSize / 500);
                for (int i = 0; i < dataSize; i += samplingRate) {
                    // Add data for subject1 (regular dots)
                    XYChart.Data<Number, Number> dataPoint1 = new XYChart.Data<>(grades1.get(i), grades2.get(i));
                    series1.getData().add(dataPoint1);

                    // Add data for subject2 (crosses)
                    XYChart.Data<Number, Number> dataPoint2 = new XYChart.Data<>(grades1.get(i), grades2.get(i));
                    series2.getData().add(dataPoint2);

                    // Code to make data points crosses to be able to see both
                    dataPoint2.nodeProperty().addListener((obs, oldNode, newNode) -> {
                        if (newNode != null) {
                            // Replace the default node with a cross
                            Group cross = new Group();
                            Line line1 = new Line(-4, -4, 4, 4); // Diagonal line 1
                            Line line2 = new Line(-4, 4, 4, -4); // Diagonal line 2
                            line1.setStyle("-fx-stroke: red; -fx-stroke-width: 1.5;"); // Red cross
                            line2.setStyle("-fx-stroke: red; -fx-stroke-width: 1.5;");
                            cross.getChildren().addAll(line1, line2);
                            ((StackPane) newNode).getChildren().clear(); // Clear default node styling
                            ((StackPane) newNode).getChildren().add(cross); // Add custom cross node
                        }
                    });
                }
            }

        }
        // Add data to the scatter chart
        scatterChart.getData().addAll(series1, series2);

        // Layout
        VBox layout = new VBox(10, titleLabel, scatterChart, backButton);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        return new Scene(layout, 800, 600);

    }
}

