package javacouchdb;

import com.google.gson.JsonObject;
import org.lightcouch.Response;

import java.util.List;

public abstract class AbstractCouchDBDAO {

    private CouchDBAccess couchDBaccess;

    public AbstractCouchDBDAO(CouchDBAccess couchDBaccess) {
        this.couchDBaccess = couchDBaccess;
    }

    public String saveDocument(JsonObject document) {
        Response response = couchDBaccess.getClient().save(document);
        return response.getId();
    }

    public JsonObject getDocumentById(String idDocument) {
        return couchDBaccess.getClient().find(JsonObject.class, idDocument);
    }

    public List<JsonObject> getAllDocuments() {
        return couchDBaccess.getClient().view("_all_docs").includeDocs(true).query(JsonObject.class);
    }

    public String updateDocument(JsonObject document) {
        Response response = couchDBaccess.getClient().update(document);
        return response.getId();
    }

    public void deleteDocument(String idDocument, String revDocument) {
        couchDBaccess.getClient().remove(idDocument, revDocument);
    }
}
