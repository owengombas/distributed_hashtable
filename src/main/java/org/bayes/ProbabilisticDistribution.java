package org.bayes;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.junit.jupiter.api.Assertions;

import javax.swing.*;
import java.awt.*;
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

    public double[] getProbabilities(double[] xs) {
        return java.util.Arrays.stream(xs).map(this::getProbability).toArray();
    }

    public double[] getRandomSamples(int n) {
        return DoubleStream.generate(this::getRandomSample).limit(n).toArray();
    }

    @Override
    public abstract String toString();
}
