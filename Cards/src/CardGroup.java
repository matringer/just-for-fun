/*
 * Author: Mat Ringer
 * Date: 2014 
 */

import java.util.ArrayList;
import java.util.stream.Collectors;

public class CardGroup implements Comparable<CardGroup> {
	protected ArrayList<Card> cards = new ArrayList<>();
	int creationId, bottomFacingCardIndex = 0;

	public int getCreationId() {
		return creationId;
	}
	
	public int getHiddenCardCount() {
		int count=0;
		for(Card c : cards) {
			if( !c.isVisible ){
				count++;
			}
		}
		return count;
	}

	public void setCreationId(int creationId) {
		this.creationId = creationId;
	}

	int getCount() {
		return cards.size();
	};

	Card takeTopCard() {
		bottomFacingCardIndex = Math.max(0, bottomFacingCardIndex - 1);
		if (cards.size() > 0) {
			Card c = cards.remove(0);
			return c;
		}
		return null;
	}

	Card takeBottomCard() {
		if (cards.size() > 0) {
			Card c = cards.remove(cards.size() - 1);
			return c;
		}
		return null;
	}

	Card getTopCard() {
		if (cards.size() > 0) {
			return cards.get(0);
		}
		return null;
	}

	void addCardToBottom(Card c) {
		cards.add(c);
	}

	Card getCardAtIndex(int index) {
		if (index < cards.size()) {
			return cards.get(index);
		}
		return null;
	}

	Card removeCardAtIndex(int index) {
		if (index < cards.size()) {
			return cards.remove(index);
		}
		return null;
	}

	void addCardToTop(Card c) {
		cards.add(0, c);
		bottomFacingCardIndex++;
	}

	boolean hasCards() {
		return (cards.size() > 0);
	}

	CardGroup reverseCards() {
		CardGroup newCards = new CardGroup();
		for (Card c : cards) {
			newCards.addCardToTop(c);
			c.isVisible=false;
		}
		return newCards;
	}
	
	void copyTo(CardGroup newCardGroup) {
		newCardGroup.cards = new ArrayList<>();
		for (Card c : cards) {
			Card newCard = new Card(c);
			newCardGroup.cards.add(newCard);
		}
		newCardGroup.creationId = this.creationId;
		newCardGroup.bottomFacingCardIndex = this.bottomFacingCardIndex;
	}
	
	@Override
	//sort from largest to smallest
	public int compareTo(CardGroup arg) {
		if( arg.getHiddenCardCount() < this.getHiddenCardCount()) 
			return -1;
		if(  arg.getHiddenCardCount() > this.getHiddenCardCount()) 
			return 1;
		return 0;
	}
	@Override
	public String toString() {
		return cards.stream()
		.map(Card::toString)
		.collect(Collectors.joining(", "));
	}
}