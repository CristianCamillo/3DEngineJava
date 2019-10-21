package graphics;

public class Vect2D implements Cloneable
{
	public float u;
	public float v;
	public float w = 1f;
	
	public Vect2D()
	{
		u = 0;
		v = 0;
	}
	
	public Vect2D(float u, float v)
	{
		this.u = u;
		this.v = v;
	}
	
	public Vect2D(float u, float v, float w)
	{
		this.u = u;
		this.v = v;
		this.w = w;
	}
	
	public String toString()
	{
		return "u = " + u + ", v = " + v + ", w = " + w;
	}
	
	public Vect2D clone()
	{
		return new Vect2D(u, v, w);
	}
}

