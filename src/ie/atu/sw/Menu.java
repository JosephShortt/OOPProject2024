
package ie.atu.sw;

public class Menu {
	//Time Complexity - O(1)
	//Menu class to display user menu
	public void loadMenu() {
		System.out.println(ConsoleColour.RED_BOLD);
		System.out.println("************************************************************");
		System.out.println("*     ATU - Dept. of Computer Science & Applied Physics    *");
		System.out.println("*                                                          *");
		System.out.println("*          Similarity Search with Word Embeddings          *");
		System.out.println("*                                                          *");
		System.out.println("************************************************************");
		System.out.println("(1) Simplify Text");
		System.out.println("(2) Search word in embeddings");
		System.out.println("(-1) Quit");
		
		//Output a menu of options and solicit text from the user
		System.out.print(ConsoleColour.BLACK_BOLD_BRIGHT);
		System.out.print("Select Option [1-?]>");
		System.out.println();
	}

}
