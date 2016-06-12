package com.przemek.stepien.macaugame.gui;

import com.przemek.stepien.macaugame.logic.Initializer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

/**
 * Created by Przemek Stepien on 30-Nov-15.
 */
public class GameWindow {

    public static Label label;
    public static HBox hBoxTop;
    public static HBox hBoxMid;
    public static HBox hBoxBot;
    public static HBox hBoxTakeDefendButtons;
    public static Pane deckPane;
    public static StackPane cardsOnTableLayout;
    public static ArrayList<ImageView> deckImage;
    public static ComboBox<String> ranksBox;
    public static ComboBox<String> suitsBox;
    public static Button changeConfirmButton;
    public static Button continueButton;
    public static Button takeWaitButton;
    public static Button defendButton;

    // Returns the game scene
    public Scene getScene() throws Exception {

        ObservableList<String> ranks = FXCollections.observableArrayList();
        ObservableList<String> suits = FXCollections.observableArrayList();
        Pane dummyPane = new Pane();
        StackPane buttonsPane = new StackPane();
        StackPane messagePane = new StackPane();
        StackPane comboBoxPane = new StackPane();
        ScrollPane topScrollPane = new ScrollPane();
        ScrollPane botScrollPane = new ScrollPane();
        VBox deckVBox = new VBox(20);
        VBox mainVBox = new VBox(20);
        VBox msgVBox = new VBox(20);
        Scene scene = new Scene(mainVBox, 800, 600);

        ranksBox = new ComboBox<>();
        suitsBox = new ComboBox<>();
        deckPane = new Pane();
        label = new Label();
        hBoxTop = new HBox(5);
        hBoxBot = new HBox(5);
        hBoxMid = new HBox(100);
        hBoxTakeDefendButtons = new HBox(10);
        deckImage = new ArrayList<>();
        cardsOnTableLayout = new StackPane();
        changeConfirmButton = new Button("Confirm");
        continueButton = new Button("Continue");
        takeWaitButton = new Button("Take / Wait");
        defendButton = new Button("Defend");

        hBoxMid.getChildren().addAll(dummyPane, messagePane, cardsOnTableLayout, deckVBox);
        hBoxTakeDefendButtons.getChildren().addAll(takeWaitButton, defendButton);

        mainVBox.getChildren().addAll(topScrollPane, hBoxMid, botScrollPane);
        msgVBox.getChildren().addAll(label, comboBoxPane, buttonsPane);
        deckVBox.getChildren().add(deckPane);
        messagePane.getChildren().add(msgVBox);
        buttonsPane.getChildren().addAll(continueButton, changeConfirmButton, hBoxTakeDefendButtons);

        msgVBox.setAlignment(Pos.CENTER);
        deckVBox.setAlignment(Pos.CENTER);
        messagePane.setAlignment(Pos.CENTER);

        hBoxTop.setAlignment(Pos.CENTER);
        hBoxMid.setAlignment(Pos.CENTER);
        hBoxBot.setAlignment(Pos.CENTER);
        hBoxTakeDefendButtons.setAlignment(Pos.CENTER);
        cardsOnTableLayout.setAlignment(Pos.CENTER);

        hBoxTop.setPrefSize(800,200);
        hBoxMid.setPrefSize(800,200);
        hBoxBot.setPrefSize(800,200);
        cardsOnTableLayout.setPrefSize(200, 0);

        messagePane.setPrefSize(200, 200);

        mainVBox.setStyle("-fx-background-color: blue");

        hBoxTop.setStyle("-fx-background-color: green");
        hBoxMid.setStyle("-fx-background-color: green");
        hBoxBot.setStyle("-fx-background-color: green");
        cardsOnTableLayout.setStyle("-fx-background-color: red; -fx-border-width: 10; -fx-border-color: blue");

        ranksBox.setItems(ranks);
        suitsBox.setItems(suits);
        ranksBox.setVisible(false);
        suitsBox.setVisible(false);
        continueButton.setVisible(false);
        changeConfirmButton.setVisible(false);
        hBoxTakeDefendButtons.setVisible(false);

        ranks.addAll("5", "6", "7", "8", "9", "10");
        suits.addAll("hearts", "diamonds", "clubs", "spades");
        comboBoxPane.getChildren().addAll(ranksBox,suitsBox);
        topScrollPane.setContent(hBoxTop);
        botScrollPane.setContent(hBoxBot);
        topScrollPane.setFitToHeight(true);
        topScrollPane.setFitToWidth(true);
        botScrollPane.setFitToHeight(true);
        botScrollPane.setFitToWidth(true);

        Initializer game = new Initializer();
        game.initGame();
        game.startGame();

        label.setStyle("-fx-text-fill: blue; -fx-font-size: 12; -fx-font-weight: bold");
        label.setText(Initializer.currentPlayer.getPlayerName() + " Start Game");
        label.setWrapText(true);

        return scene;
    }
}

