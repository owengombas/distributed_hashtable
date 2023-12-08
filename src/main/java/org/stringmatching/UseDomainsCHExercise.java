package org.stringmatching;

import javafx.util.Pair;
import org.distances.LevensteinDistance;
import org.knn.NearestNeighbours;

import java.io.IOException;
import java.sql.SQLException;

public class UseDomainsCHExercise {
    public static void main(String[] args) throws SQLException, IOException {
        DomainsCHExercise domainsCHExercise = new DomainsCHExercise();

        domainsCHExercise.connectToDatabase();
        // domainsCHExercise.initializeDatabase();
        // domainsCHExercise.insertDomains("domains.txt", 5000);
        domainsCHExercise.loadDomains();

        NearestNeighbours nnDomains = new NearestNeighbours(new LevensteinDistance(), domainsCHExercise.getDomainsDouble());
        nnDomains.computeDistances(StringToDouble.convertToDouble("gombas.ch"));
        Pair<Integer, Double>[] top3 = nnDomains.getTopK(10);

        for (Pair<Integer, Double> pair : top3) {
            System.out.println(domainsCHExercise.getDomains()[pair.getKey()] + " - " + pair.getValue());
        }
    }
}
