package src.main.dataHandle;

import src.main.dataHandle.SimilarCourses;

import java.util.*;

public class PredictionManager2{
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
        System.out.println(calculateVarianceReduction2(combinedGrades,group1Grades,group2Grades));
        

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





    //New methods to calculate variance reduction like in the discrod ss example
    private static double calculateOverallVariance(List<Double> grades){
        double pOverall = (double) getStudentsPass(grades).size()/grades.size();
        double varianceOverall = pOverall * (1-pOverall);
        return varianceOverall;
    }
    private static double calculateStudentsVariance(List<Double> grades,List<Double> passedStudents){
        double p = passedStudents.size()/grades.size();
        double studentsVariance = p * (1 - p);
        return studentsVariance;
    }
    private static double calculateWeightedVariance(List<Double>grades, List<Double>gradesWith, List<Double>gradesWithout){
        double weightedVariance = (gradesWith.size()/grades.size()) * calculateStudentsVariance(gradesWith,getStudentsPass(gradesWith)) + (gradesWithout.size()/grades.size()) * calculateStudentsVariance(gradesWithout, getStudentsPass(gradesWithout));
        return weightedVariance;
    }
    private static double calculateVarianceReduction2(List<Double>grades, List<Double>gradesWith, List<Double>gradesWithout){
        double varianceReduction = calculateOverallVariance(grades) - calculateWeightedVariance(grades, gradesWith, gradesWithout);
        return varianceReduction;
    }
    //It returns list with students that pass the course with property/without property.
    private static List<Double> getStudentsPass(List<Double> grades){
        List<Double> studentsPass = new ArrayList<>();
        for (double grade : grades){
            if (grade >= 6){
                studentsPass.add(grade);
            }
        }
        return studentsPass;
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
                notStartedCourses.add(course);
            }
        }
        return notStartedCourses;
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

    public void findBestPropertyForUncomplitedCourses(List<Course> courses){

        //It should get notStartedCourses and similarCourses to not started ones.
        //Size of list with notStartedCourses is always 0 so I think that is why this does not work.

        List<Course> notStartedCourses = new ArrayList<>();
        notStartedCourses = getNotStartedCourses(courses);
        List<Course> similarCourses = new ArrayList<>();
        Map<Integer, CurrentStudentRecord> studentRecords = studentManager.getAllStudentRecords();
        String[] properties = {"Neuro-Synaptic Interface Level", "Chrono-Adaptation Rate", "Plasma Conductivity Quotient",
                "Telepathic Synchronisation Index", "Aetheric Resonance Capacity"};
        
        for (Course notStartedCourse : notStartedCourses){
            similarCourses.add(this.similarCourses.findMostSimilarCourse(notStartedCourse));
        }

        //Loop here tries to calculate and print lowest variance reduction.
        //But it does not work because list of similarCourses is empty since notStartedCourses list is empty as well.(I have no idea why) 

        for (Course similarCourse : similarCourses){
            for (String property : properties){
                List<Object> boundaryValues = getBoundaryValuesForProperty(property);
                List<Double> group1Grades = new ArrayList<>();
                List<Double> group2Grades = new ArrayList<>();
                List<Double> grades = new ArrayList<>();
                for(Object boundaryValue : boundaryValues){
                    for (CurrentStudentRecord student : studentRecords.values()) {
                        grades = student.getCourseGrades();
                        int courseIndex = similarCourse.getColumnIndex();
                        if (courseIndex >= 0 && courseIndex < grades.size()) {
                            Double grade = grades.get(courseIndex);
                            if (grade == null) {
                                continue; // Skip students with null grades
                            }
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
                double lowestVarianceReduction = 1.0;
                if (calculateVarianceReduction2(grades, group1Grades, group2Grades) < lowestVarianceReduction) {
                    lowestVarianceReduction = calculateVarianceReduction2(grades, group1Grades, group2Grades);

                }
                System.out.println("Best Variance Reduction: " + property + " || " + "Course: " + similarCourse);  
            }
        }  

    }
        
        


}
     

