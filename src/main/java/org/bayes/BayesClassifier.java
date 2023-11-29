package org.bayes;

import javafx.util.Pair;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.junit.jupiter.api.Assertions;
import org.math.VectorXd;
import org.plotting.Plotting;

import java.util.AbstractMap;
import java.util.stream.IntStream;

public class BayesClassifier {
    ProbabilisticDistribution[] likelihoods;
    double[] priors;
    String[] classes;

    public double[] getPriors() {
        return priors;
    }

    public String[] getClasses() {
        return classes;
    }

    public ProbabilisticDistribution[] getLikelihoods() {
        return likelihoods;
    }

    public BayesClassifier(ProbabilisticDistribution[] likelihoods, double[] priors, String[] classes) {
        Assertions.assertEquals(likelihoods.length, classes.length, "The number of likelihoods and classes must be the same");
        Assertions.assertEquals(
                IntStream.range(0, priors.length).mapToDouble(i -> priors[i]).sum(),
                1.0,
                "The sum of the priors must be 1.0"
        );

        this.likelihoods = likelihoods;
        this.priors = priors;
        this.classes = classes;
    }

    public BayesClassifier(ProbabilisticDistribution[] likelihoods, String[] classes) {
        this(
                likelihoods,
                IntStream.range(0, likelihoods.length).mapToDouble(i -> 1.0 / likelihoods.length).toArray(),
                classes
        );
    }

    /**
     * Predict the probability of each class given the data x and the priors
     *
     * @param x The data for which we want to predict the class
     * @return The probability of each class
     */
    public double[] predictProbabilities(double x) {
        Assertions.assertEquals(likelihoods.length, priors.length, "The number of likelihoods and priors must be the same");

        double[] probabilities = new double[likelihoods.length];
        for (int i = 0; i < likelihoods.length; i++) {
            probabilities[i] = likelihoods[i].getProbability(x) * priors[i];
        }

        return probabilities;
    }

    /**
     * Predict the class given the probabilities of each class
     *
     * @param probabilities The probabilities of each class
     * @return The index of the class with the highest probability (argmax)
     */
    public int predict(double[] probabilities) {
        int maxIndex = 0;
        for (int i = 1; i < probabilities.length; i++) {
            if (probabilities[i] > probabilities[maxIndex]) {
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    public double accuracy(Pair<Double[], Integer[]> dataset) {
        Assertions.assertEquals(dataset.getKey().length, dataset.getValue().length, "The number of data points and labels must be the same");

        int total = dataset.getKey().length;
        int correct = 0;

        for (int i = 0; i < total; i++) {
            Double x = dataset.getKey()[i];
            Integer label = dataset.getValue()[i];
            double[] probabilities = this.predictProbabilities(x);
            int prediction = this.predict(probabilities);
            if (prediction == label) {
                correct++;
            }
        }

        return correct / (double) total;
    }

    public XYSeriesCollection getPlotCollection(double min, double max, int n, String title) {
        Double[] xs = VectorXd.linspace(min, max, n).toArray(new Double[n]);

        XYSeriesCollection dataset = new XYSeriesCollection();
        for (int i = 0; i < likelihoods.length; i++) {
            ProbabilisticDistribution distribution = likelihoods[i];
            XYSeries series = new XYSeries(distribution.toString());
            for (Double x : xs) {
                double p = distribution.getProbability(x) * priors[i];
                series.add(x.doubleValue(), p);
            }
            dataset.addSeries(series);
            // Set a label for the series
            series.setKey(String.format("%s %d (prior = %.2f)", title, i, priors[i]));
        }

        return dataset;
    }
}
