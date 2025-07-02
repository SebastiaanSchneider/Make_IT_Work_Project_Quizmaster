package controller;

import com.google.gson.Gson;
import database.mysql.CourseDAO;
import database.mysql.DBAccess;
import javacouchdb.CouchDBAccess;
import javacouchdb.CourseCouchDBDAO;
import model.Course;
import view.Main;

public class ElineNoSQLLauncher {
    public static void main(String[] args) {

        // access databases
        DBAccess dbAccess = Main.getDBaccess();
        CourseDAO courseDAO = new CourseDAO(dbAccess);
        CouchDBAccess couchDBaccess = new CouchDBAccess("*****", "*****", "*****");
        CourseCouchDBDAO courseCouchDBDAO = new CourseCouchDBDAO(couchDBaccess);
        Gson gson = new Gson();

        // getting a course from database to test
        Course course = courseDAO.getOneById(64);

        // Create: saving course in couchDB
        courseCouchDBDAO.saveSingleCourse(course);

        // Read: getting course from couchDB
        Course course1 = courseCouchDBDAO.getCourseByCourseId(64);
        System.out.println("\nCourse uit CouchDB: \n" + course1);

        // Update: updating course in couchDB
        course1.setCourseName("Gewijzigde naam");
        courseCouchDBDAO.updateCourse(course1);
        System.out.println("\nGewijzigde course: \n"  + course1);
        courseCouchDBDAO.getCourseByCourseId(64);
        System.out.println("\nCourse in DB: \n" + course1);

        // Delete: removing course from couchDB
        courseCouchDBDAO.deleteCourse(course1);

    }
}
