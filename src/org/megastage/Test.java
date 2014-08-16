package org.megastage;

public class Test {
    public static void main(String[] args) throws Exception {
        int[] a = new int[] {1,2,3,4,5};
        System.arraycopy(a, 0, a = new int[10], 1, 5);

        for(int x: a)
            System.out.println(x);
    }
}
