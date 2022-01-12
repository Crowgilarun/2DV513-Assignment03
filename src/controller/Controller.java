package controller;

import Model.*;

public class Controller {

    private view.Console view;
    private Database database;

    public void runProgram(view.Console view, Database database) {
        this.view = view;
        this.database = database;

        while (openMainPage()) ;    //Repeat until user exits
        database.closeDatabase();   //Close the database connection when finished
    }

    private boolean openMainPage() {
        String pageName = view.getSubPage();
        if (pageName.equals("Exit"))
            return false;

        while (openSubPage(pageName)) ;
        return true;
    }

    private boolean openSubPage(String pageName) {
        DAO dao;
        view.displaySubPage(pageName);
        switch (view.getPageOption()) {
            case SeeDetails:
                view.displayList(database.getList(pageName));
                return true;
            case Add:
                if (database.saveDAO(view.requestInformation(pageName)))
                    System.out.println("Successfully Added " + pageName + " To Database");
                return true;
            case Change:
                dao = database.getDAOByID(view.requestID(pageName), pageName);
                if (dao != null)
                    if (view.confirmSelection(dao))
                        if (database.updateDAO(dao.getId(), view.requestInformation(pageName)))
                            System.out.println("Successfully Updated " + pageName + " Information");
                return true;
            case Delete:
                dao = database.getDAOByID(view.requestID(pageName), pageName);
                if (dao != null)
                    if (view.confirmSelection(dao))
                        if (database.deleteDAO(pageName, dao))
                            System.out.println("Successfully Deleted " + pageName + " From Database");
                return true;
            case SeeCustomerPage:
                if (pageName.equals("Customer"))
                    view.displayList(database.getOwnedPets(view.requestID("Customer")));
                else if (pageName.equals("Pet"))
                    view.displayPetCounts(database.getPetCounts());
                return true;
            case RegisterAdoption:
                if (pageName.equals("Pet")) {
                    if (database.registerAdoption(view.requestID("Customer"), view.requestID("Pet"),
                            view.requestID("Worker"), view.requestPrice(), view.requestAdoptionDate()))
                        System.out.println("Successfully Registered Adoption");
                }
                return true;
            case SeeAdoptionCounts:
                if (pageName.equals("Pet"))
                    System.out.println(database.getAdoptionCounts());
                return true;
            default:
                return false;
        }
    }
}