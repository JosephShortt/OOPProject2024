package ie.atu.sw;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GloVeEmbeddingsMap implements WordEmbeddings{
	  private final Map<String, double[]> wordMap = new ConcurrentHashMap<>();

	    @Override
	    public void load(String filePath) throws Exception {
	        int totalLines = countLines(filePath);
	        try (var br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)))) {
	            parseEmbeddingsFile(br, totalLines);
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

	    private String[] splitLine(String line) throws Exception {
	        String[] parts = line.split(",", 51);
	        if (parts.length < 51) {
	            throw new Exception("[ERROR] Invalid line format: " + line);
	        }
	        return parts;
	    }

	    private double[] parseEmbedding(String[] parts) {
	        double[] values = new double[50];
	        for (int i = 1; i < parts.length; i++) {
	            values[i - 1] = Double.parseDouble(parts[i]);
	        }
	        return values;
	    }

	    private int countLines(String filePath) throws Exception {
	        int count = 0;
	        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)))) {
	            while (br.readLine() != null) {
	                count++;
	            }
	        }
	        return count;
	    }
	}