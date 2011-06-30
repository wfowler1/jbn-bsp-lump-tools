// NFBSP class
//
// This class holds all the basic information about all eighteen lumps
// in a NightFire BSP map (after the lumps have been separated) such as
// lump length (in bytes) as well as the number of items in the lumps.
// This is NOT a lump separator.
//
// The data for a specific lump is contained in its own Lump class, which
// is specific to that lump. This class is for manipulating and searching
// for data, while the Lump class merely holds, finds and replaces data.

import java.io.File;
import java.io.FileInputStream;

public class NFBSP {

	// INITIAL DATA DECLARATION AND DEFINITION OF CONSTANTS

	// All lumps will be in the same folder. This String IS that folder.
	private String filepath;
	
	// Each lump has its own class for handling its specific data structures.
	private Lump00 myL0;
	private Lump01 myL1;
	/*private Lump02 myL2;
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
	private Lump17 myL17;*/
	
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
	public String[] LUMPNAMES = new String[NUMLUMPS];
		
	// This holds the size of the data structures for each lump. If
	// the lump does not have a set size, the size is -1. If the lump's
	// size has not been determined yet, the size is 0.
	public int[] lumpDataSizes = new int[NUMLUMPS];
		
		
		
		
	// CONSTRUCTORS
	// This accepts a file path and parses it into the form needed. If the file is empty (or not found)
	// the program fails nicely.
	public NFBSP(String in) {
	
		LUMPNAMES[ENTITIES]="Entities";
		LUMPNAMES[PLANES]="Planes";
		LUMPNAMES[TEXTURES]="Textures";
		LUMPNAMES[MATERIALS]="Materials";
		LUMPNAMES[VERTICES]="Vertices";
		LUMPNAMES[NORMALS]="Normals";
		LUMPNAMES[INDICES]="Indices";
		LUMPNAMES[VISIBILITY]="Visibility";
		LUMPNAMES[NODES]="Nodes";
		LUMPNAMES[FACES]="Faces";
		LUMPNAMES[LIGHTING]="Lighting";
		LUMPNAMES[LEAVES]="Leaves";
		LUMPNAMES[MARKSURFACES]="Mark Surfaces";
		LUMPNAMES[MARKBRUSHES]="Mark Brushes";
		LUMPNAMES[MODELS]="Models";
		LUMPNAMES[BRUSHES]="Brushes";
		LUMPNAMES[BRUSHSIDES]="Brushsides";
		LUMPNAMES[TEXMATRIX]="Texmatrix";
		
		lumpDataSizes[ENTITIES] = -1; // The entities lump does not have a set data length per entity
		lumpDataSizes[PLANES] = 20;
		lumpDataSizes[TEXTURES] = 64;
		lumpDataSizes[MATERIALS] = 64;
		lumpDataSizes[VERTICES] = 12;
		lumpDataSizes[NORMALS] = -1; // This lump is never referenced but is necessary
		lumpDataSizes[INDICES] = 4;
		lumpDataSizes[VISIBILITY] = 0; // This one varies for every map, and will be determined by constructor
		lumpDataSizes[NODES] = 36;
		lumpDataSizes[FACES] = 48;
		lumpDataSizes[LIGHTING] = 3;
		lumpDataSizes[LEAVES] = 48;
		lumpDataSizes[MARKSURFACES] = 4;
		lumpDataSizes[MARKBRUSHES] = 4;
		lumpDataSizes[MODELS] = 56;
		lumpDataSizes[BRUSHES] = 12;
		lumpDataSizes[BRUSHSIDES] = 8;
		lumpDataSizes[TEXMATRIX] = 32;
		
		try {
			if (in.charAt(in.length()-1) != '\\' && in.charAt(in.length()-1) != '/') {
				in+="\\";
			}
			filepath=in;
			
			// Find the length of the data structure in visibility lump
			FileInputStream lump07LengthGrabber=new FileInputStream(filepath+"11 - Leaves.hex");
			byte[] lenL07AsByteArray=new byte[4];
			lump07LengthGrabber.skip(100);
			lump07LengthGrabber.read(lenL07AsByteArray);
			int lenL07 = lenL07AsByteArray[0] + lenL07AsByteArray[1]*256 + lenL07AsByteArray[2]*65536 + lenL07AsByteArray[3]*16777216;
			lumpDataSizes[VISIBILITY]=lenL07;
			
			myL0 = new Lump00(filepath+"00 - Entities.txt");
			myL1 = new Lump01(filepath+"01 - Planes.hex");
			/*myL2 = new Lump02(filepath+"02 - Textures.hex");
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
			myL16 = new Lump16(filepath+"16 - Brush Sides.hex");
			myL17 = new Lump17(filepath+"17 - Texmatrix.hex");*/
			
		} catch(java.lang.StringIndexOutOfBoundsException e) {
			System.out.println("Error: invalid path");
		} catch(java.io.FileNotFoundException e) {
			System.out.println("Error: lumps not found");
		} catch(java.io.IOException e) {
			System.out.println("Error: Leaves lump too small!");
		}
	}
	/*
	System.out.println("Entities lump: "+oldL[0].length()+" bytes");
	System.out.println("Planes lump: "+oldL[1].length()+" bytes, "+(double)oldL[1].length()/20.0+" items");
	System.out.println("Textures lump: "+oldL[2].length()+" bytes, "+(double)oldL[2].length()/64.0+" items");
	System.out.println("Materials lump: "+oldL[3].length()+" bytes, "+(double)oldL[3].length()/64.0+" items");
	System.out.println("Vertices lump: "+oldL[4].length()+" bytes, "+(double)oldL[4].length()/12.0+" items");
	System.out.println("Normals lump: "+oldL[5].length()+" bytes");
	System.out.println("Indices lump: "+oldL[6].length()+" bytes, "+(double)oldL[6].length()/4.0+" items");
	System.out.println("Visibility lump: "+oldL[7].length()+" bytes");
	System.out.println("Nodes lump: "+oldL[8].length()+" bytes, "+(double)oldL[8].length()/36.0+" items");
	System.out.println("Faces lump: "+oldL[9].length()+" bytes, "+(double)oldL[9].length()/48.0+" items");
	System.out.println("Lighting lump: "+oldL[10].length()+" bytes, "+(double)oldL[10].length()/3.0+" items");
	System.out.println("Leaves lump: "+oldL[11].length()+" bytes, "+(double)oldL[11].length()/48.0+" items");
	System.out.println("Leaf surfaces lump: "+oldL[12].length()+" bytes, "+(double)oldL[12].length()/4.0+" items");
	System.out.println("Leaf brushes lump: "+oldL[13].length()+" bytes, "+(double)oldL[13].length()/4.0+" items");
	System.out.println("Models lump: "+oldL[14].length()+" bytes, "+(double)oldL[14].length()/56.0+" items");
	System.out.println("Brushes lump: "+oldL[15].length()+" bytes, "+(double)oldL[15].length()/12.0+" items");
	System.out.println("Brush sides lump: "+oldL[16].length()+" bytes, "+(double)oldL[16].length()/8.0+" items");
	System.out.println("Texture scales lump: "+oldL[17].length()+" bytes, "+(double)oldL[17].length()/32.0+" items");
	*/
}