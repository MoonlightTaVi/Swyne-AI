package swyneai;

import java.util.*;
import java.util.stream.Collectors;

public class Token {
    private int occurrences = 0;
    private final String name;
    private final Map<String, Map<Integer, Double>> relevant = new HashMap<>();

    private int recursionDepth = 1;
    private double degradationRate = 0.9;
    private String regex = "[а-яА-Я]+";
    private final Model model;

    public Token(String name, Model model) {
        this.name = name;
        this.model = model;
    }

    public void increase() {
        occurrences++;
    }

    public void connect(Token another, int direction) {
        Map<Integer, Double> pair = relevant.getOrDefault(another.getName(), new HashMap<>());
        pair.put(direction, pair.getOrDefault(direction, 0.0) + 1);
        relevant.put(another.getName(), pair);
    }

    public double simpleSimilarity(String toToken) {
        List<Double> cases = new ArrayList<>();
        for (Map.Entry<Integer, Double> directionPair : relevant.getOrDefault(toToken, new HashMap<>()).entrySet()) {
            int direction = directionPair.getKey();
            double currentSum = 0.0;
            double currentCount = 0.0;
            for (Map.Entry<String, Map<Integer, Double>> entry : relevant.entrySet()) {
                for (Map.Entry<Integer, Double> compareToPair : relevant.get(entry.getKey()).entrySet()) {
                    if (direction != compareToPair.getKey()) {
                        continue;
                    }
                    if (toToken.equals(entry.getKey())) {
                        currentSum = compareToPair.getValue();
                    }
                    if (compareToPair.getValue() > currentCount) {
                        currentCount = compareToPair.getValue();
                    }
                }
            }
            cases.add(currentSum / currentCount * Math.pow(degradationRate, Math.abs(direction) - 1));
        }

        if (cases.isEmpty()) {
            return 0.0;
        }

        double sum = 0.0;
        for (double _case : cases) {
            sum += _case;
        }
        return sum / cases.size();
    }

    public double recursiveSimilarity(String toToken, int step, Set<String> ignore) {
        Map<String, Map<Integer, Double>> theirRelations = model.getToken(toToken).getRelevant();
        List<Double> allCases = new ArrayList<>(List.of(simpleSimilarity(model.getToken(toToken).getName())));
        ignore.add(name);
        ignore.add(toToken);
        for (String theirToken : theirRelations.keySet()) {
            if (ignore.contains(theirToken)) {
                continue;
            }
            allCases.add(simpleSimilarity(theirToken));
            ignore.add(theirToken);
            if (step < recursionDepth) {
                allCases.add(model.getToken(theirToken).recursiveSimilarity(name, step+1, ignore));
            }
        }
        double sum = 0.0;
        for (double percent : allCases) {
            sum += percent;
        }
        return sum / allCases.size();
    }

    public List<String> getMostRelevant(double percentage) {
        Map<String, Double> token2similarity = new HashMap<>();
        Set<String> ignore = new HashSet<>();
        for (String token : relevant.keySet()) {
            if (!regex.isEmpty() && !token.matches(regex)) {
                continue;
            }
            token2similarity.put(token, recursiveSimilarity(token, 0, ignore));
        }
        if (token2similarity.isEmpty()) {
            return List.of("[none]");
        }

        double max = Collections.max(token2similarity.entrySet(), Map.Entry.comparingByValue()).getValue();

        List<String> result = new ArrayList<>();
        for (Map.Entry<String, Double> pair : token2similarity.entrySet()) {
            if (pair.getValue() > max * percentage) {
                result.add(pair.getKey());
            }
        }
        return result;
    }

    public String getName() {
        return name;
    }

    public int getOccurrences() {
        return occurrences;
    }

    public int getRecursionDepth() {
        return recursionDepth;
    }

    public void setRecursionDepth(int recursionDepth) {
        this.recursionDepth = recursionDepth;
    }

    public double getDegradationRate() {
        return degradationRate;
    }

    public void setDegradationRate(double degradationRate) {
        this.degradationRate = degradationRate;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public Map<String, Map<Integer, Double>> getRelevant() {
        return relevant;
    }
}
