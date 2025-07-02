/**
 * Launcher class to test and demonstrate basic NoSQL/CouchDB operations.
 * Author: Burhan
 */

package controller;

import com.google.gson.Gson;
import database.mysql.DBAccess;
import database.mysql.QuestionDAO;
import javacouchdb.CouchDBAccess;
import javacouchdb.QuestionCouchDBDAO;
import model.Question;
import view.Main;

public class BurhanNoSQLLauncher {

    public static void main(String[] args) {
        // Initialize SQL and CouchDB access objects
        DBAccess dbAccess = Main.getDBaccess();
        CouchDBAccess couchDBaccess = new CouchDBAccess("*****", "*****", "*****");

        // Initialize DAOs for both databases
        QuestionDAO questionDAO = new QuestionDAO(dbAccess);
        QuestionCouchDBDAO questionCouchDBDAO = new QuestionCouchDBDAO(couchDBaccess);

        // Gson instance for JSON serialization and deserialization
        Gson gson = new Gson();

        // 1. Retrieve a Question object by ID from the SQL database
        Question question = questionDAO.getOneById(300); // Ensure a question with ID 300 exists

        if (question == null) {
            System.out.println("No question found with ID 300 in the SQL database.");
            return; // Exit if question not found
        }

        // 2. Serialize the Question object to a JSON string
        String questionJson = gson.toJson(question);
        System.out.println("Serialized Question to JSON:");
        System.out.println(questionJson);
        System.out.println();

        // 3. Deserialize the JSON string back into a Question object
        Question questionFromJson = gson.fromJson(questionJson, Question.class);
        System.out.println("Deserialized JSON back to Question object:");
        System.out.println(questionFromJson);
        System.out.println();

        // 4. Save the Question object into the CouchDB database (Create operation)
        questionCouchDBDAO.saveSingleQuestion(question);

        // 5. Retrieve the Question from CouchDB by its questionId (Read operation)
        Question questionFromCouchDB = questionCouchDBDAO.getQuestionByQuestionId(300);

        if (questionFromCouchDB != null) {
            System.out.println("Retrieved Question from CouchDB:");
            System.out.println(questionFromCouchDB);
            System.out.println();

            // 6. Update the question text and save changes back to CouchDB (Update operation)
            questionFromCouchDB.setQuestionText("Wat is Java eigenlijk?");
            questionCouchDBDAO.updateQuestion(questionFromCouchDB);
            System.out.println("Question updated in CouchDB.");

            // 7. Optionally, delete the Question from CouchDB (Delete operation)
//            questionCouchDBDAO.deleteQuestion(questionFromCouchDB);
//            System.out.println("Question deleted from CouchDB.");
        } else {
            System.out.println("Question not found in CouchDB.");
        }
    }
}
