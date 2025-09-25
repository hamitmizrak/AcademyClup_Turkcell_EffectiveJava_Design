package com.hamitmizrak.bad.util;


import java.io.IOException;
import java.nio.file.*;

public class BadLogger {
    // Kötü: basit dosya append; eşzamanlı erişimde bozulur
    public static void log(String msg) {
        try {
            Path p = Path.of(System.getProperty("user.home"), "hospital-bad.log");
            Files.writeString(p, msg + System.lineSeparator(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException ignored) {}
    }
}
