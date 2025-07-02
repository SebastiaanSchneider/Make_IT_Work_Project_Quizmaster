package javacouchdb;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import model.Quiz;

public class QuizCouchDBDAO extends AbstractCouchDBDAO {
    // Attributes --------------------------------------------------------------
    private Gson gson;

    // Constructors ------------------------------------------------------------
    public QuizCouchDBDAO(CouchDBAccess couchDBaccess) {
        super(couchDBaccess);
        gson = new Gson();
    }

    // Methods -----------------------------------------------------------------
    public String saveSingleQuiz(Quiz quiz) {
        // Quiz object omzetten naar JsonObject, zodat het opgeslagen kan worden mbv save()
        String jsonString = gson.toJson(quiz);
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(jsonString).getAsJsonObject();
        return saveDocument(jsonObject);
    }

    public Quiz getQuizByDocId(String doc_Id) {
        // Als je het id van een couchDB document weet, kun je daarmee een Quiz ophalen
        return gson.fromJson(getDocumentById(doc_Id), Quiz.class);
    }

    public Quiz getQuizByQuizId(int quizId) {
        // Haal alle documenten op, in de vorm van JsonObjecten; zet om naar Quiz en test op quizId
        Quiz resultaat;
        for (JsonObject jsonObject : getAllDocuments()) {
            resultaat = gson.fromJson(jsonObject, Quiz.class);
            if (resultaat.getQuizId() == quizId) {
                return resultaat;
            }
        }
        return null;
    }

    public void deleteQuiz(Quiz quiz) {
        // Op basis van _id en _rev kun je een document in CouchDB verwijderen
        String[] idAndRev = getIdAndRevOfQuiz(quiz);
        deleteDocument(idAndRev[0], idAndRev[1]);
    }

    public String[] getIdAndRevOfQuiz(Quiz quiz) {
        // Vind het _id en _rev van document behorend bij een Quiz met quizId
        String[] idAndRev = new String[2];
        for (JsonObject jsonObject : getAllDocuments()) {
            if (jsonObject.has("quizId")
                    && jsonObject.get("quizId").getAsInt() == quiz.getQuizId()) {
                idAndRev[0] = jsonObject.get("_id").getAsString();
                idAndRev[1] = jsonObject.get("_rev").getAsString();
            }
        }
        return idAndRev;
    }

    public String updateQuiz(Quiz quiz) {
        // Haal _id en _rev van document op behorend bij quiz
        // Zet quiz om in JsonObject
        String[] idAndRev = getIdAndRevOfQuiz(quiz);
        String jsonString = gson.toJson(quiz);
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(jsonString).getAsJsonObject();
        // Voeg _id en _rev toe aan JsonObject, nodig voor de update van een document.
        jsonObject.addProperty("_id" , idAndRev[0]);
        jsonObject.addProperty("_rev" , idAndRev[1]);
        return updateDocument(jsonObject);
    }


}
