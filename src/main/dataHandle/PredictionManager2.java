package src.main.dataHandle;

import java.util.*;

public class PredictionManager2 {
    private final CurrentStudentManager studentManager;
    private final List<Course> courses;
    private final StudentInfoManager studentInfoManager;
    private final SimilarCourses similarCourses;

    public PredictionManager2(CurrentStudentManager studentManager, List<Course> courses, StudentInfoManager studentInfoManager, SimilarCourses similarCourses) {
        this.studentManager = studentManager;
        this.courses = courses;
        this.studentInfoManager = studentInfoManager;
        this.similarCourses = similarCourses;
    }

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
        List<Double> combinedGrades = new ArrayList<>(group1Grades);
        combinedGrades.addAll(group2Grades);
        calculateAndPrint(group1Grades, "\nGroup 1 (Matches Boundary or Above)");
        calculateAndPrint(group2Grades, "Group 2 (Below Boundary or Not Matching)");
        System.out.printf("Comparison between Group 1 and Group 2 : \n");
        double mean1 = calculateAverage(group1Grades);
        double mean2 = calculateAverage(group2Grades);
        System.out.printf(" - Average difference: %.2f\n", Math.abs(mean1 - mean2));
        System.out.printf(" - Std Dev difference: %.2f\n", Math.abs(calculateStandardDeviation(group1Grades, mean1) - calculateStandardDeviation(group2Grades, mean2)));
        //System.out.println(group1Grades);
        //System.out.println(group2Grades);
        //System.out.println(combinedGrades);
        System.out.println("NEXT METHOD");
        System.out.println(calculateVarianceReduction(combinedGrades, group1Grades, group2Grades));


    }

    public Course findCourseByName(String courseName) {
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

    private boolean isNumericProperty(String property) {
        return property.equals("Plasma Conductivity Quotient") || property.equals("Chrono-Adaptation Rate") ||
                property.equals("Aetheric Resonance Capacity");
    }

    private void calculateAndPrint(List<Double> grades, String label) {
        double average = calculateAverage(grades);
        double stdDev = calculateStandardDeviation(grades, average);
        System.out.printf("%s | Average Grade: %.2f | Std Dev: %.2f | Number of Students: %d\n", label, average, stdDev, grades.size());
    }

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


    private double calculateOverallVariance(List<Double> grades) {
        int totalStudents = grades.size();
        int totalPass = (int) grades.stream().filter(grade -> grade != null && grade >= 6).count();
        double pOverall = (double) totalPass / totalStudents;
        return pOverall * (1 - pOverall);
    }

    private double calculateGroupVariance(List<Double> groupGrades) {
        int totalStudentsInGroup = groupGrades.size();
        int totalPassInGroup = (int) groupGrades.stream().filter(grade -> grade != null && grade >= 6).count();
        double pGroup = (double) totalPassInGroup / totalStudentsInGroup;
        return pGroup * (1 - pGroup);
    }

    private double calculateWeightedVariance(List<Double> grades, List<Double> group1Grades, List<Double> group2Grades) {
        int totalStudents = grades.size();
        int group1Size = group1Grades.size();
        int group2Size = group2Grades.size();

        double varianceWithProperty = calculateGroupVariance(group1Grades);
        double varianceWithoutProperty = calculateGroupVariance(group2Grades);

        double weightedVariance = ((double) group1Size / totalStudents) * varianceWithProperty +
                ((double) group2Size / totalStudents) * varianceWithoutProperty;
        return weightedVariance;
    }

    private double calculateVarianceReduction(List<Double> grades, List<Double> group1Grades, List<Double> group2Grades) {
        double overallVariance = calculateOverallVariance(grades);
        double weightedVariance = calculateWeightedVariance(grades, group1Grades, group2Grades);
        return overallVariance - weightedVariance;
    }

    private static List<Course> getNotStartedCourses(List<Course> courses) {
        List<Course> notStartedCourses = new ArrayList<>();
        for (Course course : courses) {
            boolean allGradeNull = true;
            for (Double grade : course.getGrades()) {
                if (grade != null) {
                    allGradeNull = false;
                    break;
                }
            }
            if (allGradeNull) {
                System.out.println("Unstarted course found: " + course.getName());
                notStartedCourses.add(course);
            }
        }
        System.out.println("Total unstarted courses found: " + notStartedCourses.size());
        return notStartedCourses;
    }

    private List<Object> getBoundaryValuesForProperty(String property) {
        List<Object> boundaryValues = new ArrayList<>();
        List<StudentInfoRecord> studentRecords = studentInfoManager.getAllStudents();

        // Collect unique values for the property
        Set<Object> uniqueValues = new HashSet<>();
        for (StudentInfoRecord student : studentRecords) {
            Object propertyValue = getStudentProperty(student, property);
            if (propertyValue != null) {
                uniqueValues.add(propertyValue);  // Add unique property values
            }
        }

        // Separate handling for numeric properties
        if (isNumericProperty(property)) {
            List<Double> numericValues = new ArrayList<>();
            for (Object value : uniqueValues) {
                try {
                    numericValues.add(Double.parseDouble(value.toString()));  // Convert to Double
                } catch (NumberFormatException e) {
                    System.out.println("Warning: Non-numeric value found for numeric property " + property + ": " + value);
                }
            }
            if (!numericValues.isEmpty()) {
                numericValues.sort(Double::compareTo);  // Optional: Sort numeric values if needed
                boundaryValues.addAll(numericValues);
            }
        } else {
            // For non-numeric properties, add unique values directly
            boundaryValues.addAll(uniqueValues);
        }

        return boundaryValues;
    }
    public List<Course> findSimilarCoursesToNotStartedCourses (List<Course> notStartedCourses){
        CourseManager courseManager = CourseManager.getInstance();
        courseManager.loadGraduateGrades();
        List<Course> graduatedCourses = courseManager.getGraduatedCourses();

        List<Course> similarCoursesList = new ArrayList<>();

        List<Course> notStartedCoursesInGraduatedStudents = new ArrayList<>();
        for (Course targetCourse : graduatedCourses) {
            for (Course notStartedCourse : notStartedCourses) {
                if (targetCourse.getName().equals(notStartedCourse.getName())) {
                    notStartedCoursesInGraduatedStudents.add(targetCourse);
                }
            }
        }

        for (Course notStartedCourse : notStartedCoursesInGraduatedStudents) {
            Course mostSimilarCourse = similarCourses.findMostSimilarCourse(notStartedCourse);
            if (mostSimilarCourse != null) {
                similarCoursesList.add(mostSimilarCourse);
            }
        }
        return similarCoursesList;
    }


    private String[] getProperties(){
        String[] properties = {"Neuro-Synaptic Interface Level", "Chrono-Adaptation Rate", "Plasma Conductivity Quotient",
                "Telepathic Synchronisation Index", "Aetheric Resonance Capacity"};
        return properties;
    }






    public List<String> findBestPropertyForUncompletedCourses(List<Course> courses) {

        List <String> bestProperties = new ArrayList<>();

        List<Course> notStartedCourses = getNotStartedCourses(courses);
        if (notStartedCourses.isEmpty()) {
            System.out.println("No unstarted courses found.");
            return null;
        }

        Map<Integer, CurrentStudentRecord> studentRecords = studentManager.getAllStudentRecords();
        String[] properties = getProperties();

        List<Course> similarCoursesList = new ArrayList<>(findSimilarCoursesToNotStartedCourses(notStartedCourses));


        for (Course similarCourse : similarCoursesList) {
            // Collect all grades for the current similarCourse to use as `grades`
            List<Double> grades = new ArrayList<>();
            for (CurrentStudentRecord student : studentRecords.values()) {
                List<Double> studentGrades = student.getCourseGrades();
                int courseIndex = similarCourse.getColumnIndex();
                if (courseIndex >= 0 && courseIndex < studentGrades.size()) {
                    Double grade = studentGrades.get(courseIndex);
                    if (grade != null) {
                        grades.add(grade);
                    }
                }
            }

            double bestVarianceReduction = 0;
            String bestProperty = null;
            Object bestBoundaryValue = null;
            int bestPassCount = 0;
            int bestFailCount = 0;


            for (String property : properties) {
                List<Object> boundaryValues = getBoundaryValuesForProperty(property);
                //System.out.println(property);

                for (Object boundaryValue : boundaryValues) {
                    List<Double> group1Grades = new ArrayList<>();
                    List<Double> group2Grades = new ArrayList<>();
                    int passCount = 0;
                    int failCount = 0;

                    for (CurrentStudentRecord student : studentRecords.values()) {
                        List<Double> studentGrades = student.getCourseGrades();
                        int courseIndex = similarCourse.getColumnIndex();
                        if (courseIndex >= 0 && courseIndex < studentGrades.size()) {
                            Double grade = studentGrades.get(courseIndex);
                            if (grade == null){
                                continue;
                            }else if (grade >= 6.0){
                                passCount++;
                            }else{
                                failCount++;
                            }

                            StudentInfoRecord infoRecord = studentInfoManager.getStudentByID(student.getStudentID());
                            if (infoRecord != null) {
                                Object propertyValue = getStudentProperty(infoRecord, property);

                                if (isNumericProperty(property)) {
                                    if (comparePropertyToBoundary(propertyValue, boundaryValue)) {
                                        group1Grades.add(grade); // Group with the property
                                    } else {
                                        group2Grades.add(grade); // Group without the property
                                    }
                                } else {
                                    if (propertyValue.equals(boundaryValue)) {
                                        group1Grades.add(grade);
                                    } else {
                                        group2Grades.add(grade);
                                    }
                                }
                            }
                        }
                    }

                    // Only calculate variance reduction if both groups have data
                    //System.out.println(property);
                    if (!group1Grades.isEmpty() && !group2Grades.isEmpty() ) {
                        double varianceReduction = calculateVarianceReduction(grades, group1Grades, group2Grades);
                        if (varianceReduction > bestVarianceReduction) {
                            bestVarianceReduction = varianceReduction;
                            bestProperty = property;
                            bestBoundaryValue = boundaryValue;
                            bestPassCount = passCount;
                            bestFailCount = failCount;
                            //System.out.println(property);
                        }
                    }
                }
            }

            if (bestProperty != null) {
                System.out.printf("Best variance reduction for course '%s' is %.4f with property '%s' and boundary value '%s'\n",
                        similarCourse.getName(), bestVarianceReduction, bestProperty, bestBoundaryValue);
                        bestProperties.add(bestProperty);
                if (bestPassCount > bestFailCount){
                    System.out.println("Most likely to pass");
                }else{
                    System.out.println("Most likely to fail");
                }
            } else {
                System.out.printf("No significant variance reduction found for course '%s'.\n", similarCourse.getName());
            }
            for (Course course : notStartedCourses){
                    evaluateStudentsForCourse(course, bestProperty, studentRecords, bestBoundaryValue);
                
            }
            
        }
        return bestProperties;
    }


    private void evaluateStudentsForCourse(Course course, String bestProperty, Map<Integer, CurrentStudentRecord> studentRecords, Object bestBoundaryValue) {
        System.out.printf("Evaluating students for course '%s' based on property '%s':\n", course.getName(), bestProperty);
        int likelyToFailCount = 0;
        int likelyToPassCount = 0;
        for (CurrentStudentRecord student : studentRecords.values()) {
            StudentInfoRecord infoRecord = studentInfoManager.getStudentByID(student.getStudentID());
            if (infoRecord != null) {
                Object propertyValue = getStudentProperty(infoRecord, bestProperty);
                boolean likelyToPass = false;
    

                    if (isNumericProperty(bestProperty)) {
                        if (comparePropertyToBoundary(propertyValue, bestBoundaryValue)) {
                            likelyToPass = true;
                            likelyToPassCount++;

                        }else {
                            likelyToFailCount ++;
                        }
                    } else {
                        if (propertyValue.equals(bestBoundaryValue)) {
                            likelyToPass = true;
                            likelyToPassCount++;

                        }else{
                            likelyToFailCount++;
                        }
                    }
    
                String result = likelyToPass ? "likely to pass" : "likely to fail";
                System.out.printf("Student ID: %d is %s for course '%s'.\n", student.getStudentID(), result, course.getName());
            }
        }
    
        System.out.println("Fail: " + likelyToFailCount);
        System.out.println("Pass: " + likelyToPassCount);
    }
}
     

