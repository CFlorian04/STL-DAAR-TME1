public class Composer {

    Composer() {
    }

    char[][] concat(char[][] matrix1, char[][] matrix2) {
        int rows = matrix1.length + matrix2.length -1;
        int cols = matrix1[0].length + matrix2[0].length -1;
        int lengthRow1 = matrix1.length;
        int lengthCol1 = matrix1[0].length;
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

        result[lengthRow1 - 1][lengthCol1] = ' ';
        for (int i = 0; i < matrix2.length; i++) {
            for (int j = 1; j < matrix2[0].length; j++) {
                result[i + lengthRow1-1][j + lengthCol1-1] = matrix2[i][j];
            }
        }
        // for (int i = 0; i < matrix2.length; i++) {
        //     // Copier chaque ligne de matrix2 dans result à la position décalée
        //     System.arraycopy(matrix2[i], 0, result[i + lengthRow1], lengthCol1, matrix2[i].length);
        // }

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

        result[0][matrix1[0].length - 1] = ' ';
        result[matrix1.length - 2][1] = ' ';
        return result;
    }

    char[][] altern(char[][] matrix1, char[][] matrix2) {
        char[][] result = new char[matrix1.length + matrix2.length-2][matrix1[0].length + matrix2[0].length-2];
        int lengthRow1 = matrix1.length;
        int lengthCol1 = matrix1[0].length;
        // altern process
        // for (int i = 0; i < lengthRow1; i++) {
        //     for (int j = 0; j < lengthCol1; j++) {
        //         result[i][j] = matrix1[i][j];
        //     }
        // }
        for (int i = 0; i < matrix1.length; i++) {
            System.arraycopy(matrix1[i], 0, result[i], 0, matrix1[i].length);
        }

        //à modifier
        for (int i = 1; i < matrix2.length-1; i++) {
            for (int j = 1 ; j < matrix2[0].length-1; j++) {
                // if (i == lengthRow1 && j == lengthCol1) {
                    //     continue;
                    // }
                result[i + lengthRow1-1][j + lengthCol1-1] = matrix2[i][j];
            }
        }
        result[0][matrix1[0].length] = ' ';
        result[result.length-1][matrix1[0].length] = ' ';
        return result;
    }
}
