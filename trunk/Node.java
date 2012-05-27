// Node class

// Holds all the data for a node in a NightFire map.

public class Node {
	
	// INITIAL DATA DECLARATION AND DEFINITION OF CONSTANTS
	
	public final int X=0;
	public final int Y=1;
	public final int Z=2;
	
	private int plane;
	private int child1;
	private int child2;
	private float[] mins=new float[3];
	private float[] maxs=new float[3];
	
	// CONSTRUCTORS
	
	// This constructor takes all data in their proper data types
	public Node(int inPlane, int inChild1, int inChild2, float inMinX, float inMinY, float inMinZ, float inMaxX, float inMaxY, float inMaxZ) {
		plane=inPlane;
		child1=inChild1;
		child2=inChild2;
		mins[X]=inMinX;
		mins[Y]=inMinY;
		mins[Z]=inMinZ;
		maxs[X]=inMaxX;
		maxs[Y]=inMaxY;
		maxs[Z]=inMaxZ;
	}
	
	public Node(int inPlane, int inChild1, int inChild2, float[] inMins, float[] inMaxs) {
		plane=inPlane;
		child1=inChild1;
		child2=inChild2;
		mins=inMins;
		maxs=inMaxs;
	}
	
	// This constructor takes 36 bytes in a byte array, as though
	// it had just been read by a FileInputStream.
	public Node(byte[] in) {
		plane=(in[3] << 24) | ((in[2] & 0xff) << 16) | ((in[1] & 0xff) << 8) | (in[0] & 0xff);
		child1=(in[7] << 24) | ((in[6] & 0xff) << 16) | ((in[5] & 0xff) << 8) | (in[4] & 0xff);
		child2=(in[11] << 24) | ((in[10] & 0xff) << 16) | ((in[9] & 0xff) << 8) | (in[8] & 0xff);
		int myInt=(in[15] << 24) | ((in[14] & 0xff) << 16) | ((in[13] & 0xff) << 8) | (in[12] & 0xff);
		mins[X]=Float.intBitsToFloat(myInt);
		myInt=(in[19] << 24) | ((in[18] & 0xff) << 16) | ((in[17] & 0xff) << 8) | (in[16] & 0xff);
		mins[Y]=Float.intBitsToFloat(myInt);
		myInt=(in[23] << 24) | ((in[22] & 0xff) << 16) | ((in[21] & 0xff) << 8) | (in[20] & 0xff);
		mins[Z]=Float.intBitsToFloat(myInt);
		myInt=(in[27] << 24) | ((in[26] & 0xff) << 16) | ((in[25] & 0xff) << 8) | (in[24] & 0xff);
		maxs[X]=Float.intBitsToFloat(myInt);
		myInt=(in[31] << 24) | ((in[30] & 0xff) << 16) | ((in[29] & 0xff) << 8) | (in[28] & 0xff);
		maxs[Y]=Float.intBitsToFloat(myInt);
		myInt=(in[35] << 24) | ((in[34] & 0xff) << 16) | ((in[33] & 0xff) << 8) | (in[32] & 0xff);
		maxs[Z]=Float.intBitsToFloat(myInt);
	}
	
	// METHODS
	
	// ACCESSORS/MUTATORS
	
	public int getPlane() {
		return plane;
	}
	
	public int getChild1() {
		return child1;
	}
	
	public int getChild2() {
		return child2;
	}
	
	public float getMinX() {
		return mins[X];
	}
	
	public float getMinY() {
		return mins[Y];
	}
	
	public float getMinZ() {
		return mins[Z];
	}
	
	public float getMaxX() {
		return maxs[X];
	}
	
	public float getMaxY() {
		return maxs[Y];
	}
	
	public float getMaxZ() {
		return maxs[Z];
	}
	
	public float[] getMaxs() {
		return maxs;
	}
	
	public float[] getMins() {
		return mins;
	}
	
	// People probably shouldn't fuck with these, though some neat experiments can
	// be created by changing a single node reference to a random leaf. You can
	// get rid of half the map just by changing one int!
	public void setPlane(int in) {
		plane=in;
	}
	
	public void setChild1(int in) {
		child1=in;
	}
	
	public void setChild2(int in) {
		child2=in;
	}
	
	// Some fun mods could be made by changing these mins and maxs, if done properly.
	public void setMinX(float in) {
		mins[X]=in;
	}
	
	public void setMinY(float in) {
		mins[Y]=in;
	}
	
	public void setMinZ(float in) {
		mins[Z]=in;
	}
	
	public void setMaxX(float in) {
		maxs[X]=in;
	}
	
	public void setMaxY(float in) {
		maxs[Y]=in;
	}
	
	public void setMaxZ(float in) {
		maxs[Z]=in;
	}
}