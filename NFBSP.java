// NFBSP class
//
// This class holds all references to the lump data structures after the
// lumps have been analyzed, and contains methods for manipulating these
// lumps, minimizing the amount of work needed to manually mod a map.
//
// The data for a specific lump is contained in its own Lump class, which
// is specific to that lump. This class is for manipulating and searching
// for data, while the Lump class merely holds, finds and replaces data.
//
// The data is stored in the exact same order in which is was read, keeping
// all the indices from one lump into another exactly the same (unless 
// something was edited).

// TODO:
// create methods for editing the lumps
// create a "map optimizer" recycling data structures with the exact same data

// The strange thing about the NightFire (and the other) BSP files is the
// virtual maze of data references. Leaves are born from Nodes created using
// planes. Leaves then (indirectly) reference faces and brushes, which in turn
// reference planes again (brushes through brush sides). The onle explanation
// I can come up with is faces and everything faces do handle how the map 
// looks visually, while brushes define the map physically. These things could
// have been handled as one thing, but they weren't for whatever reason.

// Perhaps an even stranger thing is how Nodes have bounding boxes and define
// leaves, which have their own bounding boxes, then models use several leaves
// and have THEIR own bounding boxes. How do these relate and interact, if at all?

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.io.DataOutputStream;
import java.io.FileOutputStream;

public class NFBSP {

	// INITIAL DATA DECLARATION AND DEFINITION OF CONSTANTS
	
	Runtime r = Runtime.getRuntime(); // Get a runtime object. This is for calling
	                                  // Java's garbage collector and does not need
												 // to be ported.

	// All lumps will be in the same folder. This String IS that folder.
	private String filepath;
	
	// Each lump has its own class for handling its specific data structures.
	private Lump00 myL0;
	private Lump01 myL1;
	private Lump02 myL2;
	private Lump03 myL3;
	private Lump04 myL4;
	private Lump05 myL5;
	private Lump06 myL6;
	private Lump07 myL7;
	private Lump08 myL8;
	private Lump09 myL9;
	private Lump10 myL10;
	private Lump11 myL11;
	private Lump12 myL12;
	private Lump13 myL13;
	private Lump14 myL14;
	private Lump15 myL15;
	private Lump16 myL16;
	private Lump17 myL17;
	
	// For modularity, this is handled as a constant throughout this class.
	public final int NUMLUMPS=18;
	
	// This just allows us to reference the lump by name rather than index.
	public final int ENTITIES=0;
	public final int PLANES=1;
	public final int TEXTURES=2;
	public final int MATERIALS=3;
	public final int VERTICES=4;
	public final int NORMALS=5;
	public final int INDICES=6;
	public final int VISIBILITY=7;
	public final int NODES=8;
	public final int FACES=9;
	public final int LIGHTING=10;
	public final int LEAVES=11;
	public final int MARKSURFACES=12;
	public final int MARKBRUSHES=13;
	public final int MODELS=14;
	public final int BRUSHES=15;
	public final int BRUSHSIDES=16;
	public final int TEXMATRIX=17;
	
	// This allows us to get the name of the lump using its index.
	public String[] LUMPNAMES = {"Entities", "Planes", "Textures", "Materials", "Vertices", "Normals", "Indices", "Visibility", "Nodes", "Faces",
	                             "Lighting", "Leaves", "Mark Surfaces", "Mark Brushes", "Models", "Brushes", "Brushsides", "Texmatrix"};
		
	// This holds the size of the data structures for each lump. If
	// the lump does not have a set size, the size is -1. If the lump's
	// size has not been determined yet, the size is 0.
	// The entities lump does not have a set data length per entity
	// Since there is literally no data in lump05, there is no data structure
	// Visibility varies for every map, and will be determined by constructor
	public int[] lumpDataSizes = {-1, 20, 64, 64, 12, -1, 4, 0, 36, 48, 3, 48, 4, 4, 56, 12, 8, 32};
	
	// CONSTRUCTORS
	// This accepts a file path and parses it into the form needed. If the file is empty (or not found)
	// the program fails nicely.
	public NFBSP(String in) {
		try {
			if (in.charAt(in.length()-1) != '\\' && in.charAt(in.length()-1) != '/') {
				in+="\\";
			}
			filepath=in;
			
			myL0 = new Lump00(filepath+"00 - Entities.txt");
			myL1 = new Lump01(filepath+"01 - Planes.hex");
			myL2 = new Lump02(filepath+"02 - Textures.hex");
			myL3 = new Lump03(filepath+"03 - Materials.hex");
			myL4 = new Lump04(filepath+"04 - Vertices.hex");
			myL5 = new Lump05(filepath+"05 - Normals.hex");
			myL6 = new Lump06(filepath+"06 - Indices.hex");
			myL7 = new Lump07(filepath+"07 - Visibility.hex");
			myL8 = new Lump08(filepath+"08 - Nodes.hex");
			myL9 = new Lump09(filepath+"09 - Faces.hex");
			myL10 = new Lump10(filepath+"10 - Lighting.hex");
			myL11 = new Lump11(filepath+"11 - Leaves.hex");
			myL12 = new Lump12(filepath+"12 - Mark Surfaces.hex");
			myL13 = new Lump13(filepath+"13 - Mark Brushes.hex");
			myL14 = new Lump14(filepath+"14 - Models.hex");
			myL15 = new Lump15(filepath+"15 - Brushes.hex");
			myL16 = new Lump16(filepath+"16 - Brushsides.hex");
			myL17 = new Lump17(filepath+"17 - Texmatrix.hex");
			
			r.gc(); // Take a minute to collect garbage, all the file parsing can leave a lot of crap data.
			
			lumpDataSizes[VISIBILITY] = myL7.getLengthOfData();
			
			System.out.println("Entities lump: "+myL0.getLength()+" bytes, "+myL0.getNumElements()+" items");
			System.out.println("Planes lump: "+myL1.getLength()+" bytes, "+myL1.getNumElements()+" items");
			System.out.println("Textures lump: "+myL2.getLength()+" bytes, "+myL2.getNumElements()+" items");
			System.out.println("Materials lump: "+myL3.getLength()+" bytes, "+myL3.getNumElements()+" items");
			System.out.println("Vertices lump: "+myL4.getLength()+" bytes, "+myL4.getNumElements()+" items");
			System.out.println("Normals lump: "+myL5.getLength()+" bytes");
			System.out.println("Indices lump: "+myL6.getLength()+" bytes, "+myL6.getNumElements()+" items");
			System.out.println("Visibility lump: "+myL7.getLength()+" bytes, "+myL7.getNumElements()+" items");
			System.out.println("Nodes lump: "+myL8.getLength()+" bytes, "+myL8.getNumElements()+" items");
			System.out.println("Faces lump: "+myL9.getLength()+" bytes, "+myL9.getNumElements()+" items");
			System.out.println("Lighting lump: "+myL10.getLength()+" bytes, "+myL10.getNumElements()+" items");
			System.out.println("Leaves lump: "+myL11.getLength()+" bytes, "+myL11.getNumElements()+" items");
			System.out.println("Leaf surfaces lump: "+myL12.getLength()+" bytes, "+myL12.getNumElements()+" items");
			System.out.println("Leaf brushes lump: "+myL13.getLength()+" bytes, "+myL13.getNumElements()+" items");
			System.out.println("Models lump: "+myL14.getLength()+" bytes, "+myL14.getNumElements()+" items");
			System.out.println("Brushes lump: "+myL15.getLength()+" bytes, "+myL15.getNumElements()+" items");
			System.out.println("Brush sides lump: "+myL16.getLength()+" bytes, "+myL16.getNumElements()+" items");
			System.out.println("Texture scales lump: "+myL17.getLength()+" bytes, "+myL17.getNumElements()+" items");
			
		} catch(java.lang.StringIndexOutOfBoundsException e) {
			System.out.println("Error: invalid path");
		} catch(java.io.FileNotFoundException e) {
			System.out.println("Error: lumps not found");
		} catch(java.io.IOException e) {
			System.out.println("Error: Funny lump size");
		}
	}
	
	// METHODS
	
	// +combineBSP(NFBSP)
	// One of the most ridiculous things I've ever coded. This method
	// will take another NFBSP class and combine the data between this
	// one and the "other". The result should be one set of BSP lumps
	// containing the data from both the input BSP's lumps, with all
	// the references in the second one change to account for the data
	// added by the first. When loaded into the game, this ideally
	// should load as if both maps had been loaded and merged together.
	//
	// It is highly recommended to run the BSP optimizer after this,
	// particularly since there will probably be duplicate materials
	// and textures that we only need one reference to. It's also
	// possible the maps will have identical planes and other structures,
	// but that's only likely to happen when combining two very similar
	// maps (such as the infiltrate and escape mission maps).
	public void combineBSP(NFBSP other) throws java.io.FileNotFoundException, java.io.IOException {
		myL0.add(other.getLump00());
		myL1.add(other.getLump01());
		myL2.add(other.getLump02());
		myL3.add(other.getLump03());
		myL4.add(other.getLump04());
		myL5.add(other.getLump05());
		myL6.add(other.getLump06());
	}
	
	// saveLumps(String)
	// Tells the lumps to save their data into the specified path.
	public void saveLumps(String path) {
		System.out.println("Saving entities...");
		myL0.save(path);
		System.out.println("Saving planes...");
		myL1.save(path);
		System.out.println("Saving textures...");
		myL2.save(path);
		System.out.println("Saving materials...");
		myL3.save(path);
		System.out.println("Saving vertices...");
		myL4.save(path);
		System.out.println("Saving normals...");
		myL5.save(path);
		System.out.println("Saving meshes...");
		myL6.save(path);
	}
	
	// ACCESSORS/MUTATORS
	public Lump00 getLump00() {
		return myL0;
	}
	
	public Lump01 getLump01() {
		return myL1;
	}
	
	public Lump02 getLump02() {
		return myL2;
	}
	
	public Lump03 getLump03() {
		return myL3;
	}
	
	public Lump04 getLump04() {
		return myL4;
	}
	
	public Lump05 getLump05() {
		return myL5;
	}
	
	public Lump06 getLump06() {
		return myL6;
	}
	
	public Lump07 getLump07() {
		return myL7;
	}
	
	public Lump08 getLump08() {
		return myL8;
	}
}