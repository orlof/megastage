package org.megastage.components.device;

enum FloppyOperationType {
    NONE, READ, WRITE;
}

class FloppyOperation {
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
