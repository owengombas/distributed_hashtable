package org.bayes;

import javafx.util.Pair;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.junit.jupiter.api.Assertions;
import org.math.VectorXd;
import org.plotting.LinePlottable;

import java.util.ArrayList;
import java.util.stream.DoubleStream;

public abstract class ProbabilisticDistribution {
    private String name = "";

    public String getName() {
        return name;
    }

    public ProbabilisticDistribution setName(String name) {
        this.name = name;
        return this;
    }

    abstract double getProbability(Double x);

    public ProbabilisticDistribution() {
    }

    public abstract double getRandomSample();

    public ProbabilisticDistribution(String name) {
        this.name = name;
    }

    public VectorXd<Double> getProbabilities(VectorXd<Double> xs) {
        return xs.map(this::getProbability);
    }

    public double[] getRandomSamples(int n) {
        return DoubleStream.generate(this::getRandomSample).limit(n).toArray();
    }

    @Override
    public abstract String toString();

    /**
     * Create a dataset from a set of distributions and ns
     *
     * @param distributions The distributions to sample from
     * @param ns            The number of samples to take from each distribution
     * @return The dataset
     */
    public static Pair<Double[], Integer[]>[] createRandomDataset(ProbabilisticDistribution[] distributions, int[] ns) {
        Assertions.assertEquals(distributions.length, ns.length, "The number of distributions and ns must be the same");

        ArrayList<Double>[] data = new ArrayList[distributions.length + 1];
        ArrayList<Integer>[] labels = new ArrayList[distributions.length + 1];
        data[distributions.length] = new ArrayList<>();
        labels[distributions.length] = new ArrayList<>();

        for (int i = 0; i < distributions.length; i++) {
            ProbabilisticDistribution distribution = distributions[i];
            int n = ns[i];
            data[i] = new ArrayList<>();
            labels[i] = new ArrayList<>();
            for (int j = 0; j < n; j++) {
                // Get a random sample from the distribution
                double sample = distribution.getRandomSample();

                // Add the sample to the data
                data[i].add(sample);
                data[distributions.length].add(sample);

                // Add the label to the labels
                labels[i].add(i);
                labels[distributions.length].add(i);
            }
        }

        // Merge the data and labels
        Pair<Double[], Integer[]>[] dataset = new Pair[distributions.length + 1];
        for (int i = 0; i < dataset.length; i++) {
            dataset[i] = new Pair<>(
                    data[i].toArray(new Double[data[i].size()]),
                    labels[i].toArray(new Integer[labels[i].size()])
            );
        }

        return dataset;
    }
}
