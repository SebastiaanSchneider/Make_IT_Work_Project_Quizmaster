package controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import database.mysql.CourseDAO;
import database.mysql.DBAccess;
import database.mysql.GroupDAO;
import database.mysql.UserDAO;

import model.Course;
import model.Group;
import model.User;

/**
 * Launcher to read Group objects in a CSV-file and write it to an SQL database
 */
public class SebastiaanQuizmasterLauncher {
    public static void main(String[] args) {
        // set up connection to remote SQL database
        DBAccess dbAccess = new DBAccess("*****", "*****", "*****");
        dbAccess.openConnection();

        // create DAO instances
        UserDAO userDAO = new UserDAO(dbAccess);
        CourseDAO courseDAO = new CourseDAO(dbAccess);

        // read CSV file and store each line as Group object
        File groupFile = new File ("src/main/resources/CSV bestanden/Groepen.csv");
        List<Group> groupList = new ArrayList<>();
        try {
            String line;
            Scanner scanner = new Scanner(groupFile);
            while (scanner.hasNextLine()){
                line = scanner.nextLine();
                String[] regelSplit = line.split(",");
                String courseName = regelSplit[0];
                String groupName = regelSplit[1];
                int groupSize = Integer.parseInt(regelSplit[2]);
                String teacherUsername = regelSplit[3];
                User teacher = userDAO.getUserPerUsername(teacherUsername);
                Course course = courseDAO.getCoursePerName(courseName);
                groupList.add(new Group(groupName, groupSize, teacher, course));
            }
        } catch (FileNotFoundException bestandFout) {
            System.out.println("Bestand niet gevonden.");
        }
        GroupDAO groupdao = new GroupDAO(dbAccess);

        // store each Group object in SQL database
        for (Group group : groupList){
            groupdao.storeOne(group);
        }
        dbAccess.closeConnection();
    }
}
