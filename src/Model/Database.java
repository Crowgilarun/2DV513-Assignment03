package Model;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Database {

    Connection con;

    public Database(String url, String user, String password) {
        try {
            con = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error: Could not connect to the database.");
        }
    }

    public ArrayList<DAO> getList(String table) {
        ArrayList<DAO> list = new ArrayList<>();
        try {
            Statement statement = con.createStatement();
            statement.executeQuery("USE petstore");
            ResultSet set;

            switch (table) {
                case "Pet":
                    set = statement.executeQuery("SELECT * FROM petView");
                    while (set.next()) {
                        list.add(new Pet(set.getInt(1), set.getString(2), set.getDouble(3),
                                set.getDouble(4), set.getDate(5),
                                set.getString(6), set.getString(7), set.getInt(8)));
                    }
                    break;
                case "Worker":
                    set = statement.executeQuery(
                            "SELECT workerID, worker.name, phoneNumber, birthday, startDate, wage, position.name" +
                                    " FROM worker JOIN position" +
                                    " ON worker.positionID = position.positionID");
                    while (set.next()) {
                        list.add(new Worker(set.getInt(1), set.getString(2), set.getString(3),
                                set.getDate(4), set.getDate(5),
                                set.getDouble(6), set.getString(7)));
                    }
                    break;
                case "Customer":
                    set = statement.executeQuery(
                            "SELECT customer.*, COUNT(petID) " +
                                    "FROM customer LEFT JOIN pet " +
                                    "ON customerID = ownerID " +
                                    "GROUP BY customerID");
                    while (set.next()) {
                        list.add(new Customer(set.getInt(1), set.getString(2), set.getString(3), set.getInt(4)));
                    }
            }
            return list;
        } catch (Exception e) {
            if (list.size() > 0) {
                System.out.println("ERROR: Something went wrong when getting data, list may be incomplete.");
                return list;
            } else {
                System.out.println("ERROR: Unable to get any data.");
                return null;
            }
        }
    }

    public boolean createViews() {
        try {
            Statement statement = con.createStatement();
            statement.executeQuery("USE petstore");
            statement.execute("CREATE VIEW petView AS " +
                    "SELECT petID, pet.name AS `pet name`, height, weight, birthday, species.name AS `species`, " +
                    "customer.name AS `owner name`, customerID AS `owner id`" +
                    "FROM pet JOIN species ON pet.speciesID = species.speciesID " +
                    "LEFT JOIN customer ON ownerID = customerID " +
                    "ORDER BY petID ASC");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("ERROR: Could not create the necessary view in the database.");
            return false;
        }
    }

    public ArrayList<DAO> getOwnedPets(int ownerID) {
        ArrayList<DAO> ownedPets = new ArrayList<>();
        try {
            con.createStatement().executeQuery("USE petstore");
            String selectString = "SELECT * FROM petView WHERE `owner id` = ?";
            PreparedStatement statement = con.prepareStatement(selectString);
            statement.setInt(1, ownerID);
            ResultSet set = statement.executeQuery();

            while (set.next()) {
                ownedPets.add(
                        new Pet(set.getInt(1), set.getString(2), set.getDouble(3),
                                set.getDouble(4), set.getDate(5),
                                set.getString(6), set.getString(7), set.getInt(8))
                );
            }
            return ownedPets;
        } catch (Exception e) {
            if (ownedPets.size() > 0) {
                System.out.println("ERROR: Something went wrong when getting data, list may be incomplete.");
                return ownedPets;
            } else {
                System.out.println("ERROR: Unable to get any data.");
                return null;
            }
        }
    }

    public ArrayList<String> getPetCounts() {
        ArrayList<String> result = new ArrayList<>();
        try {
            Statement statement = con.createStatement();
            statement.executeQuery("USE petstore");
            String selectString =
                    "SELECT species, COUNT(petID), SUM(`owner id` IS NOT NULL) FROM petView GROUP BY species";
            ResultSet set = statement.executeQuery(selectString);

            int total;
            int owned;
            int notOwned;
            while (set.next()) {
                total = set.getInt(2);
                owned = set.getInt(3);
                notOwned = total - owned;
                result.add(set.getString(1) + ": " + total + " total, " + notOwned + " not owned, and " + owned + " owned.");
            }
            return result;
        } catch (Exception e) {
            if (result.size() > 0) {
                System.out.println("ERROR: Something went wrong when getting data, list may be incomplete.");
                return result;
            } else {
                System.out.println("ERROR: Unable to get any data.");
                return null;
            }
        }
    }

    public boolean saveDAO(DAO dao) {
        try {
            PreparedStatement statement = con.prepareStatement(dao.getInsertionString());
            dao.prepareInsertionStatement(statement);
            statement.execute();
            return true;
        } catch (Exception e) {
            System.out.println("ERROR: Unable to save to the database.");
            return false;
        }
    }

    public DAO getDAOByID(int id, String table) {
        try {
            con.createStatement().executeQuery("USE petstore");
            PreparedStatement statement;
            ResultSet set;

            switch (table) {
                case "Pet":
                    statement = con.prepareStatement("SELECT * FROM petView WHERE id = ?");
                    statement.setInt(1, id);
                    set = statement.executeQuery();
                    if (set.next())
                        return new Pet(set.getInt(1), set.getString(2), set.getDouble(3),
                                set.getDouble(4), set.getDate(5),
                                set.getString(6), set.getString(7), set.getInt(8));
                case "Worker":
                    statement = con.prepareStatement(
                            "SELECT workerID, worker.name, phoneNumber, birthday, startDate, wage, position.name" +
                                    " FROM worker JOIN position" +
                                    " ON worker.positionID = position.positionID" +
                                    "WHERE workerID = ?");
                    statement.setInt(1, id);
                    set = statement.executeQuery();
                    if (set.next())
                        return new Worker(set.getInt(1), set.getString(2), set.getString(3),
                                set.getDate(4), set.getDate(5),
                                set.getDouble(6), set.getString(7));
                case "Customer":
                    statement = con.prepareStatement(
                            "SELECT customer.*, COUNT(petID) " +
                                    "FROM customer JOIN pet " +
                                    "ON customerID = ownerID " +
                                    "WHERE customerID = ?");
                    statement.setInt(1, id);
                    set = statement.executeQuery();
                    if (set.next())
                        return new Customer(set.getInt(1), set.getString(2), set.getString(3), set.getInt(4));
            }

            //If this is thrown, then the result set did not find any rows on set.next()
            throw new Exception();
        } catch (Exception e) {
            System.out.println("ERROR: Could not find " + table + " with that ID.");
        }
        return null;
    }

    public boolean updateDAO(int id, DAO updateHolder) {
        try {
            PreparedStatement statement = con.prepareStatement(updateHolder.getUpdateString());
            updateHolder.prepareUpdateStatement(statement, id);
            statement.executeUpdate();
            return true;
        } catch (Exception e) {
            System.out.println("ERROR: Unable to update the database.");
            return false;
        }
    }

    public boolean deleteDAO(String table, DAO dao) {
        try {
            con.createStatement().executeQuery("USE petstore");
            PreparedStatement statement = con.prepareStatement(dao.getDeletionString());
            statement.setInt(1, dao.getId());
            statement.execute();
            return true;
        } catch (Exception e) {
            System.out.println("ERROR: Unable to delete " + table + " from the database.");
            return false;
        }
    }

    public boolean registerAdoption(int customerID, int petID, int workerID, double price, Date date) {
        try {
            con.createStatement().executeQuery("USE petstore");
            String insertionString =
                    "INSERT INTO adoption (`adoptionID`, `customerID`, `petID`, `workerID`, `price`, `date`)"
                            + " VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = con.prepareStatement(insertionString);
            statement.setNull(1, 4);    //Set id to null to allow it to auto increment
            statement.setInt(2, customerID);
            statement.setInt(3, petID);
            statement.setInt(4, workerID);
            statement.setDouble(5, price);
            statement.setDate(6, date);
            statement.execute();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.out.println(e.getErrorCode());
            System.out.println("ERROR: Could not register adoption in database.");
            return false;
        }
        try {
            String updateString = "UPDATE pet SET `ownerID` = ? WHERE petID = ?";
            PreparedStatement statement = con.prepareStatement(updateString);
            statement.setInt(1, customerID);
            statement.setInt(2, petID);
            statement.executeUpdate();
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("ERROR: Could not update pet owner in database.");
            return false;
        }
    }

    public String getAdoptionCounts() {
        try {
            con.createStatement().executeQuery("USE petstore");
            String selectString =
                    "SELECT pet.petID, pet.name, ownerID, COUNT(adoptionID) " +
                    "FROM pet JOIN adoption " +
                    "ON pet.petID = adoption.petID GROUP BY pet.petID";
            PreparedStatement statement = con.prepareStatement(selectString);
            ResultSet set = statement.executeQuery();
            StringBuilder result = new StringBuilder();
            while (set.next()) {
                result.append("ID: ").append(set.getInt(1));
                String name = set.getString(2);
                if (name != null && !name.equals(""))
                    result.append("\nName: ").append(name);
                int owner = set.getInt(3);
                if (owner > 0)
                    result.append("\nCurrent Owner ID: ").append(owner);
                else
                    result.append("\nCurrently Unowned");
                result.append("\nNumber of times adopted: ").append(set.getInt(4));
                result.append("\n============================\n");
            }
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Nothing to Display";
        }
    }

    public void importDumpData() {
        boolean databaseFound;
        try {
            con.createStatement().executeQuery("USE petstore");
            databaseFound = true;
        } catch (SQLException e) {
            databaseFound = false;
        }
        if (!databaseFound) {
            try {
                Statement statement = con.createStatement();
                Scanner scanner = new Scanner("CREATE DATABASE `petstore`;\n" +
                        "USE `petstore`;\n" +
                        "\n" +
                        "CREATE TABLE `customer` (\n" +
                        "  `customerID` int(11) NOT NULL AUTO_INCREMENT,\n" +
                        "  `name` varchar(45) DEFAULT NULL,\n" +
                        "  `phoneNumber` varchar(20) DEFAULT NULL,\n" +
                        "  PRIMARY KEY (`customerID`)\n" +
                        ");\n" +
                        "CREATE TABLE `species` (\n" +
                        "  `speciesID` int(11) NOT NULL,\n" +
                        "  `name` varchar(45) NOT NULL,\n" +
                        "  PRIMARY KEY (`speciesID`),\n" +
                        "  UNIQUE KEY `name_UNIQUE` (`name`)\n" +
                        ");\n" +
                        "CREATE TABLE `pet` (\n" +
                        "  `petID` int(11) NOT NULL AUTO_INCREMENT,\n" +
                        "  `name` varchar(45) DEFAULT NULL,\n" +
                        "  `height` double NOT NULL,\n" +
                        "  `weight` double NOT NULL,\n" +
                        "  `birthday` date DEFAULT NULL,\n" +
                        "  `speciesID` int(11) NOT NULL,\n" +
                        "  `ownerID` int(11) DEFAULT NULL,\n" +
                        "  PRIMARY KEY (`petID`),\n" +
                        "  FOREIGN KEY (`ownerID`) REFERENCES `customer` (`customerID`) ON DELETE SET NULL ON UPDATE NO ACTION,\n" +
                        "  FOREIGN KEY (`speciesID`) REFERENCES `species` (`speciesID`) ON DELETE NO ACTION ON UPDATE NO ACTION\n" +
                        ");\n" +
                        "CREATE TABLE `position` (\n" +
                        "  `positionID` int(11) NOT NULL,\n" +
                        "  `name` varchar(45) NOT NULL,\n" +
                        "  PRIMARY KEY (`positionID`),\n" +
                        "  UNIQUE KEY `name_UNIQUE` (`name`)\n" +
                        ");\n" +
                        "CREATE TABLE `worker` (\n" +
                        "  `workerID` int(11) NOT NULL AUTO_INCREMENT,\n" +
                        "  `name` varchar(45) NOT NULL,\n" +
                        "  `phoneNumber` varchar(20) DEFAULT NULL,\n" +
                        "  `birthday` date NOT NULL,\n" +
                        "  `startDate` date NOT NULL,\n" +
                        "  `wage` double NOT NULL,\n" +
                        "  `positionID` int(11) NOT NULL,\n" +
                        "  PRIMARY KEY (`workerID`),\n" +
                        "  FOREIGN KEY (`positionID`) REFERENCES `position` (`positionID`) ON DELETE NO ACTION ON UPDATE NO ACTION\n" +
                        ");\n" +
                        "CREATE TABLE `adoption` (\n" +
                        "  `adoptionID` int(11) NOT NULL AUTO_INCREMENT,\n" +
                        "  `customerID` int(11) NOT NULL,\n" +
                        "  `petID` int(11) NOT NULL,\n" +
                        "  `workerID` int(11) NOT NULL,\n" +
                        "  `price` double NOT NULL,\n" +
                        "  `date` datetime NOT NULL,\n" +
                        "  PRIMARY KEY (`adoptionID`),\n" +
                        "  FOREIGN KEY (`customerID`) REFERENCES `customer` (`customerID`) ON DELETE NO ACTION ON UPDATE NO ACTION,\n" +
                        "  FOREIGN KEY (`petID`) REFERENCES `pet` (`petID`) ON DELETE NO ACTION ON UPDATE NO ACTION,\n" +
                        "  FOREIGN KEY (`workerID`) REFERENCES `worker` (`workerID`) ON DELETE NO ACTION ON UPDATE NO ACTION\n" +
                        ");\n" +
                        "INSERT INTO species(speciesID, name) VALUES\n" +
                        "(0, 'Cat'),\n" +
                        "(1, 'Dog'),\n" +
                        "(2, 'Bird'),\n" +
                        "(3, 'Snake'),\n" +
                        "(4, 'Rat'),\n" +
                        "(5, 'Fish');\n" +
                        "INSERT INTO `position`(positionID, name) VALUES\n" +
                        "(0, 'Manager'),\n" +
                        "(1, 'Clerk'),\n" +
                        "(2, 'Handler');\n" +
                        "INSERT INTO worker(workerID, name, phoneNumber, birthday, startDate, wage, positionID) VALUES\n" +
                        "(null, 'Jimmy', '707230563', '1981-06-30', '2010-07-12', 20000, 0),\n" +
                        "(null, 'Tom', '727673991', '1978-09-12', '2011-12-30', 20000, 0),\n" +
                        "(null, 'Fred', '737370315', '1992-10-07', '2009-08-05', 20000, 0),\n" +
                        "(null, 'Bob', '708337728', '1985-03-12', '2020-10-15', 2000, 1),\n" +
                        "(null, 'Markus', '701298060', '1995-02-01', '2020-10-15', 2000, 1),\n" +
                        "(null, 'Fredrik', '794004261', '1987-01-23', '2019-12-01', 2000, 1),\n" +
                        "(null, 'Viktor', '728105318', '1999-12-01', '2012-01-12', 2000, 1),\n" +
                        "(null, 'Erik', '733336560', '1960-03-23', '2015-05-04', 2000, 1),\n" +
                        "(null, 'Filip', '706497826', '1992-12-03', '2017-02-12', 3000, 2),\n" +
                        "(null, 'Robert', '768951793', '1987-06-09', '2012-06-13', 3000, 2);\n" +
                        "INSERT INTO customer(customerID, name, phoneNumber) VALUES\n" +
                        "(null, 'Iris', '708746771'),\n" +
                        "(null, 'Priscilla', '738820547'),\n" +
                        "(null, 'Maria', '762844564'),\n" +
                        "(null, 'Diogenes', null),\n" +
                        "(null, 'Lorens', '727277153'),\n" +
                        "(null, 'Helena', '764208160');\n" +
                        "INSERT INTO pet(petID, name, height, weight, birthday, speciesID, ownerID) VALUES\n" +
                        "(null, 'Jabba', 12, 300, null, 1, 1),\n" +
                        "(null, 'Chewbacca', 5, 12, '2021-01-01', 1, 2),\n" +
                        "(null, 'Ikit', 2, 1, '2015-05-12', 4, null),\n" +
                        "(null, 'Bruce', 1, 0.5, '2020-08-12', 5, 3),\n" +
                        "(null, 'Fox', 1, 2, '2019-03-21', 2, 4),\n" +
                        "(null, 'Wolf', 1, 3, '2019-04-15', 2, 5),\n" +
                        "(null, 'Falco', 1, 1.5, '2019-05-12', 2, 6);\n" +
                        "INSERT INTO adoption(adoptionID, customerID, petID,workerID,price,date) VALUES\n" +
                        "(null, 1, 1, 4, 200, '2021-12-03'),\n" +
                        "(null, 2, 2, 4, 200, '2021-01-08'),\n" +
                        "(null, 3, 4, 5, 200, '2020-09-15'),\n" +
                        "(null, 4, 5, 6, 200, '2019-03-21'),\n" +
                        "(null, 5, 6, 5, 200, '2020-03-21'),\n" +
                        "(null, 6, 7, 7, '200', '2019-04-06');");
                scanner.useDelimiter(";");
                String query;

                while (scanner.hasNext()) {
                    query = scanner.next().trim();
                    if (statement != null && !query.isEmpty()) {
                        statement.execute(query);
                    }
                }
                scanner.close();
                createViews();
                if (statement != null)
                    statement.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }

    }

    public void closeDatabase() {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                System.out.println("ERROR: Not able to close database connection.");
            }
        }
    }
}
