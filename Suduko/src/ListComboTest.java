/*
 * Author: Mat Ringer
 * Date: 2014 
 */

import java.util.ArrayList;

public class ListComboTest {

	/**
	 * @param args
	 */
	ArrayList<String> resultsList = new ArrayList<>();
	ArrayList<ArrayList> listall = new ArrayList<>();
	ArrayList<String> listone = new ArrayList<>();
	ArrayList<String> listtwo = new ArrayList<>();
	ArrayList<String> listthree = new ArrayList<>();
	ArrayList<String> listfour = new ArrayList<>();
	ArrayList<String> listfive = new ArrayList<>();
	ArrayList<String> listsix = new ArrayList<>();
	int counter = 0;
	ArrayList<ArrayList> resultsListCombos = new ArrayList<>();

	public static void main(String[] args) {
		new ListComboTest();
	}

	public ListComboTest() {

		listone.add("a");
		listone.add("b");
		listone.add("c");		
		
		listtwo.add("d");
//		listtwo.add("e");
//		listtwo.add("f");

		listthree.add("g");
		listthree.add("h");
		listthree.add("i");
		listthree.add("k");
		listthree.add("l");

		listfour.add("m");
		listfour.add("n");
		listfour.add("o1");
		listfour.add("o2");
		listfour.add("o3");
		listfour.add("o4");

		listfive.add("p");
		listfive.add("q");
		listfive.add("r");
		listfive.add("s");
		listfive.add("t");
		listfive.add("u");

		listsix.add("v");
		listsix.add("x");
		listsix.add("y");
		listsix.add("z");
		
		listall.add(listone);
		listall.add(listtwo);
		listall.add(listthree);
//		listall.add(listfour);
//		listall.add(listfive);
//		listall.add(listsix);

		goCombo(listone, "");
		System.out.println(resultsList.toString());

		System.out.println("combo2");
		goCombo2(listone, new ArrayList());
		for(ArrayList l : resultsListCombos) {
			System.out.println(l.toString());
		}
		System.out.println("size is " + resultsListCombos.size());
		
	}

	private void goCombo(ArrayList list, String inputStr) {
		for (int i = 0; i < list.size(); i++) {
			String s = list.get(i).toString();
			counter++;
			if (counter < listall.size()) {
				goCombo(listall.get(counter), inputStr + s);
			}
			else {
				resultsList.add(inputStr+s);
			}
			counter--;
		}
	}
	
	private void goCombo2(ArrayList list, ArrayList comboList) {
		for (int i = 0; i < list.size(); i++) {
			String s = list.get(i).toString();
			ArrayList newComboList = new ArrayList(comboList);
			newComboList.add(s);
			counter++;
			if (counter < listall.size()) {
				goCombo2(listall.get(counter), new ArrayList(newComboList));
			}
			else {
				resultsListCombos.add(newComboList);
			}
			counter--;
		}
	}

}
