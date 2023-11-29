package org.bayes;

import org.jfree.data.statistics.HistogramDataset;

import java.util.Arrays;

public class DiscreteDistribution extends ProbabilisticDistribution {
    private Double[] data;
    private Double[] x;
    private Double[] y;

    public DiscreteDistribution(Double[] data) {
        this.data = data;

        x = Arrays.stream(data).distinct().toArray(Double[]::new);
        y = Arrays.stream(x).map(d -> (double) Arrays.stream(data).filter(d2 -> d2.equals(d)).count()).toArray(Double[]::new);
    }

    @Override
    public void fromData(Double[] data) {
        this.data = data;
    }

    @Override
    public double getRandomSample() {
        return data[(int) (Math.random() * data.length)];
    }

    @Override
    public double getProbability(Double x) {
        return Arrays.stream(data).filter(d -> d.equals(x)).count() / (double) data.length;
    }

    @Override
    public String toString() {
        return "Discrete Distribution";
    }

    public HistogramDataset getHistogramDataset(int n, String name) {
        HistogramDataset dataset = new HistogramDataset();
        dataset.addSeries(name, Arrays.stream(data).mapToDouble(Double::doubleValue).toArray(), n);
        return dataset;
    }

    public static HistogramDataset getHistogramDataset(DiscreteDistribution[] distributions, int n, String[] names) {
        HistogramDataset dataset = new HistogramDataset();
        for (int i = 0; i < distributions.length; i++) {
            dataset.addSeries(names[i], Arrays.stream(distributions[i].data).mapToDouble(Double::doubleValue).toArray(), n);
        }
        return dataset;
    }
}
