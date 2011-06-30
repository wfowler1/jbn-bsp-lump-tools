// BrushSide class

// This class holds data of a single brush side.

public class BrushSide {
	
	// INITIAL DATA DECLARATION AND DEFINITION OF CONSTANTS
	
	private int plane;
	private int face; // I don't understand why this lump references
	                  // faces. Brushes are referenced by leaves which
							// also references faces. It seems like it's
							// duplicating faces for no good reason.
	
	// CONSTRUCTORS
	
	// This one takes the components separate and in the correct data type
	public BrushSide(int inPlane, int inFace) {
		plane=inPlane;
		face=inFace;
	}

	// This one takes an array of bytes (as if read directly from a file) and reads them
	// directly into the proper data types.
	public BrushSide(byte[] in) {
		plane=(in[3] << 24) | ((in[2] & 0xff) << 16) | ((in[1] & 0xff) << 8) | (in[0] & 0xff);
		face=(in[7] << 24) | ((in[6] & 0xff) << 16) | ((in[5] & 0xff) << 8) | (in[4] & 0xff);
	}
	
	// METHODS
	
	// ACCESSORS/MUTATORS
}