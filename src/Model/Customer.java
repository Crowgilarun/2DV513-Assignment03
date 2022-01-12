package Model;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class Customer extends DAO {

    public String name;
    public String phoneNumber;
    public int ownedPetsCount;

    public Customer(int id, String name, String phoneNumber, int pets) {
        super(id);
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.ownedPetsCount = pets;
    }

    @Override
    public String getInformation() {
        StringBuilder information = new StringBuilder();

        information.append("\nID: ").append(getId());
        information.append("\nName: ").append(name);
        if (phoneNumber != null)
            information.append("\nPhone Number: ").append(phoneNumber);
        information.append("\nNumber of owned pets: ").append(ownedPetsCount);

        return information.toString();
    }


    @Override
    public String getInsertionString() {
        return "INSERT INTO petstore.customer (`customerID`, `name`, `phoneNumber`) VALUES (?, ?, ?)";
    }

    @Override
    public void prepareInsertionStatement(PreparedStatement statement) throws SQLException {
        statement.setNull(1, 4);    //Set id to null to allow it to auto increment
        statement.setString(2, name);
        statement.setString(3, phoneNumber);
    }

    @Override
    public String getUpdateString() {
        return "UPDATE petstore.customer "
                + "SET `name` = ?, `phoneNumber` = ?"
                + " WHERE customerID = ?";
    }

    @Override
    public void prepareUpdateStatement(PreparedStatement statement, int id) throws SQLException {
        statement.setString(1, name);
        statement.setString(2, phoneNumber);
        statement.setInt(3, id);
    }

    @Override
    public String getDeletionString() {
        return "DELETE FROM customer WHERE customerID = ?";
    }
}