package org.bayes;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.junit.jupiter.api.Assertions;
import org.math.VectorXd;

import javax.swing.*;
import java.awt.*;
import java.util.AbstractMap;
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

    abstract double getProbability(double x);

    public abstract void fromData(double[] data);

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

    public XYSeriesCollection plot(double min, double max, int n, String title) {
        Double[] xs = VectorXd.linspace(min, max, n).toArray(new Double[n]);

        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries series = new XYSeries(this.toString());
        for (Double x : xs) {
            double p = this.getProbability(x);
            series.add(x.doubleValue(), p);
        }
        dataset.addSeries(series);
        // Set a label for the series
        series.setKey(String.format("%s", title));

        return dataset;
    }

    public static AbstractMap.SimpleEntry[] createRandomDataset(ProbabilisticDistribution[] distributions, String[] classes, int[] ns) {
        Assertions.assertEquals(distributions.length, classes.length, "The number of distributions and classes must be the same");

        ArrayList<AbstractMap.SimpleEntry> data = new ArrayList<>();

        for (int i = 0; i < distributions.length; i++) {
            ProbabilisticDistribution distribution = distributions[i];
            int n = ns[i];
            for (int j = 0; j < n; j++) {
                // Get a random sample from the distribution
                double sample = distribution.getRandomSample();
                // Add the sample to the array
                AbstractMap.SimpleEntry<Double, String> sampleAndDistribution = new AbstractMap.SimpleEntry<>(sample, classes[i]);
                data.add(sampleAndDistribution);
            }
        }

        return data.toArray(new AbstractMap.SimpleEntry[0]);
    }
}
