package com.valinor.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main JavaFX application for the Valinor Restaurant Reservation System.
 * Entry point for the GUI application.
 */
public class RestaurantApp extends Application {

    private static final Logger logger = LoggerFactory.getLogger(RestaurantApp.class);
    private static final String APP_TITLE = "Valinor Restaurant Management System";
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        try {
            primaryStage = stage;
            logger.info("Starting Valinor Restaurant Management System");

            // Load login view
            showLoginView();

            stage.setTitle(APP_TITLE);
            stage.show();

        } catch (Exception e) {
            logger.error("Failed to start application", e);
            throw new RuntimeException("Failed to start application", e);
        }
    }

    /**
     * Shows the login view
     */
    private void showLoginView() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 400, 500);
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
    }

    /**
     * Gets the primary stage
     * @return primary stage
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Switches to a new scene
     * @param fxmlFile FXML file name
     * @param title window title
     * @param width window width
     * @param height window height
     */
    public static void switchScene(String fxmlFile, String title, int width, int height) {
        try {
            FXMLLoader loader = new FXMLLoader(RestaurantApp.class.getResource("/fxml/" + fxmlFile));
            Parent root = loader.load();
            Scene scene = new Scene(root, width, height);
            scene.getStylesheets().add(RestaurantApp.class.getResource("/css/styles.css").toExternalForm());

            primaryStage.setScene(scene);
            primaryStage.setTitle(APP_TITLE + " - " + title);
            primaryStage.setResizable(true);

        } catch (Exception e) {
            logger.error("Failed to switch scene to: " + fxmlFile, e);
            throw new RuntimeException("Failed to switch scene", e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
