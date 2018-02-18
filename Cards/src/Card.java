
/*
 * Author: Mat Ringer
 * Date: 2014 
 */

public class Card {
	private CardSuit mySuit;
	private CardValue myValue;
	private boolean suitIsRed;
	public boolean isVisible;
	public boolean highlight;
	private int fileLocationX = 0; 
	private int fileLocationY = 0;
	
	CardPoint fromCoords = new CardPoint(0,0);
	CardPoint toCoords = new CardPoint(0,0);

	static int[] columns = new int[14];
	static int[] rows = new int[7];
	public static final int cardSizeWidth = 143;
	public static final int cardSizeHeight = 193;

	static {
		int borderSpace = 3;
		int fileStartOffsetX = 77;
		int fileStartOffsetY = 62;
		for(int i=0; i<columns.length; i++) {
			columns[i] = fileStartOffsetX + i*cardSizeWidth + i*borderSpace;
		}
		for(int i=0; i<rows.length; i++) {
			rows[i] = fileStartOffsetY + i*cardSizeHeight + i*borderSpace;
		}
		
	}
	
	public int getFileLocationX() {
		if(!isVisible) {
			return columns[8];
		}
		return fileLocationX;
	}

	public int getFileLocationY() {
		if(!isVisible) {
			return rows[6];
		}
		return fileLocationY;
	}

	public Card(CardSuit suit, CardValue value) {
		mySuit = suit;
		myValue = value;
		if(suit == CardSuit.Diamonds || suit == CardSuit.Hearts) {
			suitIsRed = true;
		}
		determineDisplayCoordinates();
	}
	
	public Card(Card c) {
		//use for copy card
		this.mySuit = c.getSuit();
		this.myValue = c.getValue();
		this.isVisible = c.isVisible;
	}
		
	CardSuit getSuit() { return mySuit; }
	CardValue getValue() { return myValue; }
	
	boolean isRedSuit() {
		return suitIsRed;
	}
	
	boolean isBlackSuit() {
		return !suitIsRed; 
	}
	
	String getSuitColor() {
		if(suitIsRed) {
			return "RED";
		}
		return "BLACK";
	}
	
	@Override
	public String toString() {
		if(myValue !=null && mySuit!= null) {
			return (myValue.toString() + " of " + mySuit.toString());
		}else {
			return "null values";
		}
	}


	private void determineDisplayCoordinates() {
		if(getSuit() == CardSuit.Hearts) {
			if(getValue() == CardValue.ACE) {
				fileLocationX = columns[0];
				fileLocationY = rows[0];
			}
			if(getValue() == CardValue.TWO) {
				fileLocationX = columns[1];
				fileLocationY = rows[0];
			}
			if(getValue() == CardValue.THREE) {
				fileLocationX = columns[0];
				fileLocationY = rows[1];
			}
			if(getValue() == CardValue.FOUR) {
				fileLocationX = columns[1];
				fileLocationY = rows[1];
			}
			if(getValue() == CardValue.FIVE) {
				fileLocationX = columns[0];
				fileLocationY = rows[2];
			}
			if(getValue() == CardValue.SIX) {
				fileLocationX = columns[1];
				fileLocationY = rows[2];
			}
			if(getValue() == CardValue.SEVEN) {
				fileLocationX = columns[0];
				fileLocationY = rows[3];
			}
			if(getValue() == CardValue.EIGHT) {
				fileLocationX = columns[1];
				fileLocationY = rows[3];
			}
			if(getValue() == CardValue.NINE) {
				fileLocationX = columns[0];
				fileLocationY = rows[4];
			}
			if(getValue() == CardValue.TEN) {
				fileLocationX = columns[1];
				fileLocationY = rows[4];
			}
			if(getValue() == CardValue.JACK) {
				fileLocationX = columns[9];
				fileLocationY = rows[4];
			}
			if(getValue() == CardValue.QUEEN) {
				fileLocationX = columns[8];
				fileLocationY = rows[5];
			}
			if(getValue() == CardValue.KING) {
				fileLocationX = columns[9];
				fileLocationY = rows[5];
			}
			
		}
		if(getSuit() == CardSuit.Diamonds) {
			if(getValue() == CardValue.ACE) {
				fileLocationX = columns[2];
				fileLocationY = rows[0];
			}
			if(getValue() == CardValue.TWO) {
				fileLocationX = columns[3];
				fileLocationY = rows[0];
			}
			if(getValue() == CardValue.THREE) {
				fileLocationX = columns[2];
				fileLocationY = rows[1];
			}
			if(getValue() == CardValue.FOUR) {
				fileLocationX = columns[3];
				fileLocationY = rows[1];
			}
			if(getValue() == CardValue.FIVE) {
				fileLocationX = columns[2];
				fileLocationY = rows[2];
			}
			if(getValue() == CardValue.SIX) {
				fileLocationX = columns[3];
				fileLocationY = rows[2];
			}
			if(getValue() == CardValue.SEVEN) {
				fileLocationX = columns[2];
				fileLocationY = rows[3];
			}
			if(getValue() == CardValue.EIGHT) {
				fileLocationX = columns[3];
				fileLocationY = rows[3];
			}
			if(getValue() == CardValue.NINE) {
				fileLocationX = columns[2];
				fileLocationY = rows[4];
			}
			if(getValue() == CardValue.TEN) {
				fileLocationX = columns[3];
				fileLocationY = rows[4];
			}
			if(getValue() == CardValue.JACK) {
				fileLocationX = columns[11];
				fileLocationY = rows[1];
			}
			if(getValue() == CardValue.QUEEN) {
				fileLocationX = columns[10];
				fileLocationY = rows[2];
			}
			if(getValue() == CardValue.KING) {
				fileLocationX = columns[11];
				fileLocationY = rows[2];
			}			
		}
		if(getSuit() == CardSuit.Clubs) {
			if(getValue() == CardValue.ACE) {
				fileLocationX = columns[4];
				fileLocationY = rows[0];
			}
			if(getValue() == CardValue.TWO) {
				fileLocationX = columns[5];
				fileLocationY = rows[0];
			}
			if(getValue() == CardValue.THREE) {
				fileLocationX = columns[4];
				fileLocationY = rows[1];
			}
			if(getValue() == CardValue.FOUR) {
				fileLocationX = columns[5];
				fileLocationY = rows[1];
			}
			if(getValue() == CardValue.FIVE) {
				fileLocationX = columns[4];
				fileLocationY = rows[2];
			}
			if(getValue() == CardValue.SIX) {
				fileLocationX = columns[5];
				fileLocationY = rows[2];
			}
			if(getValue() == CardValue.SEVEN) {
				fileLocationX = columns[4];
				fileLocationY = rows[3];
			}
			if(getValue() == CardValue.EIGHT) {
				fileLocationX = columns[5];
				fileLocationY = rows[3];
			}
			if(getValue() == CardValue.NINE) {
				fileLocationX = columns[4];
				fileLocationY = rows[4];
			}
			if(getValue() == CardValue.TEN) {
				fileLocationX = columns[5];
				fileLocationY = rows[4];
			}
			if(getValue() == CardValue.JACK) {
				fileLocationX = columns[8];
				fileLocationY = rows[0];
			}
			if(getValue() == CardValue.QUEEN) {
				fileLocationX = columns[9];
				fileLocationY = rows[0];
			}
			if(getValue() == CardValue.KING) {
				fileLocationX = columns[8];
				fileLocationY = rows[1];
			}				
		}
		if(getSuit() == CardSuit.Spades) {
			if(getValue() == CardValue.ACE) {
				fileLocationX = columns[6];
				fileLocationY = rows[0];
			}
			if(getValue() == CardValue.TWO) {
				fileLocationX = columns[7];
				fileLocationY = rows[0];
			}
			if(getValue() == CardValue.THREE) {
				fileLocationX = columns[6];
				fileLocationY = rows[1];
			}
			if(getValue() == CardValue.FOUR) {
				fileLocationX = columns[7];
				fileLocationY = rows[1];
			}
			if(getValue() == CardValue.FIVE) {
				fileLocationX = columns[6];
				fileLocationY = rows[2];
			}
			if(getValue() == CardValue.SIX) {
				fileLocationX = columns[7];
				fileLocationY = rows[2];
			}
			if(getValue() == CardValue.SEVEN) {
				fileLocationX = columns[6];
				fileLocationY = rows[3];
			}
			if(getValue() == CardValue.EIGHT) {
				fileLocationX = columns[7];
				fileLocationY = rows[3];
			}
			if(getValue() == CardValue.NINE) {
				fileLocationX = columns[6];
				fileLocationY = rows[4];
			}
			if(getValue() == CardValue.TEN) {
				fileLocationX = columns[7];
				fileLocationY = rows[4];
			}
			if(getValue() == CardValue.JACK) {
				fileLocationX = columns[8];
				fileLocationY = rows[3];
			}
			if(getValue() == CardValue.QUEEN) {
				fileLocationX = columns[9];
				fileLocationY = rows[3];
			}
			if(getValue() == CardValue.KING) {
				fileLocationX = columns[8];
				fileLocationY = rows[4];
			}				
		}
	}

	public boolean looksSame(Card c) {
		if(mySuit == c.mySuit) {
			if(myValue == c.myValue) {
				return true;
			}
		}
		return false;
	}
	
	public class CardPoint {
		float x, y;
		
		 CardPoint(float newX, float newY) {
			x = newX;
			y = newY;
		}
		 
		 @Override
		public boolean equals(Object obj) {
			 if(obj instanceof CardPoint) {
				 CardPoint c = (CardPoint) obj;
				 if(this.x==c.x) {
					 if(this.y==c.y) {
						 return true;
					 }
				 }
			 }
			 return false;
		}
	}

}
