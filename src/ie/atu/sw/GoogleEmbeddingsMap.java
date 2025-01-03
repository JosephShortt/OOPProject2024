package ie.atu.sw;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GoogleEmbeddingsMap implements WordEmbeddings{
	 private final Map<String, double[]> wordMap = new ConcurrentHashMap<>();
	    private final WordEmbeddings gloveEmbeddings;

	    public GoogleEmbeddingsMap(WordEmbeddings gloveEmbeddings) {
	        this.gloveEmbeddings = gloveEmbeddings;
	    }

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

	    @Override
	    public double[] getEmbedding(String word) {
	        return wordMap.get(word);
	    }

	    @Override
	    public boolean containsWord(String word) {
	        return wordMap.containsKey(word);
	    }

	    // Package-private method for optimization purposes
	    Map<String, double[]> getWordMap() {
	        return wordMap;
	    }
	}