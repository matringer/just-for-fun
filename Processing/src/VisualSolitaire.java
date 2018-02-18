/*
 * Author: Mat Ringer
 * Date: 2014 
 */

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;


public class VisualSolitaire extends PApplet {

	private static final long serialVersionUID = -4389365619326463767L;
	
	//finishing stuff
	ArrayList<BouncingBalls.Ball> balls = new ArrayList<>();

	public static void main(String[] args) {
//		PApplet.main(new String[] { "--present", "VisualSolitaire" });
		PApplet.main(new String[] { "VisualSolitaire" });
	}
	
//	String cardImagePath = "c:\\temp\\cardimages\\";
	String cardImagePath = "";
	Solitaire solitaire = new Solitaire();
	PImage cardImageMap;
	int cardDisplaySizeWidth=75;
	int cardDisplaySizeHeight = 101;
	PFont fontA;
	private ArrayList<Card> animationCardList = new ArrayList<>();

	@Override
	public void setup() {
		frameRate(30);	
		size(1024, 600);
		smooth();
		fontA = loadFont("AndaleMono-36.vlw");
		textFont(fontA);
		cardImageMap = loadImage(cardImagePath + "cardImageMap.jpg");
		solitaire.setupGame();
		solitaire.setPauseTurnedOn(true);
		Solitaire.totalMoneyMade=Solitaire.costToPlay*-1;
		Thread gameThread = new Thread(solitaire.new PlayGameOnThread());
		gameThread.start();
	}
	
	@Override
	public void draw() {
		if(solitaire.gameOver)
			background(0,0,150,0);
		else {
			background(0,150,0,0);
		}
		if(balls.size()>0) {
			for (int i = balls.size() - 1; i >= 0; i--) {
				BouncingBalls.Ball ball = balls.get(i);
//				System.out.println(ball.x + " " + ball.y);
				ball.move(height);
				// Display the circle
				fill(ball.colorRed, ball.colorGreen, ball.colorBlue);
				// stroke(0,life);
				ellipse(ball.x, ball.y, ball.w, ball.w);


				if (ball.finished()) {
					balls.remove(i);
				}
			}
		}
		
		drawStacks();
		drawPiles();
		drawDeck();
		drawDiscardPile();
		animateCards();

		fill(255, 255, 255, 60);
		text(Solitaire.totalMoneyMade, 850, 550);

		if(mouseY > .9*height && mouseX > .9*width) {
			mousePressed();
		}
		
	}

	@Override
	public void mousePressed() {
		synchronized (solitaire) {
			solitaire.notify();
		}

		if(solitaire.gameOver) {
			for(int i=0; i<30; i++) {
				float ballWidth = random(10, 24);
				BouncingBalls.Ball b = new BouncingBalls().new Ball(random(width), random(height), ballWidth);
				b.gravity = random(10);
				b.life=15;
				balls.add(b);
			}

			solitaire.setupGame();
			Solitaire.totalMoneyMade = Solitaire.totalMoneyMade-Solitaire.costToPlay;
			Thread gameThread = new Thread(solitaire.new PlayGameOnThread());
			gameThread.start();
		}
	}

	private void drawDeck() {
		float xOffset = 15;
		float yOffset = 475;
		float x= 0;

		for(int i=0; i<solitaire.getDeck().getCount(); i++) {
			Card c = solitaire.getDeck().getCardAtIndex(i);
			showCard(x+xOffset, yOffset,c);
			x += 2;
		}
	}
	

	private void drawDiscardPile() {
		float xOffset = 400;
		float yOffset = 475;
		float x= 0;
		Card c;
		CardGroup cg = solitaire.getDiscardPile();
		for(int i=cg.cards.size()-1; i>=0; i--) {
			c = cg.getCardAtIndex(i);
			showCard(x+xOffset, yOffset,c);
			x += 10;
		}
	}

	private void drawPiles() {
		float pileXOffset = 5;
		float pileYOffset = 155;
		float pileX = 0;
		float pileY = 0;
		Card c;
		
		for(int i=0; i<7; i++) {
			CardGroup cg = solitaire.getPiles().get(i);
			pileX = pileXOffset + cg.getCreationId()*150;
			pileY = pileYOffset;

			for(int j=cg.cards.size()-1; j>=0; j--) {
				c=cg.cards.get(j);
				showCard(pileX, pileY, c);
				if(! c.isVisible) {
					//don't show a lot of the current card if it's not visible
					pileY = pileY+5;
				}
				else {					
					//this card is not hidden so allow more space to see it 
					pileY = pileY+15;
				}
			}
		}
	}

	private void drawStacks() {
		float stackXOffset = 220;
		float stackX = 0;
		float stackY = 10;
		
		for(int i=0; i<4; i++) {
			CardGroup cg = solitaire.getStacks().get(i);
			stackX = stackXOffset + cg.getCreationId()*150;
			for(int j=cg.cards.size()-1; j>=0; j--) {
				showCard(stackX, stackY, cg.cards.get(j));
				stackX = stackX+2;
			}
		}
	}

	private void animateCards() {
//		System.out.println(animationCardList.size());
		ArrayList<Card> remainingList = new ArrayList<>();
		for(Card c : animationCardList) {
			if(animate(c) == true) {
				remainingList.add(c);
			}
			
			showCardImage(c);		
			
		}
		animationCardList = remainingList;

	}

	private void showCardImage(Card c) {
		image(cardImageMap,
				c.fromCoords.x,
				c.fromCoords.y,
				cardDisplaySizeWidth,
				cardDisplaySizeHeight,
				c.getFileLocationX(),
				c.getFileLocationY(),
				Card.cardSizeWidth + c.getFileLocationX(),
				Card.cardSizeHeight + c.getFileLocationY());
	}
	
	private void showCard(float displayX, float displayY, Card c) {		
		c.toCoords.x = displayX;
		c.toCoords.y = displayY;
		if(c.fromCoords.x != c.toCoords.x ||c.fromCoords.y != c.toCoords.y) {
			if(!animationCardList.contains(c))
				animationCardList.add(c);
		}
		else {
			showCardImage(c);
		}
		
		//show alpha mask to highlight cards changing
//		if(c.highlight) {
//			fill(0,0,0,100);
//			rect(displayX, displayY, cardDisplaySizeWidth, cardDisplaySizeHeight);
//		}
		
	}

	private boolean animate(Card c) {
//		System.out.println("animate:");
//		System.out.println(c.toString());
//		System.out.println("previousDisplay: " + c.fromCoords.x + "," + c.fromCoords.y);
//		System.out.println("displayTo: " + c.toCoords.x + "," + c.toCoords.y);
		
		if(c.fromCoords.equals(c.toCoords)) {
			return false;
		}

		float diffX = c.toCoords.x - c.fromCoords.x;
		float diffY = c.toCoords.y - c.fromCoords.y;

		float countByX = 12;
		float countByY = 12;
		
		if(diffX < 0) {
			countByX=countByX*-1;
		}
		if(diffY < 0) {
			countByY=countByY*-1;
		}
		
		if(Math.abs(diffX) > Math.abs(countByX)) {
			c.fromCoords.x = c.fromCoords.x+countByX;
		}
		else {
			//done
			c.fromCoords.x = c.toCoords.x;
		}
		
		if(Math.abs(diffY) > Math.abs(countByY)) {
			c.fromCoords.y = c.fromCoords.y+countByY;
		}
		else {
			//done
			c.fromCoords.y = c.toCoords.y;
		}

//		System.out.println("animating at: " + c.fromCoords.x + "," + c.fromCoords.y);
		return true;
		
	}
	
}
