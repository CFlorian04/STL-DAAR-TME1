public class Tests {

    private static AlgorithmeKMP algorithmeKMP = new AlgorithmeKMP(true);

    public static void main(String[] args) {
        test_multiple_motifs(
                new String[] { "Chihuahua", "Pizzi", "Peperoni", "Pizza", "Poppers", "The", "conservative", "animals",
                        "military" },
                "./backend/examples/41011-0.txt");
    }

    public static float executionTime(Runnable methode) {
        long debut = System.nanoTime();
        methode.run();
        long fin = System.nanoTime();

        float time = (fin - debut) / 1_000_000.0f;
        // System.out.println("Temps d'exécution : " + time + " ms");

        return time;
    }

    public static void test_single_motif(String motif, String fichier) {
        System.out.println("Temps d'exécution : "
                + executionTime(() -> algorithmeKMP.chercheMotifDansFichier(motif, fichier)) + " ms");
    }

    public static void test_multiple_motifs(String motifs[], String fichier) {
        for (String motif : motifs) {
            System.out.println("Motif : " + motif + " -> Temps d'exécution : "
                    + executionTime(() -> algorithmeKMP.chercheMotifDansFichier(motif, fichier)) + " ms");
        }
    }
}
