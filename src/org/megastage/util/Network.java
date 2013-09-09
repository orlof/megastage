package org.megastage.util;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

public class Network {
    private NetworkListener listener;

    private DatagramChannel channel;
    private ByteBuffer receiveBuffer = ByteBuffer.wrap(new byte[1024]);

    // TODO outbound must be synchronized
    private Queue<ByteBuffer> outbound = new LinkedList<ByteBuffer>();
    private Queue<Pair> outboundSingle = new LinkedList<Pair>();
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

    public void send(ByteBuffer buf) {
        System.out.println("Network.send");
        outbound.add(buf);
    }

    public void send(SocketAddress remote, ByteBuffer buf) {
        System.out.println("Network.send");
        outboundSingle.add(new Pair(remote, buf));
    }

    public void tick() {
        System.out.println("Network.tick");
        sendAll();
        receiveAll();
    }

    private void sendAll() {
        try {
            for(ByteBuffer buffer: outbound) {
                buffer.flip();

                for(SocketAddress s: remotes) {
                    System.out.println("Network.sendAll sending " + buffer.remaining() + " bytes to " + s.toString());
                    channel.send(buffer, s);
                }
            }

            outbound.clear();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        try {
            for(Pair pair: outboundSingle) {
                pair.msg.flip();

                System.out.println("Network.sendAll sending " + pair.msg.remaining() + " bytes to " + pair.remote.toString());
                channel.send(pair.msg, pair.remote);
            }

            outboundSingle.clear();
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
    
    private class Pair {
        private Pair(SocketAddress remote, ByteBuffer msg) {
            this.remote = remote;
            this.msg = msg;
        }

        SocketAddress remote;
        ByteBuffer msg;
    }
}
