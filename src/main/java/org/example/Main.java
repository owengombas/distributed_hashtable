package org.example;

import org.example.ht.HTBench;
import org.example.ht.HTMap;

public class Main {

    public static void main(String[] args) {
        HTMap<String, Integer> simpleKV = new HTMap<>();
        HTBench simpleKVBenc = new HTBench(simpleKV);

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
