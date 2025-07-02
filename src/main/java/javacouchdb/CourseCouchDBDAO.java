package javacouchdb;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import model.Course;

/**
 * CRUD functionality for course in couchDB
 */
public class CourseCouchDBDAO extends AbstractCouchDBDAO {
    private Gson gson;

    public CourseCouchDBDAO(CouchDBAccess couchDBaccess) {
        super(couchDBaccess);
        this.gson = new Gson();
    }

    /**
     * Saves a course in couchDB
     *
     * @param course to be saved
     * @return String that saves doc
     */
    public String saveSingleCourse(Course course) {
        String jsonString = gson.toJson(course);
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
        return saveDocument(jsonObject);
    }

    /**
     * Gets a course from couchDB by its docId.
     *
     * @param docId from course.
     * @return course belonging to docId.
     */
    public Course getCourseByDocId(String docId) {
        return gson.fromJson(getDocumentById(docId), Course.class); // zet om in object
    }

    /**
     * Gets a course from couchDB by its courseId.
     *
     * @param courseId from course.
     * @return course beloning to courseId.
     */
    public Course getCourseByCourseId(int courseId) {
        for (JsonObject jsonObject : getAllDocuments()) {
            Course result = gson.fromJson(jsonObject, Course.class);
            if (result.getCourseId() == courseId) {
                return result;
            }
        }
        return null;
    }

    /**
     * Deletes a course from couchDB.
     *
     * @param course to be deleted.
     */
    public void deleteCourse(Course course) {
        String[] idAndRev = getIdAndRevOfCourse(course);
        deleteDocument(idAndRev[0], idAndRev[1]);
    }

    /**
     * Updates a course in couchDB
     *
     * @param course to be updated
     * @return String that saves doc
     */
    public String updateCourse(Course course) {
        String[] idAndRev = getIdAndRevOfCourse(course);
        if (idAndRev[0] == null || idAndRev[1] == null) {
            throw new IllegalStateException("Course niet gevonden in database.");
        }
        String jsonString = gson.toJson(course);
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
        jsonObject.addProperty("_id", idAndRev[0]);
        jsonObject.addProperty("_rev", idAndRev[1]);
        return updateDocument(jsonObject);
    }

    /**
     * Gets id and rev from course from couchDB.
     *
     * @param course to look for in couchDB
     * @return list[] with id and rev
     */
    public String[] getIdAndRevOfCourse(Course course) {
        String[] idAndRev = new String[2];
        for (JsonObject jsonObject : getAllDocuments()) {
            if (jsonObject.has("courseId") &&
                    jsonObject.get("courseId").getAsInt() == course.getCourseId()) {
                idAndRev[0] = jsonObject.get("_id").getAsString();
                idAndRev[1] = jsonObject.get("_rev").getAsString();
                break;
            }
        }
        return idAndRev;
    }
}
