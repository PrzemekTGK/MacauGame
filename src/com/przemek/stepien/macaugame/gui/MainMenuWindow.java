package com.przemek.stepien.macaugame.gui;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.awt.*;

/**
 * Created by Przemek Stepien on 30-Nov-15.
 */
public class MainMenuWindow extends Application {
    private Stage window;

    @Override
    public void start(Stage primaryStage) throws Exception {

        window = primaryStage;
        VBox layout = new VBox(40);
        Scene scene = new Scene(layout, 800, 600);
        Label title = new Label("MACAU");

        Button startButton = new Button("START GAME");
        Button quitButton = new Button("QUIT GAME");

        layout.getChildren().addAll(title, startButton, quitButton);
        layout.setAlignment(Pos.CENTER);
        title.setFont(javafx.scene.text.Font.font(Font.ROMAN_BASELINE));

        layout.setStyle("-fx-background-color: green");
        title.setStyle("-fx-text-fill: red; -fx-font-size: 100; -fx-font-weight: bold");
        startButton.setStyle("-fx-font-size: 20; -fx-font-weight: bold");
        quitButton.setStyle("-fx-font-size: 20; -fx-font-weight: bold");

        window.setScene(scene);
        window.sizeToScene();


        startButton.setOnAction(event -> {
            try {
                GameWindow gameWindow = new GameWindow();
                window.setScene(gameWindow.getScene());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        quitButton.setOnAction(event -> window.close());

        window.show();
    }

    public static void main(String[] args){ launch(args);}
}
