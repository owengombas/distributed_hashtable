package org.example.ht;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class HTBench {
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";

    private HT<String, Integer> simpleKVInstance;
    private Set<String> keySet = new HashSet<>();

    public HT<String, Integer> getSimpleKVInstance() {
        return this.simpleKVInstance;
    }

    public HTBench(HT<String, Integer> simpleKVInstance) {
        this.simpleKVInstance = simpleKVInstance;
    }

    public int getRandomInteger(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max);
    }

    public String getRandomString(int length, String alphabet) {
        return IntStream.range(0, length)
                .mapToObj((i) -> alphabet.charAt(this.getRandomInteger(0, alphabet.length())))
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }

    public String getRandomString(int length) {
        return this.getRandomString(length, HTBench.ALPHABET);
    }

    public void putRandomEntry(int minStringLength, int maxStringLength, int minNumber, int maxNumber) {
        int stringLength = getRandomInteger(minStringLength, maxStringLength);
        int generatedNumber = getRandomInteger(minNumber, maxNumber);
        String generatedString = this.getRandomString(stringLength);

        this.keySet.add(generatedString);
        this.simpleKVInstance.put(generatedString, generatedNumber);
    }

    public long populateRandomlyParallel(int nSamples, int minStringLength, int maxStringLength, int minNumber, int maxNumber) {
        return this.executeAndGetExecutionTimeInMilliseconds(
                () -> {
                    IntStream
                            .range(0, nSamples)
                            .parallel()
                            .peek((i) -> putRandomEntry(minStringLength, maxStringLength, minNumber, maxNumber));
                }
        );
    }

    public long populateRandomlySequential(int nSamples, int minStringLength, int maxStringLength, int minNumber, int maxNumber) {
        return this.executeAndGetExecutionTimeInMilliseconds(
                () -> {
                    for (int i = 0; i < nSamples; i++) {
                        putRandomEntry(minStringLength, maxStringLength, minNumber, maxNumber);
                    }
                }
        );
    }

    public long getEverythingParallel() {
        return this.executeAndGetExecutionTimeInMilliseconds(
                () -> {
                    this.keySet
                            .stream()
                            .parallel().peek(
                                    (String key) -> {
                                        this.simpleKVInstance.get(key);
                                    }
                            );
                }
        );
    }

    public long getEverythingSequential() {
        return this.executeAndGetExecutionTimeInMilliseconds(
            () -> {
                for(String key: this.keySet) {
                    this.simpleKVInstance.get(key);
                }
            }
        );
    }

    public long executeAndGetExecutionTimeInMilliseconds(Runnable function) {
        long startTime = System.nanoTime();
        function.run();
        long endTime = System.nanoTime();
        return (endTime - startTime) / 1000000L;
    }
}
