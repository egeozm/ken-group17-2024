package src.main;

import java.util.List;
import java.util.Map;


import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.*;
import java.util.stream.Collectors;

import javafx.scene.chart.BarChart;

import javafx.scene.control.Label;
import src.main.dataHandle.Course;
import src.main.dataHandle.CourseManager;
import src.main.dataHandle.StudentGroupingManager;
import src.main.dataHandle.StudentInfoManager;
import src.main.dataHandle.CurrentStudentManager;
import javafx.scene.chart.PieChart;

public class GraphManager {
    public static Scene createBarChartScene(Stage primaryStage, String subject, String title, String selectedAxis) {
        // Title and Back Button
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-underline: true;");

        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: #e76f51; -fx-text-fill: white; -fx-font-weight: bold;");
        backButton.setOnAction(e -> primaryStage.setScene(new GUI().createMainScene(primaryStage))); // Return to the main scene

        // Define X-Axis and Y-Axis
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();

        if (selectedAxis.equalsIgnoreCase("Grades")) {
            xAxis.setLabel("Grades");
            yAxis.setLabel("Frequency");
        } else {
            xAxis.setLabel(selectedAxis);
            yAxis.setLabel("Average Grade");
        }

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);

        // Access the data
        CourseManager courses = CourseManager.getInstance();
        courses.loadPredictedGrades();
        Course course = courses.getCourseByName(subject);

        if (course != null) {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(subject + " - " + selectedAxis);

            if (selectedAxis.equalsIgnoreCase("Grades")) {
                // Calculate frequency of grades
                Map<Double, Long> gradeFrequencies = course.getGrades().stream()
                        .filter(g -> g != null)
                        .collect(Collectors.groupingBy(Double::doubleValue, Collectors.counting()));

                for (Map.Entry<Double, Long> entry : gradeFrequencies.entrySet()) {
                    series.getData().add(new XYChart.Data<>(String.valueOf(entry.getKey()), entry.getValue()));
                }
            } else {
                // Use the existing logic for other attributes
                StudentGroupingManager groupingManager = new StudentGroupingManager(new StudentInfoManager(), new CurrentStudentManager());
                Map<String, Double> averages = groupingManager.calculateAverageGradesByAttribute(course, selectedAxis);

                for (Map.Entry<String, Double> entry : averages.entrySet()) {
                    series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
                }
            }

            barChart.getData().add(series);
        }

        VBox layout = new VBox(10.0, titleLabel, barChart, backButton);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        return new Scene(layout, 900, 600);
    }

    public static Scene createScatterPlotScene(Stage primaryStage, String subject, String title, String selectedAxis) {
        // Title and Back Button
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-underline: true;");

        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: #e76f51; -fx-text-fill: white; -fx-font-weight: bold;");
        backButton.setOnAction(e -> primaryStage.setScene(new GUI().createMainScene(primaryStage))); // Return to the main scene

        // Define X-Axis and Y-Axis
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();

        if (selectedAxis.equalsIgnoreCase("Grades")) {
            xAxis.setLabel("Grades");
            yAxis.setLabel("Frequency");
        } else {
            xAxis.setLabel(selectedAxis);
            yAxis.setLabel("Grades");
        }

        ScatterChart<String, Number> scatterChart = new ScatterChart<>(xAxis, yAxis);
        scatterChart.setTitle(subject + " - Scatter Plot");

        // Access the data
        CourseManager courses = CourseManager.getInstance();
        courses.loadPredictedGrades();
        Course course = courses.getCourseByName(subject);

        if (course != null) {
            if (selectedAxis.equalsIgnoreCase("Grades")) {
                // Calculate frequency of grades
                Map<Double, Long> gradeFrequencies = course.getGrades().stream()
                        .filter(g -> g != null)
                        .collect(Collectors.groupingBy(Double::doubleValue, Collectors.counting()));

                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName("Grade Frequency");

                for (Map.Entry<Double, Long> entry : gradeFrequencies.entrySet()) {
                    series.getData().add(new XYChart.Data<>(String.valueOf(entry.getKey()), entry.getValue()));
                }

                scatterChart.getData().add(series);
            } else {
                // Use the existing logic for other attributes
                StudentGroupingManager groupingManager = new StudentGroupingManager(new StudentInfoManager(), new CurrentStudentManager());
                Map<String, List<Double>> groupedGrades = groupingManager.groupGradesByAttribute(course, selectedAxis);

                for (Map.Entry<String, List<Double>> entry : groupedGrades.entrySet()) {
                    XYChart.Series<String, Number> series = new XYChart.Series<>();
                    series.setName(entry.getKey());

                    for (Double grade : entry.getValue()) {
                        if (grade != null) {
                            series.getData().add(new XYChart.Data<>(entry.getKey(), grade));
                        }
                    }

                    scatterChart.getData().add(series);
                }
            }
        }

        VBox layout = new VBox(10.0, titleLabel, scatterChart, backButton);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        return new Scene(layout, 900, 600);
    }

    public static Scene createPieChartScene(Stage primaryStage, String subject, String title, String selectedAxis) {
        // Title and Back Button
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-underline: true;");

        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: #e76f51; -fx-text-fill: white; -fx-font-weight: bold;");
        backButton.setOnAction(e -> primaryStage.setScene(new GUI().createMainScene(primaryStage))); // Return to the main scene

        PieChart pieChart = new PieChart();
        pieChart.setTitle(subject + " - Pie Chart");

        // Access the data
        CourseManager courses = CourseManager.getInstance();
        courses.loadPredictedGrades();
        Course course = courses.getCourseByName(subject);

        if (course != null) {
            if (selectedAxis.equalsIgnoreCase("Grades")) {
                // Calculate frequency of grades
                Map<Double, Long> gradeFrequencies = course.getGrades().stream()
                        .filter(g -> g != null)
                        .collect(Collectors.groupingBy(Double::doubleValue, Collectors.counting()));

                for (Map.Entry<Double, Long> entry : gradeFrequencies.entrySet()) {
                    pieChart.getData().add(new PieChart.Data("Grade " + entry.getKey(), entry.getValue()));
                }
            } else {
                // Use existing logic for other attributes
                StudentGroupingManager groupingManager = new StudentGroupingManager(new StudentInfoManager(), new CurrentStudentManager());
                Map<String, List<Double>> groupedGrades = groupingManager.groupGradesByAttribute(course, selectedAxis);

                for (Map.Entry<String, List<Double>> entry : groupedGrades.entrySet()) {
                    pieChart.getData().add(new PieChart.Data(entry.getKey(), entry.getValue().size()));
                }
            }
        }

        VBox layout = new VBox(10.0, titleLabel, pieChart, backButton);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        return new Scene(layout, 900, 600);
    }

    public static Scene createBoxPlotScene(Stage primaryStage, String subject, String title, String selectedAxis) {
        // Title and Back Button
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-underline: true;");

        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: #e76f51; -fx-text-fill: white; -fx-font-weight: bold;");
        backButton.setOnAction(e -> primaryStage.setScene(new GUI().createMainScene(primaryStage))); // Return to the main scene

        // Access the data
        CourseManager courses = CourseManager.getInstance();
        courses.loadPredictedGrades(); // Load predicted grades dataset
        Course course = courses.getCourseByName(subject);

        Pane plotPane = new Pane(); // Pane to hold the custom box plots
        plotPane.setPrefSize(1000, 600); // Adjust the pane size

        if (course != null) {
            Map<String, List<Double>> groupedGrades;

            if (selectedAxis.equalsIgnoreCase("Grades")) {
                // Group by unique grade values
                groupedGrades = course.getGrades().stream()
                        .filter(Objects::nonNull) // Exclude null grades
                        .collect(Collectors.groupingBy(g -> String.valueOf(g), Collectors.toList()));
            } else {
                // Use the default grouping by attributes
                StudentGroupingManager groupingManager = new StudentGroupingManager(new StudentInfoManager(), new CurrentStudentManager());
                groupedGrades = groupingManager.groupGradesByAttribute(course, selectedAxis);
            }

            int groupIndex = 0; // Index to position groups on the X-axis
            double plotWidth = 900; // Total width of the plot
            double groupSpacing = plotWidth / groupedGrades.size();
            double plotHeight = 500; // Height of the plot

            for (Map.Entry<String, List<Double>> entry : groupedGrades.entrySet()) {
                String group = entry.getKey(); // Either grade or group name
                List<Double> grades = entry.getValue();

                // Calculate box plot statistics
                double min = grades.stream().mapToDouble(Double::doubleValue).min().orElse(0.0);
                double max = grades.stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
                double median = calculateMedian(grades);
                double lowerQuartile = calculateQuartile(grades, 25);
                double upperQuartile = calculateQuartile(grades, 75);

                // X-axis position for the group
                double centerX = groupSpacing * (groupIndex + 0.5);

                // Convert grade values to Y-axis positions
                double minY = plotHeight - (min / 10.0) * plotHeight; // Scale grades between 0-10
                double maxY = plotHeight - (max / 10.0) * plotHeight;
                double medianY = plotHeight - (median / 10.0) * plotHeight;
                double lowerY = plotHeight - (lowerQuartile / 10.0) * plotHeight;
                double upperY = plotHeight - (upperQuartile / 10.0) * plotHeight;

                // Draw the box
                Rectangle box = new Rectangle(centerX - groupSpacing / 4, upperY, groupSpacing / 2, lowerY - upperY);
                box.setFill(Color.LIGHTBLUE);
                box.setStroke(Color.BLACK);

                // Draw the median line
                Line medianLine = new Line(centerX - groupSpacing / 4, medianY, centerX + groupSpacing / 4, medianY);
                medianLine.setStroke(Color.BLACK);

                // Draw the whiskers
                Line minWhisker = new Line(centerX, minY, centerX, lowerY);
                minWhisker.setStroke(Color.BLACK);
                Line maxWhisker = new Line(centerX, upperY, centerX, maxY);
                maxWhisker.setStroke(Color.BLACK);

                // Draw whisker caps
                Line minCap = new Line(centerX - groupSpacing / 8, minY, centerX + groupSpacing / 8, minY);
                minCap.setStroke(Color.BLACK);
                Line maxCap = new Line(centerX - groupSpacing / 8, maxY, centerX + groupSpacing / 8, maxY);
                maxCap.setStroke(Color.BLACK);

                // Add numerical values
                Text minText = new Text(centerX + groupSpacing / 6, minY, String.format("%.1f", min));
                Text maxText = new Text(centerX + groupSpacing / 6, maxY, String.format("%.1f", max));
                Text medianText = new Text(centerX + groupSpacing / 6, medianY, String.format("%.1f", median));
                Text lowerQuartileText = new Text(centerX - groupSpacing / 3, lowerY, String.format("%.1f", lowerQuartile));
                Text upperQuartileText = new Text(centerX - groupSpacing / 3, upperY, String.format("%.1f", upperQuartile));

                // Add all elements to the plot pane
                plotPane.getChildren().addAll(box, medianLine, minWhisker, maxWhisker, minCap, maxCap,
                        minText, maxText, medianText, lowerQuartileText, upperQuartileText);

                // Add a label for the group
                Label groupLabel = new Label(group);
                groupLabel.setLayoutX(centerX - groupSpacing / 8);
                groupLabel.setLayoutY(plotHeight + 10);
                plotPane.getChildren().add(groupLabel);

                groupIndex++;
            }
        }

        // Layout adjustments
        VBox layout = new VBox(20.0, titleLabel, plotPane, backButton);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        return new Scene(layout, 1200, 800); // Adjust width and height for better fit
    }

    // Utility method to calculate the median
    private static double calculateMedian(List<Double> grades) {
        List<Double> sorted = grades.stream()
                .filter(g -> g != null)
                .sorted()
                .collect(Collectors.toList());
        int size = sorted.size();
        if (size == 0) return 0.0;
        if (size % 2 == 0) {
            return (sorted.get(size / 2 - 1) + sorted.get(size / 2)) / 2.0;
        } else {
            return sorted.get(size / 2);
        }
    }

    // Utility method to calculate a specific quartile
    private static double calculateQuartile(List<Double> grades, int percentile) {
        List<Double> sorted = grades.stream()
                .filter(g -> g != null)
                .sorted()
                .collect(Collectors.toList());
        if (sorted.isEmpty()) return 0.0;
        double index = (percentile / 100.0) * (sorted.size() - 1);
        int lowerIndex = (int) Math.floor(index);
        int upperIndex = (int) Math.ceil(index);
        if (lowerIndex == upperIndex) {
            return sorted.get(lowerIndex);
        }
        double weight = index - lowerIndex;
        return sorted.get(lowerIndex) * (1 - weight) + sorted.get(upperIndex) * weight;
    }

    private static double calculatePercentile(List<Double> sortedData, double percentile) {
        int index = (int) Math.ceil((percentile / 100.0) * sortedData.size()) - 1;
        return sortedData.get(Math.max(0, Math.min(index, sortedData.size() - 1)));
    }


}