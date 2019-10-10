package graphics;

import exceptions.TriToRasterThreadNotSetupException;

public class TriToRasterThread extends Thread
{
	private Triangle[] tris;
	private int start;
	private int end;
	
	private boolean setup = false;
	
	private Vect3D vCamera;
	private Vect3D vLight;
	private float[][] mView;
	
	private Vect3D vPlaneP;
	private Vect3D vPlaneN;
	private float[][] mProj;
	private Vect3D vOffsetView;
	private short width;
	private short height;
	
	public Triangle[] retTris;
	private int trisCounter = 0;
	
	public TriToRasterThread(Vect3D vPlaneP, Vect3D vPlaneN, float[][] mProj, Vect3D vOffsetView, short width, short height)
	{
		this.vPlaneP = vPlaneP;
		this.vPlaneN = vPlaneN;
		this.mProj = mProj;
		this.vOffsetView = vOffsetView;
		this.width = width;
		this.height = height;
	}
	
	public void setup(Triangle[] tris, int start, int end, Vect3D vCamera, Vect3D vLight, float[][] mView)
	{
		this.tris = tris;
		this.start = start;
		this.end = end;
		
		this.vCamera = vCamera;
		this.vLight = vLight;
		this.mView = mView;
		
		retTris = new Triangle[tris.length];
		
		setup = true;
	}
	
	public void run()
	{
		if(!setup)
			throw new TriToRasterThreadNotSetupException();
			
		for(int i = start; i < end; i++)
		{
			Vect3D line1 = MatUtils.subVec(tris[i].p[1], tris[i].p[0]);
			Vect3D line2 = MatUtils.subVec(tris[i].p[2], tris[i].p[0]);
			Vect3D normal = MatUtils.normVec(MatUtils.crossProd(line1, line2));
			
			Vect3D vCameraRay = MatUtils.subVec(tris[i].p[0], vCamera);
			
			if(MatUtils.dotProd(normal, vCameraRay) < 0f)
			{
				float dp = MatUtils.dotProd(vLight, normal);
				
				Triangle triViewed = new Triangle();						
				triViewed.p[0] = MatUtils.mulVecMat(tris[i].p[0], mView);
				triViewed.p[1] = MatUtils.mulVecMat(tris[i].p[1], mView);
				triViewed.p[2] = MatUtils.mulVecMat(tris[i].p[2], mView);
				triViewed.color = tris[i].color;
				triViewed.lum = 0.01f > dp ? 0.01f : dp;
				
				Triangle[] clipped = MatUtils.clipAgainstPlane(vPlaneP, vPlaneN, triViewed);
				byte nClippedTris = (byte) (clipped == null ? 0 : clipped.length);
				
				for(byte n = 0; n < nClippedTris; n++)
				{
					Triangle triProjected = new Triangle();							
					triProjected.p[0] = MatUtils.mulVecMat(clipped[n].p[0], mProj);
					triProjected.p[1] = MatUtils.mulVecMat(clipped[n].p[1], mProj);
					triProjected.p[2] = MatUtils.mulVecMat(clipped[n].p[2], mProj);
					triProjected.color = clipped[n].color;
					triProjected.lum = clipped[n].lum;

					triProjected.p[0] = MatUtils.divVec(triProjected.p[0], triProjected.p[0].w);
					triProjected.p[1] = MatUtils.divVec(triProjected.p[1], triProjected.p[1].w);
					triProjected.p[2] = MatUtils.divVec(triProjected.p[2], triProjected.p[2].w);
				
					triProjected.p[0].x *= -1f;
					triProjected.p[1].x *= -1f;
					triProjected.p[2].x *= -1f;
					triProjected.p[0].y *= -1f;
					triProjected.p[1].y *= -1f;
					triProjected.p[2].y *= -1f;
					
					triProjected.p[0] = MatUtils.addVec(triProjected.p[0], vOffsetView);
					triProjected.p[1] = MatUtils.addVec(triProjected.p[1], vOffsetView);
					triProjected.p[2] = MatUtils.addVec(triProjected.p[2], vOffsetView);
					triProjected.p[0].x *= 0.5f * width;
					triProjected.p[0].y *= 0.5f * height;
					triProjected.p[1].x *= 0.5f * width;
					triProjected.p[1].y *= 0.5f * height;
					triProjected.p[2].x *= 0.5f * width;
					triProjected.p[2].y *= 0.5f * height;
					
					retTris[trisCounter++] = triProjected;
				}
			}
		}
	}
	
	public int getTrisCounter()
	{
		return trisCounter;
	}
}
