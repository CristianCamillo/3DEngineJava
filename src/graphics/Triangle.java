package graphics;

import java.awt.Color;

public class Triangle implements Cloneable
{
	public Vect3D[] p;
	public Vect2D[] t;
	public Color color;
	public float lum = 1f;
	
	public Triangle()
	{
		p = new Vect3D[3];
		p[0] = new Vect3D();
		p[1] = new Vect3D();
		p[2] = new Vect3D();
		
		t = new Vect2D[3];
		t[0] = new Vect2D();
		t[1] = new Vect2D();
		t[2] = new Vect2D();
		
		color = Color.GRAY;
	}
		
	public Triangle(Vect3D[] p, Vect2D[] t, Color color, float lum)
	{
		this.p = p;
		this.t = t;
		this.color = color;
		this.lum = lum;
	}
	
	void applyMat(float[][] m)
	{
		for(byte i = 0; i < 3; i++)
			p[i] = p[i].applyMat(m);
	}
	
	public String toString()
	{
		String s = "";
		for(byte i = 0; i < 3; i++)
			s += p[i] + "\n";
		s += "Color = " + color + "\nLum = " + lum + "\n";
		
		return s;
	}
	
	public Triangle clone()
	{
		try
		{
			return (Triangle) super.clone();
		}
		catch(Exception e){return null;}
	}
}
