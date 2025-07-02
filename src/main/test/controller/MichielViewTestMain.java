/**
 * Test-mainklasse for testing specific views
 * @author Michiel van Haren
 */


package controller;

import database.mysql.DBAccess;
import javafx.application.Application;
import javafx.stage.Stage;
import view.SceneManager;


public class MichielViewTestMain extends Application {
    private static DBAccess dbAccess = null;
    private static SceneManager sceneManager = null;
    private static Stage primaryStage = null;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        MichielViewTestMain.primaryStage = primaryStage;
        primaryStage.setTitle("Test Michiel");
        // for testing Coordinator Dashboard view
        getSceneManager().showCoordinatorDashboard();

        // getSceneManager().showManageQuizScene();
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

}