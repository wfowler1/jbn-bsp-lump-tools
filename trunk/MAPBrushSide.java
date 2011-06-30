// MAPBrushSide class
// Holds all the data for a brush side in the format for a .MAP file version 510.

public class MAPBrushSide {

	// INITIAL DATA DECLARATION AND DEFINITION OF CONSTANTS
	private Vertex[] plane;
	private String texture;
	private float[] textureS;
	private float textureShiftS;
	private float[] textureT;
	private float textureShiftT;
	private float texRot=0;
	private float texScaleX;
	private float texScaleY;
	private int flags;
	private String material;
	private float lgtScale;
	private float lgtRot;
	
	public static final int X=0;
	public static final int Y=1;
	public static final int Z=2;
	
	// CONSTRUCTORS
	public MAPBrushSide(Vertex[] inPlane, String inTexture, float[] inTextureS, float inTextureShiftS, float[] inTextureT, float inTextureShiftT, float inTexRot,
	                    float inTexScaleX, float inTexScaleY, int inFlags, String inMaterial, float inLgtScale, float inLgtRot) {
		if(inPlane.length!=3 || inTextureS.length!=3 || inTextureT!=3) {
			// TODO: Write an exception for this
			throw new Exception();
		}
		plane=inPlane;
		texture=inTexture;
		textureS=inTextureS;
		textureShiftS=inTextureShiftS;
		textureT=inTextureT;
		textureShiftT=inTextureShiftT;
		texRot=inTexRot;
		texScaleX=inTexScaleX;
		texScaleY=inTexScaleY;
		flags=inFlags;
		materiaa=inMaterial;
		lgtScale=inLgtScale;
		lgtRot=inLgtRot;
	}
	
	// METHODS
	
	// ACCESSORS/MUTATORS
}