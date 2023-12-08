package org.file;

import java.io.*;
import java.util.Scanner;
import java.util.Vector;

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

    public String[] readLines(String path) throws IOException {
        File file = new File(path);
        Scanner scanner = new Scanner(file);

        Vector<String> lines = new Vector<String>();
        int i = 0;

        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            if (line.length() > 0) {
                lines.add(line);
            }
        }

        scanner.close();

        return lines.toArray(new String[lines.size()]);
    }
}
