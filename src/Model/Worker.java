package Model;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Worker extends DAO {

    public String name;
    public String phoneNumber;
    public Date birthday;
    public Date startDate;
    public double wage;
    public String position;

    public Worker(int id, String name, String phoneNumber, Date birthday, Date startDate, double wage, int position) {
        super(id);
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.birthday = birthday;
        this.startDate = startDate;
        this.wage = wage;
        this.position = Position.values()[position].name();
    }

    public Worker(int id, String name, String phoneNumber, Date birthday, Date startDate, double wage, String position) {
        super(id);
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.birthday = birthday;
        this.startDate = startDate;
        this.wage = wage;
        this.position = position;
    }

    @Override
    public String getInformation() {
        StringBuilder information = new StringBuilder();

        information.append("\nID: ").append(getId());
        information.append("\nName: ").append(name);
        if (phoneNumber != null)
            information.append("\nPhone Number: ").append(phoneNumber);
        information.append("\nBirthday: ").append(birthday);
        information.append("\nStart Date: ").append(startDate);
        information.append("\nWage: ").append(wage);
        information.append("\nPosition: ").append(position);

        return information.toString();
    }

    @Override
    public String getInsertionString() {
        return "INSERT INTO petstore.worker (`workerID`, `name`, `phoneNumber`, `birthday`, `startDate`, `wage`, `positionID`)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?)";
    }

    @Override
    public void prepareInsertionStatement(PreparedStatement statement) throws SQLException {
        statement.setNull(1, 4);    //Set id to null to allow it to auto increment
        statement.setString(2, name);
        statement.setString(3, phoneNumber);
        statement.setDate(4, birthday);
        statement.setDate(5, startDate);
        statement.setDouble(6, wage);
        statement.setInt(7, Position.valueOf(position).ordinal());
    }

    @Override
    public String getUpdateString() {
        return "UPDATE petstore.worker "
                + "SET `name` = ?, `phoneNumber` = ?, `birthday` = ?, `startDate` = ?, `wage` = ?, `positionID` = ?"
                + " WHERE workerID = ?";
    }

    @Override
    public void prepareUpdateStatement(PreparedStatement statement, int id) throws SQLException {
        statement.setString(1, name);
        statement.setString(2, phoneNumber);
        statement.setDate(3, birthday);
        statement.setDate(4, startDate);
        statement.setDouble(5, wage);
        statement.setInt(6, Position.valueOf(position).ordinal());
        statement.setInt(7, id);
    }

    @Override
    public String getDeletionString() {
        return "DELETE FROM worker WHERE workerID = ?";
    }

    enum Position {
        Manager, // 0
        Clerk, // 1
        Handler // 2
    }

}