package src.main;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class mostCommonGrade {
    public static void main(String[] args) {
        String csvFilePath = "src/csvFiles/GraduateGrades.csv"; 
        try {
            List<String> lines = Files.readAllLines(Paths.get(csvFilePath));

            
            String header = lines.remove(0);
            String[] subjects = header.split(",");

            
            Map<String, Integer>[] gradeFrequencies = new HashMap[subjects.length];

            
            for (int i = 1; i < subjects.length; i++) {
                gradeFrequencies[i] = new HashMap<>();
            }

            
            for (String line : lines) {
                String[] values = line.split(",");
                
                for (int i = 1; i < values.length; i++) {  
                    String grade = values[i].trim();

                    
                    if (grade.equals("NG")) {
                        continue;
                    }

                    
                    gradeFrequencies[i].put(grade, gradeFrequencies[i].getOrDefault(grade, 0) + 1);
                }
            }

            
            for (int i = 1; i < subjects.length; i++) {
                String mostFrequentGrade = null;
                int maxCount = 0;

                for (Map.Entry<String, Integer> entry : gradeFrequencies[i].entrySet()) {
                    if (entry.getValue() > maxCount) {
                        mostFrequentGrade = entry.getKey();
                        maxCount = entry.getValue();
                    }
                }

                System.out.println("Subject: " + subjects[i] + ", Most common grade: " + mostFrequentGrade);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
