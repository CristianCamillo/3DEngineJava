package graphics;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;

public class SimpleGameEngine implements KeyListener, MouseListener
{
	/*********************************************************************/
	/* Setup variables                                                   */
	/*********************************************************************/
	
	private final short WIDTH;
	private final short HEIGHT;
	private final String TITLE;
	
	/*********************************************************************/
	/* Graphics                                                          */
	/*********************************************************************/
	
	private final JFrame frame;
	private final Canvas canvas;
	
	/*********************************************************************/
	/* Game Loop variables                                               */
	/*********************************************************************/
	
	private boolean running = true;
	private short frameCounter = 0;
	private boolean FPSLock = true;
	private boolean showFPS = true;
	private short FPSCap;
	
	private short FPS = 0;
	private long elapsedTime = 0;	
	
	/*********************************************************************/
	/* Keys                                                              */
	/*********************************************************************/
	
	private boolean enterKey = false;	
	private boolean escapeKey = false;	
	private boolean spaceKey = false;
	
	private boolean upKey = false;
	private boolean downKey = false;
	private boolean rightKey = false;
	private boolean leftKey = false;
	
	private boolean wKey = false;
	private boolean eKey = false;
	private boolean rKey = false;
	private boolean sKey = false;
	private boolean aKey = false;
	private boolean dKey = false;
	private boolean xKey = false;
	
	private boolean np2Key = false;
	private boolean np4Key = false;
	private boolean np6Key = false;
	private boolean np7Key = false;
	private boolean np8Key = false;
	private boolean np9Key = false;
	private boolean npplusKey = false;
	private boolean npminusKey = false;
	
	private boolean f1Key = false;
	private boolean f2Key = false;
	private boolean f3Key = false;
	private boolean f11Key = false;
	
	/*********************************************************************/
	/* Mouse                                                             */
	/*********************************************************************/
	
	private boolean leftClick = false;
	private boolean rightClick = false;
	
	/*********************************************************************/
	/* Toggle                                                            */
	/*********************************************************************/
	long start = 0;
	private boolean[] toggle = new boolean[300];
	private short pos;
	
	/*********************************************************************/
	/* Constructor                                                       */
	/*********************************************************************/
	
	public SimpleGameEngine(short width, short height, String title, short FPSCap) throws Exception
	{
		if(width < 1 || height < 1)
			throw new IllegalArgumentException("Width and height must be greater than 0.");
		
		if(title == null)
			throw new NullPointerException("The title cannot be null.");
		
		WIDTH = width;
		HEIGHT = height;
		TITLE = title;
		this.FPSCap = FPSCap;
		
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle(title);
		frame.setResizable(false);
		
		canvas = new Canvas();
		canvas.setSize(width, height);
		
		frame.add(canvas);
		frame.pack();
		frame.setLocationRelativeTo(null);		
		frame.setVisible(true);
		frame.setAlwaysOnTop(true);
		frame.setAlwaysOnTop(false);
		
		canvas.createBufferStrategy(2);
		
		frame.addKeyListener(this);
		canvas.addKeyListener(this);
		
		frame.addMouseListener(this);
		canvas.addMouseListener(this);
	
		long t0 = System.nanoTime();
		long t1 = t0;
		
		while(running)
		{
			start = System.nanoTime();
			
			update();
			render();
			drawGraphics();
			
			frameCounter++;
			
			if(t1 >= t0 + 1000000000)
			{			
				t0 = t1;
				FPS = frameCounter;
				frameCounter = 0;
				
				if(showFPS)
					frame.setTitle(TITLE + " - " + FPS + " FPS");
			}
				
			if(FPSLock)
			{
				long timeLeft = t1 + 1000000000 / FPSCap - System.nanoTime() - 1000000; // stabilize the framerate
				if(timeLeft > 0)
					Thread.sleep(timeLeft / 1000000, (int)(timeLeft % 1000000));
				while(System.nanoTime() < t1 + (1000000000f / FPSCap)); // stabilize the framerate
			}
			
			t1 = System.nanoTime();
			elapsedTime = t1 - start;
		}
		
		frame.dispose();
	}
	
	public void update() throws Exception {}
	public void render() throws Exception {}
	
	public final Graphics getGraphics()
	{
		Graphics g = canvas.getBufferStrategy().getDrawGraphics();
		g.clearRect(0, 0, WIDTH, HEIGHT);
		
		return g;
	}
	
	private void drawGraphics()
	{
		canvas.getBufferStrategy().show();
		canvas.getBufferStrategy().getDrawGraphics().dispose();
	}
	
	public final void stop()
	{
		running = false;
	}
	
	/*********************************************************************/
	/* Getters                                                           */
	/*********************************************************************/
	
	public final short getWidth()
	{
		return WIDTH;
	}
	
	public final short getHeight()
	{
		return HEIGHT;
	}
	
	public final String getTitle()
	{
		return TITLE;
	}
	
	public final short getFPS()
	{
		return FPS;
	}
	
	public final short getFPSCap()
	{
		return FPSCap;
	}
	
	public final boolean getFPSLock()
	{
		return FPSLock;
	}
	
	public final long getElapsedTime()
	{
		return elapsedTime;
	}
	
	public final short getMouseX()
	{
		return (short)(MouseInfo.getPointerInfo().getLocation().getX() - canvas.getLocationOnScreen().getX());
	}
	
	public final short getMouseY()
	{
		return (short)(MouseInfo.getPointerInfo().getLocation().getY() - canvas.getLocationOnScreen().getY());
	}
	
	public final boolean enterKey()     {pos = KeyEvent.VK_ENTER; return enterKey;}
	public final boolean escapeKey()	{pos = KeyEvent.VK_ESCAPE; return escapeKey;}
	public final boolean spaceKey()     {pos = KeyEvent.VK_SPACE; return spaceKey;}
	public final boolean upKey() 	 	{pos = KeyEvent.VK_UP; return upKey;}
	public final boolean downKey() 	 	{pos = KeyEvent.VK_DOWN; return downKey;}
	public final boolean rightKey()  	{pos = KeyEvent.VK_RIGHT; return rightKey;}
	public final boolean leftKey() 	 	{pos = KeyEvent.VK_LEFT; return leftKey;}
	public final boolean wKey()		 	{pos = KeyEvent.VK_W; return wKey;}
	public final boolean eKey()		 	{pos = KeyEvent.VK_E; return eKey;}
	public final boolean rKey()		 	{pos = KeyEvent.VK_R; return rKey;}
	public final boolean sKey()		 	{pos = KeyEvent.VK_S; return sKey;}
	public final boolean aKey()		 	{pos = KeyEvent.VK_A; return aKey;}
	public final boolean dKey()		 	{pos = KeyEvent.VK_D; return dKey;}
	public final boolean xKey()		 	{pos = KeyEvent.VK_X; return xKey;}
	public final boolean np2Key()	 	{pos = KeyEvent.VK_NUMPAD2; return np2Key;}
	public final boolean np4Key()	 	{pos = KeyEvent.VK_NUMPAD4; return np4Key;}
	public final boolean np6Key()	 	{pos = KeyEvent.VK_NUMPAD6; return np6Key;}
	public final boolean np7Key()	 	{pos = KeyEvent.VK_NUMPAD7; return np7Key;}
	public final boolean np8Key()	 	{pos = KeyEvent.VK_NUMPAD8; return np8Key;}
	public final boolean np9Key()	 	{pos = KeyEvent.VK_NUMPAD9; return np9Key;}
	public final boolean npplusKey() 	{pos = 107; return npplusKey;}
	public final boolean npminusKey()	{pos = 109; return npminusKey;}
	public final boolean f1Key() 		{pos = KeyEvent.VK_F1; return f1Key;}
	public final boolean f2Key() 		{pos = KeyEvent.VK_F2; return f2Key;}
	public final boolean f3Key() 		{pos = KeyEvent.VK_F3; return f3Key;}
	public final boolean f11Key() 		{pos = KeyEvent.VK_F11; return f11Key;}
	
	public final boolean leftClick() 	{pos = 298; return leftClick;}
	public final boolean rightClick() 	{pos = 299; return rightClick;}
	
	/*********************************************************************/
	/* Setters                                                           */
	/*********************************************************************/
	
	public final void setFPSLock(boolean FPSLock)
	{
		this.FPSLock = FPSLock;
	}
	
	public final void setTitle(String title)
	{
		frame.setTitle(title);
	}
	
	/*********************************************************************/
	/* Toggle                                                            */
	/*********************************************************************/
	
	public final boolean toggle(boolean key)
	{
		short pos = this.pos;
		
		if(key)
		{
			if(!toggle[pos])
			{
				toggle[pos] = true;
				return true;
			}
		}
		else
			toggle[pos] = false;
		
		return false;
	}
	
	/*********************************************************************/
	/* Events                                                            */
	/*********************************************************************/

	public final void keyPressed(KeyEvent e)
	{			
		switch(e.getKeyCode())
		{
			case KeyEvent.VK_ENTER:   enterKey = true; return;
			case KeyEvent.VK_ESCAPE:  escapeKey = true; return;
			case KeyEvent.VK_SPACE:   spaceKey = true; return;
			case KeyEvent.VK_UP: 	  upKey = true; return;
			case KeyEvent.VK_DOWN:    downKey = true; return;
			case KeyEvent.VK_RIGHT:   rightKey = true; return;
			case KeyEvent.VK_LEFT: 	  leftKey = true; return;
			case KeyEvent.VK_W:       wKey = true; return;
			case KeyEvent.VK_E:       eKey = true; return;
			case KeyEvent.VK_R:       rKey = true; return;
			case KeyEvent.VK_S:  	  sKey = true; return;
			case KeyEvent.VK_A:       aKey = true; return;
			case KeyEvent.VK_D:       dKey = true; return;
			case KeyEvent.VK_X:       xKey = true; return;
			case KeyEvent.VK_NUMPAD2: np2Key = true; return;
			case KeyEvent.VK_NUMPAD4: np4Key = true; return;
			case KeyEvent.VK_NUMPAD6: np6Key = true; return;
			case KeyEvent.VK_NUMPAD7: np7Key = true; return;
			case KeyEvent.VK_NUMPAD8: np8Key = true; return;
			case KeyEvent.VK_NUMPAD9: np9Key = true; return;
			case 107:                 npplusKey = true; return; // NUMPAD_PLUS
			case 109:                 npminusKey = true; return; // NUMPAD_MINUS
			case KeyEvent.VK_F1:      f1Key = true; return;
			case KeyEvent.VK_F2:      f2Key = true; return;
			case KeyEvent.VK_F3:      f3Key = true; return;
			case KeyEvent.VK_F11:     f11Key = true; return;
		}
	}

	public final void keyReleased(KeyEvent e)
	{
		switch(e.getKeyCode())
		{
			case KeyEvent.VK_ENTER:   enterKey = false; return;
			case KeyEvent.VK_ESCAPE:  escapeKey = false; return;
			case KeyEvent.VK_SPACE:   spaceKey = false; return;
			case KeyEvent.VK_UP: 	  upKey = false; return;
			case KeyEvent.VK_DOWN:    downKey = false; return;
			case KeyEvent.VK_RIGHT:   rightKey = false; return;
			case KeyEvent.VK_LEFT: 	  leftKey = false; return;
			case KeyEvent.VK_W:       wKey = false; return;
			case KeyEvent.VK_E:       eKey = false; return;
			case KeyEvent.VK_R:       rKey = false; return;
			case KeyEvent.VK_S:  	  sKey = false; return;
			case KeyEvent.VK_A:       aKey = false; return;
			case KeyEvent.VK_D:       dKey = false; return;
			case KeyEvent.VK_X:       xKey = false; return;
			case KeyEvent.VK_NUMPAD2: np2Key = false; return;
			case KeyEvent.VK_NUMPAD4: np4Key = false; return;
			case KeyEvent.VK_NUMPAD6: np6Key = false; return;
			case KeyEvent.VK_NUMPAD7: np7Key = false; return;
			case KeyEvent.VK_NUMPAD8: np8Key = false; return;
			case KeyEvent.VK_NUMPAD9: np9Key = false; return;
			case 107:                 npplusKey = false; return; // NUMPAD_PLUS
			case 109:                 npminusKey = false; return; // NUMPAD_MINUS
			case KeyEvent.VK_F1:      f1Key = false; return;
			case KeyEvent.VK_F2:      f2Key = false; return;
			case KeyEvent.VK_F3:      f3Key = false; return;
			case KeyEvent.VK_F11:     f11Key = false; return;
		}
	}
	
	public void mousePressed(MouseEvent e)
	{
		switch(e.getButton())
		{
			case 1: leftClick = true; return;
			case 3: rightClick = true; return;
		}
	}


	public void mouseReleased(MouseEvent e)
	{
		switch(e.getButton())
		{
			case 1: leftClick = false; return;
			case 3: rightClick = false; return;
		}
	}
	
	public final void keyTyped(KeyEvent e){}
	public void mouseClicked(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}	
}
