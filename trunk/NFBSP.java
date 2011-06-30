// NFBSP class
//
// This class holds all references to the lump data structures  and
// contains the higher-level methods for manipulating these lumps,
// minimizing the amount of work needed to manually mod a map. Lower-level
// methods for manipulating the data (for example, actually CHANGING and
// accessing the data) are handled in the Lump## classes as accessors and
// mutators. This class retrieves information from the Lump classes to
// manipulate the map as a whole while keeping the lumps separate.
//
// The data for a specific lump is contained in its own Lump class, which
// is specific to that lump. This class is for manipulating and searching
// for data, while the Lump class merely holds, finds and replaces data.
//
// The data is stored in the exact same order in which is was read, keeping
// all the indices from one lump into another exactly the same (unless 
// something was edited). This means that a map opened then directly saved
// from memory will be a 1:1 match with the original.

// TODO:
// create methods for searching and editing the lumps

// The strange thing about the NightFire (and the other) BSP files is the
// virtual maze of data references. Leaves are born from Nodes created using
// planes. Leaves then (indirectly) reference faces and brushes, which in turn
// reference planes again (brushes through brush sides). The only explanation
// I can come up with is faces and everything faces do handle how the map 
// looks visually, while brushes define the map physically. These things could
// have been handled as one thing, but they weren't for whatever reason.
// I'd imagine planes and BSPtreeing are better for collision detection, while
// vertices and triangles are best for rendering.

// Perhaps an even stranger thing is how Nodes have bounding boxes and define
// leaves, which have their own bounding boxes, then models use several leaves
// and have THEIR own bounding boxes. How do these relate and interact, if at all?

import java.io.File;

public class NFBSP {

	// INITIAL DATA DECLARATION AND DEFINITION OF CONSTANTS
	
	Runtime r = Runtime.getRuntime(); // Get a runtime object. This is for calling
	                                  // Java's garbage collector and does not need
												 // to be ported. I try not to leave memory leaks
												 // but since Java has no way explicitly reallocate
												 // unused memory I have to tell it when a good
												 // time is to run the garbage collector, by
												 // calling gc(). Also, it is used to execute EXEs
												 // from within the program by calling .exec(path).

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
	public static final String[] LUMPNAMES = {"Entities", "Planes", "Textures", "Materials", "Vertices", "Normals", "Indices", "Visibility", "Nodes", "Faces",
	                             "Lighting", "Leaves", "Mark Surfaces", "Mark Brushes", "Models", "Brushes", "Brushsides", "Texmatrix"};
		
	// This holds the size of the data structures for each lump. If
	// the lump does not have a set size, the size is -1. If the lump's
	// size has not been determined yet, the size is 0.
	// The entities lump does not have a set data length per entity
	// Since there is literally no data in lump05, there is no data structure
	// Visibility varies for every map, and will be determined by constructor
	public int[] lumpDataSizes = {-1, 20, 64, 64, 12, -1, 4, 0, 36, 48, 3, 48, 4, 4, 56, 12, 8, 32};
	
	private boolean modified=false; // Every method in this class that modifies the data in RAM
	                                // will set this to true. If something else sets up an NFBSP
	                                // object then modifies the lumps directly through accessors
	                                // then this boolean will not change.
	
	// CONSTRUCTORS
	// This accepts a file path and parses it into the form needed. If the file is empty (or not found)
	// the program fails nicely.
	public NFBSP(String in) {
		try {
			if (in.substring(in.length()-4).equalsIgnoreCase(".BSP")) {
				filepath=in.substring(0,in.length()-4)+"\\";
				LS ls=new LS(in);
				ls.separateLumps();
			} else {
				if (in.charAt(in.length()-1) != '\\' && in.charAt(in.length()-1) != '/') {
					in+="\\"; // Add a '\' character to the end of the path if it isn't already there
				}
				filepath=in;
			}
			
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
		}
	}
	
	// METHODS
	
	// +optimizeBSP()
	// This method will search through the entire BSP for duplicate
	// data structures, and attempt to recycle them, making the map
	// smaller.
	//
	// One limitation of this: it's very hard to recycle data structures
	// referenced by one index then an amount of items (for example,
	// brush sides or faces). I could see it happening for meshes, but
	// it would be very complicated to do, moreso than it already is.
	// As it is now, it's already complex enough to have each lump
	// have its own optimization method.
	//
	// This could also lead to complications if further editing is done
	// in the future. For this reason, this should only be used on BSPs
	// being finalized or almost ready for release, or for combined maps.
	public void optimizeBSP() {
		// One lump at a time, in order of lumps. If a lump cannot be
		// optimized, and explanation is provided.
		
		System.out.println("\nOptimizing entities...");
		optimizeEntities(); // Every entitiy is unique and cannot be optimized. However, some
		                    // attributes are superfluous ("angles" "0 0 0") and some are
								  // only used by Gearcraft ("sequencename", oven compile options).
								  // These can be safely deleted with no ingame consequences.
		System.out.println("Optimizing planes...");
		optimizePlanes();
		System.out.println("Optimizing textures...");
		optimizeTextures(); // A compiler will never allow duplicates in this one,
		                    // so it's mostly for when something has been added
		System.out.println("Optimizing materials...");
		optimizeMaterials();
		// Vertices are referenced in index/item pairs, with insufficient redundancy
		// Normals cannot be recycled as the lump must be the same size as vertices
		System.out.println("Optimizing meshes...");
		//optimizeMeshes();
		alternativeOptimizeMeshes();
		System.out.println("Optimizing visibilities...");
		optimizeVisibility(); // Visibility is a pile of garbage. But it can be recycled.
		// Nodes are always unique.
		// Faces are referenced many times, and once in an index/items pair. Don't try this.
		// There may be enough redundancy in the lighting structure, but since I don't know
			// how to determine how many pixels are loaded by a face, I've hit a brick wall.
		// Leaves should always be unique.
		// Leaf faces are referenced by index/items, which will be unique.
		// Same with leaf brushes.
		// Models, don't make me laugh.
		// Brushes may be possible, but are probably all unique.
		// Brush sides probably need to reference a specific face.
		//System.out.println("Optimizing texture scaling matrix...");
		//optimizeTexMatrix(); // There is some redundancy here
		
		r.gc(); // Collect garbage, there may be a lot of it
	}
	
	// optimizeEntities()
	// Eliminates junk attributes like "angles" "0 0 0" and those which are only used by the map
	// editor, such as sequencename and oven compile options.
	public void optimizeEntities() {
		int numEnts=myL0.getNumElements();
		for(int i=0;i<numEnts;i++) {
			if(myL0.getEntity(i).getAttribute("origin").equals("0 0 0")) {
				myL0.getEntity(i).deleteAttribute("origin");
			}
			if(myL0.getEntity(i).getAttribute("angles").equals("0 0 0")) {
				myL0.getEntity(i).deleteAttribute("angles");
			}
			if(myL0.getEntity(i).getAttribute("classname").equals("multi_manager")) {
				myL0.getEntity(i).deleteAttribute("origin"); // Especially after compile, multi_managers no longer need to be within a map.
			}
			if(myL0.getEntity(i).getAttribute("classname").equals("env_fade")) {
				myL0.getEntity(i).deleteAttribute("origin"); // Especially after compile, env_fades no longer need to be within a map.
			}
			if(myL0.getEntity(i).getAttribute("classname").equals("multisource")) {
				myL0.getEntity(i).deleteAttribute("origin"); // Especially after compile, multisources no longer need to be within a map.
			}
			myL0.getEntity(i).deleteAttribute("sequencename");
		}
		// myL0.deleteAllWithAttribute("classname", "useless");
	}
	
	// optimizePlanes()
	// Finds duplicate planes, and eliminates them and attempts to recycle
	// them instead. This is complicated by the fact that planes are referenced
	// by several different lumps each for their own purposes. However, it is
	// for that same reason planes are probably ripe for optimization.
	// Iterators:
	// i is the current plane
	//  j is the plane the current plane is being compared to
	//   k is the current index into another lump to check if it references the deleted plane (which "other" lump differs in the loops)
	public void optimizePlanes() {
		int numDeldPlanes=0;
		int numPlns=myL1.getNumElements();
		for(int i=0;i<numPlns;i++) { // for each plane
			for(int j=i+1;j<numPlns;j++) { // for each plane after plane i
				if(myL1.getPlane(i).equals(myL1.getPlane(j))) { // If planes i and j are the same
					myL1.delete(j); // delete plane j from the list
					numDeldPlanes++;
					numPlns--;
					// Now all references to planes must be corrected to account for changed indices
					for(int k=0;k<myL8.getNumElements();k++) {
						if(myL8.getNode(k).getPlane()>j) { // if the reference is greater than that of the deleted plane
							myL8.getNode(k).setPlane(myL8.getNode(k).getPlane()-1); // subtract one from it
						} else { // otherwise
							if(myL8.getNode(k).getPlane()==j) { // if the reference IS the deleted plane
								myL8.getNode(k).setPlane(i); // Set it to the first plane instead, which is equal
							}
						}
					}
					for(int k=0;k<myL9.getNumElements();k++) {
						if(myL9.getFace(k).getPlane()>j) { // if the reference is greater than that of the deleted plane
							myL9.getFace(k).setPlane(myL9.getFace(k).getPlane()-1); // subtract one from it
						} else { // otherwise
							if(myL9.getFace(k).getPlane()==j) { // if the reference IS the deleted plane
								myL9.getFace(k).setPlane(i); // Set it to the first plane instead, which is equal
							}
						}
					}
					for(int k=0;k<myL16.getNumElements();k++) {
						if(myL16.getBrushSide(k).getPlane()>j) { // if the reference is greater than that of the deleted plane
							myL16.getBrushSide(k).setPlane(myL16.getBrushSide(k).getPlane()-1); // subtract one from it
						} else { // otherwise
							if(myL16.getBrushSide(k).getPlane()==j) { // if the reference IS the deleted plane
								myL16.getBrushSide(k).setPlane(i); // Set it to the first plane instead, which is equal
							}
						}
					}
					j--; // since element j+1 is now element j, we must check j again
				} // check if they are equal
			} // for each subsequent plane
		} // for each plane
		System.out.println(numDeldPlanes + " duplicate planes deleted.");
	} // optimizePlanes()
	
	// optimizeTextures()
	// Finds duplicate textures and fixes references to them in faces. Quite
	// easy to do since only one lump references this one.
	// Iterators:
	// i: index of the first texture
	//  j: index of the second texture
	//   k: when textures i and j are the same, this is the current face
	public void optimizeTextures() {
		int numDeldTxts=0;
		int numTxts=myL2.getNumElements();
		for(int i=0;i<numTxts-1;i++) {
			String firstTxt=myL2.getTexture(i);
			for(int j=i+1;j<numTxts;j++) {
				String secondTxt=myL2.getTexture(j);
				if(firstTxt.equalsIgnoreCase(secondTxt)) { // if a duplicate is found
					for(int k=0;k<myL9.getNumElements();k++) {
						if(myL9.getFace(k).getTexture()==j) { // Find any faces that reference texture j
							myL9.getFace(k).setTexture(i); // and instead reference texture i
						}
					}
					myL2.delete(j); // delete duplicate
					numDeldTxts++;
					numTxts--; // The array has gotten smaller
					j--; // since element j+1 is now element j, we must check j again
				}
			}
		}
		System.out.println(numDeldTxts+" duplicate textures deleted.");
	}
	
	// optimizeMaterials()
	// Finds duplicate materials and fixes references to them in faces.
	// Iterators:
	// i: index of the first material
	//  j: index of the second material
	//   k: when materials i and j are the same, this is the current face
	public void optimizeMaterials() {
		int numDeldMats=0;
		int numMats=myL3.getNumElements();
		for(int i=0;i<numMats-1;i++) {
			String firstMat=myL3.getMaterial(i);
			for(int j=i+1;j<numMats;j++) {
				String secondMat=myL3.getMaterial(j);
				if(firstMat.equalsIgnoreCase(secondMat)) { // if a duplicate is found
					for(int k=0;k<myL9.getNumElements();k++) {
						if(myL9.getFace(k).getTexture()==j) { // Find any faces that reference texture j
							myL9.getFace(k).setTexture(i); // and instead reference texture i
						}
					}
					myL3.delete(j); // delete duplicate
					numDeldMats++;
					numMats--; // The array has gotten smaller
					j--; // since element j+1 is now element j, we must check j again
				}
			}
		}
		System.out.println(numDeldMats+" duplicate materials deleted.");
	}
	
	// optimizeMeshes()
	// In many faces, meshes are referenced six at a time, and probably 100% of the time
	// these six meshes are 0, 1, 2, 0, 2, 3. These are indices to a few specific vertices,
	// but they are the same nonetheless (faces specify which specific vertices). The way to
	// check if these structures are duplicates is to check each face against all other
	// faces, and if they both reference the same number of meshes, check if the meshes are
	// equivalent. If they are, delete all data from the duplicate set and reference the
	// first instance twice.
	// I don't see why map compilers don't automatically do this. There's a lot of duplicated
	// mesh bunches that could be optimized out, in both official and custom compiled maps.
	// Only reason I can think of is this makes it unreliable, but I can't imagine how.
	//
	// This is already one of the most complicated and messy things I've ever coded, but it
	// actually WORKS. This process shrunk the meshes lump of airfield01 from 502,524 bytes
	// to 708 bytes. Upon loading the map in the game engine I didn't see any apparent problems. Also...
	// TODO: THIS PROCESS IS ACTUALLY IMCOMPLETE. It doesn't cover cases where one face has
	// for example three meshes (0, 1, 2) and another one has six (0, 1, 2, 0, 2, 3). The
	// 0, 1, 2 can be recycled and used for both cases. I'm not sure how to finish the algorithm
	// to take advantage of this though... I'll have to think about this one.
	//
	// I can't believe this isn't part of the regular compilation process. This is a ridiculous
	// amount of rundant data which can be recycled. If I completed this algorithm I could probably
	// get a lump 1000 times smaller than the original in most cases. The best explanation I can
	// come up with is the compiler wasn't complete due to NightFire's obvious rush release.
	//
	// Iterators:
	// i: Current face
	//  j: Face being compared to current face
	//   k: Current mesh in a bunch to check against the same mesh in another bunch
	//   l: When a bunch of meshes are deleted, each face must be checked to make sure its references are fixed. This is the current face for fixing references.
	public void optimizeMeshes() {
		int numFaces=myL9.getNumElements();
		int numDeldMeshs=0;
		// Phase 1: gets rid of the same data stored at different places
		for(int i=0;i<numFaces;i++) { // for each face
			for(int j=i+1;j<numFaces;j++) { // for each subsequent face
				// If both faces i and j reference the same number of meshes, and that number isn't 0, and they don't reference the SAME meshes
				if(myL9.getFace(i).getNumMeshs()!=0 && myL9.getFace(i).getMeshs()!=myL9.getFace(j).getMeshs() && myL9.getFace(i).getNumMeshs()==myL9.getFace(j).getNumMeshs()) {
					int numMeshesInBunch=myL9.getFace(i).getNumMeshs(); // How big the "bunch" is
					boolean isDuplicate=true; // Assume the bunches are the same until it is proven they are not
					for(int k=0;k<numMeshesInBunch && isDuplicate;k++) {
						// if the Kth mesh in both bunches are not the same (they are not duplicates)
						if(myL6.getMesh(myL9.getFace(i).getMeshs()+k)!=myL6.getMesh(myL9.getFace(j).getMeshs()+k)) {
							isDuplicate=false;
							break; // Exit the for loop immediately, there's no point in checking the rest
						}
					}
					if(isDuplicate) { // If the bunch of meshes is never proven not a duplicate
						numDeldMeshs+=numMeshesInBunch;
						myL6.delete(myL9.getFace(j).getMeshs(), numMeshesInBunch); // Delete the duplicate bunch
						for(int l=0;l<myL9.getNumElements();l++) { // in each face
							if(myL9.getFace(l).getMeshs() > myL9.getFace(j).getMeshs()) { // if the index of this face's meshes are after the deleted messages
								myL9.getFace(l).setMeshs(myL9.getFace(l).getMeshs()-numMeshesInBunch); // subtract the amount of deleted meshes from the index
							} else {
								if(myL9.getFace(l).getMeshs() == myL9.getFace(j).getMeshs()) { // if this face's meshes was the deleted meshes
									myL9.getFace(l).setMeshs(myL9.getFace(i).getMeshs()); // Set the mesh reference to the first instance
								}
							}
						} // Face readjuster
					} // If they were the same
				} // If mesh bunches were the same size
			} // For each subsequent face
		} // For each face
		
		// Phase 2: Find mesh bunches which are pieces of larger bunches and take advantage of that.
		// Really, this phase is for squeezing a couple hundred more bytes out of the lump. Phase 1
		// is good enough to shrink a 500KB file to a file maybe 1KB, this just brings that 1KB down
		// to maybe 200 bytes or less.
		
		// First, I need to figure out how many unique mesh bunches there are. The references in faces
		// have already been saved. Find out how many of those references are unique.
		
		// TODO: Keep thinking about this one, it's a tough cookie.
		
		System.out.println(numDeldMeshs+" duplicate meshes deleted.");
	}
	
	// alternativeOptimizeMeshes()
	// This is an alternative method to optimizing meshes. It's a very lazy way to
	// do this and could be very error-prone in certain cases, but from what I've
	// seen this should work for every compiled map and it's extremely fast.
	// This assumes every single mesh bunch follows the pattern:
	// (0, 1, 2, 0, 2, 3, 0, 3, 4, 0, 4, 5, etc.)
	// which from what I've seen, they all do, or some smaller subset of it, like:
	// (0, 1, 2) or (0, 1, 2, 0, 2, 3).
	// If I take advantage of this fact, I can have a single bunch of meshes and
	// simply reference pieces of it. This is what I would hope to do with the other
	// optimizeMeshes() method but the process is very complicated unless I cut corners
	// like I have in this method. I hope to finish the other method later on.
	//
	// After testing this seems to work fine, even on large maps with complex geometries.
	// It's a quick and dirty method but it's a sloppy lump to work on. I'd be more comfortable
	// using the slow, safe method. This method makes a lot of assumptions about the data
	// that I'd rather not make, even though all those assumptions usually seem to be correct.
	// Depending on data this heavily is a bad idea, if one little thing is different then
	// lots of glitches may occur in game.
	public void alternativeOptimizeMeshes() {
		int highestValue=2; // Assumption: The highest number will always be at least 2
		for(int i=0;i<myL6.getNumElements()/3;i++) { // for each set of three ints in the lump
			if(myL6.getMesh((i*3)+2)>highestValue) { // Assumption: Go through each third one because it'll be higher than the previous two
				highestValue=myL6.getMesh((i*3)+2); // and find and record the highest value
			}
		}
		int[] newLump=new int[(highestValue-1)*3]; // Assumption: If the highest value is N, there will be N-1 sets of 3 ints.
		for(int i=0;i<newLump.length/3;i++) { // For each set of three ints
			newLump[i*3]=0; // Assumption: The first value in each set of three is always 0
			newLump[(i*3)+1]=i+1; // Assumption: The second value always reflects the current set of three (starting at set 1, not 0)
			newLump[(i*3)+2]=i+2; // Assumption: The third value always reflects the current set of three, plus one
		}
		myL6.setMeshes(newLump);
		for(int i=0;i<myL9.getNumElements();i++) { // For each face
			myL9.getFace(i).setMeshs(0); // Assumption: The data for every face will always be at index 0 and will always be correct, and each face will reference as much of it as it needs.
		}
		System.out.println("Meshes optimized to "+newLump.length*4+" bytes.");
	}
	
	// optimizeVisibility()
	// Finds duplicate Potentially Visible Sets and deletes the second one
	public void optimizeVisibility() {
		int numPVS=myL7.getNumElements();
		if(numPVS>1) { // Make sure visibility data exists, if it doesn't then either it wasn't compiled into the map or it was deleted.
			int lengthOfPVS=myL7.getLengthOfData();
			int numDeldPVS=0;
			for(int i=0;i<numPVS;i++) { // For each PVS
				for(int j=i+1;j<numPVS;j++) { // And each subsequent PVS
					if(myL7.equals(i, myL7.getPVS(j))) { // if the PVSes are the same
						myL7.delete(j); // Delete the duplicate PVS
						numDeldPVS++;
						numPVS--;
						for(int k=0;k<myL11.getNumElements();k++) {
							if(myL11.getLeaf(k).getPVS()>j*lengthOfPVS) {
								myL11.getLeaf(k).setPVS(myL11.getLeaf(k).getPVS()-lengthOfPVS);
							} else {
								if(myL11.getLeaf(k).getPVS()==j*lengthOfPVS) {
									myL11.getLeaf(k).setPVS(i*lengthOfPVS);
								}
							}
						}
						j--; // since element j+1 is now element j, we must check j again
					}
				}
			}
			System.out.println(numDeldPVS+" duplicate Potentially Visible Sets deleted.");
		} else {
			System.out.println("Visibility data doesn't exist. This is probably NOT a problem.");
		}
	}
	
	// optimizeTexMatrix()
	// Finds any texture matrixes that are the same data, deletes all except one and
	// only use that one.
	// TODO: This causes an "Alloclightmap: full" error. Is there something wrong, or can
	// this simply not be recycled?
	public void optimizeTexMatrix() {
		int numDeldMatxs=0;
		int numMatxs=myL17.getNumElements();
		for(int i=0;i<numMatxs;i++) { // for each matrix
			for(int j=i+1;j<numMatxs;j++) { // for each matrix after matrix i
				if(myL17.getTexMatrix(i).equals(myL17.getTexMatrix(j))) { // If matrices i and j are the same
					myL17.delete(j); // delete matrix j from the list
					numDeldMatxs++;
					numMatxs--;
					for(int k=0;k<myL9.getNumElements();k++) {
						if(myL9.getFace(k).getTexStyle()>j) { // if the reference is greater than that of the deleted matrix
							myL9.getFace(k).setTexStyle(myL9.getFace(k).getTexStyle()-1); // subtract one from it
						} else { // otherwise
							if(myL9.getFace(k).getTexStyle()==j) { // if the reference IS the deleted matrix
								myL9.getFace(k).setTexStyle(i); // Set it to the i'th matrix instead, which is equal
							}
						}
					}
					j--; // since element j+1 is now element j, we must check j again
				} // check if they are equal
			} // for each subsequent matrix
		} // for each matrix
		System.out.println(numDeldMatxs + " duplicate texture scale matrices deleted.\n");
	}
	
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
	//
	// Whether this works or not is very iffy, especially since Nodes
	// require the data they reference to be on one side of a plane or
	// another. If nodes define what is on either side of a plane, then
	// why is the SAME list of planes referenced by three other unique
	// lumps with their own data structures? We need the source code to
	// this ridiculous engine, or at least a reliable reverse engineering.
	//
	// Visibility is another wildcard. How do you combine the visibility
	// data for two maps, when the amount of bits in a visibility structure
	// doesn't match the number of leaves in the map? This program is not
	// a map compiler, so I doubt it's possible to reliably create a working
	// visibility lump. Though I'd rather not do this, I'm going to have to
	// set every reference to visibility to -1 (0xFFFFFFFF) so it was
	// as if the map hadn't had visibility compiled in the first place.
	// Running a visibility algorithm is a long process that I have no idea
	// how to do, and BVIS can't do it without portal files created by BBSP.
	//
	// One possible solution is to decompile and recompile the map after this
	// process is run, but (for now) that is beyond the scope of this program.
	//
	// TODO: Finish moving the add(Lump) methods into this class. It simplifies
	// things a lot. Just make them return a new lump## object.
	// TODO: Figure out why lighting gets messed up in the process
	// TODO: Figure out what's broken. The results of this will only load in a dedicated console.
	
	// I need these here so I don't have to pass all this crap from one method to another.
	private int numWorldFaces;
	private int numModelFaces;
	private int numWorldFacesOther;
	private int numModelFacesOther;
	
	private int numWorldLeaves;
	private int numModelLeaves;
	private int numWorldLeavesOther;
	private int numModelLeavesOther;
	
	public void combineBSP(NFBSP other) {
		try {
			numWorldFaces=myL14.getModel(0).getNumFaces();
			numModelFaces=myL9.getNumElements()-numWorldFaces;
			numWorldFacesOther=other.getLump14().getModel(0).getNumFaces();
			numModelFacesOther=other.getLump09().getNumElements()-numWorldFacesOther;
			
			numWorldLeaves=myL14.getModel(0).getNumLeafs();
			numModelLeaves=myL11.getNumElements()-numWorldLeaves-1;
			numWorldLeavesOther=other.getLump14().getModel(0).getNumLeafs();
			numModelLeavesOther=other.getLump11().getNumElements()-numWorldLeavesOther-1;
			
			System.out.println(numWorldFaces+" world faces in map");
			System.out.println(numModelFaces+" model faces in map");
			System.out.println(numWorldFacesOther+" world faces in other map");
			System.out.println(numModelFacesOther+" model faces in other map");
			System.out.println(numWorldLeaves+" world leaves in map");
			System.out.println(numModelLeaves+" model leaves in map");
			System.out.println(numWorldLeavesOther+" world leaves in other map");
			System.out.println(numModelLeavesOther+" model leaves in other map");
			
			// By creating new lump objects instead of rewriting them as I go,
			// I don't need to worry about one combining method overwriting
			// data that another combining method needs. As a bonus, this also
			// makes it thread safe, if I ever figure out how to multithread 
			// Java. Dammit, Java sucks.
			Lump00 newL0;
			Lump01 newL1;
			Lump02 newL2;
			Lump03 newL3;
			Lump04 newL4;
			Lump05 newL5;
			Lump06 newL6;
			Lump07 newL7;
			Lump08 newL8;
			Lump09 newL9;
			Lump10 newL10;
			Lump11 newL11;
			Lump12 newL12;
			Lump13 newL13;
			Lump14 newL14;
			Lump15 newL15;
			Lump16 newL16;
			Lump17 newL17;

			myL0.add(other.getLump00());
			myL1.add(other.getLump01());
			myL2.add(other.getLump02());
			myL3.add(other.getLump03());
			myL4.add(other.getLump04());
			myL5.add(other.getLump05());
			myL6.add(other.getLump06());
			
			byte[][] newVis=new byte[0][0]; // one practical application of arrays
			                                // with length 0
			myL7.setPVSes(newVis);
			
			newL8=combineNodes(other.getLump08());
			newL9=combineFaces(other.getLump09());
			
			myL10.add(other.getLump10());
			// myL10.delAllPixels();
			
			newL11=combineLeaves(other.getLump11());
			newL12=combineMarkSurfaces(other.getLump12());
			
			myL13.add(other.getLump13());
			
			newL14=combineModels(other.getLump14());
			
			myL15.add(other.getLump15());

			newL16=combineBrushSides(other.getLump16());
			
			myL17.add(other.getLump17());
			
			//myL0=newL0;
			//myL1=newL1;
			//myL2=newL2;
			//myL3=newL3;
			//myL4=newL4;
			//myL5=newL5;
			//myL6=newL6;
			//myL7=newL7;
			myL8=newL8;
			myL9=newL9;
			//myL10=newL10;
			myL11=newL11;
			myL12=newL12;
			//myL13=newL13;
			myL14=newL14;
			//myL15=newL15;
			myL16=newL16;
			//myL17=newL17;
			
			modified=true;
		} catch(java.io.FileNotFoundException e) {
			System.out.println("Cannot find second BSP's files!");
		} catch(java.io.IOException e) {
			System.out.println("Unknown error adding BSP files!");
		}
	}
	
	// combineNodes(Lump08)
	// Combines two Lump08s.
	// TODO: Find a way to ensure the game engine actually loads the added lump data.
	// As it is, I've tried to put all the stuff on one side of a plane that's really
	// far out of the world, but I doubt this works.
	public Lump08 combineNodes(Lump08 in) {
		Node[] newList=new Node[myL8.getNumElements()+in.getNumElements()+3]; // Need three new nodes. First
		                                                                      // is the new root, second and
																					             // third reference the first and
		                                                                      // second maps' roots, respectively.
		                                                                      // It must be done this way to make
		                                                                      // sure everything is on the proper
		                                                                      // side of a plane, and it all
		                                                                      // shows up.
		Plane dummyPlane=new Plane(0, 0, 1, -16384, 3);
		myL1.add(dummyPlane);
		Node newRoot=new Node(myL1.getNumElements(), 1, 2, -16384, -16384, -16384, 16384, 16384, 16384);
		Node refMap1=new Node(myL1.getNumElements(), 3, -1, -16384, -16384, -16384, 16384, 16384, 16384);
		Node refMap2=new Node(myL1.getNumElements(), myL8.getNumElements()+3, -1, -16384, -16384, -16384, 16384, 16384, 16384);
		newList[0]=newRoot;
		newList[1]=refMap1;
		newList[2]=refMap2;
		for(int i=0;i<myL8.getNumElements();i++) { // For each node in THIS NFBSP object
			newList[i+3]=myL8.getNode(i);
			if(newList[i+3].getChild1()>=0) { // Child1 is a Node
				newList[i+3].setChild1(newList[i+3].getChild1()+3);
			} else { // Child1 is a leaf
				if(newList[i+3].getChild1()*-1>numWorldLeaves) { // Leaf is a model leaf
					newList[i+3].setChild1(newList[i+3].getChild1()+numWorldLeavesOther);
				}
			}
			if(newList[i+3].getChild2()>=0) { // Child2 is a Node
				newList[i+3].setChild2(newList[i+3].getChild2()+3);
			} else { // Child2 is a leaf
				if(newList[i+3].getChild2()*-1>numWorldLeaves) { // Leaf is a model leaf
					newList[i+3].setChild2(newList[i+3].getChild2()+numWorldLeavesOther);
				}
			}
		}
		for(int i=0;i<in.getNumElements();i++) { // For each node in the OTHER NFBSP object
			newList[i+myL8.getNumElements()+3]=in.getNode(i);
			if(newList[i+myL8.getNumElements()+3].getChild1()<0) { // Child1 is a Leaf
				// leaf indices are negative, so subtract the size of the old leaf list from the new index
				if(newList[i+myL8.getNumElements()+3].getChild1()*-1<numWorldLeavesOther) { // Leaf is a world leaf
					newList[i+myL8.getNumElements()+3].setChild1(newList[i+myL8.getNumElements()+3].getChild1()-numWorldLeaves);
				} else { // Leaf is a model leaf
					newList[i+myL8.getNumElements()+3].setChild1(newList[i+myL8.getNumElements()+3].getChild1()-myL11.getNumElements());
				}
			} else { // child1 will be a Node
				newList[i+myL8.getNumElements()+3].setChild1(newList[i+myL8.getNumElements()+3].getChild1()+myL8.getNumElements()+3);
			}
			if(newList[i+myL8.getNumElements()+3].getChild2()<0) { // Child2 is a Leaf
				if(newList[i+myL8.getNumElements()+3].getChild2()*-1<numWorldLeavesOther) { // Leaf is a world leaf
					newList[i+myL8.getNumElements()+3].setChild2(newList[i+myL8.getNumElements()+3].getChild2()-numWorldLeaves);
				} else { // Leaf is a model leaf
					newList[i+myL8.getNumElements()+3].setChild2(newList[i+myL8.getNumElements()+3].getChild2()-myL11.getNumElements());
				}
			} else { // child2 will be a Node
				newList[i+myL8.getNumElements()+3].setChild2(newList[i+myL8.getNumElements()+3].getChild2()+myL8.getNumElements()+3);
			}
		}
		return new Lump08(newList);
	}
	
	// combineFaces(Lump09)
	// Adds every face in another Lump09 object.
	public Lump09 combineFaces(Lump09 in) {
		Face[] newList=new Face[myL9.getNumElements()+in.getNumElements()];
		// Copy world faces first
		for(int i=0;i<numWorldFaces;i++) {
			newList[i]=myL9.getFace(i);
		}
		for(int i=0;i<numWorldFacesOther;i++) {
			newList[i+numWorldFaces]=in.getFace(i);
		}
		// Now copy model faces
		for(int i=0;i<numModelFaces;i++) {
			newList[i+numWorldFaces+numWorldFacesOther]=myL9.getFace(i+numWorldFaces);
		}
		for(int i=0;i<numModelFacesOther;i++) {
			newList[i+numWorldFaces+numWorldFacesOther+numModelFaces]=in.getFace(i+numWorldFacesOther);
		}
		
		// Correct references into other lumps, by added world faces
		for(int i=0;i<numWorldFacesOther;i++) {
			newList[i+numWorldFaces].setPlane(newList[i+numWorldFaces].getPlane()+myL1.getNumElements());
			newList[i+numWorldFaces].setVert(newList[i+numWorldFaces].getVert()+myL4.getNumElements());
			newList[i+numWorldFaces].setMeshs(newList[i+numWorldFaces].getMeshs()+myL6.getNumElements());
			newList[i+numWorldFaces].setTexture(newList[i+numWorldFaces].getTexture()+myL2.getNumElements());
			newList[i+numWorldFaces].setMaterial(newList[i+numWorldFaces].getMaterial()+myL3.getNumElements());
			newList[i+numWorldFaces].setTexStyle(newList[i+numWorldFaces].getTexStyle()+myL17.getNumElements());
			newList[i+numWorldFaces].setLgtMaps(newList[i+numWorldFaces].getLgtMaps()+myL10.getNumElements());
		}
		// Correct references into other lumps, by added model faces
		for(int i=0;i<numModelFacesOther;i++) {
			newList[i+myL9.getNumElements()+numWorldFacesOther].setPlane(newList[i+myL9.getNumElements()+numWorldFacesOther].getPlane()+myL1.getNumElements());
			newList[i+myL9.getNumElements()+numWorldFacesOther].setVert(newList[i+myL9.getNumElements()+numWorldFacesOther].getVert()+myL4.getNumElements());
			newList[i+myL9.getNumElements()+numWorldFacesOther].setMeshs(newList[i+myL9.getNumElements()+numWorldFacesOther].getMeshs()+myL6.getNumElements());
			newList[i+myL9.getNumElements()+numWorldFacesOther].setTexture(newList[i+myL9.getNumElements()+numWorldFacesOther].getTexture()+myL2.getNumElements());
			newList[i+myL9.getNumElements()+numWorldFacesOther].setMaterial(newList[i+myL9.getNumElements()+numWorldFacesOther].getMaterial()+myL3.getNumElements());
			newList[i+myL9.getNumElements()+numWorldFacesOther].setTexStyle(newList[i+myL9.getNumElements()+numWorldFacesOther].getTexStyle()+myL17.getNumElements());
			newList[i+myL9.getNumElements()+numWorldFacesOther].setLgtMaps(newList[i+myL9.getNumElements()+numWorldFacesOther].getLgtMaps()+myL10.getNumElements());
		}
		return new Lump09(newList, numWorldFaces+numWorldFacesOther, numModelFaces+numModelFacesOther);
	}
	
	// combineLeaves(Lump11)
	// Adds every leaf in another Lump11 object.
	// TODO: Make sure all the indices are right, that dummy leaf really throws things off.
	// I think this is right though.
	public Lump11 combineLeaves(Lump11 in) {
		Leaf[] newList=new Leaf[myL11.getNumElements()+in.getNumElements()-1];
		// First copy world leaves, plus the dummy leaf 0
		for(int i=0;i<numWorldLeaves+1;i++) {
			newList[i]=myL11.getLeaf(i);
			newList[i].setPVS(-1);
			// Visibility is impossible to determine
		}
		for(int i=0;i<numWorldLeavesOther;i++) { // These +1s are not errors. I'm intentionally skipping the 0th leaf since it's a dummy leaf
			newList[i+numWorldLeaves+1]=in.getLeaf(i+1);
			newList[i+numWorldLeaves+1].setPVS(-1);
		}
		// Then copy model leaves
		for(int i=0;i<numModelLeaves;i++) {
			newList[i+numWorldLeaves+numWorldLeavesOther+1]=myL11.getLeaf(i+numWorldLeaves+1);
			newList[i+numWorldLeaves+numWorldLeavesOther+1].setPVS(-1);
		}
		for(int i=0;i<numModelLeavesOther;i++) {
			newList[i+numWorldLeaves+numWorldLeavesOther+numModelLeaves+1]=in.getLeaf(i+numWorldLeavesOther+1);
			newList[i+numWorldLeaves+numWorldLeavesOther+numModelLeaves+1].setPVS(-1);
		}
		
		// Correct references into other lumps, by added world leaves
		for(int i=0;i<numWorldLeavesOther;i++) {
			newList[i+numWorldLeaves+1].setMarkSurface(newList[i+numWorldLeaves+1].getMarkSurface()+myL12.getNumElements());
			newList[i+numWorldLeaves+1].setMarkBrush(newList[i+numWorldLeaves+1].getMarkBrush()+myL13.getNumElements());
		}
		// Correct references into other lumps, by added model leaves
		for(int i=0;i<numModelLeavesOther;i++) {
			newList[i+numWorldLeaves+numWorldLeavesOther+numModelLeaves+1].setMarkSurface(newList[i+numWorldLeaves+numWorldLeavesOther+numModelLeaves+1].getMarkSurface()+myL12.getNumElements());
			newList[i+numWorldLeaves+numWorldLeavesOther+numModelLeaves+1].setMarkBrush(newList[i+numWorldLeaves+numWorldLeavesOther+numModelLeaves+1].getMarkBrush()+myL13.getNumElements());
		}
		// Make sure the face and brush references stay 0 in all leaves of type 2
		for(int i=0;i<newList.length;i++) {
			if(newList[i].getType()==2) {
				newList[i].setMarkSurface(0);
				newList[i].setMarkBrush(0);
			}
		}
		return new Lump11(newList, numWorldLeaves+numWorldLeavesOther, numModelLeaves+numModelLeavesOther);
	}
	
	// combineMarkSurfaces(Lump12)
	// Adds every mark surface from another lump12 object.
	public Lump12 combineMarkSurfaces(Lump12 in) {
		int[] newList=new int[myL12.getNumElements()+in.getNumElements()];
		// Copy this NFBSP's mark surfaces and correct for the new face organization
		for(int i=0;i<myL12.getNumElements();i++) {
			newList[i]=myL12.getMarkSurface(i);
			if(newList[i]>numWorldFaces) { // If this is a model face, add the number of world faces from the other map
				newList[i]+=numWorldFacesOther;
			}
		}
		for(int i=0;i<in.getNumElements(); i++) {
			newList[i+myL12.getNumElements()]=in.getMarkSurface(i);
			if(newList[i+myL12.getNumElements()]<numWorldFacesOther) { // If this is a world face, add only the number of world faces from the first map
				newList[i+myL12.getNumElements()]+=numWorldFaces;
			} else { // This is a model face. Add the total number of faces from the first map.
				newList[i+myL12.getNumElements()]+=myL9.getNumElements();
			}
		}
		return new Lump12(newList);
	}
	
	// combineModels(Lump14)
	// Adds every model in another Lump14 object, minus the first one. Instead, the mins and
	// maxs from the first one are set to the lower and higher values between the two maps, respectively.
	// This is because the first model is the world, and since two worlds are being combined, their
	// models must be combined as well. The rest of the models are copied with their indices reduced
	// by 1. This is already accounted for in the entities combine method.
	public Lump14 combineModels(Lump14 in) {
		Model[] newList=new Model[myL14.getNumElements()+in.getNumElements()-1];
		for(int i=0;i<myL14.getNumElements();i++) {
			newList[i]=myL14.getModel(i);
		}
		
		// mins/maxs fixing
		if(newList[0].getMinX()>in.getModel(0).getMinX()) {
			newList[0].setMinX(in.getModel(0).getMinX());
		}
		if(newList[0].getMinY()>in.getModel(0).getMinY()) {
			newList[0].setMinY(in.getModel(0).getMinY());
		}
		if(newList[0].getMinZ()>in.getModel(0).getMinZ()) {
			newList[0].setMinZ(in.getModel(0).getMinZ());
		}
		if(newList[0].getMaxX()<in.getModel(0).getMaxX()) {
			newList[0].setMaxX(in.getModel(0).getMaxX());
		}
		if(newList[0].getMaxY()<in.getModel(0).getMaxY()) {
			newList[0].setMaxY(in.getModel(0).getMaxY());
		}
		if(newList[0].getMaxZ()<in.getModel(0).getMaxZ()) {
			newList[0].setMaxZ(in.getModel(0).getMaxZ());
		}
		
		// Leaf/face referencing. This will be all the world leaves and faces.
		newList[0].setLeaf(1);
		newList[0].setNumLeafs(numWorldLeaves+numWorldLeavesOther);
		newList[0].setFace(0);
		newList[0].setNumFaces(numWorldFaces+numWorldFacesOther);
		
		// Everything after this point will use model leaves/faces, since we just dealt with the world
		for(int i=1;i<myL14.getNumElements();i++) { // Start with i=1 since the 0th one is handled above
			newList[i].setLeaf(myL14.getModel(i).getLeaf()+numWorldLeavesOther);
			newList[i].setFace(myL14.getModel(i).getFace()+numWorldFacesOther);
		}
		
		for(int i=1;i<in.getNumElements();i++) { // Start with i=1 since the 0th one is handled above
			newList[i+myL14.getNumElements()-1]=in.getModel(i);
			newList[i+myL14.getNumElements()-1].setLeaf(newList[i+myL14.getNumElements()-1].getLeaf()+numWorldLeaves+numModelLeaves);
			newList[i+myL14.getNumElements()-1].setFace(newList[i+myL14.getNumElements()-1].getFace()+numWorldFaces+numModelFaces);
		}
		return new Lump14(newList);
	}
	
	// combineBrushSides(Lump16)
	// Adds every brush side in another Lump16 object.
	public Lump16 combineBrushSides(Lump16 in) {
		BrushSide[] newList=new BrushSide[myL16.getNumElements()+in.getNumElements()];
		for(int i=0;i<myL16.getNumElements();i++) {
			newList[i]=myL16.getBrushSide(i);
			if(newList[i].getFace()>numWorldFaces) { // If this is a model face
				newList[i].setFace(myL16.getBrushSide(i).getFace()+numWorldFacesOther);
			}
		}
		
		for(int i=0;i<in.getNumElements();i++) {
			newList[i+myL16.getNumElements()]=in.getBrushSide(i);
			if(newList[i+myL16.getNumElements()].getFace()<numWorldFacesOther) { // If this is a world face
				newList[i+myL16.getNumElements()].setFace(newList[i+myL16.getNumElements()].getFace()+numWorldFaces);
			} else { // this is a model face
				newList[i+myL16.getNumElements()].setFace(newList[i+myL16.getNumElements()].getFace()+numWorldFaces+numModelFaces);
			}
			newList[i+myL16.getNumElements()].setPlane(newList[i+myL16.getNumElements()].getPlane()+myL1.getNumElements());
		}
		return new Lump16(newList);
	}
	
	// saveLumps(String)
	// Tells the lumps to save their data into the specified path.
	public void saveLumps(String path) {
		if(modified || true) { // TODO: remove "true"
			boolean recombine=false;
			if(path.substring(path.length()-4).equalsIgnoreCase(".bsp")) {
				recombine=true;
				path=path.substring(0, path.length()-4);
			}
			if(!new File(path).exists()) {
				new File(path).mkdirs();
			}
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
			System.out.println("Saving visibility...");
			myL7.save(path);
			System.out.println("Saving nodes...");
			myL8.save(path);
			System.out.println("Saving faces...");
			myL9.save(path);
			System.out.println("Saving lightmaps...");
			myL10.save(path);
			System.out.println("Saving leaves...");
			myL11.save(path);
			System.out.println("Saving mark surfaces...");
			myL12.save(path);
			System.out.println("Saving mark brushes...");
			myL13.save(path);
			System.out.println("Saving models...");
			myL14.save(path);
			System.out.println("Saving brushes...");
			myL15.save(path);
			System.out.println("Saving brush sides...");
			myL16.save(path);
			System.out.println("Saving texture matrix...");
			myL17.save(path);
			LS ls=new LS(path);
			ls.combineLumps();
		}
	}
	
	// +decompile()
	// Attempts to convert the BSP file back into a .MAP file.
	//
	// TODO: There's one extremely hard problem. Sometimes planes are resused backward. It's nearly
	// impossible to determine which way the plane needs to be flipped. Other than that, this
	// algorithm is 100% finished.
	//
	// This is another one of the most complex things I've ever had to code. I've
	// never nested for loops four deep before.
	// Iterators:
	// i: Current entity in the list
	//  j: Current leaf, referenced in a list by the model referenced by the current entity
	//   k: Current brush, referenced in a list by the current leaf.
	//    l: Current side of the current brush.
	public void decompile(String path) {
		// Begin by copying all the entities into another Lump00 object. This is
		// necessary because if I just modified the current entity list then it
		// could be saved back into the BSP and really mess some stuff up.
		Lump00 mapFile=new Lump00(myL0);
		// Then I need to go through each entity and see if it's brush-based.
		// Worldspawn is brush-based as well as any entity with model *#.
		for(int i=0;i<mapFile.getNumElements();i++) { // For each entity
			int currentModel=-1;
			if(mapFile.getEntity(i).isBrushBased()) {
				currentModel=myL0.getEntity(i).getModelNumber();
			} else {
				if(mapFile.getEntity(i).getAttribute("classname").equalsIgnoreCase("worldspawn")) {
					currentModel=0; // If the entity is worldspawn, we're dealing with model 0, which is the world.
				}
			}
			if(currentModel!=-1) { // If this is still -1 then it's strictly a point-based entity. Move on to the next one.
				double[] origin=mapFile.getEntity(i).getOrigin();
				int[] usedPlanes=new int[myL1.getNumElements()];
				Lump01 newPlanes=new Lump01(myL1);
				int firstLeaf=myL14.getModel(currentModel).getLeaf();
				int numLeaves=myL14.getModel(currentModel).getNumLeafs();
				boolean[] brushesUsed=new boolean[myL15.getNumElements()]; // Keep a list of brushes already in the model, since sometimes the leaves lump references one brush several times
				int numBrshs=0;
				for(int j=0;j<numLeaves;j++) { // For each leaf in the bunch
					int firstBrushIndex=myL11.getLeaf(j+firstLeaf).getMarkBrush();
					int numBrushIndices=myL11.getLeaf(j+firstLeaf).getNumMarkBrushes();
					if(numBrushIndices>0) { // A lot of leaves reference no brushes. If this is one, this iteration of the j loop is finished
						for(int k=0;k<numBrushIndices;k++) { // For each brush referenced
							if(!brushesUsed[myL13.getMarkBrush(firstBrushIndex+k)]) {
								brushesUsed[myL13.getMarkBrush(firstBrushIndex+k)]=true;
								Brush currentBrush=myL15.getBrush(myL13.getMarkBrush(firstBrushIndex+k)); // Get a handle to the brush
								int firstSide=currentBrush.getFirstSide();
								int numSides=currentBrush.getNumSides();
								int numPlaneFacesThisBrsh=0;
								int numVertFacesThisBrsh=0;
								int vertFaceIndex=-1; // Will be the index of a vertex-based face
								boolean[] vertFaces=new boolean[numSides]; // vertFaces[X] will be true if face X was defined by vertices
								MAPBrushSide[] brushSides=new MAPBrushSide[numSides];
								Entity mapBrush=new Entity("{ // Brush "+numBrshs);
								numBrshs++;
								for(int l=0;l<numSides;l++) { // For each side of the brush
									Vertex[] plane=new Vertex[3]; // Three points define a plane. All I have to do is find three points on that plane.
									BrushSide currentSide=myL16.getBrushSide(firstSide+l);
									Face currentFace=myL9.getFace(currentSide.getFace()); // To find those three points, I can use vertices referenced by faces.
									if(!myL2.getTexture(currentFace.getTexture()).equalsIgnoreCase("special/bevel")) { // If this face uses special/bevel, skip the face completely
										int firstVertex=currentFace.getVert();
										int numVertices=currentFace.getNumVerts();
										usedPlanes[currentSide.getPlane()]++;
										Plane currentPlane=newPlanes.getPlane(currentSide.getPlane());
										if(numVertices!=0) { // If the face actually references a set of vertices
											int firstMesh=currentFace.getMeshs(); // I don't need to know how many meshes there are, I'll just use the first three, since
											                                      // they are read in threes by the engine. One could argue these will always be (0, 1, 2)
											                                      // but I don't want to depend on data being something that it could possibly not be.
											plane[0]=myL4.getVertex(firstVertex+myL6.getMesh(firstMesh));
											plane[1]=myL4.getVertex(firstVertex+myL6.getMesh(firstMesh+1));
											plane[2]=myL4.getVertex(firstVertex+myL6.getMesh(firstMesh+2));
											numVertFacesThisBrsh++;
											vertFaces[l]=true;
											vertFaceIndex=l;
										} else { // Fallback to planar decompilation. Since there are no explicitly defined points anymore,
											      // we must find them ourselves using the A, B, C and D values.
											numPlaneFacesThisBrsh++;
											// Figure out if the plane is parallel to two of the axes. If so it can be reproduced easily
											if(currentPlane.getB()==0 && currentPlane.getC()==0) { // parallel to plane YZ
												plane[0]=new Vertex(currentPlane.getDist()/currentPlane.getA(), 0, 0);
												plane[1]=new Vertex(currentPlane.getDist()/currentPlane.getA(), 1, 0);
												plane[2]=new Vertex(currentPlane.getDist()/currentPlane.getA(), 0, 1);
												if(currentPlane.getA()>0) {
													plane=flipPlane(plane);
												}
											} else {
												if(currentPlane.getA()==0 && currentPlane.getC()==0) { // parallel to plane XZ
													plane[0]=new Vertex(0, currentPlane.getDist()/currentPlane.getB(), 0);
													plane[1]=new Vertex(0, currentPlane.getDist()/currentPlane.getB(), 1);
													plane[2]=new Vertex(1, currentPlane.getDist()/currentPlane.getB(), 0);
													if(currentPlane.getB()>0) {
														plane=flipPlane(plane);
													}
												} else {
													if(currentPlane.getA()==0 && currentPlane.getB()==0) { // parallel to plane XY
														plane[0]=new Vertex(0, 0, currentPlane.getDist()/currentPlane.getC());
														plane[1]=new Vertex(1, 0, currentPlane.getDist()/currentPlane.getC());
														plane[2]=new Vertex(0, 1, currentPlane.getDist()/currentPlane.getC());
														if(currentPlane.getC()>0) {
															plane=flipPlane(plane);
														}
													} else { // If you reach this point the plane is not parallel to any two-axis plane.
														if(currentPlane.getA()==0) { // parallel to X axis
															plane[0]=new Vertex(0, currentPlane.getDist()/currentPlane.getB(), 0);
															plane[1]=new Vertex(0, 0, currentPlane.getDist()/currentPlane.getC());
															plane[2]=new Vertex(1, 0, currentPlane.getDist()/currentPlane.getC());
															if(currentPlane.getB()*currentPlane.getC()>0) {
																plane=flipPlane(plane);
															}
														} else {
															if(currentPlane.getB()==0) { // parallel to Y axis
																plane[0]=new Vertex(0, 0, currentPlane.getDist()/currentPlane.getC());
																plane[1]=new Vertex(currentPlane.getDist()/currentPlane.getA(), 0, 0);
																plane[2]=new Vertex(currentPlane.getDist()/currentPlane.getA(), 1, 0);
																if(currentPlane.getA()*currentPlane.getC()>0) {
																	plane=flipPlane(plane);
																}
															} else {
																if(currentPlane.getC()==0) { // parallel to Z axis
																	plane[0]=new Vertex(currentPlane.getDist()/currentPlane.getA(), 0, 0);
																	plane[1]=new Vertex(0, currentPlane.getDist()/currentPlane.getB(), 0);
																	plane[2]=new Vertex(0, currentPlane.getDist()/currentPlane.getB(), 1);
																	if(currentPlane.getA()*currentPlane.getB()>0) {
																		plane=flipPlane(plane);
																	}
																} else { // If you reach this point the plane is not parallel to any axis. Therefore, any two coordinates will give a third.
																	plane[0]=new Vertex(currentPlane.getDist()/currentPlane.getA(), 0, 0);
																	plane[1]=new Vertex(0, currentPlane.getDist()/currentPlane.getB(), 0);
																	plane[2]=new Vertex(0, 0, currentPlane.getDist()/currentPlane.getC());
																	if(currentPlane.getA()*currentPlane.getB()*currentPlane.getC()>0) {
																		plane=flipPlane(plane);
																	}
																}
															}
														}
													}
												}
											}
										} // End plane stuff
										plane[0].setX(plane[0].getX()+(float)origin[0]);
										plane[0].setY(plane[0].getY()+(float)origin[1]);
										plane[0].setZ(plane[0].getZ()+(float)origin[2]);
										plane[1].setX(plane[1].getX()+(float)origin[0]);
										plane[1].setY(plane[1].getY()+(float)origin[1]);
										plane[1].setZ(plane[1].getZ()+(float)origin[2]);
										plane[2].setX(plane[2].getX()+(float)origin[0]);
										plane[2].setY(plane[2].getY()+(float)origin[1]);
										plane[2].setZ(plane[2].getZ()+(float)origin[2]);
										String texture=myL2.getTexture(currentFace.getTexture());
										float[] textureS=new float[3];
										float[] textureT=new float[3];
										TexMatrix currentTexMatrix=myL17.getTexMatrix(currentFace.getTexStyle());
										// Get the lengths of the axis vectors
										double UAxisLength=Math.sqrt(Math.pow(currentTexMatrix.getUAxisX(),2)+Math.pow(currentTexMatrix.getUAxisY(),2)+Math.pow(currentTexMatrix.getUAxisZ(),2));
										double VAxisLength=Math.sqrt(Math.pow(currentTexMatrix.getVAxisX(),2)+Math.pow(currentTexMatrix.getVAxisY(),2)+Math.pow(currentTexMatrix.getVAxisZ(),2));
										// In compiled maps, shorter vectors=longer textures and vice versa. This will convert their lengths back to 1. We'll use the actual scale values for length.
										textureS[0]=(float)(currentTexMatrix.getUAxisX()/UAxisLength);
										textureS[1]=(float)(currentTexMatrix.getUAxisY()/UAxisLength);
										textureS[2]=(float)(currentTexMatrix.getUAxisZ()/UAxisLength);
										float textureShiftS=currentTexMatrix.getUShift();
										textureT[0]=(float)(currentTexMatrix.getVAxisX()/VAxisLength);
										textureT[1]=(float)(currentTexMatrix.getVAxisY()/VAxisLength);
										textureT[2]=(float)(currentTexMatrix.getVAxisZ()/VAxisLength);
										float textureShiftT=currentTexMatrix.getVShift();
										float texRot=0; // In compiled maps this is calculated into the U and V axes, so set it to 0 until I can figure out a good way to determine a better value.
										float texScaleX=(float)(1/UAxisLength);// Let's use these values using the lengths of the U and V axes we found above.
										float texScaleY=(float)(1/VAxisLength);
										int flags=currentFace.getType(); // This is actually a set of flags. Whatever.
										String material=myL3.getMaterial(currentFace.getMaterial());
										float lgtScale=16; // These values are impossible to get from a compiled map since they
										float lgtRot=0;    // are used by RAD for generating lightmaps, then are discarded, I believe.
										try {
											brushSides[l]=new MAPBrushSide(plane, texture, textureS, textureShiftS, textureT, textureShiftT,
										                                          texRot, texScaleX, texScaleY, flags, material, lgtScale, lgtRot);
										} catch(InvalidMAPBrushSideException e) {
											System.out.println("Error creating brush side "+l+" on brush "+k+" in leaf "+j+" in model "+i+", side not written.");
										}
									}
								}
								// FOR DETERMINING PLANE FLIP
								// To figure out the correct flip for the plane, I use two facts about BSP brushes
								// 1. They are always convex, therefore a point on one of the faces will be on the negative side of every other plane in the brush
								// 2. The positive side of every plane in a brush must face outwards, and the positive side is the side with the face
								// Limitation: There must be at least one face which was defined by vertices.
								// TODO: This doesn't fucking work. What is wrong? Probably the point used
								/*if(numVertFacesThisBrsh>0) { // If there was a face defined by vertices, if not just move on
									Vertex[] points=brushSides[vertFaceIndex].getPlane();
									// Find the point in the middle of these three points. It'll be on the plane.
									Vertex temp=new Vertex(points[0].getX()+(points[0].getX()-points[1].getX())/-2, points[0].getY()+(points[0].getY()-points[1].getY())/-2, points[0].getZ()+(points[0].getZ()-points[1].getZ())/-2);
									Vertex point=new Vertex(temp.getX()+(temp.getX()-points[2].getX())/-2, temp.getY()+(temp.getY()-points[2].getY())/-2, temp.getZ()+(temp.getZ()-points[2].getZ())/-2);
									for(int l=0;l<numSides;l++) { // For each side, AFTER the entire MAPBrushSide list has been populated
										if(!vertFaces[l] && l!=vertFaceIndex) { // If the current face was not defined by vertices
											BrushSide currentSide=myL16.getBrushSide(firstSide+l);
											Face currentFace=myL9.getFace(currentSide.getFace());
											if(!myL2.getTexture(currentFace.getTexture()).equalsIgnoreCase("special/bevel")) { // If this face uses special/bevel, skip the face completely
												Plane currentPlane=newPlanes.getPlane(currentSide.getPlane());
												// Formula for the signed distance from a plane to a point, I hope to jesus this works
												// Source: http://mathworld.wolfram.com/Point-PlaneDistance.html
												double signedDist=(currentPlane.getA()*point.getX()+currentPlane.getB()*point.getY()+currentPlane.getC()*point.getZ()-currentPlane.getDist())/(Math.sqrt(Math.pow(currentPlane.getA(),2)+Math.pow(currentPlane.getB(),2)+Math.pow(currentPlane.getC(),2)));
												if(signedDist>0) { // This > may need to be flipped, I don't know yet
													brushSides[l].flipPlane();
												}
											}
										}
									}
								}*/
								
								for(int l=0;l<numSides;l++) { // For each side, AFTER the entire MAPBrushSide list has been populated and plane flip is sorted out
									try {
										mapBrush.addAttribute(brushSides[l].toString()); // Add the MAPBrushSide to the current brush as an attribute
									} catch(java.lang.NullPointerException e) { // If the object was never created, because the face was special/bevel
										; // Do nothing, it doesn't matter
									}
								}
								mapBrush.addAttribute("}");
								mapFile.getEntity(i).addAttribute(mapBrush.toString()); // Remember entity i? It's the current entity. This
								                                                        // adds the brush we've been finding and creating to
								                                                        // entity i as an attribute. The way I've coded this
								                                                        // whole program and the entities parser, this shouldn't
							                                                           // cause any issues at all.
							}
						}
					}
				}
				mapFile.getEntity(i).deleteAttribute("model");
				// Recreate origin brushes for entities that need them
				// These are discarded on compile and replaced with an "origin" attribute in the entity.
				// I need to undo that. For this I will create a 32x32 brush, centered at the point defined
				// by the "origin" attribute.
				if(origin[0]!=0 || origin[1]!=0 || origin[2]!=0) { // If this brush uses the "origin" attribute
					Entity newOriginBrush=new Entity("{ // Brush "+numBrshs);
					numBrshs++;
					Vertex[][] planes=new Vertex[6][3]; // Six planes for a cube brush, three vertices for each plane
					float[][] textureS=new float[6][3];
					float[][] textureT=new float[6][3];
					// The planes and their texture scales
					// I got these from an origin brush created by Gearcraft. Don't worry where these numbers came from, they work.
					// Top
					planes[0][0]=new Vertex((float)(-16+origin[0]), (float)(16+origin[1]), (float)(16+origin[2]));
					planes[0][1]=new Vertex((float)(16+origin[0]), (float)(16+origin[1]), (float)(16+origin[2]));
					planes[0][2]=new Vertex((float)(16+origin[0]), (float)(-16+origin[1]), (float)(16+origin[2]));
					textureS[0][0]=1;
					textureT[0][1]=-1;
					// Bottom
					planes[1][0]=new Vertex((float)(-16+origin[0]), (float)(-16+origin[1]), (float)(-16+origin[2]));
					planes[1][1]=new Vertex((float)(16+origin[0]), (float)(-16+origin[1]), (float)(-16+origin[2]));
					planes[1][2]=new Vertex((float)(16+origin[0]), (float)(16+origin[1]), (float)(-16+origin[2]));
					textureS[1][0]=1;
					textureT[1][1]=-1;
					// Left
					planes[2][0]=new Vertex((float)(-16+origin[0]), (float)(16+origin[1]), (float)(16+origin[2]));
					planes[2][1]=new Vertex((float)(-16+origin[0]), (float)(-16+origin[1]), (float)(16+origin[2]));
					planes[2][2]=new Vertex((float)(-16+origin[0]), (float)(-16+origin[1]), (float)(-16+origin[2]));
					textureS[2][1]=1;
					textureT[2][2]=-1;
					// Right
					planes[3][0]=new Vertex((float)(16+origin[0]), (float)(16+origin[1]), (float)(-16+origin[2]));
					planes[3][1]=new Vertex((float)(16+origin[0]), (float)(-16+origin[1]), (float)(-16+origin[2]));
					planes[3][2]=new Vertex((float)(16+origin[0]), (float)(-16+origin[1]), (float)(16+origin[2]));
					textureS[3][1]=1;
					textureT[3][2]=-1;
					// Near
					planes[4][0]=new Vertex((float)(16+origin[0]), (float)(16+origin[1]), (float)(16+origin[2]));
					planes[4][1]=new Vertex((float)(-16+origin[0]), (float)(16+origin[1]), (float)(16+origin[2]));
					planes[4][2]=new Vertex((float)(-16+origin[0]), (float)(16+origin[1]), (float)(-16+origin[2]));
					textureS[4][0]=1;
					textureT[4][2]=-1;
					// Far
					planes[5][0]=new Vertex((float)(16+origin[0]), (float)(-16+origin[1]), (float)(-16+origin[2]));
					planes[5][1]=new Vertex((float)(-16+origin[0]), (float)(-16+origin[1]), (float)(-16+origin[2]));
					planes[5][2]=new Vertex((float)(-16+origin[0]), (float)(-16+origin[1]), (float)(16+origin[2]));
					textureS[5][0]=1;
					textureT[5][2]=-1;
					
					for(int j=0;j<6;j++) {
						try {
							MAPBrushSide currentEdge=new MAPBrushSide(planes[j], "special/origin", textureS[j], 0, textureT[j], 0, 0, 1, 1, 0, "wld_lightmap", 16, 0);
							newOriginBrush.addAttribute(currentEdge.toString());
						} catch(InvalidMAPBrushSideException e) {
							// This message will never be displayed.
							System.out.println("Bad origin brush, there's something wrong with the code.");
						}
					}
					newOriginBrush.addAttribute("}");
					mapFile.getEntity(i).addAttribute(newOriginBrush.toString());
				}
				mapFile.getEntity(i).deleteAttribute("origin");
			}
		}
		System.out.println("Saving .map...");
		mapFile.save(path);
		r.gc(); // Collect garbage, there will be a lot of it
	}
	
	// -flipPlane(Vertex[])
	// Takes a plane as an array of vertices and flips it over.
	private Vertex[] flipPlane(Vertex[] in) {
		Vertex[] out={in[0], in[2], in[1]};
		return out;
	}
	
	// ACCESSORS/MUTATORS
	public boolean isModified() {
		return modified;
	}
	
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
	
	public Lump09 getLump09() {
		return myL9;
	}
	
	public Lump10 getLump10() {
		return myL10;
	}
	
	public Lump11 getLump11() {
		return myL11;
	}
	
	public Lump12 getLump12() {
		return myL12;
	}
	
	public Lump13 getLump13() {
		return myL13;
	}
	
	public Lump14 getLump14() {
		return myL14;
	}
	
	public Lump15 getLump15() {
		return myL15;
	}
	
	public Lump16 getLump16() {
		return myL16;
	}
	
	public Lump17 getLump17() {
		return myL17;
	}
}
