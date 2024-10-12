package swyneai;

import main.Main;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import static swyneai.Tools.adjustVector;

public class Model {
    private Map<String, Integer> occurrences = new HashMap<>();
    private Map<String, Token> tokens = new HashMap<>();
    private final Set<String> stopWords = new HashSet<>();
    private final List<String[]> sentences = new ArrayList<>();
    private int stopWordsCount = 0;
    private int sentenceCount = 0;
    private int wordCount = 0;
    private int size;
    private final int minOccurrences;
    public Model(int size, int minOccurrences) {
        this.size = size;
        this.minOccurrences = minOccurrences;
    }
    public void loadStopWords() {
        try (Scanner scanner = new Scanner(new File("src/main/resources/stopwords-ru.txt"))) {
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                stopWords.add(line);
            }
            Main.markTime(String.format("Loaded %d stop-words.", stopWords.size()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void feed(List<String> feed) {
        Main.markTime("Started feeding...");
        int i = 0;
        for (String p : feed) {
            String[] sentences = p.split("(?<=[.!?;])\\s+");
            sentenceCount += sentences.length;
            for (String sentence : sentences) {
                String regex = "\\b[а-яА-Я]+\\b";
                Pattern pattern = Pattern.compile(regex);
                Matcher m = pattern.matcher(sentence);
                List<String> words = new ArrayList<>();
                while (m.find()) {
                    String word = m.group(0).toLowerCase();
                    words.add(word);
                    wordCount++;
                    if (stopWords.contains(word)) {
                        stopWordsCount++;
                        continue;
                    }
                    if (occurrences.containsKey(word)) {
                        int prev = occurrences.get(word);
                        occurrences.put(word, ++prev);
                    } else {
                        occurrences.put(word, 1);
                    }
                    if (occurrences.size() >= this.size) {
                        Main.markTime(String.format("Reached maximum model size (%d) at %d paragraph!", this.size, i));
                        break;
                    }
                }
                if (occurrences.size() >= this.size) {
                    break;
                }
                this.sentences.add(words.toArray(new String[0]));
            }
            if (occurrences.size() >= this.size) {
                break;
            }
            i++;
        }
        Main.markTime("Finished feeding!");
        System.out.printf("Unique words: %d.%nStop words: %d.%nAll words: %d.%nSentences: %d.%nParagraphs: %d.%n", occurrences.size(), stopWordsCount, wordCount, sentenceCount, feed.size());
    }
    public void filterByOccurrences() {
        occurrences = occurrences.entrySet().stream()
                .filter(e -> e.getValue() >= minOccurrences)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        Main.markTime(String.format("After filtration by min=%d, unique words: %d.", minOccurrences, occurrences.size()));
        size = occurrences.size();
    }
    public Map<String, Integer> getOccurrences() {
        return occurrences;
    }
    public void setUpTokens() {
        Main.markTime("Setting up tokens...");
        int i = 0;
        for (String word : occurrences.keySet()) {
            Token t = new Token(word, size, i);
            tokens.put(word, t);
            i++;
        }
    }
    public Map<String, Token> getTokens() {
        return tokens;
    }
    public void train(int window, double adjustRate) {
        Main.markTime("Training...");
        for (String[] sentence : sentences) {
            for (int i = 0; i < sentence.length - window; i++) {
                if (!tokens.containsKey(sentence[i])) {
                    continue;
                }
                Vector target = tokens.get(sentence[i]).getVector();
                for (int j = i + 1; j < Math.min(i + window + 1, sentence.length); j++) {
                    if (!tokens.containsKey(sentence[j])) {
                        continue;
                    }
                    Vector original = tokens.get(sentence[j]).getVector();
                    Vector newVector = adjustVector(original, target, 1 + adjustRate);
                    tokens.get(sentence[j]).setVector(newVector);
                }
            }
        }
    }
}
