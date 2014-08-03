package info.kisai.anagrams;

import java.io.File;
import java.util.List;
import java.util.Scanner;

public class CLI {

  public static void main(String... args) throws Exception {
      LiveAnagrams anagrams = new LiveAnagrams();
      anagrams.loadWords(new File("liste.de.mots.francais.frgut.txt"));

      try (Scanner in = new Scanner(System.in)) {
          String words = null;
          List<String> path = null;

          do {
              System.out.println("Entrez les 2 mots séparés par une espace (ou rien pour quitter) :");
              words = in.nextLine();

              if (words == null || words.trim().equals("")) {
                  break;
              }
              words = words.trim();

              if (words.contains(" ")) {
                  String[] wArray = words.split(" ");
                  if (wArray.length == 2) {
                      path = anagrams.findPath(wArray[0], wArray[1]);
                      System.out.println("Chemin trouvé :  " + path + " (" + path.size() + " étapes)");
                  } else {
                      System.out.println("Vous devez entrer 2 mots séparés par une espace.");
                  }
              }
          } while (true);
      }
  }
}