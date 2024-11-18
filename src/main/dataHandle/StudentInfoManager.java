package src.main.dataHandle;

import java.util.ArrayList;
import java.util.List;

public class StudentInfoManager {
    private final List<StudentInfoRecord> students;

    public StudentInfoManager() {
        students = new ArrayList<>();
        loadStudentsFromCSV();
    }

    private void loadStudentsFromCSV() {
        String[][] data = TwoDimensionalArray.readCsvInto2DArray("src/csvFiles/StudentInfo.csv");

        if (data != null) {
            for (int i = 1; i < data.length; i++) {
                String[] row = data[i];

                // trim() function is removing spaces in string

                int studentID = Integer.parseInt(row[0].trim());
                String neuroSynapticInterfaceLevel = row[1].trim();
                double plasmaConductivityQuotient = Double.parseDouble(row[2].trim());

                String chronoAdaptationRateStr = row[3].trim().replace(" tau", "").trim(); // we are removing tau for exactly getting double value
                int chronoAdaptationRate = Integer.parseInt(chronoAdaptationRateStr);

                char telepathicSynchronisationIndex = row[4].trim().charAt(0);

                String aethericResonanceCapacityStr = row[5].trim().replace(" Hz", "").trim(); // we are removing Hz for exactly getting double value
                double aethericResonanceCapacity = Double.parseDouble(aethericResonanceCapacityStr);

                StudentInfoRecord student = new StudentInfoRecord(studentID, neuroSynapticInterfaceLevel, plasmaConductivityQuotient, chronoAdaptationRate, telepathicSynchronisationIndex, aethericResonanceCapacity);
                students.add(student); // creating each student with loop and adding them to students ArrayList
            }
        }
    }

    public List<StudentInfoRecord> getAllStudents() {
        return students;
    }

    public StudentInfoRecord getStudentByID(int studentID) {
        for (StudentInfoRecord student : students) {
            if (student.getStudentID() == studentID) {
                return student;
            }
        }
        return null;
    }

    public int size() {
        return students.size(); // This works
    }
}

/*

Example Usage:

StudentInfoManager manager = new StudentInfoManager();
manager.loadStudentsFromSSV();

for (StudentInfoRecord student : manager.getAllStudents()) {
    System.out.println(student);
  }

StudentInfoRecord foundStudent = manager.getStudentByID(210333);
System.out.println("Found Student: " + foundStudent);

 */
