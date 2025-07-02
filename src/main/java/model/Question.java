package model;

public class Question {

    // Static ID counter
    private static int counter = 0;

    // Attributes
    private int questionId;
    private String questionText;
    private String correctAnswer;
    private String incorrectAnswer1;
    private String incorrectAnswer2;
    private String incorrectAnswer3;
    private Quiz quiz;



    // Getters and Setters

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getIncorrectAnswer1() {
        return incorrectAnswer1;
    }

    public void setIncorrectAnswer1(String incorrectAnswer1) {
        this.incorrectAnswer1 = incorrectAnswer1;
    }

    public String getIncorrectAnswer2() {
        return incorrectAnswer2;
    }

    public void setIncorrectAnswer2(String incorrectAnswer2) {
        this.incorrectAnswer2 = incorrectAnswer2;
    }

    public String getIncorrectAnswer3() {
        return incorrectAnswer3;
    }

    public void setIncorrectAnswer3(String incorrectAnswer3) {
        this.incorrectAnswer3 = incorrectAnswer3;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    // All-args Constructor
    public Question(String questionText, String correctAnswer,
                    String incorrectAnswer1, String incorrectAnswer2, String incorrectAnswer3, Quiz quiz) {
        this.questionText = questionText;
        this.correctAnswer = correctAnswer;
        this.incorrectAnswer1 = incorrectAnswer1;
        this.incorrectAnswer2 = incorrectAnswer2;
        this.incorrectAnswer3 = incorrectAnswer3;
        this.quiz = quiz;
    }

    @Override
    public String toString(){
        return "Vraag: " + questionText +
                "\nGoede antwoord: " + correctAnswer +
                "\nFout antwoord 1: " + incorrectAnswer1 +
                "\nFout antwoord 2: " + incorrectAnswer2 +
                "\nFout antwoord 3: " + incorrectAnswer3 +
                "\nQuiz: " + quiz.getQuizName();
    }

    // Methods
}
