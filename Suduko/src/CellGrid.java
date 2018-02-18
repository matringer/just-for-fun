/*
 * Author: Mat Ringer
 * Date: 2014 
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CellGrid {
	Cell[][] cells;
	int size;
	int cellSize;
	int quadrantSize;
	Cell[] validationCells;
	private int rowCtr;
	private int colCtr;
	final static int validationTotal = 9 + 8 + 7 + 6 + 5 + 4 + 3 + 2 + 1;

	public static void main(String[] args) {
		CellGrid cg = new CellGrid();

		// test
		for (int i = 0; i < cg.size; i++) {
			for (int j = 0; j < cg.size; j++) {
				cg.cells[i][j] = new Cell(0);
				cg.cells[i][j].value = j;
				cg.cells[i][j].print();
			}
			System.out.print("\n");
		}
		// check quadrants
		for (int i = 0; i < cg.size; i += cg.quadrantSize) {
			for (int j = 0; j < cg.size; j += cg.quadrantSize) {
				System.out.println("checking " + i + "," + j);
				cg.calcQuadrant(i, j);
			}
		}
		
		List<Cell[]> a = Arrays.asList(cg.cells);
		System.out.println(a.size());

	}

	public CellGrid(int size) {
		this.size = size;
		this.cellSize = size * size;
		quadrantSize = (int) Math.sqrt(size);
		cells = new Cell[size][size];

		for (int skip = 0; skip < size; skip += quadrantSize) {
			for (int column = 0; column < size; column++) {
				for (int row = skip; row < skip + quadrantSize; row++) {
					Cell c = new Cell(0);
					c.quadrantId = column / quadrantSize + skip;
					cells[column][row] = c;
				}

			}

		}
		
		for(int i=0; i<size; i++){
			for (int j=0; j<size; j++) {
				cells[i][j].generatePossibleValues(size);
				cells[i][j].row = i;
				cells[i][j].column = j;
			}
		}
	}

	public CellGrid() {
		this(9);
	}

	boolean possiblesLeft() {
		for (int row = 0; row < size; row++) {
			for (int column = 0; column < size; column++) {
				if (cells[row][column].possibleValues.size() > 0)
					return false;
			}
		}
		return true;
	}
	
	public Cell getFirstCellWithPossibles() {
		for (int row = 0; row < size; row++) {
			for (int column = 0; column < size; column++) {
				if (cells[row][column].possibleValues.size() > 0) {
					return cells[row][column];
				}
			}
		}
		return null;
	}
	
	public ArrayList<ArrayList<Integer>> makeAllPossiblesList() {
		ArrayList<ArrayList<Integer>> listall = new ArrayList<>();
		for (int row = 0; row < size; row++) {
			for (int column = 0; column < size; column++) {
				if (cells[row][column].possibleValues.size() > 0) {
					listall.add(cells[row][column].possibleValues);
				}
				else {
					ArrayList<Integer> a = new ArrayList<>();
					a.add(cells[row][column].value);
					listall.add(a);
				}
			}
		}
		return listall;
	}

//	public boolean validateCombo(ArrayList<Cell> newComboList) {
	public boolean validateCombo(ArrayList<Integer> newComboList) {
		int ctr=0;
		for (int row = 0; row < size; row++) {
			for (int column = 0; column < size; column++) {
//				if (cells[row][column].possibleValues.size() > 0) {
//					cells[row][column]=newComboList.get(ctr);
					cells[row][column].value=newComboList.get(ctr);
					ctr++;
//				}
			}
		}
//		System.out.print("combo: ");
//		this.print();
		return validateGrid();
	}
	
	boolean validateGrid() {
		// check rows and columns
		for (int row = 0; row < size; row++) {
			int rowTotal = 0;
			int columnTotal = 0;
			for (int column = 0; column < size; column++) {
				rowTotal += cells[row][column].value;
				columnTotal += cells[column][row].value;
			}
			if (rowTotal != validationTotal || columnTotal != validationTotal) {
				return false;
			}
		}

		// check quadrants
		for (int i = 0; i < size; i += quadrantSize) {
			for (int j = 0; j < size; j += quadrantSize) {
				if (calcQuadrant(i, j) != true) {
					return false;
				}
			}
		}

		return true;

	}

	private boolean calcQuadrant(int startRow, int startColumn) {
		int quadrantTotal = 0;
		for (int row = startRow; row < startRow + quadrantSize; row++) {
			for (int column = startColumn; column < startColumn + quadrantSize; column++) {
				// System.out.println("cell value=" + cells[row][column].value);
				quadrantTotal += cells[row][column].value;
			}
		}
//		System.out.println("quadrantTotal=" + quadrantTotal);
		if (quadrantTotal == validationTotal) {
			return true;
		}
		return false;
	}

	public void print() {
		System.out.println("Grid:");
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				System.out.print(cells[i][j].value + ",");
			}
			System.out.println("");
		}
	}

	public void printPossibles() {
		System.out.println("Possibles Grid:");
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if(cells[i][j].possibleValues.size() > 0) {
					System.out.print(cells[i][j].possibleValues.toString() + ",");
				}
				else {
					System.out.print(cells[i][j].value + ",");
				}
			}
			System.out.println("");
		}
	}

	public CellGrid copy() {
		CellGrid cg = new CellGrid(this.size);
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				cg.cells[i][j].value = this.cells[i][j].value;
			}
		}
		return cg;
	}

	void resetIterator() {
		rowCtr = 0;
		colCtr = 0;
	}
	
	public Cell getCell(int location) {
		int row = (location/size);
		int column=(location%9);
		return cells[row][column];
	}
	
	public Cell getNextCell() {

		Cell c = null;
		
		if(colCtr <= size-1) {
			//not on last column
			c = cells[rowCtr][colCtr];
			colCtr++;
		}
		else {
			rowCtr++;
			colCtr=0;
			if(rowCtr <= size-1) {
				//not on last row
				c = cells[rowCtr][colCtr];
			}
		}
		//no more left so return nothing
		return c;
	}

	public void writeToResultsFile(String string) {
		// TODO Auto-generated method stub
		
	}
}
