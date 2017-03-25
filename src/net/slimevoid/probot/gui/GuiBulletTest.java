package net.slimevoid.probot.gui;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseInterface;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody.btRigidBodyConstructionInfo;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState;

import net.slimevoid.gl.gui.Gui;

public class GuiBulletTest extends Gui {
	
	public GuiBulletTest() {
		Bullet.init();
		System.out.println("Bullet init!");
		btDefaultCollisionConfiguration collisionConfiguration = new btDefaultCollisionConfiguration () ;
		btCollisionDispatcher dispatcher = new btCollisionDispatcher ( collisionConfiguration ) ;
		btBroadphaseInterface overlappingPairCache = new btDbvtBroadphase() ;
		btSequentialImpulseConstraintSolver solver = new btSequentialImpulseConstraintSolver();
		btDiscreteDynamicsWorld dynamicsWorld = new btDiscreteDynamicsWorld(dispatcher, overlappingPairCache, solver, collisionConfiguration) ;
		dynamicsWorld.setGravity(new Vector3(0, -10, 0));
		btRigidBody rb = new btRigidBody(new btRigidBodyConstructionInfo(1, new btDefaultMotionState(), new btBoxShape(new Vector3(1, 1, 1))));
		rb.applyImpulse(new Vector3(1, 0, 0), Vector3.Zero);
//		dynamicsWorld.addCollisionObject(rb);
		dynamicsWorld.addRigidBody(rb);
		
		for(;;) {
			tick(dynamicsWorld, rb);
		}
	}
	
	void tick(btDiscreteDynamicsWorld dynamicsWorld, btRigidBody rb) {
		System.out.println("tick!");
		dynamicsWorld.stepSimulation(1/60F, 10);
		Matrix4 trans = new Matrix4();
		rb.applyImpulse(new Vector3(1, 0, 0), Vector3.Zero);
//		rb.setWorldTransform(new Matrix4(new Vector3(5, 0, 0), new Quaternion(), new Vector3(1, 1, 1)));
//		rb.getMotionState().getWorldTransform(trans);
		trans = rb.getWorldTransform();
		Vector3 pos = new Vector3();
		trans.getTranslation(pos);
		System.out.println(": "+pos.x+", "+pos.y+" "+pos.z);
		try {Thread.sleep(100);} catch (InterruptedException e) {}
	}
}
