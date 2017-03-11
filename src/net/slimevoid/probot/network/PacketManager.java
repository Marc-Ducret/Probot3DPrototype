package net.slimevoid.probot.network;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import net.slimevoid.probot.network.packet.Packet;
import net.slimevoid.probot.network.packet.Packet00KeepAlive;
import net.slimevoid.probot.network.packet.Packet01Login;
import net.slimevoid.probot.network.packet.Packet02Error;
import net.slimevoid.probot.network.packet.Packet03Peer;
import net.slimevoid.probot.network.packet.Packet04PlayRequest;

/**
 * La classe qui gére l'envoi et la récéption de packet et qui les interprète. Cette classe est instanciée une fois par Socket et est héritée une fois pour le serveur et une fois pour le client.
 * @author Marc
 *
 */
public abstract class PacketManager implements Runnable {

    protected final Socket socket;

    /**
     * Liste des packets en attente d'envoi.
     */
    private List<Packet> sendQueue;

    /**
     * Liste des packets récéptionés en attente d'interprétation.
     */
    private List<Packet> recievedPackets;

    /**
     * L'InputStream correspondant à {@link #socket}
     */
    private DataInputStream in;

    /**
     * L'OutputStream correspondant à {@link #socket}
     */
    private DataOutputStream out;

    /**
     * Le thread gérant les communication réseau
     */
    private Thread networker;

    /**
     * Spécifie si la connection est toujours active
     */
    private boolean alive;

    /**
     * Timestamp du dernier envoi d'un KeepAlive
     * @see Packet00KeepAlive
     */
    private long lastKeepAlive;
    
    private long lastReceivedKeepAlive;

    /**
     * Crée une PacketManager pour le socket spécifié.
     * @param socket Le socket avec qui le PacketManager va intéragir
     * @throws IOException L'exception est "lancée" quand il y a un probléme de conection avec le socket
     */
    public PacketManager(Socket socket) throws IOException {
        this.socket = socket;
        this.sendQueue = new ArrayList<Packet>();
        this.recievedPackets = new ArrayList<Packet>();
        this.in = new DataInputStream(new BufferedInputStream(this.socket.getInputStream()));
        this.out = new DataOutputStream(new BufferedOutputStream(this.socket.getOutputStream()));
        this.alive = true;
        this.lastKeepAlive = 0;
        this.lastReceivedKeepAlive = System.currentTimeMillis() + 10000;
        this.networker = new Thread(this);
        this.networker.start();
    }

    @Override
    public void run() {
        try {
            while(true) {
                // Write
                if(sendQueue.size() > 0) {
                    Packet packet = sendQueue.remove(0);
                    if(packet != null) {
                    	out.write(packet.getPacketId());
                    	packet.write(out);
                    	out.flush();
                    } else {
                    	System.out.println("PACKET NULL WTF");
                    }
                } else if(!alive) {
                	break;
                }

                // Read
                if(in.available() > 0) {
                    int packetId = in.read();
                    Packet packet = Packet.createPacket(packetId);
                    packet.read(in);
                    recievedPackets.add(packet);
                }
                Thread.sleep(1);
            }
        } catch(Exception e) {
        	if(!(e instanceof IOException)) {
        		e.printStackTrace();
        	}
        	destroy(e.toString());
        }
        try {
			in.close();
			out.close();
		} catch (IOException e) {
		}
    }

    /**
     * Fonction appelée uniquement à chaque "tick". Elle a pour effet de demander l'intérprétation de tous les packets dans {@link #recievedPackets} en appelant {@link #handlePacket(Packet)}
     */
    public void update() {
        if(System.currentTimeMillis() - lastKeepAlive > 1000) {
            sendPacket(new Packet00KeepAlive());
            lastKeepAlive = System.currentTimeMillis();
        }
        if(System.currentTimeMillis() - lastReceivedKeepAlive > 4000) {
        	destroy("Timeout");
        }
        while(recievedPackets.size() > 0) {
            handlePacket(recievedPackets.remove(0));
        }
    }

    /**
     * Fonction qui intépréte les packets.
     * @param packet Le packet à interpréter
     */
    private void handlePacket(Packet packet) {
        switch(packet.getPacketId()) {
        case 0: lastReceivedKeepAlive = System.currentTimeMillis(); break;
        case 1: handleLogin((Packet01Login) packet); break;
        case 2: handleError((Packet02Error) packet); break;
        case 3: handlePeer((Packet03Peer) packet); break;
		case 4: handlePlayRequest((Packet04PlayRequest) packet); break;
    	}
    }

    protected void handleLogin(Packet01Login packet) {}
    protected void handleError(Packet02Error packet) {}
    protected void handlePeer(Packet03Peer packet) {}
    protected void handlePlayRequest(Packet04PlayRequest packet) {}
    
    /**
     * Romps la connection
     */
    public void destroy(String msg) {
    	onDisconnect(msg);
        alive = false;
    }
    
    protected abstract void onDisconnect(String msg) ;

    /**
     * Envoie le packet spécifié au client associé au PacketManager
     * @param packet Le packet à envoyer
     */
    public void sendPacket(Packet packet) {
    	if(packet == null) {
    		new Exception("Packet null").printStackTrace();
    	} else if(alive) {
    		sendQueue.add(packet);
    	}
    }

    /**
     * @return {@link #alive}
     */
    public boolean isAlive() {
        return alive;
    }
    
    public Socket getSocket() {
		return socket;
	}
}