package view;

import database.mysql.DBAccess;
import javacouchdb.CouchDBAccess;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    private static DBAccess dbAccess = null;
    private static CouchDBAccess couchDBAccess = null;
    private static SceneManager sceneManager = null;
    static Stage primaryStage = null;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Main.primaryStage = primaryStage;
        primaryStage.setTitle("Make IT Work - Project 1");
        getSceneManager().showLoginScene();
        primaryStage.show();
    }

    // singleton, always only one instance of scenemanager
    public static SceneManager getSceneManager() {
        if (sceneManager == null) { // if null, new instance of scenemanager
            sceneManager = new SceneManager(primaryStage);
        }
        return sceneManager;
    }

    public static DBAccess getDBaccess() {
        if (dbAccess == null) {
            dbAccess = new DBAccess("*****", "*****", "*****");
            dbAccess.openConnection();
        }
        return dbAccess;
    }

    public static CouchDBAccess getCouchDBAccess() {
        if (couchDBAccess == null) {
            couchDBAccess = new CouchDBAccess("*****", "*****", "*****");
            couchDBAccess.getClient();
        }
        return couchDBAccess;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }
}