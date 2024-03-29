package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import graphics.Camera;
import graphics.DepthBuffer;
import graphics.MathUtils;
import graphics.Mesh;
import graphics.PostProcessingEffects;
import graphics.RenderingPipeline;
import graphics.SimpleEngine;
import graphics.Vect3D;

public class Main
{
	private int width = 1280;
	private int height = 720;
	private String title = "3D Engine Attempt No. 100";
	private boolean fpsLock = false;
	private short fpsCap = 60;
	private boolean showFps = true;
	private boolean fullscreen = false;
	
	private final static byte N_THREAD = 1;
	
	private float[][] mProj = MathUtils.projMat(90, height * 1f / width, 0.1f, 1000f);
	
	private final Camera defaultCamera = new Camera(0.5f, 0.5f, -1f);
	private Camera camera = defaultCamera.clone();
	
	private int pixelSize = 2;
	
	private DepthBuffer db = new DepthBuffer(width / pixelSize, height / pixelSize);
	
	private ArrayList<Mesh> meshes = new ArrayList<Mesh>();
	
	private Vect3D vLight = MathUtils.normVec(new Vect3D(1f, 1f, -1f));

	private Color bgc = new Color(0, 191, 255);
	
	private final float movMult = 5f;
	private final float rotMult = 2f;
	

	private boolean showDebug = false;

	
	
	
	
	public Main() throws Exception
	{
		Mesh mesh0 = new Mesh("meshes//texturedCube.obj", true, Color.WHITE, "textures//player0.png");
		//Mesh mesh0 = new Mesh("meshes//cube.obj", false, Color.WHITE, null);
	//	mesh0.setupPosition(0, 0, 0, 0, (float)(Math.PI / 2), (float)(Math.PI / 2));
		mesh0.setupPosition(0, 0, 0, 0, 0, 0);
		
		meshes.add(mesh0);

		new SimpleEngine(width, height, title, fpsLock, fpsCap, showFps, fullscreen)
		{
			public void update()
			{
				if(key(KeyEvent.VK_ESCAPE))
					stop();
				
				float elapsedTime = getElapsedTime() / 1000000000f;
				
				// MISC ////////////////////////////////////////////////
				
				if(keyToggle(KeyEvent.VK_F1)) // switch debug view
					showDebug = !showDebug;
				
				if(keyToggle(KeyEvent.VK_F2)) // switch fps lock
					setFPSLock(!getFPSLock());				
				
				if(keyToggle(KeyEvent.VK_F3)) // switch fullscreen
				{
					setSize(width, height, !getFullscreen());
					mProj = MathUtils.projMat(90, getHeight() * 1f / getWidth(), 0.1f, 1000f);
					db = new DepthBuffer(getWidth() / pixelSize, getHeight() / pixelSize);
				}
				
				if(keyToggle(KeyEvent.VK_F4)) // set random color for mesh0
				{
					int red = (int)(Math.random() * 255);
					int green = (int)(Math.random() * 255);
					int blue = (int)(Math.random() * 255);
					
					mesh0.setColor(new Color(red, green, blue));
				}
				
				if(keyToggle(KeyEvent.VK_F5)) // set random light direction
					vLight = MathUtils.normVec(new Vect3D((float)(Math.random() * 2 - 1), (float)(Math.random() * 2 - 1), (float)(Math.random() * 2 - 1)));
				
				
				if(keyToggle(KeyEvent.VK_R)) // reset camera
					camera = defaultCamera.clone();
				
				////////////////////////////////////////////////////////
				
				// SHUTTLE /////////////////////////////////////////////
				
				float deltaPitchShuttle = 0f;
				float deltaYawShuttle = 0f;
				float deltaRollShuttle = 0f;
				
				if(key(KeyEvent.VK_NUMPAD8))
					deltaPitchShuttle = - elapsedTime;
				if(key(KeyEvent.VK_NUMPAD2))
					deltaPitchShuttle = elapsedTime;
				if(key(KeyEvent.VK_NUMPAD7))
					deltaYawShuttle = elapsedTime;
				if(key(KeyEvent.VK_NUMPAD9))
					deltaYawShuttle = - elapsedTime;
				if(key(KeyEvent.VK_NUMPAD4))
					deltaRollShuttle = - elapsedTime;
				if(key(KeyEvent.VK_NUMPAD6))
					deltaRollShuttle = elapsedTime;
				
				mesh0.rotate(deltaPitchShuttle, deltaYawShuttle, deltaRollShuttle);
				
				
				float deltaLeftShuttle = 0;
				float deltaUpShuttle = 0;
				float deltaForwardShuttle = 0;
				
				if(key(KeyEvent.VK_I))
					deltaForwardShuttle = movMult * elapsedTime;
				if(key(KeyEvent.VK_K))
					deltaForwardShuttle = - movMult * elapsedTime;
				if(key(KeyEvent.VK_J))
					deltaLeftShuttle = movMult * elapsedTime;
				if(key(KeyEvent.VK_L))
					deltaLeftShuttle = - movMult * elapsedTime;
				if(key(KeyEvent.VK_O))
					deltaUpShuttle = movMult * elapsedTime;
				if(key(KeyEvent.VK_COMMA))
					deltaUpShuttle = - movMult * elapsedTime;
				
				mesh0.move(deltaLeftShuttle, deltaUpShuttle, deltaForwardShuttle);
				
				////////////////////////////////////////////////////////
				
				// CAMERA /////////////////////////////////////////////			
				
				float deltaPitch = 0;
				float deltaYaw = 0;
				float deltaRoll = 0;
				
				if(key(KeyEvent.VK_UP))
					deltaPitch = - rotMult * elapsedTime;
				if(key(KeyEvent.VK_DOWN))
					deltaPitch = rotMult * elapsedTime;
				if(key(KeyEvent.VK_LEFT))
					deltaYaw = rotMult * elapsedTime;
				if(key(KeyEvent.VK_RIGHT))
					deltaYaw = - rotMult * elapsedTime;
				if(key(KeyEvent.VK_SHIFT))
					deltaRoll = - rotMult * elapsedTime;
				if(key(KeyEvent.VK_NUMPAD1))
					deltaRoll = rotMult * elapsedTime;
				
				camera.rotate(deltaPitch, deltaYaw, deltaRoll);
				
				
				float deltaLeft = 0;
				float deltaUp = 0;
				float deltaForward = 0;
				
				if(key(KeyEvent.VK_W))
					deltaForward = movMult * elapsedTime;
				if(key(KeyEvent.VK_S))
					deltaForward = - movMult * elapsedTime;
				if(key(KeyEvent.VK_A))
					deltaLeft = movMult * elapsedTime;
				if(key(KeyEvent.VK_D))
					deltaLeft = - movMult * elapsedTime;
				if(key(KeyEvent.VK_E))
					deltaUp = movMult * elapsedTime;
				if(key(KeyEvent.VK_X))
					deltaUp = - movMult * elapsedTime;
				
				camera.move(deltaLeft, deltaUp, deltaForward);
				
				////////////////////////////////////////////////////////
			}
			
			public void render() throws InterruptedException
			{
				Graphics g = getGraphics();
				
				////////////////////////////////////////////////////////
			
				db.reset();
				for(Mesh mesh : meshes)
					new RenderingPipeline(mesh, getWidth() / pixelSize, getHeight() / pixelSize, mProj, camera, vLight, db, showDebug).run();
				
				//////////////////////////////////////////////////////////////////////////
				
				Color bgC = bgc; 
				
				PostProcessingEffects.outerLines(db, Color.GREEN);
				//bgC = PostProcessingEffects.negative(db, bgc);
				//bgC = PostProcessingEffects.saturate(db, bgC, 0f);
				//bgC = PostProcessingEffects.grayScale(db, bgC);
				//bgC = PostProcessingEffects.fakeHDR(db, bgC);
								
				g.setColor(bgC);
				g.fillRect(0, 0, getWidth(), getHeight());
				db.draw(g, pixelSize);
				
				//////////////////////////////////////////////////////////////////////////
				
				if(showDebug)
				{
					g.setColor(Color.BLACK);
					g.fillRect(10, 10, 340, 170);
					
					String fps = "FPS  =  " + getFPS();
					
					String cameraX = "Camera X  =  " + camera.getX();
					String cameraY = "Camera Y  =  " + camera.getY();
					String cameraZ = "Camera Z  =  " + camera.getZ();
					String cameraPitch = "Camera Pitch = " + camera.getPitch();
					String cameraYaw = "Camera Yaw = " + camera.getYaw();
					String cameraRoll = "Camera Roll = " + camera.getRoll();
					
					g.setColor(Color.GREEN);
					g.setFont(new Font("Consolas", Font.PLAIN, 18));
					g.drawString(fps, 20, 30);
					g.drawString(cameraX, 20, 70);
					g.drawString(cameraY, 20, 90);
					g.drawString(cameraZ, 20, 110);
					g.drawString(cameraPitch, 20, 130);
					g.drawString(cameraYaw, 20, 150);
					g.drawString(cameraRoll, 20, 170);
				}
			}
		};
	}
}
