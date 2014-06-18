package org.megastage;

class A {}

class B extends A {}

public class Test {
    public static void main(String[] args) throws Exception {
        System.out.println(A.class.isInstance(new B()));
    }
}
