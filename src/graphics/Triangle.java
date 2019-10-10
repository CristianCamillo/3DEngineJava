package graphics;

import java.awt.Color;

public class Triangle implements Cloneable
{
	public Vect3D[] p;
	public Color color;
	public float lum = 1f;
	
	public Triangle()
	{
		p = new Vect3D[3];
		p[0] = new Vect3D();
		p[1] = new Vect3D();
		p[2] = new Vect3D();
		color = Color.GRAY;
	}
	
	public Triangle(Vect3D[] p)
	{
		this.p = p;
	}
	
	public Triangle(Vect3D[] p, Color color)
	{
		this.p = p;
		this.color = color;
	}
	
	public Triangle(Vect3D[] p, Color color, float lum)
	{
		this.p = p;
		this.color = color;
		this.lum = lum;
	}
	
	void applyMat(float[][] m)
	{
		for(byte i = 0; i < 3; i++)
			p[i] = p[i].applyMat(m);
	}
	
	
	void rotate(float alpha, float beta, float gamma)
	{
		rotateAroundAxis('x', alpha);
		rotateAroundAxis('y', beta);
		rotateAroundAxis('z', gamma);
	}
	
	private void rotateAroundAxis(char axis, float theta)
	{
		if(theta != 0)
		{
			float[][] rotMat;
			
			switch(axis)
			{
				case 'x':	rotMat = MatUtils.xRotMat(theta);
							break;
				case 'y':	rotMat = MatUtils.yRotMat(theta);
							break;
				case 'z':	rotMat = MatUtils.zRotMat(theta);
				
				default: return;
			}
			
			applyMat(rotMat);
		}
	}
	
	public String toString()
	{
		String s = "";
		for(byte i = 0; i < 3; i++)
			s += p[i] + "\n";
		s += color + "\n" + lum + "\n";
		
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
