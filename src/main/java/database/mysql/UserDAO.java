package database.mysql;

import model.Role;
import model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO extends AbstractDAO implements GenericDAO<User> {

    public UserDAO(DBAccess dbAccess) {
        super(dbAccess);
    }

    /**
     * Haalt één gebruiker op uit de database op basis van het opgegeven ID.
     */
    @Override
    public User getOneById(int id) {
        User user = null;
        String sql = "SELECT * FROM User WHERE userId = ? ";
        try {
            setupPreparedStatement(sql);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = executeSelectStatement();
            if (resultSet.next()){
                int userId = resultSet.getInt("userId");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                String firstname = resultSet.getString("first_name");
                String infix = resultSet.getString("infix");
                String lastname = resultSet.getString("last_name");
                Role role = Role.getValue(resultSet.getString("role_name"));
                user = new User(userId, username, password, firstname, infix, lastname, role);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    /**
     * Slaat een gebruiker op in de database; werkt bestaande gebruiker bij indien nodig.
     */
    @Override
    public void storeOne(User user) {
        String sql = "INSERT INTO User (userId, username, password, first_name, infix, last_name, role_name)" +
                "VALUES (?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE username = VALUES(username)," +
                "password = VALUES(password), first_name = VALUES(first_name), infix = VALUES(infix)," +
                "last_name = VALUES(last_name), role_name = VALUES(role_name)";
        try {
            setupPreparedStatementWithKey(sql);
            preparedStatement.setInt(1, user.getUserId());
            preparedStatement.setString(2, user.getUsername());
            preparedStatement.setString(3, user.getPassword());
            preparedStatement.setString(4, user.getFirstname());
            preparedStatement.setString(5, user.getInfix());
            preparedStatement.setString(6, user.getLastname());
            preparedStatement.setString(7, user.getRole().name());
            executeInsertStatementWithKey();
            System.out.println("De gebruiker met username " + user.getUsername() + " is opgeslagen.");
        } catch (SQLException sqlFout) {
            System.out.println("SQL fout " + sqlFout.getMessage());
        }
    }

    /**
     * Haalt een gebruiker op uit de database op basis van de gebruikersnaam.
     */
    public User getUserPerUsername(String username) {
        String sql = "SELECT * FROM User WHERE username = ?";
        User user = null;
        try {
            setupPreparedStatement(sql);
            preparedStatement.setString(1, username);
            ResultSet resultSet = executeSelectStatement();
            if (resultSet.next()) {
                int userId = resultSet.getInt("userId");
                String password = resultSet.getString("password");
                String firstname = resultSet.getString("first_name");
                String infix = resultSet.getString("infix");
                String lastname = resultSet.getString("last_name");
                Role role = Role.getValue(resultSet.getString("role_name"));
                user = new User(userId, username, password, firstname, infix, lastname, role);
            }
        } catch (SQLException sqlFout) {
            System.out.println("SQL fout " + sqlFout.getMessage());
        }
        return user;
    }

    /**
     * Haalt alle gebruikers uit de database op en geeft deze terug als lijst.
     */
    public List<User> getAll() {
        List<User> resultList = new ArrayList<>();
        String sql = "SELECT * FROM User";
        try {
            setupPreparedStatement(sql);
            ResultSet resultSet = executeSelectStatement();
            while (resultSet.next()) {
                int userId = resultSet.getInt("userId");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                String firstname = resultSet.getString("first_name");
                String infix = resultSet.getString("infix");
                String lastname = resultSet.getString("last_name");
                Role role = Role.getValue(resultSet.getString("role_name"));
                User user = new User(userId, username, password, firstname, infix, lastname, role);
                resultList.add(user);
            }
        } catch (SQLException sqlFout) {
            System.out.println("SQL fout " + sqlFout.getMessage());
        }
        return resultList;
    }

    /**
     * Verifieert of de combinatie van gebruikersnaam en wachtwoord bestaat.
     * Geeft een User-object terug als de combinatie geldig is, anders null.
     */
    public User authenticate(String username, String password) {
        String sql = "SELECT * FROM User WHERE username = ? AND password = ?";
        try {
            setupPreparedStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet rs = executeSelectStatement();
            if (rs.next()) {
                return new User(
                        rs.getInt("userId"), username, password,
                        rs.getString("first_name"),
                        rs.getString("infix"),
                        rs.getString("last_name"),
                        Role.getValue(rs.getString("role_name")));
            }
        } catch (SQLException ex) {
            System.out.println("SQL fout: " + ex.getMessage());
        }
        return null;
    }

    /**
     * Haalt een lijst op van alle gebruikers met een bepaalde rol.
     */
    public List<User> getUsersPerRole(Role role) {
        List<User> userList = new ArrayList<>();
        String sql = "SELECT * FROM User WHERE role_name = ?";
        try {
            setupPreparedStatement(sql);
            preparedStatement.setString(1, role.name().replace("_", " "));
            ResultSet rs = executeSelectStatement();
            while (rs.next()) {
                userList.add(new User(
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("first_name"),
                        rs.getString("infix"),
                        rs.getString("last_name"),
                        role));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return userList;
    }

    /**
     * Telt hoeveel gebruikers er zijn met een bepaalde rol.
     */
    public int countUsersPerRole(Role role) {
        String sql = "SELECT COUNT(*) FROM User WHERE role_name=?";
        try {
            setupPreparedStatement(sql);
            preparedStatement.setString(1, role.name());
            ResultSet resultSet = executeSelectStatement();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    /**
     * Verwijdert een gebruiker uit de database op basis van userId.
     */
    public void deleteOne(User user) {
        String sql = "DELETE FROM User WHERE userId = ?";
        try {
            setupPreparedStatement(sql);
            preparedStatement.setInt(1, user.getUserId());
            executeManipulateStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
