package com.przemek.stepien.macaugame.logic;

import com.przemek.stepien.macaugame.gui.GameWindow;
import com.przemek.stepien.macaugame.logic.Card.Rank;
import com.przemek.stepien.macaugame.logic.Card.Suit;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Przemek Stepien on 30/11/2015.
 */
public class Deck {

    // Cards in deck
    private ArrayList<Card> cards = new ArrayList<>();

    // Gets the deck of cards
    public ArrayList<Card> getCards() {
        return cards;
    }

    // Gets amount of remaining cards in the deck
    public int getDeckSize(){
        return this.cards.size();
    }

    // Gets any random card from the deck
    public Card getCard(){
        Random rand = new Random(System.nanoTime());
        int randomIndex = rand.nextInt(cards.size());
        Card tempCard;
        tempCard = cards.get(randomIndex);
        cards.remove(randomIndex);
        GameWindow.deckPane.getChildren().remove(GameWindow.deckPane.getChildren().size() - 1);
        return tempCard;
    }

    // Gets a specific starting card to be set on the table at the start of the game
    public Card getStartCard(){
        Card tempCard;
        boolean validCard = false;

        do {
            Random rand = new Random(System.nanoTime());
            int randomIndex = rand.nextInt(cards.size());
            tempCard = cards.get(randomIndex);
//            System.out.println(tempCard.getID());
            if(tempCard.getID().charAt(1) != 'j' && tempCard.getID().charAt(1)
                    != 'q' && tempCard.getID().charAt(1) != 'k') {
                if (Integer.parseInt(tempCard.getID().substring(1)) >= 5
                        && Integer.parseInt(tempCard.getID().substring(1)) <= 10) {
                    cards.remove(randomIndex);
                    validCard = true;
                } else {
                    continue;
                }
            } else {
                continue;
            }
        } while(!validCard);
        GameWindow.deckPane.getChildren().remove(GameWindow.deckPane.getChildren().size() - 1);
//        System.out.println(getDeckSize());
        return tempCard;
    }

    // Deck's constructor
    public Deck (){
        int i = 0;
        for(Suit s : Suit.values()){
            for(Rank r : Rank.values()){
                cards.add(new Card(s, r));
                cards.get(i).setFrontImage(new Image("com/przemek/stepien/macaugame/Cards/" + cards.get(i).getID() + ".png"));
                cards.get(i).setBackImage(new Image("com/przemek/stepien/macaugame/Cards/b1fv.png"));
                i++;
            }
        }
    }
}
