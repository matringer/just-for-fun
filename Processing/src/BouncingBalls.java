/*
 * Author: Mat Ringer
 * Date: 2014 
 */

import java.util.ArrayList;
import processing.core.*;

public class BouncingBalls extends PApplet {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		 PApplet.main(new String[] { "--present", "BouncingBalls" });
	}

	ArrayList balls;
	int ballWidth = 75;
	int ballWidthDividedByTwo = ballWidth / 2;

	@Override
	public void setup() {
		// frameRate(90);
		size(800, 600);
		smooth();
		noStroke();

		// Create an empty ArrayList
		balls = new ArrayList();

		// Start by adding one element
		balls.add(new Ball(width / 2, 0, ballWidth));
	}

	@Override
	public void draw() {
		background(255);

		// With an array, we say balls.length, with an ArrayList, we say
		// balls.size()
		// The length of an ArrayList is dynamic
		// Notice how we are looping through the ArrayList backwards
		// This is because we are deleting elements from the list
		for (int i = balls.size() - 1; i >= 0; i--) {
			// An ArrayList doesn't know what it is storing so we have to cast
			// the object coming out
			Ball ball = (Ball) balls.get(i);
			ball.move();
			ball.display();
			if (ball.finished()) {
				// Items can be deleted with remove()
				balls.remove(i);
			}
		}
	}

	@Override
	public void mousePressed() {
		// A new ball object is added to the ArrayList (by default to the end)
		balls.add(new Ball(mouseX, mouseY, ballWidth));
	}

	// Simple bouncing ball class

	public class Ball {

		float x;
		float y;
		float speed;
		float gravity;
		float w;
		float life = 255;
		float colorRed, colorBlue, colorGreen;

		public Ball(float tempX, float tempY, float tempW) {
			x = tempX;
			y = tempY;
			w = tempW;
			speed = 0;
			gravity = 0.07f;
			colorRed = random(255);
			colorBlue = random(255);
			colorGreen = random(255);
		}

		void move() {
			move(height);
		}
			
		void move(int userHeight) {
			// Add gravity to speed
			speed = speed + gravity;
			// Add speed to y location
			y = y + speed;
			// If square reaches the bottom
			// Reverse speed
			if (y > userHeight - ballWidthDividedByTwo) {
				// Dampening
				speed = speed * -0.9f;
				y = userHeight - ballWidthDividedByTwo;
			}

		}

		boolean finished() {
			// Balls fade out
			life = life - .2f;
			if (life < 0) {
				return true;
			} else {
				return false;
			}
		}

		void display() {
			// Display the circle
			fill(colorRed, colorGreen, colorBlue, life);
			// stroke(0,life);
			ellipse(x, y, w, w);
		}
	}

}
/*
Another important note. The Processing "color" primitive does not exist in Java. In fact, in Processing a "color" is really just an integer (32 bits with red, green, blue, and alpha components). Processing translates "color" to "int", but Eclipse won't do that for you. So instead of saying: 
color pink = color(255,200,200);

you should say: 
int pink = color(255,200,200);
and if you are in another class and have to refer to the "parent" PApplet:
int pink = parent.color(255,200,200); 
 
*/