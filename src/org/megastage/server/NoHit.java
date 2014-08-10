package org.megastage.server;

public class NoHit extends Hit {
    public static NoHit INSTANCE = new NoHit();

    private NoHit() {}
    
    public String toString() {
        return "NoHit(distance=" + distance;
    }
}

