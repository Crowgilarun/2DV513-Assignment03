package Model;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class DAO {
    private final int id;
    public DAO(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }
    public abstract String getInformation();
    public abstract String getInsertionString();
    public abstract void prepareInsertionStatement(PreparedStatement statement) throws SQLException;
    public abstract String getUpdateString();
    public abstract void prepareUpdateStatement(PreparedStatement statement, int id) throws SQLException;
    public abstract String getDeletionString();
}