import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class AlgorithmeKMP {

    // Mode de test pour ne pas afficher les résultats dans la console
    private static boolean showLog;

    // Méthode principale pour rechercher un motif dans un texte
    private static boolean findPattern(String motif, String texte, int line_number, boolean showUnfound) {
        boolean found = false;
        int longueurMotif = motif.length();
        int longueurTexte = texte.length();

        int[] LPS = new int[longueurMotif];
        int indexMotif = 0;

        // Construire le tableau LPS
        buildLPS(motif, longueurMotif, LPS);

        int indexTexte = 0;
        while (indexTexte < longueurTexte) {
            // Si les caractères correspondent, on avance
            if (motif.charAt(indexMotif) == texte.charAt(indexTexte)) {
                indexMotif++;
                indexTexte++;
            }

            // Si on a trouvé le motif complet
            if (indexMotif == longueurMotif) {
                if (showLog) {
                    String textHighlighted = texte.substring(0, indexTexte - indexMotif) +
                            "\u001B[31m" + texte.substring(indexTexte - indexMotif, indexTexte) + "\u001B[0m" +
                            texte.substring(indexTexte);
                    System.out.println(String.format("Ligne %s (%s : %s) : %s",
                            String.format("% 5d", line_number), String.format("% 3d", indexTexte - indexMotif),
                            String.format("% 3d", indexTexte - 1), textHighlighted));
                }
                // Revenir au dernier décalage possible
                indexMotif = LPS[indexMotif - 1];
                found = true;
            }
            // Si les caractères ne correspondent pas
            else if (indexTexte < longueurTexte && motif.charAt(indexMotif) != texte.charAt(indexTexte)) {
                if (indexMotif != 0) {
                    // Utiliser le LPS pour sauter des comparaisons
                    indexMotif = LPS[indexMotif - 1];
                } else {
                    // Avancer dans le texte si on est au début du motif
                    indexTexte++;
                }
            }
        }
        if (showLog && !found && showUnfound) {
            System.out.println("Motif non trouvé dans le texte : " + texte);
        }
        return found;
    }

    public static boolean findPatternInText(String motif, String texte) {
        return findPattern(motif, texte, 0, true);
    }

    public static void lunchKMP(String[] args) {
        String motif = null;
        String filePath = null;

        if (args.length >= 1) {
            motif = args[0];
        }
        // Créez une seule instance de BufferedReader
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        // Lire le motif à partir de l'entrée utilisateur
        while (motif == null || motif.isEmpty()) {
            System.out.print(">> Inserer le motif : ");
            try {
                motif = reader.readLine();  // Lire la ligne entrée par l'utilisateur
                
                // Vérifier si le motif est vide
                if (motif.isEmpty()) {
                    System.out.println("Le motif ne peut pas être vide. Veuillez réessayer.");
                }
            } catch (IOException e) {
                System.out.println("Erreur lors de la lecture de l'entrée utilisateur : " + e.getMessage());
            }
        }

        // Vérifier les arguments de la ligne de commande
        if (args.length >= 2) {
            filePath = args[1];
        }
        // Demander le chemin du fichier si non fourni dans les arguments
        while (filePath == null || filePath.isEmpty()) {
            System.out.print("  >> Inserer le chemin du fichier : ");
            try {
                filePath = reader.readLine();  // Lire le chemin du fichier
                
                // Vérifier si le chemin est vide
                if (filePath.isEmpty()) {
                    System.out.println("Le chemin du fichier ne peut pas être vide. Veuillez réessayer.");
                }
            } catch (IOException e) {
                System.out.println("Erreur lors de la lecture de l'entrée utilisateur : " + e.getMessage());
            }
        }


        //implement here le code to call KMP Algo
    }

    // Nouvelle méthode pour chercher un motif dans un fichier texte
    public static boolean findPatternInFile(String motif, String cheminFichier) {
        boolean found = false;
        try {
            BufferedReader lecteur = new BufferedReader(new FileReader(cheminFichier));
            String ligne;
            int numeroLigne = 1;

            while ((ligne = lecteur.readLine()) != null) {
                boolean localFound = findPattern(motif, ligne, numeroLigne, false);
                if (!found && localFound) {
                    found = true;
                }
                numeroLigne++;
            }

            lecteur.close();
        } catch (IOException e) {
            if (showLog) {
                System.out.println("Erreur lors de la lecture du fichier : " + e.getMessage());
            }
        }

        if (showLog && !found) {
            System.out.println("Motif non trouvé dans le fichier " + cheminFichier);
        }
        return found;
    }

    public static int getOccurencesInFile(String motif, String cheminFichier) {
        int occurences = 0;
        try {
            BufferedReader lecteur = new BufferedReader(new FileReader(cheminFichier));
            String ligne;
            int numeroLigne = 1;

            while ((ligne = lecteur.readLine()) != null) {
                boolean localFound = findPattern(motif, ligne, numeroLigne, false);
                if (localFound) {
                    occurences += 1;
                }
                numeroLigne++;
            }

            lecteur.close();
        } catch (IOException e) {
            if (showLog) {
                System.out.println("Erreur lors de la lecture du fichier : " + e.getMessage());
            }
        }

        if (showLog && occurences == 0) {
            System.out.println("Motif non trouvé dans le fichier " + cheminFichier);
        }
        return occurences;
    }

    // Construire le LPS (Longest Prefix Suffix)
    public static void buildLPS(String motif, int longueurMotif, int[] LPS) {
        int longueurPrefixeSuffixe = 0;
        int i = 1;
        LPS[0] = 0; // Le premier élément est toujours 0

        while (i < longueurMotif) {
            // Si les caractères correspondent
            if (motif.charAt(i) == motif.charAt(longueurPrefixeSuffixe)) {
                longueurPrefixeSuffixe++;
                LPS[i] = longueurPrefixeSuffixe;
                i++;
            } else {
                // Si les caractères ne correspondent pas
                if (longueurPrefixeSuffixe != 0) {
                    // Utiliser le LPS précédent
                    longueurPrefixeSuffixe = LPS[longueurPrefixeSuffixe - 1];
                } else {
                    // Aucun préfixe trouvé, on passe au caractère suivant
                    LPS[i] = 0;
                    i++;
                }
            }
        }
    }

    public static void main(String[] args) {
        String motif = "Chihuahua";

        // String texte = "ABABDABACDABABCABAB";
        // findPattern(motif, texte);

        String cheminFichier = "./src/examples/41011-0.txt";
        getOccurencesInFile(motif, cheminFichier);
    }

    AlgorithmeKMP() {
        AlgorithmeKMP.showLog = true;
    }

    AlgorithmeKMP(boolean showLog) {
        AlgorithmeKMP.showLog = showLog;
    }
}
