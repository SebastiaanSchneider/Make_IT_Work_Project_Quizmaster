package javacouchdb;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import model.Question;

public class QuestionCouchDBDAO extends AbstractCouchDBDAO {
    // Gson instance used to convert between Java objects and JSON format
    private Gson gson;

    // Constructor that initializes the CouchDB access and the Gson instance
    public QuestionCouchDBDAO(CouchDBAccess couchDBaccess) {
        super(couchDBaccess);
        gson = new Gson();
    }

    /**
     * Saves a single Question object to CouchDB by converting it into a JSON object.
     * @param question the Question object to be saved
     * @return the result from CouchDB (e.g., document ID or success message)
     */
    public String saveSingleQuestion(Question question) {
        // Convert the Question object to a JSON string
        String jsonString = gson.toJson(question);
        // Parse the JSON string into a JsonObject for CouchDB storage
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
        // Save the JsonObject to CouchDB and return the result
        return saveDocument(jsonObject);
    }

    /**
     * Retrieves a Question object from CouchDB based on the document ID (_id).
     * @param doc_Id the CouchDB document ID
     * @return the Question object corresponding to the given document ID
     */
    public Question getQuestionByDocId(String doc_Id) {
        // Retrieve the JSON string from CouchDB and convert it to a Question object
        return gson.fromJson(getDocumentById(doc_Id), Question.class);
    }

    /**
     * Searches all CouchDB documents for a Question with the specified questionId field.
     * Note: For better performance with large datasets, consider implementing a CouchDB view.
     * @param questionId the questionId to search for
     * @return the matching Question object if found, otherwise null
     */
    public Question getQuestionByQuestionId(int questionId) {
        // Iterate through all documents in CouchDB
        for (JsonObject jsonObject : getAllDocuments()) {
            // Convert each JsonObject to a Question object
            Question result = gson.fromJson(jsonObject, Question.class);
            // Check if the questionId matches
            if (result.getQuestionId() == questionId) {
                return result;
            }
        }
        // Return null if no matching Question is found
        return null;
    }

    /**
     * Deletes a Question document from CouchDB based on its _id and _rev fields.
     * @param question the Question object to delete
     * @throws IllegalStateException if the Question does not exist in the database
     */
    public void deleteQuestion(Question question) {
        // Retrieve the document's _id and _rev fields for deletion
        String[] idAndRev = getIdAndRevOfQuestion(question);

        // If _id or _rev is null, the document does not exist; throw an exception
        if (idAndRev[0] == null || idAndRev[1] == null) {
            throw new IllegalStateException("Question not found in database.");
        }
        // Delete the document from CouchDB using _id and _rev
        deleteDocument(idAndRev[0], idAndRev[1]);
    }

    /**
     * Retrieves the CouchDB document's _id and _rev for the given Question.
     * These values are required for updating or deleting the document.
     * @param question the Question object to find in the database
     * @return a String array where index 0 is _id and index 1 is _rev; null if not found
     */
    public String[] getIdAndRevOfQuestion(Question question) {
        String[] idAndRev = new String[2];
        // Loop through all documents in CouchDB
        for (JsonObject jsonObject : getAllDocuments()) {
            // Check if the document contains the matching questionId
            if (jsonObject.has("questionId")
                    && jsonObject.get("questionId").getAsInt() == question.getQuestionId()) {
                // Extract the _id and _rev fields from the document
                idAndRev[0] = jsonObject.get("_id").getAsString();
                idAndRev[1] = jsonObject.get("_rev").getAsString();
                break; // Stop searching once found
            }
        }
        return idAndRev;
    }

    /**
     * Updates an existing Question document in CouchDB.
     * The document's _id and _rev must be provided for the update to succeed.
     * @param question the Question object with updated data
     * @return the result from CouchDB (e.g., new revision ID)
     * @throws IllegalStateException if the Question does not exist in the database
     */
    public String updateQuestion(Question question) {
        // Get the current document _id and _rev for the Question
        String[] idAndRev = getIdAndRevOfQuestion(question);

        // If _id or _rev is missing, the document does not exist; throw exception
        if (idAndRev[0] == null || idAndRev[1] == null) {
            throw new IllegalStateException("Question not found in database.");
        }

        // Convert the updated Question object to JSON string
        String jsonString = gson.toJson(question);
        // Parse JSON string into a JsonObject
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
        // Add _id and _rev fields to the JSON to inform CouchDB which document to update
        jsonObject.addProperty("_id", idAndRev[0]);
        jsonObject.addProperty("_rev", idAndRev[1]);
        // Perform the update in CouchDB and return the result
        return updateDocument(jsonObject);
    }
}
