package javacouchdb;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import model.Quiz;
import model.QuizResult;

import java.util.ArrayList;
import java.util.List;


public class QuizResultCouchDBDAO extends AbstractCouchDBDAO {
    // Attributes --------------------------------------------------------------
    private Gson gson;

    // Constructors ------------------------------------------------------------
    public QuizResultCouchDBDAO(CouchDBAccess couchDBAccess) {
        super(couchDBAccess);
        gson = new Gson();
    }

    // Methods -----------------------------------------------------------------
    public String saveSingleQuizResult(QuizResult quizResult) {
        // QuizResult object omzetten naar JsonObject, zodat het opgeslagen kan worden mbv save()
        String jsonString = gson.toJson(quizResult);
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(jsonString).getAsJsonObject();
        return saveDocument(jsonObject);
    }


    public Quiz getQuizByDocId(String doc_Id) {
        // Als je het id van een couchDB document weet, kun je daarmee een QuizResult ophalen
        return gson.fromJson(getDocumentById(doc_Id), Quiz.class);
    }


    /**
     * Returns a list of quizresults for a given userId
     *
     * @param userId the user ID of the user we want to get quizresults from
     * @return a list of quizresults for the given user ID
     */
    public List<QuizResult> getQuizResultsByUserId(int userId) {
        // Haal alle documenten op, in de vorm van JsonObjecten;
        List<QuizResult> quizResultsList = new ArrayList<>();
        QuizResult quizResult;
        for (JsonObject jsonObject : getAllDocuments()) {
            // zet om naar QuizResult en test op userId
            quizResult = gson.fromJson(jsonObject, QuizResult.class);
            if (quizResult.getUserId() == userId) {
                quizResultsList.add(quizResult);
            }
        }
        return quizResultsList;
    }


    /**
     * Returns a list of quizresults for a given user ID and quiz ID
     * Because a user can have multiple results for the same quiz.
     *
     * @param userId the user ID of the user we want to get quizresults from
     * @param quizId the quiz ID of the quiz we want to get the results from
     * @return a list of quizresults for the given combination of user ID and quiz ID
     */
    public List<QuizResult> getQuizResultsByUserIdAndQuizId(int userId, int quizId) {
        // Haal alle documenten op, in de vorm van JsonObjecten;
        List<QuizResult> quizResultsList = new ArrayList<>();
        QuizResult quizResult;
        for (JsonObject jsonObject : getAllDocuments()) {
            // zet om naar QuizResult en test op userId
            quizResult = gson.fromJson(jsonObject, QuizResult.class);
            if (quizResult.getUserId() == userId & quizResult.getQuizId() == quizId) {
                quizResultsList.add(quizResult);
            }
        }
        return quizResultsList;
    }


    public void deleteQuizResult(QuizResult quizResult) {
        // Op basis van _id en _rev kun je een document in CouchDB verwijderen
        String[] idAndRev = getIdAndRevOfQuizResult(quizResult);
        deleteDocument(idAndRev[0], idAndRev[1]);
    }


    /**
     * Returns the Id and Rev of a quizResult as an array of strings
     *
     * @return an array of (2) strings for Id and rev.
     */
    public String[] getIdAndRevOfQuizResult(QuizResult quizResult) {
        // Vind het _id en _rev van document behorend bij een QuizResult met quizId, userId en dateTime
        String[] idAndRev = new String[2];
        for (JsonObject jsonObject : getAllDocuments()) {
            if (
                    jsonObject.has("quizId") && jsonObject.get("quizId").getAsInt() == quizResult.getQuizId()
                            && jsonObject.has("userId") && jsonObject.get("userId").getAsInt() == quizResult.getUserId()
                            && jsonObject.has("dateTime") && jsonObject.get("dateTime").getAsString().equals(quizResult.getDateTime().toString())
            ) {
                idAndRev[0] = jsonObject.get("_id").getAsString();
                idAndRev[1] = jsonObject.get("_rev").getAsString();
            }
        }
        return idAndRev;
    }


    public String updateQuizResult(QuizResult quizResult) {
        // Haal _id en _rev van document op behorend bij quiz
        // Zet quiz om in JsonObject
        String[] idAndRev = getIdAndRevOfQuizResult(quizResult);
        String jsonString = gson.toJson(quizResult);
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(jsonString).getAsJsonObject();
        // Voeg _id en _rev toe aan JsonObject, nodig voor de update van een document.
        jsonObject.addProperty("_id", idAndRev[0]);
        jsonObject.addProperty("_rev", idAndRev[1]);
        return updateDocument(jsonObject);
    }


    @Override
    public String toString() {
        return "QuizResultCouchDBDAO{" + "gson=" + gson +
                '}';
    }
}
