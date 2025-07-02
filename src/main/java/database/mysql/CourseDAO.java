package database.mysql;

import model.Course;
import model.Level;
import model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * To get access to the database for CRUD functionality for courses.
 */
public class CourseDAO extends AbstractDAO implements GenericDAO<Course> {

    public CourseDAO(DBAccess dbAccess) {
        super(dbAccess);
    }

    private final UserDAO userdao = new UserDAO(dbAccess);

    /**
     * Declaring variables
     */
    private Course course = null;
    private static final String storeOneSQL = "INSERT INTO Course (courseId, course_name, course_level, " + "coordinator_username) VALUES(?, ?, ?, ?) " + "ON DUPLICATE KEY UPDATE course_level = VALUES(course_level), " + "course_name = VALUES(course_name), " + "coordinator_username = VALUES(coordinator_username)";
    private static final String getAllSQL = "SELECT * FROM Course";
    private static final String getCoursePerNameSQL = "SELECT * FROM Course WHERE course_name = ?";
    private static final String deleteOneSQL = "DELETE FROM Course WHERE courseId = ?";
    private static final String getOneByIdSQL = "SELECT * FROM Course WHERE courseId = ?";
    private static final String courseNameExistsSQL = "SELECT COUNT(*) FROM Course WHERE course_name = ? AND courseId != ?";
    private static final String getCoursesByUsername = "SELECT * FROM Course WHERE coordinator_username = ?";

    /**
     * Saving a course in database.
     */
    @Override
    public void storeOne(Course course) {
        try {
            setupPreparedStatementWithKey(storeOneSQL);
            preparedStatement.setInt(1, course.getCourseId());
            preparedStatement.setString(2, course.getCourseName());
            preparedStatement.setString(3, course.getCourseLevel().toString());
            preparedStatement.setString(4, course.getCoordinator().getUsername());
            executeInsertStatementWithKey();
        } catch (SQLException e) {
            System.out.println("Fout bij opslaan cursus: " + e.getMessage());
        }
    }

    /**
     * Gets all courses from database
     *
     * @return list with all courses
     */
    @Override
    public List<Course> getAll() {
        List<Course> courseList = new ArrayList<>();
        try {
            setupPreparedStatement(getAllSQL);
            ResultSet rs = executeSelectStatement();
            while (rs.next()) {
                Course course = new Course(rs.getInt("courseId"), Level.valueOf(rs.getString("course_level")), rs.getString("course_name"), userdao.getUserPerUsername(rs.getString("coordinator_username")));
                courseList.add(course);
            }
        } catch (SQLException e) {
            System.out.println("Fout bij ophalen van cursussen: " + e.getMessage());
        }
        return courseList;
    }

    /**
     * Getting a course by its name
     *
     * @param courseName the name for the course
     * @return course with given name
     */
    public Course getCoursePerName(String courseName) {
        try {
            setupPreparedStatement(getCoursePerNameSQL);
            preparedStatement.setString(1, courseName);
            ResultSet rs = executeSelectStatement();
            if (rs.next()) {
                course = new Course(rs.getInt("courseId"), Level.valueOf(rs.getString("course_level")), rs.getString("course_name"), userdao.getUserPerUsername(rs.getString("coordinator_username")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return course;
    }

    /**
     * Deleting a course from database.
     *
     * @param course the object that has to be removed.
     */
    public void deleteOne(Course course) {
        try {
            setupPreparedStatement(deleteOneSQL);
            preparedStatement.setInt(1, course.getCourseId());
            executeManipulateStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets a course by its id.
     *
     * @param id for the needed course.
     * @return course with that id.
     */
    @Override
    public Course getOneById(int id) {
        try {
            setupPreparedStatement(getOneByIdSQL);
            preparedStatement.setInt(1, id);
            ResultSet rs = executeSelectStatement();
            if (rs.next()) {
                course = new Course(rs.getInt("courseId"),
                        Level.valueOf(rs.getString("course_level")),
                        rs.getString("course_name"),
                        userdao.getUserPerUsername(rs.getString("coordinator_username")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return course;
    }

    /**
     * Checks if coursename already exists.
     *
     * @param courseName the name that has to be checked.
     * @param courseId   the id with that course
     * @return if course name is already used.
     */
    public boolean courseNameExists(String courseName, int courseId) {
        try {
            setupPreparedStatement(courseNameExistsSQL);
            preparedStatement.setString(1, courseName);
            preparedStatement.setInt(2, courseId);
            ResultSet rs = executeSelectStatement();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println("Fout: " + e.getMessage());
        }
        return false;
    }

    /**
     * Gets all courses where given user is coordinator.
     *
     * @param coordinator the coordinator for the courses.
     * @return list with given coordinator as coordinator.
     */
    public List<Course> getCoursesByUsername(User coordinator) {
        List<Course> courseList = new ArrayList<>();
        try {
            setupPreparedStatement(getCoursesByUsername);
            preparedStatement.setString(1, coordinator.getUsername());
            ResultSet rs = executeSelectStatement();
            while (rs.next()) {
                courseList.add(new Course(rs.getInt("courseId"),
                        Level.valueOf(rs.getString("course_level")),
                        rs.getString("course_name"),
                        userdao.getUserPerUsername(rs.getString("coordinator_username"))));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return courseList;
    }

    // toString
    @Override
    public String toString() {
        return "Cursusnaam: " + course.getCourseName() +
                "Niveau: " + course.getCourseLevel() +
                "Coördinator: " + course.getCoordinator();
    }
}
