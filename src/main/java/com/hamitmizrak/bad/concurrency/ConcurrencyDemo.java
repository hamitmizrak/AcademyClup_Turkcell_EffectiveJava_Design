package com.hamitmizrak.bad.concurrency;



import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ConcurrencyDemo {

    public static void runExecutorMess() {
        ExecutorService ex = Executors.newFixedThreadPool(64); // Kötü: gereksiz büyük
        List<Integer> shared = new ArrayList<>(); // Kötü: synchronized değil
        for (int i = 0; i < 10_000; i++) {
            int v = i;
            ex.submit(() -> shared.add(v)); // Kötü: yarış durumu
        }
        // Kötü: shutdown yok
        System.out.println("Executor bitti gibi (ama gerçekten değil). size=" + shared.size());
    }

    // Kötü: naive Fibonacci
    public static class FibTask extends RecursiveTask<Long> {
        private final int n;
        public FibTask(int n) { this.n = n; }
        @Override protected Long compute() {
            if (n <= 1) return (long) n;
            FibTask f1 = new FibTask(n - 1);
            f1.fork();
            FibTask f2 = new FibTask(n - 2);
            long r2 = f2.compute();
            long r1 = f1.join();
            return r1 + r2;
        }
    }

    public static void runForkJoinBad(int n) {
        ForkJoinPool pool = ForkJoinPool.commonPool(); // Kötü: kontrolsüz kullanım
        long res = pool.invoke(new FibTask(n));
        System.out.println("Fib(" + n + ")=" + res);
    }
}
