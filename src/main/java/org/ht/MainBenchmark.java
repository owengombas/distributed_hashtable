package org.ht;

import org.ht.hashtable.HashtableBenchmark;
import org.ht.hashtable.HashtableMap;

public class MainBenchmark {

    public static void main(String[] args) {
        HashtableMap<String, Integer> simpleKV = new HashtableMap<>();
        HashtableBenchmark simpleKVBenc = new HashtableBenchmark(simpleKV);

        int nSamples = 10000000;
        long putParallelTimeMilliseconds = simpleKVBenc.populateRandomlyParallel(
                nSamples,
                3,
                10,
                1,
                1000
        );
        long putSequentialTimeMilliseconds = simpleKVBenc.populateRandomlySequential(
                nSamples,
                3,
                10,
                1,
                1000
        );
        System.out.println(String.format("Put Parallel (%d elements): %dms", nSamples, putParallelTimeMilliseconds));
        System.out.println(String.format("Put Sequential (%d elements): %dms", nSamples, putSequentialTimeMilliseconds));

        long getParallelTimeMilliseconds = simpleKVBenc.getEverythingParallel();
        long getSequentialTimeMilliseconds = simpleKVBenc.getEverythingSequential();
        System.out.println(String.format("Get Parallel (%d elements): %dms", nSamples, getParallelTimeMilliseconds));
        System.out.println(String.format("Get Sequential (%d elements): %dms", nSamples, getSequentialTimeMilliseconds));
    }
}
