package org.bayes;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.util.AbstractMap;
import java.util.ArrayList;

public class ProbabilisticDistributions {
    /**
     * Create a linearly spaced array of doubles
     * @param min The minimum value
     * @param max The maximum value
     * @param n The number of points
     * @return The array of doubles
     */
    public static double[] linspace(double min, double max, int n) {
        double[] xs = new double[n];
        for (int i = 0; i < n; i++) {
            xs[i] = min + (max - min) * i / (n - 1);
        }
        return xs;
    }

    /**
     * Display a set of plots (one per chart)
     * @param charts The plots to display
     */
    public static void displayPlots(JFreeChart... charts) {
        JFrame frame = new JFrame();
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        for (JFreeChart chart : charts) {
            frame.add(new ChartPanel(chart));
        }
        frame.pack();
        frame.setVisible(true);
    }

    private static XYSeriesCollection getPlotDataset(String title, ProbabilisticDistribution[] distributions, double min, double max, int n) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        for (int i = 0; i < distributions.length; i++) {
            ProbabilisticDistribution distribution = distributions[i];
            XYSeries series = new XYSeries(distribution.toString());
            for (double x : ProbabilisticDistributions.linspace(min, max, n)) {
                series.add(x, distribution.getProbability(x));
            }
            dataset.addSeries(series);
            // Set a label for the series
            series.setKey(String.format("%s %d", title, i));
        }
        return dataset;
    }

    /**
     * Plot a set of probabilistic distributions
     *
     * @param distributions The distributions to plot
     * @param min           The minimum value of x
     * @param max           The maximum value of x
     * @param n             The number of points to plot
     * @param title         The title of the plot
     * @param xLabel        The label of the x axis
     * @param yLabel        The label of the y axis
     * @return The plot
     */
    public static JFreeChart plotProbabilisticDistributions(
            ProbabilisticDistribution[] distributions,
            double min,
            double max,
            int n,
            String title,
            String xLabel,
            String yLabel
    ) {
        XYSeriesCollection dataset = ProbabilisticDistributions.getPlotDataset(title, distributions, min, max, n);

        return ChartFactory.createXYLineChart(
                title,
                xLabel,
                yLabel,
                dataset
        );
    }

    public static JFreeChart plotProbabilisticDistributions(
            ProbabilisticDistribution[] distributions,
            double min,
            double max,
            int n,
            String title,
            String xLabel,
            String yLabel,
            double[] intersectionPoints
    ) {
        XYSeriesCollection dataset = ProbabilisticDistributions.getPlotDataset(title, distributions, min, max, n);

        // Find the maximum y value for the plot
        double maxY = 0.0;
        for (ProbabilisticDistribution distribution : distributions) {
            for (double x : ProbabilisticDistributions.linspace(min, max, n)) {
                maxY = Math.max(maxY, distribution.getProbability(x));
            }
        }

        // Display the intersection points as markers
        for (int i = 0; i < intersectionPoints.length; i++) {
            double intersectionPoint = intersectionPoints[i];
            XYSeries series = new XYSeries(String.format("%s intersection %d", title, i));
            series.add(intersectionPoint, 0.0);
            series.add(intersectionPoint, maxY);
            dataset.addSeries(series);
        }

        JFreeChart chart = ChartFactory.createXYLineChart(
                title,
                xLabel,
                yLabel,
                dataset
        );

        return chart;
    }

    public static AbstractMap.SimpleEntry[] createRandomDataset(ProbabilisticDistribution[] distributions, int n) {
        ArrayList<AbstractMap.SimpleEntry> data = new ArrayList<>();

        for (ProbabilisticDistribution distribution : distributions) {
            for (int j = 0; j < n; j++) {
                // Get a random sample from the distribution
                double sample = distribution.getRandomSample();
                // Add the sample to the array
                AbstractMap.SimpleEntry<Double, String> sampleAndDistribution = new AbstractMap.SimpleEntry<>(sample, distribution.getName());
                data.add(sampleAndDistribution);
            }
        }

        return data.toArray(new AbstractMap.SimpleEntry[0]);
    }
}
