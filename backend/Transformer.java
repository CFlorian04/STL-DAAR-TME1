import java.util.ArrayList;
import java.util.List;

public class Transformer {

    Transformer() {
    }

    // Transformer l'arbre regex en NDFA avec un tableau de char[][]
    char[][] transformRegExTreeToNDFA(String regexTree) {

        Composer composer = new Composer();
        char root = regexTree.charAt(0);
        String gauche = null;
        int len = regexTree.length();;

        if ((root == '|' || root == '.') && len > 1) { //il faut aussi faire fonctionner avec (ab|cd)* et a.*
            // Trouver les indices des parenthèses et de la virgule
            System.out.println("Dans | ou . " + regexTree);
            regexTree = regexTree.substring(2, len - 1); //retirer le parenthese à la fin
            
            int indexParentheseOuvrante = regexTree.indexOf('(');
            int indexVirgule = regexTree.indexOf(','); //écraser s'il y a plusieurs virgules
            boolean leftIsSet = false;
            if (!regexTree.substring(0, indexVirgule).contains("(")){
                gauche = regexTree.substring(0,1);
                leftIsSet = true;
            }
            if (indexParentheseOuvrante != -1 && !leftIsSet) {
                int count = 0;
                for (int i = 0 ; i < regexTree.length(); i++) {
                    if (regexTree.charAt(i) == '(') {
                        count++;
                    }
                    if (regexTree.charAt(i) == ')') {
                        count--;
                        if (count == 0) {
                            indexVirgule = i+1;
                            break;
                        }
                    }
                }
            }

            // Extraire la partie gauche (entre '(' et ',')joy
            if (!leftIsSet){
                gauche = regexTree.substring(0, indexVirgule);
                System.out.println("gauche: "+ gauche);
            }
            // Extraire la partie droite (entre ',' et le dernier ')')
            String droite = regexTree.substring(indexVirgule+1, regexTree.length());
            
            if (root == '|') {
                System.out.println("Je suis rentré par '|' gauche: " + gauche + " droite: " + droite);
                return composer.altern(transformRegExTreeToNDFA(gauche), transformRegExTreeToNDFA(droite));
            } else if (root == '.') {
                System.out.println("Je suis rentré par '.' gauche: " + gauche + " droite: " + droite);
                return composer.concat(transformRegExTreeToNDFA(gauche), transformRegExTreeToNDFA(droite));
            }
        }
        // Check if there is an asterisk in the regexTree
        int indexOfAsterisk = regexTree.indexOf('*');
        System.out.println("indice" + root);// Étape 3 : Trouver la dernière parenthèse fermante
                                                       // correspondante
        if (root == '*') {
            len = regexTree.length();
            regexTree = regexTree.substring(2, len - 1);
            System.out.println("Dans *");
            System.out.println("Dedans "+ regexTree);
            return composer.star(transformRegExTreeToNDFA(regexTree));
        }
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
