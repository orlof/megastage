package org.megastage.components.device;

import org.megastage.client.ClientGlobals;
import org.megastage.ecs.BaseComponent;
import org.megastage.ecs.DirtyComponent;
import org.megastage.protocol.Carrier;
import org.megastage.protocol.ComponentMessage;
import org.megastage.protocol.Message;
import org.megastage.server.FloppyManager;

public class DeviceFloppyDrive extends BaseComponent implements Carrier {
    private transient int version = 0;

    public String[] bootroms;
    public String[] floppies;

    @Override
    public void receive(int eid) {
        ClientGlobals.bootroms = bootroms;
        ClientGlobals.floppies = floppies;
    }

    @Override
    public Message replicate(int eid) {
        if(FloppyManager.version > version) {
            version = FloppyManager.version;
            bootroms = FloppyManager.getBootromNames();
            floppies = FloppyManager.getFloppyNames();

            return new ComponentMessage(eid, this);
        }

        return null;
    }

}
