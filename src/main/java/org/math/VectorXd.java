package org.math;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

public class VectorXd<TValue extends Number> {
    List<TValue> data;

    public int size() {
        return data.size();
    }

    public VectorXd<TValue> append(TValue value) {
        data.add(value);
        return this;
    }

    public VectorXd<TValue> append(VectorXd<TValue> other) {
        data.addAll(other.data);
        return this;
    }

    public VectorXd(int n) {
        data = new ArrayList<>(n);
    }

    public static VectorXd<Double> linspace(double start, double end, int n) {
        VectorXd<Double> result = new VectorXd<>(n);
        double step = (end - start) / (n - 1);
        for (int i = 0; i < n; i++) {
            result.data.add(start + i * step);
        }
        return result;
    }

    public VectorXd<TValue> scale(Number scalar) {
        VectorXd<TValue> result = new VectorXd<>(data.size());
        result.data = (List<TValue>) data.stream().map(d -> d.doubleValue() * scalar.doubleValue()).collect(Collectors.toList());
        return result;
    }

    public VectorXd<TValue> componentwiseMultiply(VectorXd<TValue> other) {
        VectorXd<TValue> result = new VectorXd<>(data.size());
        result.data = (List<TValue>) data.stream().map(d -> d.doubleValue() * other.data.get(data.indexOf(d)).doubleValue()).collect(Collectors.toList());
        return result;
    }

    public TValue get(int i) {
        return data.get(i);
    }

    public VectorXd<TValue> set(int i, TValue value) {
        data.set(i, value);
        return this;
    }

    public VectorXd<TValue> map(Function<TValue, TValue> f) {
        VectorXd<TValue> result = new VectorXd<>(data.size());
        result.data = (List<TValue>) data.stream().map(f::apply).collect(Collectors.toList());
        return result;
    }

    public TValue[] toArray(TValue[] array) {
        return data.toArray(array);
    }

    public List<TValue> toList() {
        return data;
    }
}
