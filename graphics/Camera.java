package graphics;

public final class Camera extends Obj3D implements Cloneable
{
	public Camera(float x, float y, float z)
	{
		vPosition = new Vect3D(x, y, z);
	}
	
	float[][] getMatView()
	{		
		Vect3D vTarget = MathUtils.addVec(vPosition, vForward);
		float[][] mCamera = MathUtils.pointAtMat(vPosition, vTarget, vUp);
		
		return MathUtils.lookAtMat(mCamera);
	}
	
	protected void costrainAngles()
	{
	/*	pitch = (float)(pitch < - Math.PI / 2 ? - Math.PI / 2 : pitch);
		pitch = (float)(pitch > Math.PI / 2 ? Math.PI / 2 : pitch); */
		
		pitch = (float)(pitch > Math.PI ? - (Math.PI * 2 - pitch) : pitch);
		pitch = (float)(pitch < - Math.PI ? pitch + Math.PI * 2: pitch);
		
		yaw = (float)(yaw > Math.PI ? - (Math.PI * 2 - yaw) : yaw);
		yaw = (float)(yaw < - Math.PI ? yaw + Math.PI * 2: yaw);	
		
		roll = (float)(roll > Math.PI ? - (Math.PI * 2 - roll) : roll);
		roll = (float)(roll < - Math.PI ? roll + Math.PI * 2: roll);
	}
	
	public Camera clone()
	{
		try
		{
			return (Camera) super.clone();
		}
		catch(Exception e)
		{
			return null;
		}
	}
}