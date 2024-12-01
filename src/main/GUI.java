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

import java.util.*;
import java.util.stream.Collectors;

public class GUI extends Application {

    private VBox filterColumn1;
    private VBox filterColumn2;
    private VBox filterColumn3;
    private VBox filterColumn4;
    private VBox filterColumn5;

    @Override
    public void start(Stage primaryStage) {
        // Title Label
        Label titleLabel = new Label("Data Dashboard");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // Dropdown for "Select Your Data"
        Label selectDataLabel = new Label("Select Your Data:");
        ComboBox<String> selectDataDropdown = new ComboBox<>();
        selectDataDropdown.setPrefWidth(300);
        CourseManager courses = CourseManager.getInstance();
        courses.loadPredictedGrades();
        for (Course course : courses.getCurrentCourses()) {
            selectDataDropdown.getItems().add(course.getName());
        }
        selectDataDropdown.setValue("All Courses");

        // Dropdown for "Select X-Axis"
        Label selectXAxisLabel = new Label("Select X-Axis:");
        ComboBox<String> selectXAxisDropdown = new ComboBox<>();
        selectXAxisDropdown.setPrefWidth(200);
        selectXAxisDropdown.getItems().addAll("Grades", "NSIL", "PCQ", "CAR", "TSI", "ARC");
        selectXAxisDropdown.setValue("Grades");

        // Text Field for Chart Title
        Label titleEntryLabel = new Label("Enter your title:");
        TextField titleEntryField = new TextField();
        titleEntryField.setPromptText("Enter chart title");
        titleEntryField.setPrefWidth(400);

        // Filtering Options
        Label filterLabel = new Label("Filter Your Data:");

        filterColumn1 = createCheckboxColumn("NSIL:", new String[]{"Full", "High", "Medium", "Low", "Nothing"});
        filterColumn2 = createCheckboxColumn("CAR:", new String[]{"3 Tau", "2 Tau", "1 Tau"});
        filterColumn3 = createCheckboxColumn("TSI:", new String[]{"A", "B", "C", "D", "E", "F"});
        filterColumn4 = createCheckboxColumn("ARC:", new String[]{"5.0 HZ", "1.0 HZ", "0.5 HZ", "0.1 HZ"});
        filterColumn5 = createMinMaxColumn();

        // Chart Buttons
        Label chartLabel = new Label("Select A Chart:");
        HBox chartButtons = new HBox(15);
        chartButtons.setAlignment(Pos.CENTER);
        chartButtons.setPadding(new Insets(20));

        // Bar Chart Button
        Button barChartButton = createChartButton("Bar Chart");
        barChartButton.setOnAction(e -> {
            String selectedCourse = selectDataDropdown.getValue();
            String selectedXAxis = selectXAxisDropdown.getValue();
            String title = titleEntryField.getText();
            Map<String, List<String>> filters = getSelectedFilters();

            if (selectedCourse == null || selectedCourse.equalsIgnoreCase("All Courses")) {
                showAlert();
            } else {
                primaryStage.setScene(GraphManager.createBarChartScene(primaryStage, selectedCourse, title, selectedXAxis, filters));
            }
        });

        // Box Plot Button
        Button boxPlotButton = createChartButton("Box Plot");
        boxPlotButton.setOnAction(e -> {
            String selectedCourse = selectDataDropdown.getValue();
            String selectedXAxis = selectXAxisDropdown.getValue();
            String title = titleEntryField.getText();
            Map<String, List<String>> filters = getSelectedFilters();

            if (selectedCourse == null || selectedCourse.equalsIgnoreCase("All Courses")) {
                showAlert();
            } else {
                 primaryStage.setScene(GraphManager.createBoxPlotScene(primaryStage, selectedCourse, title, selectedXAxis, filters));
            }
        });

        // Scatter Plot Button
        Button scatterPlotButton = createChartButton("Scatter Plot");
        scatterPlotButton.setOnAction(e -> {
            String selectedCourse = selectDataDropdown.getValue();
            String selectedXAxis = selectXAxisDropdown.getValue();
            String title = titleEntryField.getText();
            Map<String, List<String>> filters = getSelectedFilters();

            if (selectedCourse == null || selectedCourse.equalsIgnoreCase("All Courses")) {
                showAlert();
            } else {
                 primaryStage.setScene(GraphManager.createScatterPlotScene(primaryStage, selectedCourse, title, selectedXAxis, filters));
            }
        });

        // Pie Chart Button
        Button pieChartButton = createChartButton("Pie Chart");
        pieChartButton.setOnAction(e -> {
            String selectedCourse = selectDataDropdown.getValue();
            String selectedXAxis = selectXAxisDropdown.getValue();
            String title = titleEntryField.getText();
            Map<String, List<String>> filters = getSelectedFilters();

            if (selectedCourse == null || selectedCourse.equalsIgnoreCase("All Courses")) {
                showAlert();
            } else {
                primaryStage.setScene(GraphManager.createPieChartScene(primaryStage, selectedCourse, title, selectedXAxis, filters));
            }
        });

        chartButtons.getChildren().addAll(barChartButton, boxPlotButton, scatterPlotButton, pieChartButton);

        // Main Layout
        VBox layout = new VBox(30);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));
        layout.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));

        GridPane filterGrid = new GridPane();
        filterGrid.setHgap(25);
        filterGrid.setVgap(25);
        filterGrid.add(filterColumn1, 0, 0);
        filterGrid.add(filterColumn2, 1, 0);
        filterGrid.add(filterColumn3, 2, 0);
        filterGrid.add(filterColumn4, 3, 0);
        filterGrid.add(filterColumn5, 4, 0);

        layout.getChildren().addAll(
                titleLabel,
                selectDataLabel, selectDataDropdown,
                selectXAxisLabel, selectXAxisDropdown,
                titleEntryLabel, titleEntryField,
                filterLabel, filterGrid,
                chartLabel, chartButtons
        );

        Scene scene = new Scene(new ScrollPane(layout), 785, 835);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Data Dashboard");
        primaryStage.show();
    }

    private VBox createCheckboxColumn(String label, String[] options) {
        Label columnLabel = new Label(label);
        VBox column = new VBox(5);
        column.getChildren().add(columnLabel);
        for (String option : options) {
            CheckBox checkBox = new CheckBox(option);
            column.getChildren().add(checkBox);
        }
        return column;
    }

    private VBox createMinMaxColumn() {
        Label columnLabel = new Label("PCQ:");
        HBox minMaxControls = new HBox(5);
        Label minLabel = new Label("Min:");
        Spinner<Integer> minSpinner = new Spinner<>(-42, 134, -42);
        Label maxLabel = new Label("Max:");
        Spinner<Integer> maxSpinner = new Spinner<>(-42, 134, 134);
        minMaxControls.getChildren().addAll(minLabel, minSpinner, maxLabel, maxSpinner);
        return new VBox(5, columnLabel, minMaxControls);
    }

    private Button createChartButton(String label) {
        Button button = new Button(label);
        button.setStyle("-fx-background-color: #2a9d8f; -fx-text-fill: white; -fx-font-weight: bold;");
        return button;
    }

    private void showAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Input Error");
        alert.setHeaderText(null);
        alert.setContentText("Please select a course");
        alert.showAndWait();
    }

    private Map<String, List<String>> getSelectedFilters() {
        Map<String, List<String>> filters = new HashMap<>();

        // NSIL
        List<String> nsilFilters = filterColumn1.getChildren().stream()
                .filter(node -> node instanceof CheckBox && ((CheckBox) node).isSelected())
                .map(node -> ((CheckBox) node).getText())
                .collect(Collectors.toList());
        if (!nsilFilters.isEmpty()) {
            filters.put("NSIL", nsilFilters);
        }

        // CAR
        List<String> carFilters = filterColumn2.getChildren().stream()
                .filter(node -> node instanceof CheckBox && ((CheckBox) node).isSelected())
                .map(node -> ((CheckBox) node).getText())
                .collect(Collectors.toList());
        if (!carFilters.isEmpty()) {
            filters.put("CAR", carFilters);
        }

        // ARC
        List<String> arcFilters = filterColumn4.getChildren().stream()
                .filter(node -> node instanceof CheckBox && ((CheckBox) node).isSelected())
                .map(node -> ((CheckBox) node).getText())
                .collect(Collectors.toList());
        if (!arcFilters.isEmpty()) {
            filters.put("ARC", arcFilters);
        }

        // PCQ
        Spinner<Integer> minSpinner = (Spinner<Integer>) ((HBox) filterColumn5.getChildren().get(1)).getChildren().get(1);
        Spinner<Integer> maxSpinner = (Spinner<Integer>) ((HBox) filterColumn5.getChildren().get(1)).getChildren().get(3);
        filters.put("PCQ", Arrays.asList(String.valueOf(minSpinner.getValue()), String.valueOf(maxSpinner.getValue())));

        // TSI
        List<String> tsiFilters = filterColumn3.getChildren().stream()
                .filter(node -> node instanceof CheckBox && ((CheckBox) node).isSelected())
                .map(node -> ((CheckBox) node).getText())
                .collect(Collectors.toList());
        if (!tsiFilters.isEmpty()) {
            filters.put("TSI", tsiFilters);
        }


        return filters;
    }


    // Create the Main Scene
    public static Scene createMainScene(Stage primaryStage) {
        // Title Label
        Label titleLabel = new Label("Data Dashboard");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2a9d8f;");

        // Navigation Button
        Button navigateButton = new Button("Go to Dashboard");
        Button exitButton = new Button("Exit");
        navigateButton.setStyle("-fx-background-color: #2a9d8f; -fx-text-fill: white; -fx-font-weight: bold;");
        navigateButton.setOnAction(e -> new GUI().start(primaryStage)); // Navigate to the main dashboard
        exitButton.setOnAction(e -> System.exit(0)); // Exit the application

        // Layout
        VBox layout = new VBox(10.0);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(titleLabel, navigateButton, exitButton);

        return new Scene(layout, 900, 600);
    }

    public static void main(String[] args) {
        launch(args);
    }
}