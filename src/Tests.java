import java.util.Arrays;

public class Tests {

    private static AlgorithmeKMP algorithmeKMP = new AlgorithmeKMP(false);

    private static float pourcentage_exclu = (float) 0.05;

    public static void LunchTest(String[] args) {

        int repetitions = 10000;
        String fichier = "./backend/examples/41011-0.txt";
        String motif = "Chihuahua";

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

        // test_single_motif_min_max(motif, fichier, true, repetitions);
        test_multiple_motifs_min_max(motifs, fichier, true, repetitions);
    }

    // Méthode d'execution de méthode - retourne le temps d'execution
    public static float executionTime(Runnable methode) {
        long debut = System.nanoTime();
        methode.run();
        long fin = System.nanoTime();

        float time = (fin - debut) / 1_000_000.0f;
        // System.out.println("Temps d'exécution : " + time + " ms");

        return time;
    }

    // Retourne le temps d'execution de l'algo KMP
    public static float test_single_motif(String motif, String fichier, boolean showLog) {
        float time = executionTime(() -> algorithmeKMP.findPatternInFile(motif, fichier));
        if (showLog) {
            System.out.println("Motif : " + motif + " [" + algorithmeKMP.findPatternInFile(motif, fichier)
                    + "] -> Temps d'exécution : " + time + " ms");
        }
        return time;
    }

    // Retourne la moyenne des temps d'executions
    public static float test_single_motif(String motif, String fichier, boolean showLog, int repetitions) {
        float time = 0;
        for (int i = 0; i < repetitions; i++) {
            time += test_single_motif(motif, fichier, false);
        }
        time = time / repetitions;
        if (showLog) {
            System.out.println("Motif : " + motif + " [" + algorithmeKMP.getOccurencesInFile(motif, fichier)
                    + "] -> Temps d'exécution : " + time + " ms");
        }
        return time;
    }

    // Retourne le minimum, le maximum et la moyenne en excluant les 10% des valeurs
    // extrêmes
    public static float[] test_single_motif_min_max(String motif, String fichier, boolean showLog, int repetitions) {
        float[] times = new float[repetitions];

        // Collecte des temps d'exécution
        for (int i = 0; i < repetitions; i++) {
            times[i] = test_single_motif(motif, fichier, false);
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
            System.out.println("Motif : " + motif + " [" + algorithmeKMP.getOccurencesInFile(motif, fichier)
                    + "] -> Min : " + min
                    + " ms | Max : " + max
                    + " ms | Moy : " + moy
                    + " ms");
        }

        return new float[] { min, max, moy };
    }

    // Retourne les temps d'executions de plusieurs motifs
    public static float[] test_multiple_motifs(String motifs[], String fichier, boolean showLog) {
        float[] times = new float[motifs.length];
        for (int i = 0; i < motifs.length; i++) {
            times[i] = test_single_motif(motifs[i], fichier, false);
            if (showLog) {
                System.out.println(
                        "Motif : " + motifs[i] + " [" + algorithmeKMP.getOccurencesInFile(motifs[i], fichier)
                                + "] -> Temps d'exécution : " + times[i] + " ms");
            }
        }
        return times;
    }

    // Retourne les moyennes d'executions de plusieurs motifs
    public static float[] test_multiple_motifs(String motifs[], String fichier, boolean showLog, int repetitions) {
        float[] times = new float[motifs.length];
        for (int i = 0; i < repetitions; i++) {
            float l_times[] = test_multiple_motifs(motifs, fichier, false);
            for (int j = 0; j < times.length; j++) {
                times[j] += l_times[j];
            }
        }
        for (int k = 0; k < times.length; k++) {
            times[k] = times[k] / repetitions;
            if (showLog) {
                System.out.println(
                        "Motif : " + motifs[k] + " [" + algorithmeKMP.getOccurencesInFile(motifs[k], fichier)
                                + "] -> Temps d'exécution : " + times[k] + " ms");
            }
        }

        return times;
    }

    public static float[][] test_multiple_motifs_min_max(String motifs[], String fichier, boolean showLog,
            int repetitions) {
        float[][] times = new float[motifs.length][3];
        int maxLength = get_motifs_max_length(motifs);

        for (int i = 0; i < motifs.length; i++) {
            float l_time[] = test_single_motif_min_max(motifs[i], fichier, false, repetitions);
            times[i] = l_time;
            if (showLog) {
                System.out.println(
                        "Motif : "
                                + String.format("%" + String.valueOf(maxLength + 5) + "s",
                                        motifs[i] + " [" + algorithmeKMP.getOccurencesInFile(motifs[i], fichier)
                                                + "]")
                                + " -> Min : "
                                + l_time[0]
                                + " ms \t| Max : "
                                + l_time[1]
                                + " ms \t| Moy : "
                                + l_time[2] + " ms");
            }
        }

        return times;
    }

    public static int get_motifs_max_length(String motifs[]) {
        int maxLength = 0;

        for (String motif : motifs) {
            if (motif.length() > maxLength) {
                maxLength = motif.length(); // Mise à jour de la longueur maximale
            }
        }

        return maxLength;
    }
}
