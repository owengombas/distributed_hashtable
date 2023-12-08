package org.stringmatching;

import javafx.util.Pair;
import org.distances.LevensteinDistance;
import org.file.FileUtils;
import org.knn.NearestNeighbours;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class UseKNN {
    public static void main(String[] args) throws IOException {
        // Similar words for the LevensteinDistance
        String[] strings = {"kitten", "sitting", "saturday", "sunday", "rosettacode", "raisethysword"};
        double[][] stringsDouble = StringToDouble.convertToDouble(strings);
        NearestNeighbours nn = new NearestNeighbours(new LevensteinDistance(), stringsDouble);
        nn.computeDistances(StringToDouble.convertToDouble("kit"));
        System.out.println(Arrays.toString(nn.getTopK(3)));
    }
}
