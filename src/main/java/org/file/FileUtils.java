package org.file;

import java.io.*;
import java.util.Scanner;

public class FileUtils {
    public void replicate(String source, String destination) throws IOException {
        File sourceFile = new File(source);
        File destinationFile = new File(destination);

        Scanner sourceFileScanner = new Scanner(sourceFile);
        FileWriter destinationWriter = new FileWriter(destinationFile);

        while (sourceFileScanner.hasNext()) {
            destinationWriter.write(String.format("%s\n", sourceFileScanner.next()));
        }

        sourceFileScanner.close();
        destinationWriter.close();
    }
}
