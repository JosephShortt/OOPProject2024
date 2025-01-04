package ie.atu.sw;

/**
 * Interface for text processing operations.
 *
 * @version 1.0.0
 * @author [Joseph Shortt]
 */

public interface TextProcessor {
	
	 /**
     * Processes text from input file and writes results to output file.
     *
     * @param inputPath source file path
     * @param outputPath destination file path
     * @throws Exception if processing fails
     */
	
    void processText(String inputPath, String outputPath) throws Exception;
}
