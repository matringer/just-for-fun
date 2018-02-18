/*
 * Author: Mat Ringer
 * Date: 2014 
 */

import processing.core.PApplet;
import processing.serial.*;

public class SerialSimpleRead extends PApplet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5963706482308004781L;

	public static void main(String[] args) {
		PApplet.main(new String[] { "--present", "SerialSimpleRead", "--full-screen" });
	}

	Serial myPort; // Create object from Serial class
	int val; // Data received from the serial port

	@Override
	public void setup() {
		// size(screen.width, screen.height);
		String portName = Serial.list()[1];
		myPort = new Serial(this, portName, 9600);
	}

	@Override
	public void draw() {
		if (myPort.available() > 0) { // If data is available,
			val = myPort.read(); // read it and store it in val
		}
		// System.out.println(val);
		background(val); // Set background to white
	}

}
