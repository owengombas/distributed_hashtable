package org.distances;

import javafx.util.Pair;
import org.stringmatching.StringToDouble;

import java.util.Vector;

public class LevensteinDistance implements Distance {
    private String string1;
    private String string2;
    private int[][] matrix;
    private int[][] pathMatrix;
    private boolean computed = false;

    public int[][] getPathMatrix() {
        return this.pathMatrix;
    }

    public int[][] getMatrix() {
        return this.matrix;
    }

    public int getDistance() {
        return this.matrix[string1.length()][string2.length()];
    }

    public LevensteinDistance() {
    }

    private int costSubstitution(int i, int j) {
        if (string1.charAt(i - 1) == string2.charAt(j - 1)) {
            return this.matrix[i - 1][j - 1];
        }
        return this.matrix[i - 1][j - 1] + 2;
    }

    private int costDeletion(int i, int j) {
        return this.matrix[i - 1][j] + 1;
    }

    private int costInsertion(int i, int j) {
        return this.matrix[i][j - 1] + 1;
    }

    private void initializeMatrix() {
        this.matrix = new int[this.string1.length() + 1][this.string2.length() + 1];
        this.pathMatrix = new int[this.string1.length() + 1][this.string2.length() + 1];

        for (int i = 0; i <= this.string1.length(); i++) {
            this.matrix[i][0] = i;
        }

        for (int j = 0; j <= this.string2.length(); j++) {
            this.matrix[0][j] = j;
        }
    }

    public double computeDistance(String string1, String string2) {
        this.string1 = string1;
        this.string2 = string2;

        this.initializeMatrix();

        for (int i = 1; i <= string1.length(); i++) {
            for (int j = 1; j <= string2.length(); j++) {
                int costInsertion = costInsertion(i, j);
                int costDeletion = costDeletion(i, j);
                int costSubstitution = costSubstitution(i, j);
                int[] costs = {costInsertion, costDeletion, costSubstitution};

                int min = 0;
                for (int k = 1; k < costs.length; k++) {
                    if (costs[k] <= costs[min]) {
                        min = k;
                    }
                }

                this.pathMatrix[i][j] = min;
                this.matrix[i][j] = costs[min];
            }
        }

        this.computed = true;

        return this.getDistance();
    }

    @Override
    public double computeDistance(double[] individual1, double[] individual2) {
        this.string1 = StringToDouble.convertToString(individual1);
        this.string2 = StringToDouble.convertToString(individual2);
        this.computeDistance(string1, string2);

        return this.getDistance();
    }

    public Pair[] backtrackPath() {
        if (!this.computed) {
            return null;
        }

        Vector<Pair<Integer, Integer>> path = new Vector<>();

        int i = string1.length();
        int j = string2.length();

        while (i > 0 || j > 0) {
            int pathValue = this.pathMatrix[i][j];
            switch (pathValue) {
                case 0:
                    path.add(new Pair<>(i, j - 1));
                    j--;
                    break;
                case 1:
                    path.add(new Pair<>(i - 1, j));
                    i--;
                    break;
                case 2:
                    path.add(new Pair<>(i - 1, j - 1));
                    i--;
                    j--;
                    break;
            }
        }

        return path.toArray(new Pair[path.size()]);
    }

    @Override
    public String toString() {
        if (!this.computed) {
            return "Distance not computed";
        }

        StringBuilder result = new StringBuilder();

        result.append("Matrix:\n");
        for (int i = 0; i <= string1.length(); i++) {
            for (int j = 0; j <= string2.length(); j++) {
                result.append(String.format("%3d ", this.matrix[i][j]));
            }
            result.append("\n");
        }

        result.append("Path:\n");
        for (int i = 0; i <= string1.length(); i++) {
            for (int j = 0; j <= string2.length(); j++) {
                result.append(String.format("%3d ", this.pathMatrix[i][j]));
            }
            result.append("\n");
        }

        result.append("Path:\n");
        Pair[] path = this.backtrackPath();
        for (int i = path.length - 1; i >= 0; i--) {
            result.append(String.format("(%d, %d) ", path[i].getKey(), path[i].getValue()));
        }

        result.append("Distance: ").append(this.matrix[string1.length()][string2.length()]).append("\n");

        return result.toString();
    }
}
