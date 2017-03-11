package net.slimevoid.probot.network;

import java.io.IOException;
import java.net.Socket;

import net.slimevoid.probot.network.packet.Packet01Login;
import net.slimevoid.probot.network.packet.Packet04PlayRequest;
import net.slimevoid.probot.server.ProbotServer;

public class PacketManagerServer extends PacketManager {

    /**
     * Le client corespondant au PacketManager
     */
    private final Client client;

    /**
     * Crée une PacketManager pour le socket spécifié.
     * @param socket Le socket avec qui le PacketManager va intéragir
     * @throws IOException L'exception est "lancée" quand il y a un probléme de conection avec le socket
     */
    public PacketManagerServer(Socket socket, Client client) throws IOException {
        super(socket);
        this.client = client;
    }

    /**
     * @return {@link #client}
     */
    public Client getClient() {
        return client;
    }

    @Override
    protected void handleLogin(Packet01Login packet) {
    	client.setUsername(packet.getUsername());
    	System.out.println("Client "+packet.getUsername()+" connected");
    	ProbotServer.instance.newClient(client);
    }
    
    @Override
    protected void handlePlayRequest(Packet04PlayRequest packet) {
    	super.handlePlayRequest(packet);
    	ProbotServer.instance.playRequest(client);
    }
    
    public String getHost(PacketManager pm) {
    	String host = pm.getSocket().getInetAddress().getHostAddress();
    	return host;
    }
    
	@Override
	protected void onDisconnect(String msg) {
		System.out.println("Client "+client.getUsername()+" disconnected ("+msg+")");
	}
}