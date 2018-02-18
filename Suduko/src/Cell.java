/*
 * Author: Mat Ringer
 * Date: 2014 
 */

import java.util.ArrayList;


public class Cell {

	ArrayList<Integer> possibleValues = new ArrayList<>();

	int value =0;
	int quadrantId=0;
	public int row = 0;
	public int column = 0;
	
	public Cell(int val) {
		value = val;
	}
	
	public void generatePossibleValues(int size) {
		for (int i=0; i<size; i++) {
			possibleValues.add(new Integer(i+1));
		}
	}

	public void addPossibleValue(int val) {
		if(! containsPossibleValue(val)) {
			possibleValues.add(new Integer(val));
		}
	}
	
	public void print() {
		System.out.print(value + " ");
	}

	public void printPossibles() {
		for(Integer i : possibleValues) {
			System.out.print(i + ",");
		}
		System.out.print("\n");
		
	}

	public boolean containsPossibleValue(int value2) {
		for(Integer i : possibleValues) {
			if(value2 == i) {
				return true;
			}
		}
		return false;
	}

	public boolean removePossibleValue(int valueToRemove) {
		boolean madeChange = false;
		for(int i=0; i<possibleValues.size(); i++) {
			if(possibleValues.get(i) == valueToRemove ) {
				possibleValues.remove(i);
				madeChange = true;
			}
		}
//		if(possibleValues.size()==1) {
//			value = possibleValues.get(0);
//			possibleValues.remove(0);
//		}
		return madeChange;
	}

	public Cell copy() {
		Cell c = new Cell(this.value);
		c.column = this.column;
		c.row = this.row;
		c.quadrantId = this.quadrantId;
		return c;
	}
}
