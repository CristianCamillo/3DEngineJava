package graphics;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class Texturizer
{
	public static void texturedTriangle(int x1, int y1, float u1, float v1, float w1,
								 		int x2, int y2, float u2, float v2, float w2,
								 		int x3, int y3, float u3, float v3, float w3,
								 		BufferedImage tex, DepthBuffer db, float lum, boolean showWireframe)
	{
		lum = 1f; // forced, remove
		
		if(y2 < y1)
		{
			x1 = getItself(x2, x2 = x1);
			y1 = getItself(y2, y2 = y1);			
			u1 = getItself(u2, u2 = u1);
			v1 = getItself(v2, v2 = v1);
			w1 = getItself(w2, w2 = w1);
		}
		
		if(y3 < y1)
		{
			x1 = getItself(x3, x3 = x1);
			y1 = getItself(y3, y3 = y1);			
			u1 = getItself(u3, u3 = u1);
			v1 = getItself(v3, v3 = v1);
			w1 = getItself(w3, w3 = w1);
		}
		
		if(y3 < y2)
		{
			x2 = getItself(x3, x3 = x2);
			y2 = getItself(y3, y3 = y2);			
			u2 = getItself(u3, u3 = u2);
			v2 = getItself(v3, v3 = v2);
			w2 = getItself(w3, w3 = w2);
		}
		
		int   dx1 = x2 - x1;
		int   dy1 = y2 - y1;		
		float du1 = u2 - u1;
		float dv1 = v2 - v1;		
		float dw1 = w2 - w1;
		
		int   dx2 = x3 - x1;
		int   dy2 = y3 - y1;
		float du2 = u3 - u1;
		float dv2 = v3 - v1;	
		float dw2 = w3 - w1;
		
		float u, v, w;
		
		float daxStep = 0;
		float dbxStep = 0;
		float du1Step = 0;
		float dv1Step = 0;
		float du2Step = 0;
		float dv2Step = 0;
		float dw1Step = 0;
		float dw2Step = 0;
		
		if(dy1 != 0) daxStep = dx1 * 1f / Math.abs(dy1);
		if(dy2 != 0) dbxStep = dx2 * 1f / Math.abs(dy2);
		
		if(dy1 != 0) du1Step = du1 * 1f / Math.abs(dy1);
		if(dy1 != 0) dv1Step = dv1 * 1f / Math.abs(dy1);
		if(dy1 != 0) dw1Step = dw1 * 1f / Math.abs(dy1);
		
		if(dy2 != 0) du2Step = du2 * 1f / Math.abs(dy2);
		if(dy2 != 0) dv2Step = dv2 * 1f / Math.abs(dy2);
		if(dy2 != 0) dw2Step = dw2 * 1f / Math.abs(dy2);
		
		if(dy1 != 0)
		{
			for(int y = y1; y <= y2; y++)
			{
				int ax = (int)(x1 + (y - y1) * daxStep);
				int bx = (int)(x1 + (y - y1) * dbxStep);
				
				float su = u1 + (y - y1) * du1Step;
				float sv = v1 + (y - y1) * dv1Step;
				float sw = w1 + (y - y1) * dw1Step;
				
				float eu = u1 + (y - y1) * du2Step;
				float ev = v1 + (y - y1) * dv2Step;
				float ew = w1 + (y - y1) * dw2Step;
				
				if(ax > bx)
				{
					ax = getItself(bx, bx = ax);
					su = getItself(eu, eu = su);
					sv = getItself(ev, ev = sv);
					sw = getItself(ew, ew = sw);
				}
				
				u = su;
				v = sv;
				w = sw;
				
				float tstep = 1f / (bx - ax);
				float t = 0f;
				
				for(int x = ax; x < bx; x++)
				{
					u = (1f - t) * su + t * eu;
					v = (1f - t) * sv + t * ev;
					w = (1f - t) * sw + t * ew;
					
					if(showWireframe && (x == ax || x == bx - 1 || y == y1 || y == y2))
					{
						db.setValue(x, y, w);
						db.setColor(x, y, Color.WHITE);
					}					
					else if(w > db.getValue(x, y))
					{
						db.setValue(x, y, w);
						Color color = sampleColor(u / w, v / w, tex);
						color = new Color((int)(color.getRed() * lum), (int)(color.getGreen() * lum), (int)(color.getBlue() * lum));
						db.setColor(x, y, color);
					}

					t += tstep;
				}
			}
		}
		
		dy1 = y3 - y2; //// were in if - start
		dx1 = x3 - x2;
		dv1 = v3 - v2;
		du1 = u3 - u2;
		dw1 = w3 - w2;
		
		if(dy1 != 0) daxStep = dx1 * 1f / Math.abs(dy1);
		if(dy2 != 0) dbxStep = dx2 * 1f / Math.abs(dy2);
		
		du1Step = 0;
		dv1Step = 0;
		
		if(dy1 != 0) du1Step = du1 * 1f / Math.abs(dy1);
		if(dy1 != 0) dv1Step = dv1 * 1f / Math.abs(dy1);
		if(dy1 != 0) dw1Step = dw1 * 1f / Math.abs(dy1); ///// were in if - end
		
		if(dy1 != 0)
		{			
			for(int y = y2; y <= y3; y++)
			{
				int ax = (int)(x2 + (y - y2) * daxStep);
				int bx = (int)(x1 + (y - y1) * dbxStep);
				
				float su = u2 + (y - y2) * du1Step;
				float sv = v2 + (y - y2) * dv1Step;
				float sw = w1 + (y - y1) * dw1Step;
				
				float eu = u1 + (y - y1) * du2Step;
				float ev = v1 + (y - y1) * dv2Step;
				float ew = w1 + (y - y1) * dw2Step;
				
				if(ax > bx)
				{
					ax = getItself(bx, bx = ax);
					su = getItself(eu, eu = su);
					sv = getItself(ev, ev = sv);
					sw = getItself(ew, ew = sw);
				}
				
				u = su;
				v = sv;
				w = sw;
				
				float tstep = 1f / (bx - ax);
				float t = 0f;
				
				for(int x = ax; x < bx; x++)
				{
					u = (1f - t) * su + t * eu;
					v = (1f - t) * sv + t * ev;
					w = (1f - t) * sw + t * ew;
					
					if(showWireframe && (x == ax || x == bx - 1 || y == y1 || y == y2))
					{
						db.setValue(x, y, w);
						db.setColor(x, y, Color.WHITE);
					}					
					else if(w > db.getValue(x, y))
					{
						db.setValue(x, y, w);
						Color color = sampleColor(u / w, v / w, tex);
						color = new Color((int)(color.getRed() * lum), (int)(color.getGreen() * lum), (int)(color.getBlue() * lum));
						db.setColor(x, y, color);
					}
					
					t += tstep;
				}
			}
		}
	}
	
	private static int getItself(int itself, int dummy)
	{
		return itself;
	}

	private static float getItself(float itself, float dummy)
	{
		return itself;
	}
	
	private static Color sampleColor(float x, float y, BufferedImage tex)
	{
		int sx = (int)(x * tex.getWidth());
		int sy = (int)(y * (tex.getHeight() - 1f));
		
	//	System.out.println(x + " " + y + "\n" + sx + " " + sy);
	//	System.exit(0);
		
	//	return new Color(tex.getRGB(sx, sy));
		
		if(sx < 0 || sx >= tex.getWidth() || sy < 0 || sy >= tex.getHeight())
			return Color.GREEN;
		else
			return new Color(tex.getRGB(sx, sy));
	}
}
