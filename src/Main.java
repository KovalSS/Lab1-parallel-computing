
class Main {
    public static int[][] generateMatrix(int n) {
        int[][] matrix = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                matrix[i][j] = 1;
            }
        }
        return matrix;
    }
    public static void main(String[] args) {
        int[] N = {100,500,1000,2000,5000,10000,20000,30000};
        for (int n : N) {

            long startTime = System.nanoTime();
            int[][] A = generateMatrix(n);
            long endTime = System.nanoTime();
            double durationMs = (endTime - startTime) / 1000000.0;
            System.out.println("Time taken to generate " + n + "x" + n + " matrix: " + durationMs + " ms");
        }

    }
}