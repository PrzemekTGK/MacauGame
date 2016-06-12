package com.przemek.stepien.macaugame.logic;

import com.przemek.stepien.macaugame.gui.GameWindow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Created by Przemek Stepien on 30/11/2015.
 */
public class Card{

    // Card's Suit and Rank enums
    protected enum Suit {
        HEARTS("h"), DIAMONDS("d"), CLUBS("c"), SPADES("s");

        private String suitName;

        Suit(String suitName){
            this.suitName = suitName;
        }

        @Override
        public String toString() {
            return suitName;
        }
    }

    protected enum Rank{
        TWO('2'), THREE('3'), FOUR('4'), FIVE('5'), SIX('6'), SEVEN('7'), EIGHT('8'), NINE('9'), TEN('0'), JACK('j'),
        QUEEN('q'), KING('k'), ACE('1');

        private char rankID;

        Rank(char rankID){
            this.rankID = rankID;
        }

        @Override
        public String toString(){
            return rankID + "";
        }
    }

    // Card's Components
    private Rank rank;
    private Suit suit;
    private String id;
    private ImageView frontImage;
    private ImageView backImage;

    // Card's getters and setters
    public String getID (){
        return this.id;
    }
    public ImageView getFrontImageView() {
        return frontImage;
    }
    public void setFrontImage(Image frontImage){
        this.frontImage.setImage(frontImage);
    }
    public void setBackImage(Image backImage){
        this.backImage.setImage(backImage);
    }

    // Card's constructor
    public Card(Suit suit, Rank rank){

        this.suit = suit;
        this.rank = rank;
        this.frontImage = new ImageView();
        this.backImage = new ImageView();
        if(this.rank.toString().equals("0")) {
            this.id = this.suit.toString() + "1" + this.rank.toString();
        } else {
            this.id = this.suit.toString() + this.rank.toString();
        }

        this.frontImage.setId(this.getID());

        Utility.makeCardMovable(this.getFrontImageView(), GameWindow.cardsOnTableLayout);
    }
}
