import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class RegEx {
  // MACROS
  static final int CONCAT = 0xC04CA7;
  static final int ETOILE = 0xE7011E;
  static final int ALTERN = 0xA17E54;
  static final int PROTECTION = 0xBADDAD;

  static final int PARENTHESEOUVRANT = 0x16641664;
  static final int PARENTHESEFERMANT = 0x51515151;
  static final int DOT = 0xD07;

  static final char epsilon = ' ';
  static final char empty_char = '\u0000';

  // REGEX
  private static String regEx;

  // CONSTRUCTOR
  public RegEx() {
  }

  // MAIN
  public static void LunchReGex(String arg[]) {

    HashMap<String, Boolean> startState = new HashMap<>();
    HashMap<String, Boolean> finalState = new HashMap<>();
    
    // System.out.println("Welcome to Bogota, Mr. Thomas Anderson.");
    if (arg.length != 0) {
      regEx = arg[0];
    } else {
      Scanner s = new Scanner(System.in);
      System.out.println("  >> Inserer l'expression reguliere : ");
      regEx = s.next();  // Lire l'expression régulière
    }
    // System.out.println("  >> Parsing regEx \"" + regEx + "\".");
    // System.out.println("  >> ...");

    if (regEx.length() < 1) {
      System.err.println("  >> ERREUR: L'expression reguliere est vide.");
    } else {
      System.out.print("  >> Code ASCII: [" + (int) regEx.charAt(0));
      for (int i = 1; i < regEx.length(); i++)
        System.out.print("," + (int) regEx.charAt(i));
      System.out.println("].");
      RegExTree ret = null;
      try {
        ret = parse();
        System.out.println("  >> Arbre syntaxique: " + ret.toString() + ".");
        //le code est à inserer ici normalement
        // My own code
        Transformer transformer = new Transformer(empty_char, epsilon);
        char[][] ndfa = transformer.transformRegExTreeToNDFA(ret.toString());
        transformer.displayMatrix(ndfa);
        // if(ndfa[5][3] == ' ') System.out.println("true");
        HashMap<String, HashMap<String, String>> dfa = transformer.transformNDFAToDFA(ndfa, startState, finalState);
        dfa = transformer.minimizeDFA(dfa, startState, finalState);

        transformer.afficherHashMap(dfa);

        System.out.println("Afficher les etats de depart");
        transformer.afficherState(startState);
        System.out.println("Afficher les etats finaux");
        transformer.afficherState(finalState);
  
        // System.out.println("parcours DFA");
        
        // DFA Optimization 
  
        // Instanciation de la classe DFA
        DFA monDFA = new DFA(dfa, startState, finalState);
  
        String file = null;
        if ((arg.length >= 2) && (arg[1] != null || arg[1] != "") ) {
          file = arg[1];
        } else {
          while (file == null) {
            System.out.println("Indiquer le chemin du fichier .txt:");
            Scanner sc = new Scanner(System.in);
            file = sc.next();
            sc.close();
          }
          // file = "./src/examples/41011-0.txt";
        }
        // Vérification des lignes dans un fichier texte
        monDFA.verifierTexte(file);
  
        // End of my own code
      } catch (Exception e) {
        System.err.println("  >> ERREUR: L'expression reguliere est invalide \"" + regEx + "\".");
      }
    }

    System.out.println("  >> ...");
  }

  // FROM REGEX TO SYNTAX TREE
  private static RegExTree parse() throws Exception {

    ArrayList<RegExTree> result = new ArrayList<RegExTree>();
    for (int i = 0; i < regEx.length(); i++)
      result.add(new RegExTree(charToRoot(regEx.charAt(i)), new ArrayList<RegExTree>()));

    return parse(result);
  }

  private static int charToRoot(char c) {
    if (c == '.')
      return DOT;
    if (c == '*')
      return ETOILE;
    if (c == '|')
      return ALTERN;
    if (c == '(')
      return PARENTHESEOUVRANT;
    if (c == ')')
      return PARENTHESEFERMANT;
    return (int) c;
  }

  private static RegExTree parse(ArrayList<RegExTree> result) throws Exception {
    while (containParenthese(result))
      result = processParenthese(result);
    while (containEtoile(result))
      result = processEtoile(result);
    while (containConcat(result))
      result = processConcat(result);
    while (containAltern(result))
      result = processAltern(result);

    if (result.size() > 1)
      throw new Exception();

    return removeProtection(result.get(0));
  }

  private static boolean containParenthese(ArrayList<RegExTree> trees) {
    for (RegExTree t : trees)
      if (t.root == PARENTHESEFERMANT || t.root == PARENTHESEOUVRANT)
        return true;
    return false;
  }

  private static ArrayList<RegExTree> processParenthese(ArrayList<RegExTree> trees) throws Exception {
    ArrayList<RegExTree> result = new ArrayList<RegExTree>();
    boolean found = false;
    for (RegExTree t : trees) {
      if (!found && t.root == PARENTHESEFERMANT) {
        boolean done = false;
        ArrayList<RegExTree> content = new ArrayList<RegExTree>();
        while (!done && !result.isEmpty())
          if (result.get(result.size() - 1).root == PARENTHESEOUVRANT) {
            done = true;
            result.remove(result.size() - 1);
          } else
            content.add(0, result.remove(result.size() - 1));
        if (!done)
          throw new Exception();
        found = true;
        ArrayList<RegExTree> subTrees = new ArrayList<RegExTree>();
        subTrees.add(parse(content));
        result.add(new RegExTree(PROTECTION, subTrees));
      } else {
        result.add(t);
      }
    }
    if (!found)
      throw new Exception();
    return result;
  }

  private static boolean containEtoile(ArrayList<RegExTree> trees) {
    for (RegExTree t : trees)
      if (t.root == ETOILE && t.subTrees.isEmpty())
        return true;
    return false;
  }

  private static ArrayList<RegExTree> processEtoile(ArrayList<RegExTree> trees) throws Exception {
    ArrayList<RegExTree> result = new ArrayList<RegExTree>();
    boolean found = false;
    for (RegExTree t : trees) {
      if (!found && t.root == ETOILE && t.subTrees.isEmpty()) {
        if (result.isEmpty())
          throw new Exception();
        found = true;
        RegExTree last = result.remove(result.size() - 1);
        ArrayList<RegExTree> subTrees = new ArrayList<RegExTree>();
        subTrees.add(last);
        result.add(new RegExTree(ETOILE, subTrees));
      } else {
        result.add(t);
      }
    }
    return result;
  }

  private static boolean containConcat(ArrayList<RegExTree> trees) {
    boolean firstFound = false;
    for (RegExTree t : trees) {
      if (!firstFound && t.root != ALTERN) {
        firstFound = true;
        continue;
      }
      if (firstFound)
        if (t.root != ALTERN)
          return true;
        else
          firstFound = false;
    }
    return false;
  }

  private static ArrayList<RegExTree> processConcat(ArrayList<RegExTree> trees) throws Exception {
    ArrayList<RegExTree> result = new ArrayList<RegExTree>();
    boolean found = false;
    boolean firstFound = false;
    for (RegExTree t : trees) {
      if (!found && !firstFound && t.root != ALTERN) {
        firstFound = true;
        result.add(t);
        continue;
      }
      if (!found && firstFound && t.root == ALTERN) {
        firstFound = false;
        result.add(t);
        continue;
      }
      if (!found && firstFound && t.root != ALTERN) {
        found = true;
        RegExTree last = result.remove(result.size() - 1);
        ArrayList<RegExTree> subTrees = new ArrayList<RegExTree>();
        subTrees.add(last);
        subTrees.add(t);
        result.add(new RegExTree(CONCAT, subTrees));
      } else {
        result.add(t);
      }
    }
    return result;
  }

  private static boolean containAltern(ArrayList<RegExTree> trees) {
    for (RegExTree t : trees)
      if (t.root == ALTERN && t.subTrees.isEmpty())
        return true;
    return false;
  }

  private static ArrayList<RegExTree> processAltern(ArrayList<RegExTree> trees) throws Exception {
    ArrayList<RegExTree> result = new ArrayList<RegExTree>();
    boolean found = false;
    RegExTree gauche = null;
    boolean done = false;
    for (RegExTree t : trees) {
      if (!found && t.root == ALTERN && t.subTrees.isEmpty()) {
        if (result.isEmpty())
          throw new Exception();
        found = true;
        gauche = result.remove(result.size() - 1);
        continue;
      }
      if (found && !done) {
        if (gauche == null)
          throw new Exception();
        done = true;
        ArrayList<RegExTree> subTrees = new ArrayList<RegExTree>();
        subTrees.add(gauche);
        subTrees.add(t);
        result.add(new RegExTree(ALTERN, subTrees));
      } else {
        result.add(t);
      }
    }
    return result;
  }

  private static RegExTree removeProtection(RegExTree tree) throws Exception {
    if (tree.root == PROTECTION && tree.subTrees.size() != 1)
      throw new Exception();
    if (tree.subTrees.isEmpty())
      return tree;
    if (tree.root == PROTECTION)
      return removeProtection(tree.subTrees.get(0));

    ArrayList<RegExTree> subTrees = new ArrayList<RegExTree>();
    for (RegExTree t : tree.subTrees)
      subTrees.add(removeProtection(t));
    return new RegExTree(tree.root, subTrees);
  }

  // EXAMPLE
  // --> RegEx from Aho-Ullman book Chap.10 Example 10.25
  private static RegExTree exampleAhoUllman() {
    RegExTree a = new RegExTree((int) 'a', new ArrayList<RegExTree>());
    RegExTree b = new RegExTree((int) 'b', new ArrayList<RegExTree>());
    RegExTree c = new RegExTree((int) 'c', new ArrayList<RegExTree>());
    ArrayList<RegExTree> subTrees = new ArrayList<RegExTree>();
    subTrees.add(c);
    RegExTree cEtoile = new RegExTree(ETOILE, subTrees);
    subTrees = new ArrayList<RegExTree>();
    subTrees.add(b);
    subTrees.add(cEtoile);
    RegExTree dotBCEtoile = new RegExTree(CONCAT, subTrees);
    subTrees = new ArrayList<RegExTree>();
    subTrees.add(a);
    subTrees.add(dotBCEtoile);
    return new RegExTree(ALTERN, subTrees);
  }
}