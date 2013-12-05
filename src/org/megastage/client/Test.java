/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.client;

import com.jme3.math.FastMath;
import org.megastage.util.Quaternion;
import org.megastage.util.Vector;

/**
 *
 * @author teppo
 */
public class Test {
    public static void main(String[] args) throws Exception {
        A b = new B();
        System.out.println(b.get());
        System.out.println(b.x);
        
    }
}

class A {
    public int x = 1;
    
    public int get() {
        return x;
    }
}

class B extends A {
    public int x = 2;
}

