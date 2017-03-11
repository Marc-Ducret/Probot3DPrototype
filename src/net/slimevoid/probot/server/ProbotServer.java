package net.slimevoid.probot.server;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import net.slimevoid.probot.network.Client;
import net.slimevoid.probot.network.NetworkEngineServer;
import net.slimevoid.probot.network.packet.Packet03Peer;

public class ProbotServer {
	
	public static ProbotServer instance;
	
	public NetworkEngineServer net;
	public List<Client> clients;
	private Client waiting;
	
	public ProbotServer(int port) {
		net = new NetworkEngineServer(port);
		net.init();
		clients = new ArrayList<>();
		waiting = null;
	}
	
	public void newClient(Client client) {
		clients.add(client);
	}
	
	public void playRequest(Client c) {
		System.out.println(c.getUsername()+" wants to play");
		if(waiting != null) {
			System.out.println("Initiate hole punching between "+waiting.getUsername()+" and "+c.getUsername()); //TODO rm?
			Socket sok = net.getPacketManager(waiting).getSocket();
			net.sendPacket(new Packet03Peer(getAddr(sok), sok.getPort(), 1), c);
			sok = net.getPacketManager(c).getSocket();
			net.sendPacket(new Packet03Peer(getAddr(sok), sok.getPort(), 2), waiting);
			waiting = null;
		} else {
			waiting = c;
		}
	}
	
	public static String getAddr(Socket sok) {
		String addr = sok.getInetAddress().getHostAddress();
		return addr.startsWith("127.0.0.1") ? "89.156.241.115" : addr; //TODO ??
	}
	
	public static void main(String[] args) {
		instance = new ProbotServer(8004); //TODO port? config?
		instance.net.startUpdateThread();
	}
}
