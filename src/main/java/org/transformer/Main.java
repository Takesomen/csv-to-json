package org.transformer;

import java.util.*;


public class Main {


    public static void main(String[] args) {
        System.out.println("Интерактивный ввод? (True/False)");
        Scanner scan = new Scanner(System.in);
        if (scan.hasNextBoolean()) {
            boolean mode = scan.nextBoolean();
            if (mode) {
                var interactiveMode = new InteractiveMode();
                interactiveMode.run();
            } else {
                var app = new ConsoleApp();
                app.run(args);
            }
        } else {
            System.out.println("Неправильный ввод");
        }
    }
}