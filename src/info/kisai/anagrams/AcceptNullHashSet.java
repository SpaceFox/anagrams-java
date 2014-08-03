package info.kisai.anagrams;

import java.util.Collection;
import java.util.HashSet;

public class AcceptNullHashSet<E> extends HashSet<E> {

  @Override
  public boolean addAll(Collection<? extends E> c) {
      if (c == null) {
          return false;
      }
      return super.addAll(c);
  }
}