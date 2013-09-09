package org.megastage.util;

import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: contko3
 * Date: 8/21/13
 * Time: 2:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class RAM {
    public char[] mem = new char[0];

    public RAM() {}

    public RAM(char[] memory, char start, int size) {
        update(memory, start, size);
    }

    public RAM(char[] mem) {
        this.mem = mem;
    }

    public boolean update(char[] mem) {
        return update(mem, (char) 0, mem.length);
    }

    public boolean update(char[] memory, char start, int size) {
        if(equals(memory, start, size)) return false;

        this.mem = Arrays.copyOfRange(memory, start, start + size);
        return true;
    }

    public boolean equals(char[] memory, char start, int size) {
        if(size != mem.length) return false;
        
        for(int i=0; i < size; i++) {
            if(mem[i] != memory[i + start]) return false;
        }

        return true;
    }

}
