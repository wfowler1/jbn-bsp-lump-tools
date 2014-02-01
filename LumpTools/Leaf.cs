using System;
// Leaf class
// Master class for leaf structures. Only four formats needs leaves in order to be
// decompiled; Source, Nightfire, Quake and Quake 2.

public class Leaf:LumpObject {
	
	// INITIAL DATA DECLARATION AND DEFINITION OF CONSTANTS
	
	// In some formats (Quake 3 is the notable exclusion), leaves must be used to find
	// a list of brushes or faces to create solids out of.
	private int contents = -1;
	private int pvs = -1;
	private Vector3D mins = Vector3D.UNDEFINED;
	private Vector3D maxs = Vector3D.UNDEFINED;
	private int firstMarkBrush = -1;
	private int numMarkBrushes = -1;
	private int firstMarkFace = -1;
	private int numMarkFaces = -1;
	
	// CONSTRUCTORS
	public Leaf() {
	}

	public Leaf(LumpObject data):base(data.Data) {
		new Leaf(data.Data);
	}
	
	public Leaf(byte[] data):base(data) {
		this.contents=DataReader.readInt(data[0], data[1], data[2], data[3]);
		this.pvs=DataReader.readInt(data[4], data[5], data[6], data[7]);
		this.mins=DataReader.readPoint3F(data[8], data[9], data[10], data[11], data[12], data[13], data[14], data[15], data[16], data[17], data[18], data[19]);
		this.maxs=DataReader.readPoint3F(data[20], data[21], data[22], data[23], data[24], data[25], data[26], data[27], data[28], data[29], data[30], data[31]);
		this.firstMarkFace=DataReader.readInt(data[32], data[33], data[34], data[35]);
		this.numMarkFaces=DataReader.readInt(data[36], data[37], data[38], data[39]);
		this.firstMarkBrush=DataReader.readInt(data[40], data[41], data[42], data[43]);
		this.numMarkBrushes=DataReader.readInt(data[44], data[45], data[46], data[47]);
	}
	
	// METHODS
	public byte[] toByteArray() {
		byte[] ret = new byte[48];
		byte[] temp = BitConverter.GetBytes(contents);
		Array.Copy(temp, 0, ret, 0, 4);
		temp = BitConverter.GetBytes(pvs);
		Array.Copy(temp, 0, ret, 4, 4);
		temp = BitConverter.GetBytes((float)mins.X);
		Array.Copy(temp, 0, ret, 8, 4);
		temp = BitConverter.GetBytes((float)mins.Y);
		Array.Copy(temp, 0, ret, 12, 4);
		temp = BitConverter.GetBytes((float)mins.Z);
		Array.Copy(temp, 0, ret, 16, 4);
		temp = BitConverter.GetBytes((float)maxs.X);
		Array.Copy(temp, 0, ret, 20, 4);
		temp = BitConverter.GetBytes((float)maxs.Y);
		Array.Copy(temp, 0, ret, 24, 4);
		temp = BitConverter.GetBytes((float)maxs.Z);
		Array.Copy(temp, 0, ret, 28, 4);
		temp = BitConverter.GetBytes(firstMarkFace);
		Array.Copy(temp, 0, ret, 32, 4);
		temp = BitConverter.GetBytes(numMarkFaces);
		Array.Copy(temp, 0, ret, 36, 4);
		temp = BitConverter.GetBytes(firstMarkBrush);
		Array.Copy(temp, 0, ret, 40, 4);
		temp = BitConverter.GetBytes(numMarkBrushes);
		Array.Copy(temp, 0, ret, 44, 4);
		return ret;
	}

	public static Lump<Leaf> createLump(byte[] data) {
		int structLength = 48;
		int offset = 0;
		Lump<Leaf> lump = new Lump<Leaf>(data.Length, structLength, data.Length / structLength);
		byte[] bytes = new byte[structLength];
		for (int i = 0; i < data.Length / structLength; i++) {
			for (int j = 0; j < structLength; j++) {
				bytes[j] = data[offset + j];
			}
			lump.Add(new Leaf(bytes));
			offset += structLength;
		}
		return lump;
	}
	
	// ACCESSORS/MUTATORS
	public virtual int Contents {
		get { return contents; }
		set { contents = value; }
	}
	public virtual int PVS {
		get { return pvs; }
		set { pvs = value; }
	}
	public virtual Vector3D Mins {
		get { return mins; }
		set { mins = value; }
	}
	public virtual Vector3D Maxs {
		get { return maxs; }
		set { maxs = value; }
	}
	public virtual int FirstMarkBrush {
		get { return firstMarkBrush; }
		set { firstMarkBrush = value; }
	}
	public virtual int NumMarkBrushes {
		get { return numMarkBrushes; }
		set { numMarkBrushes = value; }
	}
	public virtual int FirstMarkFace {
		get { return firstMarkFace; }
		set { firstMarkFace = value; }
	}
	public virtual int NumMarkFaces {
		get { return numMarkFaces; }
		set { numMarkFaces = value; }
	}
}