package controller;

import database.mysql.*;
import javacouchdb.CouchDBAccess;
import javacouchdb.QuizResultCouchDBDAO;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.*;
import view.Main;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Functionality to assign or remove registrated students to group.
 */
public class AssignStudentsToGroupController {

    @FXML
    private ComboBox<Course> courseComboBox;
    @FXML
    private ComboBox<Group> groupComboBox;
    @FXML
    ListView<User> studentList;
    @FXML
    private ListView<User> studentsInGroupList;
    @FXML
    private Label numberOfStudents;
    @FXML
    private Label nameRoleLabel;

    // access to database
    private final static DBAccess dbAccess = Main.getDBaccess();
    private final static CouchDBAccess couchDBAccess = Main.getCouchDBAccess();
    private final static CourseDAO courseDAO = new CourseDAO(dbAccess);
    private final static GroupDAO groupDAO = new GroupDAO(dbAccess);
    private final static StudentGroupDAO studentgroupDAO = new StudentGroupDAO(dbAccess);
    private final User user = WelcomeController.getUser();
    private final static UserDAO userDAO = new UserDAO(dbAccess);
    private final static QuizResultCouchDBDAO resultDAO = new QuizResultCouchDBDAO(couchDBAccess);

    // setting up scene
    public void setup() {
        nameRoleLabel.setText(user.getFullName() + "\nRol: " + user.getRole().toString());
        setUpCourses();
    }

    /**
     * Gets all courses and fills list.
     */
    public void setUpCourses() {
        courseComboBox.getItems().clear();
        courseComboBox.setCellFactory(list -> createCourseListCell());
        courseComboBox.setButtonCell(createCourseListCell());
        courseComboBox.getItems().addAll(courseDAO.getAll());
        courseComboBox.getSelectionModel().selectedItemProperty().addListener(
                (observableValue, oldCourse, newCourse) -> {
                    updateStudentList(newCourse);
                    setUpGroups(newCourse);
                });
    }

    /**
     * Gets all groups and fills list.
     */
    public void setUpGroups(Course course) {
        groupComboBox.getItems().clear();
        groupComboBox.setCellFactory(list -> createGroupListCell());
        groupComboBox.setButtonCell(createGroupListCell());
        groupComboBox.getItems().addAll(groupDAO.getAllGroupsPerCourse(course));
        groupComboBox.getSelectionModel().selectedItemProperty().addListener(
                (observableValue, oldGroup, newGroup) -> {
                    updateStudentsInGroups(newGroup);
                });
    }

    /**
     * Updates the list with students already in groups.
     */
    public void updateStudentsInGroups(Group group) {
        studentsInGroupList.getItems().clear();
        studentsInGroupList.getItems().addAll(studentgroupDAO.getAllUsersPerStudentGroupId
                (group.getGroupId()));
        studentsInGroupList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        int aantal = studentgroupDAO.getAllUsersPerStudentGroupId(group.getGroupId()).size();
        numberOfStudents.setText("Aantal studenten: " + aantal);
    }

    /**
     * Updates the list with students without group.
     */
    public void updateStudentList(Course course) {
        studentList.getItems().clear();
        studentList.getItems().addAll(studentgroupDAO.getUsersWithoutGroup(course));
        studentList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    /**
     * Decides what is shown within courselist.
     *
     * @return list with courses
     */
    private ListCell<Course> createCourseListCell() {
        return new ListCell<>() {
            @Override
            protected void updateItem(Course course, boolean empty) {
                super.updateItem(course, empty);
                setText((empty || course == null) ? null : "Cursus: " + course.getCourseName() +
                        " Niveau: " + course.getCourseLevel());
            }
        };
    }

    /**
     * Decides what is shown within groupList.
     */
    private ListCell<Group> createGroupListCell() {
        return new ListCell<>() {
            @Override
            protected void updateItem(Group group, boolean empty) {
                super.updateItem(group, empty);
                setText((empty || group == null) ? null : group.getName() + " Cursus: " +
                        group.getCourse().getCourseName());
            }
        };
    }

    /**
     * Assigns students to groups.
     */
    public void doAssign() {
        ObservableList<User> selectedStudents = studentList.getSelectionModel().getSelectedItems();
        Group group = groupComboBox.getSelectionModel().getSelectedItem();
        if (selectedStudents.isEmpty() || groupComboBox.getSelectionModel().getSelectedItem() == null) {
            showWarning().showAndWait();
            return;
        }
        if (studentgroupDAO.getAllUsersPerStudentGroupId(group.getGroupId()).size() <= group.getCapacity()) {
            for (User student : selectedStudents) {
                studentgroupDAO.storeOne(new StudentGroup(student.getUserId(), group.getGroupId()));
            }
            updateStudentList(group.getCourse());
            updateStudentsInGroups(group);
        } else {
            showError().showAndWait();
            return;
        }
        showInfo().showAndWait();
    }

    /**
     * Removes students from group.
     */
    public void doRemove() {
        ObservableList<User> selectedStudents = studentsInGroupList.getSelectionModel().getSelectedItems();
        Group group = groupComboBox.getSelectionModel().getSelectedItem();
        if (selectedStudents.isEmpty() || groupComboBox.getSelectionModel().getSelectedItem() == null) {
            showWarning().showAndWait();
            return;
        }
        for (User student : selectedStudents) {
            studentgroupDAO.deleteOne(new StudentGroup(student.getUserId(), group.getGroupId()));
        }
        updateStudentList(group.getCourse());
        updateStudentsInGroups(group);
        showInfo().showAndWait();
    }

    /**
     * Goes back to menu.
     */
    public void doMenu() {
        Main.getSceneManager().showWelcomeScene(user);
    }

    /**
     * Shows pop-up when saved.
     */
    public Alert showInfo() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Opgeslagen");
        alert.setHeaderText("De wijzigingen zijn opgeslagen.");
        return alert;
    }

    /**
     * Shows pop-up when nothing is selected.
     */
    public Alert showWarning() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Geen selectie");
        alert.setHeaderText("Selecteer een groep en student.");
        return alert;
    }

    /**
     * Shows pop-up when group is full.
     */
    public Alert showError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Opslaan mislukt");
        alert.setHeaderText("Deze groep zit vol.");
        return alert;
    }

    /**
     * Exports studentresults to file.
     *
     * @author Jordi
     */
    public void exportStudentQuizResultsToFile() {
        List<User> studentList = userDAO.getAll();
        try (PrintWriter writer = new PrintWriter("student_resultaten.txt")) {
            writer.println("Overzicht Quizresultaten\n");
            for (User student : studentList) {
                writer.println("Student: " + student.getFirstname() + " " + student.getInfix() + " "
                        + student.getLastname() + " (ID: " + student.getUserId() + ")");
                List<QuizResult> results = resultDAO.getQuizResultsByUserId(student.getUserId());
                if (results.isEmpty()) {
                    writer.println("\tGeen quizresultaten gevonden");
                } else {
                    for (QuizResult result : results) {
                        writer.println("\tQuiz: " + result.getQuizId() + " | Score: " +
                                result.getScore() + " | Datum: " + result.getDateTime());
                    }
                }
                writer.println();
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Kon bestand niet aanmaken of openen", e);
        }
    }
}
