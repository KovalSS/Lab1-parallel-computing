import java.util.Locale;

class Main {
    static final int PHYSICAL_CORES = 8;
    static final int LOGICAL_CORES = 12;
    static final int[] N = {100,500,1000,2000,5000,10000,20000};
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
        for (int n : N) {
            int[][] A = generateMatrix(n);
            int[][] B = generateMatrix(n);
            int[][] C = new int[n][n];
            System.out.printf("Matrix Size: %dx%d\n", n, n);
            for (int threadCount : threadCounts) {
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
                double durationMs = (endTime - startTime) / 1000000.0;
                System.out.printf(Locale.US,"Threads: %d | Time: %.4f ms\n", threadCount, durationMs);            }
        }

    }
}