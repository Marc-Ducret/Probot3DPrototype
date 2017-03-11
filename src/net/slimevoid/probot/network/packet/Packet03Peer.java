package net.slimevoid.probot.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet03Peer extends Packet {
	
	private String peerAddr;
	private int peerPort;
	private int playerNumber;
	
	public Packet03Peer(String peerAddr, int peerPort, int playerNumber) {
		this.peerAddr = peerAddr;
		this.peerPort = peerPort;
		this.playerNumber = playerNumber;
	}
	
	public Packet03Peer() {
	}
	
	@Override
	public void read(DataInputStream in) throws IOException {
		peerAddr = in.readUTF();
		peerPort = in.readInt();
		playerNumber = in.read();
	}

	@Override
	public void write(DataOutputStream out) throws IOException {
		out.writeUTF(peerAddr);
		out.writeInt(peerPort);
		out.write(playerNumber);
	}
	
	public String getPeerAddr() {
		return peerAddr;
	}
	
	public int getPeerPort() {
		return peerPort;
	}
	
	public int getPlayerNumber() {
		return playerNumber;
	}
}
