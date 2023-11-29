package org.bayes;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

public class DiscreteDistribution extends ProbabilisticDistribution {
    private Map<Double, Long> probabilities;

    public DiscreteDistribution(double[] data) {
        super();
        this.fromData(data);
    }

    @Override
    public double getProbability(double x) {
        return probabilities.getOrDefault(x, 0L) / (double) probabilities.values().stream().mapToLong(Long::longValue).sum();
    }

    @Override
    public void fromData(double[] data) {
        // Count the number of occurrences of each value
        this.probabilities = DoubleStream.of(data).boxed().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }

    @Override
    public double getRandomSample() {
        return probabilities.keySet().stream().skip((int) (Math.random() * probabilities.size())).findFirst().orElse(0.0);
    }

    @Override
    public String toString() {
        return "Discrete Distribution";
    }
}
