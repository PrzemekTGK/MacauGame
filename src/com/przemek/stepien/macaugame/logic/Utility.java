package com.przemek.stepien.macaugame.logic;

import com.przemek.stepien.macaugame.gui.GameWindow;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;

import static com.przemek.stepien.macaugame.logic.Initializer.*;

/**
 * Created by Przemek Stepien on 03/12/2015.
 */
public class Utility {

    private static int roundCounter = 0;
    private static int numSameCards = 0;
    private static int numSameAttackCards = 0;
    private static int numSameDefendCards = 0;
    private static int numSameWaitCards = 0;
    private static int numSameKingCards = 0;
    private static boolean isEndTurn;
    private static boolean isCardPlayed;
    private static boolean isSameCards = false;
    private static boolean isAceChange = false;
    private static boolean isJackChange = false;
    private static boolean isPlayerDefending = false;
    private static boolean isPlayerWon = false;
    private static String aceChangeID = null;
    private static String jackChangeID = null;
    private static String sameCardID = null;
    private static String defendCardID = null;

    // Makes card movable
    public static void makeCardMovable(ImageView source, Node target) {

        /*
         *  Enables the source card to be clicked and dragged around
         *  the screen but not to be released and dropped anywhere provided
         *  that the card is highlighted as a valid card to be put on the table
         */
        source.setOnDragDetected(dragDetectEvent -> {

            // Enables cards of the same rank that are being chained together
            if (isSameCards) {

                if (source.getId().substring(1).equals(sameCardID.substring(1))) {
                    Dragboard db = source.startDragAndDrop(TransferMode.ANY);
                    ClipboardContent content = new ClipboardContent();
                    content.putImage(source.getImage());
                    db.setContent(content);
                }

                // Enables same rank action cards if player is defending
            } else if (isPlayerDefending) {

                if (defendCardID.equals("hk") && source.getId().equals("sk")) {
                    Dragboard db = source.startDragAndDrop(TransferMode.ANY);
                    ClipboardContent content = new ClipboardContent();
                    content.putImage(source.getImage());
                    db.setContent(content);
                } else if (defendCardID.equals("sk") && source.getId().equals("hk")) {
                    Dragboard db = source.startDragAndDrop(TransferMode.ANY);
                    ClipboardContent content = new ClipboardContent();
                    content.putImage(source.getImage());
                    db.setContent(content);
                } else if (source.getId().substring(1).equals(defendCardID.substring(1))) {
                    Dragboard db = source.startDragAndDrop(TransferMode.ANY);
                    ClipboardContent content = new ClipboardContent();
                    content.putImage(source.getImage());
                    db.setContent(content);
                }

                // Doesn't enable any cards if player has won
            } else if (isPlayerWon) {

                // Enables valid cards that are not being chained yet to be put on the table based on required criteria
            } else {

                // Enables valid cards for after Ace card was played and changed the required suit
                if (isAceChange) {

                    if (checkIsValidForAce(source)) {
                        Dragboard db = source.startDragAndDrop(TransferMode.ANY);
                        ClipboardContent content = new ClipboardContent();
                        content.putImage(source.getImage());
                        db.setContent(content);
                    }

                    // Enables valid cards for after Jack card was played and changed the required rank
                } else if (isJackChange) {

                    if (checkIsValidForJack(source)) {
                        Dragboard db = source.startDragAndDrop(TransferMode.ANY);
                        ClipboardContent content = new ClipboardContent();
                        content.putImage(source.getImage());
                        db.setContent(content);
                    }

                    // Enables any valid cards for a regular play without action cards on the table
                } else if (checkIsValidCard(source)) {

                    System.out.println("Check is Valid");
                    Dragboard db = source.startDragAndDrop(TransferMode.ANY);
                    ClipboardContent content = new ClipboardContent();
                    content.putImage(source.getImage());
                    db.setContent(content);
                }
            }

            /*
             *  Checks if the source card is valid to be dropped on target card
             *  and if it is, it allows the source card to be dropped on target
             */
            target.setOnDragOver(dragOverEvent -> {

                // Checks if the card is valid to be dropped without Ace or Jack card being played before
                if (!isAceChange && !isJackChange) {

                    if (dragOverEvent.getGestureSource() != target && (dragOverEvent.getDragboard().hasImage())
                            && checkIsValidCard(source)) {
                        dragOverEvent.acceptTransferModes(TransferMode.MOVE);
                    }

                    // Checks if the card is valid after Ace card has been played
                } else if (isAceChange) {

                    if ((dragOverEvent.getGestureSource() != target) && (dragOverEvent.getDragboard().hasImage())
                            && checkIsValidForAce(source)) {
                        dragOverEvent.acceptTransferModes(TransferMode.MOVE);
                    }

                    // Checks if the card is valid after Jack card has been played
                } else if (isJackChange) {

                    if ((dragOverEvent.getGestureSource() != target) && (dragOverEvent.getDragboard().hasImage())
                            && checkIsValidForJack(source)) {
                        dragOverEvent.acceptTransferModes(TransferMode.MOVE);
                    }
                }

                dragOverEvent.consume();
            });

            // Gives a visual hint of the target that it can take the source
            target.setOnDragEntered(dragEnteredEvent -> {

                if (dragEnteredEvent.getGestureSource() != target &&
                        dragEnteredEvent.getDragboard().hasImage()) {
                    target.setStyle("-fx-background-color: blue; -fx-border-width: 10; -fx-border-color: blue");
                }

                dragEnteredEvent.consume();
            });

            // Removes the visual hint of the target
            target.setOnDragExited(dragExitedEvent -> {

                target.setStyle("-fx-background-color: red; -fx-border-width: 10; -fx-border-color: blue");
                dragExitedEvent.consume();
            });

            /*
             * Moves the card image from hand to table on the GUI side of the application and
             * moves the actual Card object from player's hand ArrayList to table's ArrayList of cards
             */
            target.setOnDragDropped(dragDroppedEvent -> {

                boolean playerHasCard = false;
                boolean success = false;
                boolean imageMoved = false;
                Card tempCard = null;
                Dragboard db1 = dragDroppedEvent.getDragboard();


                // Checks if source card is in current player's hand
                for (int i = 0; i < currentPlayer.getHand().size(); ++i) {

                    if (currentPlayer.getHand().get(i).getID().equals(source.getId())) {
                        playerHasCard = true;
                    }
                }

                // If source card is in current player hand it's image will be put on the table
                if (db1.hasImage() && playerHasCard) {

                    System.out.println("Card played - " + source.getId());
                    ((StackPane) target).getChildren().add(source);
                    success = true;
                    imageMoved = true;
                    isCardPlayed = true;
                }

                // Moves actual card object to table's cards array list
                if (imageMoved) {

                    // Removes actual card object from players hand and put it on the table
                    for (int i = 0; i < currentPlayer.getHand().size(); ++i) {
                        if (currentPlayer.getHand().get(i).getID().equals(source.getId())) {
                            tempCard = currentPlayer.getHand().get(i);
                            currentPlayer.getHand().remove(i);
                            table.addCardToTable(tempCard);
                            break;
                        }
                    }

                    numSameCards = 0;

                    // Checks if there's more cards of the same rank in player's hand
                    // as the card that was played and counts the amount of those cards
                    for (int i = 0; i < currentPlayer.getHand().size(); ++i) {

                        if (currentPlayer.getHand().get(i).getID().endsWith(source.getId().substring(1))) {
                            numSameCards++;
                        }
                    }

                    // If there is no same rank cards in player's hand it allows the round to be ended
                    // and if there is more cards of the same rank in player's hand it allows the player
                    // to chain those cards together before the end of player's turn
                    if (numSameCards < 1) {

                        isEndTurn = true;
                        isSameCards = false;

                    } else {

                        isSameCards = true;
                        sameCardID = source.getId();
                        isEndTurn = false;
                    }

                    tempCard = null;

                    // Disables suit modification after suit being changed by Ace card
                    // and a valid card was played after that change.
                    if (isAceChange && !source.getId().endsWith("1")) {

                        isAceChange = false;
                    }

                    // Disables suit modification after suit being changed by Jack card
                    // and a valid card was played after that change.
                    if (isJackChange && !source.getId().endsWith("j")) {

                        isJackChange = false;
                    }

                    rehighlightValidCards();
                }

                imageMoved = false;
                dragDroppedEvent.setDropCompleted(success);
                dragDroppedEvent.consume();
            });

            // Switches player's turn and performs required checks at the start of next player's turn
            source.setOnDragDone(dragDoneEvent -> {

                // Checks for end of the turn after the card was played
                // or if player can chain or defend before ending turn
                if (isEndTurn) {

                    // Checks if the card played was an wait/attack card, increments the amount of
                    // specific action cards and ends the current player's turn to switch to the next player
                    if (table.getTopCard().getID().endsWith("2") || table.getTopCard().getID().endsWith("3")) {

                        // Check for "2" and "3" attack cards
                        if (source.getId().endsWith("2") || source.getId().endsWith("3")) {
                            ++numSameAttackCards;
                            System.out.println("Same attack cards increment 1: " + numSameAttackCards);
                        }
                    } else if (table.getTopCard().getID().endsWith("4")) {

                        // Check for a wait card "4"
                        if (source.getId().endsWith("4")) {
                            System.out.println("Same wait cards increment 1: " + ++numSameWaitCards);
                        }
                    } else {

                        // Check for attack King cards: King of Hearts and King of Spades
                        if (table.getTopCard().getID().equals("hk") || table.getTopCard().getID().equals("sk")) {
                            System.out.println(table.getTopCard().getID());
                            numSameKingCards++;
                        }
                    }


                    GameWindow.defendButton.setOnAction(event -> {

                        isSameCards = false;
                        sameCardID = null;
                        defendCardID = null;
                        numSameCards = 0;
                        numSameDefendCards = 0;
                        checkForCardModifier();
                    });

                    GameWindow.takeWaitButton.setOnAction(event -> {

                        isSameCards = false;
                        sameCardID = null;
                        defendCardID = null;
                        numSameCards = 0;
                        numSameDefendCards = 0;
                        isPlayerDefending = false;

                        System.out.println("Take/wait button pressed");

                        if (table.getTopCard().getID().endsWith("4")) {
                            System.out.println("Take/wait button pressed with a 4 card on table");
                            resetHighlightCards();
                            checkIsPlayerWait();
                        } else {
                            System.out.println("Take/wait button pressed with a non 4 card on table");
                            checkIsPlayerTake(numSameAttackCards, numSameKingCards);
                            GameWindow.label.setText(currentPlayer.getPlayerName() + " turn");
                        }

                        for (int i = 0; i < currentPlayer.getHand().size(); ++i) {

                            if (currentPlayer.getHand().get(i).getID().charAt(0)
                                    == table.getTopCard().getID().charAt(0)
                                    || currentPlayer.getHand().get(i).getID().substring(1).equals(table.getTopCard()
                                    .getID().substring(1))
                                    || currentPlayer.getHand().get(i).getID().endsWith("q")) {
                                highlightValidCards(i);
                            }
                        }


                        GameWindow.hBoxTakeDefendButtons.setVisible(false);
                        checkIsPlayerTake(numSameAttackCards, numSameKingCards);
                    });

                    System.out.println("Same attack cards " + numSameAttackCards);
                    checkForCardModifier();
                    GameWindow.continueButton.setVisible(false);

                    // Checks for player chaining or defending without ending the turn yet
                } else {

                    // Check's if the actual card was played and put on the table
                    // rather than just dropped anywhere on the screen
                    if (isCardPlayed) {

                        System.out.println("Same Wait cards: " + numSameWaitCards);

                        if (isPlayerDefending) {

                            if (GameWindow.defendButton.isDisable()) {
                                GameWindow.defendButton.setDisable(false);
                            }

                            if (GameWindow.takeWaitButton.isVisible()) {
                                GameWindow.takeWaitButton.setDisable(true);
                            }

                            numSameDefendCards--;
                            if (source.getId().endsWith("2") || source.getId().endsWith("3")) {

                                ++numSameAttackCards;

                            } else if (source.getId().endsWith("4")) {

                                System.out.println("Same wait cards increment 3: " + ++numSameWaitCards);
                            } else {

                                if (table.getTopCard().getID().equals("hk") || table.getTopCard().getID().equals("sk")) {
                                    ++numSameKingCards;
                                }
                            }

                            GameWindow.label.setText(currentPlayer.getPlayerName() + " can defend with "
                                    + numSameDefendCards + " card(s)");


                        } else {

                            if (source.getId().endsWith("2") || source.getId().endsWith("3")) {

                                ++numSameAttackCards;

                            } else if (source.getId().endsWith("4") && !isPlayerDefending) {
                                System.out.println("Same wait cards increment 2: " + ++numSameWaitCards);
                            } else {
                                if (table.getTopCard().getID().equals("hk") || table.getTopCard().getID().equals("sk")) {
                                    ++numSameKingCards;
                                }
                            }
                            System.out.println("Same attack cards increment 2: " + numSameAttackCards);

                            numSameDefendCards = 0;
                            isPlayerDefending = false;
                            GameWindow.label.setText(currentPlayer.getPlayerName() + " has " + numSameCards
                                    + " more card(s) of same rank");
                            GameWindow.continueButton.setVisible(true);
                        }

                        GameWindow.continueButton.setOnAction(event -> {

                            isSameCards = false;
                            sameCardID = null;
                            numSameCards = 0;
                            checkForCardModifier();
                            GameWindow.continueButton.setVisible(false);
                        });

                        GameWindow.defendButton.setOnAction(event -> {

                            isSameCards = false;
                            sameCardID = null;
                            defendCardID = null;
                            numSameCards = 0;
                            numSameDefendCards = 0;
                            checkForCardModifier();
                        });

                        GameWindow.takeWaitButton.setOnAction(event -> {

                            isSameCards = false;
                            sameCardID = null;
                            defendCardID = null;
                            numSameCards = 0;
                            numSameDefendCards = 0;
                            isPlayerDefending = false;

                            System.out.println("Take/wait button pressed");

                            if (table.getTopCard().getID().endsWith("4")) {
                                System.out.println("Take/wait button pressed with a 4 card on table");
                                resetHighlightCards();
                                checkIsPlayerWait();
                            } else {
                                System.out.println("Take/wait button pressed with a non 4 card on table");
                                checkIsPlayerTake(numSameAttackCards, numSameKingCards);
                                GameWindow.label.setText(currentPlayer.getPlayerName() + " turn");
                            }

                            for (int i = 0; i < currentPlayer.getHand().size(); ++i) {

                                if (currentPlayer.getHand().get(i).getID().charAt(0)
                                        == table.getTopCard().getID().charAt(0)
                                        || currentPlayer.getHand().get(i).getID().substring(1).equals(table.getTopCard()
                                        .getID().substring(1))
                                        || currentPlayer.getHand().get(i).getID().endsWith("q")) {
                                    highlightValidCards(i);
                                }
                            }


                            GameWindow.hBoxTakeDefendButtons.setVisible(false);
                            checkIsPlayerTake(numSameAttackCards, numSameKingCards);
                        });

                        isCardPlayed = false;
                    }
                }
                dragDoneEvent.consume();
            });
            dragDetectEvent.consume();
        });
    }

    /*
     * Performs table card checks right before player's turn ends, switches player's turn
     * to next player and performs table card checks at the start of next player's turn
     */
    private static void checkForCardModifier() {
        resetHighlightCards();
        for (int i = 0; i < GameWindow.cardsOnTableLayout.getChildren().size(); ++i) {
            GameWindow.cardsOnTableLayout.getChildren().get(i).setScaleX(1);
            GameWindow.cardsOnTableLayout.getChildren().get(i).setScaleY(1);
            GameWindow.cardsOnTableLayout.getChildren().get(i).setScaleZ(1);
            GameWindow.cardsOnTableLayout.getChildren().get(i).setStyle("-fx-effect: null");
        }
        if (checkForAce()) {
            GameWindow.label.setText(currentPlayer.getPlayerName() + " pick the new suit");
            GameWindow.suitsBox.setPromptText("Pick a suit");
            GameWindow.suitsBox.setVisible(true);
            GameWindow.changeConfirmButton.setVisible(true);
            GameWindow.changeConfirmButton.setOnAction(event -> {
                if (checkComboBox(GameWindow.suitsBox)) {
                    setSuitChangeID();
                    GameWindow.suitsBox.getSelectionModel().select(-1);
                    newTurnCheck();
                    GameWindow.suitsBox.setVisible(false);
                    GameWindow.changeConfirmButton.setVisible(false);
                    hasValidCards();
                    isEndTurn = false;
                    isCardPlayed = false;
                    numSameCards = 0;
                    numSameWaitCards = 0;
                    sameCardID = null;
                }
            });
        } else if (checkForJack()) {
            GameWindow.label.setText(currentPlayer.getPlayerName() + " pick the new rank");
            GameWindow.ranksBox.setPromptText("Pick a rank");
            GameWindow.ranksBox.setVisible(true);
            GameWindow.changeConfirmButton.setVisible(true);
            GameWindow.changeConfirmButton.setOnAction(event -> {
                if (checkComboBox(GameWindow.ranksBox)) {
                    setRankChangeID();
                    GameWindow.ranksBox.getSelectionModel().select(-1);
                    newTurnCheck();
                    GameWindow.ranksBox.setVisible(false);
                    GameWindow.changeConfirmButton.setVisible(false);
                    hasValidCards();
                    isEndTurn = false;
                    isCardPlayed = false;
                    numSameCards = 0;
                    numSameWaitCards = 0;
                    sameCardID = null;
                }
            });
        } else {
            newTurnCheck();
            hasValidCards();
            isEndTurn = false;
            isCardPlayed = false;
            numSameCards = 0;
            if (!isPlayerDefending) {
                numSameWaitCards = 0;
            }
            sameCardID = null;
        }
    }

    /*
     * Switches player's turns and checks if next player is waiting or not. If next player is waiting it skips to next
     * and keeps skipping waiting player for as many turns as player has specified in his waitTurns variable
     */
    private static void newTurnCheck() {
        System.out.println("\n" + currentPlayer.getPlayerName() + " moves  non Waiting");

        System.out.println(currentPlayer.getPlayerName() + " waits " + currentPlayer.isPlayerWait());

        switchPlayerTurn();
        checkIsPlayerWait();
        Player tempPlayer = currentPlayer;
        if (isPlayerWon) {
            if (GameWindow.hBoxTakeDefendButtons.isVisible()) {
                GameWindow.hBoxTakeDefendButtons.setVisible(false);
            }
            resetHighlightCards();
        } else {
            if (isPlayerDefending) {
                GameWindow.label.setText(currentPlayer.getPlayerName() + " can defend with "
                        + numSameDefendCards + " card(s)");
            } else {
                GameWindow.label.setText(currentPlayer.getPlayerName() + " turn");
            }

            if (currentPlayer.isPlayerWait()) {

                currentPlayer.setWaitTurns(currentPlayer.getWaitTurns() - 1);
                if (currentPlayer.getWaitTurns() <= 0) currentPlayer.setPlayerWait(false);

                if (currentPlayer.isPlayerWait()) {
                    for (int i = 0; i < players.size(); ++i) {
                        if (players.get(i) == currentPlayer) {
                            System.out.println("Current player " + players.get(i).getPlayerName());
                            System.out.println("\nSwitching players during wait");
                            checkIsPlayerTake(numSameAttackCards, numSameKingCards);
                            players.get(i).setPlayerTurn(false);
                            if (i == players.size() - 1) {
                                players.get(0).setPlayerTurn(true);
                                currentPlayer = players.get(0);
                            } else {
                                players.get(i + 1).setPlayerTurn(true);
                                currentPlayer = players.get(i + 1);
                            }
                            break;
                        }
                    }
                    GameWindow.label.setText(tempPlayer.getPlayerName() + " waits " + tempPlayer.getWaitTurns() + " turn(s) 1, "
                            + currentPlayer.getPlayerName() + " moves");
                    System.out.println("\n" + currentPlayer.getPlayerName() + " moves at waiting");
                }

                if (isPlayerWon) {
                    GameWindow.label.setText(currentPlayer.getPlayerName() + " Won!");
                    resetHighlightCards();
                }
            }
        }
    }

    // Switches player's turn to next player
    private static void switchPlayerTurn() {

        System.out.println("\nSwitching players");
        if (isPlayerWin()) {

            GameWindow.label.setText(currentPlayer.getPlayerName() + " Won!");
            resetHighlightCards();
        } else {

            checkRefillDeck();

            for (int i = 0; i < players.size(); ++i) {
                if (players.get(i).isPlayerTurn() && i < players.size() - 1) {

                    players.get(i).setPlayerTurn(false);
                    players.get(i + 1).setPlayerTurn(true);
                    currentPlayer = players.get(i + 1);
                    break;
                } else if (players.get(i).isPlayerTurn() && i == players.size() - 1) {

                    players.get(i).setPlayerTurn(false);
                    players.get(0).setPlayerTurn(true);
                    currentPlayer = players.get(0);
                    break;
                }
            }

            roundCounter++;
            System.out.println("========================== End Turn ==========================");
            System.out.println("Round: " + roundCounter);
            System.out.println("\nSame King Cards: " + numSameKingCards);
            System.out.println("Same Attack Cards: " + numSameAttackCards);
            System.out.println("Same Wait Cards: " + numSameWaitCards);
            if (!isPlayerDefend()) {

                if (GameWindow.hBoxTakeDefendButtons.isVisible()) {
                    GameWindow.hBoxTakeDefendButtons.setVisible(false);
                }

                numSameDefendCards = 0;
                defendCardID = null;
                isPlayerDefending = false;
                checkIsPlayerTake(numSameAttackCards, numSameKingCards);
            } else if (isPlayerDefend() && !currentPlayer.isPlayerWait()) {

                numSameDefendCards = 0;
                isPlayerDefending = true;
                defendCardID = table.getTopCard().getID();
                GameWindow.hBoxTakeDefendButtons.setVisible(true);
                GameWindow.defendButton.setDisable(true);

                if (GameWindow.takeWaitButton.isDisabled()) {
                    GameWindow.takeWaitButton.setDisable(false);
                }

                for (int i = 0; i < currentPlayer.getHand().size(); ++i) {

                    if (defendCardID.equals("sk") && currentPlayer.getHand().get(i).getID().equals("hk")) {
                        numSameDefendCards = 1;
                        break;
                    } else if (defendCardID.equals("hk") && currentPlayer.getHand().get(i).getID().equals("sk")) {
                        numSameDefendCards = 1;
                        break;
                    } else if (!defendCardID.equals("sk") && !defendCardID.equals("hk")
                            && currentPlayer.getHand().get(i).getID().endsWith(defendCardID.substring(1))) {
                        ++numSameDefendCards;
                    }
                }
                //checkIsPlayerTake(numSameAttackCards, numSameKingCards);
            }

            if (isPlayerWon) {
                GameWindow.label.setText(currentPlayer.getPlayerName() + " Won!");
                resetHighlightCards();
            }
            System.out.println("\nDeck size: " + dealer.getDeck().getDeckSize());
            System.out.println("Table cards size: " + table.getTableCards().size());
        }
    }

    // Checks player's hand for valid cards
    private static int checkPlayersHand() {
        int validCards = 0;
        if (!isAceChange && !isJackChange && !isPlayerDefending) {
            for (int i = 0; i < currentPlayer.getHand().size(); ++i) {
                if (table.getTopCard().getID().endsWith("q")) {
                    validCards++;
                    highlightValidCards(i);
                } else if (currentPlayer.getHand().get(i).getID().charAt(0) == table.getTopCard().getID().charAt(0)
                        || currentPlayer.getHand().get(i).getID().substring(1).equals(table.getTopCard().getID().substring(1))
                        || currentPlayer.getHand().get(i).getID().endsWith("q")) {
                    validCards++;
                    highlightValidCards(i);
                }
            }
        } else if (isAceChange) {
            for (int i = 0; i < currentPlayer.getHand().size(); ++i) {
                if (currentPlayer.getHand().get(i).getID().endsWith("1")
                        || currentPlayer.getHand().get(i).getID().charAt(0) == aceChangeID.charAt(0)
                        || currentPlayer.getHand().get(i).getID().endsWith("q")) {
                    validCards++;
                    highlightValidCards(i);
                }
            }
        } else if (isJackChange) {
            for (int i = 0; i < currentPlayer.getHand().size(); ++i) {
                if (currentPlayer.getHand().get(i).getID().charAt(1) == 'j'
                        || currentPlayer.getHand().get(i).getID().substring(1).equals(jackChangeID.substring(1))
                        || currentPlayer.getHand().get(i).getID().endsWith("q")) {
                    validCards++;
                    highlightValidCards(i);
                }
            }
        } else if (isPlayerDefending) {
            for (int i = 0; i < currentPlayer.getHand().size(); ++i) {
                if (currentPlayer.getHand().get(i).getID().endsWith(defendCardID.substring(1))) {
                    System.out.println("Highlight cards " + i);
                    System.out.println("Table top card " + table.getTopCard().getID());
                    System.out.println("Defend card ID " + defendCardID);
                    validCards++;
                    highlightValidCards(i);
                }
            }
        }
        return validCards;
    }

    // Checks if current player has valid cards in hand
    public static void hasValidCards() {
        while (checkPlayersHand() == 0 && !isPlayerWon) {
            Card tempCard = Initializer.dealer.dealCard();
            currentPlayer.takeCard(tempCard);
            System.out.println(currentPlayer.getPlayerName() + " has taken " + tempCard.getID() + " card");
            if (currentPlayer.getPlayerName().endsWith("1")) {
                GameWindow.hBoxTop.getChildren().add(currentPlayer.getHand().get(
                        currentPlayer.getHand().size() - 1).getFrontImageView());
            } else if (currentPlayer.getPlayerName().endsWith("2")) {
                GameWindow.hBoxBot.getChildren().add(currentPlayer.getHand().get(
                        currentPlayer.getHand().size() - 1).getFrontImageView());
            }
            if (checkPlayersHand() > 0) {
                break;
            }
            newTurnCheck();
        }
    }

    // Checks current top card from the table to see if next player will be taking cards
    private static void checkIsPlayerTake(int actionRepeat, int kingRepeat) {
        for (int i = 0; i < actionRepeat; ++i) {
            if (table.getTopCard().getID().endsWith("2")) {
                playerTake2Cards();
                System.out.println(currentPlayer.getPlayerName() + " takes 2 cards");
            } else if (table.getTopCard().getID().endsWith("3")) {
                playerTake3Cards();
                System.out.println(currentPlayer.getPlayerName() + " takes 3 cards");
            }
        }

        for (int i = 0; i < kingRepeat; ++i) {
            if (table.getTopCard().getID().equals("hk")) {
                System.out.println(currentPlayer.getPlayerName() + " takes after king of hearts");
                kingOfHearts();

            } else if (table.getTopCard().getID().equals("sk")) {
                System.out.println(currentPlayer.getPlayerName() + " takes after king of spades");
                kingOfSpades();
            }
        }
    }

    // Checks current top card from the table to see if next player will be waiting
    private static void checkIsPlayerWait() {
        if (table.getTopCard().getID().endsWith("4")) {
            playerWait();
        }
    }

    // Player takes 2 cards from the deck
    private static void playerTake2Cards() {
        Card tempCard;
        for (int i = 0; i < 2; ++i) {
            tempCard = dealer.dealCard();
            currentPlayer.takeCard(tempCard);

            // Checks which player to deal cards to
            if (currentPlayer.getPlayerName().endsWith("1")) {
                GameWindow.hBoxTop.getChildren().add(tempCard.getFrontImageView());
            } else if (currentPlayer.getPlayerName().endsWith("2")) {
                GameWindow.hBoxBot.getChildren().add(tempCard.getFrontImageView());
            }
        }
        numSameAttackCards = 0;
    }

    // Player takes 3 cards from the deck
    private static void playerTake3Cards() {
        Card tempCard;
        for (int i = 0; i < 3; ++i) {
            tempCard = dealer.dealCard();
            currentPlayer.takeCard(tempCard);

            // Checks which player to deal cards to
            if (currentPlayer.getPlayerName().endsWith("1")) {
                GameWindow.hBoxTop.getChildren().add(tempCard.getFrontImageView());
            } else if (currentPlayer.getPlayerName().endsWith("2")) {
                GameWindow.hBoxBot.getChildren().add(tempCard.getFrontImageView());
            }
        }
        numSameAttackCards = 0;
    }

    // Makes current player skip turn if previous player played with a 4 card
    private static void playerWait() {
        if (!isPlayerDefending) {

            if (numSameWaitCards > 0) {
                currentPlayer.setWaitTurns(numSameWaitCards);
                currentPlayer.setPlayerWait(true);
            }
            System.out.println(currentPlayer.getPlayerName() + " waits " + currentPlayer.getWaitTurns() + " turns "
                    + currentPlayer.isPlayerWait());

            Player tempPlayer = currentPlayer;
            for (int i = 0; i < players.size(); ++i) {
                if (players.get(i).isPlayerTurn() && i < players.size() - 1) {
                    players.get(i).setPlayerTurn(false);
                    players.get(i + 1).setPlayerTurn(true);
                    currentPlayer = players.get(i + 1);
                    break;
                } else if (players.get(i).isPlayerTurn() && i == players.size() - 1) {
                    players.get(i).setPlayerTurn(false);
                    players.get(0).setPlayerTurn(true);
                    currentPlayer = players.get(0);
                    break;
                }
            }
            GameWindow.label.setText(tempPlayer.getPlayerName() + " waits " + tempPlayer.getWaitTurns() + " turn(s) 2, "
                    + currentPlayer.getPlayerName() + " moves");

            if (isPlayerWon) {
                GameWindow.label.setText(currentPlayer.getPlayerName() + " Won!");
                resetHighlightCards();
            }
            System.out.println("\n" + currentPlayer.getPlayerName() + " moves at waiting method");
        }
    }

    // Deals 5 cards to next player
    private static void kingOfHearts() {
        Card tempCard;
        for (int i = 0; i < 5; ++i) {
            tempCard = dealer.dealCard();
            currentPlayer.takeCard(tempCard);

            // Checks which player to deal cards to
            if (currentPlayer.getPlayerName().endsWith("1")) {
                GameWindow.hBoxTop.getChildren().add(tempCard.getFrontImageView());
            } else if (currentPlayer.getPlayerName().endsWith("2")) {
                GameWindow.hBoxBot.getChildren().add(tempCard.getFrontImageView());
            }
        }
        numSameKingCards = 0;
    }

    // Deals 5 cards to previous player
    private static void kingOfSpades() {
        Card tempCard;
        Player tempPlayer = null;

        // Checks if there is more than 2 players
        if (players.size() > 2) {
            for (int i = 0; i < players.size(); ++i) {
                if (players.get(i) == currentPlayer) {
                    if (i == 0) {
                        tempPlayer = players.get(players.size() - 1);
                    } else {
                        tempPlayer = players.get(i - 1);
                    }
                }
            }

            for (int i = 0; i < 5; ++i) {
                tempCard = dealer.dealCard();
                tempPlayer.takeCard(tempCard);

                // Checks which player to deal cards to
                if (tempPlayer.getPlayerName().endsWith("1")) {
                    GameWindow.hBoxTop.getChildren().add(tempCard.getFrontImageView());
                } else if (tempPlayer.getPlayerName().endsWith("2")) {
                    GameWindow.hBoxBot.getChildren().add(tempCard.getFrontImageView());
                }
            }
        } else { // if only 2 players
            for (int i = 0; i < 5; ++i) {
                tempCard = dealer.dealCard();
                currentPlayer.takeCard(tempCard);

                // Checks which player to deal cards to
                if (currentPlayer.getPlayerName().endsWith("1")) {
                    GameWindow.hBoxTop.getChildren().add(tempCard.getFrontImageView());
                } else if (currentPlayer.getPlayerName().endsWith("2")) {
                    GameWindow.hBoxBot.getChildren().add(tempCard.getFrontImageView());
                }
            }
        }
        numSameKingCards = 0;
    }

    // Checks if card on table is an Ace
    private static boolean checkForAce() {
        if (table.getTopCard().getID().endsWith("1")) {

            return true;
        } else {

            return false;
        }
    }

    // Checks if card on table is a Jack
    private static boolean checkForJack() {
        if (table.getTopCard().getID().endsWith("j")) {
            return true;
        } else {
            return false;
        }
    }

    // Changes card suit to one demanded by last player
    private static void setSuitChangeID() {

        String suitChange = "";

        if (GameWindow.suitsBox.getSelectionModel().getSelectedIndex() >= 0) {

            suitChange = GameWindow.suitsBox.getValue();
        }

        switch (suitChange) {

            case "hearts":
                aceChangeID = "h" + table.getTopCard().getID().substring(1);
                break;
            case "spades":
                aceChangeID = "s" + table.getTopCard().getID().substring(1);
                break;
            case "diamonds":
                aceChangeID = "d" + table.getTopCard().getID().substring(1);
                break;
            case "clubs":
                aceChangeID = "c" + table.getTopCard().getID().substring(1);
                break;
            default:
                break;
        }

        isAceChange = true;
    }

    // Changes card rank to one demanded by last player
    private static void setRankChangeID() {

        String rankChange = "";

        if (GameWindow.ranksBox.getSelectionModel().getSelectedIndex() >= 0) {
            rankChange = GameWindow.ranksBox.getValue();
        }

        switch (rankChange) {

            case "5":
                jackChangeID = table.getTopCard().getID().charAt(0) + "5";
                break;
            case "6":
                jackChangeID = table.getTopCard().getID().charAt(0) + "6";
                break;
            case "7":
                jackChangeID = table.getTopCard().getID().charAt(0) + "7";
                break;
            case "8":
                jackChangeID = table.getTopCard().getID().charAt(0) + "8";
                break;
            case "9":
                jackChangeID = table.getTopCard().getID().charAt(0) + "9";
                break;
            case "10":
                jackChangeID = table.getTopCard().getID().charAt(0) + "10";
                break;
            default:
                break;
        }

        isJackChange = true;
    }

    // Checks combo box for selected item's index and returns true if it's >= 0
    private static boolean checkComboBox(ComboBox comboBox) {

        if (comboBox.getSelectionModel().getSelectedIndex() >= 0) {
            return true;
        } else {
            return false;
        }
    }

    // Checks if picked card is valid to play
    private static boolean checkIsValidCard(ImageView source) {

        if ((source.getId().charAt(0) == table.getTopCard().getID().charAt(0))
                || (source.getId().substring(1).equals(table.getTopCard().getID().substring(1)))
                || (source.getId().substring(1).equals("q"))
                || (table.getTopCard().getID().substring(1).equals("q"))) {

            return true;
        } else {

            return false;
        }
    }

    // Checks if picked card is valid to play for ace change
    private static boolean checkIsValidForAce(ImageView source) {
        if ((source.getId().charAt(0) == aceChangeID.charAt(0))
                || (source.getId().endsWith("1"))
                || (source.getId().substring(1).equals("q"))) {

            return true;
        } else {

            return false;
        }
    }

    // Checks if picked card is valid to play for jack change
    private static boolean checkIsValidForJack(ImageView source) {

        if ((source.getId().substring(1).equals(jackChangeID.substring(1)))
                || (source.getId().endsWith("j"))
                || (source.getId().substring(1).equals("q"))) {

            return true;
        } else {

            return false;
        }
    }

    // Checks if player can defend from attack card, wait card or king card
    private static boolean isPlayerDefend() {

        boolean playerDefends = false;

        if (table.getTopCard().getID().endsWith("2") || table.getTopCard().getID().endsWith("3") || table.getTopCard().getID().endsWith("4")) {

            for (int i = 0; i < currentPlayer.getHand().size(); ++i) {

                if (currentPlayer.getHand().get(i).getID().substring(1).equals(table.getTopCard().getID().substring(1))) {
                    playerDefends = true;
                    break;
                }
            }

        } else if (table.getTopCard().getID().equals("sk")) {

            for (int i = 0; i < currentPlayer.getHand().size(); ++i) {
                if (currentPlayer.getHand().get(i).getID().equals("hk")) {
                    playerDefends = true;
                    break;
                }
            }
        } else if (table.getTopCard().getID().equals("hk")) {

            for (int i = 0; i < currentPlayer.getHand().size(); ++i) {
                if (currentPlayer.getHand().get(i).getID().equals("sk")) {
                    playerDefends = true;
                    break;
                }
            }
        } else if (currentPlayer.isPlayerWait()) {

            playerDefends = false;
        } else {

            playerDefends = false;
        }
        return playerDefends;
    }

    private static boolean isPlayerWin() {
        if (currentPlayer.getHand().size() == 0) {
            isPlayerWon = true;
            return true;
        } else {
            return false;
        }
    }

    // Highlights valid cards from player's hand
    private static void highlightValidCards(int i) {

        if (currentPlayer.getPlayerName().endsWith("1")) {

            GameWindow.hBoxTop.getChildren().get(i).setScaleX(1.1);
            GameWindow.hBoxTop.getChildren().get(i).setScaleY(1.1);
            GameWindow.hBoxTop.getChildren().get(i).setScaleZ(1.1);
            GameWindow.hBoxTop.getChildren().get(i).setStyle(
                    "-fx-effect: dropshadow(three-pass-box, black, 10, 0, 0, 0)");
        } else if (currentPlayer.getPlayerName().endsWith("2")) {

            GameWindow.hBoxBot.getChildren().get(i).setScaleX(1.1);
            GameWindow.hBoxBot.getChildren().get(i).setScaleY(1.1);
            GameWindow.hBoxBot.getChildren().get(i).setScaleZ(1.1);
            GameWindow.hBoxBot.getChildren().get(i).setStyle(
                    "-fx-effect: dropshadow(three-pass-box, black, 10, 0, 0, 0)");
        }
    }

    private static void rehighlightValidCards() {

        if (currentPlayer.getPlayerName().endsWith("1")) {

            System.out.println("Card played ID Player 1 " + sameCardID);
            for (int i = 0; i < GameWindow.hBoxTop.getChildren().size(); ++i) {

                if (GameWindow.hBoxTop.getChildren().get(i).getId().substring(1).equals(
                        sameCardID.substring(1))) {
                    GameWindow.hBoxTop.getChildren().get(i).setScaleX(1.1);
                    GameWindow.hBoxTop.getChildren().get(i).setScaleY(1.1);
                    GameWindow.hBoxTop.getChildren().get(i).setScaleZ(1.1);
                    GameWindow.hBoxTop.getChildren().get(i).setStyle(
                            "-fx-effect: dropshadow(three-pass-box, black, 10, 0, 0, 0)");
                } else {

                    GameWindow.hBoxTop.getChildren().get(i).setScaleX(1);
                    GameWindow.hBoxTop.getChildren().get(i).setScaleY(1);
                    GameWindow.hBoxTop.getChildren().get(i).setScaleZ(1);
                    GameWindow.hBoxTop.getChildren().get(i).setStyle("-fx-effect: null");
                }
            }
        } else if (currentPlayer.getPlayerName().endsWith("2")) {

            System.out.println("Card played ID Player 2 " + sameCardID);
            for (int i = 0; i < GameWindow.hBoxBot.getChildren().size(); ++i) {

                if (GameWindow.hBoxBot.getChildren().get(i).getId().substring(1).equals(
                        sameCardID.substring(1))) {
                    GameWindow.hBoxBot.getChildren().get(i).setScaleX(1.1);
                    GameWindow.hBoxBot.getChildren().get(i).setScaleY(1.1);
                    GameWindow.hBoxBot.getChildren().get(i).setScaleZ(1.1);
                    GameWindow.hBoxBot.getChildren().get(i).setStyle(
                            "-fx-effect: dropshadow(three-pass-box, black, 10, 0, 0, 0)");
                } else {

                    GameWindow.hBoxBot.getChildren().get(i).setScaleX(1);
                    GameWindow.hBoxBot.getChildren().get(i).setScaleY(1);
                    GameWindow.hBoxBot.getChildren().get(i).setScaleZ(1);
                    GameWindow.hBoxBot.getChildren().get(i).setStyle("-fx-effect: null");
                }
            }
        }
    }

    // Resets the highlight on cards after player's move
    private static void resetHighlightCards() {

        if (currentPlayer.getPlayerName().endsWith("1")) {

            for (int i = 0; i < GameWindow.hBoxTop.getChildren().size(); ++i) {

                GameWindow.hBoxTop.getChildren().get(i).setScaleX(1);
                GameWindow.hBoxTop.getChildren().get(i).setScaleY(1);
                GameWindow.hBoxTop.getChildren().get(i).setScaleZ(1);
                GameWindow.hBoxTop.getChildren().get(i).setStyle("-fx-effect: null");
            }
        } else if (currentPlayer.getPlayerName().endsWith("2")) {

            for (int i = 0; i < GameWindow.hBoxBot.getChildren().size(); ++i) {

                GameWindow.hBoxBot.getChildren().get(i).setScaleX(1);
                GameWindow.hBoxBot.getChildren().get(i).setScaleY(1);
                GameWindow.hBoxBot.getChildren().get(i).setScaleZ(1);
                GameWindow.hBoxBot.getChildren().get(i).setStyle("-fx-effect: null");
            }
        }
    }

    // Checks if deck needs to be refilled
    private static void checkRefillDeck() {

        ArrayList<Card> tempDeck = new ArrayList<>();

        if (table.getTopCard().getID().endsWith("2")) {

            if (dealer.getDeck().getDeckSize() <= 2 * numSameAttackCards) {
                GameWindow.deckImage.clear();

                for (int i = 0; i < table.getTableCards().size() - 1; ) {

                    tempDeck.add(table.getTableCards().get(i));
                    table.getTableCards().remove(i);
                    System.out.println("Table cards size " + table.getTableCards().size());

                }

                for (int i = 0; i < GameWindow.cardsOnTableLayout.getChildren().size() - 1; ) {

                    GameWindow.cardsOnTableLayout.getChildren().remove(i);
                    System.out.println("Table cards images size " + GameWindow.cardsOnTableLayout.getChildren().size());
                }

                for (int i = 0; i < tempDeck.size(); ++i) {

                    GameWindow.deckImage.add(new ImageView(
                            new Image("com/przemek/stepien/macaugame/logic/Cards/b1fv.png")));
                    GameWindow.deckPane.getChildren().add(GameWindow.deckImage.get(i));
                    GameWindow.deckImage.get(i).setLayoutX(i * -1.5);
                }

                dealer.getDeck().getCards().addAll(tempDeck);
            }
        } else if (table.getTopCard().getID().endsWith("3")) {

            if (dealer.getDeck().getDeckSize() <= 3 * numSameAttackCards) {
                GameWindow.deckImage.clear();

                for (int i = 0; i < table.getTableCards().size() - 1; ) {

                    tempDeck.add(table.getTableCards().get(i));
                    table.getTableCards().remove(i);
                    System.out.println("Table cards size " + table.getTableCards().size());

                }

                for (int i = 0; i < GameWindow.cardsOnTableLayout.getChildren().size() - 1; ) {

                    GameWindow.cardsOnTableLayout.getChildren().remove(i);
                    System.out.println("Table cards images size " + GameWindow.cardsOnTableLayout.getChildren().size());
                }

                for (int i = 0; i < tempDeck.size(); ++i) {

                    GameWindow.deckImage.add(new ImageView(
                            new Image("com/przemek/stepien/macaugame/logic/Cards/b1fv.png")));
                    GameWindow.deckPane.getChildren().add(GameWindow.deckImage.get(i));
                    GameWindow.deckImage.get(i).setLayoutX(i * -1.5);
                }

                dealer.getDeck().getCards().addAll(tempDeck);
            }
        } else if (table.getTopCard().getID().equals("hk") || table.getTopCard().getID().equals("sk")) {

            if (dealer.getDeck().getDeckSize() <= 5 * numSameKingCards) {

                GameWindow.deckImage.clear();

                for (int i = 0; i < table.getTableCards().size() - 1; ) {

                    tempDeck.add(table.getTableCards().get(i));
                    table.getTableCards().remove(i);
                    System.out.println("Table cards size " + table.getTableCards().size());
                }

                for (int i = 0; i < GameWindow.cardsOnTableLayout.getChildren().size() - 1; ) {

                    GameWindow.cardsOnTableLayout.getChildren().remove(i);
                    System.out.println("Table cards images size " + GameWindow.cardsOnTableLayout.getChildren().size());
                }

                for (int i = 0; i < tempDeck.size(); ++i) {

                    GameWindow.deckImage.add(new ImageView(
                            new Image("com/przemek/stepien/macaugame/logic/Cards/b1fv.png")));
                    GameWindow.deckPane.getChildren().add(GameWindow.deckImage.get(i));
                    GameWindow.deckImage.get(i).setLayoutX(i * -1.5);
                }

                dealer.getDeck().getCards().addAll(tempDeck);
            }
        } else {
            if (dealer.getDeck().getDeckSize() <= 1) {

                GameWindow.deckImage.clear();

                for (int i = 0; i < table.getTableCards().size() - 1; ) {

                    tempDeck.add(table.getTableCards().get(i));
                    table.getTableCards().remove(i);
                    System.out.println("Table cards size " + table.getTableCards().size());

                }

                for (int i = 0; i < GameWindow.cardsOnTableLayout.getChildren().size() - 1; ) {

                    GameWindow.cardsOnTableLayout.getChildren().remove(i);
                    System.out.println("Table cards images size " + GameWindow.cardsOnTableLayout.getChildren().size());
                }

                for (int i = 0; i < tempDeck.size(); ++i) {

                    GameWindow.deckImage.add(new ImageView(
                            new Image("com/przemek/stepien/macaugame/logic/Cards/b1fv.png")));
                    GameWindow.deckPane.getChildren().add(GameWindow.deckImage.get(i));
                    GameWindow.deckImage.get(i).setLayoutX(i * -1.5);
                }

                dealer.getDeck().getCards().addAll(tempDeck);
            }
        }
    }
}
