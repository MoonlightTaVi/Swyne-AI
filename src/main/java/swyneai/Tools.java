package swyneai;

public class Tools {
    private static int adjustIterations = 1000;
    private static double minAdjustDifference = 1e-5;
    private static double adjustExtent = 0.01;

    public static int getAdjustIterations() {
        return adjustIterations;
    }
    public static void setAdjustIterations(int adjustIterations) {
        Tools.adjustIterations = adjustIterations;
    }
    public static double getAdjustExtent() {
        return adjustExtent;
    }
    public static void setAdjustExtent(double adjustExtent) {
        Tools.adjustExtent = adjustExtent;
    }
    public static double getMinAdjustDifference() {
        return minAdjustDifference;
    }
    public static void setMinAdjustDifference(double minAdjustDifference) {
        Tools.minAdjustDifference = minAdjustDifference;
    }

    public static Vector adjustVector(Vector original, Vector target, double modifier) {
        if (original.size() != target.size()) {
            return null;
        }
        if (original.length() == 0 || target.length() == 0) {
            return new Vector(original);
        }
        double originalSimilarity = original.similarity(target);
        double newSimilarity = originalSimilarity * modifier;
        if (originalSimilarity == 0) {
            newSimilarity += (modifier - 1);
        }
        newSimilarity = Math.max(Math.min(newSimilarity, 1), -1);
        Vector ret = new Vector(original);
        for (int i = 0; i < adjustIterations; i++) {
            double currentSimilarity = ret.similarity(target);
            if (Math.abs(newSimilarity - currentSimilarity) < minAdjustDifference) {
                break;
            }
            for (int j = 0; j < original.size(); j++) {
                ret.setCoord(j, ret.getCoord(j) + (newSimilarity - currentSimilarity) * adjustExtent * (Double.compare(target.getCoord(j), original.getCoord(j))));
            }
        }
        return ret;
    }
}
