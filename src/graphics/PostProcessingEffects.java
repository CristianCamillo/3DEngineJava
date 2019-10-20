package graphics;

import java.awt.Color;

public class PostProcessingEffects
{
	public static void outerLines(DepthBuffer db, Color color)
	{
		checkDBNotNull(db);
		checkColorNotNull(color);
		
		for(int y = 0; y < db.getHeight(); y++)
			for(int x = 0; x < db.getWidth(); x++)
				if(db.getValue(x, y - 1) == 0f ||
				   db.getValue(x, y + 1) == 0f ||
				   db.getValue(x - 1, y) == 0f ||
				   db.getValue(x + 1, y) == 0f)
					db.setColor(x, y, color);
	}
	
	public static Color negative(DepthBuffer db, Color bgc)
	{
		checkDBNotNull(db);
		checkColorNotNull(bgc);
		
		for(int y = 0; y < db.getHeight(); y++)
			for(int x = 0; x < db.getWidth(); x++)
			{
				Color color = db.getColor(x, y);
				if(color != null)
					db.setColor(x, y, negative(color));
			}
		
		return negative(bgc);
	}
	
	public static Color saturate(DepthBuffer db, Color bgc, float sat)
	{
		checkDBNotNull(db);
		checkColorNotNull(bgc);
		
		for(int y = 0; y < db.getHeight(); y++)
			for(int x = 0; x < db.getWidth(); x++)
			{
				Color color = db.getColor(x, y);				
				if(color != null)
					db.setColor(x, y, saturate(color, sat));
			}
		
		return saturate(bgc, sat);
	}
	
	public static Color grayScale(DepthBuffer db, Color bgc)
	{
		return saturate(db, bgc, 0f);
	}
	
	/*public static void technicolor2(DepthBuffer db)
	{
		checkDBNotNull(db);
		
		Vect3D colorStrenght = new Vect3D(0.2f, 0.2f, 0.2f);
		float brightness = 1.0f;
		float saturation = 1.0f;
		float strength = 1.0f;

		for(int y = 0; y < db.getHeight(); y++)
			for(int x = 0; x < db.getWidth(); x++)
			{		
				Color c = db.getColor(x, y);  // to saturate
				Vect3D color = new Vect3D(c.getRed(), c.getGreen(), c.getBlue());			
				Vect3D temp = new Vect3D(1f - color.x, 1f - color.y, 1f - color.z);
				Vect3D target = temp.grg;
				Vect3D target2 = temp.bbr;
				Vect3D temp2 = color * target;
				temp2 *= target2;
	
				temp = temp2 * ColorStrength;
				temp2 *= Brightness;
	
				target = temp.grg;
				target2 = temp.bbr;
	
				temp = MathUtils.subVec(color, target);
				temp = MathUtils.addVec(temp, temp2);
				temp2 = MathUtils.subVec(temp, target2);
	
				color = lerp(color, temp2, strength);
				float temp3 = MathUtils.dotProd(color, new Vect3D(0.333f, 0.333f, 0.333f));
				color = lerp(new Vect3D(temp3, temp3, temp3), color, saturation);
	
				db.setColor(x, y, new Color(color.x, color.y, color.z));
			}
	}*/
	
	public static Color fakeHDR(DepthBuffer db, Color bgc) // no
	{
		checkDBNotNull(db);
		checkColorNotNull(bgc);
		
		float HDRPower = 1.3f;
		float[] radius = new float[2];
		radius[0] = 0.793f;
		radius[1] = 0.87f;
		
		Vect2D[] toAddVec = new Vect2D[]
		{
			new Vect2D(1.5f, -1.5f),
			new Vect2D(-1.5f, -1.5f),
			new Vect2D(1.5f, 1.5f),
			new Vect2D(-1.5f, 1.5f),
			new Vect2D(0f, -2.5f),
			new Vect2D(0f, 2.5f),
			new Vect2D(-2.5f, 0f),
			new Vect2D(2.5f, 0f),
		};
		
		for(int y = 0; y < db.getHeight(); y++)
			for(int x = 0; x < db.getWidth(); x++)
			{
				Vect2D texCoord = new Vect2D(x, y);
				Vect2D curPos;
				float[] bloomSum = new float[2];
				bloomSum[0] = 0;
				bloomSum[1] = 0;
				
				for(int j = 0; j < 2; j++)
				{
					for(int i = 0; i < 8; i++)
					{
						curPos = MathUtils.addVec(texCoord, MathUtils.mulVec(toAddVec[i], radius[j]));
						Color color = db.getColor(Math.round(curPos.u), Math.round(curPos.v));
						bloomSum[j] += color != null ? color.getRGB() : bgc.getRGB();
					}

					if(j == 0)
						bloomSum[j] *= 0.005;
					else
						bloomSum[j] *= 0.010;
				}
				
				Color c = db.getColor(x, y);
				float color = c != null ? c.getRGB() : bgc.getRGB();
				float dist = radius[1] - radius[0];
				float HDR = (color + (bloomSum[1] - bloomSum[0])) * dist;
				float blend = HDR + color;
				color = (float)(Math.pow(Math.abs(blend), HDRPower) + HDR); // pow - don't use fractions for HDRpower				
				
				db.setColor(x, y, saturate(new Color((int)color), 0.5f));
			}
		
		return saturate(bgc, 0.5f); 
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Utils
	
	private static void checkDBNotNull(DepthBuffer db)
	{
		if(db == null)
			throw new NullPointerException("The depth buffer cannot be null.");
	}
	
	private static void checkColorNotNull(Color color)
	{
		if(color == null)
			throw new NullPointerException("The color cannot be null.");
	}
	
	private static Color negative(Color color)
	{		
		return new Color(255 - color.getRed(), 255 - color.getGreen(), 255 - color.getBlue());
	}
	
	private static Color saturate(Color color, float sat)
	{		
		float[] hsb = new float[3];
		
		Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);
		
		return new Color(Color.HSBtoRGB(hsb[0], sat, hsb[2]));
	}
	
	private static Vect3D lerp(Vect3D a, Vect3D b, float t)
	{
		return MathUtils.addVec(a, MathUtils.mulVec(MathUtils.subVec(b, a), t));
	}
	
	private static float dotProd(Vect3D v, float n)
	{
		return v.x * n + v.y * n + v.z * n;
	}
	
	private static float dotProd(Vect3D v0, float extra0, Vect3D v1, float extra1)
	{
		return v0.x * v1.x + v0.y * v1.y + v0.z * v1.z + extra0 * extra1;
	}
}
