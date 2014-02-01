using System;
// Brush class
// Tries to hold the data used by all formats of brush structure

public class Brush:LumpObject {

	// INITIAL DATA DECLARATION AND DEFINITION OF CONSTANTS
	// All four brush formats use some of these in some way
	private int firstSide = -1;
	private int numSides = -1;
	private byte[] contents = new byte[4];
	
	// CONSTRUCTORS
	public Brush() {
	}

	public Brush(LumpObject oldObject):base(oldObject.Data) {
		new Brush(oldObject.Data);
	}
	
	public Brush(byte[] data):base(data) {
		contents = new byte[]{data[0], data[1], data[2], data[3]};
		firstSide = DataReader.readInt(data[4], data[5], data[6], data[7]);
		numSides = DataReader.readInt(data[8], data[9], data[10], data[11]);
	}

	// METHODS
	public byte[] toByteArray() {
		byte[] ret = new byte[12];
		Array.Copy(contents, 0, ret, 0, 4);
		byte[] temp = BitConverter.GetBytes(firstSide);
		Array.Copy(temp, 0, ret, 4, 4);
		temp = BitConverter.GetBytes(numSides);
		Array.Copy(temp, 0, ret, 8, 4);
		return ret;
	}

	// createLump(byte[], uint)
	// Parses a byte array into a Lump object containing Brushes.
	public static Lump<Brush> createLump(byte[] inBytes) {
		int structLength = 12;
		int offset = 0;
		Lump<Brush> lump = new Lump<Brush>(inBytes.Length, structLength, inBytes.Length / structLength);
		byte[] bytes = new byte[structLength];
		for (int i = 0; i < inBytes.Length / structLength; i++) {
			for (int j = 0; j < structLength; j++) {
				bytes[j] = inBytes[offset + j];
			}
			lump.Add(new Brush(bytes));
			offset += structLength;
		}
		return lump;
	}

	// ACCESSORS/MUTATORS
	virtual public int FirstSide {
		get {
			return firstSide;
		}
		set {
			firstSide = value;
		}
	}
	virtual public int NumSides {
		get {
			return numSides;
		}
		set {
			numSides = value;
		}
	}
	virtual public byte[] Contents {
		get {
			return contents;
		}
		set {
			if(contents.Length == 4) {
				contents = value;
			}
		}
	}
}