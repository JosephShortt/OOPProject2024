package ie.atu.sw;

import java.util.Scanner;

public class Runner {
    public static void main(String[] args) throws Exception {
    	//File paths
        String embeddingsFile = "";
        String google1000File = "";
        String outputFile = "";
        String inputFile = "";
        //Option variable for menu items
        int option = 0;
        
        //Declaring scanner for user input
        Scanner scanner = new Scanner(System.in);
        
        // Create objects
        WordEmbeddings gloveEmbeddings = new GloVeEmbeddingsMap();
        WordEmbeddings googleEmbeddings = new GoogleEmbeddingsMap(gloveEmbeddings);
        TextProcessor textProcessor = new ConcurrentTextProcessor(gloveEmbeddings, googleEmbeddings);
        Menu menu = new Menu();
        
        
        //Ask user to enter in file paths
        System.out.println("Please enter the path and name for glove embeddings file:");
        embeddingsFile = scanner.nextLine();

        System.out.println("Please enter the path and name of the google1000 file:");
        google1000File = scanner.nextLine();

        System.out.println("Please enter the path and name of the text file to simplify:");
        inputFile = scanner.nextLine();

        System.out.println("Please enter the path and name of the output file:");
        outputFile = scanner.nextLine();
        

        // Load embeddings
        gloveEmbeddings.load(embeddingsFile);
        System.out.println("GloVe embeddings loaded successfully");

        googleEmbeddings.load(google1000File);
        System.out.println("Google 1000 words loaded successfully");
        
        //Loop menu items 
        while (option != -1) {
            menu.loadMenu();
            option = scanner.nextInt(); //Take in user input
            scanner.nextLine(); // Consume newline
            
            //If user enters 1, call processText and pass I/O
            if (option == 1) {
                textProcessor.processText(inputFile, outputFile);
                System.out.println("Word replacement completed. Check the output file.");
              //If user enters 2, call contains on user input word
            } else if (option == 2) {
                System.out.println("Please enter the word you want to search for in embeddings:");
                String searchWord = scanner.nextLine();
                if (gloveEmbeddings.containsWord(searchWord)) {
                    System.out.println(searchWord + " was found in embeddings");
                } else {
                    System.out.println(searchWord + " was not found in embeddings");
                }
            }
            //Same thing if they want to search for word in google-1000
            else if (option==3) {
            	System.out.println("Please enter the word you want to search for in google 1000");
            	String searchWord = scanner.nextLine();
            	
            	if(googleEmbeddings.containsWord(searchWord)) {
            		System.out.println(searchWord+" was found in google-1000");
            	}
            	else {
            		System.out.println(searchWord+" was not found in google-1000");
            	}
            	
            }
            else if(option==4) {
                System.out.println("Google-1000 contains "+googleEmbeddings.getSize()+" embeddings");
            }
            else if(option==5) {
                System.out.println("GloVe Embeddings contains "+gloveEmbeddings.getSize()+" embeddings");
            }
        }
        System.out.println("Thanks for using my program!!");
    }

    public static void printProgress(int index, int total) {
        if (index > total) return;
        int size = 50;
        char done = '█';
        char todo = '░';
        int complete = (100 * index) / total;
        int completeLen = size * complete / 100;

        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < size; i++) {
            sb.append((i < completeLen) ? done : todo);
        }

        System.out.print("\r" + sb + "] " + complete + "%");

        if (index == total) System.out.print("\n");
    }
}