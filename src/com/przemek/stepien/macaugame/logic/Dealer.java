package com.przemek.stepien.macaugame.logic;

/**
 * Created by Przemek Stepien on 01/12/2015.
 */
public class Dealer {

    // Dealer's deck object
    private Deck deck;

    // Get's the deck object
    public Deck getDeck() {
        return deck;
    }

    // Deals any random card to the player
    public Card dealCard(){
        return deck.getCard();
    }

    // Deals a specific stating card to be set on the table
    public Card dealStartCard(){
        return deck.getStartCard();
    }

    // Refills deck when it's empty
    public void refillDeck (){

    }

    // Dealers constructor
    public Dealer(){
        this.deck = new Deck();
    }
}
