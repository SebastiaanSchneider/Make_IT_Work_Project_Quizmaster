package javacouchdb;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import model.Group;

/**
 * DAO providing CouchDB database funtions related to Group objects
 * @author Sebastiaan Schneider
 */
public class GroupCouchDBDAO extends AbstractCouchDBDAO {
    private Gson gson;

    // setup connection to CouchDB
    public GroupCouchDBDAO(CouchDBAccess couchDBaccess) {
        super(couchDBaccess);
    }

    // save group to CouchDB
    public String saveGroup(Group group) {
        String jsonString = gson.toJson(group);
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
        return saveDocument(jsonObject);
    }

    // get group from CouchDB by its doc ID
    public Group getGroupByDocId(String docId) {
        // find group in CouchDB database and turn into Group object
        return gson.fromJson(getDocumentById(docId), Group.class);
    }

    // get group from CouchDB by its group ID
    public Group getGroupByGroupId(int groupId) {
        for (JsonObject jsonObject: getAllDocuments()) {
            Group result = gson.fromJson(jsonObject, Group.class);
            if (result.getGroupId() == groupId) {
                return result;
            }
        }
        return null;
    }

    // delete group from CouchDB
    public void deleteGroup(Group group) {
        String[] idAndRev = getIdAndRevForGroup(group);
        deleteDocument(idAndRev[0], idAndRev[1]);
    }

    // update a course in CouchDB
    public String updateGroup(Group group) {
        String[] idAndRev = getIdAndRevForGroup(group);

        if (idAndRev[0] == null || idAndRev[1] == null) {
            throw new IllegalStateException("Groep niet gevonden in de database");
        }

        String jsonString = gson.toJson(group);
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
        jsonObject.addProperty("_id", idAndRev[0]);
        jsonObject.addProperty("_rev", idAndRev[1]);
        return updateDocument(jsonObject);
    }

    // get ID and rev from group in CouchDB
    public String[] getIdAndRevForGroup(Group group) {
        String[] idAndRev = new String[2];

        for (JsonObject jsonObject: getAllDocuments()) {
            if (jsonObject.has("groupId") && jsonObject.get("groupId").getAsInt() ==
                    group.getGroupId()) {
                idAndRev[0] = jsonObject.get("_id").getAsString();
                idAndRev[1] = jsonObject.get("_rev").getAsString();
                break;
            }
        }
        return idAndRev;
    }
}
