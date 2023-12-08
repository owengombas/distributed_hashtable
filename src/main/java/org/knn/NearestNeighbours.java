package org.knn;

import javafx.util.Pair;
import org.distances.Distance;
import org.junit.jupiter.api.Assertions;

import java.util.Arrays;
import java.util.stream.IntStream;

public class NearestNeighbours {
    protected Distance distance;
    protected double[][] data;
    protected boolean fitted = false;
    protected Pair<Integer, Double>[] nearestNeighbours;

    public double[][] getData() {
        return this.data;
    }

    public Pair<Integer, Double>[] getNearestNeighbours() {
        return nearestNeighbours;
    }

    public NearestNeighbours(Distance distance, double[][] data) {
        this.distance = distance;
        this.data = data;
    }

    public void computeDistances(double[] individual) {
        this.nearestNeighbours = new Pair[this.data.length];

        // Iterative way to compute the distances
        // for (int i = 0; i < this.data.length; i++) {
        //     double[] currentIndividual = this.data[i];
        //    this.nearestNeighbours[i] = new Pair<>(i, this.distance.computeDistance(individual, currentIndividual));
        // }

        // Functional way to compute the distances
        this.nearestNeighbours = IntStream.range(0, this.data.length)
                .mapToObj(i -> new Pair<>(i, this.distance.computeDistance(individual, this.data[i])))
                .toArray(Pair[]::new);


        // Sort the distances and indices arrays
        this.nearestNeighbours = Arrays.stream(this.nearestNeighbours).parallel()
                .sorted((pair1, pair2) -> pair1.getValue().compareTo(pair2.getValue()))
                .toArray(Pair[]::new);

        this.fitted = true;
    }

    public Pair<Integer, Double>[] getTopK(int topK) {
        Assertions.assertTrue(this.fitted);

        return Arrays.copyOfRange(this.nearestNeighbours, 0, topK);
    }
}
