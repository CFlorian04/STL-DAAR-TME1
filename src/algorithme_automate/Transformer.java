package algorithme_automate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class Transformer {

    char empty_char, epsilon;

    Transformer(char p_empty_char, char p_epsilon) {
        empty_char = p_empty_char;
        epsilon = p_epsilon;
    }

    // Transform the regex tree into NDFA using a char[][] array
    char[][] transformRegExTreeToNDFA(String regexTree) {

        Composer composer = new Composer(epsilon);
        char root = regexTree.charAt(0);
        String gauche = null;
        int len = regexTree.length();

        if ((root == '|' || root == '.') && len > 1) {

            regexTree = regexTree.substring(2, len - 1); // Remove the parenthesis at the end
            
            int indexParentheseOuvrante = regexTree.indexOf('(');
            int indexVirgule = regexTree.indexOf(','); // Overwrite if there are multiple commas
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
            if (!leftIsSet){
                gauche = regexTree.substring(0, indexVirgule);
            }
            String droite = regexTree.substring(indexVirgule+1, regexTree.length());
            
            if (root == '|') {
                return composer.altern(transformRegExTreeToNDFA(gauche), transformRegExTreeToNDFA(droite));
            } else if (root == '.') {
                return composer.concat(transformRegExTreeToNDFA(gauche), transformRegExTreeToNDFA(droite));
            }
        }
        if (root == '*') {
            len = regexTree.length();
            regexTree = regexTree.substring(2, len - 1);
            return composer.star(transformRegExTreeToNDFA(regexTree));
        }
        return transformLetterToCharArray(root);
    }

    // Transform the NDFA into DFA
    HashMap<String, HashMap<String, String>> transformNDFAToDFA(char[][] ndfa, HashMap<String, Boolean> startState, HashMap<String, Boolean> finalState) {

        HashMap<String, HashMap<String, String>> dfa = new HashMap<>();
        Queue<Integer> successorList;
        HashSet<Integer> visited; //  // Fast and unique index
        List<Character> letters = extractKeys(ndfa); //letters are the HashMap's keys 
        boolean letterFound;
        boolean beginToSearch;
        String currentState;
        StringBuilder result;
        //findFinal State
        int acceptedState = findFinalStates(ndfa);
        System.out.println("Etats d'acceptation : "+ acceptedState);
        // Find the start states
        Queue<String> departState = new LinkedList<>();
        StringBuilder state = new StringBuilder(); // Find the start stat
        visited = new HashSet<>();
        successorList = new LinkedList<>();
        successorList.add(0);
        while (!successorList.isEmpty()) {
            int indice = successorList.poll();
            visited.add(indice);
            for (int j = 0; j < ndfa.length; j++) {
                if(ndfa[indice][j]==epsilon) {
                    if (!visited.contains(j)) { // If j is not in the successor list, add it
                        successorList.add(j);
                        visited.add(j);
                    }
                }
            }
        }

        // Convert each integer to String and add it
        for (Integer elem : visited) {
            if (state.length() > 0) {
                state.append(","); // Add a comma before each element, except the first
            }
            state.append(elem.toString());
        }
        String start = state.toString();
        departState.add(start);// Separate the indices of the states by commas.
        startState.put(start, true);

        // construction de la Dfa
        while (!departState.isEmpty()) {
            currentState = departState.poll();
            boolean isFinalStateSet = false;
            dfa.put(currentState, createMapWithKeys(letters));
            for (char letter : letters) {
                String[] elements = currentState.split(",");
                for (String element : elements) {
                    int index = Integer.parseInt(element.trim()); // Convert the String to an integer
                    successorList = new LinkedList<>();
                    visited = new HashSet<>();
                    letterFound = false;
                    beginToSearch = true;
                    while (!successorList.isEmpty() || beginToSearch) { 
                        if (!beginToSearch) {
                            index = successorList.poll();
                        } 
                        beginToSearch = false;
                        for (int j = 0; j < ndfa[index].length; j++) { // Traverse the columns of ndfa
                            // If we find letters other than ' ' and \u0000, we find its successors if the path € exists
                            if ((ndfa[index][j] == letter) || (letterFound && ndfa[index][j] == epsilon)) {
                                // If we find a letter, we store the indices of the successors to visit in a list
                                letterFound = true;
                                if (!visited.contains(j)) { // If j is not in the successor list, add it
                                    successorList.add(j);
                                    visited.add(j);
                                }
                            }
                        }
                    }
                    if (!visited.isEmpty()){
                        result = new StringBuilder();
                        for (Integer elem : visited) {
                            if (result.length() > 0) {
                                result.append(","); // Add a comma before each element, except the first
                            }
                            result.append(elem.toString());
                        }
                        dfa.get(currentState).put(""+letter, result.toString()); 
                        if (!result.toString().equals(currentState)) { // Avoid redundancy in the current state
                            departState.add(result.toString()); // At some point, we need to stop adding
                        }
                    }
                    
                    // Convert currentState to an array of strings
                    String[] statesArray = currentState.split(",");

                    // Create a Set to avoid substring issues
                    Set<String> statesSet = new HashSet<>(Arrays.asList(statesArray));

                    // Check if acceptedState is in the Set
                    if (statesSet.contains(String.valueOf(acceptedState))) {
                        finalState.put(currentState, true);
                    } else {
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
                if (c != epsilon && c != empty_char) { // Ignore empty or invalid transitions
                    letters.add(c);
                    System.out.println("key:"+c);
                }
            }
        }
        return new ArrayList<>(letters);
    }

    /// Method to create a HashMap with reusable keys
    HashMap<String, String> createMapWithKeys(List<Character> keys) {
        HashMap<String, String> map = new HashMap<>();

        // Initialize each key with a default value
        for (Character key : keys) {
            map.put(key.toString(), ""); // Default value
        }
        
        return map;
    }

    // Minimize DFA
    public HashMap<String, HashMap<String, String>> minimizeDFA(HashMap<String, HashMap<String, String>> dfa, 
                                                                       HashMap<String, Boolean> startState, 
                                                                       HashMap<String, Boolean> finalState) {
        // Step 1: Remove unreachable states
        Set<String> reachableStates = findReachableStates(dfa, startState);
        removeUnreachableStates(dfa, finalState, reachableStates);
        
        // Step 2: Merge equivalent states
        Set<Set<String>> partition = partitionStates(dfa, finalState, reachableStates);
        HashMap<String, String> stateMapping = mergeEquivalentStates(partition);

        // Step 3: Construct the minimized DFA
        return constructMinimizedDFA(dfa, stateMapping, startState, finalState);
    }

    // Find reachable states from the start state
    private Set<String> findReachableStates(HashMap<String, HashMap<String, String>> dfa, 
                                                   HashMap<String, Boolean> startState) {
        Set<String> reachable = new HashSet<>();
        Queue<String> queue = new LinkedList<>(startState.keySet());
        while (!queue.isEmpty()) {
            String current = queue.poll();
            if (!reachable.contains(current)) {
                reachable.add(current);
                for (String next : dfa.get(current).values()) {
                    if (!next.isEmpty() && !reachable.contains(next)) {
                        queue.add(next);
                    }
                }
            }
        }
        return reachable;
    }

    // Remove states that are not reachable
    private void removeUnreachableStates(HashMap<String, HashMap<String, String>> dfa, 
                                                HashMap<String, Boolean> finalState, 
                                                Set<String> reachableStates) {
        dfa.keySet().retainAll(reachableStates);
        finalState.keySet().retainAll(reachableStates);
    }

    // Partition the states into groups (final/non-final first)
    private Set<Set<String>> partitionStates(HashMap<String, HashMap<String, String>> dfa, 
                                                    HashMap<String, Boolean> finalState, 
                                                    Set<String> reachableStates) {
        Set<String> finalStates = new HashSet<>();
        Set<String> nonFinalStates = new HashSet<>();

        for (String state : reachableStates) {
            if (finalState.getOrDefault(state, false)) {
                finalStates.add(state);
            } else {
                nonFinalStates.add(state);
            }
        }

        Set<Set<String>> partition = new HashSet<>();
        partition.add(finalStates);
        partition.add(nonFinalStates);

        boolean changed;
        do {
            changed = false;
            Set<Set<String>> newPartition = new HashSet<>();
            for (Set<String> group : partition) {
                Set<Set<String>> splitGroups = split(group, partition, dfa);
                newPartition.addAll(splitGroups);
                if (splitGroups.size() > 1) {
                    changed = true;
                }
            }
            partition = newPartition;
        } while (changed);

        return partition;
    }

    // Split groups based on distinguishable states
    private Set<Set<String>> split(Set<String> group, Set<Set<String>> partition, 
                                          HashMap<String, HashMap<String, String>> dfa) {
        Map<String, Set<String>> splitMap = new HashMap<>();
        for (String state : group) {
            String signature = getSignature(state, partition, dfa);
            splitMap.computeIfAbsent(signature, k -> new HashSet<>()).add(state);
        }
        return new HashSet<>(splitMap.values());
    }

    // Get the signature of a state based on its transitions
    private String getSignature(String state, Set<Set<String>> partition, 
                                       HashMap<String, HashMap<String, String>> dfa) {
        StringBuilder signature = new StringBuilder();
        for (String input : dfa.get(state).keySet()) {
            String next = dfa.get(state).get(input);
            signature.append(findGroup(next, partition)).append(";");
        }
        return signature.toString();
    }

    // Find which group a state belongs to
    private String findGroup(String state, Set<Set<String>> partition) {
        for (Set<String> group : partition) {
            if (group.contains(state)) {
                return group.toString();
            }
        }
        return "";
    }

    // Merge equivalent states and create a mapping of old states to new states
    private HashMap<String, String> mergeEquivalentStates(Set<Set<String>> partition) {
        HashMap<String, String> stateMapping = new HashMap<>();
        for (Set<String> group : partition) {
            String representative = group.iterator().next(); // Pick any state as the representative
            for (String state : group) {
                stateMapping.put(state, representative);
            }
        }
        return stateMapping;
    }

    // Construct the minimized DFA using the state mapping
    private HashMap<String, HashMap<String, String>> constructMinimizedDFA(HashMap<String, HashMap<String, String>> dfa, 
                                                                                  HashMap<String, String> stateMapping, 
                                                                                  HashMap<String, Boolean> startState, 
                                                                                  HashMap<String, Boolean> finalState) {
        HashMap<String, HashMap<String, String>> minimizedDFA = new HashMap<>();

        for (String oldState : dfa.keySet()) {
            String newState = stateMapping.get(oldState);
            minimizedDFA.putIfAbsent(newState, new HashMap<>());

            for (Map.Entry<String, String> transition : dfa.get(oldState).entrySet()) {
                String newTargetState = stateMapping.get(transition.getValue());
                minimizedDFA.get(newState).put(transition.getKey(), newTargetState);
            }
        }

        // Adjust the start and final states according to the new state mapping
        startState.keySet().retainAll(stateMapping.keySet());
        finalState.keySet().retainAll(stateMapping.keySet());

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
        System.out.println("Matrice AFND");
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
