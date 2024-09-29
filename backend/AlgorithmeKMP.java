public class AlgorithmeKMP {

    // Méthode principale pour rechercher un motif dans un texte
    public static void chercherMotif(String motif, String texte) {
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
                System.out.println("Motif trouvé à l'indice " + (indexTexte - indexMotif));
                // Revenir au dernier décalage possible
                indexMotif = LPS[indexMotif - 1];
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
        String texte = "ABABDABACDABABCABAB";
        String motif = "ABABCABAB";
        chercherMotif(motif, texte);
    }
}
