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
	
	public World() {
		init();
	}

	public void init() {
		Bullet.init();
		btDefaultCollisionConfiguration collisionConfiguration = new btDefaultCollisionConfiguration () ;
		btCollisionDispatcher dispatcher = new btCollisionDispatcher(collisionConfiguration) ;
		btBroadphaseInterface overlappingPairCache = new btDbvtBroadphase() ;
		btSequentialImpulseConstraintSolver solver = new btSequentialImpulseConstraintSolver();
		dynamicsWorld = new btDiscreteDynamicsWorld(dispatcher, overlappingPairCache, solver, collisionConfiguration) ;
		dynamicsWorld.setGravity(new Vector3(0, -10, 0));
	}
	
	public void tick() {
		dynamicsWorld.stepSimulation(TIMESTEP);
	}
}
