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

public class GUI extends Application {

    private ComboBox<String> secondDropdown = null; // Reference for the second dropdown

    @Override
    public void start(Stage primaryStage) {

        // Title Label
        Label titleLabel = new Label("Data Dashboard");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // Dropdown for "Select Your Data"
        Label selectDataLabel = new Label("Select Your Data::");
        ComboBox<String> selectDataDropdown = new ComboBox<>();
        selectDataDropdown.setPrefWidth(300);
        CourseManager courses = CourseManager.getInstance();
        courses.loadPredictedGrades();
        for (Course course : courses.getCurrentCourses()) {
            selectDataDropdown.getItems().add(course.getName());
        }
        selectDataDropdown.setValue("All Courses");

        // Button to add a second dropdown
        Button addDropdownButton = new Button("Add second course for Scatter Plot");
        addDropdownButton.setStyle("-fx-font-size: 14px; -fx-background-color: #2a9d8f; -fx-text-fill: white; -fx-font-weight: bold;");
        addDropdownButton.setPrefSize(300, 30);


        // VBox to hold the dropdowns
        VBox dropdownContainer = new VBox(10);
        dropdownContainer.setAlignment(Pos.CENTER);
        dropdownContainer.getChildren().add(selectDataDropdown);

        dropdownContainer.getChildren().add(addDropdownButton);

        addDropdownButton.setOnAction(e -> {
            if (secondDropdown == null) { // Add the second dropdown only if it doesn't exist
                secondDropdown = new ComboBox<>();
                secondDropdown.setPrefWidth(300);
                secondDropdown.getItems().add("All Courses");
                for (Course course : courses.getCurrentCourses()) {
                    secondDropdown.getItems().add(course.getName());
                }
                secondDropdown.setValue("All Courses");

                Label scatterLabel = new Label("Choose second course: ");
                scatterLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
                dropdownContainer.getChildren().addAll(scatterLabel, secondDropdown);

                dropdownContainer.getChildren().remove(addDropdownButton);
            }
        });

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

        // Filter Column 1 (Checkboxes)
        VBox filterColumn1 = createCheckboxColumn("NSIL:", new String[]{"Full", "High", "Medium", "Low", "Nothing"});

        // Filter Column 2 (Checkboxes)
        VBox filterColumn2 = createCheckboxColumn("CAR:", new String[]{"3 Tau", "2 Tau", "1 Tau"});

        // Filter Column 3 (Checkboxes)
        VBox filterColumn3 = createCheckboxColumn("TSI:", new String[]{"A", "B", "C", "D", "E", "F"});

        // Filter Column 4 (Checkboxes)
        VBox filterColumn4 = createCheckboxColumn("ARC:", new String[]{"5.0 HZ", "1.0 HZ", "0.5 HZ", "0.1 HZ"});

        // Filter Column 5 (Min-Max Slider)
        VBox filterColumn5 = createMinMaxColumn();

        // Chart Buttons
        Label chartLabel = new Label("Select A Chart:");
        HBox chartButtons = new HBox(15);
        chartButtons.setAlignment(Pos.CENTER);
        chartButtons.setPadding(new Insets(20));
        //Bar Chart
        Button barChartButton = createChartButton("Bar Chart");
        barChartButton.setOnAction(e -> {
            String selectedCourse = selectDataDropdown.getValue();
            String selectedXAxis = selectXAxisDropdown.getValue();
            if (selectedCourse != null && selectedXAxis != null) {
                primaryStage.setScene(GraphManager.createBarChartScene(primaryStage, selectedCourse, selectedXAxis));
            } else {
                showAlert("Please select both a course and an X-Axis.");
            }
        });

        //Box Plot
        Button boxPlotButton = createChartButton("Box Plot");

        //Scatter Plot
        Button scatterPlotButton = createChartButton("Scatter Plot");
        scatterPlotButton.setOnAction(e -> {
            String selectedCourse = selectDataDropdown.getValue();
            String selectedXAxis = selectXAxisDropdown.getValue();
            String selected2ndCourse = secondDropdown.getValue();
            if (selectedCourse != null && selectedXAxis != null && secondDropdown != null) {
                primaryStage.setScene(GraphManager.createScatterPlotScene(primaryStage, selectedCourse, selected2ndCourse, selectedXAxis));
            } else {
                showAlert("Please select both a course and an X-Axis.");
            }
        });

        Button pieChartButton = createChartButton("Pie Chart");
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
                selectDataLabel, dropdownContainer,
                selectXAxisLabel, selectXAxisDropdown,
                titleEntryLabel, titleEntryField,
                filterLabel, filterGrid,
                chartLabel, chartButtons
        );


        // Set Scene and Show
        Scene scene = new Scene(new ScrollPane(layout), 763, 700); // Wrap layout in ScrollPane
        primaryStage.setScene(scene);
        primaryStage.setTitle("Data Dashboard");
        primaryStage.setResizable(true); // Allow resizing if needed
        primaryStage.show();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Input Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Create a column of checkboxes for filtering
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

    // Create a column with min-max controls for filtering
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

    // Create a button for selecting a chart
    private Button createChartButton(String label) {
        Button button = new Button(label);
        button.setStyle("-fx-background-color: #2a9d8f; -fx-text-fill: white; -fx-font-weight: bold;");
        button.setOnAction(e -> {
            // Implement button actions here
            System.out.println(label + " button clicked.");
        });
        return button;
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
        navigateButton.setOnAction(e -> new GUI().start(primaryStage));
        exitButton.setOnAction(e -> System.exit(0));

        // Layout
        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(titleLabel, navigateButton, exitButton);

        return new Scene(layout, 400, 300);
    }

    public static void main(String[] args) {
        launch(args);
    }
}