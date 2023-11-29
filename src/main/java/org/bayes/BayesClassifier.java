package org.bayes;

import org.junit.jupiter.api.Assertions;

import java.util.AbstractMap;
import java.util.stream.IntStream;

public class BayesClassifier {
    public BayesClassifier() {
    }

    /**
     * Predict the probability of each class given the data x and the priors
     *
     * @param likelihoods The likelihoods of each class (the length of this array is the number of classes)
     * @param priors      The priors of each class (the length of this array is the number of classes)
     * @param x           The data for which we want to predict the class
     * @return The probability of each class
     */
    public double[] predictProbabilies(ProbabilisticDistribution[] likelihoods, double[] priors, double x) {
        Assertions.assertEquals(likelihoods.length, priors.length, "The number of likelihoods and priors must be the same");

        double[] probabilities = new double[likelihoods.length];
        for (int i = 0; i < likelihoods.length; i++) {
            probabilities[i] = likelihoods[i].getProbability(x) * priors[i];
        }

        return probabilities;
    }

    /**
     * Predict the probability of each class given the data x, we do not know the priors so we assume they are equal
     * priors[] = {1/len(likelihoods), 1/len(likelihoods), ...}
     *
     * @param likelihoods The likelihoods of each class (the length of this array is the number of classes)
     * @param x           The data for which we want to predict the class
     * @return The probability of each class
     */
    public double[] predictProbabilies(ProbabilisticDistribution[] likelihoods, double x) {
        double[] priors = IntStream.range(0, likelihoods.length).mapToDouble(i -> 1.0 / likelihoods.length).toArray();
        return predictProbabilies(likelihoods, priors, x);
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

    public double accuracy(ProbabilisticDistribution[] models, AbstractMap.SimpleEntry<Double, String> dataset[]) {
        int correct = 0;

        for (AbstractMap.SimpleEntry<Double, String> data : dataset) {
            double[] probabilities = predictProbabilies(models, data.getKey());
            int prediction = predict(probabilities);
            if (models[prediction].getName().equals(data.getValue())) {
                correct++;
            }
        }

        return correct / (double) dataset.length;
    }
}
