import controller.Controller;
import Model.Database;
import view.*;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        Database database = new Database("jdbc:mysql://localhost:3306/", "root", "root");
        //For first time running, uncomment this line to set up the database
        //setUpDatabase(database);
        Console view = new Console();
        Controller controller = new Controller();
        controller.runProgram(view, database);
    }

    public static void setUpDatabase(Database database) {
        String filepath = "src/Datadump.txt";
        File script = new File(filepath);
        database.importDumpData(script);
    }

}