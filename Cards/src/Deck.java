/*
 * Author: Mat Ringer
 * Date: 2014 
 */

import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;

public class Deck extends CardGroup {

	Deck() {
		addCards();
	}
		
	private void addCards() {
		CardSuit[] suits = CardSuit.values();
		CardValue[] values = CardValue.values();
		Card card = null;

		for (int j = 0; j < CardSuit.values().length; j++) {
			for (int i = 0; i < CardValue.values().length; i++) {
				card = new Card(suits[j], values[i]);
				cards.add(card);
			}
		}
	}

	void shuffle(int times) {
		for (int i = 0; i < times; i++) {
			MatLogger.print("\nindexes: ");
			ArrayList<Card> newCardList = new ArrayList<>();
			Random random = new Random(System.nanoTime());
			while (hasCards()) {
				random.setSeed(System.nanoTime());
				int index = random.nextInt(cards.size());
				MatLogger.print(index + " ");
				newCardList.add(cards.get(index));
				cards.remove(index);
			}
			cards = newCardList;
		}
	}

	void print() {
		MatLogger.printLine("\n\n--------------------------------------------------------------------");
		MatLogger.printLine("Deck size = " + cards.size());
		MatLogger.print(
				cards.stream()
				.map(Card::toString)
				.collect(Collectors.joining(", ")));
		MatLogger.printLine("\n--------------------------------------------------------------------\n");
		
	}

	public void replaceCards(CardGroup cardGroup) {
		cards = cardGroup.cards;
	}

}
