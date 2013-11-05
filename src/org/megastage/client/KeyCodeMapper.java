/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.client;

import java.util.HashMap;

/**
 *
 * @author Teppo
 */
public class KeyCodeMapper {
    public static final HashMap<Integer, Character> map = new HashMap<>();
    
    static {
        map.put(0x0d, (char) 0x0a);
        map.put(0x0d, (char) 0x0a);
    }
}
