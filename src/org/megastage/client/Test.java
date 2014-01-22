/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.client;

/**
 *
 * @author teppo
 */
public class Test {
    public static void main(String[] args) throws Exception {
        A b = (A) new B();
        System.out.println(b.get());
    }
}

class A {
    public String get() {
        return "A";
    }
}

class B extends A {
/*
    public String get() {
        return "B";
    }
*/
}

