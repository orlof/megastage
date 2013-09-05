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

    private Queue<ByteBuffer> outbound = new LinkedList<ByteBuffer>();
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
        outbound.add(buf);
    }

    public void tick() {
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
}
