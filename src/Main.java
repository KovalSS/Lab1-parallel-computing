import java.io.File;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.Locale;

public class Main {
    static final int PHYSICAL_CORES = 8;
    static final int LOGICAL_CORES = 12;

    static final int RUNS = 50;

    static final int[] N = {50, 100, 500, 1000, 2000, 2500, 5000, 7500, 10000, 15000, 20000};
    static final int[] threadCounts = {
            1,
            PHYSICAL_CORES / 2,
            PHYSICAL_CORES,
            LOGICAL_CORES,
            LOGICAL_CORES * 2,
            LOGICAL_CORES * 4,
            LOGICAL_CORES * 8,
            LOGICAL_CORES * 16
    };

    public static int[][] generateMatrix(int n) {
        int[][] matrix = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                matrix[i][j] = 1;
            }
        }
        return matrix;
    }

    public static void main(String[] args) throws InterruptedException {
        try (PrintWriter writer = new PrintWriter(new File("results.csv"))) {
            writer.println("MatrixSize,Threads,Time_ms");
            System.out.println("Start benchmarking with " + RUNS + " runs average...");

            for (int n : N) {
                int[][] A = generateMatrix(n);
                int[][] B = generateMatrix(n);
                int[][] C = new int[n][n];

                System.out.printf("Processing Matrix Size: %dx%d\n", n, n);

                for (int threadCount : threadCounts) {

                    double totalDuration = 0;
                    for (int r = 0; r < RUNS; r++) {
                        long startTime = System.nanoTime();

                        Thread[] threads = new Thread[threadCount];
                        for (int i = 0; i < threadCount; i++) {
                            int threadId = i;
                            threads[i] = new Thread(() -> {
                                for (int row = threadId; row < n; row += threadCount) {
                                    for (int col = 0; col < n; col++) {
                                        C[row][col] = A[row][col] + B[row][col];
                                    }
                                }
                            });
                            threads[i].start();
                        }
                        for (Thread t : threads) t.join();

                        long endTime = System.nanoTime();
                        totalDuration += (endTime - startTime) / 1000000.0;
                    }

                    double avgDuration = totalDuration / RUNS;

                    System.out.printf(Locale.US, "Threads: %d | Avg Time: %.4f ms\n", threadCount, avgDuration);
                    writer.printf(Locale.US, "%d,%d,%.4f\n", n, threadCount, avgDuration);
                    writer.flush();
                }
            }
            System.out.println("Done! Results saved to 'results.csv'");

        } catch (IOException e) {
            System.err.println("Error writing to CSV file: " + e.getMessage());
        }
    }
}