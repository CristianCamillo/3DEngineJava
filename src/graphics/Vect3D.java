package graphics;

//updated 16/10/2019

public class Vect3D implements Cloneable
{
	public float x;
	public float y;
	public float z;
	public float w = 1f;
	
	public Vect3D()
	{
		x = 0;
		y = 0;
		z = 0;
	}
	
	public Vect3D(float x, float y, float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vect3D(float x, float y, float z, float w)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}
	
	Vect3D applyMat(float[][] m)
	{
		Vect3D v = MathUtils.mulVecMat(this, m);
		v = MathUtils.divVec(v, v.w);
		
		return v;
	}
	
	public String toString()
	{
		return "x = " + x + ", y = " + y + ", z = " + z + ", w = " + w;
	}
	
	public Vect3D clone()
	{
		try
		{
			return (Vect3D) super.clone();
		}
		catch(Exception e){return null;}
	}
}
