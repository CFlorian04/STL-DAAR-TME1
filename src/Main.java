import java.util.Scanner;

public class Main {
    
    public static void main(String[] args) { // args[0] => pattern, args[1] => file, args[2] => method

        // Programme à lancer pour les tests
        //Tests.LunchTest(args);
        // Fin programme test

        // Programme principal

        String algo = null;
        if ((args.length >= 1)){
            if (args[0].contains("*") || args[0].contains("|") || args[0].contains(".") || args[0].contains(")") || args[0].contains(")")){
                RegEx.LunchReGex(args);
            } else if (args.length == 3) {
                algo = args[2];
                choseMethod(args, algo);
            } 
        }

        if (args.length < 1 || args.length == 2) {
            System.out.println("Choisir la methode a utiliser (KMP ou Automate)");
            Scanner sc = new Scanner(System.in);
            algo = sc.nextLine();
            choseMethod(args, algo);
        }
        //Fin du programme principal
    }

    public static void choseMethod(String[] args, String algo) {
        if (algo.equals("Automate")){
            RegEx.LunchReGex(args);
        } else if (algo.equals("KMP")) {
            AlgorithmeKMP.lunchKMP(args);
        } else {
            System.out.println("La méthode n'existe pas");
        }
    }
}
