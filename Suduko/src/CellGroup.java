/*
 * Author: Mat Ringer
 * Date: 2014 
 */

import java.util.ArrayList;


public class CellGroup {
	ArrayList<Cell> cells = new ArrayList<>();
	
	public void add(Cell c) {
		cells.add(c);
	}
	
	public int size() {
		return cells.size();
	}

	public void print() {
		for(Cell s : cells) {
			s.print();
		}
		System.out.println("");
	}
}
