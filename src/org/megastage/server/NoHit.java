package org.megastage.server;

public class NoHit extends Hit {
    public static NoHit INSTANCE = new NoHit();

    private NoHit() {
        super(0.0f);
    }
    
    @Override
    public String toString() {
        return "NoHit(distance=" + distance;
    }
    
}

