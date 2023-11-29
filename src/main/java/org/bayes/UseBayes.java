package org.bayes;

import javafx.util.Pair;
import org.jfree.chart.JFreeChart;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.xy.XYSeriesCollection;
import org.junit.jupiter.api.Assertions;
import org.plotting.Plotting;

import java.util.Arrays;

public class UseBayes {
    public static void main(String[] args) {
        // Create the classes
        String[] classes = new String[]{"Student", "Professor"};

        // Set the priors
        double[] priors = new double[]{0.1, 0.9};
        Assertions.assertEquals(Arrays.stream(priors).sum(), 1.0, "The sum of the priors must be 1.0");

        // Create the distributions for the student and the professor
        GaussianDistribution studentDistribution = new GaussianDistribution(25.0, 10.0, "Student");
        GaussianDistribution professorDistribution = new GaussianDistribution(35.0, 10.0, "Professor");
        GaussianDistribution[] distributions = new GaussianDistribution[]{studentDistribution, professorDistribution};

        // Generate the dataset from the distributions
        int total = 10000;
        Pair<Double[], Integer[]> datasets[] = ProbabilisticDistribution.createRandomDataset(
                distributions,
                new int[]{(int) (priors[0] * total), (int) (priors[1] * total)}
        );
        DiscreteDistribution[] datasetDistributions = {
                new DiscreteDistribution(datasets[0].getKey()),
                new DiscreteDistribution(datasets[1].getKey())
        };

        // Model the distributions from the dataset instead of using the real distributions
        studentDistribution = new GaussianDistribution(datasets[0].getKey(), "Student");
        professorDistribution = new GaussianDistribution(datasets[1].getKey(), "Professor");
        distributions = new GaussianDistribution[]{studentDistribution, professorDistribution};

        // Create a bayes classifier with the dataset and the priors
        BayesClassifier bayesClassifierWithoutPriors = new BayesClassifier(distributions, classes);
        BayesClassifier bayesClassifierWithPriors = new BayesClassifier(distributions, priors, classes);

        // Plotting
        double min = 0.0;
        double max = 80.0;
        int n = 1000;
        HistogramDataset datasetProfessorStudentPlot = DiscreteDistribution.getHistogramDataset(new DiscreteDistribution[]{datasetDistributions[0], datasetDistributions[1]}, 100, classes);
        XYSeriesCollection bayesClassifierWithoutPriorsPlot = bayesClassifierWithoutPriors.getPlotCollection(min, max, n, "Bayes Classifier without priors");
        XYSeriesCollection bayesClassifierWithPriorsPlot = bayesClassifierWithPriors.getPlotCollection(min, max, n, "Bayes Classifier with priors");
        XYSeriesCollection studentDistributionsPlotDataset = studentDistribution.getPlotCollection(min, max, n, "Student distribution");
        XYSeriesCollection professorDistributionsPlotDataset = professorDistribution.getPlotCollection(min, max, n, "Professor distribution");
        JFreeChart bayesClassifierPlotWithoutPriors = Plotting.createXYLineChart(
                "Bayes Classifier without priors",
                "x",
                "p(class|x)p(x)",
                bayesClassifierWithoutPriorsPlot
        );
        JFreeChart bayesClassifierPlotWithPriors = Plotting.createXYLineChart(
                "Bayes Classifier with priors",
                "x",
                "p(class|x)p(x)",
                bayesClassifierWithPriorsPlot
        );
        JFreeChart studentProfessorDistributionsPlot = Plotting.createXYLineChart(
                "Student and professor distributions",
                "x",
                "p(x)",
                professorDistributionsPlotDataset, studentDistributionsPlotDataset
        );
        JFreeChart datasetPlotChart = Plotting.createXYBarChart(
                "Dataset",
                "x",
                "p(x)",
                datasetProfessorStudentPlot
        );
        Plotting.displayPlots(datasetPlotChart, studentProfessorDistributionsPlot, bayesClassifierPlotWithoutPriors, bayesClassifierPlotWithPriors);

        // Compute the accuracy of the classifier
        Pair<Double[], Integer[]> wholeDataset = datasets[2];
        double accuracyWithoutPriors = bayesClassifierWithoutPriors.accuracy(wholeDataset);
        double accuracyWithPriors = bayesClassifierWithPriors.accuracy(wholeDataset);
        System.out.printf("Accuracy without priors: %.2f%%\n", accuracyWithoutPriors * 100);
        System.out.printf("Accuracy with priors: %.2f%%\n", accuracyWithPriors * 100);

        // Get the probabilities of each class given x and predict the class
        double dataX = 20.0;
        double[] probabilities = bayesClassifierWithPriors.predictProbabilities(dataX);
        int prediction = bayesClassifierWithPriors.predict(probabilities);
        System.out.println(String.format("Probabilities of each class given x = %.2f: %s (with priors)", dataX, Arrays.toString(probabilities)));
        System.out.println(String.format("Prediction: %s", bayesClassifierWithPriors.getClasses()[prediction]));

        probabilities = bayesClassifierWithoutPriors.predictProbabilities(dataX);
        prediction = bayesClassifierWithoutPriors.predict(probabilities);
        System.out.println(String.format("Probabilities of each class given x = %.2f: %s (without priors)", dataX, Arrays.toString(probabilities)));
        System.out.println(String.format("Prediction: %s", bayesClassifierWithoutPriors.getClasses()[prediction]));
    }
}
