/*
 * Author: Mat Ringer
 * Date: 2014 
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PFont;

public class SodukoSolver extends PApplet {

	private static final long serialVersionUID = 1177216435026693031L;
	ArrayList<Box> boxes = new ArrayList<>();
	ArrayList<Line> lines = new ArrayList<>();
	final int rectSize = 60;
	final int screenWidth = 1000;
	final int screenHeight = 670;
	private PFont fontA;
	final int fontSize = 36;
	final String validInputString = "123456789?";
	String solveButtonText = "Solve It";
	String resetButtonText = "Reset";
	int solveButtonTextX, solveButtonTextY, solveButtonRectX, solveButtonRectY, solveButtonRectWidth, solveButtonRectHeight;
	int resetButtonTextX, resetButtonTextY, resetButtonRectX, resetButtonRectY, resetButtonRectWidth, resetButtonRectHeight;
	String gameInputFile = "c:\\temp\\tempGameInput.txt";
	GameThread gameThreadObj;
	Thread gameThread;
	boolean showGameResults=false;
	private int alphaCounter = 0;

	public static void main(String[] args) {
		// PApplet.main(new String[] { "--present", "SodukoSolver" });
		PApplet.main(new String[] { "SodukoSolver" });
	}
	
	class GameThread implements Runnable {
		CellGrid cellgrid = new CellGrid(); //just to avoid NPE
		
		@Override
		public void run() {
			cellgrid = SudokuGame.playgame(gameInputFile);
			showGameResults=true;
		}
	}

	@Override
	public void setup() {
		size(screenWidth, screenHeight);
		smooth();
		calculateCoords();
		assignBoxQuandrants();
		// fontA = loadFont("c:\\temp\\facts\\Mangal-Bold-24.vlw");
		fontA = loadFont("AndaleMono-36.vlw");
		textFont(fontA, fontSize);
		solveButtonTextX = screenWidth - solveButtonText.length() * fontSize;
		solveButtonTextY = screenHeight / 2;
		solveButtonRectX = solveButtonTextX - 10;
		solveButtonRectY = solveButtonTextY - fontSize;
		solveButtonRectWidth = solveButtonText.length() * 25;
		solveButtonRectHeight = fontSize + 10;

		resetButtonTextX = solveButtonTextX;
		resetButtonTextY = solveButtonRectY + solveButtonRectHeight + 35;
		resetButtonRectX = solveButtonRectX;
		resetButtonRectY = solveButtonRectY + solveButtonRectHeight;
		resetButtonRectWidth = solveButtonRectWidth;
		resetButtonRectHeight = solveButtonRectHeight;

		gameThreadObj = new GameThread();
		gameThread = new Thread(gameThreadObj);
	}

	private void assignBoxQuandrants() {
		int quadrantValue=0;
		for(int i=0; i<81; i=i+9) {
			if(i==27) {
				quadrantValue=quadrantValue+3;
			}
			if(i==54) {
				quadrantValue=quadrantValue+3;
			}
			assignRowQuadVals(i, quadrantValue);
		}

		//print it out
//		for(int y=0; y<9; y++) {
//			for(int z=0; z<9; z++) {
//				System.out.print(boxes.get(y*9 + z).quadrant + " ");
//			}
//			System.out.println("");
//		}
//
	}

	
	private void assignRowQuadVals(int index, int quadrantValue) {
		for(int i=0; i<3; i++) {
			for(int j=0; j<3; j++) {	
				boxes.get(index+i*3+j).quadrant = quadrantValue;
			}
			quadrantValue++;
		}
	}

	@Override
	public void draw() {
		background(255, 255, 255);
		strokeWeight(1);

		// highlight solve button if hovered
		if (mouseOverSolveButton() && !gameThread.isAlive()) {
			fill(0, 0, 0, 50);
		} else {
			noFill();
		}
		rect(solveButtonRectX, solveButtonRectY, solveButtonRectWidth, solveButtonRectHeight);

		// highlight reset button if hovered
		if (mouseOverResetButton() && !gameThread.isAlive()) {
			fill(0, 0, 0, 50);
		} else {
			noFill();
		}
		rect(resetButtonRectX, resetButtonRectY, resetButtonRectWidth, resetButtonRectHeight);

		// draw solve button text
		fill(100, 10, 200);
		text(solveButtonText, solveButtonTextX, solveButtonTextY);

		// draw reset button text
		fill(100, 10, 200);
		text(resetButtonText, resetButtonTextX, resetButtonTextY);

		// draw boxes and values
		for (Box b : boxes) {
			if (b.active) {
				fill(0, 0, 0, 50);
			} else if (b.flash) {
				fill(200, 10, 25, 200);
				b.flashcounter++;
				if (b.flashcounter > 10) {
					b.flash = false;
					b.flashcounter = 0;
				}
			} else {
				noFill();
			}
			rect(b.point.myX, b.point.myY, rectSize, rectSize);

			fill(b.r, b.g, b.b, b.alpha);
			text(b.value, b.point.myX + fontSize / 2, b.point.myY + fontSize);
		}

		strokeWeight(3);
		for (Line l : lines) {
			line(l.myLineStart.myX, l.myLineStart.myY, l.myLineEnd.myX, l.myLineEnd.myY);
		}
		
		if(showGameResults) {
			CellGrid cg =gameThreadObj.cellgrid;
			for (int i = 0; i < cg.size; i++) {
				for (int j = 0; j < cg.size; j++) {
					String intStr = Integer.toString(cg.cells[i][j].value);
					boxes.get(i * 9 + j).value = intStr.charAt(0);
				}
			}
			showGameResults=false;
		}
		else {
			if(gameThread.isAlive()) {
				fill(255, 100, 0, alphaCounter );
				text("Thinking", 700, 200);
				alphaCounter = alphaCounter+10;
				if(alphaCounter > 255) {
					alphaCounter=0;
				}
			}
		}
	}

	private boolean mouseOverResetButton() {
		if (mouseX > resetButtonRectX && mouseX < resetButtonRectX + resetButtonRectWidth) {
			if (mouseY > resetButtonRectY && mouseY <= resetButtonRectY + resetButtonRectHeight) {
				return true;
			}
		}
		return false;
	}

	private boolean mouseOverSolveButton() {
		if (mouseX > solveButtonRectX && mouseX < solveButtonRectX + solveButtonRectWidth) {
			if (mouseY > solveButtonRectY && mouseY <= solveButtonRectY + solveButtonRectHeight) {
				return true;
			}
		}
		return false;
	}

	private void calculateCoords() {
		int pad = 10;
		int rectStartX = pad;
		int rectStartY = pad;

		for (int i = 1; i < 10; i++) {
			for (int j = 1; j < 10; j++) {
				boxes.add(new Box(rectStartX, rectStartY));
				rectStartX = rectStartX + rectSize + pad;
				if (j % 3 == 0 && j != 9) {
					lines.add(new Line(rectStartX, pad, rectStartX, 9 * (rectSize + pad) + 2 * pad));
					rectStartX += pad;
				}
			}
			rectStartX = pad;
			rectStartY = rectStartY + rectSize + pad;

			if (i % 3 == 0 && i != 9) {
				lines.add(new Line(pad, rectStartY, 9 * (rectSize + pad) + 2 * pad, rectStartY));
				rectStartY += pad;
			}

		}

	}

	@Override
	public void mousePressed() {
		if(gameThread.isAlive()) {
			return;
		}
		if (mouseOverSolveButton()) {
			writeInputFile();
			gameThread = new Thread(gameThreadObj);
			gameThread.start();

			return;
		}
		if (mouseOverResetButton()) {
			for (Box bp : boxes) {
				bp.setToDefault();
			}
			return;
		}

		for (Box b : boxes) {
			b.active = false;
			if (mouseX > b.point.myX && mouseX < b.point.myX + rectSize) {
				if (mouseY > b.point.myY && mouseY < b.point.myY + rectSize) {
					b.active = true;
				}
			}
		}

	}

	private void writeInputFile() {

		File file = new File(gameInputFile);
		try {
			BufferedWriter bfw = new BufferedWriter(new FileWriter(file));
			bfw.write("9\r\n");

			for (int i = 1; i <= boxes.size(); i++) {
				bfw.write(boxes.get(i - 1).value);
				if (i % 9 == 0) {
					bfw.write("\r\n");
				} else {
					bfw.write(",");
				}
			}
			bfw.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void keyTyped() {
		for (Box b : boxes) {
			if (b.active) {
				if (validInputString.indexOf(key) >= 0) {
					if(key == '?') {
						//they are just reseting this box back to default
						b.setToDefault();;
						return; 
					}
					int match = numberAlreadyExistsInRowOrColumnOrQuadrant(boxes.indexOf(b), key);
					if (match < 0) { // no match
						b.value = key;
						b.r = 0;
						b.g = 200;
						b.b = 100;
						b.alpha = 255;
					} else { // got a conflict
						boxes.get(match).flash = true;
					}
				}
			}

		}

	}

	/*
	 * boxIndex is the index into boxes of the proposedValue
	 * 
	 * check if value already exists and if so, return the index of the conflict so we can highlight it
	 */
	public int numberAlreadyExistsInRowOrColumnOrQuadrant(int boxIndex, char proposedValue) {
		// check for row duplicates
		int row = boxIndex / 9; // determine row #
		for (int i = row * 9; i < (row + 1) * 9; i++) {
			if (boxes.get(i).value == proposedValue) {
				return i;
			}
		}

		// check for column duplicates
		int column = boxIndex;
		while (column > 0) {
			column = column - 9;
		}
		if (column < 0) {
			column = column + 9;
		}

		for (int i = column; i < boxes.size(); i = i + 9) {
			if (boxes.get(i).value == proposedValue) {
				return i;
			}
		}

		// check for quadrant duplicates
		for(int i=0; i<boxes.size(); i++){
			if(boxes.get(i).quadrant == boxes.get(boxIndex).quadrant) {
				if(boxes.get(i).value == proposedValue) {
					return i;
				}
			}
		}

		return -1;
	}

	class Point {
		int myX;
		int myY;

		Point(int x, int y) {
			myX = x;
			myY = y;
		};
	}

	class Box {
		public int quadrant;
		public boolean flash = false;
		int flashcounter = 0;
		char value = '?';
		boolean active = false;
		Point point;
		int r, g, b, alpha = 100;

		Box(int x, int y) {
			point = new Point(x, y);
		}

		@Override
		public String toString() {
			return "value: " + value + " quadrant: " + quadrant;
		}

		void setToDefault() {
			r = 0;
			g = 0;
			b = 0;
			alpha = 100;
			value = '?';
			active = false;
		}
	}

	class Line {
		Point myLineStart;
		Point myLineEnd;

		Line(int startx, int starty, int endx, int endy) {
			myLineStart = new Point(startx, starty);
			myLineEnd = new Point(endx, endy);
		}
	}
}
