package org.megastage.components.dcpu;

import java.util.HashMap;
import java.util.Map;

public class KeyMapping
{
  public Map<Integer, Integer> keyMap = new HashMap<Integer, Integer>();

  public int getKey(int key) {
    if (this.keyMap.containsKey(Integer.valueOf(key))) return ((Integer)this.keyMap.get(Integer.valueOf(key))).intValue();
    return -1;
  }

  protected void map(int key, int c) {
    this.keyMap.put(Integer.valueOf(key), Integer.valueOf(c));
  }
}