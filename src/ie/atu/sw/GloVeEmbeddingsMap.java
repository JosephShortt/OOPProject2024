package ie.atu.sw;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of the WordEmbeddings interface that handles GloVe (Global
 * Vectors for Word Representation) embeddings. This class provides
 * functionality to load, store, and retrieve word embeddings from a file using
 * a thread-safe concurrent hash map implementation.
 * 
 * The class handles 50-dimensional word vectors stored in CSV format, where
 * each line contains: - First column: The word - Following 50 columns: Vector
 * components
 * 
 * @version 1.0.0
 * @author [Joseph Shortt]
 */

public class GloVeEmbeddingsMap implements WordEmbeddings {

	/**
	 * Thread-safe map storing word embeddings where keys are words and values are
	 * their vector representations.
	 */
	private final Map<String, double[]> wordMap = new ConcurrentHashMap<>();

	/**
	 * Loads word embeddings from a specified file path. Reads the file line by
	 * line, parsing each line into a word and its corresponding embedding vector.
	 * 
	 * Time Complexity: O(n) where n is the number of lines in the embeddings file
	 *
	 * @param filePath path to the embeddings file
	 * @throws Exception if the file cannot be read or contains invalid data
	 */

	@Override
	public void load(String filePath) throws Exception {
		int totalLines = countLines(filePath);
		try (var br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)))) {
			parseEmbeddingsFile(br, totalLines);
		}
	}

	/**
	 * Retrieves the embedding vector for a given word.
	 * 
	 * Time Complexity: O(1) average case due to hash table lookup
	 *
	 * @param word the word to look up
	 * @return the embedding vector for the word, or null if the word is not found
	 */
	
	@Override
	public double[] getEmbedding(String word) {
		return wordMap.get(word);
	}

	/**
	 * Checks if a word exists in the embeddings.
	 * 
	 * Time Complexity: O(1) average case using hash table lookup
	 *
	 * @param word the word to check
	 * @return true if the word exists in the embeddings, false otherwise
	 */
	
	@Override
	public boolean containsWord(String word) {
		return wordMap.containsKey(word);
	}

	/**
	 * Parses the embeddings file line by line, converting each line into a word and
	 * its vector representation. Updates the progress bar during processing.
	 * 
	 * Time Complexity: O(n) where n is the number of lines in the file
	 *
	 * @param br         BufferedReader for reading the file
	 * @param totalLines total number of lines in the file for progress tracking
	 * @throws Exception if there's an error parsing the file
	 */

	private void parseEmbeddingsFile(BufferedReader br, int totalLines) throws Exception {
		int currentLine = 0;
		String next;
		while ((next = br.readLine()) != null) {
			String[] parts = splitLine(next);
			String word = parts[0];
			double[] values = parseEmbedding(parts);
			wordMap.put(word, values);
			Runner.printProgress(++currentLine, totalLines);
		}
	}

	/**
	 * Splits a line from the embeddings file into components. Expects each line to
	 * have exactly 51 comma-separated values (word + 50 vector components).
	 * 
	 * Time Complexity: O(1) as it performs a fixed split operation
	 *
	 * @param line the line to split
	 * @return array of strings containing the word and vector components
	 * @throws Exception if the line format is invalid
	 */
	
	private String[] splitLine(String line) throws Exception {
		String[] parts = line.split(",", 51);
		if (parts.length < 51) {
			throw new Exception("[ERROR] Invalid line format: " + line);
		}
		return parts;
	}

	/**
     * Converts the string array of vector components into a double array.
     * 
     * Time Complexity: O(n) where n is the length of the array (fixed at 50)
     *
     * @param parts array containing the word and vector components
     * @return double array of the vector components
     */
	
	private double[] parseEmbedding(String[] parts) {
		double[] values = new double[50];
		for (int i = 1; i < parts.length; i++) {
			values[i - 1] = Double.parseDouble(parts[i]);
		}
		return values;
	}

	/**
     * Counts the total number of lines in the embeddings file.
     * 
     * Time Complexity: O(n) where n is the number of lines in the file
     *
     * @param filePath path to the embeddings file
     * @return total number of lines in the file
     * @throws Exception if there's an error reading the file
     */
	private int countLines(String filePath) throws Exception {
		int count = 0;
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)))) {
			while (br.readLine() != null) {
				count++;
			}
		}
		return count;
	}

    /**
     * Returns the total number of words in the embeddings map.
     * 
     * Time Complexity: O(1) as it uses internal map counter
     *
     * @return number of words in the embeddings map
     */
	
	@Override
	public int getSize() {
		return wordMap.size();
	}
}