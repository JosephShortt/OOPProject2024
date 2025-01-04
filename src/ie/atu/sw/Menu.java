
package ie.atu.sw;

/**
 * Handles menu display for the text processing application.
 *
 * @version 1.0.0
 * @author [Joseph Shortt]
 */

public class Menu {
	
	 /**
     * Displays application menu with color formatting.
     * Time Complexity: O(1)
     */
	
	public void loadMenu() {
		System.out.println(ConsoleColour.RED_BOLD);
		System.out.println("************************************************************");
		System.out.println("*     ATU - Dept. of Computer Science & Applied Physics    *");
		System.out.println("*                                                          *");
		System.out.println("*          Simplify Sentence with Word Embeddings          *");
		System.out.println("*                                                          *");
		System.out.println("************************************************************");
		System.out.println("(1) Simplify Text");
		System.out.println("(2) Search word in embeddings");
		System.out.println("(3)Search for word in google-1000");
		System.out.println("(4) Output length of google-1000");
		System.out.println("(5) Output length of glove embeddings");
		System.out.println("(-1) Quit");
		
		//Output a menu of options and solicit text from the user
		System.out.print(ConsoleColour.BLACK_BOLD_BRIGHT);
		System.out.print("Select Option [1-4]>");
		System.out.println();
	}

}
