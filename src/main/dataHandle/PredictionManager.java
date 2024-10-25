package src.main.dataHandle;

import java.util.*;

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
            List<Double> grades = student.getCourseGrades();
            int courseIndex = targetCourse.getColumnIndex();
            System.out.printf("Student ID: %d | Course: %s | Course Index: %d | Grades Size: %d\n",
                    student.getStudentID(), targetCourse.getName(), courseIndex, grades.size());
            if (courseIndex >= 0 && courseIndex < grades.size()) {
                Double grade = grades.get(courseIndex);
                if (grade == null) {
                    System.out.printf("Student ID: %d | No grade found for course: %s\n", student.getStudentID(), courseName);
                    continue; // Skip students with null grades
                }
                // Get the student information from StudentInfoManager
                StudentInfoRecord infoRecord = studentInfoManager.getStudentByID(student.getStudentID());
                if (infoRecord != null) {
                    Object propertyValue = getStudentProperty(infoRecord, property); // Object is used because method can return different type of values
                    System.out.printf("Student ID: %d | Property Value: %s\n", student.getStudentID(), propertyValue);
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
        calculateAndPrint(group1Grades, "\nGroup 1 (Matches Boundary or Above)");
        calculateAndPrint(group2Grades, "Group 2 (Below Boundary or Not Matching)");
        System.out.printf("Comparison between Group 1 and Group 2 : \n");
        double mean1 = calculateAverage(group1Grades);
        double mean2 = calculateAverage(group2Grades);
        System.out.printf(" - Average difference: %.2f\n", Math.abs(mean1 - mean2));
        System.out.printf(" - Std Dev difference: %.2f\n", Math.abs(calculateStandardDeviation(group1Grades, mean1) - calculateStandardDeviation(group2Grades, mean2)));

    }

    public void compareAverageGradeUsingOtherCourses(String mainCourseName, String comparisonCourseName, String property, Object boundaryValue) {
        Course mainCourse = findCourseByName(mainCourseName);
        Course comparisonCourse = findCourseByName(comparisonCourseName);

        if (mainCourse == null || comparisonCourse == null) {
            System.out.println("One or both courses not found: " + mainCourseName + ", " + comparisonCourseName);
            return;
        }

        List<Double> group1Grades = new ArrayList<>();
        List<Double> group2Grades = new ArrayList<>();
        Map<Integer, CurrentStudentRecord> studentRecords = studentManager.getAllStudentRecords();

        for (CurrentStudentRecord student : studentRecords.values()) {
            List<Double> grades = student.getCourseGrades();
            int mainCourseIndex = mainCourse.getColumnIndex();
            int comparisonCourseIndex = comparisonCourse.getColumnIndex();

            if (mainCourseIndex >= 0 && mainCourseIndex < grades.size() && comparisonCourseIndex >= 0 && comparisonCourseIndex < grades.size()) {
                Double mainCourseGrade = grades.get(mainCourseIndex);
                Double comparisonCourseGrade = grades.get(comparisonCourseIndex);

                if (mainCourseGrade != null && comparisonCourseGrade != null) {
                    StudentInfoRecord infoRecord = studentInfoManager.getStudentByID(student.getStudentID());
                    if (infoRecord != null) {
                        Object propertyValue = getStudentProperty(infoRecord, property);

                        if (isNumericProperty(property)) {
                            if (comparePropertyToBoundary(propertyValue, boundaryValue)) {
                                group1Grades.add(mainCourseGrade); // Matches boundary
                            } else {
                                group2Grades.add(mainCourseGrade); // Doesn't match boundary
                            }
                        } else {
                            if (propertyValue.equals(boundaryValue)) {
                                group1Grades.add(mainCourseGrade); // Matches boundary
                            } else {
                                group2Grades.add(mainCourseGrade); // Doesn't match boundary
                            }
                        }
                    }
                }
            }
        }

        calculateAndPrint(group1Grades, "Group 1 (Matches Property or Boundary)");
        calculateAndPrint(group2Grades, "Group 2 (Doesn't Match Property or Boundary)");
        double mean1 = calculateAverage(group1Grades);
        double mean2 = calculateAverage(group2Grades);
        System.out.printf(" - Average difference: %.2f\n", Math.abs(mean1 - mean2));
    }

    // Method to compare one course to all other courses based on property and boundary value
    public void compareCourseToAllOtherCourses(String mainCourseName, String property, Object boundaryValue) {
        Course mainCourse = findCourseByName(mainCourseName);

        if (mainCourse == null) {
            System.out.println("Course not found: " + mainCourseName);
            return;
        }

        Map<Integer, CurrentStudentRecord> studentRecords = studentManager.getAllStudentRecords();
        List<Double> mainCourseGrades = new ArrayList<>();

        // Get grades for the main course, filtered by property and boundary value
        for (CurrentStudentRecord student : studentRecords.values()) {
            List<Double> grades = student.getCourseGrades();
            int mainCourseIndex = mainCourse.getColumnIndex();
            if (mainCourseIndex >= 0 && mainCourseIndex < grades.size()) {
                Double mainCourseGrade = grades.get(mainCourseIndex);
                if (mainCourseGrade != null && matchesProperty(student, property, boundaryValue)) {
                    mainCourseGrades.add(mainCourseGrade);
                }
            }
        }

        // Now, compare with all other courses
        for (Course otherCourse : courses) {
            if (!otherCourse.equals(mainCourse)) {
                List<Double> otherCourseGrades = new ArrayList<>();
                for (CurrentStudentRecord student : studentRecords.values()) {
                    List<Double> grades = student.getCompletedCourseGrades();
                    int otherCourseIndex = otherCourse.getColumnIndex();
                    if (otherCourseIndex >= 0 && otherCourseIndex < grades.size()) {
                        Double otherCourseGrade = grades.get(otherCourseIndex);
                        if (otherCourseGrade != null && matchesProperty(student, property, boundaryValue)) {
                            otherCourseGrades.add(otherCourseGrade);
                        }
                    }
                }

                displayComparison(mainCourseName, mainCourseGrades, otherCourse.getName(), otherCourseGrades);
            }
        }
    }

    // Method to find the best properties and boundary values to predict grades for a specific course
    public void findBestPropertyForCourse(String courseName) {
        Course targetCourse = findCourseByName(courseName);
        if (targetCourse == null) {
            System.out.println("Course not found: " + courseName);
            return;
        }

        // List of properties to evaluate
        String[] properties = {"Neuro-Synaptic Interface Level", "Chrono-Adaptation Rate", "Plasma Conductivity Quotient",
                "Telepathic Synchronisation Index", "Aetheric Resonance Capacity"};

        double overallVariance = calculateOverallVariance(targetCourse);
        double bestVarianceReduction = -1;
        List<Map<String, Object>> bestProperties = new ArrayList<>(); // List to hold all best properties

        for (String property : properties) {
            List<Object> boundaryValues = getBoundaryValuesForProperty(property);

            for (Object boundaryValue : boundaryValues) {
                double varianceReduction = overallVariance - calculateVarianceReduction(targetCourse, property, boundaryValue);
                System.out.printf("Property: %s | Boundary: %s | Variance Reduction: %.3f\n", property, boundaryValue, varianceReduction);

                if (varianceReduction > bestVarianceReduction) {
                    // New best variance reduction, reset the list
                    bestVarianceReduction = varianceReduction;
                    bestProperties.clear();
                    Map<String, Object> bestPropertyData = new HashMap<>();
                    bestPropertyData.put("Property", property);
                    bestPropertyData.put("Boundary", boundaryValue);
                    bestProperties.add(bestPropertyData);
                } else if (varianceReduction == bestVarianceReduction) {
                    // Equal variance reduction, add to the list
                    Map<String, Object> bestPropertyData = new HashMap<>();
                    bestPropertyData.put("Property", property);
                    bestPropertyData.put("Boundary", boundaryValue);
                    bestProperties.add(bestPropertyData);
                }
            }
        }

        // Output the final best properties
        if (!bestProperties.isEmpty()) {
            System.out.printf("\nBest properties for predicting the grade (Variance Reduction: %.3f):\n", bestVarianceReduction);
            for (Map<String, Object> propertyData : bestProperties) {
                System.out.printf(" - Property: %s | Boundary: %s\n", propertyData.get("Property"), propertyData.get("Boundary"));
            }
        } else {
            System.out.println("No suitable property and boundary found for variance reduction.");
        }
    }


    // Method to calculate variance reduction for a specific property and boundary
    private double calculateVarianceReduction(Course course, String property, Object boundaryValue) {
        List<Double> group1Grades = new ArrayList<>();
        List<Double> group2Grades = new ArrayList<>();
        Map<Integer, CurrentStudentRecord> studentRecords = studentManager.getAllStudentRecords();

        for (CurrentStudentRecord student : studentRecords.values()) {
            List<Double> grades = student.getCompletedCourseGrades();
            int courseIndex = course.getColumnIndex();
            if (courseIndex >= 0 && courseIndex < grades.size()) {
                Double grade = grades.get(courseIndex);
                if (grade != null) {
                    StudentInfoRecord infoRecord = studentInfoManager.getStudentByID(student.getStudentID());
                    if (infoRecord != null) {
                        Object propertyValue = getStudentProperty(infoRecord, property);

                        if (isNumericProperty(property)) {
                            // Compare based on the boundary value
                            if (comparePropertyToBoundary(propertyValue, boundaryValue)) {
                                group1Grades.add(grade);
                            } else {
                                group2Grades.add(grade);
                            }
                        } else {
                            // For categorical properties, split based on equality
                            if (propertyValue.equals(boundaryValue)) {
                                group1Grades.add(grade);
                            } else {
                                group2Grades.add(grade);
                            }
                        }
                    }
                }
            }
        }

        double overallVariance = calculateOverallVariance(course);
        double group1Variance = calculateVariance(group1Grades, calculateAverage(group1Grades));
        double group2Variance = calculateVariance(group2Grades, calculateAverage(group2Grades));

        // Weighted average of the variances
        double weightedVariance = (group1Grades.size() * group1Variance + group2Grades.size() * group2Variance) /
                (group1Grades.size() + group2Grades.size());

        return overallVariance - weightedVariance; // Variance reduction
    }

    private double calculateOverallVariance(Course course) {
        List<Double> allGrades = new ArrayList<>();
        Map<Integer, CurrentStudentRecord> studentRecords = studentManager.getAllStudentRecords();

        // Collect all grades for the given course
        for (CurrentStudentRecord student : studentRecords.values()) {
            List<Double> grades = student.getCompletedCourseGrades();
            int courseIndex = course.getColumnIndex();

            if (courseIndex >= 0 && courseIndex < grades.size()) {
                Double grade = grades.get(courseIndex);
                if (grade != null) {
                    allGrades.add(grade);
                }
            }
        }

        // Calculate the mean of all grades
        double mean = calculateAverage(allGrades);

        // Calculate variance using the mean
        double variance = 0;
        for (Double grade : allGrades) {
            variance += Math.pow(grade - mean, 2);
        }

        // Return the variance
        return (!allGrades.isEmpty()) ? variance / allGrades.size() : 0.0;
    }

    private List<Object> getBoundaryValuesForProperty(String property) {
        List<Object> boundaryValues = new ArrayList<>();
        List<StudentInfoRecord> studentRecords = studentInfoManager.getAllStudents();  // Now it's a list, not a map

        // Collect unique values for the property
        Set<Object> uniqueValues = new HashSet<>();
        for (StudentInfoRecord student : studentRecords) {  // Iterate over the list of students
            Object propertyValue = getStudentProperty(student, property);
            if (propertyValue != null) {
                uniqueValues.add(propertyValue);  // Add the property value to the unique values set
            }
        }

        // For numeric properties, calculate a few potential boundary values (e.g., min, max, average)
        if (isNumericProperty(property)) {
            List<Double> numericValues = new ArrayList<>();
            for (Object value : uniqueValues) {
                numericValues.add(Double.parseDouble(value.toString()));  // Convert property values to double
            }

            if (!numericValues.isEmpty()) {
                // Calculate min, max, and average as boundary values
                double min = Collections.min(numericValues);
                double max = Collections.max(numericValues);
                double average = calculateAverage(numericValues);  // Use helper method to calculate the average

                boundaryValues.add(min);     // Add min value
                boundaryValues.add(average); // Add average value
                boundaryValues.add(max);     // Add max value
            }
        } else {
            // For categorical properties, just return the unique values
            boundaryValues.addAll(uniqueValues);  // Add all unique categorical values
        }

        return boundaryValues;
    }


    // Check if a student matches the specified property and boundary value
    private boolean matchesProperty(CurrentStudentRecord student, String property, Object boundaryValue) {
        StudentInfoRecord infoRecord = studentInfoManager.getStudentByID(student.getStudentID());
        if (infoRecord == null) {
            return false;
        }

        Object propertyValue = getStudentProperty(infoRecord, property);
        if (propertyValue == null) {
            return false;
        }

        if (boundaryValue instanceof String) {
            return propertyValue.equals(boundaryValue);  // For categorical properties
        }

        return false;
    }

    // Display comparison between two courses
    private void displayComparison(String mainCourseName, List<Double> mainCourseGrades, String otherCourseName, List<Double> otherCourseGrades) {
        double mainCourseAvg = calculateAverage(mainCourseGrades);
        double otherCourseAvg = calculateAverage(otherCourseGrades);
        double mainCourseStdDev = calculateStandardDeviation(mainCourseGrades, mainCourseAvg);
        double otherCourseStdDev = calculateStandardDeviation(otherCourseGrades, otherCourseAvg);

        System.out.printf("Comparison between %s and %s:\n", mainCourseName, otherCourseName);
        System.out.printf("%s - Average Grade: %.2f | Std Dev: %.2f | Students: %d\n", mainCourseName, mainCourseAvg, mainCourseStdDev, mainCourseGrades.size());
        System.out.printf("%s - Average Grade: %.2f | Std Dev: %.2f | Students: %d\n", otherCourseName, otherCourseAvg, otherCourseStdDev, otherCourseGrades.size());
        System.out.printf(" - Average difference: %.2f\n", Math.abs(mainCourseAvg - otherCourseAvg));
        System.out.printf(" - Std Dev difference: %.2f\n", Math.abs(mainCourseStdDev - otherCourseStdDev));
        System.out.println();
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
        // For categorical properties, check for equality
        if (propertyValue instanceof String && boundaryValue instanceof String) {
            return propertyValue.equals(boundaryValue); // Direct comparison for non-numeric properties
        }

        // For numeric properties, check if propertyValue >= boundaryValue
        if (propertyValue instanceof Number && boundaryValue instanceof Number) {
            return ((Number) propertyValue).doubleValue() >= ((Number) boundaryValue).doubleValue();
        }

        // Handle numeric values encoded as strings (e.g., "5.0 Hz")
        if (propertyValue instanceof String && boundaryValue instanceof Number) {
            try {
                double propNumericValue = Double.parseDouble(propertyValue.toString().replaceAll("[^\\d.]", ""));
                double boundValue = ((Number) boundaryValue).doubleValue();
                return propNumericValue >= boundValue;
            } catch (NumberFormatException e) {
                System.out.println("Invalid numeric format: " + propertyValue);
                return false;
            }
        }

        // Default fallback for incompatible types
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

    private double calculateVariance(List<Double> grades, double mean) {
        double sum = 0;
        for (Double grade : grades) {
            sum += Math.pow(grade - mean, 2);
        }
        return (!grades.isEmpty()) ? sum / grades.size() : 0.0;
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