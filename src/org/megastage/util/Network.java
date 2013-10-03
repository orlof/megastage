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
    protected Queue<Message> outbound = new LinkedList<Message>();
    protected HashSet<SocketAddress> remotes = new HashSet<SocketAddress>();

    public Network(NetworkListener listener, int port) {
        this.listener = listener;

        try {
            channel = DatagramChannel.open()
                    .setOption(StandardSocketOptions.SO_REUSEADDR, true);

            channel.configureBlocking(false);

            SocketAddress address = port == 0 ? null: new InetSocketAddress(port);
            channel.socket().bind(address);

            LOG.info("local port bound to " + channel.socket().getLocalSocketAddress());

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

    public synchronized void broadcast(ByteBuffer buf) {
        LOG.finer(buf.position() + " bytes to ALL remotes");
        outbound.add(new Message(buf));
    }

    public synchronized void unicast(SocketAddress remote, ByteBuffer buf) {
        LOG.finer(buf.position() + " bytes to " + remote.toString());
        outbound.add(new Message(remote, buf));
    }

    public synchronized void multicast(HashSet<SocketAddress> receivers, ByteBuffer buf) {
        LOG.finer(buf.position() + " bytes to " + receivers.toString());
        outbound.add(new Message(receivers, buf));
    }

    public void tick() {
        // System.out.println("Network.tick");
        sendAll();
        receiveAll();
    }

    private synchronized void sendAll() {
        try {
            for(Message msg: outbound) {
                msg.buffer.flip();

                for(SocketAddress s: remotes) {
                    LOG.finer(msg.buffer.remaining() + " bytes to " + s.toString());

                    channel.send(msg.buffer, s);
                    msg.buffer.rewind();
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

    public class Message {
        ByteBuffer buffer;
        HashSet<SocketAddress> receivers;

        Message(ByteBuffer buffer) {
            this.receivers = remotes;
            this.buffer = buffer;
        }

        Message(HashSet<SocketAddress> receivers, ByteBuffer buffer) {
            this.receivers = receivers;
            this.buffer = buffer;
        }

        Message(SocketAddress receiver, ByteBuffer buffer) {
            this.receivers = new HashSet<SocketAddress>();
            this.receivers.add(receiver);
            this.buffer = buffer;
        }
    }
}
