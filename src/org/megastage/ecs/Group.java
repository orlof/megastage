package org.megastage.ecs;

import org.megastage.util.Log;
import org.megastage.util.ID;

public class Group {

    private World world;
    private int[] next, prev;
    private int[] allOf;

    public String name;
    public int size;
    
    public Group(World world, int[] cids) {
        this.world = world;
        this.allOf = cids;
        
        StringBuilder sb = new StringBuilder("Group[");
        for(int i=0; i < allOf.length; i++) {
            if(i > 0) sb.append(", ");
            sb.append(CompType.map[allOf[i]]);
        }
        sb.append("]");
        name = sb.toString();

        int len = world.capacity + 2;
        next = new int[len];
        prev = new int[len];

        for (int i = 2; i < len; i++) {
            prev[i] = -1;
        }

        Log.info(name);
        for (int eid = world.eidIter(); eid != 0; eid = world.eidNext()) {
            update(eid);
        }
    }

    public boolean contains(int eid) {
        return prev[eid] != -1;
    }

    public final void update(int eid) {
        if (world.hasEntity(eid) && match(eid)) {
            add(eid);
        } else {
            remove(eid);
        }
    }

    public boolean match(int eid) {
        for (int cid : allOf) {
            if (!world.hasComponent(eid, cid)) {
                return false;
            }
        }
        return true;
    }
    
    // Here is the single thread iterator
    private int ceid;

    public final int iterator() {
        return ceid = next[0];
    }

    public final int next() {
        return ceid = next[ceid];
    }

    public final void add(int eid) {
        if (!contains(eid)) {
            Log.trace(toString() + " " + ID.get(eid));
            next[eid] = 0;
            prev[eid] = prev[0];
            next[prev[0]] = eid;
            prev[0] = eid;
            size++;
        }
    }

    public final void remove(int eid) {
        if (contains(eid)) {
            Log.trace(toString() + " " + ID.get(eid));
            next[prev[eid]] = next[eid];
            prev[next[eid]] = prev[eid];
            prev[eid] = -1;
            size--;
        }
    }

    public int left, right;
    
    public final Group pairIterator() {
        left = right = next[0];
        return this;
    }

    public final boolean nextPair() {
        if(next[right] == 0) {
            left = next[left];
            right = next[left];
            if(right == 0 || left == 0) {
                return false;
            }
        } else {
            right = next[right];
        }
        return true;
    }
    
    public String toString() {
        return name;
    }
}

