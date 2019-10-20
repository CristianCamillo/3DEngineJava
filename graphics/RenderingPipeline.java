package graphics;

import java.awt.Color;
import java.util.ArrayList;

public class RenderingPipeline //extends Thread
{
	private final Mesh mesh;
	private final int width;
	private final int height;
	private final float[][] mProj;
	private final Camera camera;
	private final Vect3D vLight;
	
	private final ArrayList<Triangle> visibleTris = new ArrayList<Triangle>();
	
	private final DepthBuffer db;
	
	private final boolean showWireframe;
	
	private final Vect3D vW00;
	private final Vect3D v0H0; 
	
	private final static Vect3D V_000 = new Vect3D(0f, 0f, 0f);
	private final static Vect3D V_010 = new Vect3D(0f, 1f, 0f);
	private final static Vect3D V_0m10 = new Vect3D(0f, -1f, 0f);
	private final static Vect3D V_100 = new Vect3D(1f, 0f, 0f);
	private final static Vect3D V_m100 = new Vect3D(-1f, 0f, 0f);
	private final static Vect3D V_001 = new Vect3D(0f, 0f, 1f);
	
	private final static Vect3D V_NEAR_PLANE = new Vect3D(0f, 0f, 0.1f); // was set to 0.001
	
	private final static Vect3D V_OFFSET_VIEW = new Vect3D(1f, 1f, 0f);
	
	public RenderingPipeline(Mesh mesh,
							 int width,
							 int height,
							 float[][] mProj,
							 Camera camera,
							 Vect3D vLight,
							 DepthBuffer db,
							 boolean showWireframe)
	{
		this.mesh = mesh;
		this.width = width;
		this.height = height;
		this.mProj = mProj;
		this.camera = camera;
		this.vLight = vLight;
		this.db = db;
		this.showWireframe = showWireframe;
		
		vW00 = new Vect3D(width - 1f, 0f, 0f);
		v0H0 = new Vect3D(0f, height - 1f, 0f);
	}
	
	public void run()
	{
		float[][] mView = camera.getMatView();	
		
		for(Triangle tri : mesh.tris)
		{
			Vect3D line1 = MathUtils.subVec(tri.p[1], tri.p[0]);
			Vect3D line2 = MathUtils.subVec(tri.p[2], tri.p[0]);
			Vect3D normal = MathUtils.normVec(MathUtils.crossProd(line1, line2));
			
			Vect3D vCameraRay = MathUtils.subVec(tri.p[0], camera.vPosition);
			
			if(MathUtils.dotProd(normal, vCameraRay) < 0f)
			{
				float dp = MathUtils.dotProd(vLight, normal);	
				float lum = 0.01f > dp ? 0.01f : dp;
				
				Vect3D[] p = new Vect3D[3];
				
				p[0] = MathUtils.mulVecMat(tri.p[0], mView);
				p[1] = MathUtils.mulVecMat(tri.p[1], mView);
				p[2] = MathUtils.mulVecMat(tri.p[2], mView);				
				
				Triangle triViewed = new Triangle(p, tri.t, tri.color, lum);				
				
				Triangle[] clipped = MathUtils.clipAgainstPlane(V_NEAR_PLANE, V_001, triViewed);
				int nClippedTris = clipped == null ? 0 : clipped.length;
				
				for(int n = 0; n < nClippedTris; n++)
				{
					p = new Vect3D[3];
					
					p[0] = MathUtils.mulVecMat(clipped[n].p[0], mProj);
					p[1] = MathUtils.mulVecMat(clipped[n].p[1], mProj);
					p[2] = MathUtils.mulVecMat(clipped[n].p[2], mProj);
					
					Triangle triProjected = new Triangle(p, clipped[n].t, clipped[n].color, clipped[n].lum);
					
				/*	triProjected.t[0] = MathUtils.divVec(triProjected.t[0], triProjected.p[0].w);
					triProjected.t[1] = MathUtils.divVec(triProjected.t[1], triProjected.p[1].w);
					triProjected.t[2] = MathUtils.divVec(triProjected.t[2], triProjected.p[2].w);
					
					triProjected.t[0].w = 1f / triProjected.p[0].w;
					triProjected.t[1].w = 1f / triProjected.p[1].w;
					triProjected.t[2].w = 1f / triProjected.p[2].w;*/
					
				/*	triProjected.t[0].u /= triProjected.p[0].w; //
					triProjected.t[1].u /= triProjected.p[1].w; //
					triProjected.t[2].u /= triProjected.p[2].w; //
					
					triProjected.t[0].v /= triProjected.p[0].w; //
					triProjected.t[1].v /= triProjected.p[1].w; //
					triProjected.t[2].v /= triProjected.p[2].w; //
					
					triProjected.t[0].w = 1f / triProjected.p[0].w; //
					triProjected.t[1].w = 1f / triProjected.p[1].w; //
					triProjected.t[2].w = 1f / triProjected.p[2].w; //
					*/
					triProjected.p[0] = MathUtils.divVec(triProjected.p[0], triProjected.p[0].w);
					triProjected.p[1] = MathUtils.divVec(triProjected.p[1], triProjected.p[1].w);
					triProjected.p[2] = MathUtils.divVec(triProjected.p[2], triProjected.p[2].w);
				
					triProjected.p[0].x *= -1f;
					triProjected.p[1].x *= -1f;
					triProjected.p[2].x *= -1f;
					triProjected.p[0].y *= -1f;
					triProjected.p[1].y *= -1f;
					triProjected.p[2].y *= -1f;
					
					triProjected.p[0] = MathUtils.addVec(triProjected.p[0], V_OFFSET_VIEW);
					triProjected.p[1] = MathUtils.addVec(triProjected.p[1], V_OFFSET_VIEW);
					triProjected.p[2] = MathUtils.addVec(triProjected.p[2], V_OFFSET_VIEW);
					triProjected.p[0].x *= 0.5f * width;
					triProjected.p[0].y *= 0.5f * height;
					triProjected.p[1].x *= 0.5f * width;
					triProjected.p[1].y *= 0.5f * height;
					triProjected.p[2].x *= 0.5f * width;
					triProjected.p[2].y *= 0.5f * height;
					
					visibleTris.add(triProjected);
					
					if(showWireframe)
						addWireframe(triProjected);
				}
			}
		}
		
		for(int i = 0; i < visibleTris.size(); i++)
		{
			ArrayList<Triangle> listTris = new ArrayList<Triangle>();
			
			listTris.add(visibleTris.get(i));
			int nNewTris = 1;
			
			for(int p = 0; p < 4; p++)
			{
				int nTrisToAdd = 0;
				while(nNewTris > 0)
				{
					Triangle test = listTris.get(0);
					Triangle[] clipped = null;
					listTris.remove(0);
					nNewTris--;
					
					switch(p)
					{
						case 0: clipped = MathUtils.clipAgainstPlane(V_000, V_010, test); break;
						case 1: clipped = MathUtils.clipAgainstPlane(v0H0,  V_0m10, test); break;
						case 2: clipped = MathUtils.clipAgainstPlane(V_000, V_100, test); break;
						case 3: clipped = MathUtils.clipAgainstPlane(vW00,  V_m100, test);
					}
					
					nTrisToAdd = clipped == null ? 0 : clipped.length;
					
					for(int w = 0; w < nTrisToAdd; w++)
						listTris.add(clipped[w]);
				}
				
				nNewTris = listTris.size();
			}
			
			for(Triangle t : listTris)
				Texturizer.texturedTriangle(Math.round(t.p[0].x), Math.round(t.p[0].y), t.t[0].u, t.t[0].v, t.t[0].w,
											Math.round(t.p[1].x), Math.round(t.p[1].y), t.t[1].u, t.t[1].v, t.t[1].w,
											Math.round(t.p[2].x), Math.round(t.p[2].y), t.t[2].u, t.t[2].v, t.t[2].w,
											mesh.tex, db, t.lum, false);
		}
	}
	
	private void addWireframe(Triangle t)
	{
		int[] xs = new int[]{Math.round(t.p[0].x), Math.round(t.p[1].x), Math.round(t.p[2].x)};
		int[] ys = new int[]{Math.round(t.p[0].y), Math.round(t.p[1].y), Math.round(t.p[2].y)};
		
		if(xs[0] != xs[1])				
			for(int x = Math.min(xs[0], xs[1]); x <= Math.max(xs[0], xs[1]); x++)
			{					
				int y = Math.round(ys[0] + ((ys[1] - ys[0]) * 1f / (xs[1] - xs[0])) * (x - xs[0]));
					
				db.setValue(x, y, Float.POSITIVE_INFINITY);
				db.setColor(x, y, Color.RED);
			}
		else
			for(int y = Math.min(ys[0], ys[1]); y <= Math.max(ys[0], ys[1]); y++)
			{					
				int x = Math.round(xs[0] + ((xs[1] - xs[0]) * 1f / (ys[1] - ys[0])) * (y - ys[0]));
											
				db.setValue(x, y, Float.POSITIVE_INFINITY);
				db.setColor(x, y, Color.RED);
			}
		
		if(xs[1] != xs[2])				
			for(int x = Math.min(xs[1], xs[2]); x <= Math.max(xs[1], xs[2]); x++)
			{					
				int y = Math.round(ys[1] + ((ys[2] - ys[1]) * 1f / (xs[2] - xs[1])) * (x - xs[1]));
										
				db.setValue(x, y, Float.POSITIVE_INFINITY);
				db.setColor(x, y, Color.RED);
			}
		else
			for(int y = Math.min(ys[1], ys[2]); y <= Math.max(ys[1], ys[2]); y++)
			{					
				int x = Math.round(xs[1] + ((xs[2] - xs[1]) * 1f / (ys[2] - ys[1])) * (y - ys[1]));
											
				db.setValue(x, y, Float.POSITIVE_INFINITY);
				db.setColor(x, y, Color.RED);
			}
		
		if(xs[0] != xs[2])				
			for(int x = Math.min(xs[0], xs[2]); x <= Math.max(xs[0], xs[2]); x++)
			{					
				int y = Math.round(ys[0] + ((ys[2] - ys[0]) * 1f / (xs[2] - xs[0])) * (x - xs[0]));
															
				db.setValue(x, y, Float.POSITIVE_INFINITY);
				db.setColor(x, y, Color.RED);
			}
		else
			for(int y = Math.min(ys[0], ys[2]); y <= Math.max(ys[0], ys[2]); y++)
			{					
				int x = Math.round(xs[0] + ((xs[2] - xs[0]) * 1f / (ys[2] - ys[0])) * (y - ys[0]));
											
				db.setValue(x, y, Float.POSITIVE_INFINITY);
				db.setColor(x, y, Color.RED);
			}
	}
}