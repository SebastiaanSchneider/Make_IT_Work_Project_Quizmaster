package database.mysql;

import model.Course;
import model.Registration;
import model.StudentGroup;
import model.User;
import view.Main;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * To get access to the database for CRUD functionality for studentgroup.
 */
public class StudentGroupDAO extends AbstractDAO implements GenericDAO<StudentGroup> {

    public StudentGroupDAO(DBAccess dbAccess) {
        super(dbAccess);
    }

    private final static DBAccess dbAccess = Main.getDBaccess();
    private final static UserDAO userDAO = new UserDAO(dbAccess);

    // SQL statements in constanten
    private final static String storeOneSQL = "INSERT INTO StudentGroup VALUES (?,?)";
    private final static String deleteOneSQL = "DELETE FROM StudentGroup WHERE userId = ? AND groupId = ?";
    private final static String getUsersPerGroupSQL = "SELECT userId FROM StudentGroup WHERE groupId = ?";
    private final static String getusersWithoutGroupSQL = "SELECT r.userId FROM Registration r " +
            "WHERE r.courseId = ? " +
            "AND r.userId NOT IN (" +
            "   SELECT sg.userId FROM StudentGroup sg " +
            "   JOIN `Group` g ON sg.groupId = g.groupId " +
            "   WHERE g.course_name = ?" +
            ")";

    /**
     * Stores a StudentGroup in database
     *
     * @param studentGroup that has to be saved.
     */
    @Override
    public void storeOne(StudentGroup studentGroup) {
        try {
            setupPreparedStatement(storeOneSQL);
            preparedStatement.setInt(1, studentGroup.getUserId());
            preparedStatement.setInt(2, studentGroup.getGroupId());
            executeManipulateStatement();
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
    }

    /**
     * Deletes a studentgroup from database
     *
     * @param studentGroup that has to be deleted.
     */
    public void deleteOne(StudentGroup studentGroup) {
        try {
            setupPreparedStatement(deleteOneSQL);
            preparedStatement.setInt(1, studentGroup.getUserId());
            preparedStatement.setInt(2, studentGroup.getGroupId());
            executeManipulateStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Gets all users within a group
     *
     * @param groupId from group
     * @return
     */
    public List<User> getAllUsersPerStudentGroupId(int groupId) {
        UserDAO userDAO = new UserDAO(dbAccess);
        List<User> students = new ArrayList<>();
        try {
            setupPreparedStatement(getUsersPerGroupSQL);
            preparedStatement.setInt(1, groupId);
            ResultSet rs = executeSelectStatement();
            while (rs.next()) {
                User student = userDAO.getOneById(rs.getInt("userId"));
                students.add(student);
            }
        } catch (SQLException foutmelding) {
            System.out.println("Fout bij ophalen van inschrijvingen: " + foutmelding.getMessage());
        }
        return students;
    }

    /**
     * Gets all users with registration but without a group
     *
     * @param course where registrations are
     * @return list with students
     */
    public List<User> getUsersWithoutGroup(Course course) {
        List<User> students = new ArrayList<>();
        try {
            setupPreparedStatement(getusersWithoutGroupSQL);
            preparedStatement.setInt(1, course.getCourseId());
            preparedStatement.setString(2, course.getCourseName());
            ResultSet rs = executeSelectStatement();
            while (rs.next()) {
                User student = userDAO.getOneById(rs.getInt("userId"));
                students.add(student);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return students;
    }

    @Override
    public List<StudentGroup> getAll() {
        return List.of();
    }

    @Override
    public StudentGroup getOneById(int id) {
        return null;
    }

}
