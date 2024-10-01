import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
    HashMap<String, HashMap<String, String>> transformNDFAToDFA(char[][] ndfa) {

        HashMap<String, HashMap<String, String>> dfa = new HashMap<>();
        HashMap<String, Boolean> startState = new HashMap<>();
        HashMap<String, Boolean> finalState = new HashMap<>();
        Queue<Integer> successorList;
        HashSet<Integer> visited; //   rapide et indice unique
        List<Character> letters = extractKeys(ndfa); //letters are the HashMap's keys 
        boolean letterFound;
        boolean beginToSearch;
        String currentState;
        StringBuilder result;
        //findFinal State
        int acceptedState = findFinalStates(ndfa);

        //trouver les etats de depart
        Queue<String> departState = new LinkedList<>();
        String state = "0";//Depart
        for (int j = 0; j < ndfa[0].length; j++) {
            if (ndfa[0][j] == ' '){
                state += j;
            }
        }
        departState.add(state);

        while (!departState.isEmpty()) { //autre condition à ajouter
            currentState = departState.poll();
            //je peux inserer directement les valeurs aussi (peut etre)
            dfa.put(currentState, createMapWithKeys(letters));
            for (char letter : letters) {
                for (char c : currentState.toCharArray()) {
                    int index = c - '0'; // Convertit le char en entier
                
                    System.out.println("lettre apres while :"+letter);
                    successorList = new LinkedList<>();
                    visited = new HashSet<>();
                    letterFound = false;
                    //!!!Il faut que la lettre à rechercher soit les keys parcourues une à une; à chaque lettre ses visited state
                    beginToSearch = true;
                    while (!successorList.isEmpty() || beginToSearch) { 
                        if (!beginToSearch) {
                            index = successorList.poll();
                        } 
                        beginToSearch = false;
                        for (int j = 0; j < ndfa[index].length; j++) { // parcourir les colonnes de ndfa
                            //si on trouve des lettres autres que ' ' et \u0000, on trouve ses successeurs si le chemin € existe
                            if ((ndfa[index][j] == letter) || (letterFound && ndfa[index][j] == ' ')) {
                                //si on trouve une lettre, on stock dans une liste les indices des successeurs à visiter
                                letterFound = true;
                                if (!visited.contains(j)) { //si j n'est pas dans la liste de successeur, on l'ajoute
                                    successorList.add(j);
                                    visited.add(j);
                                }
                                index=j;
                            }
                        }
                    }

                    if (!visited.isEmpty()){
                        result = new StringBuilder();
                        for (Integer elem : visited) {
                            result.append(elem.toString()); // Convertir chaque entier en String et ajouter
                        }
                        System.out.println("letter: "+letter+" <-ajouter");
                        dfa.get(currentState).put(""+letter, result.toString()); // a verifier
                        if (!result.toString().equals(currentState)) { // ne pas avoir une redondance dans le current state
                            departState.add(result.toString()); // il faut qu'a un moment donnée, on n'ajoute plus
                        }
                    }

                    if (currentState.contains(acceptedState+"")) {
                        finalState.put(currentState, true);
                    } else {
                        finalState.put(currentState, false);
                    }
                    boolean alreadySet = false;
                    for (String element : departState) {
                        if (currentState.contains(element) && !alreadySet) {
                            startState.put(currentState, true);
                            alreadySet = true;
                        } else {
                            startState.put(currentState, false);
                        }
                    }
                }
            }
        }
        return dfa;
    }


    List<Character> extractKeys(char[][] ndfa) {
        HashSet<Character> letters = new HashSet<>();
        for (char[] row : ndfa) {
            for (char c : row) {
                if (c != ' ' && c != '\u0000') { // Ignorer les transitions vides ou invalides
                    letters.add(c);
                    System.out.println("key:"+c);
                }
            }
        }
        return new ArrayList<>(letters);
    }

    // Méthode pour créer un HashMap avec les clés réutilisables
    public static HashMap<String, String> createMapWithKeys(List<Character> keys) {
        HashMap<String, String> map = new HashMap<>();

        // Initialiser chaque clé avec une valeur par défaut (ou autre logique)
        for (Character key : keys) {
            map.put(key.toString(), ""); // Valeur par défaut
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
        array[0][1] = ' '; // Space would be like epsilon
        array[1][2] = letter;
        array[2][3] = ' ';
        return array;
    }

    // Afficher la matrice
    void displayMatrix(char[][] matrice) {
        for (char[] ligne : matrice) {
            for (char caractere : ligne) {
                if (caractere == ' ') {
                    System.out.print("E");
                } else {
                    System.out.print(caractere + " ");
                }
            }
            System.out.println();
        }
    }

    // Trouver les sommets états finals
    int findFinalStates(char[][] matrix) {

        int acceptedState = 0;
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
                acceptedState = i;
            }
        }
        return acceptedState;
    }

    // Fonction pour afficher le contenu du HashMap
    public void afficherHashMap(HashMap<String, HashMap<String, String>> map) {
        // Parcourir le HashMap externe
        for (Map.Entry<String, HashMap<String, String>> outerEntry : map.entrySet()) {
            String outerKey = outerEntry.getKey();
            HashMap<String, String> innerMap = outerEntry.getValue();

            System.out.println("Clé du HashMap externe: " + outerKey);

            // Parcourir le HashMap interne
            for (Map.Entry<String, String> innerEntry : innerMap.entrySet()) {
                String innerKey = innerEntry.getKey();
                String innerValue = innerEntry.getValue();
                System.out.println("  Clé interne: " + innerKey + ", Valeur interne: " + innerValue);
            }
        }
    }
}
