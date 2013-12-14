/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.components;

import com.artemis.Entity;
import com.esotericsoftware.kryonet.Connection;
import org.megastage.components.client.ClientVideoMemory;
import org.megastage.components.dcpu.LEMUtil;
import org.megastage.systems.ClientNetworkSystem;
import org.megastage.util.ClientGlobals;
import org.megastage.util.RAM;

public class MonitorData extends EntityComponent {
    public char videoAddr = 0x8000;
    public RAM video = new RAM(LEMUtil.defaultVideo);

    public char fontAddr = 0x0000;
    public RAM font = new RAM(LEMUtil.defaultFont);

    public char paletteAddr = 0x0000;
    public RAM palette = new RAM(LEMUtil.defaultPalette);

    @Override
    public void receive(ClientNetworkSystem system, Connection pc, Entity entity) {
        ClientVideoMemory videoMemory = ClientGlobals.artemis.getComponent(entity, ClientVideoMemory.class);
        videoMemory.update(this);
    }

    
}
