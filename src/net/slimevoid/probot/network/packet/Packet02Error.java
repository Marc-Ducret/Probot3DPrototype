package net.slimevoid.probot.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet02Error extends Packet {
	
	private String error;
	
	public Packet02Error() {
	}
	
	public Packet02Error(String error) {
		this.error = error;
	}

	@Override
	public void read(DataInputStream in) throws IOException {
		error = in.readUTF();
	}

	@Override
	public void write(DataOutputStream out) throws IOException {
		out.writeUTF(error);
	}
	
	public String getError() {
		return error;
	}
}
