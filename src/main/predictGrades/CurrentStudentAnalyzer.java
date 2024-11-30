package src.main.predictGrades;

import java.util.ArrayList;

public class CurrentStudentAnalyzer {
    private String [][] gradesData;
    private String [][] infoData;
    ArrayList<CurrentStudent> currentStudents = new ArrayList<>();
    public CurrentStudentAnalyzer(String [][] gradesData, String [][] infoData) {
        this.gradesData = gradesData;
        this.infoData = infoData;


        // Extracting grades from the CurrentStudent.csv file
        for (int i = 1; i < gradesData.length; i++) {
            ArrayList<Double> grades = new ArrayList<>();
            for (int j = 1; j < gradesData[i].length; j++) {
                if (gradesData[i][j].contentEquals("NG")) {
                    grades.add(null);
                }else {
                    grades.add(Double.parseDouble(gradesData[i][j]));
                }
            }
            ArrayList<String> traits = new ArrayList<>();
            for (int k = 1; k < infoData[i].length; k++) {
                traits.add(infoData[i][k]);
            }
            CurrentStudent cStudent = new CurrentStudent(Integer.parseInt(gradesData[i][0]), grades, traits);
            cStudent.setYearOfStudy(findYearOfStudy(grades));
            currentStudents.add(cStudent);
        }
    }

    public int findYearOfStudy(ArrayList<Double> grades){
        CourseAnalyzer courseAnalyzer = new CourseAnalyzer(gradesData, gradesData[0]);
        ArrayList<Course> secondYearCourses = courseAnalyzer.getCoursesByYear(2);
        ArrayList<Course> thirdYearCourses = courseAnalyzer.getCoursesByYear(3);
        ArrayList<Integer> indexesOfSecondYearCourses = new ArrayList<>();
        ArrayList<Integer> indexesOfThirdYearCourses = new ArrayList<>();

        for (Course c : secondYearCourses) {
            indexesOfSecondYearCourses.add(c.getCourseID());
        }
        for (Course c : thirdYearCourses) {
            indexesOfThirdYearCourses.add(c.getCourseID());
        }
        int c = 0;
        for (Double grade: grades) {
            if (grade != null) {
                if (indexesOfThirdYearCourses.contains(c)) {
                    c = 0;
                    return 3;
                }
            }
            c++;
        }
        for (Double grade: grades) {
            if (grade != null) {
                if (indexesOfSecondYearCourses.contains(grades.indexOf(grade))) {
                    return 2;
                }
            }
        }
        return 1;
    }

    public ArrayList<CurrentStudent> getCurrentStudentsByYear(int year) {
        ArrayList<CurrentStudent> result = new ArrayList<>();
        for (CurrentStudent cStudent : currentStudents) {
            if (cStudent.getYearOfStudy() == year) {
                result.add(cStudent);
            }
        }
        return result;
    }

    public ArrayList<CurrentStudent> getAllCurrentStudents() {
        return currentStudents;
    }


    public CurrentStudent getCurrentStudentByID(int id) {
        for (CurrentStudent cStudent : currentStudents) {
            if (cStudent.getStudentID() == id) {
                return cStudent;
            }
        }
        return null;
    }
}
