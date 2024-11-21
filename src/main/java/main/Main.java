package main;

import swyneai.Model;
import swyneai.Token;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import static fbreader.Tools.detectCharset;
import static fbreader.Tools.readText;
import static fbreader.Tools.readToList;

public class Main {
    private static long lastStamp = System.currentTimeMillis();
    public static void main(String[] args) {
        String path = "src/main/resources/feed/second_variety_ru.fb2";
        markTime("Detecting charset... (start)");
        String charset = detectCharset(path);
        markTime("Started reading file...");
        String text = readText(path, charset);
        int count = 10000;
        markTime(String.format("Finished reading file. Reading %d part(s)...", count));
        Model model = new Model();
        model.feed(readToList(text, count));
        markTime("Finished feeding.");
        String sampleWord = "хендрикс";
        Token sampleToken = model.getToken(sampleWord);
        double threshold = 0.4;
        System.out.printf("The most relevant tokens for %s (filtering by %s%%) are:%n", sampleWord, threshold);
        sampleToken.getMostRelevant(threshold).forEach(word -> System.out.printf("\"%s\" : %s%%%n", word, sampleToken.simpleSimilarity(word)));

        /*Model model = new Model(5000);
        model.loadStopWords();
        model.feed(readToList(text, count));
        model.filterByOccurrences(2);
        model.setUpTokens();
        model.train(3, 1);
        String mostOften = Collections.max(model.getOccurrences().entrySet(), Map.Entry.comparingByValue()).getKey();
        System.out.printf("The word, which occurs the most often: \"%s\" (%d times).%n", mostOften, model.getOccurrences().get(mostOften));
        model.makeGroups(0.6);
        Map<String, Set<Token>> groups = model.getTokens().get(mostOften).getGroups();
        System.out.println("The groups for it:");
        groups.forEach((key, value) -> System.out.printf("%s: %s%n", key, String.join(", ", value.stream().map(Token::getName).toList())));
        Vector mostOftenVector = model.getTokens().get(mostOften).getVector();
        Set<Token> relatedTokens = new HashSet<>(model.getTokens().entrySet()
                .stream().filter(e -> !e.getKey().equals(mostOften))
                .filter(e -> e.getValue().getVector().similarity(mostOftenVector) > 0.6)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                .values());
        System.out.println("The most related words:");
        for (Token t : relatedTokens) {
            System.out.printf("\"%s\": %s%% (mentioned %d times).%n", t.getName(), t.getVector().similarity(mostOftenVector), model.getOccurrences().get(t.getName()));
        }*/
        markTime("Finished.");
    }
    public static void markTime(String title) {
        long timePassed = System.currentTimeMillis() - lastStamp;
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        lastStamp = System.currentTimeMillis();
        System.out.printf("%s (%s seconds): \"%s\"%n", ts, ((float)timePassed / 1000), title);
    }
}
