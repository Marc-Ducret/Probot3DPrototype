package net.slimevoid.probot.gui;

import static java.lang.Math.max;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

import net.slimevoid.gl.GLInterface;
import net.slimevoid.gl.gui.Gui;
import net.slimevoid.gl.gui.Rectangle;
import net.slimevoid.network.inputsync.InputData;
import net.slimevoid.network.inputsync.InputSyncSocket;
import net.slimevoid.network.inputsync.InputSyncSocket.ClientType;

public class GuiPong extends Gui {
	
	public static GuiPong instance;
	
	private static final int[] KEY_MAP = new int[]{GLFW_KEY_W, GLFW_KEY_A, GLFW_KEY_S, GLFW_KEY_D};
	
	private int tickID = 0;
	private int inputTickID = 0;
	private final InputSyncSocket inputSync;
	private final boolean[] localKeys = new boolean[InputData.INPUT_SIZE];
	private long lastTick = -1;
	private long maxTickInterval = 0;
	private float avgTickInterval = 20000;
	private long lastTickIntervalReset;
	
	private Racket rA, rB;
	private Ball b;
	private int score;
	
	private String status = "";
	
	public GuiPong(InputSyncSocket inputSync) {
		this.inputSync = inputSync;
		instance = this;
		lastTickIntervalReset = GLInterface.getTimeMicro();
		initGame();
		new Thread(() -> {
			while(inputSync.isAlive()) {
				pongTick();
				timeTick();
			}
			status += " TIMEOUT";
			try{Thread.sleep(2000);} catch(Exception e) {}
			GLInterface.closeGui();
		}).start();
		new Thread(() -> {
			while(inputSync.isAlive()) {
				inputTick();
			}
		}).start();
	}
	
	private void initGame() {
		rA = new Racket(0, GLInterface.windowHeight/2 - Racket.H/2);
		rB = new Racket(GLInterface.windowWidth - Racket.W, GLInterface.windowHeight/2 - Racket.H/2);
		b = new Ball(GLInterface.windowWidth/2 - Ball.S/2, GLInterface.windowHeight/2 - Ball.S/2);
	}

	private void inputTick() {
		if(inputTickID <= tickID + 5) {
			InputData local = InputData.poolInputData(inputTickID);
			for(int i = 0; i < InputData.INPUT_SIZE; i ++) local.states[i] = localKeys[i];
			inputSync.provideLocalInputData(local);
			inputTickID++;
		}
		try{Thread.sleep(20);} catch(Exception e) {}
	}

	private void timeTick() {
		long t = GLInterface.getTimeMicro();
		if(lastTick > 0) {
			if(t - lastTickIntervalReset > 1000000) {
				lastTickIntervalReset = t;
				maxTickInterval = 0;
			}
			long len = t - lastTick;
			maxTickInterval = max(maxTickInterval, len);
			avgTickInterval = 0.99F * avgTickInterval + 0.01F * len;
		}
		lastTick = t;
		try{Thread.sleep(15);} catch(Exception e) {}
	}

	private void pongTick() {
		while(!inputSync.isInputDataReady()) {
			if(!inputSync.isAlive()) return;
			try{Thread.sleep(1);} catch(Exception e) {}
		}
		InputData me = inputSync.getLocalInputData(), him = inputSync.getDistantInputData();
		status = "meZ "+me.states[0]+" | himZ "+him.states[0];
		float dt = 1 / 50F;
		rA.tick(inputSync.type == ClientType.A ? me : him, dt);
		rB.tick(inputSync.type == ClientType.B ? me : him, dt);
		score += (inputSync.type == ClientType.A ? 1 : -1) * b.tick(dt, rA, rB);
		inputSync.freeOldestData();
		tickID ++;
	}
	
	@Override
	public void keyChanged(int keycode, int action) {
		super.keyChanged(keycode, action);
		synchronized(localKeys) {
			for(int i = 0; i < KEY_MAP.length; i ++) if(KEY_MAP[i] == keycode) {
				if(action == GLFW_PRESS) 	localKeys[i] = true;
				if(action == GLFW_RELEASE) 	localKeys[i] = false;
			}
		}
	}
	
	@Override
	public void draw() {
		super.draw();
		float interpolation = GLInterface.getInterpolation();
		rA.draw(interpolation);
		rB.draw(interpolation);
		b.draw(interpolation);
		GLInterface.addTextCenter("Score: "+score, "consolas", 16, GLInterface.windowWidth/2, GLInterface.windowHeight - 25, 0x800000);
		GLInterface.addText(status, "consolas", 16, 5, 5);
		GLInterface.addText("max "+(int)(maxTickInterval/1000F)+" | avg "+(int)(avgTickInterval/1000F)+" | "+(inputTickID - tickID)+" | T "+tickID, "consolas", 16, 5, 20);
		GLInterface.addRectangle(Rectangle.poolRectangle(0, 0, GLInterface.windowWidth, GLInterface.windowHeight).setTexture("probotTitle"));
	}
}
