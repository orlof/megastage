package org.megastage.components.transfer;

import org.megastage.client.ClientGlobals;
import org.megastage.ecs.ReplicatedComponent;
import org.megastage.server.FloppyManager;

public class VirtualFloppyDriveData extends ReplicatedComponent {
    public String[] bootroms;
    public String[] floppies;

    public static VirtualFloppyDriveData create() {
        VirtualFloppyDriveData data = new VirtualFloppyDriveData();
        data.bootroms = FloppyManager.getBootromNames();
        data.floppies = FloppyManager.getFloppyNames();
        return data;
    }

    @Override
    public void receive(int eid) {
        ClientGlobals.bootroms = bootroms;
        ClientGlobals.floppies = floppies;
    }
}
