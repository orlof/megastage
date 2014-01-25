package org.megastage.protocol;

import com.artemis.Component;
import com.artemis.Entity;
import com.esotericsoftware.kryonet.Connection;

/**
 * Created with IntelliJ IDEA.
 * User: contko3
 * Date: 10/3/13
 * Time: 6:50 AM
 * To change this template use File | Settings | File Templates.
 */
public class PlayerConnection extends Connection {
    public Entity player;
    public Component item;
}
