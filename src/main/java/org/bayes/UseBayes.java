package org.bayes;

import org.jfree.chart.JFreeChart;
import org.junit.jupiter.api.Assertions;

import java.util.AbstractMap;
import java.util.Arrays;

public class UseBayes {
    public static void main(String[] args) {
        double priorStudent = 0.6;
        double priorProfessor = 1-priorStudent;
        double[] priors = new double[]{priorStudent, priorProfessor};
        Assertions.assertEquals(Arrays.stream(priors).sum(), 1.0, "The sum of the priors must be 1.0");

        GaussianDistribution studentDistribution = new GaussianDistribution(25.0, 10.0, "Student");
        GaussianDistribution professorDistribution = new GaussianDistribution(35.0, 10.0, "Professor");
        GaussianDistribution[] likelihoods = new GaussianDistribution[]{studentDistribution, professorDistribution};
        GaussianDistribution[] posteriors = new GaussianDistribution[]{
                studentDistribution.scale(priorStudent),
                professorDistribution.scale(priorProfessor)
        };

        // You could insert that into the database
        double[] studentsRandomSamples = studentDistribution.getRandomSamples(100);
        double[] professorsRandomSamples = professorDistribution.getRandomSamples(100);
        System.out.printf("%s random samples: %s\n", studentDistribution.getName(), Arrays.toString(studentsRandomSamples));
        System.out.printf("%s random samples: %s\n", professorDistribution.getName(), Arrays.toString(professorsRandomSamples));

        // Plot the likelihoods
        JFreeChart likelihoodsCart = ProbabilisticDistributions.plotProbabilisticDistributions(
                likelihoods, 0, 80, 1000,
                "likelihoods Distributions",
                "x",
                "p(class|x)"
                // GaussianDistribution.findIntersections(likelihoods)
        );
        JFreeChart posteriorsCart = ProbabilisticDistributions.plotProbabilisticDistributions(
                posteriors, 0, 80, 1000,
                "posteriors Distributions",
                "x",
                "p(class|x)p(x)"
                // GaussianDistribution.findIntersections(posteriors)
        );
        ProbabilisticDistributions.displayPlots(likelihoodsCart, posteriorsCart);

        // Predict the probabilities of each classes (student or professor) given the data x
        double dataX = 80.0;
        BayesClassifier bc = new BayesClassifier();
        double[] probabilities = bc.predictProbabilies(likelihoods, dataX);
        System.out.println("Probabilities: " + java.util.Arrays.toString(probabilities));

        // Predict the class given the probabilities
        int prediction = bc.predict(probabilities);
        System.out.println("Prediction: " + likelihoods[prediction].getName());

        // You could retrieve that from the database
        // Actual datas
        AbstractMap.SimpleEntry[] dataset = ProbabilisticDistributions.createRandomDataset(posteriors, 10000);
        System.out.println("Dataset with likelihood and prior model: " + java.util.Arrays.toString(dataset));
        double accuracyWithPrior = bc.accuracy(posteriors, dataset);
        System.out.println("Dataset for likelihoods only model: " + java.util.Arrays.toString(dataset));
        double accuracyLikelihoodOnly = bc.accuracy(likelihoods, dataset);

        System.out.printf("Accuracy with likelihoods only: %.2f%%\n", accuracyLikelihoodOnly * 100);
        System.out.printf("Accuracy with prior: %.2f%%\n", accuracyWithPrior * 100);
    }
}
