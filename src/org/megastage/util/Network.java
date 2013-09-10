package org.megastage.util;

import org.megastage.util.application.Log;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Logger;

public class Network {
    private final static Logger LOG = Logger.getLogger(Network.class.getName());

    private NetworkListener listener;

    private DatagramChannel channel;
    private ByteBuffer receiveBuffer = ByteBuffer.wrap(new byte[1024]);

    // TODO outbound must be synchronized
    private Queue<BroadcastMessage> outbound = new LinkedList<BroadcastMessage>();
    private HashSet<SocketAddress> remotes = new HashSet<SocketAddress>();

    public Network(NetworkListener listener, int port) {
        this.listener = listener;

        try {
            channel = DatagramChannel.open()
                    .setOption(StandardSocketOptions.SO_REUSEADDR, true);

            channel.configureBlocking(false);

            DatagramSocket socket = channel.socket();
            SocketAddress address = new InetSocketAddress(port);
            socket.bind(address);

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addRemote(SocketAddress address) {
        remotes.add(address);
    }

    public void removeRemote(SocketAddress address) {
        remotes.remove(address);
    }

    public void broadcast(ByteBuffer buf) {
        LOG.finer(buf.position() + " bytes to ALL remotes");
        outbound.add(new BroadcastMessage(buf));
    }

    public void unicast(SocketAddress remote, ByteBuffer buf) {
        LOG.finer(buf.position() + " bytes to " + remote.toString());
        outbound.add(new UnicastMessage(remote, buf));
    }

    public void tick() {
        // System.out.println("Network.tick");
        sendAll();
        receiveAll();
    }

    private void sendAll() {
        try {
            for(BroadcastMessage msg: outbound) {
                msg.buffer.flip();

                if(msg instanceof UnicastMessage) {
                    UnicastMessage unicastMessage = (UnicastMessage) msg;

                    LOG.finer(unicastMessage.buffer.remaining() + " bytes to " + unicastMessage.remote.toString());

                    channel.send(unicastMessage.buffer, unicastMessage.remote);
                } else {
                    for(SocketAddress s: remotes) {
                        LOG.finer(msg.buffer.remaining() + " bytes to " + s.toString());

                        channel.send(msg.buffer, s);
                    }
                }
            }

            outbound.clear();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }

    private void receiveAll() {
        try {
            while(true) {
                receiveBuffer.clear();
                SocketAddress client = channel.receive(receiveBuffer);

                if(client == null) {
                    break;
                }

                receiveBuffer.flip();

                listener.handleMessage(client, receiveBuffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class BroadcastMessage {
        public ByteBuffer buffer;

        private BroadcastMessage(ByteBuffer buffer) {
            this.buffer = buffer;
        }
    }

    private static class UnicastMessage extends BroadcastMessage {
        public SocketAddress remote;

        private UnicastMessage(SocketAddress remote, ByteBuffer buffer) {
            super(buffer);
            this.remote = remote;
        }
    }
}
