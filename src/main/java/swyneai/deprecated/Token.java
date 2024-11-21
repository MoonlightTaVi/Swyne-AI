package swyneai.deprecated;

import java.util.*;

public class Token {
    private final String name;
    private Vector vector;
    private Map<String, Set<Token>> groups = new HashMap<>();

    public Token(String name, int modelSize, int id) {
        this.name = name;
        this.vector = new Vector(modelSize);
        this.vector.setCoord(id, 1);
    }
    public synchronized Vector getVector() {
        return vector;
    }
    public void setVector(Vector vector) {
        this.vector = vector;
    }
    public void addGroup(String group, Token... tokens) {
        Set<Token> set = groups.getOrDefault(group, new HashSet<>());
        set.addAll(List.of(tokens));
        groups.put(group, set);
    }
    public Map<String, Set<Token>> getGroups() {
        return groups;
    }
    public String getName() {
        return name;
    }
}
