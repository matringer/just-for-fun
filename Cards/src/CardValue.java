/*
 * Author: Mat Ringer
 * Date: 2014 
 */

public enum CardValue {
	ACE(1), TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7), EIGHT(8), NINE(9), TEN(10), JACK(11), QUEEN(12), KING(13);

	private int value;
	private boolean acesHigh;

	int getValue() {
		if (value == 1 && acesHigh) {
			return 14;
		}
		return value;
	}

	CardValue(int value) {
		this.value = value;
	}

	void setAcesHigh(boolean ah) {
		acesHigh = ah;
	}
}
