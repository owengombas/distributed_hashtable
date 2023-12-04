package org.basics;

import org.file.FileUtils;

import java.io.IOException;

public class UseEx2 {
    public static void main(String[] args) throws IOException {
        FileUtils fileUtils = new FileUtils();
        fileUtils.replicate("source.txt", "destination.txt");
    }
}
