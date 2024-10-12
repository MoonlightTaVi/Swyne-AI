package main;

import swyneai.Model;
import swyneai.Token;
import swyneai.Tools.*;
import swyneai.Vector;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        Model model = new Model(5000, 2);
        model.loadStopWords();
        model.feed(readToList(text, count));
        model.filterByOccurrences();
        model.setUpTokens();
        model.train(1, 0.5);
        String mostOften = Collections.max(model.getOccurrences().entrySet(), Map.Entry.comparingByValue()).getKey();
        System.out.printf("The word, which occurs the most often: \"%s\" (%d times).%n", mostOften, model.getOccurrences().get(mostOften));
        Vector mostOftenVector = model.getTokens().get(mostOften).getVector();
        Set<Token> relatedTokens = new HashSet<>(model.getTokens().entrySet()
                .stream().filter(e -> !e.getKey().equals(mostOften))
                .filter(e -> e.getValue().getVector().similarity(mostOftenVector) > 0.4)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                .values());
        System.out.println("The most related words:");
        for (Token t : relatedTokens) {
            System.out.printf("\"%s\": %s%% (mentioned %d times).%n", t.getName(), t.getVector().similarity(mostOftenVector), model.getOccurrences().get(t.getName()));
        }
        markTime("Finished.");
    }
    public static void markTime(String title) {
        long timePassed = System.currentTimeMillis() - lastStamp;
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        lastStamp = System.currentTimeMillis();
        System.out.printf("%s (%s seconds): \"%s\"%n", ts, ((float)timePassed / 1000), title);
    }
}
