package src.main;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import src.main.dataHandle.*;

import java.util.*;
import java.util.stream.Collectors;

public class GraphManager {

    public static Scene createBarChartScene(Stage primaryStage, String subject, String title, String selectedAxis, Map<String, List<String>> filters) {
        // Title Label
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-underline: true;");

        // Back Button
        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: #e76f51; -fx-text-fill: white; -fx-font-weight: bold;");
        backButton.setOnAction(e -> primaryStage.setScene(new GUI().createMainScene(primaryStage)));

        // X-Axis and Y-Axis
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel(selectedAxis.equals("Grades") ? "Grades" : selectedAxis);
        yAxis.setLabel(selectedAxis.equals("Grades") ? "Frequency" : "Grade Value");

        // Bar Chart
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);

        // Access Data
        CourseManager courses = CourseManager.getInstance();
        Course course = courses.getCourseByName(subject);

        if (course != null) {
            // Group and Filter Data
            StudentGroupingManager groupingManager = new StudentGroupingManager(new StudentInfoManager(), new PredictedCurrentStudentManager());
            Map<String, Map<String, List<Double>>> groupedData = groupingManager.groupAndFilterData(course, selectedAxis, filters);

            // Predefined Order
            List<String> predefinedOrder = getPredefinedOrder(selectedAxis);

            if (selectedAxis.equals("Grades")) {
                // Handle Grades axis separately
                for (Map.Entry<String, Map<String, List<Double>>> groupEntry : groupedData.entrySet()) {
                    Map<String, Long> gradeFrequencies = groupEntry.getValue().get("Grades").stream()
                            .map(String::valueOf)
                            .collect(Collectors.groupingBy(g -> g, Collectors.counting()));

                    // Sort grade labels numerically
                    gradeFrequencies = gradeFrequencies.entrySet().stream()
                            .sorted(Comparator.comparingDouble(e -> Double.parseDouble(e.getKey())))
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey,
                                    Map.Entry::getValue,
                                    (e1, e2) -> e1,
                                    LinkedHashMap::new));

                    XYChart.Series<String, Number> series = new XYChart.Series<>();
                    series.setName("Grades");

                    for (Map.Entry<String, Long> entry : gradeFrequencies.entrySet()) {
                        series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
                    }

                    barChart.getData().add(series);
                }
            } else {
                // Handle non-Grades axes
                for (String category : predefinedOrder) {
                    Map.Entry<String, Map<String, List<Double>>> matchingGroup = groupedData.entrySet().stream()
                            .filter(entry -> normalizeKey(entry.getKey(), getUnit(selectedAxis)).equals(normalizeKey(category, getUnit(selectedAxis))))
                            .findFirst()
                            .orElse(null);

                    if (matchingGroup != null) {
                        Map<String, List<Double>> data = matchingGroup.getValue();

                        XYChart.Series<String, Number> series = new XYChart.Series<>();
                        series.setName(category);

                        // Calculate average grades
                        double averageGrade = data.get("Grades").stream()
                                .mapToDouble(Double::doubleValue)
                                .average()
                                .orElse(0.0);
                        series.getData().add(new XYChart.Data<>(category, averageGrade));
                        barChart.getData().add(series);
                    }
                }
            }
        }

        // Layout
        VBox layout = new VBox(10.0);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(titleLabel, barChart, backButton);

        return new Scene(layout, 900, 600);
    }

    public static Scene createScatterPlotScene(Stage primaryStage, String subject, String title, String selectedAxis, Map<String, List<String>> filters) {
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-underline: true;");

        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: #e76f51; -fx-text-fill: white; -fx-font-weight: bold;");
        backButton.setOnAction(e -> primaryStage.setScene(new GUI().createMainScene(primaryStage)));

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel(selectedAxis.equals("Grades") ? "Grades" : selectedAxis);
        yAxis.setLabel(selectedAxis.equals("Grades") ? "Frequency" : "Grade Value");

        ScatterChart<String, Number> scatterChart = new ScatterChart<>(xAxis, yAxis);
        scatterChart.setTitle(subject + " - Scatter Plot");

        CourseManager courses = CourseManager.getInstance();
        Course course = courses.getCourseByName(subject);

        if (course != null) {
            StudentGroupingManager groupingManager = new StudentGroupingManager(new StudentInfoManager(), new PredictedCurrentStudentManager());
            Map<String, Map<String, List<Double>>> groupedData = groupingManager.groupAndFilterData(course, selectedAxis, filters);

            for (Map.Entry<String, Map<String, List<Double>>> groupEntry : groupedData.entrySet()) {
                String xLabel = groupEntry.getKey();
                List<Double> grades = groupEntry.getValue().get("Grades");

                if (grades != null) {
                    XYChart.Series<String, Number> series = new XYChart.Series<>();
                    series.setName(xLabel);

                    for (Double grade : grades) {
                        if (grade != null) {
                            series.getData().add(new XYChart.Data<>(xLabel, grade));
                        }
                    }
                    scatterChart.getData().add(series);
                }
            }
        }

        VBox layout = new VBox(10.0);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(titleLabel, scatterChart, backButton);
        return new Scene(layout, 900, 600);
    }

    public static Scene createPieChartScene(Stage primaryStage, String subject, String title, String selectedAxis, Map<String, List<String>> filters) {
        // Title Label
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-underline: true;");

        // Back Button
        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: #e76f51; -fx-text-fill: white; -fx-font-weight: bold;");
        backButton.setOnAction(e -> primaryStage.setScene(new GUI().createMainScene(primaryStage)));

        // Pie Chart
        PieChart pieChart = new PieChart();
        pieChart.setTitle(subject + " - Pie Chart");

        // Access Data
        CourseManager courses = CourseManager.getInstance();
        Course course = courses.getCourseByName(subject);

        if (course != null) {
            // Group and Filter Data
            StudentGroupingManager groupingManager = new StudentGroupingManager(new StudentInfoManager(), new PredictedCurrentStudentManager());
            Map<String, Map<String, List<Double>>> groupedData = groupingManager.groupAndFilterData(course, selectedAxis, filters);

            // Handle Grades Differently
            if (selectedAxis.equalsIgnoreCase("Grades")) {
                // Flatten all grades into a single list
                List<Double> allGrades = groupedData.values().stream()
                        .flatMap(data -> data.getOrDefault("Grades", Collections.emptyList()).stream())
                        .collect(Collectors.toList());

                // Count frequencies for each grade
                Map<String, Long> gradeFrequencies = allGrades.stream()
                        .map(String::valueOf)
                        .collect(Collectors.groupingBy(g -> g, Collectors.counting()));

                // Sort grade labels numerically
                Map<String, Long> sortedGradeFrequencies = gradeFrequencies.entrySet().stream()
                        .sorted(Comparator.comparingDouble(e -> Double.parseDouble(e.getKey())))
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (e1, e2) -> e1, // Merge function (not needed here)
                                LinkedHashMap::new // Maintain insertion order
                        ));

                // Calculate total grades count
                long totalGrades = sortedGradeFrequencies.values().stream().mapToLong(Long::longValue).sum();

                // Add slices for each grade
                for (Map.Entry<String, Long> entry : sortedGradeFrequencies.entrySet()) {
                    double percentage = (totalGrades > 0) ? (entry.getValue() / (double) totalGrades) * 100 : 0;
                    pieChart.getData().add(new PieChart.Data(
                            entry.getKey() + " (" + String.format("%.1f%%", percentage) + ")", entry.getValue()));
                }
            } else {
                // Predefined Order
                List<String> predefinedOrder = getPredefinedOrder(selectedAxis);

                // Calculate total count for percentages
                int totalCount = groupedData.values().stream()
                        .mapToInt(data -> data.getOrDefault("Grades", Collections.emptyList()).size())
                        .sum();

                // Iterate over predefined order to ensure consistent pie slice order
                for (String category : predefinedOrder) {
                    // Match Group by Normalized Key
                    Map.Entry<String, Map<String, List<Double>>> matchingGroup = groupedData.entrySet().stream()
                            .filter(entry -> normalizeKey(entry.getKey(), getUnit(selectedAxis))
                                    .equals(normalizeKey(category, getUnit(selectedAxis))))
                            .findFirst()
                            .orElse(null);

                    if (matchingGroup != null) {
                        List<Double> grades = matchingGroup.getValue().get("Grades");

                        if (grades != null && !grades.isEmpty()) {
                            int groupSize = grades.size();
                            double percentage = (totalCount > 0) ? (groupSize / (double) totalCount) * 100 : 0;

                            // Add a slice to the pie chart
                            pieChart.getData().add(new PieChart.Data(
                                    category + " (" + String.format("%.1f%%", percentage) + ")", groupSize));
                        }
                    } else {
                        // Add an empty slice for categories without data
                        pieChart.getData().add(new PieChart.Data(category + " (0.0%)", 0));
                    }
                }
            }
        }

        // Layout
        VBox layout = new VBox(10.0);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(titleLabel, pieChart, backButton);

        return new Scene(layout, 900, 600);
    }

    public static Scene createBoxPlotScene(Stage primaryStage, String subject, String title, String selectedAxis, Map<String, List<String>> filters) {
        // Title Label
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-underline: true;");

        // Back Button
        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: #e76f51; -fx-text-fill: white; -fx-font-weight: bold;");
        backButton.setOnAction(e -> primaryStage.setScene(new GUI().createMainScene(primaryStage)));

        // Box Plot Pane
        Pane plotPane = new Pane();
        plotPane.setPrefSize(1000, 600);

        // Access data
        CourseManager courses = CourseManager.getInstance();
        Course course = courses.getCourseByName(subject);

        if (course != null) {
            // Group and Filter Data
            StudentGroupingManager groupingManager = new StudentGroupingManager(new StudentInfoManager(), new PredictedCurrentStudentManager());
            Map<String, Map<String, List<Double>>> groupedData = groupingManager.groupAndFilterData(course, selectedAxis, filters);

            // Predefined order for categories
            List<String> predefinedOrder = getPredefinedOrder(selectedAxis);

            // Handle Grades Differently
            if (selectedAxis.equalsIgnoreCase("Grades")) {
                // Flatten all grades into a single list
                List<Double> allGrades = groupedData.values().stream()
                        .flatMap(data -> data.getOrDefault("Grades", Collections.emptyList()).stream())
                        .collect(Collectors.toList());

                // If grades exist, calculate and display one unified box plot
                if (!allGrades.isEmpty()) {
                    double min = allGrades.stream().mapToDouble(Double::doubleValue).min().orElse(0.0);
                    double max = allGrades.stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
                    double median = calculateMedian(allGrades);
                    double lowerQuartile = calculateQuartile(allGrades, 25);
                    double upperQuartile = calculateQuartile(allGrades, 75);

                    // Center the single box plot
                    double centerX = plotPane.getPrefWidth() / 2;
                    double plotHeight = plotPane.getPrefHeight();

                    // Convert grade values to Y-axis positions
                    double minY = plotHeight - (min / 10.0) * plotHeight;
                    double maxY = plotHeight - (max / 10.0) * plotHeight;
                    double medianY = plotHeight - (median / 10.0) * plotHeight;
                    double lowerY = plotHeight - (lowerQuartile / 10.0) * plotHeight;
                    double upperY = plotHeight - (upperQuartile / 10.0) * plotHeight;

                    // Draw the box
                    Rectangle box = new Rectangle(centerX - 50, upperY, 100, lowerY - upperY);
                    box.setFill(Color.LIGHTBLUE);
                    box.setStroke(Color.BLACK);

                    // Draw the median line
                    Line medianLine = new Line(centerX - 50, medianY, centerX + 50, medianY);
                    medianLine.setStroke(Color.BLACK);

                    // Draw whiskers
                    Line minWhisker = new Line(centerX, minY, centerX, lowerY);
                    minWhisker.setStroke(Color.BLACK);
                    Line maxWhisker = new Line(centerX, upperY, centerX, maxY);
                    maxWhisker.setStroke(Color.BLACK);

                    // Add whisker caps
                    Line minCap = new Line(centerX - 25, minY, centerX + 25, minY);
                    Line maxCap = new Line(centerX - 25, maxY, centerX + 25, maxY);

                    // Add numerical annotations
                    Text minText = new Text(centerX + 55, minY, String.format("%.1f", min));
                    Text maxText = new Text(centerX + 55, maxY, String.format("%.1f", max));
                    Text medianText = new Text(centerX + 55, medianY, String.format("%.1f", median));
                    Text lowerQuartileText = new Text(centerX - 120, lowerY, String.format("%.1f", lowerQuartile));
                    Text upperQuartileText = new Text(centerX - 120, upperY, String.format("%.1f", upperQuartile));

                    // Style the text
                    minText.setStyle("-fx-font-size: 12px;");
                    maxText.setStyle("-fx-font-size: 12px;");
                    medianText.setStyle("-fx-font-size: 12px;");
                    lowerQuartileText.setStyle("-fx-font-size: 12px;");
                    upperQuartileText.setStyle("-fx-font-size: 12px;");

                    // Add elements to the pane
                    plotPane.getChildren().addAll(box, medianLine, minWhisker, maxWhisker, minCap, maxCap, minText, maxText, medianText, lowerQuartileText, upperQuartileText);
                }
            } else {
                // Iterate over predefined order for other filters
                int groupIndex = 0; // Used for X-axis positioning
                double plotWidth = 900; // Total width of the plot
                double groupSpacing = plotWidth / predefinedOrder.size();
                double plotHeight = 500;

                for (String category : predefinedOrder) {
                    // Match Group by Normalized Key
                    Map.Entry<String, Map<String, List<Double>>> matchingGroup = groupedData.entrySet().stream()
                            .filter(entry -> normalizeKey(entry.getKey(), getUnit(selectedAxis))
                                    .equals(normalizeKey(category, getUnit(selectedAxis))))
                            .findFirst()
                            .orElse(null);

                    if (matchingGroup != null) {
                        List<Double> grades = matchingGroup.getValue().get("Grades");

                        if (grades != null && !grades.isEmpty()) {
                            // Calculate box plot statistics
                            double min = grades.stream().mapToDouble(Double::doubleValue).min().orElse(0.0);
                            double max = grades.stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
                            double median = calculateMedian(grades);
                            double lowerQuartile = calculateQuartile(grades, 25);
                            double upperQuartile = calculateQuartile(grades, 75);

                            // X-axis position for the group
                            double centerX = groupSpacing * (groupIndex + 0.5);

                            // Convert grade values to Y-axis positions
                            double minY = plotHeight - (min / 10.0) * plotHeight;
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

                            // Draw whiskers
                            Line minWhisker = new Line(centerX, minY, centerX, lowerY);
                            minWhisker.setStroke(Color.BLACK);
                            Line maxWhisker = new Line(centerX, upperY, centerX, maxY);
                            maxWhisker.setStroke(Color.BLACK);

                            // Add whisker caps
                            Line minCap = new Line(centerX - groupSpacing / 8, minY, centerX + groupSpacing / 8, minY);
                            Line maxCap = new Line(centerX - groupSpacing / 8, maxY, centerX + groupSpacing / 8, maxY);

                            // Add numerical annotations
                            Text minText = new Text(centerX + groupSpacing / 4 + 5, minY, String.format("%.1f", min));
                            Text maxText = new Text(centerX + groupSpacing / 4 + 5, maxY, String.format("%.1f", max));
                            Text medianText = new Text(centerX + groupSpacing / 4 + 5, medianY, String.format("%.1f", median));
                            Text lowerQuartileText = new Text(centerX - groupSpacing / 2, lowerY, String.format("%.1f", lowerQuartile));
                            Text upperQuartileText = new Text(centerX - groupSpacing / 2, upperY, String.format("%.1f", upperQuartile));

                            // Style the text
                            minText.setStyle("-fx-font-size: 12px;");
                            maxText.setStyle("-fx-font-size: 12px;");
                            medianText.setStyle("-fx-font-size: 12px;");
                            lowerQuartileText.setStyle("-fx-font-size: 12px;");
                            upperQuartileText.setStyle("-fx-font-size: 12px;");

                            // Add elements to the pane
                            plotPane.getChildren().addAll(box, medianLine, minWhisker, maxWhisker, minCap, maxCap, minText, maxText, medianText, lowerQuartileText, upperQuartileText);

                            // Add a label for the group
                            Label groupLabelNode = new Label(category);
                            groupLabelNode.setLayoutX(centerX - groupSpacing / 4);
                            groupLabelNode.setLayoutY(plotHeight + 20);
                            plotPane.getChildren().add(groupLabelNode);
                        }
                        groupIndex++;
                    }
                }
            }
        }

        VBox layout = new VBox(10.0, titleLabel, plotPane, backButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        return new Scene(layout, 1200, 800); // Adjust width and height for better fit
    }

    // Utility method to calculate the median
    private static double calculateMedian(List<Double> grades) {
        List<Double> sorted = grades.stream().sorted().collect(Collectors.toList());
        int size = sorted.size();
        if (size == 0) return 0.0;
        return (size % 2 == 0) ? (sorted.get(size / 2 - 1) + sorted.get(size / 2)) / 2.0 : sorted.get(size / 2);
    }

    // Utility method to calculate a specific quartile
    private static double calculateQuartile(List<Double> grades, int percentile) {
        List<Double> sorted = grades.stream().sorted().collect(Collectors.toList());
        if (sorted.isEmpty()) return 0.0;
        double index = (percentile / 100.0) * (sorted.size() - 1);
        int lowerIndex = (int) Math.floor(index);
        int upperIndex = (int) Math.ceil(index);
        if (lowerIndex == upperIndex) return sorted.get(lowerIndex);
        double weight = index - lowerIndex;
        return sorted.get(lowerIndex) * (1 - weight) + sorted.get(upperIndex) * weight;
    }

    private static List<String> getPredefinedOrder(String selectedAxis) {
        switch (selectedAxis.toLowerCase().trim()) {
            case "nsil":
                return Arrays.asList("nothing", "low", "medium", "high", "full");
            case "car":
                return Arrays.asList("1 tau", "2 tau", "3 tau").stream().map(value -> normalizeKey(value, "tau")).collect(Collectors.toList());
            case "tsi":
                return Arrays.asList("A", "B", "C", "D", "E", "F");
            case "arc":
                return Arrays.asList("0.1 hz", "0.5 hz", "1.0 hz", "5.0 hz").stream().map(value -> normalizeKey(value, "hz")).collect(Collectors.toList());
            default:
                return Collections.emptyList();
        }
    }

    // Helper method to normalize keys for predefined values
    private static String normalizeKey(String key, String unit) {
        if (key == null || unit == null) return "";
        return key.trim().toLowerCase().replace(" " + unit.toLowerCase(), "").trim();
    }

    private static String getUnit(String selectedAxis) {
        switch (selectedAxis.toLowerCase().trim()) {
            case "car":
                return "tau";
            case "arc":
                return "hz";
            default:
                return ""; // No unit for other axes
        }
    }

}