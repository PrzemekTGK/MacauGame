package com.przemek.stepien.macaugame.logic;

import java.util.ArrayList;

/**
 * Created by Przemek Stepien on 01/12/2015.
 */
public class Table {

    // Cards on the table that were played with by players
    private ArrayList<Card> tableCards = new ArrayList<>();

    // Adds a card to the table
    public void addCardToTable(Card card){
        tableCards.add(card);
    }

    // Table's cards getter
    public ArrayList<Card> getTableCards() {
        return tableCards;
    }

    // Table's top card getter
    public Card getTopCard(){
        return tableCards.get(tableCards.size()-1);
    }
}
