package org.megastage.components.srv;

import java.util.LinkedList;
import org.megastage.components.BlockChange;
import org.megastage.ecs.ReplicatedComponent;
import org.megastage.protocol.Message;

public class BlockChanges extends ReplicatedComponent {
    public LinkedList<BlockChange> changes = new LinkedList<>();

    @Override
    public boolean isReplicable() {
        return false;
    }
    
    @Override
    public boolean isDirty() {
        return !changes.isEmpty();
    }

    @Override
    public Message synchronize(int eid) {
        BlockChange change = changes.remove();
        return change.synchronize(eid);
    }
}
