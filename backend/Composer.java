public class Composer {

    char empty_char, epsilon;

    Composer(char p_empty_char, char p_epsilon) {
        empty_char = p_empty_char;
        epsilon = p_epsilon;
    }

    char[][] concat(char[][] matrix1, char[][] matrix2) {
        int rows = matrix1.length + matrix2.length;
        int cols = matrix1[0].length + matrix2[0].length;
        int lengthRow1 = matrix1.length;
        int lengthCol1 = matrix1[0].length;
        char[][] result = new char[rows][cols];

        // concat process
        for (int i = 0; i < matrix1.length; i++) {
            for (int j = 0; j < matrix1[0].length; j++) {
                result[i][j] = matrix1[i][j];
            }
        }
        result[lengthRow1 - 1][lengthCol1] = epsilon;
        for (int i = 0; i < matrix2.length; i++) {
            for (int j = 0; j < matrix2[0].length; j++) {
                result[i + lengthRow1][j + lengthCol1] = matrix2[i][j];
            }
        }

        return result;
    }

    char[][] star(char[][] matrix1) {
        char[][] result = new char[matrix1.length][matrix1[0].length];
        for (int i = 0; i < matrix1.length; i++) {
            for (int j = 0; j < matrix1[0].length; j++) {
                result[i][j] = matrix1[i][j];
            }
        }
        result[0][matrix1[0].length - 1] = epsilon;
        result[matrix1.length - 2][1] = epsilon;
        return result;
    }

    char[][] altern(char[][] matrix1, char[][] matrix2) {
        char[][] result = new char[matrix1.length + matrix2.length][matrix1[0].length + matrix2[0].length];
        int lengthRow1 = matrix1.length;
        int lengthCol1 = matrix1[0].length;
        // altern process
        for (int i = 0; i < lengthRow1; i++) {
            for (int j = 0; j < lengthCol1; j++) {
                result[i][j] = matrix1[i][j];
            }
        }
        result[0][lengthCol1] = epsilon;
        result[lengthRow1 - 1][matrix1[0].length + matrix2[0].length - 1] = epsilon;
        for (int i = 0; i < matrix2.length; i++) {
            for (int j = 0; j < matrix2[0].length; j++) {
                if (i == lengthRow1 && j == lengthCol1) {
                    continue;
                }
                result[i + lengthRow1][j + lengthCol1] = matrix2[i][j];
            }
        }

        return result;
    }
}
