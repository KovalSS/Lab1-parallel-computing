import java.io.File;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.Locale;

public class ParallelTest {
    public static volatile int preventOptimization;

    public static void main(String[] args) {
        System.out.println("=== STARTING PARALLEL BENCHMARK ===");

        try (PrintWriter writer = new PrintWriter(new File("parallel_results.csv"))) {
            writer.println("MatrixSize,Threads,Time_ms");

            System.out.println(">>> Warming up threads...");
            int warmN = 1000;
            int[][] wA = BenchmarkUtils.generateMatrix(warmN);
            int[][] wB = BenchmarkUtils.generateMatrix(warmN);
            int[][] wC = new int[warmN][warmN];
            int wThreadCount = 4;
            Thread[] wThreads = new Thread[wThreadCount];
            for(int i=0; i<wThreadCount; i++) {
                int id = i;
                wThreads[i] = new Thread(() -> {
                    BenchmarkUtils.parallelStrideAdd(wA, wB, wC, warmN, id, wThreadCount);
                });
                wThreads[i].start();
            }
            for(Thread t : wThreads) try { t.join(); } catch (InterruptedException e) {}
            System.out.println(">>> Warmup done.\n");

            for (int n : BenchmarkUtils.N) {
                int[][] A = BenchmarkUtils.generateMatrix(n);
                int[][] B = BenchmarkUtils.generateMatrix(n);
                int[][] C = new int[n][n];

                System.out.printf("Processing %dx%d:\n", n, n);

                for (int threadsCount : BenchmarkUtils.THREAD_COUNTS) {
                    double totalDuration = 0;

                    for (int r = 0; r < BenchmarkUtils.RUNS; r++) {
                        long startTime = System.nanoTime();

                        Thread[] threads = new Thread[threadsCount];
                        for (int i = 0; i < threadsCount; i++) {
                            int threadId = i;
                            threads[i] = new Thread(() -> {
                                BenchmarkUtils.parallelStrideAdd(A, B, C, n, threadId, threadsCount);
                            });
                            threads[i].start();
                        }
                        try {
                            for (Thread t : threads) t.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        long endTime = System.nanoTime();
                        totalDuration += (endTime - startTime) / 1_000_000.0;
                    }

                    preventOptimization = C[n - 1][n - 1];

                    double avgTime = totalDuration / BenchmarkUtils.RUNS;
                    System.out.printf(Locale.US, "   Threads: %d | Time: %.4f ms\n", threadsCount, avgTime);
                    BenchmarkUtils.saveResult(writer, n, threadsCount, avgTime);
                }
            }
            System.out.println("Done! Saved to parallel_results.csv");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}