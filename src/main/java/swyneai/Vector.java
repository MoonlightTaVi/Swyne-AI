package swyneai;

import java.util.Arrays;

public class Vector {
    private final double[] coordinates;
    public Vector(int size) {
        coordinates = new double[size];
    }
    public Vector(double ... coordinates) {
        this.coordinates = coordinates;
    }
    public Vector(Vector from) {
        this.coordinates = from.getCoordinates();
    }
    public double[] getCoordinates() {
        return coordinates;
    }
    public int size() {
        return coordinates.length;
    }
    public double length() {
        double ret = 0;
        for (double item : coordinates) {
            ret += Math.pow(item, 2);
        }
        return Math.sqrt(ret);
    }
    public void setCoord(int id, double value) {
        if (id < 0 || id >= size()) {
            return;
        }
        coordinates[id] = value;
    }
    public double getCoord(int id) {
        if (id < 0 || id >= size()) {
            return 0;
        }
        return coordinates[id];
    }
    public double multiScalar(Vector target) {
        if (this.size() != target.size()) {
            return 0;
        }
        double ret = 0;
        for (int i = 0; i < this.size(); i++) {
            ret += this.getCoord(i) * target.getCoord(i);
        }
        return ret;
    }
    public double similarity(Vector target) {
        if (this.size() != target.size()) {
            return 0;
        }
        if (this.length() == 0 || target.length() == 0) {
            return 0;
        }
        return this.multiScalar(target) / (this.length() * target.length());
    }
    public synchronized Vector add(Vector target) {
        if (this.size() != target.size()) {
            return null;
        }
        Vector ret = new Vector(size());
        for (int i = 0; i < this.size(); i++) {
            ret.setCoord(i, this.getCoord(i) + target.getCoord(i));
        }
        return ret;
    }
    public synchronized Vector normalized() {
        if (length() == 0) {
            return null;
        }
        Vector ret = new Vector(size());
        double length = length();
        for (int i = 0; i < size(); i++) {
            ret.setCoord(i, getCoord(i) / length);
        }
        return ret;
    }

    @Override
    public String toString() {
        return String.format("Vector(%s)", String.join(";", Arrays.stream(coordinates).mapToObj(Double::toString).toList()));
    }
}
