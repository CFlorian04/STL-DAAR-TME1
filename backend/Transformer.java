import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

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
        // int indexOfAsterisk = regexTree.indexOf('*');
        // System.out.println("indice" + root);// Étape 3 : Trouver la dernière parenthèse fermante
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
    HashMap<String, HashMap<String, String>> transformNDFAToDFA(char[][] ndfa, int n) {

        HashMap<String, HashMap<String, String>> dfa = new HashMap<>();
        HashMap<String, Boolean> startState;
        HashMap<String, Boolean> finalState;
        //Qui sont les lettres qui vont devenir des keys -> on parcourt le tableau de char et si c'est different de ' ' et \u0000, c'en est une
        Queue<Integer> successorList;
        HashSet<Integer> visited; //   rapide et indice unique

        List<String> letters = extractKeys(ndfa); //letters are the HashMap's keys 

        //trouver les etats de depart
        String state = "0";//Depart
        for (int j = 0; j < ndfa[0].length; j++) {
            if (ndfa[0][j] == ' '){
                state += j;
            }
        }
        //boucle à definir
        boolean redondance = false;//à definir
        Queue<String> departState = new LinkedList<>();
        departState.add(state);
        while (!departState.isEmpty()) { //autre condition à ajouter
            String currentState = departState.poll();
            HashMap<String, String> dfa_rows = createMapWithKeys(letters);//je peux inserer directement les valeurs aussi (peut etre)
            dfa.put(currentState, dfa_rows);
            for (char c : currentState.toCharArray()) {
                int index = c - '0'; // Convertit le char en entier
                successorList = new LinkedList<>();
                visited = new HashSet<>();
                boolean letterFound = false;
                char letter='\u0000';


                //!!!Il faut que la lettre à rechercher soit les keys parcourues une à une; à chaque lettre ses visited state

                boolean start = true;
                while (!successorList.isEmpty() || start) { 
                    start = false;
                    if (!start) {
                        index = successorList.poll();
                    } 
                    for (int j = 0; j < ndfa[index].length; j++) {
                        //si on trouve des lettres autres que ' ' et \u0000, on trouve ses successeurs si le chemin € existe
                        if ((!letterFound && !(ndfa[index][j] == ' ' && ndfa[index][j] == '\u0000')) || (letterFound && ndfa[index][j] == ' ')) {
                            //si on trouve une lettre, on stock dans une liste les indices des successeurs à visiter
                            letterFound = true;
                            if (!visited.contains(j)) { //si j n'est pas dans la liste de successeur, on l'ajoute
                                letter = ndfa[index][j];
                                successorList.add(j);
                                visited.add(j);
                            }
                        }
                    }
                }
                StringBuilder result = new StringBuilder();
                for (Integer number : visited) {
                    result.append(number.toString()); // Convertir chaque entier en String et ajouter
                }
                dfa.get(state).put(letter+"", result.toString()); // a verifier
                departState.add(result.toString());
            }
        }
        return dfa;
    }


    List<String> extractKeys(char[][] ndfa) {
        List<String> keys = new ArrayList<>();
        
        for (char[] ndfaRows : ndfa) {
            for (char elem : ndfaRows) {
                // Vérifier que l'élément est valide (ni espace ni caractère null)
                if (elem != ' ' && elem != '\u0000') {
                    String key = String.valueOf(elem);
                    if (!keys.contains(key)) { // Éviter les doublons
                        keys.add(key);
                    }
                }
            }
        }
        return keys;
    }

    // Méthode pour créer un HashMap avec les clés réutilisables
    public static HashMap<String, String> createMapWithKeys(List<String> keys) {
        HashMap<String, String> map = new HashMap<>();
        
        // Initialiser chaque clé avec une valeur par défaut (ou autre logique)
        for (String key : keys) {
            map.put(key, ""); // Valeur par défaut
        }
        
        return map;
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
