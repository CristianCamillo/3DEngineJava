package graphics;

public abstract class Obj3D implements Cloneable
{
	protected final static Vect3D V_LEFT = new Vect3D(1, 0, 0);
	protected final static Vect3D V_UP = new Vect3D(0, 1, 0);
	protected final static Vect3D V_FORWARD = new Vect3D(0, 0, 1);
	
	Vect3D vPosition = null;	
	protected Vect3D vLeft = new Vect3D(1, 0, 0);  // depends on pitch, yaw and roll
	protected Vect3D vUp = new Vect3D(0, 1, 0);  // depends on pitch, yaw and roll
	protected Vect3D vForward = new Vect3D(0, 0, 1);  // depends on pitch, yaw and roll
	protected float pitch = 0f;
	protected float yaw = 0f;
	protected float roll = 0f;
	
	protected Vect3D vResult = null;
	
	public void move(float deltaLeft, float deltaUp, float deltaForward)
	{
		if(deltaLeft != 0 || deltaUp != 0 || deltaForward != 0)
		{
			Vect3D vDeltaLeft = MathUtils.mulVec(vLeft, deltaLeft);
			Vect3D vDeltaUp = MathUtils.mulVec(vUp, deltaUp);
			Vect3D vDeltaForward =  MathUtils.mulVec(vForward, deltaForward);			
			
			vResult = MathUtils.addVec(MathUtils.addVec(vDeltaLeft, vDeltaUp), vDeltaForward);			
			vPosition = MathUtils.addVec(vPosition, vResult);
		}
	}
	
	public void rotate(float deltaPitch, float deltaYaw, float deltaRoll)
	{	
		if(deltaPitch != 0 || deltaYaw != 0 || deltaRoll != 0)
		{
			pitch += deltaPitch;
			yaw += deltaYaw;
			roll += deltaRoll;
			
			costrainAngles();
			
			float[][] mRot = MathUtils.mulMat(MathUtils.mulMat(MathUtils.xRotMat(pitch), MathUtils.yRotMat(yaw)), MathUtils.zRotMat(roll));
			vLeft = MathUtils.normVec(MathUtils.mulVecMat(V_LEFT, mRot));
			vUp = MathUtils.normVec(MathUtils.mulVecMat(V_UP, mRot));
			vForward = MathUtils.normVec(MathUtils.mulVecMat(V_FORWARD, mRot));
		}
	}
	
	protected abstract void costrainAngles();
	
	/* GETTERS */
	
	public float getX()
	{
		return vPosition.x;
	}
	
	public float getY()
	{
		return vPosition.y;
	}
	
	public float getZ()
	{
		return vPosition.z;
	}
	
	public float getPitch()
	{
		return (float)Math.toDegrees(pitch);
	}
	
	public float getYaw()
	{
		return (float)Math.toDegrees(yaw);
	}
	
	public float getRoll()
	{
		return (float)Math.toDegrees(roll);
	}
	
	public Obj3D clone()
	{
		try
		{
			return (Obj3D) super.clone();
		}
		catch(Exception e)
		{
			return null;
		}
	}
}
