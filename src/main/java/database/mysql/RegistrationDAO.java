package database.mysql;

import model.Course;
import model.Registration;
import model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * To get access to the database for CRUD functionality for registrations.
 */
public class RegistrationDAO extends AbstractDAO implements GenericDAO<Registration> {

    // access databases
    private final UserDAO userdao = new UserDAO(dbAccess);
    private final CourseDAO courseDAO = new CourseDAO(dbAccess);

    public RegistrationDAO(DBAccess dbAccess) {
        super(dbAccess);
    }

    // constanten SQL
    private final static String errorSQL = "Fout: ";
    private final static String deleteOneSQL =
            "DELETE FROM Registration WHERE userId = ? AND courseId = ?";
    private final static String storeOneSQL =
            "INSERT INTO Registration (userId, courseId) VALUES(?, ?)";
    private final static String getAllCoursesPerUserSQL =
            "SELECT courseId FROM Registration WHERE userId = ?";
    private final static String getAllUsersPerCourseSQL =
            "SELECT userId FROM Registration WHERE courseId = ?";

    @Override
    public List<Registration> getAll() {
        return null;
    }

    /**
     * Gets all registrations for user.
     *
     * @param student the user logged-in.
     * @return list with all courses with registrations.
     */
    public List<Course> getAllCoursesPerUser(User student) {
        List<Course> registrations = new ArrayList<>();
        try {
            setupPreparedStatement(getAllCoursesPerUserSQL);
            preparedStatement.setInt(1, student.getUserId());
            ResultSet rs = executeSelectStatement();
            while (rs.next()) {
                Course course = courseDAO.getOneById(rs.getInt("courseId"));
                registrations.add(course);
            }
        } catch (SQLException e) {
            System.out.println(errorSQL + e.getMessage());
        }
        return registrations;
    }

    /**
     * Gets all users with registrations to course.
     *
     * @param course the course searched for
     * @return list with users registrated to the course.
     */
    public List<User> getAllUsersPerCourse(Course course) {
        List<User> students = new ArrayList<>();
        try {
            setupPreparedStatement(getAllUsersPerCourseSQL);
            preparedStatement.setInt(1, course.getCourseId());
            ResultSet rs = executeSelectStatement();
            while (rs.next()) {
                User student = userdao.getOneById(rs.getInt("userId"));
                students.add(student);
            }
        } catch (SQLException e) {
            System.out.println(errorSQL + e.getMessage());
        }
        return students;
    }

    @Override
    public Registration getOneById(int id) {
        return null;
    }

    /**
     * Saves a registration in database (sign in).
     *
     * @param registration that needs to be saved.
     */
    @Override
    public void storeOne(Registration registration) {
        try {
            setupPreparedStatement(storeOneSQL);
            preparedStatement.setInt(1, registration.getStudent().getUserId());
            preparedStatement.setInt(2, registration.getCourse().getCourseId());
            executeManipulateStatement();
        } catch (SQLException e) {
            System.out.println(errorSQL + e.getMessage());
        }
    }

    /**
     * Deletes a registration (sign out).
     *
     * @param registration that has to be deleted.
     */
    public void deleteOne(Registration registration) {
        try {
            setupPreparedStatement(deleteOneSQL);
            preparedStatement.setInt(1, registration.getStudent().getUserId());
            preparedStatement.setInt(2, registration.getCourse().getCourseId());
            executeManipulateStatement();
        } catch (SQLException e) {
            System.out.println(errorSQL + e.getMessage());
        }
    }
}
