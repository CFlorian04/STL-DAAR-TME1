import java.util.*;

public class Transformer {

    char empty_char, epsilon;

    Set<Character> alphabet;

    Transformer(char p_empty_char, char p_epsilon) {
        empty_char = p_empty_char;
        epsilon = p_epsilon;
    }

    // Transformer l'arbre regex en NDFA avec un tableau de char[][]
    char[][] transformRegExTreeToNDFA(String regexTree) {

        Composer composer = new Composer(empty_char, epsilon);
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
                return composer.altern(transformRegExTreeToNDFA(gauche), transformRegExTreeToNDFA(droite));
            } else if (root == '.') {
                return composer.concat(transformRegExTreeToNDFA(gauche), transformRegExTreeToNDFA(droite));
            }
        }
        // Check if there is an asterisk in the regexTree
        int indexOfAsterisk = regexTree.indexOf('*');

        if (indexOfAsterisk != -1) {
            int indexOfOpeningParenthesis = regexTree.indexOf('(', indexOfAsterisk);

            if (indexOfOpeningParenthesis != -1) {
                int indexOfClosingParenthesis = regexTree.indexOf(')', indexOfOpeningParenthesis);

                if (indexOfClosingParenthesis != -1) {
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
        return transformLetterToCharArray(root);
    }

    // Transformer une lettre en tableau de char[][]
    char[][] transformLetterToCharArray(char letter) {
        char[][] array = new char[4][4];
        array[0][1] = epsilon;
        array[1][2] = letter;
        array[2][3] = epsilon;
        return array;
    }

    // Conversion NDFA vers DFA
    char[][] transformNDFAtoDFA(char[][] ndfa) {
        alphabet = NDFAgetAlphabet(ndfa);

        return null;
    }

    // Obtenir l'alphabet du NDFA
    Set<Character> NDFAgetAlphabet(char[][] ndfa) {
        Set<Character> alphabet = new HashSet<>();
        for (int i = 0; i < ndfa.length; i++) {
            for (int j = 0; j < ndfa[i].length; j++) {
                char symbol = ndfa[i][j];
                if (symbol != empty_char && symbol != epsilon) {
                    alphabet.add(symbol);
                }
            }
        }
        System.out.println("Alphabet du NDFA : " + alphabet);
        return alphabet;
    }

    // Afficher la matrice
    void displayMatrix(char[][] matrice) {
        for (char[] ligne : matrice) {
            for (char caractere : ligne) {
                System.out.print((caractere != '\u0000' ? caractere : '-') + " ");
            }
            System.out.println();
        }
    }

}
