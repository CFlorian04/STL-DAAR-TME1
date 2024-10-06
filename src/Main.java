import java.util.Scanner;

public class Main {
    
    public static void main(String[] args) { // args[0] => pattern, args[1] => file, args[2] => method
        
        Scanner sc = new Scanner(System.in);
        
        // Afficher le menu pour choisir entre les tests et les algorithmes
        System.out.println("Choisissez une option :");
        System.out.println("1. Lancer les tests");
        System.out.println("2. Tester les algorithmes");
        
        int choix = sc.nextInt();
        sc.nextLine(); // Consommer la nouvelle ligne après l'entier
        
        switch (choix) {
            case 1:
                // Programme à lancer pour les tests
                Tests.LaunchTest(args);
                break;
            case 2:
                // Programme principal pour les algorithmes
                String algo = null;
                if (args.length >= 1) {
                    if (args[0].contains("*") || args[0].contains("|") || args[0].contains(".") || args[0].contains(")") || args[0].contains(")")) {
                        RegEx.LaunchRegEx(args);
                    } else if (args.length == 3) {
                        algo = args[2];
                        choseMethod(args, algo);
                    }
                }
                
                if (args.length < 1 || args.length == 2) {
                    System.out.println("Choisir la méthode à utiliser (KMP ou Automate)");
                    algo = sc.nextLine();
                    choseMethod(args, algo);
                }
                break;
            default:
                System.out.println("Option non valide. Veuillez choisir 1 ou 2.");
                break;
        }
        
        sc.close(); // Fermer le scanner après utilisation
    }

    public static void choseMethod(String[] args, String algo) {
        if (algo.equals("Automate")) {
            new RegEx(true).LaunchRegEx(args);
        } else if (algo.equals("KMP")) {
            new AlgorithmeKMP(true).LaunchKMP(args);
        } else {
            System.out.println("La méthode n'existe pas");
        }


    }
}
