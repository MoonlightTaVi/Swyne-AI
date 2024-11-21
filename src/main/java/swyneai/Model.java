package swyneai;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class Model {
    private final Map<String, Token> model = new HashMap<>();

    public static void main(String[] args) {
        String regex = "\\w+";
        Stream.of("1. Eat-yee bla, sleep, rave, repeat... Right?!".split("\\b")).filter(w -> w.matches(regex)).toList().forEach(t -> System.out.printf("\"%s\"%n", t));
    }

    public void feed(List<String> feed) {
        for (String paragraph : feed) {
            for (String sentence : paragraph.split("(?<=[.!?;])\\s+")) {
                String[] tokens = sentence.split("\\b");
                for (int i = 0; i < tokens.length; i++) {
                    String iWord = tokens[i].toLowerCase();
                    Token itoken = model.getOrDefault(iWord, new Token(iWord, this));
                    itoken.increase();
                    for (int j = 0; j < tokens.length; j++) {
                        if (i == j) {
                            continue;
                        }
                        String jWord = tokens[j].toLowerCase();
                        Token jtoken = model.getOrDefault(jWord, new Token(jWord, this));
                        jtoken.increase();
                        itoken.connect(jtoken, i-j);
                        jtoken.connect(itoken, j-i);
                        model.put(jWord, jtoken);
                    }
                    model.put(iWord, itoken);
                }
            }
        }
    }

    public Token getToken(String name) {
        return model.getOrDefault(name.toLowerCase(), new Token(name.toLowerCase(), this));
    }
}
