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
        Label titleLabel = new Label(title); // Use the passed title
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: #e76f51; -fx-text-fill: white; -fx-font-weight: bold;");
        backButton.setOnAction(e -> primaryStage.setScene(new GUI().createMainScene(primaryStage))); // Return to the main scene

        // X-Axis and Y-Axis
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel(selectedAxis); // Set the label to the selected axis (e.g., "NSIL")

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Average Grade");

        // Creating the Bar Chart
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);

        // Access the data
        CourseManager courses = CourseManager.getInstance();
        courses.loadPredictedGrades(); // Load predicted grades dataset
        Course course = courses.getCourseByName(subject);

        if (course != null) {
            // Use StudentGroupingManager to calculate averages
            StudentGroupingManager groupingManager = new StudentGroupingManager(new StudentInfoManager(), new CurrentStudentManager());
            Map<String, Double> averageGradesByGroup = groupingManager.calculateAverageGradesByAttribute(course, selectedAxis);

            // Add data to series
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(subject + " (" + selectedAxis + ")");

            for (Map.Entry<String, Double> entry : averageGradesByGroup.entrySet()) {
                String group = entry.getKey(); // Group name (e.g., "Full", "High")
                Double averageGrade = entry.getValue(); // Average grade for the group

                // Add data point to the series
                series.getData().add(new XYChart.Data<>(group, averageGrade));
            }

            // Add series to bar chart
            barChart.getData().add(series);
        }

        // Layout
        VBox layout = new VBox(10, titleLabel, barChart, backButton);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        return new Scene(layout, 800, 600);
    }

    public static Scene createScatterPlotScene(Stage primaryStage, String subject, String title, String selectedAxis) {
        // Title and Back Button
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-underline: true;");

        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: #e76f51; -fx-text-fill: white; -fx-font-weight: bold;");
        backButton.setOnAction(e -> primaryStage.setScene(new GUI().createMainScene(primaryStage))); // Return to the main scene

        // Define X-Axis (Groups) and Y-Axis (Grades)
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel(selectedAxis); // E.g., "NSIL"

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Grades");

        // Scatter Chart
        ScatterChart<String, Number> scatterChart = new ScatterChart<>(xAxis, yAxis);
        scatterChart.setTitle(subject + " - Scatter Plot");

        // Access the data
        CourseManager courses = CourseManager.getInstance();
        courses.loadPredictedGrades(); // Load predicted grades dataset
        Course course = courses.getCourseByName(subject);

        if (course != null) {
            StudentGroupingManager groupingManager = new StudentGroupingManager(new StudentInfoManager(), new CurrentStudentManager());
            Map<String, List<Double>> groupedGrades = groupingManager.groupGradesByAttribute(course, selectedAxis);

            // Add each group as a series
            for (Map.Entry<String, List<Double>> entry : groupedGrades.entrySet()) {
                String group = entry.getKey(); // Group name (e.g., "Full", "High")
                List<Double> grades = entry.getValue();

                // Create a series for the group
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName(group); // Use group name for the legend

                // Add grades as points to the series
                for (Double grade : grades) {
                    if (grade != null) {
                        series.getData().add(new XYChart.Data<>(group, grade));
                    }
                }

                scatterChart.getData().add(series); // Add series to chart
            }
        }

        // Layout
        VBox layout = new VBox(10.0, titleLabel, scatterChart, backButton);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        return new Scene(layout, 900, 600); // Adjust width/height if needed
    }

    public static Scene createPieChartScene(Stage primaryStage, String subject, String title, String selectedAxis) {
        // Title and Back Button
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: #e76f51; -fx-text-fill: white; -fx-font-weight: bold;");
        backButton.setOnAction(e -> primaryStage.setScene(new GUI().createMainScene(primaryStage))); // Return to the main scene

        // Creating the PieChart
        PieChart pieChart = new PieChart();
        pieChart.setTitle(subject + " (" + selectedAxis + ")");

        // Access the data
        CourseManager courses = CourseManager.getInstance();
        courses.loadPredictedGrades(); // Load predicted grades dataset
        Course course = courses.getCourseByName(subject);

        if (course != null) {
            // Use StudentGroupingManager to calculate averages
            StudentGroupingManager groupingManager = new StudentGroupingManager(new StudentInfoManager(), new CurrentStudentManager());
            Map<String, Double> averageGradesByGroup = groupingManager.calculateAverageGradesByAttribute(course, selectedAxis);

            // Calculate the total average grades for percentage calculation
            double totalAverageGrade = averageGradesByGroup.values().stream().mapToDouble(Double::doubleValue).sum();

            // Add data to the PieChart
            for (Map.Entry<String, Double> entry : averageGradesByGroup.entrySet()) {
                String group = entry.getKey(); // Group name (e.g., "Full", "High")
                Double averageGrade = entry.getValue(); // Average grade for the group

                // Calculate percentage
                double percentage = (averageGrade / totalAverageGrade) * 100;

                // Create a PieChart.Data object with group name, average grade, and percentage
                PieChart.Data slice = new PieChart.Data(
                        group + " (" + String.format("%.2f", averageGrade) + ", " + String.format("%.1f", percentage) + "%)",
                        averageGrade
                );
                pieChart.getData().add(slice);
            }
        }

        // Layout
        VBox layout = new VBox(10, titleLabel, pieChart, backButton);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        return new Scene(layout, 800, 600);
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
            StudentGroupingManager groupingManager = new StudentGroupingManager(new StudentInfoManager(), new CurrentStudentManager());
            Map<String, List<Double>> groupedGrades = groupingManager.groupGradesByAttribute(course, selectedAxis);

            int groupIndex = 0; // Index to position groups on the X-axis
            double plotWidth = 900; // Total width of the plot
            double groupSpacing = plotWidth / groupedGrades.size();
            double plotHeight = 500; // Height of the plot

            for (Map.Entry<String, List<Double>> entry : groupedGrades.entrySet()) {
                String group = entry.getKey();
                List<Double> grades = entry.getValue();

                // Calculate box plot statistics
                double min = grades.stream().filter(g -> g != null).mapToDouble(Double::doubleValue).min().orElse(0.0);
                double max = grades.stream().filter(g -> g != null).mapToDouble(Double::doubleValue).max().orElse(0.0);
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