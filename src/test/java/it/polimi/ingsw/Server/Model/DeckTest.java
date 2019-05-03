package it.polimi.ingsw.Server.Model;

import it.polimi.ingsw.Server.Model.Deck;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeckTest {

    @Test
    void testDeckFunctionality() {
        Deck<Integer> deckTest = new Deck<Integer>();

        deckTest.discardCard(5);
        assertEquals(5,deckTest.drawCard());
        deckTest.discardCard(5);
        deckTest.discardCard(35);
        deckTest.discardCard(32);
        int i = deckTest.drawCard();
        assertTrue( i == 5 || i == 35 || i == 32);

    }

}