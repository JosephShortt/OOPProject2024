package ie.atu.sw;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.ArrayList;
import java.io.*;

/**
 * Implements TextProcessor interface for concurrent text processing using word
 * embeddings. Processes text by comparing words against Google-1000 and finding
 * similar words using GloVe embeddings when necessary.
 *
 * @version 1.0.0
 * @author [Joseph Shortt]
 */

public class ConcurrentTextProcessor implements TextProcessor {
	/** Reference to GloVe embeddings */
	private final WordEmbeddings gloveEmbeddings;
	/** Reference to Google embeddings */
	private final WordEmbeddings googleEmbeddings;
	/** Batch size for concurrent processing */
	private static final int BATCH_SIZE = 1000;

	/**
	 * Constructs processor with GloVe and Google embeddings.
	 *
	 * @param gloveEmbeddings  GloVe word embeddings
	 * @param googleEmbeddings Google word embeddings
	 *
	 */
	public ConcurrentTextProcessor(WordEmbeddings gloveEmbeddings, WordEmbeddings googleEmbeddings) {
		this.gloveEmbeddings = gloveEmbeddings;
		this.googleEmbeddings = googleEmbeddings;
	}

	/**
	 * Processes input text file and writes simplified text to output. Time
	 * Complexity: O(n)
	 *
	 * @param inputPath  source file path
	 * @param outputPath destination file path
	 * @throws Exception if processing fails
	 *
	 */
	@Override
	public void processText(String inputPath, String outputPath) throws Exception {
		List<String> words = readAndTokenize(inputPath);
		List<String> processedWords = processConcurrently(words);
		writeResults(outputPath, processedWords);
	}

	/**
	 * Reads and tokenizes input file into words. Time Complexity: O(n)
	 *
	 * @param filePath input file path
	 * @return list of words
	 * @throws IOException if file reading fails
	 */

	private List<String> readAndTokenize(String filePath) throws IOException {
		List<String> words = new ArrayList<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			String line;
			while ((line = reader.readLine()) != null) {
				String[] lineWords = line.split("\\s+");
				for (String word : lineWords) {
					if (!word.trim().isEmpty()) {
						words.add(word.trim());
					}
				}
			}
		}
		return words;
	}

	/**
	 * Processes words concurrently in batches. Time Complexity: O(n)
	 *
	 * @param words list of words to process
	 * @return processed words
	 * @throws Exception if processing fails
	 */

	private List<String> processConcurrently(List<String> words) throws Exception {
		List<String> results = new ArrayList<>(words.size());
		List<List<String>> batches = createBatches(words, BATCH_SIZE);

		try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
			List<Future<List<String>>> futures = new ArrayList<>();

			for (List<String> batch : batches) {
				futures.add(executor.submit(() -> processBatch(batch)));
			}

			for (Future<List<String>> future : futures) {
				results.addAll(future.get());
			}
		}

		return results;
	}

	/**
     * Creates batches of words for concurrent processing.
     * Time Complexity: O(n)
     *
     * @param items items to batch
     * @param batchSize size of each batch
     * @return list of batches
     */
	
	private List<List<String>> createBatches(List<String> items, int batchSize) {
		List<List<String>> batches = new ArrayList<>();
		for (int i = 0; i < items.size(); i += batchSize) {
			batches.add(new ArrayList<>(items.subList(i, Math.min(items.size(), i + batchSize))));
		}
		return batches;
	}

	/**
     * Processes a batch of words using stream operations.
     * Time Complexity: O(n)
     *
     * @param batch batch of words
     * @return processed words
     */
	
	private List<String> processBatch(List<String> batch) {
		return batch.stream().map(this::processWord).toList();
	}

	/**
     * Processes individual word using embeddings.
     * Time Complexity: O(n)
     *
     * @param word word to process
     * @return processed word
     */	private String processWord(String word) {
		if (googleEmbeddings.containsWord(word)) {
			return word;
		}

		double[] wordVector = gloveEmbeddings.getEmbedding(word);
		if (wordVector == null) {
			return word;
		}

		return findMostSimilarWord(wordVector);
	}

     /**
      * Finds most similar word in Google embeddings.
      * Time Complexity: O(n)
      *
      * @param wordVector vector to compare
      * @return most similar word
      */
     
	private String findMostSimilarWord(double[] wordVector) {
		String bestMatch = "";
		double highestSimilarity = -1;

		// This could be further optimized by pre-computing norms
		for (var entry : ((GoogleEmbeddingsMap) googleEmbeddings).getWordMap().entrySet()) {
			double similarity = cosineSimilarity(wordVector, entry.getValue());
			if (similarity > highestSimilarity) {
				highestSimilarity = similarity;
				bestMatch = entry.getKey();
			}
		}

		return bestMatch;
	}

	/**
     * Calculates cosine similarity between vectors.
     * Time Complexity: O(n)
     * I used AI in assisting with calculating the cosine of the two input vectors
     * as I was add the square roots of the norms instead of multiplying
     *
     * @param v1 first vector
     * @param v2 second vector
     * @return similarity score
     */
	private double cosineSimilarity(double[] v1, double[] v2) {
		double dotProduct = 0.0;
		double norm1 = 0.0;
		double norm2 = 0.0;

		for (int i = 0; i < v1.length; i++) {
			dotProduct += v1[i] * v2[i];
			norm1 += Math.pow(v1[i], 2);
			norm2 += Math.pow(v2[i], 2);
		}

		return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
	}

	/**
     * Writes processed words to output file.
     * Time Complexity: O(n)
     *
     * @param outputPath output file path
     * @param words processed words
     * @throws IOException if writing fails
     */
	
	private void writeResults(String outputPath, List<String> words) throws IOException {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
			writer.write(String.join(" ", words));
		}
	}
}