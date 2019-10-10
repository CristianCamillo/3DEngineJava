package graphics;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

public class Mesh
{
	public Triangle[] tris;	
	public float x = 0f;
	public float y = 0f;
	public float z = 0f;
	public float alpha = 0f;
	public float beta = 0f;
	public float gamma = 0f;
	private Color color;
	
	private Triangle[] backupTris;
	
	public Mesh() {}
	
	public Mesh(String filePath, Color color) throws FileNotFoundException
	{		
		this.color = color;
		
		FileReader fr = new FileReader(filePath);
		Scanner sca = new Scanner(fr);
		
		ArrayList<Vect3D> vectors = new ArrayList<Vect3D>();
		ArrayList<Triangle> tris = new ArrayList<Triangle>();

		while(sca.hasNextLine())
		{	
			String line = sca.nextLine();
			
			if(!line.startsWith("#"))
			{		
				String[] s = line.split(" ");

				switch(s[0])
				{
					case "v": vectors.add(new Vect3D(Float.parseFloat(s[1]), Float.parseFloat(s[2]), Float.parseFloat(s[3]))); break;
					case "f":
						Vect3D[] p = new Vect3D[3];
						p[0] = vectors.get(Integer.parseInt(s[1]) - 1);
						p[1] = vectors.get(Integer.parseInt(s[2]) - 1);
						p[2] = vectors.get(Integer.parseInt(s[3]) - 1);
						tris.add(new Triangle(p, color));
				}
			}
		}
		
		sca.close();
		
		this.tris = new Triangle[tris.size()];
		backupTris = new Triangle[tris.size()];
		
		for(int i = 0; i < tris.size(); i++)
		{
			this.tris[i] = tris.get(i);
			backupTris[i] = this.tris[i].clone();
		}
	}
	
	public void translate(float x, float y, float z)
	{
		if(x != 0f || y != 0f || z != 0f)
			applyMat(MatUtils.transMat(x, y, z));
		
		this.x += x;
		this.y += y;
		this.z += z;
	}
	
	
	public void rotate(float alpha, float beta, float gamma)
	{		
		float x = this.x;
		float y = this.y;
		float z = this.z;
		
		alpha += this.alpha;
		beta += this.beta;
		gamma += this.gamma;
		
		reset();
		
		if(alpha != 0f)
			applyMat(MatUtils.xRotMat(alpha));
		
		if(beta != 0f)
			applyMat(MatUtils.yRotMat(beta));
		
		if(gamma != 0f)
			applyMat(MatUtils.zRotMat(gamma));
		
		this.alpha = alpha;
		this.beta = beta;
		this.gamma = gamma;
		
		translate(x, y, z);
	}
	
	/**********************************************************************/
	
	public void reset()
	{
		for(int i = 0; i < tris.length; i++)
			tris[i] = backupTris[i].clone();
		
		x = 0f;
		y = 0f;
		z = 0f;
		
		alpha = 0f;
		beta = 0f;
		gamma = 0f;
	}	
	
	private void applyMat(float[][] m)
	{
		for(int i = 0; i < tris.length; i++)
			tris[i].applyMat(m);
	}
}

