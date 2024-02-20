package com.nikola.userhandlerbe2.utils;

import java.util.Date;

public class Logger {
    public static void log(String message) {
        System.out.println("-------------------------");
        System.out.println(new Date() + ": " + message);
        System.out.println("-------------------------");
    }
}
