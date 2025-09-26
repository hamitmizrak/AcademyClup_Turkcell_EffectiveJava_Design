package com.hamitmizrak.bad.util;


import java.util.concurrent.atomic.AtomicInteger;

public class ThreadStuff {

    // Kötü: gereksiz global sayaç
    public static AtomicInteger counter = new AtomicInteger();

    public static class ReminderThread extends Thread {
        @Override public void run() {
            while (true) {
                // Kötü: busy loop + sleep
                try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
                counter.incrementAndGet();
                // Kötü: System.out ile “log”
                System.out.println("Reminder tick " + counter.get());
            }
        }
    }
}
