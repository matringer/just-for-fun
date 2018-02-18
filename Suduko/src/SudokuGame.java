/*
 * Author: Mat Ringer
 * Date: 2014 
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

public class SudokuGame {

	static CellGrid gameGrid;
	private int counter;
	ArrayList<ArrayList<Integer>> allPossiblesList = new ArrayList<>();
	private boolean weAreDone = false;
	int failCtr;
	private int methodCtr = 0;
	ArrayList<Integer> lookupQuadrant;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		playgame("c:\\temp\\suduko.txt");
	}

	public static CellGrid playgame(String filename) {
		System.out.println("-------------------------------");
		System.out.println("Playing game:  " + filename);
		System.out.println("-------------------------------");
		try {
			long start = System.nanoTime();
			SudokuGame game = new SudokuGame();
			game.readFile(filename);
			game.generateQuandrantList();
			SudokuGame.gameGrid.print();
			SudokuGame.gameGrid.printPossibles();
			game.generateCombos();
			long end = System.nanoTime();
			System.out.println("Started: " + start);
			System.out.println("Ended: " + end);
			double timeElapsed = end - start;
			System.out.println("Seconds to solve: " + new Double(timeElapsed / 1000.0/ 1000.0 / 1000.0));
			System.out.println("generateAllPossibles counter=" + game.methodCtr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return gameGrid;

	}

	public void readFile(String filename) throws Exception {
		File file = new File(filename);
		BufferedReader bfr = new BufferedReader(new FileReader(file));
		String line, valueStr;

		int lineCounter = 0;
		int size = 0;

		// first line is size
		line = bfr.readLine();
		size = Integer.parseInt(line);
		gameGrid = new CellGrid(size);

		while ((line = bfr.readLine()) != null) {
			StringTokenizer st = new StringTokenizer(line, ",");
			int tokenCounter = 0;
			while (st.hasMoreTokens()) {
				valueStr = st.nextToken();
				// System.out.println(valueStr);

				Cell c = gameGrid.cells[lineCounter][tokenCounter];
				if (valueStr.compareToIgnoreCase("x") != 0 && valueStr.compareToIgnoreCase("?") !=0) {
					c.value = Integer.parseInt(valueStr);
					c.possibleValues.clear();
				}
				tokenCounter++;
			}
			lineCounter++;
		}
		bfr.close();
	}

	private void generateCombos() {
		boolean madeChange = true;
		while (madeChange) {
			madeChange = removePossibleValuesFromGrid();
		}
		gameGrid.printPossibles();

		allPossiblesList = gameGrid.makeAllPossiblesList();
//		System.out.println("mat");
//		System.out.println(allPossiblesList);
//		System.out.println("matdone");
		generateAllPossibles(allPossiblesList.get(0), new ArrayList<Integer>());
	}

	private void generateAllPossibles(ArrayList<Integer> possibleValues, ArrayList<Integer> arrayList) {
		methodCtr++;
		if (weAreDone) {
			return;
		}
		for (int i = 0; i < possibleValues.size(); i++) {
//			 System.out.println("arrayList: " + arrayList.size() +  " " + arrayList);
			Integer currentPossible = possibleValues.get(i);
			if (doesNumberAlreadyExistInRowOrColumnOrQuadrant(arrayList, currentPossible)) {
				if (possibleValues.size() - 1 > i) {
					continue;
				} else {
					return;
				}
			}
			ArrayList<Integer> newComboList = new ArrayList<>(arrayList);
			newComboList.add(currentPossible);
			counter++;
			if (counter < allPossiblesList.size()) {
				generateAllPossibles(allPossiblesList.get(counter), new ArrayList<>(newComboList));
			} else {
				if (gameGrid.validateCombo(newComboList)) {
					System.out.println("**************************************");
					System.out.println("Done");
					gameGrid.print();
					gameGrid.writeToResultsFile("c:\\temp\\tempGameOutput.txt");
					weAreDone = true;
				} else {
					failCtr++;
					System.out.println("-------------------");
					System.out.println("Combo failed: " + failCtr + " - " + new Date());
					gameGrid.print();
				}
			}
			counter--;
		}
	}

	public boolean doesNumberAlreadyExistInRowOrColumnOrQuadrant(ArrayList<Integer> arrayList, Integer currentPossible) {
		// check row
		int rowCtr = arrayList.size() / 9 * 9;  //why did i do this?
//		System.out.println("matringer : "+ rowCtr + " : " + arrayList.size()+ " : " + arrayList);
		for (int i = rowCtr; i < arrayList.size(); i++) {
			if (arrayList.get(i).intValue() == currentPossible.intValue()) {
				return true;
			}
		}

		// check column
		int columnCtr = arrayList.size() % 9;
		while (columnCtr < arrayList.size()) {
			if (arrayList.get(columnCtr).intValue() == currentPossible.intValue()) {
				return true;
			}
			columnCtr += 9;
		}

		// check quadrant
		int currentQuadrant = getQuadrant(arrayList.size());
//		System.out.println("current quadrant is: " + currentQuadrant);
		for (int i = 0; i < arrayList.size(); i++) {
			if (getQuadrant(i) == currentQuadrant) {
				if (arrayList.get(i).intValue() == currentPossible.intValue()) {
					// System.out.println("arraylist: " + arrayList);
					// System.out.println("match for " + currentPossible +
					// " at quadrant: " + currentQuadrant);
					return true;
				}
			}
		}
		return false;
	}

	private void generateQuandrantList() {
		int size1 = 81;
		lookupQuadrant = new ArrayList<>(size1);
		int ctr = 0;
		int factor = 0;
		for (int i = 0; i < size1; i++) {
			if (i % 3 == 0) {
				ctr++;
			}
			if (i % 9 == 0) {
				ctr = 0;
			}
			if (i == 27) {
				factor = 3;
			} else if (i == 54) {
				factor = 6;
			}

			lookupQuadrant.add(new Integer(ctr + factor));
		}
	}

	private int getQuadrant(int input) {

		// System.out.println(lookupQuadrant);
		// System.out.println("lookup quandrant for " + input + " is " +
		// lookupQuadrant.get(input).intValue());
		return lookupQuadrant.get(input).intValue();
	}

	private boolean removePossibleValuesFromGrid() {
		boolean returnVal;
		boolean madeChange = false;
		for (int row = 0; row < gameGrid.size; row++) {
			for (int column = 0; column < gameGrid.size; column++) {
				Cell currentCell = gameGrid.cells[row][column];
				returnVal = removePossibleCellValues(currentCell);
				if (returnVal) {
					madeChange = true;
				}
			}
		}
		return madeChange;
	}

	private boolean removePossibleCellValues(Cell currentCell) {
		boolean returnVal;
		boolean madeChange = false;
		for (int i = 0; i < gameGrid.size; i++) {
			// check row
			returnVal = currentCell.removePossibleValue(gameGrid.cells[currentCell.row][i].value);

			if (returnVal) {
				madeChange = true;
			}
			// check column
			returnVal = currentCell.removePossibleValue(gameGrid.cells[i][currentCell.column].value);
			if (returnVal) {
				madeChange = true;
			}
			for (int j = 0; j < gameGrid.size; j++) {
				// check quandrant
				if (currentCell.quadrantId == gameGrid.cells[i][j].quadrantId) {
					returnVal = currentCell.removePossibleValue(gameGrid.cells[i][j].value);
				}
				if (returnVal) {
					madeChange = true;
				}
			}
		}
		return madeChange;
	}

}
