package net.slimevoid.probot.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Classe qui reprÃ©sente un packet, qui en contient les donnÃ©es et qui intÃ©ragit avec les streams pour envoyer ces donnÃ©es. C'est une classe abstraite qui a pour seule vocation d'Ãªtre hÃ©ritÃ©e une fois par type de packet.
 * @author Marc
 */
public abstract class Packet {

    /**
     * L'identifiant du packet
     */
    private final int packetId;

    /**
     * La Map pour retrouver les classes Ã  partir de leur identifiant
     */
    private static Map<Integer, Class<? extends Packet>> idToPacketType = new HashMap<Integer, Class<? extends Packet>>();

    public Packet() {
        packetId = getPacketIdBasedOnClass();
    }

    /**
     * Cette fonction est appelÃ©e quand le packet est "lu". Il faut "lire" toutes les donnÃ©es "Ã©crite" dans la fonction write.
     * @param in : L'InputStream qui contiens les donnÃ©es
     * @throws IOException
     */
    public abstract void read(DataInputStream in) throws IOException;

    /**
     * Cette fonction est appelÃ©e quand le packet est "Ã©crit". Il faut "lire" toutes les donnÃ©es qui vont Ãªtre "lues" dans la fonction read.
     * @param out : L'OutputStream oÃ¹ l'on doit "Ã©crire"
     * @throws IOException
     */
    public abstract void write(DataOutputStream out) throws IOException;

    /**
     * @return {@link #packetId} Ã  partir de la classe en utilisant {@link #idToPacketType}
     */
    private int getPacketIdBasedOnClass() {
        Class<? extends Packet> classe = this.getClass();
        for(Entry<Integer, Class<? extends Packet>> entry : idToPacketType.entrySet()) {
            if(entry.getValue() == classe) {
                return entry.getKey();
            }
        }
        return -1;
    }

    /**
     * CrÃ©e un packet corespondant au type spÃ©cifiÃ© par l'indentifiant
     * @param id L'indentifiant reprÃ©sentant un type de packet
     * @return Le packet crÃ©e
     */
    public static Packet createPacket(int id) {
        if(!idToPacketType.containsKey(id)) {
            throw new IllegalArgumentException("packet "+id+" unregistered");
        }
        Class<? extends Packet> packetClass = idToPacketType.get(id);
        try {
            return packetClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("An excepetion is preventing a packet from being created", e);
        }
    }

    /**
     * Ajoute le type de packet spÃ©cifiÃ© Ã  {@link #idToPacketType} ce qui est nÃ©cÃ©saire pour qu'il soit utilisable.
     * @param id : L'indentifiant du type packet
     * @param classe : La classe du type de packet
     */
    private static void registerPacket(int id, Class<? extends Packet> classe) {
        idToPacketType.put(id, classe);
    }

    public static void registerPackets() {
        registerPacket( 0, Packet00KeepAlive.class);
        registerPacket( 1, Packet01Login.class);
        registerPacket( 2, Packet02Error.class);
        registerPacket( 3, Packet03Peer.class);
        registerPacket( 4, Packet04PlayRequest.class);
    }

    static {
        registerPackets();
    }
    
    /**
     * @return {@link #packetId}
     */
    public int getPacketId() {
        return packetId;
    }
}