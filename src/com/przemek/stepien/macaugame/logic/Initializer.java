package com.przemek.stepien.macaugame.logic;

import com.przemek.stepien.macaugame.gui.GameWindow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Przemek Stepien on 01/12/2015.
 */
public class Initializer {

    // Game components
    public static Dealer dealer;
    static ArrayList<Player> players;
    static Table table;
    public static Player currentPlayer;

    // Game components's getters
    public Dealer getDealer() {
        return dealer;
    }
    public ArrayList<Player> getPlayers() {
        return players;
    }
    public Table getTable() {
        return table;
    }

    // Initializes start of the game
    public void initGame(){

        // Displays the deck on the table
        for(int i = 0; i < getDealer().getDeck().getDeckSize(); ++i){
            GameWindow.deckImage.add(new ImageView(new Image("com/przemek/stepien/macaugame/Cards/b1fv.png")));
            GameWindow.deckPane.getChildren().add(GameWindow.deckImage.get(i));
            GameWindow.deckImage.get(i).setLayoutX(i * - 1.5);
        }

        // Initializes players
        for(int i = 0; i < 2; ++i){
            getPlayers().add(new Player("Player " + (i + 1)));
        }

        // Deals cards to players
        for(int i = 0; i < getPlayers().size(); ++i){
            for(int j = 0; j < 5; ++j){

                // Deals 5 cards to each player
                getPlayers().get(i).takeCard(getDealer().dealCard());
                GameWindow.deckImage.remove(j);

                // Lays out player's cards images on the table
                if(i == 0) {
                    GameWindow.hBoxTop.getChildren().add(getPlayers().get(i).getHand().get(j).getFrontImageView());
                } else if (i ==1) {
                    GameWindow.hBoxBot.getChildren().add(getPlayers().get(i).getHand().get(j).getFrontImageView());
                }
            }
        }

        // Deals starting card to the table
        getTable().addCardToTable(getDealer().dealStartCard());
        GameWindow.cardsOnTableLayout.getChildren().add(getTable().getTopCard().getFrontImageView());

        // Sets random player's turn for the first round
        Random rand = new Random(System.nanoTime());
        int playerIndex = rand.nextInt(getPlayers().size());
        getPlayers().get(playerIndex).setPlayerTurn(true);
    }

    // Starts the game
    public void startGame(){
        for(int i = 0; i < players.size(); ++i){
            if(players.get(i).isPlayerTurn()){
                currentPlayer = getPlayers().get(i);

            }
        }
        Utility.hasValidCards();
    }

    // Initializer constructor
    public Initializer(){
        this.dealer = new Dealer();
        this.players = new ArrayList<>();
        this.table = new Table();
    }
}
