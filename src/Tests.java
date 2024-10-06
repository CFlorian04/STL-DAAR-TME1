
import java.util.Arrays;

public class Tests {

    private static AlgorithmeKMP algorithmeKMP = new AlgorithmeKMP(false);
    private static RegEx algorithmeAutomate = new RegEx();

    private static float pourcentage_exclu = (float) 0.05;

    public static void LunchTest(String[] args) {

        int repetitions = 10000;
        String fichier = "./backend/examples/41011-0.txt";

        String[] motifs = {
                // Motifs courts présents dans le texte
                "war",
                "shoot",

                // Motifs courts absents du texte
                "queen",
                "dolls",

                // Motifs longs présents dans le texte
                "General Miles was",
                "Lieutenant Casey",

                // Motifs longs absents du texte
                "the queen's coronation",
                "in the heart of the jungle",

                // Motifs apparaissant une seule fois
                "good encyclopædia",
                "Hooick"
        };

        test_KMP(motifs, fichier, false, repetitions);
        //test_Automate(motifs, fichier, true, repetitions);
    }
    


    // Méthode d'execution de méthode - retourne le temps d'execution
    public static float executionTime(Runnable methode) {
        long debut = System.nanoTime();
        methode.run();
        long fin = System.nanoTime();
        float time = (fin - debut) / 1_000_000.0f;
        return time;
    }


    public static float[][] test_KMP(String motifs[], String fichier, boolean showLog, int repetitions) {
        float[][] times = new float[motifs.length][3];
        int maxLength = get_motifs_max_length(motifs);

        for (int i = 0; i < motifs.length; i++) {
            float l_time[] = KMPCalculerStatistiquesTemps(motifs[i], fichier, false, repetitions);
            times[i] = l_time;
            
            if (showLog) {
                System.out.println(
                    "KMP |\t Motif : "
                    + String.format("%" + String.valueOf(maxLength + 5) + "s",motifs[i] + " [" + algorithmeKMP.getOccurencesInFile(motifs[i], fichier)+ "]")
                    + "  -> Min : " + l_time[0] + " ms"
                    + " \t| Max : " + l_time[1] + " ms"
                    + " \t| Moy : " + l_time[2] + " ms"
                );
            }
        }

        return times;

    }

    public static float[][] test_Automate(String motifs[], String fichier, boolean showLog, int repetitions) {
        float[][] times = new float[motifs.length][3];
        int maxLength = get_motifs_max_length(motifs);

        for (int i = 0; i < motifs.length; i++) {
            float l_time[] = AutomateCalculerStatistiquesTemps(motifs[i], fichier, false, repetitions);
            times[i] = l_time;
            
            if (showLog) {
                System.out.println(
                    "Automate |\t Motif : "
                    + String.format("%" + String.valueOf(maxLength + 5) + "s",motifs[i] + " [" + algorithmeKMP.getOccurencesInFile(motifs[i], fichier)+ "]")
                    + "  -> Min : " + l_time[0] + " ms"
                    + " \t| Max : " + l_time[1] + " ms"
                    + " \t| Moy : " + l_time[2] + " ms"
                );
            }
        }

        return times;

    }


    private static float[] KMPCalculerStatistiquesTemps(String motif, String fichier, boolean showLog, int repetitions) {
        float[] times = new float[repetitions];

        // Collecte des temps d'exécution
        for (int i = 0; i < repetitions; i++) {
            times[i] = executionTime(() -> algorithmeKMP.findPatternInFile(motif, fichier));
        }

        // Tri des temps
        Arrays.sort(times);

        // Calcul des index pour exclure les X % les plus petits et plus grands
        int startIndex = (int) (repetitions * pourcentage_exclu);
        int endIndex = (int) (repetitions * (1 - pourcentage_exclu)) - 1;

        float min = times[startIndex];
        float max = times[endIndex];
        float moy = 0;

        // Calcul de la moyenne sur les 80% des valeurs restantes
        for (int i = startIndex; i < endIndex; i++) {
            moy += times[i];
        }
        moy = moy / (endIndex - startIndex);

        if (showLog) {
            System.out.println("Motif : " + motif + " [" + algorithmeKMP.getOccurencesInFile(motif, fichier)
                    + "] -> Min : " + min
                    + " ms | Max : " + max
                    + " ms | Moy : " + moy
                    + " ms");
        }

        return new float[] { min, max, moy };
    }

    private static float[] AutomateCalculerStatistiquesTemps(String motif, String fichier, boolean showLog, int repetitions) {
        float[] times = new float[repetitions];

        // Collecte des temps d'exécution
        for (int i = 0; i < repetitions; i++) {
            times[i] = executionTime(() -> algorithmeAutomate.findPatternInFile(motif, fichier));
        }

        // Tri des temps
        Arrays.sort(times);

        // Calcul des index pour exclure les 10% les plus petits et les 10% les plus
        // grands
        int startIndex = (int) (repetitions * pourcentage_exclu);
        int endIndex = (int) (repetitions * (1 - pourcentage_exclu));

        float min = times[startIndex];
        float max = times[endIndex - 1]; // dernier élément inclus dans les 80%
        float moy = 0;

        // Calcul de la moyenne sur les 80% des valeurs restantes
        for (int i = startIndex; i < endIndex; i++) {
            moy += times[i];
        }
        moy = moy / (endIndex - startIndex);

        if (showLog) {
            System.out.println("Motif : " + motif
                    + "] -> Min : " + min
                    + " ms | Max : " + max
                    + " ms | Moy : " + moy
                    + " ms");
        }

        return new float[] { min, max, moy };
    }

    private static int get_motifs_max_length(String motifs[]) {
        int maxLength = 0;

        for (String motif : motifs) {
            if (motif.length() > maxLength) {
                maxLength = motif.length(); // Mise à jour de la longueur maximale
            }
        }

        return maxLength;
    }
}
