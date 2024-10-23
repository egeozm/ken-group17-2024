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

    public void compareAverageGradeForProperty(String courseName, String property) {
        Course targetCourse = findCourseByName(courseName);
        if (targetCourse == null) {
            System.out.println("Course not found: " + courseName);
        }

        Map<String, List<Double>> propertyGroups = new HashMap<>();
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
                        String propertyValue = getStudentProperty(infoRecord, property);
                        // Check if the group exists, if not, create a new list
                        if (!propertyGroups.containsKey(propertyValue)) {
                            propertyGroups.put(propertyValue, new ArrayList<>());
                        }
                        // Add the grade to the existing list
                        propertyGroups.get(propertyValue).add(grade);
                    }
                }
            }
        }
        // With Map.Entry: You retrieve both the key and value together in a single Map.Entry object.
        for (Map.Entry<String, List<Double>> entry : propertyGroups.entrySet()) {
            String group = entry.getKey();
            List<Double> grades = entry.getValue();
            double average = calculateAverage(grades);
            double stdDev = calculateStandardDeviation(grades, average);

            System.out.printf("Group: %s | Average Grade: %.2f | Std Dev: %.2f | Number of Students: %d\n", group, average, stdDev, grades.size());
        }
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
    predictionManager.compareAverageGradeForProperty("Vortex Quantum Mechanics", "Aetheric Resonance Capacity");

 */