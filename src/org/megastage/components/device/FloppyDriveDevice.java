package org.megastage.components.device;

import org.megastage.client.ClientGlobals;
import org.megastage.protocol.Message;
import org.megastage.server.FloppyManager;

public class FloppyDriveDevice extends Device {
    private transient int version = 0;

    public String[] bootroms;
    public String[] floppies;

    public boolean isDirty(int eid) {
        return FloppyManager.version > version;
    }

    @Override
    public void receive(int eid) {
        ClientGlobals.bootroms = bootroms;
        ClientGlobals.floppies = floppies;
    }
    @Override
    public Message synchronize(int eid) {
        version = FloppyManager.version;
        bootroms = FloppyManager.getBootromNames();
        floppies = FloppyManager.getFloppyNames();

        return super.synchronize(eid);
    }

}
