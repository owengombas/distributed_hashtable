package org.distances;

public class DistanceL2 implements Distance {
    public DistanceL2() {}

    @Override
    public double computeDistance(double[] individual1, double[] individual2) {
        double distance = 0.0;
        for (int i = 0; i < individual1.length; i++) {
            distance += Math.pow(individual1[i] - individual2[i], 2);
        }
        return Math.sqrt(distance);
    }
}
