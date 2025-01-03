package ie.atu.sw;
//WordEmbeddings Interface for glove and google embedding's
public interface WordEmbeddings {

	void load(String filePath) throws Exception;

	double[] getEmbedding(String word);

	boolean containsWord(String word);
	
	int getSize();
}
