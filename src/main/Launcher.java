package main;

public class Launcher
{	
	public static void main(String[] args) throws Exception
	{
		System.setProperty("sun.java2d.opengl", "true");
		new Main();
	}
}
