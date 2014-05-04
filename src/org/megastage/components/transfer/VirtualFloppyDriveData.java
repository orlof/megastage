package org.megastage.components.transfer;

import com.esotericsoftware.kryonet.Connection;
import org.megastage.client.ClientGlobals;
import org.megastage.components.BaseComponent;
import org.megastage.ecs.World;
import org.megastage.server.FloppyManager;

public class VirtualFloppyDriveData extends BaseComponent {
    public String[] bootroms;
    public String[] floppies;

    public static VirtualFloppyDriveData create() {
        VirtualFloppyDriveData data = new VirtualFloppyDriveData();
        data.bootroms = FloppyManager.getBootromNames();
        data.floppies = FloppyManager.getFloppyNames();
        return data;
    }

    @Override
    public void receive(World world, Connection pc, int eid) {
        ClientGlobals.bootroms = bootroms;
        ClientGlobals.floppies = floppies;
    }
}
