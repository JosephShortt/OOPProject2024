package ie.atu.sw;

import java.util.Scanner;

public class Runner {
    public static void main(String[] args) throws Exception {
        String embeddingsFile = "";
        String google1000File = "";
        String outputFile = "";
        String inputFile = "";
        int option = 0;

        Scanner scanner = new Scanner(System.in);
        
        // Create implementations with dependency injection
        WordEmbeddings gloveEmbeddings = new GloVeEmbeddingsMap();
        WordEmbeddings googleEmbeddings = new GoogleEmbeddingsMap(gloveEmbeddings);
        TextProcessor textProcessor = new ConcurrentTextProcessor(gloveEmbeddings, googleEmbeddings);
        Menu menu = new Menu();

        System.out.println("Please enter the path and name for glove embeddings file:");
        embeddingsFile = scanner.nextLine();

        System.out.println("Please enter the path and name of the google1000 file:");
        google1000File = scanner.nextLine();

        System.out.println("Please enter the path and name of the text file to simplify:");
        inputFile = scanner.nextLine();

        System.out.println("Please enter the path and name of the output file:");
        String tempOutput = scanner.nextLine();
        if (!tempOutput.isEmpty()) {
            outputFile = tempOutput;
        }

        // Load embeddings
        gloveEmbeddings.load(embeddingsFile);
        System.out.println("GloVe embeddings loaded successfully");

        googleEmbeddings.load(google1000File);
        System.out.println("Google 1000 words loaded successfully");

        while (option != -1) {
            menu.loadMenu();
            option = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            if (option == 1) {
                textProcessor.processText(inputFile, outputFile);
                System.out.println("Word replacement completed. Check the output file.");
            } else if (option == 2) {
                System.out.println("Please enter the word you want to search for in embeddings:");
                String searchWord = scanner.nextLine();
                if (gloveEmbeddings.containsWord(searchWord)) {
                    System.out.println(searchWord + " was found in embeddings");
                } else {
                    System.out.println(searchWord + " was not found in embeddings");
                }
            }
        }
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