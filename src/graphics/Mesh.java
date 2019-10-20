package graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.ImageIcon;

public final class Mesh extends Obj3D
{	
	private final boolean hasTexture;
	
	private Color color = null;
	public BufferedImage tex = null; // change to private
	
	public Triangle[] tris;
	private Triangle[] backupTris;
	
	public Mesh(String filePath, boolean hasTexture, Color color, String meshPath) throws FileNotFoundException
	{		
		this.hasTexture = hasTexture;
		
		if(!hasTexture)
		{
			if(color == null)
				throw new NullPointerException("The color cannot be null.");
			
			this.tex = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
			tex.setRGB(0, 0, color.getRGB());
		}
		else
			tex = toBufferedImage(new ImageIcon(meshPath).getImage());
		
		tex.setAccelerationPriority(1);
		
		this.color = color;	
		
		vPosition = new Vect3D();	
		
		FileReader fr = new FileReader(filePath);
		Scanner sca = new Scanner(fr);
		
		ArrayList<Vect3D> vectors = new ArrayList<Vect3D>();
		ArrayList<Vect2D> texs = new ArrayList<Vect2D>();
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
					case "vt": texs.add(new Vect2D(Float.parseFloat(s[1]), Float.parseFloat(s[2]))); break;
					case "f":
						if(!hasTexture)
						{
							Vect3D[] p = new Vect3D[3];
							p[0] = vectors.get(Integer.parseInt(s[1]) - 1);
							p[1] = vectors.get(Integer.parseInt(s[2]) - 1);
							p[2] = vectors.get(Integer.parseInt(s[3]) - 1);
							
							Vect2D[] t = new Vect2D[3];
							t[0] = new Vect2D();
							t[1] = new Vect2D();
							t[2] = new Vect2D();
							
							tris.add(new Triangle(p, t, this.color, 1f));							
						}
						else
						{							
							Vect3D[] p = new Vect3D[3];
							Vect2D[] t = new Vect2D[3];
							
							String[] sf0 = s[1].split("/"); 
							String[] sf1 = s[2].split("/"); 
							String[] sf2 = s[3].split("/");
							
							p[0] = vectors.get(Integer.parseInt(sf0[0]) - 1);
							p[1] = vectors.get(Integer.parseInt(sf1[0]) - 1);
							p[2] = vectors.get(Integer.parseInt(sf2[0]) - 1);
							t[0] = texs.get(Integer.parseInt(sf0[1]) - 1);
							t[1] = texs.get(Integer.parseInt(sf1[1]) - 1);
							t[2] = texs.get(Integer.parseInt(sf2[1]) - 1);
							
							tris.add(new Triangle(p, t, this.color, 1f));
						}
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
	
	public boolean hasTexture()
	{
		return hasTexture;
	}
	
	public void setupPosition(float x, float y, float z, float pitch, float yaw, float roll)
	{
		for(int i = 0; i < tris.length; i++)
			backupTris[i] = tris[i];
		
		float[][] mRot = MathUtils.mulMat(MathUtils.mulMat(MathUtils.xRotMat(pitch), MathUtils.yRotMat(yaw)), MathUtils.zRotMat(roll));
		applyMat(mRot);
		
		applyMat(MathUtils.transMat(x, y, z));
		
		resetTris();
	}
	
	private void resetTris()
	{
		for(int i = 0; i < tris.length; i++)	
			tris[i] = backupTris[i].clone();
	}
	
	public void move(float deltaLeft, float deltaUp, float deltaForward)
	{
		if(deltaLeft != 0 || deltaUp != 0 || deltaForward != 0)
		{
			super.move(deltaLeft, deltaUp, deltaForward);
			applyMat(MathUtils.transMat(vResult.x, vResult.y, vResult.z));
		}
	}
	
	public void rotate(float deltaPitch, float deltaYaw, float deltaRoll)
	{
		if(deltaPitch != 0 || deltaYaw != 0 || deltaRoll != 0)
		{
			super.rotate(deltaPitch, deltaYaw, deltaRoll);			
			applyMat(MathUtils.mulMat(MathUtils.mulMat(MathUtils.xRotMat(deltaPitch), MathUtils.yRotMat(deltaYaw)), MathUtils.zRotMat(deltaRoll)));
		}
	}
	
	protected void costrainAngles()
	{
		pitch = (float)(pitch > Math.PI ? - (Math.PI * 2 - pitch) : pitch);
		pitch = (float)(pitch < - Math.PI ? pitch + Math.PI * 2: pitch);
		
		yaw = (float)(yaw > Math.PI ? - (Math.PI * 2 - yaw) : yaw);
		yaw = (float)(yaw < - Math.PI ? yaw + Math.PI * 2: yaw);	
		
		roll = (float)(roll > Math.PI ? - (Math.PI * 2 - roll) : roll);
		roll = (float)(roll < - Math.PI ? roll + Math.PI * 2: roll);
	}
	
	private void applyMat(float[][] m)
	{
		for(int i = 0; i < tris.length; i++)
			tris[i].applyMat(m);
	}
	
	public Color getColor()
	{
		return color;
	}
	
	public void setColor(Color color)
	{
		for(int i = 0; i < tris.length; i++)
		{
			tris[i].color = color;
			backupTris[i].color = color;
		}
		
		this.color = color;
	}
	
	private BufferedImage toBufferedImage(Image img)
	{
		BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D bGr = bimage.createGraphics();
		bGr.drawImage(img, 0, 0, null);
		bGr.dispose();
		
		return bimage;
	}
}