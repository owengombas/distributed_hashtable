package org.stringmatching;

import org.distances.LevensteinDistance;
import org.file.FileUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class UseLevensteinDistance {
    public static void main(String[] args) throws IOException {
        LevensteinDistance ld = new LevensteinDistance();
        ld.computeDistance("ABABBB", "BABAAA");
        System.out.println(ld);
    }
}
