import java.io.File;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.Locale;

public class SequentialTest {
    public static volatile int preventOptimization;

    public static void main(String[] args) {

        int demoN = 5;
        int[][] demoA = BenchmarkUtils.generateMatrix(demoN);
        int[][] demoB = BenchmarkUtils.generateMatrix(demoN);
        int[][] demoC = new int[demoN][demoN];
        BenchmarkUtils.sequentialAdd(demoA, demoB, demoC, demoN);
        System.out.println("Matrix A:");
        BenchmarkUtils.printMatrixSample(demoA, demoN);
        System.out.println("Matrix B:");
        BenchmarkUtils.printMatrixSample(demoB, demoN);
        System.out.println("Result C = A + B:");
        BenchmarkUtils.printMatrixSample(demoC, demoN);

        System.out.println("=== STARTING SEQUENTIAL BENCHMARK ===");
        try (PrintWriter writer = new PrintWriter(new File("sequential_results.csv"))) {
            writer.println("MatrixSize,Threads,Time_ms");

            System.out.println(">>> Warming up...");
            int warmN = 1000;
            int[][] wA = BenchmarkUtils.generateMatrix(warmN);
            int[][] wB = BenchmarkUtils.generateMatrix(warmN);
            int[][] wC = new int[warmN][warmN];
            for (int k = 0; k < 1000; k++) {
                BenchmarkUtils.sequentialAdd(wA, wB, wC, warmN);
            }
            System.out.println(">>> Warmup done.\n");

            for (int n : BenchmarkUtils.N) {
                int[][] A = BenchmarkUtils.generateMatrix(n);
                int[][] B = BenchmarkUtils.generateMatrix(n);
                int[][] C = new int[n][n];

                double totalDuration = 0;

                System.out.printf("Processing %dx%d... ", n, n);

                for (int r = 0; r < BenchmarkUtils.RUNS; r++) {
                    long startTime = System.nanoTime();

                    BenchmarkUtils.sequentialAdd(A, B, C, n);

                    long endTime = System.nanoTime();
                    totalDuration += (endTime - startTime) / 1_000_000.0;
                }

                preventOptimization = C[n - 1][n - 1];

                double avgTime = totalDuration / BenchmarkUtils.RUNS;
                System.out.printf(Locale.US, "Time: %.4f ms\n", avgTime);

                BenchmarkUtils.saveResult(writer, n, 1, avgTime);
            }
            System.out.println("Done! Saved to sequential_results.csv");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}