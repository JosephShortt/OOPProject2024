package ie.atu.sw;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of the WordEmbeddings interface that manages Google's word
 * embeddings. This class maintains a thread-safe map of words and their
 * corresponding embeddings, utilizing GloVe embeddings as the source for vector
 * representations.
 * 
 * The class implements loose coupling through dependency injection of the GloVe
 * embeddings and provides thread-safe operations using ConcurrentHashMap.
 * 
 * @version 1.0.0
 * @author [Joseph Shortt]
 */

public class GoogleEmbeddingsMap implements WordEmbeddings {
	/**
	 * Thread-safe map storing word embeddings where keys are words and values are
	 * their vector representations
	 */
	private final Map<String, double[]> wordMap = new ConcurrentHashMap<>();

	/** Reference to the GloVe embeddings implementation */
	private final WordEmbeddings gloveEmbeddings;

	
	 /**
     * Constructs a new GoogleEmbeddingsMap with a reference to GloVe embeddings.
     * Implements loose coupling through dependency injection.
     * 
     * @param gloveEmbeddings the GloVe embeddings implementation to use for vector lookup
     */
	
	public GoogleEmbeddingsMap(WordEmbeddings gloveEmbeddings) {
		this.gloveEmbeddings = gloveEmbeddings;
	}

	 
	  /**
     * Loads words from a file and retrieves their corresponding embeddings from GloVe.
     * Only words that exist in GloVe embeddings are stored in the map.
     * 
     * Time Complexity: O(n) where n is the number of words in the input file
     * 
     * @param filePath path to the file containing Google words
     * @throws Exception if there's an error reading the file
     */
	@Override
	public void load(String filePath) throws Exception {
		try (var br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)))) {
			String word;
			while ((word = br.readLine()) != null) {
				double[] embedding = gloveEmbeddings.getEmbedding(word);
				if (embedding != null) {
					wordMap.put(word, embedding);
				}
			}
		}
	}

	 /**
     * Retrieves the embedding vector for a given word.
     * 
     * Time Complexity: O(1) average case using hash table lookup
     * 
     * @param word the word to look up
     * @return the embedding vector for the word, or null if not found
     */
	@Override
	public double[] getEmbedding(String word) {
		return wordMap.get(word);
	}

	/**
     * Checks if a word exists in the Google embeddings.
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
     * Returns the internal word map. Package-private method for optimization purposes.
     * 
     * Time Complexity: O(1) constant time operation
     * 
     * @return the concurrent hash map containing word embeddings
     */
	Map<String, double[]> getWordMap() {
		return wordMap;
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