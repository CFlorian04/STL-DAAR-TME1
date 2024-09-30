import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class AlgorithmeKMP {

    // Méthode principale pour rechercher un motif dans un texte
    public static boolean chercherMotif(String motif, String texte, int line_number, boolean showUnfound) {
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
                String textHighlighted = texte.substring(0, indexTexte - indexMotif) +
                        "\u001B[31m" + texte.substring(indexTexte - indexMotif, indexTexte) + "\u001B[0m" +
                        texte.substring(indexTexte);
                System.out.println(
                        line_number + " ( " + (indexTexte - indexMotif) + " : " + (indexTexte - 1) + " ) \t: "
                                + textHighlighted);
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
        if (!found && showUnfound) {
            System.out.println("Motif non trouvé dans le texte : " + texte);
        }
        return found;
    }

    public static boolean chercherMotif(String motif, String texte) {
        return chercherMotif(motif, texte, 0, true);
    }

    // Nouvelle méthode pour chercher un motif dans un fichier texte
    public static boolean chercheMotif(String motif, String cheminFichier) {
        boolean found = false;
        try {
            BufferedReader lecteur = new BufferedReader(new FileReader(cheminFichier));
            String ligne;
            int numeroLigne = 1;

            while ((ligne = lecteur.readLine()) != null) {
                boolean localFound = chercherMotif(motif, ligne, numeroLigne, false);
                if (!found && localFound) {
                    found = true;
                }
                numeroLigne++;
            }

            lecteur.close();
        } catch (IOException e) {
            System.out.println("Erreur lors de la lecture du fichier : " + e.getMessage());
        }

        if (!found) {
            System.out.println("Motif non trouvé dans le fichier " + cheminFichier);
        }
        return found;
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
        String motif = "England";

        String texte = "ABABDABACDABABCABAB";
        chercherMotif(motif, texte);

        String cheminFichier = "./backend/examples/41011-0.txt";
        chercheMotif(motif, cheminFichier);
    }
}
