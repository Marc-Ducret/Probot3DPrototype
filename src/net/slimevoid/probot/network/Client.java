package net.slimevoid.probot.network;


/**
 * Classe représentant un client connecté au serveur
 * @author Marc
 *
 */
public class Client {
    /**
     * Un identifiant unique par client
     */
    private final long clientId;

    /**
     * Le nom du client
     */
    private String username;
    
    private static long lastClientId = 0;

    /**
     * Construit un client
     */
    public Client() {
        clientId = lastClientId ++;
    }

    /**
     * @return {@link #clientId}
     */
    public long getClientId() {
        return clientId;
    }

    /**
     * @param username {@link #username}
     */
    public void setUsername(final String username) {
        this.username = username;
    }
    
    /**
     * @return {@link #username}
     */
    public String getUsername() {
        return username;
    }
    
    public void error(String error) {
    }
}