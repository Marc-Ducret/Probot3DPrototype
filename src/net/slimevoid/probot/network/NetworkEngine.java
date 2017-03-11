package net.slimevoid.probot.network;

import java.util.ArrayList;
import java.util.List;

import net.slimevoid.probot.network.packet.Packet;

/**
 * Classe gérant le réseau, à implémenter pour le serveur et le client.
 * @author Marc
 *
 */
public abstract class NetworkEngine {

    /**
     * Liste des différents PacketManager
     */
    private List<PacketManager> packetManagers;

    public NetworkEngine() {
        packetManagers = new ArrayList<PacketManager>();
    }

	public void init() {
	}
	
	/**
	 * Appelle automatiquement la fonction tick réguliérement
	 */
	public void startUpdateThread() {
		new Thread(() -> {
			while(true) {
				tick();
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

    /**
     * Mise à jour des communications réseau (interprétation des packets...)
     */
    public void tick() {
        for(int i = 0; i < packetManagers.size(); i ++) {
            PacketManager packetManager = packetManagers.get(i);
            packetManager.update();
            if(!packetManager.isAlive()) {
                removePacketManager(packetManager);
                i--;
            }
        }
    }

    /**
     * Ajoute un PacketManager à {@link #packetManagers}.
     */
    protected void addPacketManager(PacketManager packetManager) {
        packetManagers.add(packetManager);
    }

    /**
     * Retire le PacketManager spécifié de {@link #packetManagers}
     * @param packetManager Le PacketManager à retirer
     */
    protected void removePacketManager(PacketManager packetManager) {
        packetManagers.remove(packetManager);
    }
    
    public void sendAll(Packet packet) {
    	for(PacketManager pm : packetManagers) {
    		pm.sendPacket(packet);
    	}
    }
}