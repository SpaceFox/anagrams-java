package info.kisai.anagrams;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class Anagrams {

  private Map<String, Set<String>> data = new HashMap<>();

  public void add(String word) {

      String key = toKey(word);

      if (!data.containsKey(key)) {
          data.put(key, new HashSet<String>());
      }

      data.get(key).add(word);
  }

  public Set<String> getAnagramsOf(String word) {
      return data.get(toKey(word));
  }

  private static String toKey(String in) {

      char[] chars = in.toCharArray();
      List<Character> list = new ArrayList<>(chars.length);
      StringBuilder sb = new StringBuilder(chars.length);

      for (char c : chars) {
          list.add(c);
      }

      Collections.sort(list);

      for (Character c : list) {
          sb.append(c);
      }
      return sb.toString();
  }
}