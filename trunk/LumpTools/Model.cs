using System;
// Model class

public class Model:LumpObject {
	
	// INITIAL DATA DECLARATION AND DEFINITION OF CONSTANTS
	
	// In general, we need to use models to find one or more leaves containing the
	// information for the solids described by this model.
	private Vector3D mins = Vector3D.UNDEFINED;
	private Vector3D maxs = Vector3D.UNDEFINED;
	private uint[] unknowns = new uint[4];
	private int firstLeaf = - 1;
	private int numLeaves = - 1;
	private int firstFace = - 1;
	private int numFaces = - 1;
	
	// CONSTRUCTORS
	public Model() {
	}

	public Model(LumpObject data):base(data.Data) {
		new Model(data.Data);
	}
	
	public Model(byte[] data):base(data) {
		mins = DataReader.readPoint3F(data[0], data[1], data[2], data[3], data[4], data[5], data[6], data[7], data[8], data[9], data[10], data[11]);
		maxs = DataReader.readPoint3F(data[12], data[13], data[14], data[15], data[16], data[17], data[18], data[19], data[20], data[21], data[22], data[23]);
		unknowns[0] = DataReader.readUInt(data[24], data[25], data[26], data[27]);
		unknowns[1] = DataReader.readUInt(data[28], data[29], data[30], data[31]);
		unknowns[2] = DataReader.readUInt(data[32], data[33], data[34], data[35]);
		unknowns[3] = DataReader.readUInt(data[36], data[37], data[38], data[39]);
		firstLeaf = DataReader.readInt(data[40], data[41], data[42], data[43]);
		numLeaves = DataReader.readInt(data[44], data[45], data[46], data[47]);
		firstFace = DataReader.readInt(data[48], data[49], data[50], data[51]);
		numFaces = DataReader.readInt(data[52], data[53], data[54], data[55]);
	}
	
	// METHODS
	public byte[] toByteArray() {
		byte[] ret = new byte[56];
		byte[] temp = BitConverter.GetBytes((float)mins.X);
		Array.Copy(temp, 0, ret, 0, 4);
		temp = BitConverter.GetBytes((float)mins.Y);
		Array.Copy(temp, 0, ret, 4, 4);
		temp = BitConverter.GetBytes((float)mins.Z);
		Array.Copy(temp, 0, ret, 8, 4);
		temp = BitConverter.GetBytes((float)maxs.X);
		Array.Copy(temp, 0, ret, 12, 4);
		temp = BitConverter.GetBytes((float)maxs.Y);
		Array.Copy(temp, 0, ret, 16, 4);
		temp = BitConverter.GetBytes((float)maxs.Z);
		Array.Copy(temp, 0, ret, 20, 4);
		temp = BitConverter.GetBytes(unknowns[0]);
		Array.Copy(temp, 0, ret, 24, 4);
		temp = BitConverter.GetBytes(unknowns[1]);
		Array.Copy(temp, 0, ret, 28, 4);
		temp = BitConverter.GetBytes(unknowns[2]);
		Array.Copy(temp, 0, ret, 32, 4);
		temp = BitConverter.GetBytes(unknowns[3]);
		Array.Copy(temp, 0, ret, 36, 4);
		temp = BitConverter.GetBytes(firstLeaf);
		Array.Copy(temp, 0, ret, 40, 4);
		temp = BitConverter.GetBytes(numLeaves);
		Array.Copy(temp, 0, ret, 44, 4);
		temp = BitConverter.GetBytes(firstFace);
		Array.Copy(temp, 0, ret, 48, 4);
		temp = BitConverter.GetBytes(numFaces);
		Array.Copy(temp, 0, ret, 52, 4);
		return ret;
	}
	public static Lump<Model> createLump(byte[] data) {
		int structLength = 56;
		int offset = 0;
		Lump<Model> lump = new Lump<Model>(data.Length, structLength, data.Length / structLength);
		byte[] bytes = new byte[structLength];
		for (int i = 0; i < data.Length / structLength; i++) {
			for (int j = 0; j < structLength; j++) {
				bytes[j] = data[offset + j];
			}
			lump.Add(new Model(bytes));
			offset += structLength;
		}
		return lump;
	}
	
	// ACCESSORS/MUTATORS
	public Vector3D Mins {
		get {
			return mins;
		}
		set {
			mins = value;
		}
	}

	public Vector3D Maxs {
		get {
			return maxs;
		}
		set {
			maxs = value;
		}
	}

	public uint[] Unknowns {
		get {
			return unknowns;
		}
		set {
			if(value.Length == 4) {
				unknowns = value;
			}
		}
	}
	
	virtual public int FirstLeaf {
		get {
			return firstLeaf;
		}
		set {
			firstLeaf = value;
		}
	}
	
	virtual public int NumLeaves {
		get {
			return numLeaves;
		}
		set {
			numLeaves = value;
		}
	}
	
	virtual public int FirstFace {
		get {
			return firstFace;
		}
		set {
			firstFace = value;
		}
	}
	
	virtual public int NumFaces {
		get {
			return numFaces;
		}
		set {
			numFaces = value;
		}
	}
}