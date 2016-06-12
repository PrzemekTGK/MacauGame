package com.przemek.stepien.macaugame.logic;

import java.util.ArrayList;

/**
 * Created by Przemek Stepien on 01/12/2015.
 */
public class Player {

    // Player's components
    private ArrayList<Card> hand;
    private String playerName;
    private boolean playerTurn;
    private boolean playerWait;
    private int waitTurns;


    // Player takes a card from deck
    public void takeCard(Card card){
        hand.add(card);
    }

    // Player's components getters and setters
    public String getPlayerName() {
        return playerName;
    }
    public ArrayList<Card> getHand() {
        return hand;
    }
    public boolean isPlayerTurn() {
        return playerTurn;
    }
    public void setPlayerTurn(boolean playerTurn) {
        this.playerTurn = playerTurn;
    }
    public boolean isPlayerWait() {return playerWait;}
    public void setPlayerWait(boolean playerWait) {this.playerWait = playerWait;}
    public int getWaitTurns() {return waitTurns;}
    public void setWaitTurns(int waitTurns) {this.waitTurns = waitTurns;}

    // Player's constructor
    public Player(String name){
        this.hand = new ArrayList<>();
        this.playerTurn = false;
        this.playerName = name;
        this.playerWait = false;
        this.waitTurns = 0;
    }
}
