package Model;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Pet extends DAO {

    public String name;
    public double height;
    public double weight;
    public Date birthday;
    public String species;
    public String owner;
    public int ownerID;    //Defaults to 0 if the pet has no owner

    public Pet(int id, String name, double height, double weight, Date birthday, int species, int ownerID) {
        super(id);
        if (name != null && !name.equals(""))
            this.name = name; // Can be null
        this.height = height;
        this.weight = weight;
        this.birthday = birthday; // Can be null
        this.species = Species.values()[species].name();
        this.ownerID = ownerID;
    }

    public Pet(int id, String name, double height, double weight, Date birthday, String species, String owner, int ownerID) {
        super(id);
        if (name != null && !name.equals(""))
            this.name = name; // Can be null
        this.height = height;
        this.weight = weight;
        this.birthday = birthday; // Can be null
        this.species = species;
        this.owner = owner;
        this.ownerID = ownerID;
    }

    @Override
    public String getInformation() {
        StringBuilder information = new StringBuilder();

        information.append("ID: ").append(getId());
        if (name != null)
            information.append("\nName: ").append(name);
        information.append("\nHeight: ").append(height);
        information.append("\nWeight: ").append(weight);
        if (birthday != null)
            information.append("\nBirthday: ").append(birthday);
        information.append("\nSpecies: ").append(species);
        if (ownerID != 0)
            information.append("\nOwner: ").append(owner).append(" (ID = ").append(ownerID).append(")");

        return information.toString();
    }

    @Override
    public String getInsertionString() {
        return "INSERT INTO petstore.pet (`petID`, `name`, `height`, `weight`, `birthday`, `speciesID`, `ownerID`)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?)";
    }

    @Override
    public void prepareInsertionStatement(PreparedStatement statement) throws SQLException {
        statement.setNull(1, 4);    //Set id to null to allow it to auto increment
        statement.setString(2, name);
        statement.setDouble(3, height);
        statement.setDouble(4, weight);
        statement.setDate(5, birthday);
        statement.setInt(6, Species.valueOf(species).ordinal());
        //Pets without an owner have their ownerID default to 0 since Java doesn't do null ints
        if (ownerID > 0)
            statement.setInt(7, ownerID);
        else
            statement.setNull(7, 4);
    }

    @Override
    public String getUpdateString() {
        return "UPDATE petstore.pet "
                + "SET `name` = ?, `height` = ?, `weight` = ?, `birthday` = ?, `speciesID` = ?, `ownerID` = ?"
                + " WHERE petID = ?";
    }

    @Override
    public void prepareUpdateStatement(PreparedStatement statement, int id) throws SQLException {
        statement.setString(1, name);
        statement.setDouble(2, height);
        statement.setDouble(3, weight);
        statement.setDate(4, birthday);
        statement.setInt(5, Species.valueOf(species).ordinal());
        if (ownerID > 0)
            statement.setInt(6, ownerID);
        else
            statement.setNull(6, 4);
        statement.setInt(7, id);
    }

    @Override
    public String getDeletionString() {
        return "DELETE FROM pet WHERE petID = ?";
    }

    public enum Species {
        Cat,    // 0
        Dog,    // 1
        Bird,   // 2
        Snake,  // 3
        Rat,    // 4
        Fish    // 5
    }
}