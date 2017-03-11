package net.slimevoid.probot.network;

import java.net.Socket;

/**
 * Déclinaison de NetworkEngine pour le client
 * @author Marc
 *
 */
public class NetworkEngineClient extends NetworkEngine {

    /**
     * Le packet manager gérant la connection avec le serveur; vaut null si non connecté
     */
    public PacketManager server;

    @Override
    public void init() {
    }

    /**
     * Tente d'établir une connection au server spécifié
     * @param host IP ou Hostname du serveur
     * @param port Port du serveur
     */
    public boolean connect(String host, int port) {
        try {
            PacketManagerClient pm = new PacketManagerClient(new Socket(host, port));
            addPacketManager(pm);
            server = pm;
            System.out.println("Connection to "+host+":"+port+" sucessful");
            return true;
        } catch(Exception e) {
        	System.out.println("Connection to "+host+":"+port+" failed ("+e+")");
            return false;
        }
    }
}
