package net.slimevoid.probot.gui;

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

public class GuiPong extends Gui {
	
	public static GuiPong instance;
	
	private static final int[] KEY_MAP = new int[]{GLFW_KEY_W, GLFW_KEY_A, GLFW_KEY_S, GLFW_KEY_D};
	
	private int tickID = -1;
	private final InputSyncSocket inputSync;
	private final boolean[] localKeys = new boolean[InputData.INPUT_SIZE];
	
	private String status = "";
	
	public GuiPong(InputSyncSocket inputSync) {
		this.inputSync = inputSync;
		instance = this;
		new Thread(() -> {
			while(inputSync.isAlive()) {
				pongTick();
				try{Thread.sleep(20);} catch(Exception e) {}
			}
			status += " TIMEOUT";
		}).start();
	}
	
	private void pongTick() {
		if(tickID >= 0) {
			while(!inputSync.isInputDataReady()) {
				if(!inputSync.isAlive()) return;
				try{Thread.sleep(1);} catch(Exception e) {}
			}
			InputData me = inputSync.getLocalInputData(), him = inputSync.getDistantInputData();
			status = "meZ "+me.states[0]+" | himZ "+him.states[0];
			inputSync.freeOldestData();
		}
		InputData local = InputData.poolInputData(tickID+1);
		for(int i = 0; i < InputData.INPUT_SIZE; i ++) local.states[i] = localKeys[i];
		inputSync.provideLocalInputData(local);
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
		GLInterface.addText(status, "consolas", 16, 5, 5);
		GLInterface.addRectangle(Rectangle.poolRectangle(0, 0, GLInterface.windowWidth, GLInterface.windowHeight).setTexture("probotTitle"));
	}
}
