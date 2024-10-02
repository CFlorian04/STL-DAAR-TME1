import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class Transformer {

    char empty_char, epsilon;

    Transformer(char p_empty_char, char p_epsilon) {
        empty_char = p_empty_char;
        epsilon = p_epsilon;
    }

    // Transformer l'arbre regex en NDFA avec un tableau de char[][]
    char[][] transformRegExTreeToNDFA(String regexTree) {

        Composer composer = new Composer(epsilon);
        char root = regexTree.charAt(0);
        String gauche = null;
        int len = regexTree.length();

        if ((root == '|' || root == '.') && len > 1) { //il faut aussi faire fonctionner avec (ab|cd)* et a.*
            // Trouver les indices des parenthèses et de la virgule
            // System.out.println("Dans | ou . " + regexTree);
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
                // System.out.println("gauche: "+ gauche);
            }
            // Extraire la partie droite (entre ',' et le dernier ')')
            String droite = regexTree.substring(indexVirgule+1, regexTree.length());
            
            if (root == '|') {
                // System.out.println("Je suis rentré par '|' gauche: " + gauche + " droite: " + droite);
                return composer.altern(transformRegExTreeToNDFA(gauche), transformRegExTreeToNDFA(droite));
            } else if (root == '.') {
                // System.out.println("Je suis rentré par '.' gauche: " + gauche + " droite: " + droite);
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
            // System.out.println("Dans *");
            // System.out.println("Dedans "+ regexTree);
            return composer.star(transformRegExTreeToNDFA(regexTree));
        }
        return transformLetterToCharArray(root);
    }

    // Transformer le NDFA en DFA avec un tableau de char[][]
    HashMap<String, HashMap<String, String>> transformNDFAToDFA(char[][] ndfa, HashMap<String, Boolean> startState, HashMap<String, Boolean> finalState) {

        HashMap<String, HashMap<String, String>> dfa = new HashMap<>();
        Queue<Integer> successorList;
        HashSet<Integer> visited; //   rapide et indice unique
        List<Character> letters = extractKeys(ndfa); //letters are the HashMap's keys 
        boolean letterFound;
        boolean beginToSearch;
        String currentState;
        StringBuilder result;
        //findFinal State
        int acceptedState = findFinalStates(ndfa);
        System.out.println("Etats d'acceptation : "+ acceptedState);
        //Trouver les etats de depart
        Queue<String> departState = new LinkedList<>();
        StringBuilder state = new StringBuilder(); //Trouver les etats du depart 
        visited = new HashSet<>();
        successorList = new LinkedList<>();
        successorList.add(0);
        while (!successorList.isEmpty()) {
            int indice = successorList.poll();
            visited.add(indice);
            for (int j = 0; j < ndfa.length; j++) {
                if(ndfa[indice][j]==epsilon) {
                    if (!visited.contains(j)) { //si j n'est pas dans la liste de successeur, on l'ajoute
                        successorList.add(j);
                        visited.add(j);
                    }
                }
            }
        }

        // Convertir chaque entier en String et ajouter
        for (Integer elem : visited) {
            if (state.length() > 0) {
                state.append(","); // Ajouter une virgule avant chaque élément, sauf le premier
            }
            state.append(elem.toString());
        }
        String start = state.toString();
        departState.add(start);// séparer les indices des states par des virgules.
        startState.put(start, true);

        // construction de la Dfa
        while (!departState.isEmpty()) {
            currentState = departState.poll();
            boolean isFinalStateSet = false;
            dfa.put(currentState, createMapWithKeys(letters));
            for (char letter : letters) {
                String[] elements = currentState.split(",");
                for (String element : elements) {
                    int index = Integer.parseInt(element.trim()); // Convertit le String en entier
                    // System.out.println("lettre apres while :"+letter);
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
                            if ((ndfa[index][j] == letter) || (letterFound && ndfa[index][j] == epsilon)) {
                                //si on trouve une lettre, on stock dans une liste les indices des successeurs à visiter
                                letterFound = true;
                                if (!visited.contains(j)) { //si j n'est pas dans la liste de successeur, on l'ajoute
                                    successorList.add(j);
                                    visited.add(j);
                                }
                                //index=j; // a mediter
                            }
                        }





                        //il faut bien definir l'etat final 


                        if (visited.contains(acceptedState)){
                            finalState.put(currentState, true);
                            isFinalStateSet = true;
                        }
                    }
                    if (!visited.isEmpty()){
                        result = new StringBuilder();
                        for (Integer elem : visited) {
                            if (result.length() > 0) {
                                result.append(","); // Ajouter une virgule avant chaque élément, sauf le premier
                            }
                            result.append(elem.toString());
                        }
                        dfa.get(currentState).put(""+letter, result.toString()); // a verifier
                        if (!result.toString().equals(currentState)) { // ne pas avoir une redondance dans le current state
                            departState.add(result.toString()); // il faut qu'a un moment donnée, on n'ajoute plus
                        }
                    }

                    if (!isFinalStateSet) {
                        finalState.put(currentState, false);
                    }
                    
                    if (!currentState.equals(start)){
                        startState.put(currentState, false);
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
                if (c != epsilon && c != empty_char) { // Ignorer les transitions vides ou invalides
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
        array[0][1] = epsilon; // Space would be like epsilon
        array[1][2] = letter;
        array[2][3] = epsilon;
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
    public void afficherState(HashMap<String, Boolean> state) {
        // Parcours et affichage du contenu du HashMap
        for (Map.Entry<String, Boolean> entry : state.entrySet()) {
            System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
        }
    }

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
