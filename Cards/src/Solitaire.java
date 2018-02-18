/*
 * Author: Mat Ringer
 * Date: 2014 
 */

import java.lang.Thread.State;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class Solitaire {

	private static int gamesToPlay = 10000;
	private static boolean PLAY_FROM_BIGGEST_PILE_FIRST = true;
	
	private static final int winningPot = 260;
	public static final int costToPlay = 52;
	private static int gameCounter = 0;

	Deck deck;
	CardGroup discardPile; //discard pile from drawing on deck
	ArrayList<CardGroup> piles; //these are the 7 working piles of cards in middle
	ArrayList<CardGroup> stacks; //these are the 4 stacks of cards at top
	int totalmoves=0;
	
	public boolean gameOver;
	private boolean needToRunThroughDeckAgain;
	private boolean pauseTurnedOn = false;

	public boolean isPauseTurnedOn() { return pauseTurnedOn; }
	public void setPauseTurnedOn(boolean pauseTurnedOn) { this.pauseTurnedOn = pauseTurnedOn; }

	public static int gamesMoneyWon, gamesWon, totalMoneyMade;

	public Deck getDeck() { return deck; };
	public CardGroup getDiscardPile() {return discardPile; };
	public ArrayList<CardGroup> getPiles() {return piles; };
	public ArrayList<CardGroup> getStacks() {return stacks; };

	public static void main(String[] args) {		
		MatLogger.logToScreen = false;
		MatLogger.logToFile = false;

		if (args.length > 0) {
			gamesToPlay = Integer.parseInt(args[0]);
		}
		
		Thread statThread = new Thread(new Solitaire().new StatusCheck());
		statThread.setPriority(Thread.MIN_PRIORITY);
		statThread.start();
		
		long startTime = new Date().getTime();		
		playGamesOnThreads2();
//		playGamesOnCurrentThread();
		statThread.stop();
		long timeSpent = new Date().getTime() - startTime;
		
		System.out.println("finished with games");
		MatLogger.logToScreen = true;
		//MatLogger.logToFile = true;
		MatLogger.printLine("PLAY_FROM_BIGGEST_PILE_FIRST is " + PLAY_FROM_BIGGEST_PILE_FIRST);
		MatLogger.printLine("total games played:  " + gamesToPlay);
		MatLogger.printLine("total time spent in seconds:   " + timeSpent / 1000f);
		MatLogger.printLine("average milliseconds per game: " + ((float) timeSpent / (float) gamesToPlay));		
		float gamesWonPercentage = ((float) gamesWon / (float) gamesToPlay) * 100;
		float gamesMoneyWonPercentage = ((float) gamesMoneyWon / (float) gamesToPlay) * 100;
//		MatLogger.printLine("winning percentage:      " + winningPercentage + "%");
		MatLogger.printLine("total games that made money:     " + gamesMoneyWon + " (" + gamesMoneyWonPercentage + "%)");
		MatLogger.printLine("total games won completely:     " + gamesWon + " (" + gamesWonPercentage + "%)");
		MatLogger.printLine("total money spent:       "
				+ NumberFormat.getCurrencyInstance().format(gamesToPlay * costToPlay));
		MatLogger.printLine("total money made:        " + NumberFormat.getCurrencyInstance().format(totalMoneyMade));
		MatLogger.printLine("net profit:              "
				+ NumberFormat.getCurrencyInstance().format((totalMoneyMade - gamesToPlay * costToPlay)));
		float averageProfitPerHand = (float) totalMoneyMade / (float) gamesToPlay - costToPlay;
		MatLogger.printLine("Average profit per game: "
				+ NumberFormat.getCurrencyInstance().format(averageProfitPerHand));
	}
	
	private static void playGamesOnCurrentThread() {
		Solitaire s = new Solitaire();
		for (gameCounter = 0; gameCounter < gamesToPlay; gameCounter++) {
			s.setupGame();
			s.playGame();
		}
	}
	
	private static void playGamesOnThreads() {
		int chunk = 20000;
		for(gameCounter = 0; gameCounter < gamesToPlay; gameCounter+=chunk ) {
			if((gamesToPlay - gameCounter) < chunk) {
				chunk = gamesToPlay - gameCounter;
			}
			putGamesOnThreads( chunk );
		}
	}
	
	private static void playGamesOnThreads2() {
		int maxThreads=120;
		int maxGamesPerThread=50;
		ArrayList<Thread> threadList = new ArrayList<>(); 
		Thread gameThread  = null;
		Solitaire sGame = null;
		int threadsOpen, gamesLeft, gamesPerThread = 0;
		
		while(gameCounter < gamesToPlay) {
			threadsOpen = maxThreads-threadList.size();
			for (int i = 0; i < threadsOpen; i++) {
				gamesLeft = gamesToPlay-gameCounter;
				gamesPerThread = Math.min(maxGamesPerThread, gamesLeft);
				gameCounter += gamesPerThread;
				if(gameCounter > gamesToPlay) {
					break;
				}
				sGame = new Solitaire();
				gameThread = new Thread(sGame.new PlayGameOnThreadWithSetupLoop(gamesPerThread));
				threadList.add(gameThread);
				gameThread.start();
			}
			
			Thread.yield();
			for(int i=0; i<threadList.size(); i++) {
				gameThread =threadList.get(i); 
				if(! gameThread.isAlive()) {
					threadList.remove(gameThread);
				}
			}
		}
		boolean stillRunning = true;
		while(stillRunning) {
			stillRunning=false;
			for(Thread sThread : threadList) {
				if(sThread.isAlive()) {
					stillRunning = true;
					break;
				}
			}
			Thread.yield();
		}
	}
	
	private static void putGamesOnThreads(int howMany) {
//		System.out.println("new chunk");
		ArrayList<Thread> threadList = new ArrayList<>(); 
		Solitaire sGame = null;
		Thread gameThread  = null;
		for (int i = 0; i < howMany; i++) {
			sGame = new Solitaire();
			gameThread = new Thread(sGame.new PlayGameOnThreadWithSetup());
			gameThread.start();
			threadList.add(gameThread);
		}
		boolean somethingRunning = true;
		State state = null;
		while(somethingRunning) {
			Thread.yield();
			somethingRunning = false;
			for(Thread sThread : threadList) {
				state = sThread.getState();
				if(sThread.isAlive()) {
					somethingRunning = true;
					break;
				}
			}
		}
	}

	void deal() {
		piles = new ArrayList<>();
		stacks = new ArrayList<>();
		discardPile = new CardGroup();

		for (int i = 0; i < 7; i++) {
			piles.add(makePile(i));
		}

		for (int i = 0; i < 4; i++) {
			Stack s = new Stack();
			s.setCreationId(i);
			stacks.add(s);
		}
	}

	CardGroup makePile(int pileIndex) {
		CardGroup pile = new CardGroup();
		for (int i = 0; i < pileIndex + 1; i++) {
			Card c = deck.takeTopCard();
			pile.addCardToTop(c);
		}
		pile.getTopCard().isVisible = true;
		pile.setCreationId(pileIndex);
		pile.bottomFacingCardIndex=0;
		MatLogger.printLine("====================");
		MatLogger.printLine("Pile " + pileIndex + " is:  ");
		MatLogger.printLine(pile.toString());
		return pile;
	}

	boolean makeMoves() {
		boolean moveMade = false;
		boolean loopAgain = true;

		while (loopAgain) {
			loopAgain = false;
			if (playOnStacksUntilDone()) {
				moveMade = true;
				loopAgain = true;
			}
			if (playOnPiles()) {
				moveMade = true;
				loopAgain = true;
			}
		}

		return moveMade;
	}

	private boolean playOnPiles() {
		if(PLAY_FROM_BIGGEST_PILE_FIRST) {			
			Collections.sort(piles);
		}
		
		for (CardGroup p1 : piles) {
			for (CardGroup p2 : piles) {
				if (makePileMoves(p1, p2)) {
					totalmoves++;
					//return right away since we may be able to play on top stacks
					return true;
				}
			}
		}

		for (CardGroup p : piles) {
			if (playOnPileFromDeck(p)) {
				totalmoves++;
				//return right away since we may be able to play on top stacks
				return true;
			}
		}
		return false;
	}

	boolean playOnPileFromDeck(CardGroup p) {
		if (discardPile.getTopCard() == null) {
			return false;
		}
		if (p.getTopCard() == null && discardPile.getTopCard().getValue() == CardValue.KING) {
			MatLogger.printLine("deck: moving " + discardPile.getTopCard() + " to empty pile");
			return moveFromDiscardPileToPile(p);
		}

		if (canPlayThisCardOnThatCard(discardPile.getTopCard(), p.getTopCard())) {
			MatLogger.printLine("deck: moving " + discardPile.getTopCard() + " to " + p.getTopCard());
			return moveFromDiscardPileToPile(p);
		}
		return false;
	}
	private boolean moveFromDiscardPileToPile(CardGroup p) {
		Card c = discardPile.takeTopCard();
		p.addCardToTop(c);
		pause();
		return true;
	}

	boolean makePileMoves(CardGroup from, CardGroup to) {
		MatLogger.printLine("Can we move cards from pile: " + from);
		MatLogger.printLine("to pile: " + to);
		if (from == to) {
			return false;
		}
		MatLogger.printLine("from.bottomFacingCardIndex=" + from.bottomFacingCardIndex);
		Card fromBottomCard = from.getCardAtIndex(from.bottomFacingCardIndex);
		if (fromBottomCard == null) {
			return false;
		}
		Card toCard = to.getTopCard();

		if (toCard == null && from.getHiddenCardCount() > 0 && fromBottomCard.getValue() == CardValue.KING) {
			//we can move this king and any cards with it to a new pile
			MatLogger.printLine("pile: about to move " + fromBottomCard + " to " + toCard);
			movePileCards(from, to);
			return true;
		}
		
		if (canPlayThisCardOnThatCard(fromBottomCard, toCard)) {
			MatLogger.printLine("pile: about to move " + fromBottomCard + " to " + toCard);
			movePileCards(from, to);
			return true;
		}
		return false;
	}

	private void movePileCards(CardGroup from, CardGroup to) {
		for (int i = from.bottomFacingCardIndex; i >= 0; i--) {
			Card c = from.removeCardAtIndex(i);
			to.addCardToTop(c);
		}
		from.bottomFacingCardIndex = 0;
		if(from.hasCards()) {
			from.getCardAtIndex(0).isVisible = true;
		}
		pause();
	}

	private boolean playOnStacksUntilDone() {
		boolean madePlay = false;
		boolean loopAgain = true;
		while (loopAgain) {
			loopAgain = false;
			for (CardGroup s : stacks) {
				if (playOnStack(s)) {
					totalmoves++;
					madePlay = true;
					loopAgain = true;
				}
			}
		}
		return madePlay;
	}

	private void pause() {
		if( !pauseTurnedOn  ) {
			return;
		}
		synchronized (this) { 
			try {
				this.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}				
		}
	}

	private boolean playOnStack(CardGroup stack) {
		boolean played = false;
		for (CardGroup pile : piles) {
			if (playCardToStack(pile, stack)) {
				played = true;
			}
		}
		if (playCardToStack(discardPile, stack)) {
			played = true;
		}
		return played;
	}

	private boolean playCardToStack(CardGroup from, CardGroup stack) {
		Card fromCard = from.getTopCard();
		if (fromCard == null) {
			return false;
		}

		if (stack.getTopCard() == null) {
			// stack doesn't exist yet
			if (fromCard.getValue() == CardValue.ACE) {
				return moveCardToStack(from, stack, fromCard);
			}
		} else if (fromCard.getSuit() == stack.getTopCard().getSuit()) {
			if (fromCard.getValue().getValue() == stack.getTopCard().getValue().getValue() + 1) {
				return moveCardToStack(from, stack, fromCard);
			}
		}
		return false;
	}
	private boolean moveCardToStack(CardGroup from, CardGroup stack, Card fromCard) {
		MatLogger.printLine("stack: Playing " + fromCard + " on stack");
		stack.addCardToTop(from.takeTopCard());
		if(from.hasCards()) {
			from.getTopCard().isVisible=true;
		}
		pause();
		return true;
	}

	
	private boolean canPlayThisCardOnThatCard(Card thisCard, Card thatCard) {
		MatLogger.printLine("can we play " + thisCard + " on " + thatCard + "?");
		if (thisCard == null || thatCard == null) {
			return false;
		}
		if (thisCard.getSuitColor().compareTo(thatCard.getSuitColor()) == 0) {
			// suits are same
			return false;
		}
		if (thisCard.getValue().getValue() != thatCard.getValue().getValue() - 1) {
			// thisCard needs to be 1 less than the other card
			return false;
		}
		return true;
	}

	public int playGame() {
		totalmoves = 0;
		gameOver = false;
		needToRunThroughDeckAgain = false;
		pause();
		int cardsLeftInDeck = 0;
		boolean deckCycled = false;
		boolean moveMade = makeMoves();

		while (moveMade || !deckCycled) {
			flip3Cards();
			moveMade = makeMoves();
			if(moveMade) {
				needToRunThroughDeckAgain = true;
			}
			if (deck.getCount() == 0 && !needToRunThroughDeckAgain) {
				deckCycled = true;
			}
		}
		
		gameOver= true;
		MatLogger.printLine("\ncardsLeftInDeck: " + cardsLeftInDeck);
		StringBuffer str = new StringBuffer("stack count: ");
		int moneyMade = 0;
		for (CardGroup s : stacks) {
			str.append(s.getCount() + ", ");
			moneyMade += s.getCount() * 5;
		}
		
		MatLogger.printLine(str.toString());
		MatLogger.printLine("Money made: " + moneyMade);
		MatLogger.printLine("Net profit: " + (moneyMade - costToPlay));
		MatLogger.printLine("Total moves made:  " + this.totalmoves+"\n");
		
		totalMoneyMade += moneyMade;
		if (moneyMade == winningPot) {
			gamesWon++;
		}
		if(moneyMade > costToPlay) {
			gamesMoneyWon++;
		}

		return moneyMade;
	}
	
	public void setupGame() {
		deck = new Deck();
		deck.print();
		deck.shuffle(7);
		deal();
		deck.print();
	}

	private void flip3Cards() {
		totalmoves++;
		if (deck.getCount() == 0) {
			discardPile = discardPile.reverseCards();
			deck.replaceCards(discardPile);
			discardPile = new CardGroup();
			needToRunThroughDeckAgain = false;
		}
		CardGroup top3 = getTop3();
		addToDiscardPile(top3);
		MatLogger.printLine("flip cards:  " + discardPile.getTopCard() + " is on top");// " + "deck/discard
		pause();
	}

	private CardGroup getTop3() {
		CardGroup top3 = new CardGroup();
		int cardsToMove = Math.min(deck.getCount(), 3);

		for (int i = 0; i < cardsToMove; i++) {
			top3.addCardToBottom(deck.takeTopCard());
		}
		return top3;
	}

	private void addToDiscardPile(CardGroup cards) {
		for (int i = 0; i < cards.getCount(); i++) {
			discardPile.addCardToTop(cards.getCardAtIndex(i));
			cards.getCardAtIndex(i).isVisible=true;
		}
	}


	/*
	 * created pile class to
	 */
//	public class Pile extends CardGroup implements Comparable<Pile> {
//		private int bottomFacingCardIndex = 0;
//		
//
//		void addPileCard(Card c) {
//			addCardToTop(c);
//			bottomFacingCardIndex++;
//		}
//
//		public Card takeTopCard() {
//			bottomFacingCardIndex = Math.max(0, bottomFacingCardIndex - 1);
//			return super.takeTopCard();
//		}
//		
//		public void copyTo(Pile p) {
//			super.copyTo(p);
//			p.bottomFacingCardIndex = this.bottomFacingCardIndex;
//		}
//
//		@Override
//		//sort from largest to smallest
//		public int compareTo(Pile arg) {
//			if( arg.getHiddenCardCount() < this.getHiddenCardCount()) 
//				return -1;
//			if(  arg.getHiddenCardCount() > this.getHiddenCardCount()) 
//				return 1;
//			return 0;
//		}
//
//	}

	public class Stack extends CardGroup {
	}

	public class PlayGameOnThread implements Runnable {

		@Override
		public void run() {
			Solitaire.this.playGame();
		}
	}
	
	public class PlayGameOnThreadWithSetup implements Runnable {

		@Override
		public void run() {
			Solitaire.this.setupGame();
			Solitaire.this.playGame();
		}
	}
	
	public class PlayGameOnThreadWithSetupLoop implements Runnable {
		int mygamesPerThread;
		public PlayGameOnThreadWithSetupLoop(int gamesPerThread) {
			mygamesPerThread=gamesPerThread;
		}
		@Override
		public void run() {
			for(int i=0; i<mygamesPerThread; i++) {
				Solitaire.this.setupGame();
				Solitaire.this.playGame();
			}
		}
	}


	public class StatusCheck implements Runnable {

		@Override
		public void run() {
			while (gamesToPlay > gameCounter) {
				System.out.println("games played so far: " + gameCounter);
				try {
					Thread.sleep(5 * 1000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}
	}

}