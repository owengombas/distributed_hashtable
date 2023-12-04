package org.math;

import org.junit.jupiter.api.Assertions;

public class Matrix {
    private double[][] matrix;
    private int rows;
    private int columns;

    public int getColumns() {
        return columns;
    }

    public int getRows() {
        return this.rows;
    }

    static Matrix identity(int size) {
        Matrix result = new Matrix(size, size);

        for (int i = 0; i < size; i++) {
            result.set(i, i, 1.0);
        }

        return result;
    }

    public double get(int row, int column) {
        return this.matrix[row][column];
    }

    public Matrix set(int row, int column, double value) {
        this.matrix[row][column] = value;
        return this;
    }

    public Matrix(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.matrix = new double[rows][columns];
    }

    public Matrix(Matrix matrix) {
        this.rows = matrix.getRows();
        this.columns = matrix.getColumns();
        this.matrix = new double[rows][columns];

        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.columns; j++) {
                this.set(i, j, matrix.get(i, j));
            }
        }
    }

    public Matrix add(Matrix matrix) {
        Matrix result = new Matrix(this.getRows(), this.getColumns());

        for (int i = 0; i < this.getRows(); i++) {
            for (int j = 0; j < this.getColumns(); j++) {
                result.set(i, j, this.get(i, j) + matrix.get(i, j));
            }
        }

        return result;
    }

    public Matrix substract(Matrix matrix) {
        Matrix result = new Matrix(this.getRows(), this.getColumns());

        for (int i = 0; i < this.getRows(); i++) {
            for (int j = 0; j < this.getColumns(); j++) {
                result.set(i, j, this.get(i, j) - matrix.get(i, j));
            }
        }

        return result;
    }

    public Matrix multiply(Matrix matrix) {
        int n = this.getRows();
        int m = this.getColumns();
        Assertions.assertEquals(m, matrix.getRows());
        int p = this.getColumns();

        Matrix result = new Matrix(n, p);

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < p; j++) {
                double sum = 0.0;
                for (int k = 0; k < m; k++) {
                    sum += this.get(i, k) * this.get(k, j);
                }
                result.set(i, j, sum);
            }
        }

        return result;
    }
}
