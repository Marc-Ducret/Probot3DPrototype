package net.slimevoid.probot.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Packet vide qui ne sert qu'a vérifier que la connection reste en vie.
 * @author Marc
 */
public class Packet00KeepAlive extends Packet {

    @Override
    public void read(DataInputStream in) throws IOException {
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
    }
}

