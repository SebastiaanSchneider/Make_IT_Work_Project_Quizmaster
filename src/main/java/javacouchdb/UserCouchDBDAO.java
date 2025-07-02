package javacouchdb;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import model.User;

public class UserCouchDBDAO extends AbstractCouchDBDAO {
    private Gson gson;

    /**
     * Constructor die de databaseverbinding initialiseert en een Gson-object maakt.
     */
    public UserCouchDBDAO(CouchDBAccess couchDBaccess) {
        super(couchDBaccess);
        this.gson = new Gson();
    }

    /**
     * Slaat een enkele gebruiker op in CouchDB door deze om te zetten naar JSON.
     */
    public String saveSingleUser(User user) {
        String jsonString = gson.toJson(user);
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
        return saveDocument(jsonObject);
    }

    /**
     * Haalt een gebruiker op via het document-ID uit de CouchDB.
     */
    public User getUserByDocId(String docId) {
        return gson.fromJson(getDocumentById(docId), User.class); // zet JSON om naar User-object
    }

    /**
     * Doorzoekt alle documenten in de database en retourneert de gebruiker met het overeenkomende userId.
     */
    public User getUserByUserId(int userId) {
        for (JsonObject jsonObject : getAllDocuments()) {
            User result = gson.fromJson(jsonObject, User.class);
            if (result.getUserId() == userId) {
                return result;
            }
        }
        return null;
    }

    /**
     * Verwijdert een gebruiker uit de database aan de hand van zijn document-ID en -REV.
     */
    public void deleteUser(User user) {
        String[] idAndRev = getIdAndRevOfUser(user);
        deleteDocument(idAndRev[0], idAndRev[1]);
    }

    /**
     * Werkt een bestaande gebruiker bij in de database, mits ID en REV gevonden kunnen worden.
     */
    public String updateUser(User user) {
        String[] idAndRev = getIdAndRevOfUser(user);
        if (idAndRev[0] == null || idAndRev[1] == null) {
            throw new IllegalStateException("Gebruiker niet gevonden in database.");
        }
        String jsonString = gson.toJson(user);
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
        jsonObject.addProperty("_id", idAndRev[0]);
        jsonObject.addProperty("_rev", idAndRev[1]);
        return updateDocument(jsonObject);
    }

    /**
     * Zoekt het document-ID en de revisie (REV) van een gebruiker op basis van zijn userId.
     */
    public String[] getIdAndRevOfUser(User user) {
        String[] idAndRev = new String[2];
        for (JsonObject jsonObject : getAllDocuments()) {
            if (jsonObject.has("userId") &&
                    jsonObject.get("userId").getAsInt() == user.getUserId()) {
                idAndRev[0] = jsonObject.get("_id").getAsString();
                idAndRev[1] = jsonObject.get("_rev").getAsString();
                break;
            }
        }
        return idAndRev;
    }
}
