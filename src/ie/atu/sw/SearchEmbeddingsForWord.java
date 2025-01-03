package ie.atu.sw;

public class SearchEmbeddingsForWord {
    public static boolean search(String searchWord, WordEmbeddings embeddings) {
        return embeddings.containsWord(searchWord);
    }
}