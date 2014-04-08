package org.megastage.components.transfer;

import com.artemis.Entity;
import com.esotericsoftware.kryonet.Connection;
import java.util.HashMap;
import org.megastage.client.ClientGlobals;
import org.megastage.components.BaseComponent;
import org.megastage.components.dcpu.FloppyDisk;

public class VirtualFloppyDriveData extends BaseComponent {
    public String[] bootroms;
    public String[] floppies;

    public static VirtualFloppyDriveData create(HashMap<String, char[]> bootroms, HashMap<String, FloppyDisk> floppies) {
        VirtualFloppyDriveData data = new VirtualFloppyDriveData();
        data.bootroms = bootroms.keySet().toArray(new String[bootroms.size()]);
        data.floppies = floppies.keySet().toArray(new String[floppies.size()]);
        return data;
    }

    @Override
    public void receive(Connection pc, Entity entity) {
        ClientGlobals.bootroms = bootroms;
        ClientGlobals.floppies = floppies;
    }
}
