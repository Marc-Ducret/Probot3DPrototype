package net.slimevoid.probot.gui;

import net.slimevoid.gl.GLInterface;
import net.slimevoid.gl.gui.Rectangle;
import net.slimevoid.network.inputsync.InputData;

public class Racket {
	
	public static final int W = 10, H = 100;
	public static final int SPEED = 100;
	
	public int x;
	public int y, prevY;
	
	public Racket(int x, int y) {
		this.x = x;
		this.y = prevY = y;
	}
	
	public void draw(float interpolation) {
		GLInterface.addRectangle(Rectangle.poolRectangle(x, y*interpolation+prevY*(1-interpolation), W, H).setColor(0x0));
	}
	
	private void move(float dy) {
		prevY = this.y;
		this.y += dy;
	}
	
	public boolean inter(Ball ball) {
		return !(ball.x > x + W || ball.x + Ball.S < x || ball.y > y + H || ball.y + Ball.S < y);
	}
	
	public void tick(InputData in, float dt) {
		float dy = 0;
		if(in.states[0]) dy += 1;
		if(in.states[2]) dy -= 1;
		dy *= SPEED*dt;
		move(dy);
	}
}
