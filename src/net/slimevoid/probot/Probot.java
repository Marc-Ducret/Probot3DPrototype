package net.slimevoid.probot;

import net.slimevoid.gl.GLInterface;
import net.slimevoid.probot.gui.GuiMainMenu;

public class Probot {
	
	public static void main(String[] args) throws InterruptedException {
		GLInterface.start(1280, 720, "Probot!", false);
		GLInterface.changeGui(new GuiMainMenu());
	}
}