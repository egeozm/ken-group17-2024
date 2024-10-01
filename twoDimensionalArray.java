import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;  

public class twoDimensionalArray {

    public static void main(String[]args){

        String csvFilePath = "src/main/GraduateGrades.csv";

        String [][] csvData = ReadCsvInto2DArray(csvFilePath,34);

        for (int i = 0; i < 1; i++ ){

            System.out.println(String.join(",",csvData[i]));

        }

    }

     public static String[][] ReadCsvInto2DArray(String csvFile, int amountOfFields){

        List<String> recordList = new ArrayList<String>();
        String seperate = ",";
        String currentLine; 
        
        try{

            FileReader fr = new FileReader(csvFile);
            BufferedReader br = new BufferedReader(fr);

            while((currentLine = br.readLine()) != null){

                recordList.add(currentLine);
            }

            int recordCount = recordList.size();

            String arrayToReturn[][] = new String[recordCount][amountOfFields];
            String[] data;

            for(int i = 0; i< recordCount; i++){

                data = recordList.get(i).split(seperate);
                for(int j = 0; j<data.length; j++){

                    arrayToReturn[i][j] = data[j];
                    
                }
           
            }

            return arrayToReturn;

        }
        catch(Exception e)
        {
            System.out.println(e);
            return null;

        }
     }    
}
