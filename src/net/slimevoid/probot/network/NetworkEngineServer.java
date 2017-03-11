package net.slimevoid.probot.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;

import net.slimevoid.probot.network.packet.Packet;

/**
 * Déclinaison de NetworkEngine pour le server
 * @author Marc
 *
 */
public class NetworkEngineServer extends NetworkEngine {

    /**
     * Le serveur concret qui écoute
     */
    private ServerSocket serverSocket;

    /**
     * Une map qui fait corespondre les PacketManager à l'indentifiant unique de leur client
     */
    private Map<Long, PacketManager> mapPacketManagers;

    /**
     * Le thread qui attend les connections
     */
    private NetworkListener netListener;

    /**
     * Le port sur lequel le serveur écoute
     */
    private final int port;

    /**
     * Crée une instance de NetworkEngineServer
     * @param port Le port sur lequel écouter
     * @throws IOException Une exception est lancée si le socket ne peut pas être ouvert
     */
    public NetworkEngineServer(int port) {
        this.port = port;
    }

    @Override
    public void init() {
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(1000);
            netListener = new NetworkListener(serverSocket, this);
            netListener.start();
            mapPacketManagers = new HashMap<Long, PacketManager>();
            System.out.println("Server initialized on port: "+port);
        } catch (Exception e) {
            throw new RuntimeException("Server failed to init", e);
        }
    }

    /**
     * Ajoute un PacketManager à {@link NetworkEngine#packetManagers}.
     */
    public void addPacketManager(PacketManagerServer packetManager) {
        super.addPacketManager(packetManager);
        mapPacketManagers.put(packetManager.getClient().getClientId(), packetManager);
    }

    /**
     * Retire un PacketManager de {@link NetworkEngine#packetManagers}.
     * @param packetManager Le PacketManager à retirer
     */
    public void removePacketManager(PacketManagerServer packetManager) {
        super.removePacketManager(packetManager);
        mapPacketManagers.remove(packetManager.getClient().getClientId());
    }
    
    public void sendPacket(Packet packet, Client...clients) {
    	for(Client client : clients) {
    		mapPacketManagers.get(client.getClientId()).sendPacket(packet);
    	}
    }
    
    public PacketManager getPacketManager(Client client) {
    	return mapPacketManagers.get(client.getClientId());
    }
}