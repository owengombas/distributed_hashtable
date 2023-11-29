package org.bayes;

import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeriesCollection;
import org.junit.jupiter.api.Assertions;
import org.plotting.Plotting;

import java.util.AbstractMap;
import java.util.Arrays;

public class UseBayes {
    public static void main(String[] args) {
        // Set the priors
        double priorStudent = 0.1;
        double priorProfessor = 1 - priorStudent;
        double[] priors = new double[]{priorStudent, priorProfessor};

        // Create the distributions for the student and the professor
        GaussianDistribution studentDistribution = new GaussianDistribution(25.0, 10.0, "Student");
        GaussianDistribution professorDistribution = new GaussianDistribution(35.0, 10.0, "Professor");
        GaussianDistribution[] distributions = new GaussianDistribution[]{studentDistribution, professorDistribution};

        // Create the classes
        String[] classes = new String[]{"Student", "Professor"};

        // Generate the dataset
        int total = 10000;
        AbstractMap.SimpleEntry[] dataset = ProbabilisticDistribution.createRandomDataset(
                distributions,
                classes,
                new int[]{(int) (priorStudent * total), (int) (priorProfessor * total)}
        );

        // Create a bayes classifier with the dataset and the priors
        BayesClassifier bayesClassifierWithoutPriors = new BayesClassifier(distributions, classes);
        BayesClassifier bayesClassifierWithPriors = new BayesClassifier(distributions, priors, classes);

        // Plot the distributions
        double min = 0.0;
        double max = 80.0;
        int n = 1000;
        JFreeChart bayesClassifierWithoutPriorsPlot = bayesClassifierWithoutPriors.plot(min, max, n, "Bayes Classifier without priors", "x", "p(class|x)p(x)");
        JFreeChart bayesClassifierWithPriorsPlot = bayesClassifierWithPriors.plot(min, max, n, "Bayes Classifier with priors", "x", "p(class|x)p(x)");
        XYSeriesCollection studentDistributionsPlotDataset = studentDistribution.plot(min, max, n, "Student distribution");
        XYSeriesCollection professorDistributionsPlotDataset = professorDistribution.plot(min, max, n, "Professor distribution");
        JFreeChart studentProfessorDistributionsPlot = Plotting.createXYLineChart(
                "Student and professor distributions",
                "x",
                "p(x)",
                professorDistributionsPlotDataset, studentDistributionsPlotDataset
        );
        Plotting.displayPlots(studentProfessorDistributionsPlot, bayesClassifierWithoutPriorsPlot, bayesClassifierWithPriorsPlot);

        // Compute the accuracy of the classifier
        double accuracyWithoutPriors = bayesClassifierWithoutPriors.accuracy(dataset);
        double accuracyWithPriors = bayesClassifierWithPriors.accuracy(dataset);
        System.out.printf("Accuracy without priors: %.2f%%\n", accuracyWithoutPriors * 100);
        System.out.printf("Accuracy with priors: %.2f%%\n", accuracyWithPriors * 100);

        // For one data point
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
