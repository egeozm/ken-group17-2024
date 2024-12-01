package src.main.dataHandle;

import java.util.*;
import java.util.stream.Collectors;

public class StudentGroupingManager {
    private final StudentInfoManager studentInfoManager;
    private final PredictedCurrentStudentManager currentStudentManager;

    public StudentGroupingManager(StudentInfoManager studentInfoManager, PredictedCurrentStudentManager currentStudentManager) {
        this.studentInfoManager = studentInfoManager;
        this.currentStudentManager = currentStudentManager;
    }

    // Group and filter data dynamically based on user selection
    public Map<String, Map<String, List<Double>>> groupAndFilterData(Course course, String selectedAxis, Map<String, List<String>> filters) {
        Map<String, Map<String, List<Double>>> groupedData = new HashMap<>();

        for (StudentInfoRecord student : studentInfoManager.getAllStudents()) {
            String axisGroup = getAttributeValue(student, selectedAxis);

            if (!matchesFilters(student, filters)) {
                continue; // Skip students not matching the filters
            }

            CurrentStudentRecord studentRecord = currentStudentManager.getStudentRecord(student.getStudentID());
            if (studentRecord != null) {
                List<Double> grades = studentRecord.getCourseGrades();
                if (course.getColumnIndex() < grades.size()) {
                    Double grade = grades.get(course.getColumnIndex());
                    if (grade != null) {
                        groupedData.computeIfAbsent(axisGroup, k -> new HashMap<>());
                        groupedData.get(axisGroup)
                                .computeIfAbsent("Grades", k -> new ArrayList<>())
                                .add(grade);
                    }
                }
            }
        }
        return groupedData;
    }


    // Get attribute value dynamically based on selected axis
    private String getAttributeValue(StudentInfoRecord student, String attribute) {
        switch (attribute.toLowerCase()) {
            case "nsil":
                return student.getNeuroSynapticInterfaceLevel(); // Ensure this method exists and works
            case "car":
                return String.valueOf(student.getChronoAdaptationRate()); // Ensure proper conversion
            case "tsi":
                return String.valueOf(student.getTelepathicSynchronisationIndex()); // Likely working
            case "arc":
                return String.valueOf(student.getAethericResonanceCapacity()); // Ensure proper conversion
            case "pcq":
                return String.valueOf(student.getPlasmaConductivityQuotient()); // Ensure proper conversion
            default:
                return "Grades"; // Default case may cause issues
        }
    }


    private boolean matchesFilters(StudentInfoRecord student, Map<String, List<String>> filters) {
        for (Map.Entry<String, List<String>> filter : filters.entrySet()) {
            String attribute = filter.getKey();
            List<String> allowedValues = filter.getValue();

            if (allowedValues == null || allowedValues.isEmpty()) {
                continue; // Skip empty filters
            }

            String studentValue = getAttributeValue(student, attribute);
            if (studentValue == null) {
                return false; // No value to compare
            }

            // Handle PCQ (range filter)
            if (attribute.equalsIgnoreCase("PCQ")) {
                try {
                    double pcqValue = Double.parseDouble(studentValue);
                    double minPCQ = Double.parseDouble(allowedValues.get(0));
                    double maxPCQ = Double.parseDouble(allowedValues.get(1));
                    if (pcqValue < minPCQ || pcqValue > maxPCQ) {
                        return false;
                    }
                } catch (NumberFormatException e) {
                    return false; // Handle invalid numeric input
                }
            }
            // Handle CAR (case-insensitive match for "tau")
            else if (attribute.equalsIgnoreCase("CAR")) {
                String normalizedStudentValue = studentValue.toLowerCase().replace(" tau", "").trim();
                List<String> normalizedAllowedValues = allowedValues.stream()
                        .map(value -> value.toLowerCase().replace(" tau", "").trim())
                        .collect(Collectors.toList());
                if (!normalizedAllowedValues.contains(normalizedStudentValue)) {
                    return false;
                }
            }
            // Handle ARC (case-insensitive match for "Hz")
            else if (attribute.equalsIgnoreCase("ARC")) {
                String normalizedStudentValue = studentValue.toLowerCase().replace(" hz", "").trim();
                List<String> normalizedAllowedValues = allowedValues.stream()
                        .map(value -> value.toLowerCase().replace(" hz", "").trim())
                        .collect(Collectors.toList());
                if (!normalizedAllowedValues.contains(normalizedStudentValue)) {
                    return false;
                }
            }
            // Handle other attributes (exact match, case-insensitive)
            else {
                if (allowedValues.stream().noneMatch(value -> value.equalsIgnoreCase(studentValue))) {
                    return false; // No match for non-PCQ filters
                }
            }
        }
        return true; // All filters matched
    }


}
