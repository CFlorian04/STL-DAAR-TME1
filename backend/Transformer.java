import java.util.ArrayList;
import java.util.List;

public class Transformer {

    Transformer() {
    }

    // Transformer l'arbre regex en NDFA avec un tableau de char[][]
    char[][] transformRegExTreeToNDFA(String regexTree) {

        Composer composer = new Composer();
        char root = regexTree.charAt(0);

        if (root == '|' || root == '.') {
            // Trouver les indices des parenthèses et de la virgule
            int indexParentheseOuvrante = regexTree.indexOf('(');
            int indexVirgule = regexTree.indexOf(',');
            int indexParentheseFermante = regexTree.lastIndexOf(')');

            // Extraire la partie gauche (entre '(' et ',')
            String gauche = regexTree.substring(indexParentheseOuvrante + 1, indexVirgule);

            // Extraire la partie droite (entre ',' et le dernier ')')
            String droite = regexTree.substring(indexVirgule + 1, indexParentheseFermante);

            if (root == '|') {
                System.out.println("Je suis rentré ici; gauche: " + gauche + " droite: " + droite);
                return composer.altern(transformRegExTreeToNDFA(gauche), transformRegExTreeToNDFA(droite));
            } else if (root == '.') {
                System.out.println("Je suis rentré par la");
                return composer.concat(transformRegExTreeToNDFA(gauche), transformRegExTreeToNDFA(droite));
            }
        }
        // Check if there is an asterisk in the regexTree
        int indexOfAsterisk = regexTree.indexOf('*');
        System.out.println("indice" + indexOfAsterisk);// Étape 3 : Trouver la dernière parenthèse fermante
                                                       // correspondante

        if (indexOfAsterisk != -1) {
            // Étape 2 : Trouver la première parenthèse ouvrante après *
            int indexOfOpeningParenthesis = regexTree.indexOf('(', indexOfAsterisk);

            if (indexOfOpeningParenthesis != -1) {
                int indexOfClosingParenthesis = regexTree.indexOf(')', indexOfOpeningParenthesis);

                if (indexOfClosingParenthesis != -1) {
                    // Étape 4 : Extraire le texte entre les parenthèses
                    String valeur = regexTree.substring(indexOfOpeningParenthesis + 1, indexOfClosingParenthesis)
                            .trim();
                    return composer.star(transformRegExTreeToNDFA(valeur));
                } else {
                    System.out.println("Parenthèse fermante non trouvée.");
                }
            } else {
                System.out.println("Parenthèse ouvrante non trouvée après *.");
            }
        }
        // if (!regexTree.contains("("))
        return transformLetterToCharArray(root);
    }

    // Transformer le NDFA en DFA avec un tableau de char[][]
    char[][] transformNDFAToDFA(char[][] ndfa, int n) {
        char[][] dfa = new char[n][n]; // Tableau de char en 2 dimensions
        return dfa;
    }

    // Transformer le NDFA en DFA minimisé avec un tableau de char[][]
    char[][] transformRegExTreeToMinimizedDFA(char[][] ndfa, int n) {
        char[][] minimizedDFA = new char[n][n]; // Tableau de char en 2 dimensions
        return minimizedDFA;
    }

    // Transformer une lettre en tableau de char[][]
    char[][] transformLetterToCharArray(char letter) {
        char[][] array = new char[4][4];
        array[0][1] = 'E'; // Space would be like epsilon
        array[1][2] = letter;
        array[2][3] = 'E';
        return array;
    }

    // Afficher la matrice
    void displayMatrix(char[][] matrice) {
        for (char[] ligne : matrice) {
            for (char caractere : ligne) {
                System.out.print(caractere + " ");
            }
            System.out.println();
        }
    }

    // Trouver les sommets états finals
    int[] findFinalStates(char[][] matrix) {

        List<Integer> finalStatesList = new ArrayList<>();
        for (int i = 0; i < matrix.length; i++) {
            // test if the default value in char[][] is modified in the current row
            boolean modified = false;
            for (int j = 0; j < matrix[i].length; j++) {
                if (matrix[i][j] != '\u0000') {
                    modified = true;
                    break;
                }
            }
            if (!modified) {
                finalStatesList.add(i);
            }
        }
        return finalStatesList.stream().mapToInt(Integer::intValue).toArray();
    }
}
