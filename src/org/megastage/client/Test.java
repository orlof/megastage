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
        A a = new A();
        B b = new B();
        System.out.println(a.getClass().isAssignableFrom(b.getClass()));
        System.out.println(b.getClass().isAssignableFrom(a.getClass()));
        System.out.println(a.getClass().isAssignableFrom(a.getClass()));
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

