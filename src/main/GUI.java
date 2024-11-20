package src.main;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import src.main.dataHandle.Course;
import src.main.dataHandle.CourseManager;

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
        for (Course x : Courses.getCurrentCourses()){subjectDropdown1.getItems().add(x.getName());}
        subjectDropdown1.setVisible(true);
        subjectDropdown1.setValue("*Pick a Course*");

        // Dropdown for subject 2
        Label subjectLabel2 = new Label("Select Subject:");
        ComboBox<String> subjectDropdown2 = new ComboBox<>();
        subjectDropdown2.setVisible(false);
        subjectLabel2.setVisible(false);
        subjectDropdown2.setValue("*Pick a Course*");


        // This Hides the second subject dropdown until "Scatter Plot " is selected, so it can be used for comparison
        graphDropdown.valueProperty().addListener((observable, oldValue, newValue) -> {
            if ("Scatter Plot".equals(newValue)) {
                // Show the course dropdown and add back all the courses
                subjectDropdown2.setVisible(true);
                subjectLabel2.setVisible(true);
                subjectDropdown2.getItems().clear();  // Clear previous state
                for (Course x : Courses.getCurrentCourses()){subjectDropdown2.getItems().add(x.getName());}
                for (Course x : Courses.getCurrentCourses()){subjectDropdown2.getItems().add(x.getName());}// Example courses
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
                        //primaryStage.setScene(createScatterPlotScene(primaryStage, subject1, subject2)); ---- put as comment so no errors whilst testing
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
        Parent scatterLayout = null;
        return new Scene(scatterLayout, 800, 600);
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
