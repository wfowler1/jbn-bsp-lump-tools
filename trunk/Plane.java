// Plane class

// This class holds data on ONE plane. It's only really useful when
// used in an array along with many others. Each piece of data has
// its own variable.

public class Plane {
	
	// INITIAL DATA DECLARATION AND DEFINITION OF CONSTANTS
	
	public final int X=0;
	public final int Y=1;
	public final int Z=2;
	
	private float[] coords=new float[3];
	private float dist;
	private int type;
	
	// CONSTRUCTORS
	
	// This one takes the components separate and in the correct data type
	public Plane(float inX, float inY, float inZ, float inDist, int inType) {
		coords[X]=inX;
		coords[Y]=inY;
		coords[Z]=inZ;
		dist=inDist;
		type=inType;
	}
	
	
	public Plane(float[] inCoords, float inDist, int inType) throws InvalidPlaneException{
		if(inCoords.length!=3) {
			throw new InvalidPlaneException();
		}
		coords=inCoords;
		dist=inDist;
		type=inType;
	}

	// This one takes an array of bytes (as if read directly from a file) and reads them
	// directly into the proper data types.
	public Plane(byte[] in) throws InvalidPlaneException {
		if(in.length!=20) {
			throw new InvalidPlaneException();
		}
		int myInt=(in[3] << 24) | ((in[2] & 0xff) << 16) | ((in[1] & 0xff) << 8) | (in[0] & 0xff);
		coords[X]=Float.intBitsToFloat(myInt);
		myInt=(in[7] << 24) | ((in[6] & 0xff) << 16) | ((in[5] & 0xff) << 8) | (in[4] & 0xff);
		coords[Y]=Float.intBitsToFloat(myInt);
		myInt=(in[11] << 24) | ((in[10] & 0xff) << 16) | ((in[9] & 0xff) << 8) | (in[8] & 0xff);
		coords[Z]=Float.intBitsToFloat(myInt);
		myInt=(in[15] << 24) | ((in[14] & 0xff) << 16) | ((in[13] & 0xff) << 8) | (in[12] & 0xff);
		dist=Float.intBitsToFloat(myInt);
		type=(in[19] << 24) | ((in[18] & 0xff) << 16) | ((in[17] & 0xff) << 8) | (in[16] & 0xff);
	}
	
	// METHODS
	
	// ACCESSORS/MUTATORS
	
	// returns the coordinates as a float3
	public float[] getCoords() {
		return coords;
	}
	
	public float getX() {
		return coords[X];
	}
	
	public float getY() {
		return coords[Y];
	}
	
	public float getZ() {
		return coords[Z];
	}
	
	public float getDist() {
		return dist;
	}
	
	public int getType() {
		return type;
	}
}