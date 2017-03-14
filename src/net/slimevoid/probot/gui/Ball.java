package net.slimevoid.probot.gui;

import net.slimevoid.gl.GLInterface;
import net.slimevoid.gl.gui.Rectangle;

public class Ball {
	
	public static final int S = 10;
	private static final int SPEED = 200;
	
	public float x, y, prevX, prevY;
	public float mx = SPEED, my = SPEED;
	
	public Ball(float x, float y) {
		this.x = prevX = x;
		this.y = prevY = y;
	}
	
	public void draw(float interpolation) {
		GLInterface.addRectangle(Rectangle.poolRectangle(
				x*interpolation + prevX*(1-interpolation), 
				y*interpolation + prevY*(1-interpolation), S, S)
				.setColor(0x0));
	}
	
	private void move(float dx, float dy) {
		prevX = this.x; prevY = this.y;
		this.x += dx; this.y += dy;
	}
	
	public int tick(float dt, Racket rA, Racket rB) {
		move(mx*dt, my*dt);
		if((y <= 0 && my < 0) || (y + S >= GLInterface.windowHeight && my > 0)) {
			move(0, -my*dt);
			my = -my;
		}
		if((rA.inter(this) && mx < 0) || (rB.inter(this) && mx > 0)) {
			move(-mx*dt, 0);
			mx = -mx;
		} else if(x <= 0 && mx < 0) {
			move(-mx*dt, 0);
			mx = -mx;
			return -1;
		} else if(x + S >= GLInterface.windowWidth && mx > 0) {
			move(-mx*dt, 0);
			mx = -mx;
			return 1;
		}
		return 0;
	}
}
