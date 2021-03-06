/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.apertium.lttoolbox.collections;

import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;

/**
 *
 * @author Jacob Nordfalk
 */
public class SlowIntegerTreeSet extends TreeSet<Integer> implements IntSet {// TODO Var HashSet og det var OK?!? The TreeSet

  @Override
  public void add(int i) {
    super.add(i);
  }

  @Override
  public void remove(int i) {
    super.remove(i);
  }

  @Override
  public boolean contains(int i) {
    return super.contains(i);
  }

  @Override
  public int firstInt() {
    return super.iterator().next();
  }

  @Override
  public boolean addAll(IntSet c) {
    Iterator<? extends Integer> e = c.iterator();
    while (e.hasNext()) {
       add(e.next());
    }
    return true;
  }
}
