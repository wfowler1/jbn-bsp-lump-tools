using System;
// BrushSide class

public class BrushSide:LumpObject {
	
	// INITIAL DATA DECLARATION AND DEFINITION OF CONSTANTS
	
	private int plane = - 1;
	private int face = - 1;
	
	// CONSTRUCTORS
	public BrushSide() {
	}

	public BrushSide(LumpObject bobSaget):base(bobSaget.Data) {
		new BrushSide(bobSaget.Data);
	}
	
	public BrushSide(byte[] data):base(data) {
		face = DataReader.readInt(data[0], data[1], data[2], data[3]);
		plane = DataReader.readInt(data[4], data[5], data[6], data[7]);
	}
	
	// METHODS
	public byte[] toByteArray() {
		byte[] ret = new byte[8];
		byte[] temp = BitConverter.GetBytes(face);
		Array.Copy(temp, 0, ret, 0, 4);
		temp = BitConverter.GetBytes(plane);
		Array.Copy(temp, 0, ret, 4, 4);
		return ret;
	}

	public static Lump<BrushSide> createLump(byte[] data) {
		int structLength = 8;
		int offset = 0;
		Lump<BrushSide> lump = new Lump<BrushSide>(data.Length, structLength, data.Length / structLength);
		byte[] bytes = new byte[structLength];
		for (int i = 0; i < data.Length / structLength; i++) {
			for (int j = 0; j < structLength; j++) {
				bytes[j] = data[offset + j];
			}
			lump.Add(new BrushSide(bytes));
			offset += structLength;
		}
		return lump;
	}

	// ACCESSORS/MUTATORS

	virtual public int Plane {
		get {
			return plane;
		}
		set {
			plane = value;
		}
	}

	virtual public int Face {
		get {
			return face;
		}
		set {
			face = value;
		}
	}
}