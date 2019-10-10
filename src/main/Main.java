package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;

import graphics.MatUtils;
import graphics.Mesh;
import graphics.SimpleGameEngine;
import graphics.TriToRasterThread;
import graphics.Triangle;
import graphics.Vect3D;


public class Main
{
	/* Final(ish) */
	
	private short WIDTH = 1280;
	private short HEIGHT = 720;
	private String TITLE = "3D Engine";
	private short FPSCAP = 60;
	private final byte N_THREAD = 4;
	
	float[][] mProj = MatUtils.projMat(90, HEIGHT * 1f / WIDTH, 0.1f, 1000f);
	Vect3D vUp = new Vect3D(0f, 1f, 0f);
	Vect3D vPlaneP = new Vect3D(0f, 0f, 0.001f);
	Vect3D vPlaneN = new Vect3D(0f, 0f, 1f);
	Vect3D vOffsetView = new Vect3D(1f, 1f, 0f);
	
	TriToRasterThread[] ttrt = new TriToRasterThread[N_THREAD];
	
	/**************/
	
	private Mesh[] mesh = new Mesh[100];
	
	Vect3D vCamera = new Vect3D(0f, 2f, -5f);
	Vect3D vLookDir = new Vect3D(0f, 0f, 1f);
	Vect3D vLight = MatUtils.normVec(new Vect3D(0f, 1f, -1f));
	
	float xaw = 0f;
	float yaw = 0f;
	
	float speedMult = 5f;
	
	Color bgrColor = Color.BLACK;
	boolean showDebug = false;
	
	public Main() throws Exception
	{
		mesh[0] = new Mesh("meshes//teapot.obj", Color.WHITE);
		
		//mesh[0].translate(0, 4, 0);
	//	mesh[1] = new Mesh("meshes//pumpkin.obj", new Color(255, 140, 0));
		
		//mesh[1].rotate(- MatUtils.PI / 2, 0, 0);
		//mesh[1].translate(0, 100, 100);
		
		new SimpleGameEngine(WIDTH, HEIGHT, TITLE, FPSCAP)
		{
			public void update()
			{
				if(!escapeKey())
				{
					if(toggle(f1Key()))
						showDebug = !showDebug;
					
					if(toggle(f3Key()))
						setFPSLock(!getFPSLock());
					
					float elapsedTime = getElapsedTime() / 1000000000f;

					if(np4Key() && !np6Key())
						mesh[0].rotate(0f, - MatUtils.PI  * elapsedTime, 0f);
					if(np6Key() && !np4Key())
						mesh[0].rotate(0f, MatUtils.PI * elapsedTime, 0f);
					if(np2Key() && !np8Key())
						mesh[0].rotate(- MatUtils.PI * elapsedTime, 0f, 0f);
					if(np8Key() && !np2Key())
						mesh[0].rotate(MatUtils.PI * elapsedTime, 0f, 0f);
					if(np7Key() && !np9Key())
						mesh[0].rotate(0f, 0f, - MatUtils.PI * elapsedTime);
					if(np9Key() && !np7Key())
						mesh[0].rotate(0f, 0f, MatUtils.PI * elapsedTime);					
					
					float[][] mLightRot = MatUtils.yRotMat(1f * elapsedTime);
					vLight = MatUtils.normVec(MatUtils.mulVecMat(vLight, mLightRot));
					
					Vect3D vForward = MatUtils.mulVec(vLookDir, speedMult * elapsedTime);
					Vect3D vRight   = MatUtils.mulVec(MatUtils.normVec(MatUtils.crossProd(vLookDir, vUp)), speedMult * elapsedTime);
					Vect3D vUp1     = MatUtils.mulVec(vUp, speedMult * elapsedTime);
					
					if(wKey() && !sKey())
						vCamera = MatUtils.addVec(vCamera, vForward);
					if(sKey() && !wKey())
						vCamera = MatUtils.subVec(vCamera, vForward);
					if(aKey() && !dKey())
						vCamera = MatUtils.subVec(vCamera, vRight);
					if(dKey() && !aKey())
						vCamera = MatUtils.addVec(vCamera, vRight);
					if(eKey() && !xKey())
						vCamera = MatUtils.addVec(vCamera, vUp1);
					if(xKey() && !eKey())
						vCamera = MatUtils.subVec(vCamera, vUp1);
					
					if(upKey())
					{
						xaw -= 1f * elapsedTime;
						if(xaw < - MatUtils.PI / 2)
							xaw = - MatUtils.PI / 2 + 0.001f;
					}
					if(downKey())
					{
						xaw += 1f * elapsedTime;
						if(xaw > MatUtils.PI / 2)
							xaw = MatUtils.PI / 2 - 0.001f;
					}
					if(leftKey())
						yaw += 1f * elapsedTime;
					if(rightKey())
						yaw -= 1f * elapsedTime;
					yaw = yaw > MatUtils.PI * 2 ? yaw - MatUtils.PI * 2 : yaw;
					yaw = yaw < - MatUtils.PI * 2 ? yaw + MatUtils.PI * 2 : yaw;
				}
				else
					stop();
			}
			
			public void render() throws InterruptedException
			{
				Graphics g = getGraphics();
				
				g.setColor(bgrColor);
				g.fillRect(0, 0, WIDTH, HEIGHT);
				
				Vect3D vTarget = new Vect3D(0f, 0f, 1f);
				float[][] mCameraRot = MatUtils.mulMat(MatUtils.xRotMat(xaw), MatUtils.yRotMat(yaw));
				vLookDir = MatUtils.mulVecMat(vTarget, mCameraRot);
				vTarget = MatUtils.addVec(vCamera, vLookDir);
				float[][] mCamera = MatUtils.pointAtMat(vCamera, vTarget, vUp);
				float[][] mView = MatUtils.lookAtMat(mCamera);
				
				ArrayList<Triangle> vecTriToRaster = new ArrayList<Triangle>();				
				
				short k = -1;
				while(mesh[++k] != null)
				{
					for(byte i = 0; i < N_THREAD; i++)
						ttrt[i] = new TriToRasterThread(vPlaneP, vPlaneN, mProj, vOffsetView, WIDTH, HEIGHT);
					
					for(byte i = 0; i < N_THREAD; i++)
						ttrt[i].setup(mesh[k].tris, i * mesh[k].tris.length / N_THREAD, (i + 1) * mesh[k].tris.length / N_THREAD, vCamera, vLight, mView);
					
					for(TriToRasterThread thread : ttrt)
						thread.start();
					
					for(TriToRasterThread thread : ttrt)
						thread.join();
					
					for(byte i = 0; i < N_THREAD; i++)
						for(int j = 0; j < ttrt[i].getTrisCounter(); j++)
							vecTriToRaster.add(ttrt[i].retTris[j]);
				}
				
				vecTriToRaster.sort((o1, o2) ->
				{
					Triangle t1 = (Triangle) o1;
					Triangle t2 = (Triangle) o2;
					
					float z1 = (t1.p[0].z + t1.p[1].z + t1.p[2].z) / 3f;
					float z2 = (t2.p[0].z + t2.p[1].z + t2.p[2].z) / 3f;
					
					return z1 < z2 ? 1 : (z1 > z2 ? -1 : 0);
				});				
				
				for(Triangle triToRaster : vecTriToRaster)
				{
					Triangle[] clipped = new Triangle[2];
					ArrayList<Triangle> listTris = new ArrayList<Triangle>();
					
					listTris.add(triToRaster);
					int nNewTris = 1;
					
					for(byte p = 0; p < 4; p++)
					{
						byte nTrisToAdd = 0;
						while(nNewTris > 0)
						{
							Triangle test = listTris.get(0);
							listTris.remove(0);
							nNewTris--;
							
							switch(p)
							{
								case 0: clipped = MatUtils.clipAgainstPlane(new Vect3D(0f, 0f, 0f), new Vect3D(0f, 1f, 0f), test); break;
								case 1: clipped = MatUtils.clipAgainstPlane(new Vect3D(0f, HEIGHT - 1f, 0f), new Vect3D(0f, -1f, 0f), test); break;
								case 2: clipped = MatUtils.clipAgainstPlane(new Vect3D(0f, 0f, 0f), new Vect3D(1f, 0f, 0f), test); break;
								case 3: clipped = MatUtils.clipAgainstPlane(new Vect3D(WIDTH - 1f, 0f, 0f), new Vect3D(-1f, 0f, 0f), test);
							}
							
							nTrisToAdd = (byte) (clipped == null ? 0 : clipped.length);
							
							for(byte w = 0; w < nTrisToAdd; w++)
								listTris.add(clipped[w]);
						}
						
						nNewTris = listTris.size();
					}
					
					for(Triangle t : listTris)
					{
						g.setColor(new Color((int)(t.color.getRed() * t.lum), (int)(t.color.getGreen() * t.lum), (int)(t.color.getBlue() * t.lum)));
						
						int[] xs = new int[]{(int)t.p[0].x, (int)t.p[1].x, (int)t.p[2].x};
						int[] ys = new int[]{(int)t.p[0].y, (int)t.p[1].y, (int)t.p[2].y};
					
						g.fillPolygon(xs, ys, 3);
						
						if(showDebug)
						{
							g.setColor(Color.WHITE);
							g.drawPolygon(xs, ys, 3);
						}
					}
				}
				
				if(showDebug)
				{
					g.setColor(Color.BLACK);
					g.fillRect(10, 10, 300, 110);
					
					String fps = "FPS  =  " + getFPS();
					
					String cameraX = "Camera.X  =  " + vCamera.x;
					String cameraY = "Camera.Y  =  " + vCamera.y;
					String cameraZ = "Camera.Z  =  " + vCamera.z;
					
					g.setColor(Color.GREEN);
					g.setFont(new Font("Consolas", Font.PLAIN, 18));
					g.drawString(fps, 20, 30);
					g.drawString(cameraX, 20, 70);
					g.drawString(cameraY, 20, 90);
					g.drawString(cameraZ, 20, 110);
				}
			}
		};
	}
}
