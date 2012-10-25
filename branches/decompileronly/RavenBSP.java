// RavenBSP class
// This class gathers all relevant information from the lumps of a Raven software BSP.
// Used in Jedi Outcast and Soldier of Fortune 2.

public class RavenBSP extends v46BSP {

	// INITIAL DATA DECLARATION AND DEFINITION OF CONSTANTS
	
	// This is the version of the BSP. This will determine the lumps order and aid in
	// decompilation.
	public static final int VERSION=1347633746; // While this isn't actually the version, it sure is distinctive.
	
	// Each lump has its own class for handling its specific data structures.
	// These are the only lumps we need for decompilation.
	// Many lumps are inherited from v46BSP
	private RavenBrushSides rbrushSides;
	private RavenVertices rvertices;
	private RavenFaces rfaces;
	
	// CONSTRUCTORS
	public RavenBSP(String path) {
		super(path);
	}
	
	// METHODS
	
	public void printBSPReport() {
		super.printBSPReport();
		try {
			Window.println("Brush sides lump: "+rbrushSides.getLength()+" bytes, "+rbrushSides.length()+" items",Window.VERBOSITY_MAPSTATS);
		} catch(java.lang.NullPointerException e) {
			Window.println("Brush sides not yet parsed!",Window.VERBOSITY_MAPSTATS);
		}
		try {
			Window.println("Vertices lump: "+rvertices.getLength()+" bytes, "+rvertices.length()+" items",Window.VERBOSITY_MAPSTATS);
		} catch(java.lang.NullPointerException e) {
			Window.println("Vertices not yet parsed!",Window.VERBOSITY_MAPSTATS);
		}
		try {
			Window.println("Faces lump: "+rfaces.getLength()+" bytes, "+rfaces.length()+" items",Window.VERBOSITY_MAPSTATS);
		} catch(java.lang.NullPointerException e) {
			Window.println("Faces not yet parsed!",Window.VERBOSITY_MAPSTATS);
		}
	}
	
	// ACCESSORS/MUTATORS
	public void setRBrushSides(byte[] data) {
		this.rbrushSides=new RavenBrushSides(data);
	}
	
	public RavenBrushSides getRBrushSides() {
		return this.rbrushSides;
	}
	
	public void setRVertices(byte[] data) {
		this.rvertices=new RavenVertices(data);
	}
	
	public RavenVertices getRVertices() {
		return this.rvertices;
	}
	
	public void setRFaces(byte[] data) {
		this.rfaces=new RavenFaces(data);
	}
	
	public RavenFaces getRFaces() {
		return this.rfaces;
	}
}