package org.megastage.components.device;

import org.megastage.ecs.ToStringComponent;

enum FloppyOperationType {
    NONE, READ, WRITE;
}

class FloppyOperation extends ToStringComponent {
    FloppyOperationType type;
    int sector;
    int memory;
    long finish;

    public FloppyOperation() {}
    
    public FloppyOperation(FloppyOperationType type, int sector, int memory, long cycles) {
        this.type = type;
        this.sector = sector;
        this.memory = memory;
        this.finish = cycles;
    }
}
