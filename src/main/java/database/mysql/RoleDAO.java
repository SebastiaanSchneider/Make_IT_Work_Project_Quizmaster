package database.mysql;

import model.Role;

import java.sql.SQLException;
import java.util.List;

/**
 * To get access to the database for CRUD functionality for roles.
 */
public class RoleDAO extends AbstractDAO implements GenericDAO<Role>{
    DBAccess dbAccess;

    public RoleDAO(DBAccess dbAccess) {
        super(dbAccess);
    }

    // method can save a rolename in database
    public void storeOne(String roleName) {
        String sql = "INSERT IGNORE INTO Role (role_name) VALUES (?)";
        try {
            setupPreparedStatement(sql);
            preparedStatement.setString(1, roleName);
            executeManipulateStatement();
        } catch (SQLException sqlFout) {
            System.out.println("SQL fout " + sqlFout.getMessage());

        }
    } // storeOne

    @Override
    public List<Role> getAll() {
        return List.of();
    }

    @Override
    public Role getOneById(int id) {
        return null;
    }

    @Override
    public void storeOne(Role type) {

    }
}
