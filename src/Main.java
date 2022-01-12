import controller.Controller;
import Model.Database;
import view.*;

public class Main {
    public static void main(String[] args) {
        Database database = new Database("jdbc:mysql://localhost:3306/", "root", "root");
        database.importDumpData();
        Console view = new Console();
        Controller controller = new Controller();
        controller.runProgram(view, database);
    }
}