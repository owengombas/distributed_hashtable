package org.stringmatching;

public class StringToDouble {
    public static double[] convertToDouble(String text) {
        double[] textRepresentation = new double[text.length()];

        for (int i = 0; i < text.length(); i++) {
            textRepresentation[i] = (double) text.charAt(i);
        }

        return textRepresentation;
    }

    public static String convertToString(double[] textRepresentation) {
        StringBuilder text = new StringBuilder();

        for (double character : textRepresentation) {
            text.append((char) character);
        }

        return text.toString();
    }

    public static double[][] convertToDouble(String... text) {
        double[][] textRepresentation = new double[text.length][];

        for (int i = 0; i < text.length; i++) {
            textRepresentation[i] = convertToDouble(text[i]);
        }

        return textRepresentation;
    }

    public static String[] convertToString(double[][] textRepresentation) {
        String[] text = new String[textRepresentation.length];

        for (int i = 0; i < textRepresentation.length; i++) {
            text[i] = convertToString(textRepresentation[i]);
        }

        return text;
    }
}
