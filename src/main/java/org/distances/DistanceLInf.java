package org.distances;

public class DistanceLInf implements Distance {
    public DistanceLInf() {}

    @Override
    public double computeDistance(double[] individual1, double[] individual2) {
        double distance = 0.0;
        for (int i = 0; i < individual1.length; i++) {
            distance = Math.max(distance, Math.abs(individual1[i] - individual2[i]));
        }
        return distance;
    }
}
