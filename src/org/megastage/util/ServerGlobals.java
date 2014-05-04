package org.megastage.util;

import org.megastage.ecs.World;
import org.megastage.protocol.Message;
import org.megastage.server.FloppyManager;

/**
 * MegaStage
 * User: Orlof
 * Date: 17.8.2013
 * Time: 20:50
 */
public class ServerGlobals {
    public static Bag<Message> updates = new Bag<>(100);
    public static World world;
    public static FloppyManager floppyManager;

    public static Bag<Message> getUpdates() {
        if(updates.size == 0) {
            return null;
        }
        Bag<Message> old = updates;
        updates = new Bag<>(100);
        return old;
    }
}

