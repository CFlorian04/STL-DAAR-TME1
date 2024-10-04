public class Composer {

    char empty_char, epsilon;

    Composer(char p_epsilon) {
        epsilon = p_epsilon;
    }
    

    //à verifier
    char[][] concat(char[][] matrix1, char[][] matrix2) {
        int rows = matrix1.length + matrix2.length;
        int cols = matrix1[0].length + matrix2[0].length;
        int length1 = matrix1.length;
        char[][] result = new char[rows][cols];

        // concat process
        // for (int i = 0; i < matrix1.length; i++) {
        //     for (int j = 0; j < matrix1[0].length; j++) {
        //         result[i][j] = matrix1[i][j];
        //     }
        // }
        for (int i = 0; i < matrix1.length-1; i++) {
            System.arraycopy(matrix1[i], 0, result[i], 0, matrix1[i].length);
        }

        // for (int i = 0; i < matrix2.length; i++) {
        //     for (int j = 0; j < matrix2[0].length; j++) {
        //         result[i + length1][j + length1] = matrix2[i][j];
        //     }
        // }
        for (int i = 0; i < matrix2.length; i++) {
            // Copier chaque ligne de matrix2 dans result à la position décalée
            System.arraycopy(matrix2[i], 0, result[i + length1], length1, matrix2[i].length);
        }
        result[length1 - 1][length1] = epsilon;

        return result;
    }

    char[][] star(char[][] matrix1) {
        char[][] result = new char[matrix1.length][matrix1[0].length];
        // for (int i = 0; i < matrix1.length; i++) {
        //     for (int j = 0; j < matrix1[0].length; j++) {
        //         result[i][j] = matrix1[i][j];
        //     }
        // }
        for (int i = 0; i < matrix1.length; i++) {
            System.arraycopy(matrix1[i], 0, result[i], 0, matrix1[i].length);
        }

        result[0][matrix1[0].length - 1] = epsilon;
        result[matrix1.length - 2][1] = epsilon;
        return result;
    }

    char[][] altern(char[][] matrix1, char[][] matrix2) {
        int rows = matrix1.length + matrix2.length +1;
        int cols = matrix1[0].length + matrix2[0].length +1;
        int lengthRow1 = matrix1.length;
        int lengthCol1 = matrix1[0].length;
        char[][] result = new char[rows+1][cols+1];

        // concat process
        for (int i = 0; i < matrix1.length; i++) {
            for (int j = 0; j < matrix1[0].length; j++) {
                result[i+1][j+1] = matrix1[i][j];
            }
        }
        // for (int i = 0; i < matrix1.length-1; i++) {
        //     System.arraycopy(matrix1[i], 0, result[i], 0, matrix1[i].length);
        // }
        for (int i = 0; i < matrix2.length; i++) {
            for (int j = 0; j < matrix2[0].length; j++) {
                result[(i+lengthRow1+2)][(j+lengthCol1+2)] = matrix2[i][j];
            }
        }
        result[0][1] = epsilon;
        result[0][lengthCol1+2] = epsilon;
        result[lengthRow1][lengthCol1+1] = epsilon;
        result[lengthRow1 + matrix2.length +1 ][lengthCol1+1] = epsilon;
        return result;
    }
}
