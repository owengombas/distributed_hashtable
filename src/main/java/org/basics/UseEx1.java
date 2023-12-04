package org.basics;

import java.util.Scanner;

public class UseEx1 {
    public static String getNChars(int N, String str) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < N; i++) {
            result.append(str);
        }
        return result.toString();
    }

    public static void main(String[] args) {
        while (true) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter a number: ");
            int num = scanner.nextInt();
            // String str = scanner.nextLine();

            if (num <= 0) return;

            boolean isEven = num % 2 == 0;
            if (isEven) System.out.println(getNChars(num, "-"));
            else System.out.println(getNChars(num, "="));

            System.out.println();
        }
    }
}
