package org.megastage.components.dcpu;

import org.megastage.components.BaseComponent;

public class EntityList extends BaseComponent {
    public int size = 0;
    public int[] eid = new int[50];

    public void add(int eid) {
        this.eid[size++] = eid;
    }

}
