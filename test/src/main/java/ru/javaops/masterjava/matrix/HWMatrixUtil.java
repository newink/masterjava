package ru.javaops.masterjava.matrix;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HWMatrixUtil {

    public static int[][] concurrentMultiply(int[][] matrixA, int[][] matrixB, ExecutorService executor) throws InterruptedException, ExecutionException {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        List<Callable<Void>> tasks = IntStream.range(0, matrixSize)
                .parallel()
                .mapToObj(i -> new Callable<Void>() {
                    private final int[] tempColumn = new int[matrixSize];

                    @Override
                    public Void call() throws Exception {
                        for (int j = 0; j < matrixSize; j++) {
                            tempColumn[j] = matrixB[j][i];
                        }

                        for (int c = 0; c < matrixSize; c++) {
                            int row[] = matrixA[c];
                            int sum = 0;
                            for (int j = 0; j < matrixSize; j++) {
                                sum += tempColumn[j] * row[j];
                            }
                            matrixC[c][i] = sum;
                        }
                        return null;
                    }
                }).collect(Collectors.toList());
        executor.invokeAll(tasks);
        return matrixC;
    }

    public static int[][] singleThreadMultiplyOpt(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        for (int col = 0; col < matrixSize; col++) {
            final int[] columnB = new int[matrixSize];
            for (int k = 0; k < matrixSize; k++) {
                columnB[k] = matrixB[k][col];
            }

            for (int row = 0; row < matrixSize; row++) {
                int sum = 0;
                final int[] rowA = matrixA[row];
                for (int k = 0; k < matrixSize; k++) {
                    sum += rowA[k] * columnB[k];
                }
                matrixC[row][col] = sum;
            }
        }
        return matrixC;
    }

    public static int[][] create(int size) {
        int[][] matrix = new int[size][size];
        Random rn = new Random();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = rn.nextInt(10);
            }
        }
        return matrix;
    }

    public static boolean compare(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                if (matrixA[i][j] != matrixB[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }
}
