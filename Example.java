import java.security.PublicKey;
import java.util.ArrayList;

import src.main.dataHandle.StudentInfoManager;
import src.main.dataHandle.StudentInfoRecord;

public class Example {

    public static void main(String[]args){

        StudentInfoManager manager = new StudentInfoManager();
        manager.loadStudentsFromSSV();

        int length = manager.size();

        for (int i = 0; i < students.size(); i++) {
            StudentInfoRecord student = students.get(i);
            System.out.println(student);
        }


    }

}
