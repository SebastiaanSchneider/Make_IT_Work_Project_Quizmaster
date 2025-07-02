package controller;

import database.mysql.DBAccess;
import database.mysql.QuestionDAO;
import javacouchdb.CouchDBAccess;
import javacouchdb.QuizResultCouchDBDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Question;
import model.Quiz;
import model.User;
import model.QuizResult;
import view.Main;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class FillOutQuizController {

    @FXML
    private Label titleLabel;
    @FXML
    private TextArea questionArea;
    @FXML
    private Button optionA;
    @FXML
    private Button optionB;
    @FXML
    private Button optionC;
    @FXML
    private Button optionD;
    @FXML
    private Button submitButton;
    @FXML
    private Button volgendeButton;
    @FXML
    private Label nameRoleLabel;



    private final static DBAccess dbAccess = Main.getDBaccess();
    private final static QuestionDAO questionDAO = new QuestionDAO(dbAccess);

    private Quiz currentQuiz;
    private User currentUser;

    CouchDBAccess couchDBAccess = Main.getCouchDBAccess();
    QuizResultCouchDBDAO dao = new QuizResultCouchDBDAO(couchDBAccess);

    /**
     * Stores the list of questions for the current quiz.
     * Example: [Question1, Question2, Question3]
     */
    private List<Question> questionList;

    /**
     * Stores the shuffled option labels ("A", "B", "C", "D") for each question.
     * This list has the same size as questionList.
     * Each inner list is a shuffled order of ["A", "B", "C", "D"].
     * Example: [["C", "A", "B", "D"], ["B", "D", "C", "A"], ...]
     */
    private List<List<String>> optionsList = new ArrayList<>();

    /**
     * Stores the student's selected answer texts.
     * Initialized with empty strings, updated when the student selects an option.
     * Example: ["Paris", "3", "", "Mount Everest"]
     */
    private List<String> studentAnswers = new ArrayList<>();

    /**
     * Keeps track of which question is currently displayed.
     */
    private int currentIndex;

    private final List<String> options = List.of("A", "B", "C", "D");

    /**
     * Initializes the quiz: loads questions, shuffles answer options for each one,
     * and displays the first question.
     *
     * @param quiz The Quiz object selected by the student.
     */
    public void setup(Quiz quiz) {
        User user = WelcomeController.getUser();
        nameRoleLabel.setText(user.getFullName() + "\nRol: " + user.getRole().toString());
        this.currentQuiz = quiz;

        // Retrieve questions from database for this quiz
        questionList = questionDAO.getQuestionsByQuizName(quiz.getQuizName());
        // For each question, generate a random order of answer option labels (A, B, C, D)
        for (int i = 0; i < questionList.size(); i++) {
            List<String> shuffledOptions = new ArrayList<>(options);
            Collections.shuffle(shuffledOptions); // Shuffle "A", "B", "C", "D"
            optionsList.add(shuffledOptions); // Add the shuffled list to optionsList
            studentAnswers.add("");// Add empty answer string (not answered yet)
        }

        System.out.println("Number of questions: " + questionList.size());

        currentIndex = 0;
        showCurrentQuestion(questionList.get(currentIndex));
    }

    /**
     * Answers are displayed in a randomized order based on the previously shuffled list.
     * Displays the current question and its shuffled answer options.
     * If the question was already answered, disables the chosen answer button.
     *
     * @param question The Question object currently being displayed.
     */
    private void showCurrentQuestion(Question question) {
        refreshButtons();
        if (!studentAnswers.get(currentIndex).isBlank()) {
            makeQuestionAnswered(question);
        }

        titleLabel.setText("Vraag " + (currentIndex + 1));

        // Start building the question content to show in the TextArea
        StringBuilder questionText = new StringBuilder(question.getQuestionText() + "\n\n");

        // Create a list to hold the answer options with their shuffled labels (A, B, C, D)
        List<String> answers = new ArrayList<>();

        // Retrieve the shuffled labels from optionsList for the current question
        // Then, combine each label with the corresponding answer text
        answers.add(optionsList.get(currentIndex).get(0) + ") " + question.getCorrectAnswer() + "\n");
        answers.add(optionsList.get(currentIndex).get(1) + ") " + question.getIncorrectAnswer1() + "\n");
        answers.add(optionsList.get(currentIndex).get(2) + ") " + question.getIncorrectAnswer2() + "\n");
        answers.add(optionsList.get(currentIndex).get(3) + ") " + question.getIncorrectAnswer3() + "\n");

        // Sort the answers alphabetically by their label ("A)", "B)", etc.)
        // This ensures the answers are always displayed in order A–D, even though the actual answer texts are shuffled.
        Collections.sort(answers);

        // Append each formatted answer option into the question text
        for (String answer : answers) {
            questionText.append(answer);
        }

        // Finally, display the full question + answer text in the question area
        questionArea.setText(questionText.toString());

        if (currentIndex == questionList.size() - 1) {
            submitButton.setVisible(true);
            volgendeButton.setDisable(true);
        } else {
            submitButton.setVisible(false);
            volgendeButton.setDisable(false);
        }
    }

    /**
     * Enables all answer option buttons.
     * Used to reset button states before displaying a new question.
     */
    private void refreshButtons() {
        optionA.setDisable(false);
        optionB.setDisable(false);
        optionC.setDisable(false);
        optionD.setDisable(false);
    }

    /**
     * Disables the button corresponding to the student's previously selected answer for the current question.
     * Highlights that the question was answered by disabling the chosen option.
     *
     * @param question The Question object currently being displayed.
     */
    private void makeQuestionAnswered(Question question) {
        String studentAnswer = studentAnswers.get(currentIndex);

        // Get the list of option labels (A, B, C, D) for the current question
        List<String> answerOptions = optionsList.get(currentIndex);

        // This will store the label (A, B, C, or D) of the selected option
        String selectedOption = "";

        // Match the student's answer to the correct or incorrect answer options,
        // and assign the corresponding label from answerOptions.
        if (studentAnswer.equals(question.getCorrectAnswer())) {
            selectedOption = answerOptions.get(0);
        } else if (studentAnswer.equals(question.getIncorrectAnswer1())) {
            selectedOption = answerOptions.get(1);
        } else if (studentAnswer.equals(question.getIncorrectAnswer2())) {
            selectedOption = answerOptions.get(2);
        } else if (studentAnswer.equals(question.getIncorrectAnswer3())) {
            selectedOption = answerOptions.get(3);
        }

        switch (selectedOption) {
            case "A":
                optionA.setDisable(true);
                break;
            case "B":
                optionB.setDisable(true);
                break;
            case "C":
                optionC.setDisable(true);
                break;
            case "D":
                optionD.setDisable(true);
                break;
            default:
                break;
        }
    }


    /**
     * Handles the student's selection of answer "A" for the current question.
     */
    public void doRegisterA() {
        registerAnswer("A");
        doNextQuestion();
    }

    /**
     * Handles the student's selection of answer "B" for the current question.
     */
    public void doRegisterB() {
        registerAnswer("B");
        doNextQuestion();
    }

    /**
     * Handles the student's selection of answer "C" for the current question.
     */
    public void doRegisterC() {
        registerAnswer("C");
        doNextQuestion();
    }

    /**
     * Handles the student's selection of answer "D" for the current question.
     */
    public void doRegisterD() {
        registerAnswer("D");
        doNextQuestion();
    }

    /**
     * Records the student's selected answer for the current question.
     * Maps the selected option letter ("A", "B", "C", or "D") to the actual answer text.
     * Updates the studentAnswers list at the current question index.
     *
     * @param answer The option letter chosen by the student.
     *               How it works?
     *               optionsList.get(currentIndex) == ["B", "A", "D", "C"]
     *               "B" corresponds to the correct answer (index 0)
     *               "A" corresponds to incorrect1 (index 1)
     *               "D" corresponds to incorrect2 (index 2)
     *               "C" corresponds to incorrect3 (index 3)
     *               If the student clicks option "A", this method:
     *               Looks up "A" in the shuffled list: it's at index 1.
     *               So it chooses question.getIncorrectAnswer1() and stores that text in studentAnswers.
     */

    private void registerAnswer(String answer) {

        switch (optionsList.get(currentIndex).indexOf(answer)) {
            case 0:
                studentAnswers.set(currentIndex, questionList.get(currentIndex).getCorrectAnswer());
                break;
            case 1:
                studentAnswers.set(currentIndex, questionList.get(currentIndex).getIncorrectAnswer1());
                break;
            case 2:
                studentAnswers.set(currentIndex, questionList.get(currentIndex).getIncorrectAnswer2());
                break;
            default:
                studentAnswers.set(currentIndex, questionList.get(currentIndex).getIncorrectAnswer3());
                break;
        }
    }

    /**
     * to the next question in the quiz.
     * If the current question is the last one, calculates the total score
     * and to the student feedback screen.
     */
    public void doNextQuestion() {
        if (currentIndex < questionList.size() - 1) {
            currentIndex++;
            showCurrentQuestion(questionList.get(currentIndex));
            volgendeButton.setDisable(false); // make the button active
        } else {
            // last question setdisable button
            volgendeButton.setDisable(true);
        }
    }

    /**
     * Moves to the previous question if not already at the beginning.
     */
    public void doPreviousQuestion() {
        if (currentIndex > 0) {
            currentIndex--;
            showCurrentQuestion(questionList.get(currentIndex));
        }
    }

    /**
     * Returns the user to the welcome screen.
     */
    public void doMenu() {
        Main.getSceneManager().showWelcomeScene(WelcomeController.getUser());
    }

    /**
     * Calculates the total number of questions the student answered correctly.
     * Compares the student's recorded answers with the correct answers.
     * <p>
     * Note: Because answer options are shuffled before displaying,
     * the answers stored must correspond correctly to the shuffled options for accurate scoring.
     * This implementation assumes studentAnswers stores the exact answer texts.
     *
     * @return The number of correctly answered questions.
     */
    public int calculateScore() {
        int score = 0;// Counter to keep track of the student's score

        // If the question list or student answers are empty, return a score of 0
        if (questionList == null || questionList.isEmpty() || studentAnswers.isEmpty()) {
            return 0;
        }

        for (int i = 0; i < questionList.size(); i++) {
            Question question = questionList.get(i);// Get the i-th question
            String givenAnswerText = studentAnswers.get(i);// Get the student's answer for the i-th question

             /*
             The studentAnswers list stores the actual answer texts (e.g., "Paris") instead of option letters like A, B, etc.
             This is because the answer choices might be shuffled, and the order of the letters could change.
             If the student selects "Paris", meaning studentAnswers[i] = "Paris", it is counted as correct.
             **/
            if (givenAnswerText.equals(question.getCorrectAnswer())) {
                score++;
            }
        }
        return score;
    }

    //for the feedbackscherm
    public boolean isQuizPassed() {
        int correctAnswers = calculateScore();
        int requiredCorrectAnswers = currentQuiz.getSuccessDefinition();
        return correctAnswers >= requiredCorrectAnswers;
    }

    @FXML
    private void handleSubmit() {
        // Create confirmation window
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Bevestiging");
        confirmation.setHeaderText("Weet je het zeker?");
        confirmation.setContentText("Wil je de quiz echt indienen?");

        // Show the confirmation window and wait for the user's response
        Optional<ButtonType> result = confirmation.showAndWait();

        // if the user "OK" click
        if (result.isPresent() && result.get() == ButtonType.OK) {
            submitQuiz();
        }
    }


    /**
     * finish Quiz, calculate the reesult and go to feedback
     */
    private void submitQuiz() {
        int totalCorrect = calculateScore();
        System.out.println("Quiz wordt ingediend. Totaal correcte antwoorden: " + totalCorrect + "/" + questionList.size());

        currentUser = WelcomeController.getUser();
        int currentUserId = currentUser.getUserId();
        int currentQuizId = currentQuiz.getQuizId();

        QuizResult result = new QuizResult(
                currentQuizId,
                currentUserId,
                LocalDateTime.now(),
                totalCorrect
        );

        dao.saveSingleQuizResult(result);
        boolean isPassed = isQuizPassed();
        Main.getSceneManager().showStudentFeedback(currentQuiz, isPassed);
    }


}