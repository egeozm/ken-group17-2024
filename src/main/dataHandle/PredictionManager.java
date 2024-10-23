package src.main.dataHandle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PredictionManager {
    private final CurrentStudentManager studentManager;
    private final List<Course> courses;
    private final StudentInfoManager studentInfoManager;

    public PredictionManager(CurrentStudentManager studentManager, List<Course> courses, StudentInfoManager studentInfoManager) {
        this.studentManager = studentManager;
        this.courses = courses;
        this.studentInfoManager = studentInfoManager;
    }

    // Method to compare average grades for a course by a specific property
    // Handle both boundary for numeric and group-based for categorical properties
    public void compareAverageGradeForProperty(String courseName, String property, Object boundaryValue) {
        Course targetCourse = findCourseByName(courseName);
        if (targetCourse == null) {
            System.out.println("Course not found: " + courseName);
        }

        List<Double> group1Grades = new ArrayList<>();
        List<Double> group2Grades = new ArrayList<>();
        Map<Integer, CurrentStudentRecord> studentRecords = studentManager.getAllStudentRecords();

        for (CurrentStudentRecord student : studentRecords.values()) {
            List<Double> grades = student.getCompletedCourseGrades();
            int courseIndex = targetCourse.getColumnIndex();

            if (courseIndex >= 0 && courseIndex < grades.size()) {
                Double grade = grades.get(courseIndex);
                if (grade != null) {
                    // Get the student information from StudentInfoManager
                    StudentInfoRecord infoRecord = studentInfoManager.getStudentByID(student.getStudentID());
                    if (infoRecord != null) {
                        Object propertyValue = getStudentProperty(infoRecord, property); // Object is used because method can return different type of values

                        if (isNumericProperty(property)) {
                            // For numeric properties
                            if (comparePropertyToBoundary(propertyValue, boundaryValue)) {
                                group1Grades.add(grade); // Above boundary or in first group
                            } else {
                                group2Grades.add(grade); // Below boundary or in second group
                            }
                        } else {
                            // For categorical properties
                            if (propertyValue.equals(boundaryValue)) {
                                group1Grades.add(grade); // Matches boundary
                            } else {
                                group2Grades.add(grade); // UnMatches boundary
                            }
                        }
                    }
                }
            }
        }
        calculateAndPrint(group1Grades, "Group 1 (Matches Boundary or Above)");
        calculateAndPrint(group2Grades, "Group 2 (Below Boundary or Not Matching)");
    }

    private void calculateAndPrint(List<Double> grades, String label) {
        double average = calculateAverage(grades);
        double stdDev = calculateStandardDeviation(grades, average);
        System.out.printf("%s | Average Grade: %.2f | Std Dev: %.2f | Number of Students: %d\n", label, average, stdDev, grades.size());
    }

    private boolean isNumericProperty(String property) {
        return property.equals("Plasma Conductivity Quotient") || property.equals("Chrono-Adaptation Rate") ||
                property.equals("Aetheric Resonance Capacity");
    }

    private boolean comparePropertyToBoundary(Object propertyValue, Object boundaryValue) {
        // Check if propertyValue is a string with numeric content, handle accordingly
        if (propertyValue instanceof String && boundaryValue instanceof Number) {
            /*
            This scenario occurs when we have a string that contains a number along with non-numeric characters
            (like "5.0 Hz").
            The method aims to extract the numeric part of the string and then compare it to boundaryValue.
             */
            try {
                // Parse the string to extract a numeric value
                double propNumericValue = Double.parseDouble(propertyValue.toString().replaceAll("[^\\d.]", "")); // Fixed usage you can always search on the internet before use it. Don't try to remember it.
                double boundValue = ((Number) boundaryValue).doubleValue();
                return propNumericValue >= boundValue;
            } catch (NumberFormatException e) {
                System.out.println("Invalid numeric format: " + propertyValue);
                return false;
            }
        }

        // If both are numbers, compare them directly
        if (propertyValue instanceof Number && boundaryValue instanceof Number) {
            return ((Number) propertyValue).doubleValue() >= ((Number) boundaryValue).doubleValue();
        }

        // Default fallback: Return false if types are incompatible
        System.out.println("Error: Incompatible types for comparison: " + propertyValue + " and " + boundaryValue);
        return false;
    }

    private Course findCourseByName(String courseName) {
        for (Course course : courses) {
            if (course.getName().equals(courseName)) {
                return course;
            }
        }
        return null;
    }

    private String getStudentProperty(StudentInfoRecord student, String property) {
        return switch (property) {
            case "Neuro-Synaptic Interface Level" -> student.getNeuroSynapticInterfaceLevel();
            case "Chrono-Adaptation Rate" -> String.valueOf(student.getChronoAdaptationRate());
            case "Plasma Conductivity Quotient" -> String.valueOf(student.getPlasmaConductivityQuotient());
            case "Telepathic Synchronisation Index" -> String.valueOf(student.getTelepathicSynchronisationIndex());
            case "Aetheric Resonance Capacity" -> String.valueOf(student.getAethericResonanceCapacity());
            default -> "Unknown";
        };
    }

    // Helper method to calculate the average of a list of grades
    private double calculateAverage(List<Double> grades) {
        double sum = 0;
        for (Double grade : grades) {
            sum += grade;
        }
        return (!grades.isEmpty()) ? sum / grades.size() : 0.0;
    }

    private double calculateStandardDeviation(List<Double> grades, double mean) {
        double sum = 0;
        int count = 0;
        for (Double grade : grades) {
            if (grade != null) {
                sum += Math.pow(grade - mean, 2);
                count++;
            }
        }
        return (!grades.isEmpty()) ? Math.sqrt(sum / count) : 0;
    }
}

/*
    Usage:
        CurrentStudentManager currentStudentManager = new CurrentStudentManager();
        StudentInfoManager studentInfoManager = new StudentInfoManager();
        CourseManager courseManager = CourseManager.getInstance();
        courseManager.loadCurrentGrades();
        List<Course> courses = courseManager.getCourseRecords();
        PredictionManager predictionManager = new PredictionManager(currentStudentManager, courses, studentInfoManager);
        predictionManager.compareAverageGradeForProperty("Vortex Quantum Mechanics", "Telepathic Synchronisation Index", "B");


 */