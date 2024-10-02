import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DFA {
    private HashMap<String, HashMap<String, String>> dfa;
    private HashMap<String, Boolean> startState;
    private HashMap<String, Boolean> finalState;

    // Constructeur
    public DFA(HashMap<String, HashMap<String, String>> dfa, 
               HashMap<String, Boolean> startState, 
               HashMap<String, Boolean> finalState) {
        this.dfa = dfa;
        this.startState = startState;
        this.finalState = finalState;
    }

    // Vérifie une ligne à l'aide du DFA
    public boolean verifierLigne(String ligne) {
        // Commencer à l'état initial
        String currentState = null;

        // Trouver l'état initial (dans startState où la valeur est true)
        for (Map.Entry<String, Boolean> entry : startState.entrySet()) {
            if (entry.getValue()) {
                currentState = entry.getKey(); // Cet état est l'état de départ
                break;
            }
        }

        if (currentState == null) {
            System.out.println("Aucun état de départ trouvé.");
            return false;
        }

        // Parcourir la ligne
        for (char symbole : ligne.toCharArray()) {
            // Récupérer les transitions pour l'état courant
            HashMap<String, String> transitions = dfa.get(currentState);

            if (transitions == null) {
                System.out.println("Pas de transition disponible pour l'état: " + currentState);
                return false; // Aucun état trouvé pour ce symbole
            }

            // Trouver la transition pour le symbole actuel
            String symboleString = Character.toString(symbole);
            if (transitions.containsKey(symboleString)) {
                currentState = transitions.get(symboleString); // Aller à l'état suivant
            } else {
                System.out.println("Pas de transition pour le symbole: " + symboleString);
                return false; // Pas de transition pour ce symbole
            }
        }

        // Vérifier si l'état final est dans finalState et marqué comme true
        return finalState.getOrDefault(currentState, false);
    }

    // Vérifie toutes les lignes d'un fichier
    public void verifierTexte(String nomFichier) {
        try (BufferedReader br = new BufferedReader(new FileReader(nomFichier))) {
            String ligne;
            int lineNumber = 0; // Compteur pour le numéro de ligne
            while ((ligne = br.readLine()) != null) {
                lineNumber++; // Incrémenter le numéro de ligne
                if (verifierLigne(ligne)) {
                    System.out.println("Ligne " + lineNumber + "  " + ligne);
                }
            }
        } catch (IOException e) {
            System.err.println("Erreur de lecture du fichier: " + e.getMessage());
        }
    }
}
