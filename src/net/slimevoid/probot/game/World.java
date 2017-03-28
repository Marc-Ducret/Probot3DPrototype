package net.slimevoid.probot.game;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseInterface;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;

public class World {
	
	private static final float TIMESTEP = 1/60F;
	
	private btDiscreteDynamicsWorld dynamicsWorld;
	private EntityList ents = new EntityList();
	
	public World() {
		init();
	}

	public void init() {
		Bullet.init();
		btDefaultCollisionConfiguration colConf = new btDefaultCollisionConfiguration();
		btCollisionDispatcher dispatcher = new btCollisionDispatcher(colConf);
		btBroadphaseInterface overlappingPairCache = new btDbvtBroadphase();
		btSequentialImpulseConstraintSolver solver = new btSequentialImpulseConstraintSolver();
		dynamicsWorld = new btDiscreteDynamicsWorld(dispatcher, overlappingPairCache, solver, colConf);
		dynamicsWorld.setGravity(new Vector3(0, -10, 0));
	}
	
	public void tick() {
		dynamicsWorld.stepSimulation(TIMESTEP);
		tickEnts();
	}
	
	public void tickEnts() {
		for(Entity e = ents.first; e != null; e = e.next) {
			e.tick();
		}
		while(ents.first != null && !ents.first.isAlive()) ents.removeFirst();
		for(Entity e = ents.first; e != null; e = e.next) {
			if(e.next != null && !e.next.isAlive()) ents.removeFollowing(e);
		}
	}
	
	public void addEntity(Entity e) {
		ents.addEntity(e);
		e.enterWorld(this);
	}
}

class EntityList {
	
	Entity first;
	
	public void addEntity(Entity e) {
		e.next = first;
		first = e;
	}
	
	public void removeFirst() {
		if(first != null) first = first.next;
	}
	
	public void removeFollowing(Entity prev) {
		if(prev.next != null && prev.next.next != null) prev.next = prev.next.next;
		else prev.next = null;
	}
}