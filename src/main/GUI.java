package src.main;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import src.main.dataHandle.Course;
import src.main.dataHandle.CourseManager;

import java.util.List;
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
        CourseManager Courses = CourseManager.getInstance();
        Courses.loadCurrentGrades();
        Courses.loadGraduateGrades();
        for (Course x : Courses.getCurrentCourses()){subjectDropdown1.getItems().add(x.getName());}// add all courses in dropdown

        subjectDropdown1.setVisible(true);
        subjectDropdown1.setValue("*Pick a Course*");

        // Dropdown for subject 2
        Label subjectLabel2 = new Label("Select Subject:");
        ComboBox<String> subjectDropdown2 = new ComboBox<>();
        subjectDropdown2.setVisible(false);
        subjectLabel2.setVisible(false);
        subjectDropdown2.setValue("*Pick a Course*");

        // Dropdown for "Bar chart selection"
        Label filter1 = new Label("Select Graph Content");
        ComboBox<String> filterDropdown = new ComboBox<>();
        filterDropdown.getItems().addAll("Pass/Fail");
        filterDropdown.setValue("Pick a category");
        filterDropdown.setVisible(false);



        // This Hides the second subject dropdown until "Scatter Plot " is selected, so it can be used for comparison
        graphDropdown.valueProperty().addListener((observable, oldValue, newValue) -> {
            if ("Scatter Plot".equals(newValue)) {
                // Show the course dropdown and add back all the courses
                subjectDropdown2.setVisible(true);
                subjectLabel2.setVisible(true);
                subjectDropdown2.getItems().clear();  // Clear previous state
                for (Course x : Courses.getCurrentCourses()){subjectDropdown2.getItems().add(x.getName());}
            if ("Bar Chart".equals(newValue)){
                filter1.setVisible(true);
            }
            } else {
                // Hide the course dropdown if "Scatter Plot" is not selected
                subjectDropdown2.setVisible(false);
                subjectLabel2.setVisible(false);
            }
        });

        // Submit Button
        Button submitButton = new Button("Submit");
        submitButton.setStyle("-fx-background-color: #e76f51; -fx-text-fill: white; -fx-font-weight: bold;");
        submitButton.setOnAction( e-> {
            //Values submitted
            String selectedGraph = graphDropdown.getValue();

            //Sending to different Scenes

            if(selectedGraph.equals("Scatter Plot")){
                if ("Scatter Plot".equals(graphDropdown.getValue())) {
                    // Check for valid subjects and if true then send to scatter graph
                    String subject1 = subjectDropdown1.getValue();
                    String subject2 = subjectDropdown2.getValue();
                    if (Objects.equals(subject1, "*Pick a Course*") || Objects.equals(subject2, "*Pick a Course*")) {

                        showAlert("Please select both subjects.");
                    } else {
                        primaryStage.setScene(createScatterPlotScene(primaryStage, subject1, subject2)); //---- put as comment so no errors whilst testing
                    }
                }
            };
            if(selectedGraph.equals("Histograms")){
                if ("Histograms".equals(graphDropdown.getValue())) {
                    // Check for valid subjects and if true then send to scatter graph
                    String subject1 = subjectDropdown1.getValue();
                    if (!Objects.equals(subject1, "*Pick a Course*")) {
                        //primaryStage.setScene(createHistogramScene(primaryStage, subject1)); ----  put as comment so no errors whilst testing
                    } else {
                        showAlert("Please select a subjects.");
                    }
                }


            }
            if(selectedGraph.equals("Bar Chart")){
                if ("Bar Chart".equals(graphDropdown.getValue())) {
                    // Check for valid subjects and if true then send to scatter graph
                    String subject1 = subjectDropdown1.getValue();
                    if (!Objects.equals(subject1, "*Pick a Course*")) {
                        //primaryStage.setScene(createBarChartScene(primaryStage, subject1)); ---- // put as comment so no errors whilst testing
                    } else {
                        showAlert("Please select a subjects.");
                    }
                }


            }
        });

        // Layout
        VBox layout = new VBox(15);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        // Add components to layout
        layout.getChildren().addAll(
                titleLabel,
                graphTypeLabel, graphDropdown,
                subjectLabel1, subjectDropdown1,
                subjectLabel2,subjectDropdown2,


                submitButton
        );

        // Set the scene
        Scene scene = new Scene(layout, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Chart Displayer");
        primaryStage.show();
    }
    //This function lets you return back to main scene
    private Scene createMainScene(Stage primaryStage) {
        start(primaryStage);
        return primaryStage.getScene();
    }

//Implementation of the  Bar Chart below
    private Scene createBarChartScene(Stage primaryStage, String subject1) {

        Parent scatterLayout = null;
        return new Scene(scatterLayout, 800, 600);
    }
//Implementation of the Histogram below
    private Scene createHistogramScene(Stage primaryStage, String subject1) {
        Parent scatterLayout = null;
        return new Scene(scatterLayout, 800, 600);
    }
//Implementation of the scatter plot diagram below
private Scene createScatterPlotScene(Stage primaryStage, String subject1, String subject2) {
    // Title and Back Button
    Label titleLabel = new Label("Scatter Plot: " + subject1 + " vs " + subject2);
    titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

    Button backButton = new Button("Back");
    backButton.setStyle("-fx-background-color: #e76f51; -fx-text-fill: white; -fx-font-weight: bold;");
    backButton.setOnAction(e -> start(primaryStage)); // Return to the main scene

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
        } else {
            showAlert("No data available for the selected subjects.");
        }
    } else {
        showAlert("Unable to retrieve data for the selected courses.");
    }

    // Add data to the scatter chart
    scatterChart.getData().addAll(series1, series2);

    // Layout
    VBox layout = new VBox(10, titleLabel, scatterChart, backButton);
    layout.setPadding(new Insets(20));
    layout.setAlignment(Pos.CENTER);

    return new Scene(layout, 800, 600);
}








    //Implementation of the Alert used for some illegal arguments
    private void showAlert(String s) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Error");
        alert.setHeaderText("Error");
        alert.setContentText(s);
        alert.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
