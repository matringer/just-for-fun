/*
 * Author: Mat Ringer
 * Date: 2014 
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Date;


public class MatLogger {
	public static boolean logToScreen = true;
	public static boolean logToFile = false;
	public static boolean logFileIntitalized=false;

	static File outputFile;
	static FileWriter fw;
	static BufferedWriter bfw;

	static {
		if(logToFile) {
			try {
				Date d = new Date();
				outputFile = new File("c:\\temp\\solitaire-logs\\log-" + d.getTime() +".matringer" );
				fw = new FileWriter(outputFile);
				bfw = new BufferedWriter(fw);
				logFileIntitalized=true;
	//			bfw.append(new Date().toLocaleString() + "\n");
	//			bfw.append("-------------------------------------\n");
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void printLine(String s) {
		print(s + "\n");
	}

	public static void print(String s) {
		if(logToScreen) {
			System.out.print(s);
		}
		if(logToFile) {
			if(logFileIntitalized) {
				printToFile(s);
			}
			else {
				System.err.println("log to file failed!");
			}
		}
	}

	
	private static void printToFile(CharSequence c) {
		try {
			bfw.append(c);
			bfw.flush();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public enum LogLevel {
		Screen,
		File;
	}
}
