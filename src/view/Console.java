package view;

import Model.Customer;
import Model.DAO;
import Model.Pet;
import Model.Worker;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Console {
    private String getStringInput() {
        return new Scanner(System.in).nextLine();
    }

    private int getIntInput() {
        try {
            return new Scanner(System.in).nextInt();
        } catch (InputMismatchException ex) {
            return -1;
        }
    }

    private double getDoubleInput() {
        try {
            return new Scanner(System.in).nextDouble();
        } catch (InputMismatchException ex) {
            return -1;
        }
    }

    private void printSmallSeparator() {
        System.out.println("============================");
    }

    private void printLargeSeparator() {
        System.out.println("==================================================");
    }

    // Might be removed
    private void confirmContinue() {
        System.out.println("\nPress Enter To Continue");
        try {
            System.in.read();         //Wait for confirmation before continuing
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getSubPage() {
        printSmallSeparator();
        System.out.println("Welcome to our pet store!");
        printSmallSeparator();

        System.out.println("\n1. Manage Pets" +
                "\n2. Manage Employees" +
                "\n3. Manage Customers" +
                "\n0. Quit\n");

        String selection = null;

        while (selection == null) {
            switch (getIntInput()) {
                case 0:
                    selection = "Exit";
                    break;
                case 1:
                    selection = "Pet";
                    break;
                case 2:
                    selection = "Worker";
                    break;
                case 3:
                    selection = "Customer";
                    break;
                default:
                    System.out.println("Invalid menu option");
            }
        }

        return selection;
    }

    public void displaySubPage(String pageName) {
        System.out.println("\n1. View " + pageName + "s" +
                "\n2. Add New " + pageName +
                "\n3. Update " + pageName + " Details" +
                "\n4. Delete " + pageName +
                (pageName.equals("Customer") ? "\n5. View Owned Pets" : "") +
                (pageName.equals("Pet") ? "\n5. View Pet Counts\n6. Register Adoption\n7. View Adoption Counts" : "") +
                "\n0. Back");
    }

    public MenuOption getPageOption() {
        MenuOption selection = null;
        while (selection == null) {
            switch (getIntInput()) {
                case 0:
                    selection = MenuOption.Back;
                    break;
                case 1:
                    selection = MenuOption.SeeDetails;
                    break;
                case 2:
                    selection = MenuOption.Add;
                    break;
                case 3:
                    selection = MenuOption.Change;
                    break;
                case 4:
                    selection = MenuOption.Delete;
                    break;
                case 5:
                    selection = MenuOption.SeeCustomerPage;
                    break;
                case 6:
                    selection = MenuOption.RegisterAdoption;
                    break;
                case 7:
                    selection = MenuOption.SeeAdoptionCounts;
                    break;
                default:
                    System.out.println("Invalid menu option.");
            }
        }

        return selection;
    }

    public String requestPetName() {
        System.out.println("\nPlease Enter the Pet's Name, or Press Enter If they don't have one yet: ");
        return getStringInput();
    }

    public double requestPetHeight() {
        System.out.println("\nPlease Enter the Pet's Height in Centimeters: ");
        double input = getDoubleInput();
        if (input == -1) {
            System.out.print("Invalid Input, Please Format the Number As '12' Or '34.56'.");
            return requestPetHeight();
        }
        return input;
    }

    public double requestPetWeight() {
        System.out.println("\nPlease Enter the Pet's Weight in Grams: ");
        double input = getDoubleInput();
        if (input == -1) {
            System.out.print("Invalid Input, Please Format the Number As '12' Or '34.56'.");
            return requestPetWeight();
        }
        return input;
    }

    public Date requestPetBirthday() {
        // SQL Date format = YYYY-MM-DD
        System.out.println("\nPlease Enter the Pet's Birthdate, or Press Enter If Unknown: ");
        String dateString = getStringInput();
        if (dateString.equals(""))
            return null;
        try {
            return Date.valueOf(dateString);
        } catch (IllegalArgumentException e) {
            System.out.print("Invalid Input, Please Format the Date As 'YYYY-MM-DD'.");
            return requestPetBirthday();
        }
    }

    public int requestPetSpecies() {
        System.out.println("\nPlease Enter the Pet's Species: " +
                "\n0: Cat" +
                "\n1: Dog" +
                "\n2: Bird" +
                "\n3: Snake" +
                "\n4: Rat" +
                "\n5: Fish");

        int input = getIntInput();
        if (input < 0 || input >= 6) {
            System.out.print("Invalid Input, Please Choose a Value From the List.");
            return requestPetSpecies();
        }

        return input;
    }

    public int requestPetOwnerID() {
        System.out.println("\nPlease Enter the Pet's Owner ID, or -1 If they don't have one yet: ");
        return getIntInput();
    }

    public String requestWorkerName() {
        System.out.println("\nPlease Enter the Worker's Name: ");
        String input = getStringInput();
        if (input.equals("")) {
            System.out.print("Invalid Name.");
            return requestWorkerName();
        }
        return input;
    }

    public String requestWorkerPhoneNumber() {
        System.out.println("\nPlease Enter the Worker's Phone Number, or Press Enter If they don't have one: ");
        String input = getStringInput();

        if (input.length() == 0)
            return input;
        //This regex makes sure the input only has numbers and dashes, and doesn't have a dash at the start or end
        if (!input.matches("^\\d[\\d-]+\\d$")) {
            System.out.print("Invalid Phone Number, Please Only Use Numbers and Dashes, Like `123-4567`.");
            return requestWorkerPhoneNumber();
        }
        if (input.length() < 7) {
            System.out.print("Invalid Phone Number, Must Be At Least 7 Digits.");
            return requestWorkerPhoneNumber();
        }
        return input;
    }

    public Date requestWorkerBirthday() {
        System.out.println("\nPlease Enter the Worker's Birthdate: ");
        String dateString = getStringInput();
        try {
            return Date.valueOf(dateString);
        } catch (IllegalArgumentException e) {
            System.out.print("Invalid Input, Please Format the Date As 'YYYY-MM-DD'.");
            return requestWorkerBirthday();
        }
    }

    public Date requestWorkerStartDate() {
        System.out.println("\nPlease Enter the Date the Worker Began Their Employment: ");
        String dateString = getStringInput();
        try {
            return Date.valueOf(dateString);
        } catch (IllegalArgumentException e) {
            System.out.print("Invalid Input, Please Format the Date As 'YYYY-MM-DD'.");
            return requestWorkerStartDate();
        }
    }

    public double requestWorkerWage() {
        System.out.println("\nPlease Enter the Worker's Hourly Wage: ");
        double input = getDoubleInput();
        if (input < 0) {
            System.out.print("Invalid Input, Please Format the Number As '12' Or '34.56'.");
            return requestWorkerWage();
        }
        return input;
    }

    public int requestWorkerPosition() {
        System.out.println("\nPlease Enter the Worker's Position: " +
                "\n0: Manager" +
                "\n1: Clerk" +
                "\n2: Handler");

        int input = getIntInput();
        if (input < 0 || input >= 3) {
            System.out.print("Invalid Input, Please Choose a Value From the List.");
            return requestWorkerPosition();
        }
        return input;
    }

    public boolean confirmSelection(DAO dao) {
        System.out.println(dao.getInformation());
        System.out.print("Continue With This Selection?\nY/N: ");
        return getStringInput().equalsIgnoreCase("y");
    }

    public String requestCustomerName() {
        System.out.println("\nPlease Enter the Customer's Name: ");
        String input = getStringInput();
        if (input.equals("")) {
            System.out.print("Invalid Name.");
            return requestCustomerName();
        }
        return input;
    }

    public String requestCustomerPhoneNumber() {
        System.out.println("\nPlease Enter the Customer's Phone Number, or Press Enter If they don't have one: ");
        String input = getStringInput();

        if (input.length() == 0)
            return input;
        //This regex makes sure the input only has numbers and dashes, and doesn't have a dash at the start or end
        if (!input.matches("^\\d[\\d-]+\\d$")) {
            System.out.print("Invalid Phone Number, Please Only Use Numbers and Dashes, Like `123-4567`.");
            return requestCustomerPhoneNumber();
        }
        if (input.length() < 7) {
            System.out.print("Invalid Phone Number, Must Be At Least 7 Digits.");
            return requestCustomerPhoneNumber();
        }
        return input;
    }

    public void displayList(ArrayList<DAO> daoList) {
        if (daoList.size() == 0)
            System.out.println("Nothing to display.");
        for (DAO dao : daoList) {
            printSmallSeparator();
            System.out.println(dao.getInformation());
        }
        confirmContinue();
    }

    public DAO requestInformation(String pageName) {
        switch (pageName) {
            case "Pet":
                return new Pet(0, requestPetName(), requestPetHeight(), requestPetWeight(),
                        requestPetBirthday(), requestPetSpecies(), requestPetOwnerID());
            case "Worker":
                return new Worker(0, requestWorkerName(), requestWorkerPhoneNumber(), requestWorkerBirthday(),
                        requestWorkerStartDate(), requestWorkerWage(), requestWorkerPosition());
            case "Customer":
                return new Customer(0, requestCustomerName(), requestCustomerPhoneNumber(), 0);
            default:
                return null;
        }
    }

    public int requestID(String pageName) {
        System.out.println("Please Enter the ID of the " + pageName + ": ");
        int input = getIntInput();
        if (input < 0) {
            System.out.print("Invalid Input, Please Input the ID As a Single Number.");
            return requestID(pageName);
        }
        return input;
    }

    public void displayPetCounts(ArrayList<String> counts) {
        printSmallSeparator();
        for (String count : counts) {
            System.out.println(count);
        }
        printSmallSeparator();
        confirmContinue();
    }

    public double requestPrice() {
        System.out.println("Please Enter the Price/Fee of the Adoption: ");
        double input = getDoubleInput();
        if (input < 0) {
            System.out.println("Invalid Input, Please Format the Number As '12' Or '34.56'.");
            return requestPrice();
        }
        return input;
    }

    public Date requestAdoptionDate() {
        System.out.println("\nPlease Enter the Date of the Adoption: ");
        String dateString = getStringInput();
        try {
            return Date.valueOf(dateString);
        } catch (IllegalArgumentException e) {
            System.out.print("Invalid Input, Please Format the Date As 'YYYY-MM-DD'.");
            return requestWorkerStartDate();
        }
    }

}