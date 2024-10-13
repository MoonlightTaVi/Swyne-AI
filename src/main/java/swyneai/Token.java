package swyneai;

public class Token {
    private final String name;
    private Vector vector;
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
    public String getName() {
        return name;
    }
}
