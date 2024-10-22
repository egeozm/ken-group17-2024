package src.main.dataHandle;

import java.util.ArrayList;
import java.util.List;

public class StudentInfoManager {
    private final List<StudentInfoRecord> students;

    public StudentInfoManager() {
        students = new ArrayList<>();
    }

    public void loadStudentsFromSSV() {
        String[][] data = TwoDimensionalArray.readCsvInto2DArray("src/csvFiles/StudentInfo.csv");

        if (data != null) {
            for (int i = 1; i < data.length; i++) {
                String[] row = data[i];

                int studentID = Integer.parseInt(row[0].trim());
                String neuroSynapticInterfaceLevel = row[1].trim();
                double plasmaConductivityQuotient = Double.parseDouble(row[2].trim());

                String chronoAdaptationRateStr = row[3].trim().replace(" tau", "").trim();
                int chronoAdaptationRate = Integer.parseInt(chronoAdaptationRateStr);

                char telepathicSynchronisationIndex = row[4].trim().charAt(0);

                String aethericResonanceCapacityStr = row[5].trim().replace(" Hz", "").trim();
                double aethericResonanceCapacity = Double.parseDouble(aethericResonanceCapacityStr);

                StudentInfoRecord student = new StudentInfoRecord(studentID, neuroSynapticInterfaceLevel, plasmaConductivityQuotient, chronoAdaptationRate, telepathicSynchronisationIndex, aethericResonanceCapacity);
                students.add(student);
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
}
