package graphics;

import java.awt.Color;
import java.awt.Graphics;

public class DepthBuffer
{
	private final float[] depthBuffer;
	private final Color[] colorBuffer;
	
	private final int width;
	private final int height;
	
	public DepthBuffer(int width, int height)
	{
		if(width <= 0 || height <= 0)
			throw new IllegalArgumentException("Width and height must be greater than 0.");
		
		depthBuffer = new float[width * height];
		colorBuffer = new Color[width * height];
		
		this.width = width;
		this.height = height;
	}
	
	public float getValue(int x, int y)
	{
		return depthBuffer[x + y * getWidth()];
	}
	
	public Color getColor(int x, int y)
	{
		return colorBuffer[x + y * getWidth()];
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	public void reset()
	{
		for(int y = 0; y < height; y++)
			for(int x = 0; x < width; x++)
				depthBuffer[x + y * getWidth()] = 0f;
	}
	
	public void setValue(int x, int y, float v)
	{
		depthBuffer[x + y * getWidth()] = v;
	}
	
	public void setColor(int x, int y, Color color)
	{
		colorBuffer[x + y * getWidth()] = color;
	}
	
	public void draw(Graphics g)
	{
		for(int y = 0; y < getHeight(); y++)
			for(int x = 0; x < getWidth(); x++)
				if(getValue(x, y) > 0f)
				{
					g.setColor(getColor(x, y));
					g.drawLine(x, y, x, y);
				}
	}
}
