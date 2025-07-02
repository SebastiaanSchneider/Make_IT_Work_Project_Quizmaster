/**
 * Controller for the CreateUpdateQuiz view
 *
 * @author Michiel van Haren
 */


package controller;

import database.mysql.CourseDAO;
import database.mysql.QuizDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Course;
import model.Level;
import model.Quiz;
import model.User;
import view.Main;

import java.util.List;


public class CreateUpdateQuizController {
    // Attributes --------------------------------------------------------------
    @FXML
    private Label titelLabel;

    @FXML
    private TextField quizNameTextfield;

    @FXML
    private TextField successDefinitionTextfield;

    @FXML
    private ComboBox<Level> quizLevelComboBox;

    @FXML
    private ComboBox<Course> quizCourseComboBox;

    @FXML
    private Button createUpdateQuizButton;

    @FXML
    private Label nameRoleLabel;

    private final QuizDAO quizDAO;
    private final CourseDAO courseDAO;

    private int quizId; // for tracking the correct quizId behind the scenes

    protected static String previousView = ""; // needed for correct functioning of the back button
    User user = WelcomeController.getUser();


    // Constructors ----------------------------------------------------------------------------------------------

    /**
     * Default constructor
     */
    public CreateUpdateQuizController() {
        this.quizDAO = new QuizDAO(Main.getDBaccess());
        this.courseDAO = new CourseDAO(Main.getDBaccess());
        this.quizId = 0;
    }

    // Methods --------------------------------------------------------------------------------------------------

    /**
     * Initial setup of the view controls depending on
     * whether a quiz is present (!= null) or not
     *
     * @param filledQuiz a Quiz object whose attributes should be used
     *                   to populate the fields of the view when in "update" view
     */
    public void setup(Quiz filledQuiz) {
        // Retrieve currently logged in user
        User coordinator = WelcomeController.getUser();
        titelLabel.setText("Maak een nieuwe quiz aan");
        createUpdateQuizButton.setText("Maak quiz aan");
        nameRoleLabel.setText(user.getFullName() + "\nRol: " + user.getRole().toString());

        // clears the comboboxes for initial setup
        quizLevelComboBox.getItems().clear();
        quizCourseComboBox.getItems().clear();

        // add quizlevels from quizlevelslist in class Quiz
        // to quizLevel combobox
        quizLevelComboBox.getItems().addAll(Level.values());

        // retrieves a list of all courses in the database
        List<Course> courseList = courseDAO.getCoursesByUsername(coordinator);
        // add (toString of) all courses in database to quizCourse combobox
        quizCourseComboBox.getItems().addAll(courseList);

        // If quiz is not null → update mode: populate textfield with QuizName
        // and set comboboxes to correct values for the given quiz.
        // Also change title of the screen and createUpdate button text.
        if (filledQuiz != null) {
            titelLabel.setText("Wijzig bestaande quiz");
            createUpdateQuizButton.setText("Wijzig quiz");
            quizNameTextfield.setText(filledQuiz.getQuizName());
            successDefinitionTextfield.setText(String.valueOf(filledQuiz.getSuccessDefinition()));
            quizLevelComboBox.setValue(filledQuiz.getQuizLevel());
            quizCourseComboBox.setValue(filledQuiz.getCourse());
            this.quizId = filledQuiz.getQuizId();
        }
    }

    /**
     * Functionality for the maak/wijzig quiz button
     */
    public void doCreateUpdateQuiz() {

        // First, check if the quiz level is lower than or equal to the level of the selected course
        if (validateFilled() && validateQuizLevelCompareCourseLevel() && validateSuccessDefinitionFormat() ) {
            // create quiz object from input fields
            Quiz quiz = new Quiz(quizId, quizNameTextfield.getText(), quizLevelComboBox.getValue(),
                    quizCourseComboBox.getValue(), Integer.parseInt(successDefinitionTextfield.getText()));
            // store quiz in database
            if (quizDAO.quizNameExists(quiz.getQuizName(), quiz.getQuizId())) {
                showError("Quiznaam al in gebruik. Kies een andere naam.").showAndWait();
            } else {
                quizDAO.storeOne(quiz);
                showInfo().showAndWait();
                // after clicking ok in the dialog, the program returns to the previous screen
                doGoBack();
            }
        } else {
            // if any of the validations fail, show the corresponding error message
            String errorMessage = "";
            if (!validateFilled())
                errorMessage = "Voer a.u.b. alle velden in";
            else if (!validateQuizLevelCompareCourseLevel())
                errorMessage = "Het niveau van de quiz mag niet hoger zijn dan het niveau van de cursus";
            else if (!validateSuccessDefinitionFormat())
                errorMessage = "Vul a.u.b. alleen getallen in boven de 0";
            showError(errorMessage).showAndWait();
        }


    }

    /**
     * Functionality for the menu button
     */
    public void doMenu() {
        Main.getSceneManager().showWelcomeScene(WelcomeController.getUser());
    }


    /**
     * Functionality for the back-button.
     * It will use the previousView attribute to go back to the correct screen
     */
    public void doGoBack() {
        if (previousView.equals("Dashboard")) {
            Main.getSceneManager().showCoordinatorDashboard();
        } else if (previousView.equals("Manager")) {
            Main.getSceneManager().showManageQuizScene();
        }
    }


    /**
     * Shows information alert when saving a quiz
     *
     * @return an alert to be shown when a quiz is saved.
     */
    public Alert showInfo() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Opgeslagen");
        alert.setHeaderText("De quiz is opgeslagen.");
        return alert;
    }


    /**
     * Shows warning alert when trying to save a quiz which name already exists
     *
     * @return an alert to be shown when user tries to save a new quiz with a the same name as an existing one.
     */
    public Alert showError(String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Waarschuwing");
        alert.setHeaderText(errorMessage);
        return alert;
    }


    /**
     * Checks if all required fields are filled.
     *
     * @return whether all fields are filled
     */
    public boolean validateFilled() {
        return (validateQuizName() && validateQuizLevel() && validateQuizCourse()) && validateCesuur();
    }


    /**
     * Checks if Quiz name field is filled
     *
     * @return true if field is filled, false if field is empty
     */
    public boolean validateQuizName() {
        return (!quizNameTextfield.getText().trim().isEmpty());
    }


    /**
     * Checks if Quiz level combobox has selection
     *
     * @return true if combobox has a selection, false if no selection is made
     */
    public boolean validateQuizLevel() {
        return (quizLevelComboBox.getValue() != null);
    }


    /**
     * Checks if Quiz Course combobox has selection
     *
     * @return true if combobox has a selection, false if no selection is made
     */
    public boolean validateQuizCourse() {
        return (quizCourseComboBox.getValue() != null);
    }


    /**
     * Checks if Cesuur field is filled
     *
     * @return true if field is filled, false if field is empty
     */
    public boolean validateCesuur() {
        return (!successDefinitionTextfield.getText().trim().isEmpty());
    }


    /**
     * Checks if cesuur field contains only numbers and if so if the number is > 0
     */
    public boolean validateSuccessDefinitionFormat() {
        if (!successDefinitionTextfield.getText().trim().isEmpty()) {
            try {
                if (Integer.parseInt(successDefinitionTextfield.getText()) > 0) {
                    // It's a valid integer and it's >= 0
                    return true;
                }
            } catch (NumberFormatException e) {
                // It's not a valid integer
                return false;
            }
        }
        return false;
    } // validateSuccessDefinitionFormat


    /**
     * Checks if selected quiz level is lower or equal to the selected course level
     */
    public boolean validateQuizLevelCompareCourseLevel() {
        return (quizLevelComboBox.getValue().
                compareTo(quizCourseComboBox.getValue().getCourseLevel()) <= 0);
    } // validateQuizLevelCompareCourseLevel
}

