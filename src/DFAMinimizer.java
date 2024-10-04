import java.util.*;

public class DFAMinimizer {

    public static HashMap<String, HashMap<String, String>> minimizeDFA(HashMap<String, HashMap<String, String>> dfa, 
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
    private static Set<String> findReachableStates(HashMap<String, HashMap<String, String>> dfa, 
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
    private static void removeUnreachableStates(HashMap<String, HashMap<String, String>> dfa, 
                                                HashMap<String, Boolean> finalState, 
                                                Set<String> reachableStates) {
        dfa.keySet().retainAll(reachableStates);
        finalState.keySet().retainAll(reachableStates);
    }

    // Partition the states into groups (final/non-final first)
    private static Set<Set<String>> partitionStates(HashMap<String, HashMap<String, String>> dfa, 
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
    private static Set<Set<String>> split(Set<String> group, Set<Set<String>> partition, 
                                          HashMap<String, HashMap<String, String>> dfa) {
        Map<String, Set<String>> splitMap = new HashMap<>();
        for (String state : group) {
            String signature = getSignature(state, partition, dfa);
            splitMap.computeIfAbsent(signature, k -> new HashSet<>()).add(state);
        }
        return new HashSet<>(splitMap.values());
    }

    // Get the signature of a state based on its transitions
    private static String getSignature(String state, Set<Set<String>> partition, 
                                       HashMap<String, HashMap<String, String>> dfa) {
        StringBuilder signature = new StringBuilder();
        for (String input : dfa.get(state).keySet()) {
            String next = dfa.get(state).get(input);
            signature.append(findGroup(next, partition)).append(";");
        }
        return signature.toString();
    }

    // Find which group a state belongs to
    private static String findGroup(String state, Set<Set<String>> partition) {
        for (Set<String> group : partition) {
            if (group.contains(state)) {
                return group.toString();
            }
        }
        return "";
    }

    // Merge equivalent states and create a mapping of old states to new states
    private static HashMap<String, String> mergeEquivalentStates(Set<Set<String>> partition) {
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
    private static HashMap<String, HashMap<String, String>> constructMinimizedDFA(HashMap<String, HashMap<String, String>> dfa, 
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
}
