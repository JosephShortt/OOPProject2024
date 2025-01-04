package ie.atu.sw;

/**
 * Interface defining operations for word embedding implementations. Provides
 * methods to load, retrieve, and query word vectors.
 *
 * @version 1.0.0
 * @author [Joseph Shortt]
 */

public interface WordEmbeddings {

	/**
	 * Loads word embeddings from a file.
	 *
	 * @param filePath path to embeddings file
	 * @throws Exception if loading fails
	 */

	void load(String filePath) throws Exception;

	/**
	 * Retrieves vector representation for a word.
	 *
	 * @param word target word
	 * @return vector representation or null if not found
	 */

	double[] getEmbedding(String word);

	/**
	 * Checks if word exists in embeddings.
	 *
	 * @param word word to check
	 * @return true if word exists, false otherwise
	 */

	boolean containsWord(String word);

	/**
	 * Gets total number of embeddings.
	 *
	 * @return count of embedded words
	 */

	int getSize();
}
