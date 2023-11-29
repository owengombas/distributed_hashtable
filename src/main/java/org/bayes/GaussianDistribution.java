package org.bayes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.DoubleStream;

public class GaussianDistribution extends ProbabilisticDistribution {
    private double mean;
    private double variance;

    public double getMean() {
        return mean;
    }

    public double getVariance() {
        return variance;
    }

    public GaussianDistribution(double mean, double variance, String name) {
        super(name);
        this.mean = mean;
        this.variance = variance;
    }

    public double[] findIntersections(GaussianDistribution other) {
        double a = 1 / (2 * variance) - 1 / (2 * other.variance);
        double b = other.mean / other.variance - mean / variance;
        double c = mean * mean / (2 * variance) - other.mean * other.mean / (2 * other.variance) - Math.log(Math.sqrt(variance / other.variance));
        double[] roots = new double[2];
        double discriminant = Math.sqrt(b * b - 4 * a * c);
        roots[0] = (-b + discriminant) / (2 * a);
        roots[1] = (-b - discriminant) / (2 * a);
        return roots;
    }

    @Override
    public double getProbability(double x) {
        double a = 1 / Math.sqrt(2 * Math.PI * variance);
        double b = Math.exp(-Math.pow(x - mean, 2) / (2 * variance));
        return a * b;
    }

    @Override
    public void fromData(double[] data) {
        this.mean = DoubleStream.of(data).average().orElse(0);
        this.variance = DoubleStream.of(data).map(d -> Math.pow(d - mean, 2)).average().orElse(0);
    }

    @Override
    public String toString() {
        return String.format("Gaussian Distribution %s (mean = %.2f, variance = %.2f)", this.getName(), mean, variance);
    }

    @Override
    public double getRandomSample() {
        double u1 = Math.random();
        double u2 = Math.random();
        double z0 = Math.sqrt(-2 * Math.log(u1)) * Math.cos(2 * Math.PI * u2);
        return z0 * Math.sqrt(variance) + mean;
    }

    public static double[] findIntersections(GaussianDistribution... distributions) {
        ArrayList<double[]> intersections = new ArrayList<>();
        for (int i = 0; i < distributions.length; i++) {
            for (int j = i + 1; j < distributions.length; j++) {
                intersections.add(distributions[i].findIntersections(distributions[j]));
            }
        }
        return intersections.stream().flatMapToDouble(Arrays::stream).toArray();
    }
}
