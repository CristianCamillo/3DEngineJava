package graphics;

public class MathUtils
{	
	/*********************************************************************/
	/* Vector functions                                                  */
	/*********************************************************************/
	
	public static Vect2D addVec(Vect2D v0, Vect2D v1)
	{
		return new Vect2D(v0.u + v1.u, v0.v + v1.v);
	}
	
	public static Vect3D addVec(Vect3D v0, Vect3D v1)
	{
		return new Vect3D(v0.x + v1.x, v0.y + v1.y, v0.z + v1.z);
	}
	
	public static Vect3D subVec(Vect3D v0, Vect3D v1)
	{
		return new Vect3D(v0.x - v1.x, v0.y - v1.y, v0.z - v1.z);
	}
	
	public static Vect2D mulVec(Vect2D v, float m)
	{
		return new Vect2D(v.u * m, v.v * m);
	}
	
	public static Vect3D mulVec(Vect3D v, float m)
	{
		return new Vect3D(v.x * m, v.y * m, v.z * m);
	}
	
	public static Vect2D divVec(Vect2D v, float d)
	{
		if(d == 0)
			throw new ArithmeticException();
		return new Vect2D(v.u / d, v.v / d);
	}
	
	public static Vect3D divVec(Vect3D v, float d)
	{
		if(d == 0)
			throw new ArithmeticException();
		return new Vect3D(v.x / d, v.y / d, v.z / d);
	}
	
	public static float dotProd(Vect3D v0, Vect3D v1)
	{
		return v0.x * v1.x + v0.y * v1.y + v0.z * v1.z;
	}
	
	public static float vecLen(Vect3D v)
	{
		return (float) Math.sqrt(dotProd(v, v));
	}

	public static Vect3D normVec(Vect3D v)
	{
		float l = vecLen(v);
		return divVec(v, l);
	}
	
	public static Vect3D crossProd(Vect3D v0, Vect3D v1)
	{
		return new Vect3D
		(
			v0.y * v1.z - v0.z * v1.y,
			v0.z * v1.x - v0.x * v1.z,
			v0.x * v1.y - v0.y * v1.x
		);
	}
	
	public static Vect3D mulVecMat(Vect3D v, float[][] m)
	{
		return new Vect3D
		(
			v.x * m[0][0] + v.y * m[1][0] + v.z * m[2][0] + v.w * m[3][0],
			v.x * m[0][1] + v.y * m[1][1] + v.z * m[2][1] + v.w * m[3][1],
			v.x * m[0][2] + v.y * m[1][2] + v.z * m[2][2] + v.w * m[3][2],
			v.x * m[0][3] + v.y * m[1][3] + v.z * m[2][3] + v.w * m[3][3]
		);
	}
	
	public static Vect3D interPlane(Vect3D planeP, Vect3D planeN, Vect3D lineStart, Vect3D lineEnd, float[] t)
	{
		float planeD = - dotProd(planeN, planeP);
		float ad = dotProd(lineStart, planeN);
		float bd = dotProd(lineEnd, planeN);
		t[0] = (- planeD - ad) / (bd - ad);
		Vect3D lineStartToEnd = subVec(lineEnd, lineStart);
		Vect3D lineToIntersect = mulVec(lineStartToEnd, t[0]);
		
		return addVec(lineStart, lineToIntersect);
	}
	
	/*********************************************************************/
	/* Matrix functions                                                  */
	/*********************************************************************/
	
	public static float[][] identityMat()
	{
		float[][] m = new float[4][4];
		
		m[0][0] = 1.0f;
		m[1][1] = 1.0f;
		m[2][2] = 1.0f;
		m[3][3] = 1.0f;
		
		return m;
	}
	
	public static float[][] xRotMat(float a)
	{
		float[][] m = new float[4][4];
		
		m[0][0] = 1.0f;
		m[1][1] = (float) Math.cos(a);
		m[1][2] = (float) Math.sin(a);
		m[2][1] = (float) - Math.sin(a);
		m[2][2] = (float) Math.cos(a);
		m[3][3] = 1.0f;
		
		return m;
	}
	
	public static float[][] yRotMat(float a)
	{
		float[][] m = new float[4][4];
		
		m[0][0] = (float) Math.cos(a);
		m[1][1] = 1.0f;
		m[0][2] = (float) - Math.sin(a);
		m[2][0] = (float) Math.sin(a);
		m[2][2] = (float) Math.cos(a);
		m[3][3] = 1.0f;
		
		return m;
	}
	
	public static float[][] zRotMat(float a)
	{
		float[][] m = new float[4][4];
		
		m[0][0] = (float) Math.cos(a);
		m[0][1] = (float) Math.sin(a);
		m[1][0] = (float) - Math.sin(a);
		m[1][1] = (float) Math.cos(a);
		m[2][2] = 1.0f;
		m[3][3] = 1.0f;
		
		return m;
	}
	
	public static float[][] transMat(float x, float y, float z)
	{
		float[][] m = new float[4][4];
		
		m[0][0] = 1.0f;
		m[1][1] = 1.0f;
		m[2][2] = 1.0f;
		m[3][3] = 1.0f;
		m[3][0] = x;
		m[3][1] = y;
		m[3][2] = z;
		
		return m;
	}
	
	public static float[][] projMat(float fovDegrees, float aspectRatio, float near, float far)
	{
		float fovRad = (float) (1.0f / Math.tan(fovDegrees * 0.5f / 180.0f * Math.PI));
		float[][] m = new float[4][4];
		
		m[0][0] = aspectRatio * fovRad;
		m[1][1] = fovRad;
		m[2][2] = far / (far - near);
		m[3][2] = (- far * near) / (far - near);
		m[2][3] = 1.0f;
		m[3][3] = 0.0f;
		
		return m;
	}
	
	public static float[][] mulMat(float[][] m0, float[][] m1)
	{
		float[][] m = new float[4][4];
		
		for(byte c = 0; c < 4; c++)
			for(byte r = 0; r < 4; r++)
				m[r][c] = m0[r][0] * m1[0][c] + m0[r][1] * m1[1][c] + m0[r][2] * m1[2][c] + m0[r][3] * m1[3][c];
		
		return m;
	}
	
	public static float[][] pointAtMat(Vect3D pos, Vect3D target, Vect3D up)
	{
		Vect3D newForward = normVec(subVec(target, pos));

		Vect3D a = mulVec(newForward, dotProd(up, newForward));
		Vect3D newUp = normVec(subVec(up, a));

		Vect3D newRight = crossProd(newUp, newForward);

		float[][] m = new float[4][4];
		
		m[0][0] = newRight.x;	m[0][1] = newRight.y;	m[0][2] = newRight.z;	m[0][3] = 0f;
		m[1][0] = newUp.x;		m[1][1] = newUp.y;		m[1][2] = newUp.z;		m[1][3] = 0f;
		m[2][0] = newForward.x;	m[2][1] = newForward.y;	m[2][2] = newForward.z;	m[2][3] = 0f;
		m[3][0] = pos.x;		m[3][1] = pos.y;		m[3][2] = pos.z;		m[3][3] = 1f;
		
		return m;
	}
	
	public static float[][] lookAtMat(float[][] m0) // Only for Rotation/Translation Matrices (can only use PointAt matrix)
	{
		float[][] m = new float[4][4];
		
		m[0][0] = m0[0][0]; m[0][1] = m0[1][0]; m[0][2] = m0[2][0]; m[0][3] = 0.0f;
		m[1][0] = m0[0][1]; m[1][1] = m0[1][1]; m[1][2] = m0[2][1]; m[1][3] = 0.0f;
		m[2][0] = m0[0][2]; m[2][1] = m0[1][2]; m[2][2] = m0[2][2]; m[2][3] = 0.0f;
		m[3][0] = -(m0[3][0] * m[0][0] + m0[3][1] * m[1][0] + m0[3][2] * m[2][0]);
		m[3][1] = -(m0[3][0] * m[0][1] + m0[3][1] * m[1][1] + m0[3][2] * m[2][1]);
		m[3][2] = -(m0[3][0] * m[0][2] + m0[3][1] * m[1][2] + m0[3][2] * m[2][2]);
		m[3][3] = 1.0f;
		
		return m;
	}
	
	
	/*********************************************************************/
	/* Other functions                                                   */
	/*********************************************************************/
	
	public static Triangle[] clipAgainstPlane(Vect3D planeP, Vect3D normal, Triangle in)
	{		
		Vect3D[] insidePoints = new Vect3D[3];
		Vect3D[] outsidePoints = new Vect3D[3];
		int nInsidePointCount  = 0;
		int nOutsidePointCount = 0;
		
		Vect2D[] insideTex = new Vect2D[3];
		Vect2D[] outsideTex = new Vect2D[3];
		int nInsideTexCount  = 0;
		int nOutsideTexCount = 0;
		
		Triangle inTri = in.clone();
		
		float[] d = new float[3];
		d[0] = distance(inTri.p[0], normal, planeP);
		d[1] = distance(inTri.p[1], normal, planeP);
		d[2] = distance(inTri.p[2], normal, planeP);	
		
		for(int i = 0; i < 3; i++)
			if(d[i] >= 0)
			{
				insidePoints[nInsidePointCount++] = inTri.p[i];
				insideTex[nInsideTexCount++] = inTri.t[i];
			}
			else
			{
				outsidePoints[nOutsidePointCount++] = inTri.p[i];
				outsideTex[nOutsideTexCount++] = inTri.t[i];
			}
		
		if(nInsidePointCount == 0)
			return null;
		else if(nInsidePointCount == 3)
			return new Triangle[]{inTri};
		else if(nInsidePointCount == 1 && nOutsidePointCount == 2)
		{
			float[] tt = new float[1];
			
			
			Vect3D[] p = new Vect3D[3];
			Vect2D[] t = new Vect2D[3];
			
			p[0] = insidePoints[0];
			t[0] = insideTex[0];
			
			p[1] = interPlane(planeP, normal, insidePoints[0], outsidePoints[0], tt);
			t[1] = new Vect2D(tt[0] * (outsideTex[0].u - insideTex[0].u) + insideTex[0].u,
							  tt[0] * (outsideTex[0].v - insideTex[0].v) + insideTex[0].v,
							  tt[0] * (outsideTex[0].w - insideTex[0].w) + insideTex[0].w);	
			
			p[2] = interPlane(planeP, normal, insidePoints[0], outsidePoints[1], tt);
			t[2] = new Vect2D(tt[0] * (outsideTex[1].u - insideTex[0].u) + insideTex[0].u,
					  		  tt[0] * (outsideTex[1].v - insideTex[0].v) + insideTex[0].v,
					  		  tt[0] * (outsideTex[1].w - insideTex[0].w) + insideTex[0].w);		
		
			
			return new Triangle[] {new Triangle(p, t, inTri.color, inTri.lum)};
		}
		else
		{
			float[] tt = new float[1];
			
			
			Vect3D[] p0 = new Vect3D[3];
			Vect2D[] t0 = new Vect2D[3];
			
			p0[0] = insidePoints[0];
			t0[0] = insideTex[0];
			
			p0[1] = insidePoints[1];
			t0[1] = insideTex[1];
			
			p0[2] = interPlane(planeP, normal, insidePoints[0], outsidePoints[0], tt);
			t0[2] = new Vect2D(tt[0] * (outsideTex[0].u - insideTex[0].u) + insideTex[0].u,
							   tt[0] * (outsideTex[0].v - insideTex[0].v) + insideTex[0].v,
							   tt[0] * (outsideTex[0].w - insideTex[0].w) + insideTex[0].w);
	
			
			Vect3D[] p1 = new Vect3D[3];
			Vect2D[] t1 = new Vect2D[3];
			
			p1[0] = insidePoints[1];
			t1[0] = insideTex[1];
			
			p1[1] = p0[2];
			t1[1] = t0[2];
			
			p1[2] = interPlane(planeP, normal, insidePoints[1], outsidePoints[0], tt);
			t1[2] = new Vect2D(tt[0] * (outsideTex[0].u - insideTex[1].u) + insideTex[1].u,
							   tt[0] * (outsideTex[0].v - insideTex[1].v) + insideTex[1].v,
							   tt[0] * (outsideTex[0].w - insideTex[1].w) + insideTex[1].w);


			return new Triangle[] {new Triangle(p0, t0, inTri.color, inTri.lum), new Triangle(p1, t1, inTri.color, inTri.lum)};
		}
	}
	
	public static float distance(Vect3D p, Vect3D normal, Vect3D planeP)
	{
		return dotProd(normal, p) - dotProd(normal, planeP);
	}
}
