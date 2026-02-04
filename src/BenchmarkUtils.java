import java.io.File;
import java.io.PrintWriter;
import java.io.IOException;

public class BenchmarkUtils {
    public static final int PHYSICAL_CORES = 8;
    public static final int LOGICAL_CORES = 12;
    public static final int RUNS = 5;

    public static final int[] N = {
            50, 100, 500, 1000, 2000, 2500, 5000, 7500, 10000, 15000, 20000
    };

    public static final int[] THREAD_COUNTS = {
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

    public static void printMatrixSample(int[][] matrix, int size) {
        int limit = Math.min(size, 5);
        for (int i = 0; i < limit; i++) {
            for (int j = 0; j < limit; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println("...");
    }

    public static void saveResult(PrintWriter writer, int size, int threads, double time) {
        if (writer != null) {
            writer.printf(java.util.Locale.US, "%d,%d,%.4f\n", size, threads, time);
            writer.flush();
        }
    }
    public static void sequentialAdd(int[][] A, int[][] B, int[][] C, int n) {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                C[i][j] = A[i][j] + B[i][j];
            }
        }
    }
    public static void parallelStrideAdd(int[][] A, int[][] B, int[][] C, int n, int threadId, int threadCount) {
        for (int row = threadId; row < n; row += threadCount) {
            for (int col = 0; col < n; col++) {
                C[row][col] = A[row][col] + B[row][col];
            }
        }
    }
}