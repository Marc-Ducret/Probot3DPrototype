package net.slimevoid.probot.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Packet que le client envoi lors de sa connection au serveur dans le but de préciser son nom d'utilisateur
 * @author Marc
 *
 */
public class Packet01Login extends Packet {

    /**
     * Le nom du joueur se connectant
     */
    private String username;

    /**
     * Constructeur appelé lorsque le packet est reçu
     */
    public Packet01Login() {
    }

    /**
     * Construit un packet login
     * @param username {@link #username}
     */
    public Packet01Login(String username) {
        this.username = username;
    }

    @Override
    public void read(DataInputStream in) throws IOException {
    	username = in.readUTF();
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeUTF(username);
    }
    
    /**
     * @return {@link #username}
     */
    public String getUsername() {
        return username;
    }
}