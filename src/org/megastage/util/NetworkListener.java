package org.megastage.util;

import java.net.SocketAddress;
import java.nio.ByteBuffer;

public interface NetworkListener {
    public void handleMessage(SocketAddress remote, ByteBuffer buf);
}
