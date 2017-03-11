package net.slimevoid.probot.gui;

import net.slimevoid.gl.GLInterface;
import net.slimevoid.gl.gui.Component;
import net.slimevoid.gl.gui.Gui;
import net.slimevoid.gl.gui.Rectangle;
import net.slimevoid.gl.gui.component.Button;
import net.slimevoid.probot.network.NetworkEngineClient;
import net.slimevoid.probot.network.packet.Packet01Login;
import net.slimevoid.probot.network.packet.Packet04PlayRequest;

public class GuiMainMenu extends Gui {

	private static final int bW = 200, bH = 60, bM = 20;
	
	private final Button buttonFight, buttonBuild;
	
	public GuiMainMenu() {
		buttonFight = new Button("Fight", () -> {
			System.out.println("Fight!");//TODO rm
			NetworkEngineClient net = new NetworkEngineClient();
			if(!net.connect("89.156.241.115", 8004)) {
				System.out.println("Connection unsuccessful :("); //TODO addr?
				return;
			}
			net.sendAll(new Packet01Login(System.getProperty("user.name")+Integer.toHexString((int) (System.currentTimeMillis() % 0xFF))));
			net.sendAll(new Packet04PlayRequest());
			net.startUpdateThread();
		});
		buttonBuild = new Button("Build", () -> {
			System.out.println("Build!");
		});
		buttonFight.constrain(Component.N, null, Component.N, -300);
		buttonFight.constrain(Component.W, null, Component.W, GLInterface.windowWidth / 2 - bW / 2);
		buttonFight.setSize(bW, bH);
		buttonBuild.constrain(Component.N, buttonFight, Component.S, -bM);
		buttonBuild.constrain(Component.W, buttonFight, Component.W, 0);
		buttonBuild.setSize(bW, bH);
		addComponent(buttonFight);
		addComponent(buttonBuild);
	}
	
	@Override
	public void draw() {
		super.draw();
		GLInterface.addRectangle(Rectangle.poolRectangle(0, 0, GLInterface.windowWidth, GLInterface.windowHeight).setTexture("probotTitle"));
	}
}
