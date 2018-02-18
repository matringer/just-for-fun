/*
 * Author: Mat Ringer
 * Date: 2014 
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.Random;
import java.util.StringTokenizer;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;

public class VisualFacts extends PApplet {

	private static final long serialVersionUID = 1L;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		 PApplet.main(new String[] { "--present", "VisualFacts" });
		PApplet.main(new String[] { "VisualFacts" });
	}

	ArrayList<String> questionList = new ArrayList<>();
	ArrayList<String> answerList = new ArrayList<>();
	ArrayList<String> yardsGainedList = new ArrayList<>();
	ArrayList<String> yardsLostList = new ArrayList<>();
	
	private final int MAX_TEAMS=4;
	int currentTeam, backgroundColorR, backgroundColorG, backgroundColorB, questionIndex, questionCounter, quesitonLimit, numberOfTeams = 0;
	PFont fontA;
	private String input = "";
	private boolean endInputForQuestion, endInputForPlayers, addYards, subtractYards, showGameOver, askForTeams = false;
	PImage thumbsUpImage, blntImage, allDoneImage, fbFieldImage = null;
	ArrayList<FactPlayer> teams = new ArrayList<>(MAX_TEAMS);
	Properties propsFile= new Properties();
	
	@Override
	public void setup() {
		try {
			loadPropertiesFile();

			size(900, 600);
			smooth();

			thumbsUpImage = loadImage(propsFile.getProperty("answer_correct_image"));
			blntImage = loadImage(propsFile.getProperty("answer_wrong_image"));
			allDoneImage = loadImage(propsFile.getProperty("all_done_image"));
			fbFieldImage = loadImage(propsFile.getProperty("foot_ball_field_image"));
			
			teams.add( new FactPlayer(propsFile.getProperty("team_image_1"), 255, 0, 0, "Red Team") );
			teams.add( new FactPlayer(propsFile.getProperty("team_image_2"), 255, 0, 255, "Purple Team") );
			teams.add( new FactPlayer(propsFile.getProperty("team_image_3"), 0, 0, 255, "Blue Team") );
			teams.add( new FactPlayer(propsFile.getProperty("team_image_4"), 255, 255, 0, "Yellow Team") );
			
			fontA = loadFont(propsFile.getProperty("font_file"));
			textFont(fontA);

			loadFactFile();
			askForTeams=true;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loadPropertiesFile() throws Exception {
		File f= new File("fact-properties.txt");
		FileReader fr = new FileReader(f);
		propsFile.load(fr);
		propsFile.list(System.out);
		fr.close();
		
		backgroundColorR = Integer.parseInt(propsFile.getProperty("background_color_red"));
		backgroundColorG = Integer.parseInt(propsFile.getProperty("background_color_green"));
		backgroundColorB = Integer.parseInt(propsFile.getProperty("background_color_blue"));
		
		quesitonLimit = Integer.parseInt(propsFile.getProperty("max_questions"));
	}

	private void loadFactFile() throws Exception {
		File f = new File(propsFile.getProperty("facts_file"));
		FileReader fr = new FileReader(f);
		BufferedReader bfr = new BufferedReader(fr);
		String line, question, answer, yardsGained, yardsLost;

		while ((line = bfr.readLine()) != null) {
			if(line.startsWith("#")) {
				//this is a comment so ignore
				continue;
			}
			StringTokenizer st = new StringTokenizer(line, ",");
			while (st.hasMoreTokens()) {
				question = st.nextToken();
				answer = st.nextToken();
				if (st.hasMoreTokens()) {
					yardsGained = st.nextToken();
				} else {
					yardsGained = "1"; // default is 1
				}
				if (st.hasMoreTokens()) {
					yardsLost = st.nextToken();
				} else {
					yardsLost = "1"; // default is 1
				}

				questionList.add(question.trim());
				answerList.add(answer.trim());
				yardsGainedList.add(yardsGained.trim());
				yardsLostList.add(yardsLost.trim());
				
			}

		}
		bfr.close(); fr.close(); 
		System.out.println("quesitonList: " + questionList.toString());
//		System.out.println("answerList: " + answerList.toString());
//		System.out.println("pointList: " + pointList.toString());
	}

	@Override
	public void draw() {		
		background(backgroundColorR, backgroundColorG, backgroundColorB);
		
		if(askForTeams) {
			showAskForPlayers();
			return;
		}
		
		showFootballField();
		showPlayers();

		if(showGameOver) {
			showGameOver();
			return;
		}
		
		showPlayerTurn();
		showQuestion();
		showInput();
		
		if (endInputForQuestion) {
			showAnswer();
		}
		
		showQuestionCount();
	}

	private void showPlayers() {
		int startY=260;
		for(int i=0; i<numberOfTeams; i++) {
			boolean highlight=false;
			if(i==currentTeam) {
				highlight=true;
			}
			teams.get(i).showImage(93, startY, highlight);
	
			startY += 80;
		}

	}

	private void showAskForPlayers() {
		if(endInputForPlayers) {
			if(numberOfTeams==0) {
				try {
					numberOfTeams=Integer.parseInt(input);
				}
				catch (NumberFormatException e) {
					e.printStackTrace();
					numberOfTeams=1;
				}
				numberOfTeams = Math.max(numberOfTeams, 1);
				numberOfTeams = Math.min(numberOfTeams, MAX_TEAMS);
			}
			fill(255, 255, 255);
			text("There will be " + numberOfTeams + " team(s)!", 50, 50);
			int x=100; int y=100;
			for(int i=0; i<numberOfTeams; i++) {
				teams.get(i).showName(x, y);
				y+=50;
			}
		}
		else {
			fill(0, 0, 255);
			String s = "How many teams?";
			text(s, 50, 50);
			showInput(s.length()*13 + 70, 50);
		}
	}

	private void showFootballField() {
		float fbFieldHeight=360;
		float fbFieldWidth=(float) 2.44*fbFieldHeight; //keep original aspect ratio
		image(this.fbFieldImage, 10, 225, fbFieldWidth, fbFieldHeight);
		
	}

	private void showGameOver() {
		fill(255, 255, 0);
		text("All done!", 25, 60);
	}

	private void showQuestionCount() {
		fill(255, 255, 255);
		text("Question\n" + questionCounter, width - 150, 25);
	}
	
	private void showAnswer() {
		String answer = this.answerList.get(questionIndex);
		fill(0, 0, 255);
		String txt = "Answer is:  " + answer;
		if (input.compareTo(answer) == 0) {
			addYards = true;
			fill(0, 0, 255);
			txt = txt + "\nYou got it RIGHT!\nYou gained " + yardsGainedList.get(questionIndex) + " yards(s).";
			image(thumbsUpImage, 400, 15, 266, 200);
		} else {
			// better luck next time
			subtractYards = true;
			fill(0, 0, 255);
			txt = txt + "\nYou got it wrong!\nYou lost " + yardsLostList.get(questionIndex) + " yards(s).";
			image(blntImage, 400, 15, 266, 200);
		}
		text(txt, 25, 125);
	}

	private void incrementCurrentPlayer() {
		if(questionCounter==0) {
			//don't increment if still on the first question
			currentTeam=0;
			return;
		}
		else  {
			for(int i=currentTeam+1; i<=MAX_TEAMS; i++) { 
				if(i >= numberOfTeams) {
					//wrap around
					i=0;
				}
				if(teams.get(i).touchdown == false) {
					//it's this team's turn now
					currentTeam=i;
					return;
				}
				if(i==currentTeam) {
					//we've cycled through back to the current team so stop looping
					break;
				}
			}
			System.out.println("game over");
			showGameOver=true;
		}
	}

	private void showInput() {
		fill(0, 0, 0);
		text(input, (float)(questionList.get(questionIndex).length() * 11.5 + 60), 60);
	}

	private void showInput(float x, float y) {
		fill(0, 0, 0);
		text(input, x, y);
	}

	private void showQuestion() {
		fill(0, 0, 255);
		String s = questionList.get(questionIndex);
		s = s + " = ";
		if(input.length()==0) {
			//show a question mark if there's nothing typed
			s = s + "?";
		}
		text(s, 25, 60);
	}

	private void showPlayerTurn() {
		//print player name
		fill(getCurrentTeam().myRedValue, getCurrentTeam().myGreenValue, getCurrentTeam().myBlueValue);
		text(getCurrentTeam().myPlayerName,25, 25);
	}


	@Override
	public void keyTyped() {
		if (endInputForQuestion && endInputForPlayers) {
			return;
		}
		if (key == BACKSPACE) {
			if (input.length() > 0) {
				//remove last typed character
				input = input.substring(0, input.length() - 1);
			}
			return;
		}
		if (key == ENTER || key == RETURN) {
			endInputForQuestion = true;
			endInputForPlayers = true;
			return;
		}

		//we can only accept so much input 
		if(input.length()<20) {
			input = input + key;
		}		
	}

	@Override
	public void mousePressed() {
		input = ""; //blank out the input
		if (endInputForQuestion) {
			if (addYards) {
				getCurrentTeam().incrementYards(yardsGainedList.get(questionIndex));
				addYards = false;
			}
			if (subtractYards) {
				getCurrentTeam().decrementYards(yardsLostList.get(questionIndex));
				subtractYards = false;
			}
			if(questionCounter >= quesitonLimit) {
				showGameOver=true;
			}
			else {
				incrementCurrentPlayer();
				setNextQuestionIndex();
			}
			endInputForQuestion = false;
		}
		if(endInputForPlayers) {
			askForTeams=false;
		}
	}

	private FactPlayer getCurrentTeam() {
		//just made this for readability
		return teams.get(currentTeam);
	}

	private void setNextQuestionIndex() {
		Random random = new Random(new Date().getTime());
		int randomNbr = random.nextInt(questionList.size());

		if (randomNbr == questionIndex) {
			questionIndex += 1;
			if (questionIndex >= questionList.size()) {
				questionIndex = 0;
			}
		} else {
			questionIndex = randomNbr;
		}
		questionCounter++;
	}
	
	public class FactPlayer {
		String myPlayerName=null;
		private PImage myImage = null;
		int myRedValue, myGreenValue, myBlueValue, currentYard=0;
		boolean isVisible=true;
		double pixelsToYards = 6.7;
		boolean touchdown = false;
		
		public PImage getImage() {return myImage;};
		
		public void showName(int x, int y) {
			fill(myRedValue, myGreenValue, myBlueValue);
			text(myPlayerName, x, y);
		}

		public void showImage(int x, int y, boolean highlight) {
			if(isVisible) {
				image(myImage, getCurrentImagePositionX() + x, y, 40, 40);
				if(touchdown) {
					Random random = new Random(new Date().getTime());
					showOutline(random.nextInt(256), random.nextInt(256), random.nextInt(256), x, y);
				}
				else if(highlight) {
					showOutline(myRedValue, myGreenValue, myBlueValue, x, y);
				}
			}
		}

		private void showOutline(int r, int b, int g, int x, int y) {
			noFill();
			strokeWeight(5);
			stroke(r, b, g);
			rect(getCurrentImagePositionX() + x, y, 40, 40);
		}

		public FactPlayer(String filename, int r, int g, int b, String playerName) {
			myImage = loadImage(filename);
			myRedValue=r;
			myGreenValue=g;
			myBlueValue=b;
			myPlayerName = playerName;
		}
		
		
		public int parseYards(String s) {
			int amount=1;
			try {
				amount = Integer.parseInt(s);
			}
			catch (NumberFormatException e) {
				e.printStackTrace();
			}

			return amount;	
		}
		
		public void incrementYards(String s) {
			adjustYards( parseYards(s) );
		}

		public void decrementYards(String s) {
			adjustYards(parseYards(s) * -1);
		}
		
		public void adjustYards (int amount) {
			currentYard=currentYard+amount;
			currentYard=Math.max(currentYard, 0); //can't be less than 0
			currentYard=Math.min(currentYard, 101); //can't be more than 100 (101 is for appearances)
			if(currentYard==101) {
				touchdown=true;
			}
		}
		
		public int getCurrentImagePositionX() {
			return (int) (currentYard * pixelsToYards);
		}

	}
}
