package info.kisai.anagrams;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class LiveAnagrams {

  private Set<String> words = new HashSet<>();
  private Anagrams anagrams = new Anagrams();

  private Set<String> alreadTestedWords = new HashSet<>();
  private Map<Integer, Map<String, String>> previous = new HashMap<>();

  public void loadWords(File dictionary) throws FileNotFoundException {

      System.out.println("Chargement du dictionnaire...");

      long start = System.currentTimeMillis();

      try (Scanner scanner = new Scanner(dictionary, "UTF8")) {
          String word = null;
          while (scanner.hasNextLine()) {
              word = clean(scanner.nextLine());
              words.add(word);
              anagrams.add(word);
          }
      }
      System.out.println("Chargement OK, " + words.size() + " mots en " + (System.currentTimeMillis() - start) + " ms");
  }

  private static String clean(String word) {
      return word.toLowerCase(Locale.FRANCE)
              .replace("à", "a")
              .replace("â", "a")
              .replace("ä", "a")
              .replace("é", "e")
              .replace("è", "e")
              .replace("ê", "e")
              .replace("ë", "e")
              .replace("î", "i")
              .replace("ï", "i")
              .replace("ô", "o")
              .replace("ö", "o")
              .replace("û", "u")
              .replace("ü", "u")
              .replace("ù", "u");
  }

  public List<String> findPath(String rawStart, String rawEnd) throws Exception {

      long tStart = System.currentTimeMillis();

      String start = clean(rawStart);
      String end = clean(rawEnd);
      if (!words.contains(start)) {
          throw new Exception("Le mot " + rawStart + " est inconnu");
      }
      if (!words.contains(end)) {
          throw new Exception("Le mot " + rawEnd + " est inconnu");
      }

      System.out.println("Recherche d'un chemin entre " + start + " et  " + end);

      // Initialize variables
      int depth = 0;
      alreadTestedWords = new HashSet<>();
      previous = new HashMap<>();
      previous.put(0, new HashMap<String, String>());
      Set<String> previousDepthWords = new HashSet<>();
      Set<String> depthWords;
      List<String> path = new LinkedList<>();

      previousDepthWords.add(start);

      // Loop: while word is not found
      do {
          // 1. Set depth
          long startLoop = System.currentTimeMillis();
          depth++;
          previous.put(depth, new HashMap<String, String>());

          System.out.println("Calcul des possibilités de profondeur " + depth);

          // 2. Compute all words available at this level
          depthWords = computeDepthWords(depth, previousDepthWords);

          // 3. Is one of these words the final one ?
          if (depthWords.size() == 0) {
              System.out.println("Aucun chemin possible !");
              break;
          }
          if (depthWords.contains(end)) {
              System.out.println("Chemin trouvé !");
              break;
          }

          // 4. Update data
          alreadTestedWords.addAll(depthWords);
          previousDepthWords = depthWords;

          System.out.println("    Profondeur explorée en " + (System.currentTimeMillis() - startLoop) + " ms");

      } while (true);

      if (depthWords.size()  > 0) {
          path = extractPath(end, depth);
      }

      System.out.println("Calculé en " + (System.currentTimeMillis() - tStart) + " ms");

      return path;
  }

  private Set<String> computeDepthWords(int depth, Set<String> previousDepthWords) {

      int nbWords;
      Set<String> depthWords = new HashSet<>();

      for (String previousLevelWord: previousDepthWords) {

          Set<String> possibleWords = findPossibleWords(previousLevelWord);

          for (String possibleWord : possibleWords) {
              previous.get(depth).put(possibleWord, previousLevelWord);
          }

          depthWords.addAll(possibleWords);
      }

      // Remove words known from previous level
      depthWords.removeAll(alreadTestedWords);

      nbWords = alreadTestedWords.size() + depthWords.size();
              System.out.println("    " + depthWords.size() + " mots trouvés à cette profondeur / total : "
              + nbWords + " / " + words.size() + " soit " + ((double) nbWords / (double) words.size()) * 100
              + "%");
      return depthWords;
  }

  private List<String> extractPath(String end, int depth) {

      List<String> path = new LinkedList<>();
      String previousWord = end;

      for (int i = depth; i >= 0; i--) {
          path.add(previousWord);
          previousWord = previous.get(i).get(previousWord);
      }

      Collections.reverse(path);
      return path;
  }

  private Set<String> findPossibleWords(String word) {

      Set<String> out = new AcceptNullHashSet<>();
      char[] chars = word.toCharArray();

      // Simple anagrams
      out.addAll(anagrams.getAnagramsOf(word));

      add1Char(word, out);
      remove1Char(chars, out);
      replace1Char(chars, out);

      // Avoid infinite loops...
      out.remove(word);

      return out;
  }

  private void add1Char(String word, Set<String> out) {
      for (char c = 'a'; c <= 'z'; c++) {
          out.addAll(anagrams.getAnagramsOf(word + c));
      }
  }

  private void remove1Char(char[] chars, Set<String> out) {

      int length = chars.length;
      StringBuilder sb;

      // Loop : remove letter at position i
      for (int i = 0; i < length; i++) {
          sb = new StringBuilder(length - 1);
          for (int j = 0; j < length; j++) {
              if (i != j) {
                  sb.append(chars[j]);
              }
          }
          out.addAll(anagrams.getAnagramsOf(sb.toString()));
      }
  }

  private void replace1Char(char[] chars, Set<String> out) {

      int length;
      length = chars.length;
      char[] tmpCarArray;

      for (int i = 0; i < length; i++) {
          for (char c = 'a'; c <= 'z'; c++) {
              tmpCarArray = Arrays.copyOf(chars, length);
              if (tmpCarArray[i] != c) {
                  tmpCarArray[i] = c;
                  out.addAll(anagrams.getAnagramsOf(new String(tmpCarArray)));
              }
          }
      }
  }

}