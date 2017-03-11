package net.slimevoid.probot.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Fait écouter un ServerSocket et ajoute les nouveau clients
 * @author Marc
 *
 */
public class NetworkListener extends Thread {

    /**
     * Le server qui va écouter
     */
    private final ServerSocket server;

    /**
     * L'instance de NetworkEngineServer
     */
    private final NetworkEngineServer netManager;

    /**
     * @param server Le server qui va écouter
     */
    public NetworkListener(ServerSocket server, NetworkEngineServer netManager) {
        this.server = server;
        this.netManager = netManager;
    }

    @Override
    public void run() {
        while(true) {
            try {
                Socket socket = server.accept();
                socket.setSoTimeout(1000);
                Client client = new Client();
                netManager.addPacketManager(new PacketManagerServer(socket, client));
            } catch (IOException e) {
            }
        }
    }
}

