package it.polimi.ingsw;
import java.util.*;
import java.util.LinkedList;
/**
 * 
 */
public class Deck<T> {
	/**
	 * list of cards in the deck
	 */
	private List<T> deckList;

	/**
	 * list of discarded cards
	 */
	private List<T> discards;


	/**
	 * Default constructor
	 * basic initialization
	 */
	public Deck() {

		deckList = new LinkedList<>();
		discards = new LinkedList<>();

	}


	/**
	 * 	if deck is empty then take cards from discards shuffle and add to deck
	 */
	private void shuffle() {
		if(deckList.isEmpty()){
			deckList.addAll(discards);
			discards.clear();
			Collections.shuffle(deckList);}
	}


	/**
	 * @return first card of the deck
	 */
	public T drawCard() {
		T c;
		if(deckList.isEmpty()){
			this.shuffle();
		}
		c = deckList.get(0);
		deckList.remove(0);
		return c;
	}

	/**
	 * @param toDiscard card to add to discards
	 */
	public void discardCard(T toDiscard) {
		discards.add(toDiscard);
	}

}