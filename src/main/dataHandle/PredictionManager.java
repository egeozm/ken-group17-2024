package src.main.dataHandle;

import java.util.*;

public class PredictionManager {
    private final CurrentStudentManager studentManager;
    private final List<Course> courses;
    private final StudentInfoManager studentInfoManager;
    private final SimilarCourses similarCourses;


    public PredictionManager(CurrentStudentManager studentManager, List<Course> courses, StudentInfoManager studentInfoManager, SimilarCourses similarCourses) {
        this.studentManager = studentManager;
        this.courses = courses;
        this.studentInfoManager = studentInfoManager;
        this.similarCourses = similarCourses;
    }

    public void findBestPropertyOrCombinationForCourse(String courseName) {
        Course targetCourse = findCourseByName(courseName);
        if (targetCourse == null) {
            System.out.println("Course not found: " + courseName);
            return;
        }

        if (!courseHasGrades(targetCourse)) {
            System.out.println("No students have completed the course: " + courseName);
            return;
        }

        String[] properties = {"Neuro-Synaptic Interface Level", "Chrono-Adaptation Rate",
                "Plasma Conductivity Quotient", "Telepathic Synchronisation Index",
                "Aetheric Resonance Capacity"};

        double overallVariance = calculateOverallVariance(targetCourse);
        double bestVarianceReduction = -1;
        List<String> bestProperties = null;
        Object bestBoundary = null;

        // Generate combinations of properties
        List<List<String>> propertyCombinations = generatePropertyCombinations(properties, 2); // Try single and pair combinations

        for (List<String> combination : propertyCombinations) {
            // Get possible boundary values for each property in the combination
            List<Object> boundaryValues = new ArrayList<>();
            for (String property : combination) {
                boundaryValues.addAll(getBoundaryValuesForProperty(property));
            }

            for (Object boundaryValue : boundaryValues) {
                double combinedVarianceReduction = calculateCombinedVarianceReduction(targetCourse, combination, boundaryValue);

                if (combinedVarianceReduction > bestVarianceReduction) {
                    bestVarianceReduction = combinedVarianceReduction;
                    bestProperties = new ArrayList<>(combination);
                    bestBoundary = boundaryValue;
                }
            }
        }

        if (bestProperties != null) {
            System.out.printf("Best property/combination for predicting grade in course %s: %s with boundary %s, Variance Reduction: %.6f\n",
                    courseName, bestProperties, bestBoundary, overallVariance - bestVarianceReduction);
        } else {
            System.out.println("No suitable property/combination found for variance reduction.");
        }
    }


    public void predictGradeForUncompletedCourse(String uncompletedCourseName, int studentID) {
        Course uncompletedCourse = findCourseByName(uncompletedCourseName);

        if (uncompletedCourse == null) {
            System.out.println("Uncompleted course not found: " + uncompletedCourseName);
            return;
        }

        // Use the SimilarCourses to find the most similar course
        Course mostSimilarCourse = similarCourses.findMostSimilarCourse(uncompletedCourse);

        if (mostSimilarCourse == null) {
            System.out.println("No similar course found for: " + uncompletedCourseName);
            return;
        }

        // Use the most similar course to find the best property for prediction
        Map<String, Object> bestProperty = findBestProperty(mostSimilarCourse.getName());

        if (bestProperty == null) {
            System.out.println("No suitable property found for predicting grade for the similar course: " + mostSimilarCourse.getName());
            return;
        }

        // Predict the grade for the student using the best property
        StudentInfoRecord studentInfo = studentInfoManager.getStudentByID(studentID);
        if (studentInfo == null) {
            System.out.println("Student not found: " + studentID);
            return;
        }

        Object studentProperty = getStudentProperty(studentInfo, (String) bestProperty.get("Property"));
        if (studentProperty == null) {
            System.out.println("Student does not have the required property for prediction.");
            return;
        }

        // If the student's property value matches the boundary, predict one grade, otherwise predict another
        double predictedGrade;
        if (comparePropertyToBoundary(studentProperty, bestProperty.get("Boundary"))) {
            predictedGrade = calculateAverageGradeForProperty(mostSimilarCourse, (String) bestProperty.get("Property"), bestProperty.get("Boundary"));
        } else {
            predictedGrade = calculateAverageGradeForProperty(mostSimilarCourse, (String) bestProperty.get("Property"), null);
        }

        System.out.printf("Predicted grade for student %d in course %s (based on similar course %s): %.2f\n",
                studentID, uncompletedCourseName, mostSimilarCourse.getName(), predictedGrade);
    }

    private double calculateCombinedVarianceReduction(Course course, List<String> properties, Object boundaryValue) {
        List<Double> group1Grades = new ArrayList<>();
        List<Double> group2Grades = new ArrayList<>();
        Map<Integer, CurrentStudentRecord> studentRecords = studentManager.getAllStudentRecords();

        for (CurrentStudentRecord student : studentRecords.values()) {
            List<Double> grades = student.getCourseGrades();
            int courseIndex = course.getColumnIndex();
            if (courseIndex >= 0 && courseIndex < grades.size()) {
                Double grade = grades.get(courseIndex);
                if (grade != null) {
                    StudentInfoRecord infoRecord = studentInfoManager.getStudentByID(student.getStudentID());
                    if (infoRecord != null) {
                        boolean matchesBoundary = true;
                        for (String property : properties) {
                            Object propertyValue = getStudentProperty(infoRecord, property);
                            if (!comparePropertyToBoundary(propertyValue, boundaryValue)) {
                                matchesBoundary = false;
                                break;
                            }
                        }

                        if (matchesBoundary) {
                            group1Grades.add(grade);
                        } else {
                            group2Grades.add(grade);
                        }
                    }
                }
            }
        }

        double group1Variance = calculateVariance(group1Grades, calculateAverage(group1Grades));
        double group2Variance = calculateVariance(group2Grades, calculateAverage(group2Grades));
        double weightedVariance = (group1Grades.size() * group1Variance + group2Grades.size() * group2Variance) /
                (group1Grades.size() + group2Grades.size());

        return calculateOverallVariance(course) - weightedVariance;
    }

    // Helper function to generate combinations of properties
    private List<List<String>> generatePropertyCombinations(String[] properties, int maxCombinationLength) {
        List<List<String>> combinations = new ArrayList<>();
        for (int i = 1; i <= maxCombinationLength; i++) {
            combinations.addAll(combine(properties, i, 0, new ArrayList<>()));
        }
        return combinations;
    }

    private List<List<String>> combine(String[] properties, int length, int start, List<String> current) {
        List<List<String>> result = new ArrayList<>();
        if (current.size() == length) {
            result.add(new ArrayList<>(current));
            return result;
        }
        for (int i = start; i < properties.length; i++) {
            current.add(properties[i]);
            result.addAll(combine(properties, length, i + 1, current));
            current.remove(current.size() - 1);
        }
        return result;
    }

    // Method to find the best property and boundary value to predict grades for a specific course
    private Map<String, Object> findBestProperty(String courseName) {
        Course targetCourse = findCourseByName(courseName);
        if (targetCourse == null) {
            System.out.println("Course not found: " + courseName);
            return null;
        }

        // Check if there are any grades for this course
        if (!courseHasGrades(targetCourse)) {
            System.out.println("No students have completed the course: " + courseName);
            return null;
        }

        String[] properties = {"Neuro-Synaptic Interface Level", "Chrono-Adaptation Rate", "Plasma Conductivity Quotient",
                "Telepathic Synchronisation Index", "Aetheric Resonance Capacity"};

        double overallVariance = calculateOverallVariance(targetCourse);
        double bestVarianceReduction = -1;
        Map<String, Object> bestPropertyData = null;

        for (String property : properties) {
            List<Object> boundaryValues = getBoundaryValuesForProperty(property);

            for (Object boundaryValue : boundaryValues) {
                double varianceReduction = overallVariance - calculateVarianceReduction(targetCourse, property, boundaryValue);

                if (varianceReduction > bestVarianceReduction) {
                    bestVarianceReduction = varianceReduction;
                    bestPropertyData = new HashMap<>();
                    bestPropertyData.put("Property", property);
                    bestPropertyData.put("Boundary", boundaryValue);
                }
            }
        }

        if (bestPropertyData != null) {
            System.out.printf("Best property for predicting the grade in course %s: %s with boundary %s\n",
                    courseName, bestPropertyData.get("Property"), bestPropertyData.get("Boundary"));
        }

        return bestPropertyData;
    }

    // Method to calculate the average grade for a specific property and boundary
    private double calculateAverageGradeForProperty(Course course, String property, Object boundaryValue) {
        List<Double> grades = new ArrayList<>();
        Map<Integer, CurrentStudentRecord> studentRecords = studentManager.getAllStudentRecords();

        for (CurrentStudentRecord student : studentRecords.values()) {
            List<Double> studentGrades = student.getCourseGrades();
            int courseIndex = course.getColumnIndex();
            if (courseIndex >= 0 && courseIndex < studentGrades.size()) {
                Double grade = studentGrades.get(courseIndex);
                if (grade != null) {
                    StudentInfoRecord infoRecord = studentInfoManager.getStudentByID(student.getStudentID());
                    Object studentProperty = getStudentProperty(infoRecord, property);

                    if (boundaryValue == null || comparePropertyToBoundary(studentProperty, boundaryValue)) {
                        grades.add(grade);
                    }
                }
            }
        }

        return calculateAverage(grades);
    }


    // Method to calculate variance reduction for a specific property and boundary
    private double calculateVarianceReduction(Course course, String property, Object boundaryValue) {
        List<Double> group1Grades = new ArrayList<>();
        List<Double> group2Grades = new ArrayList<>();
        Map<Integer, CurrentStudentRecord> studentRecords = studentManager.getAllStudentRecords();

        for (CurrentStudentRecord student : studentRecords.values()) {
            List<Double> grades = student.getCourseGrades();
            int courseIndex = course.getColumnIndex();
            if (courseIndex >= 0 && courseIndex < grades.size()) {
                Double grade = grades.get(courseIndex);
                if (grade != null) {
                    StudentInfoRecord infoRecord = studentInfoManager.getStudentByID(student.getStudentID());
                    if (infoRecord != null) {
                        Object propertyValue = getStudentProperty(infoRecord, property);

                        if (comparePropertyToBoundary(propertyValue, boundaryValue)) {
                            group1Grades.add(grade); // Matches boundary
                        } else {
                            group2Grades.add(grade); // Doesn't match boundary
                        }
                    }
                }
            }
        }
        System.out.println(" Group 1 size : " + group1Grades.size());
        System.out.println(" Group 2 size : " + group2Grades.size());
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
            List<Double> grades = student.getCourseGrades();
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

        if (isNumericProperty(property)) {
            List<Double> numericValues = new ArrayList<>();
            for (Object value : uniqueValues) {
                numericValues.add(Double.parseDouble(value.toString()));  // Convert property values to double
            }

            if (!numericValues.isEmpty()) {
                boundaryValues.addAll(uniqueValues);
            }
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

    private boolean courseHasGrades(Course course) {
        Map<Integer, CurrentStudentRecord> studentRecords = studentManager.getAllStudentRecords();
        for (CurrentStudentRecord student : studentRecords.values()) {
            List<Double> grades = student.getCourseGrades();
            int courseIndex = course.getColumnIndex();
            if (courseIndex >= 0 && courseIndex < grades.size() && grades.get(courseIndex) != null) {
                return true; // If any grade is found, return true
            }
        }
        return false; // No grades found
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