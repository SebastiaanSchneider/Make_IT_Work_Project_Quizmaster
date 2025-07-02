package database.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Course;
import model.Group;
import model.User;

/**
 * DAO providing MySQL database funtions related to Group objects
 * @author Sebastiaan Schneider
 */
public class GroupDAO extends AbstractDAO implements GenericDAO<Group> {
    // connect to database
    public GroupDAO(DBAccess dbAccess) {
        super(dbAccess);
    }
    UserDAO userDAO = new UserDAO(dbAccess);
    CourseDAO courseDAO = new CourseDAO(dbAccess);

    // store Group object to database
    @Override
    public void storeOne(Group group) {
        String sql = "INSERT INTO `Group` (groupId, group_name, max_groupsize, course_name," +
                "teacher_username)  VALUES(?, ?, ?, ?, ?)" +
                "ON DUPLICATE KEY UPDATE group_name = VALUES(group_name), " +
                "max_groupsize = VALUES(max_groupsize), " +
                "course_name = VALUES(course_name), " +
                "teacher_username = VALUES(teacher_username);";

        try {
            setupPreparedStatementWithKey(sql);
            preparedStatement.setInt(1, group.getGroupId());
            preparedStatement.setString(2, group.getName());
            preparedStatement.setInt(3, group.getCapacity());
            preparedStatement.setString(4, group.getCourse().getCourseName());
            preparedStatement.setString(5, group.getTeacher().getUsername());
            executeInsertStatementWithKey();
        } catch (SQLException sqlFout) {
            sqlFout.printStackTrace();
            System.out.println(sqlFout.getMessage());
        }
    }

    // get all Group objects from database
    @Override
    public List<Group> getAll() {
        List<Group> groupList = new ArrayList<>();
        String sql = "SELECT * FROM `Group`;";

        try {
            setupPreparedStatement(sql);
            try (ResultSet resultSet = executeSelectStatement()) {

                while (resultSet.next()) {
                    Group group = new Group(
                            resultSet.getInt("groupId"),
                            resultSet.getString("group_name"),
                            resultSet.getInt("max_groupsize"),
                            userDAO.getUserPerUsername(resultSet.getString("teacher_username")),
                            courseDAO.getCoursePerName(resultSet.getString("course_name")));
                    groupList.add(group);
                }
            }
        } catch (SQLException foutmelding) {
            System.out.println(foutmelding.getMessage());
        }
        return groupList;
    }

    // get specific Group object by ID
    @Override
    public Group getOneById(int id) {
        String sql = "SELECT * FROM `Group` WHERE groupId = ?";
        Group group = null;

        try {
            setupPreparedStatement(sql);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = executeSelectStatement();

            if (resultSet.next()) {
                group = getGroup(id, resultSet);
            }
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
        return group;
    }

    // get specific Group object by name
    public Group getGroupByName(String groupName) {
        String sql = "SELECT * FROM `Group` WHERE group_name = ?";
        Group group = null;

        try {
            setupPreparedStatement(sql);
            preparedStatement.setString(1, groupName);
            ResultSet resultSet = executeSelectStatement();

            if (resultSet.next()) {
                int groupId = resultSet.getInt("groupId");
                group = getGroup(groupId, resultSet);
            }
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
        return group;
    }

    // get all Group object belonging to a specific Course
    public List<Group> getAllGroupsPerCourse(Course course) {
        String sql = "SELECT * FROM `Group` WHERE course_name = ? ";
        List<Group> groupList = new ArrayList<>();
        try {
            setupPreparedStatement(sql);
            preparedStatement.setString(1, course.getCourseName());
            ResultSet resultSet = executeSelectStatement();

            while (resultSet.next()) {
                int groupId = resultSet.getInt("groupId");
                groupList.add(getGroup(groupId, resultSet));
            }
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
        return groupList;
    }

    // delete Group object from database
    public void deleteOne(Group group) {
        String sql = "DELETE FROM `Group` WHERE groupId = ?";

        try {
            setupPreparedStatement(sql);
            preparedStatement.setInt(1, group.getGroupId());
            executeManipulateStatement();
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
            throw new RuntimeException(exception);
        }
    }

    // create Group object to use in getOneById and getGroupByName
    private Group getGroup(int id, ResultSet resultSet) throws SQLException {
        Group group;
        String name = resultSet.getString("group_name");
        int capacity = resultSet.getInt("max_groupsize");
        User user = userDAO.getUserPerUsername(resultSet.getString("teacher_username"));
        Course course = courseDAO.getCoursePerName(resultSet.getString("course_name"));
        group = new Group(id, name, capacity,user, course);
        return group;
    }

    // count groups per course for ManageGroupsController
    public int countNumberOfGroups(Group group) {
        int numberOfGroups = 0;
        String sql = "SELECT COUNT(*) FROM `Group` WHERE course_name = ?";

        try {
            setupPreparedStatement(sql);
            preparedStatement.setString(1,
                    group.getCourse().getCourseName());
            try (ResultSet resultSet = executeSelectStatement()) {
                if (resultSet.next()) {
                    numberOfGroups = resultSet.getInt(1);
                }
            }
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
        return numberOfGroups;
    }
}
