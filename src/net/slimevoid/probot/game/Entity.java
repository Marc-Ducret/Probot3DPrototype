package net.slimevoid.probot.game;

public class Entity {
	
	public Entity next;
	
	private World world;
	private boolean alive;
	
	public void enterWorld(World world) {
		this.world = world;
		alive = true;
	}
	
	public void tick() {
	}
	
	public boolean isAlive() {
		return alive;
	}

	public World getWorld() {
		return world;
	}
}
