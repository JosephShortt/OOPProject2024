package ie.atu.sw;

public interface WordEmbeddings {

	void load(String filePath) throws Exception;

	double[] getEmbedding(String word);

	boolean containsWord(String word);
}
