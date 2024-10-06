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

    // Vérifie une sous-chaîne de la ligne à partir d'une position donnée
    public boolean verifierSousChaine(String ligne, int startPos) {
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
            // Aucun état de départ trouvé
            return false;
        }

        // Parcourir la sous-chaîne à partir de startPos
        for (int i = startPos; i < ligne.length(); i++) {
            char symbole = ligne.charAt(i);

            // Récupérer les transitions pour l'état courant
            HashMap<String, String> transitions = dfa.get(currentState);

            if (transitions == null) {
                // Aucun état trouvé pour ce symbole
                return false; 
            }

            // Trouver la transition pour le symbole actuel
            String symboleString = Character.toString(symbole);
            if (transitions.containsKey(symboleString)) {
                currentState = transitions.get(symboleString); // Aller à l'état suivant
            } else if (transitions.containsKey(".")) {
                currentState = transitions.get("."); // Accepter n'importe quel caractère avec "."
            } else {
                // Pas de transition pour ce symbole, rejet de la sous-chaîne
                return false;
            }

            // Si on atteint un état final, retourner vrai
            if (finalState.getOrDefault(currentState, false)) {
                return true;
            }
        }

        // Vérifier si l'état final est atteint après avoir parcouru la sous-chaîne
        return finalState.getOrDefault(currentState, false);
    }

    // Vérifie une ligne à l'aide du DFA pour chaque sous-chaîne possible
    public boolean verifierLigne(String ligne) {
        for (int i = 0; i < ligne.length(); i++) {
            if (verifierSousChaine(ligne, i)) {
                return true; // Si une sous-chaîne est acceptée, on retourne vrai
            }
        }
        return false; // Si aucune sous-chaîne n'est acceptée, on retourne faux
    }

    // Vérifie toutes les lignes d'un fichier
    public boolean verifierTexte(String nomFichier) {
        boolean found = false;
        try (BufferedReader br = new BufferedReader(new FileReader(nomFichier))) {
            String ligne;
            int lineNumber = 0; // Compteur pour le numéro de ligne
            while ((ligne = br.readLine()) != null) {
                lineNumber++; // Incrémenter le numéro de ligne
                if (verifierLigne(ligne)) {
                    System.out.println("Ligne " + lineNumber + " : " + ligne);
                    found = true;
                }
            }
        } catch (IOException e) {
            System.err.println("Erreur de lecture du fichier: " + e.getMessage());
        }

        return found;
    }

}