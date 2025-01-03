package ie.atu.sw;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.ArrayList;
import java.io.*;

public class ConcurrentTextProcessor implements TextProcessor {
    private final WordEmbeddings gloveEmbeddings;
    private final WordEmbeddings googleEmbeddings;
    private static final int BATCH_SIZE = 1000; // Adjust based on your needs

    public ConcurrentTextProcessor(WordEmbeddings gloveEmbeddings, WordEmbeddings googleEmbeddings) {
        this.gloveEmbeddings = gloveEmbeddings;
        this.googleEmbeddings = googleEmbeddings;
    }

    @Override
    public void processText(String inputPath, String outputPath) throws Exception {
        List<String> words = readAndTokenize(inputPath);
        List<String> processedWords = processConcurrently(words);
        writeResults(outputPath, processedWords);
    }

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

    private List<List<String>> createBatches(List<String> items, int batchSize) {
        List<List<String>> batches = new ArrayList<>();
        for (int i = 0; i < items.size(); i += batchSize) {
            batches.add(new ArrayList<>(
                items.subList(i, Math.min(items.size(), i + batchSize))
            ));
        }
        return batches;
    }

    private List<String> processBatch(List<String> batch) {
        return batch.stream()
            .map(this::processWord)
            .toList();
    }

    private String processWord(String word) {
        if (googleEmbeddings.containsWord(word)) {
            return word;
        }

        double[] wordVector = gloveEmbeddings.getEmbedding(word);
        if (wordVector == null) {
            return word;
        }

        return findMostSimilarWord(wordVector);
    }

    private String findMostSimilarWord(double[] wordVector) {
        String bestMatch = "";
        double highestSimilarity = -1;

        // This could be further optimized by pre-computing norms
        for (var entry : ((GoogleEmbeddingsMap)googleEmbeddings).getWordMap().entrySet()) {
            double similarity = cosineSimilarity(wordVector, entry.getValue());
            if (similarity > highestSimilarity) {
                highestSimilarity = similarity;
                bestMatch = entry.getKey();							
            }																										
        }

        return bestMatch;
    }

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

    private void writeResults(String outputPath, List<String> words) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
            writer.write(String.join(" ", words));
        }
    }
}