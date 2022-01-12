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

    public void importDumpData(File script) {
        try {
            Statement statement = con.createStatement();
            Scanner scanner = new Scanner(script);
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
