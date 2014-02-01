using System.IO;
using System.Collections.Generic;
using System.Linq;
// BSPReader class

// Does the actual reading of the BSP file and takes appropriate
// action based primarily on BSP version number. It also feeds all
// appropriate data to the different BSP version classes. This
// does not actually do any data processing or analysis, it simply
// reads from the hard drive and sends the data where it needs to go.
// Deprecates the LS class, and doesn't create a file for every lump!
using System;

public class BSPReader {
	
	// INITIAL DATA DECLARATION AND DEFINITION OF CONSTANTS
	
	private FileInfo BSPFile;
	private FileStream stream;
	private BinaryReader br;
	public const int OFFSET = 0;
	public const int LENGTH = 1;
	
	private int lumpOffset = 0;
	private int lumpLength = 0;
	private BSP BSPObject;
	
	// CONSTRUCTORS
	
	// Takes a String in and assumes it is a path. That path is the path to the file
	// that is the BSP and its name minus the .BSP extension is assumed to be the folder.
	// See comments below for clarification. Case does not matter on the extension, so it
	// could be .BSP, .bsp, etc.
	
	public BSPReader(string path) : this(new FileInfo(path)) {
	}
	
	public BSPReader(FileInfo file) {
		BSPFile = file;
		if (!File.Exists(BSPFile.FullName)) {
			Console.WriteLine("Unable to open BSP file; file "+BSPFile.FullName+" not found.");
		} else {
			this.stream = new FileStream(BSPFile.FullName, FileMode.Open);
			this.br = new BinaryReader(this.stream);
		}
	}
	
	// METHODS

	public void readBSP() {
		try {
			byte[] theLump = new byte[0];
			byte[] vis = new byte[0];
			BSPObject = new BSP(BSPFile.FullName);
			Console.WriteLine("Opening " + BSPFile.FullName);
			for(int i=0;i<18;i++) {
				try {
					theLump = readLumpNum(i);
					switch(i) {
						case 0:
							BSPObject.Entities = Entity.createLump(theLump);
							break;
						case 1:
							BSPObject.Planes = Plane.createLump(theLump);
							break;
						case 2:
							BSPObject.Textures = Texture.createLump(theLump);
							break;
						case 3:
							BSPObject.Materials = Texture.createLump(theLump);
							break;
						case 4:
							BSPObject.Vertices = Vertex.createLump(theLump);
							break;
						case 5:
							BSPObject.Normals = new Lump<LumpObject>(theLump.Length, 0);
							break;
						case 6:
							BSPObject.Indices = new NumList(theLump, NumList.dataType.UINT);
							break;
						case 7:
							vis = theLump;
							break;
						case 8:
							BSPObject.Nodes = Node.createLump(theLump);
							break;
						case 9:
							BSPObject.Faces = Face.createLump(theLump);
							break;
						case 10:
							BSPObject.Lightmaps = new NumList(theLump, NumList.dataType.UBYTE);
							break;
						case 11:
							BSPObject.Leaves = Leaf.createLump(theLump);
							break;
						case 12:
							BSPObject.MarkSurfaces = new NumList(theLump, NumList.dataType.UINT);
							break;
						case 13:
							BSPObject.MarkBrushes = new NumList(theLump, NumList.dataType.UINT);
							break;
						case 14:
							BSPObject.Models = Model.createLump(theLump);
							break;
						case 15:
							BSPObject.Brushes = Brush.createLump(theLump);
							break;
						case 16:
							BSPObject.BrushSides = BrushSide.createLump(theLump);
							break;
						case 17:
							BSPObject.TexInfo = TexInfo.createLump(theLump);
							break;
					}
				} catch {
					dumpLump(theLump);
				}
			}
			try {
				int visLength = BSPObject.Leaves[2].PVS;
				if(visLength > 0 && vis.Length > 0) {
					BSPObject.Vis = new Lump<LumpObject>(vis, visLength);
				}
			} catch(ArgumentOutOfRangeException) { ; }
			if(BSPObject.Vis == null) {
				BSPObject.Vis = new Lump<LumpObject>(0,0);
			}
			BSPObject.printBSPReport();
		}
		catch (System.IO.IOException) {
			Console.WriteLine("Unable to access BSP file! Is it open in another program?");
		}
		br.Close();
	}

	public byte[] readLumpNum(int index) {
		return readLumpFromHeader(4 + (8*index));
	}
	
	// Returns the lump referenced by the offset/length pair at the specified offset
	public byte[] readLumpFromHeader(int offset) {
		try {
			stream.Seek(offset, SeekOrigin.Begin);
			byte[] input = br.ReadBytes(8);
			lumpOffset = DataReader.readInt(input[0], input[1], input[2], input[3]);
			lumpLength = DataReader.readInt(input[4], input[5], input[6], input[7]);
			return readLump(lumpOffset, lumpLength);
		}
		catch (System.IO.IOException) {
			Console.WriteLine("Unknown error reading BSP, it was working before!");
		}
		return new byte[0];
	}

	// +readLump(int, int)
	// Reads the lump length bytes long at offset in the file
	public byte[] readLump(int offset, int length) {
		try {
			stream.Seek(offset, SeekOrigin.Begin);
			byte[] input = br.ReadBytes(length);
			return input;
		}
		catch (System.IO.IOException) {
			Console.WriteLine("Unknown error reading BSP, it was working before!");
		}
		return new byte[0];
	}

	public void dumpLump(byte[] data) {
		if(Settings.dumpCrashLump) {
			Console.WriteLine("Error reading a lump, dumping to crashlump.lmp!");
			writeLump("crashlump.lmp", data);
		}
	}

	public void writeLump(string name, byte[] data) {
		try {
			FileStream debugstream;
			if(Settings.outputFolder=="default") {
				debugstream = new FileStream(BSPObject.Folder +"\\"+ name, FileMode.Create, FileAccess.Write);
			} else {
				debugstream = new FileStream(Settings.outputFolder + name, FileMode.Create, FileAccess.Write);
			}
			BinaryWriter bw = new BinaryWriter(debugstream);
			debugstream.Seek(0, SeekOrigin.Begin);
			bw.Write(data);
		} catch(System.IO.IOException e) {
			if(Settings.outputFolder=="default") {
				Console.WriteLine("ERROR: Could not save "+BSPObject.Folder +"\\"+ name+", ensure the file is not open in another program.");
			} else {
				Console.WriteLine("ERROR: Could not save "+Settings.outputFolder + name+", ensure the file is not open in another program.");
			}
			throw e;
		}
	}
	
	// ACCESSORS/MUTATORS
	
	public BSP BSPData {
		get {
			return BSPObject;
		}
	}
}