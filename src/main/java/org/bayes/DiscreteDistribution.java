package org.bayes;

import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;
import org.junit.jupiter.api.Assertions;

import java.util.Arrays;

public class DiscreteDistribution extends ProbabilisticDistribution {
    private Double[] data;
    private boolean isNormalized = true;

    public int getSum() {
        return Arrays.stream(data).mapToInt(Double::intValue).sum();
    }

    public Double[] getData() {
        return data;
    }

    public DiscreteDistribution setNormalized(boolean normalized) {
        this.isNormalized = normalized;
        return this;
    }

    public DiscreteDistribution(Double[] data) {
        this.data = data;
    }

    @Override
    public double getRandomSample() {
        return data[(int) (Math.random() * data.length)];
    }

    @Override
    public double getProbability(Double x) {
        double count = Arrays.stream(this.getData()).filter(d -> d.equals(x)).count();

        if (isNormalized) {
            return count / this.getSum();
        }

        return count;
    }

    @Override
    public String toString() {
        return "Discrete Distribution";
    }

    public HistogramDataset getHistogramDataset(int n, String name) {
        HistogramDataset dataset = new HistogramDataset();
        dataset.setType(this.isNormalized ? HistogramType.RELATIVE_FREQUENCY : HistogramType.FREQUENCY);
        dataset.addSeries(name, Arrays.stream(this.getData()).mapToDouble(Double::doubleValue).toArray(), n);
        return dataset;
    }

    public static HistogramDataset getHistogramDataset(DiscreteDistribution[] distributions, double[] priors, int n, String[] names, boolean isNormalized) {
        Assertions.assertEquals(distributions.length, priors.length, "The number of distributions and priors must be the same");

        HistogramDataset dataset = new HistogramDataset();
        dataset.setType(isNormalized ? HistogramType.RELATIVE_FREQUENCY : HistogramType.FREQUENCY);
        for (int i = 0; i < distributions.length; i++) {
            Double[] data = distributions[i].getData();
            Double prior = priors[i];
            String name = names[i];
            // Multiply the data by the prior
            data = Arrays.stream(data).map(d -> d * prior).toArray(Double[]::new);
            dataset.addSeries(name, Arrays.stream(data).mapToDouble(Double::doubleValue).toArray(), n);
        }
        return dataset;
    }


    public static HistogramDataset getHistogramDataset(DiscreteDistribution[] distributions, int n, String[] names, boolean isNormalized) {
        double[] uniformPriors = new double[distributions.length];
        Arrays.fill(uniformPriors, 1.0 / distributions.length);
        return getHistogramDataset(distributions, uniformPriors, n, names, isNormalized);
    }
}
