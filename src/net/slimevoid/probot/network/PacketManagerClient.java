package net.slimevoid.probot.network;

import java.io.IOException;
import java.net.Socket;

import net.slimevoid.network.inputsync.InputSyncSocket;
import net.slimevoid.network.inputsync.InputSyncSocket.ClientType;
import net.slimevoid.probot.network.packet.Packet03Peer;

public class PacketManagerClient extends PacketManager {
	
    /**
     * Crée une PacketManager pour le socket spécifié.
     * @param socket Le socket avec qui le PacketManager va intéragir
     * @throws IOException L'exception est "lancée" quand il y a un probléme de conection avec le socket
     */
    public PacketManagerClient(Socket socket) throws IOException {
        super(socket);
    }
    
    @Override
    protected void handlePeer(Packet03Peer packet) {
    	super.handlePeer(packet);
    	new Thread(() -> {
    		System.out.println("Go!");//TODO rm
    		InputSyncSocket sok = new InputSyncSocket(packet.getPeerAddr(), 
										packet.getPeerPort(), 
										socket.getLocalPort(),
										ClientType.values()[packet.getPlayerNumber()-1]);
    		System.out.println("Connection: "+sok.connect(20));
    	}).start();
    }
    
	@Override
	protected void onDisconnect(String msg) {
		System.out.println("Disconnected "+msg);
	}
}