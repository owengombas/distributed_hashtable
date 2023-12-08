package org.knn;

import javafx.util.Pair;
import org.distances.Distance;
import org.junit.jupiter.api.Assertions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class KNearestNeighbours extends NearestNeighbours {
    private int[] labels;

    public int[] getLabels() {
        return this.labels;
    }

    public int getLabel(int index) {
        return this.labels[index];
    }

    public KNearestNeighbours(Distance distance, double[][] data, int[] labels) {
        super(distance, data);
        this.labels = labels;
    }

    public int[] getLabelsTopK(int topK) {
        Assertions.assertTrue(this.fitted);

        int[] labelsTopK = new int[topK];
        for (int i = 0; i < topK; i++) {
            labelsTopK[i] = this.getLabel(this.nearestNeighbours[i].getKey());
        }
        return labelsTopK;
    }

    public int inferLabel(int topK) {
        Assertions.assertTrue(this.fitted);

        int[] labelsTopK = this.getLabelsTopK(topK);
        Map<Integer, Integer> labelCounts = new HashMap<>();

        for (int label : labelsTopK) {
            if (labelCounts.containsKey(label)) {
                labelCounts.put(label, labelCounts.get(label) + 1);
            } else {
                labelCounts.put(label, 1);
            }
        }

        return labelCounts.entrySet().stream()
                .max((entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1)
                .get().getKey();
    }
}
