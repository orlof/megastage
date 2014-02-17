package org.megastage.util;

import com.artemis.World;
import com.badlogic.gdx.utils.Array;
import org.megastage.protocol.Message;

/**
 * MegaStage
 * User: Orlof
 * Date: 17.8.2013
 * Time: 20:50
 */
public class ServerGlobals {
    public static Array<Message> updates = new Array<>(100);
    public static World world;

    public static Array<Message> getUpdates() {
        if(updates.size == 0) {
            return null;
        }
        Array<Message> old = updates;
        updates = new Array<>(100);
        return old;
    }
}

