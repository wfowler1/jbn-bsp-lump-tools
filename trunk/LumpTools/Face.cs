using System;
// Face class
// Replaces all the separate face classes for different versions of BSP.
// Or, at least the ones I need.

public class Face:LumpObject {
	
	// INITIAL DATA DECLARATION AND DEFINITION OF CONSTANTS
	private int plane = - 1;
	private int firstVertex = - 1;
	private int numVertices = - 1;
	private int firstIndex = -1;
	private int numIndices = -1;
	private byte[] flags = new byte[4];
	private int texture = - 1;
	private int material = - 1;
	private int textureScale = - 1;
	private int unknown = -1;
	private int lightStyles = -1;
	private int lightMaps = -1;
	
	// CONSTRUCTORS
	public Face() {
	}
	
	public Face(LumpObject data):base(data.Data) {
		new Face(data.Data);
	}
	
	public Face(byte[] data):base(data) {
		plane = DataReader.readInt(data[0], data[1], data[2], data[3]);
		firstVertex = DataReader.readInt(data[4], data[5], data[6], data[7]);
		numVertices = DataReader.readInt(data[8], data[9], data[10], data[11]);
		firstIndex = DataReader.readInt(data[12], data[13], data[14], data[15]);
		numIndices = DataReader.readInt(data[16], data[17], data[18], data[19]);
		flags = new byte[]{data[20], data[21], data[22], data[23]};
		texture = DataReader.readInt(data[24], data[25], data[26], data[27]);
		material = DataReader.readInt(data[28], data[29], data[30], data[31]);
		textureScale = DataReader.readInt(data[32], data[33], data[34], data[35]);
		unknown = DataReader.readInt(data[36], data[37], data[38], data[39]);
		lightStyles = DataReader.readInt(data[40], data[41], data[42], data[43]);
		lightMaps = DataReader.readInt(data[44], data[45], data[46], data[47]);
	}
	
	// METHODS
	public byte[] toByteArray() {
		byte[] ret = new byte[48];
		byte[] temp = BitConverter.GetBytes(plane);
		Array.Copy(temp, 0, ret, 0, 4);
		temp = BitConverter.GetBytes(firstVertex);
		Array.Copy(temp, 0, ret, 4, 4);
		temp = BitConverter.GetBytes(numVertices);
		Array.Copy(temp, 0, ret, 8, 4);
		temp = BitConverter.GetBytes(firstIndex);
		Array.Copy(temp, 0, ret, 12, 4);
		temp = BitConverter.GetBytes(numIndices);
		Array.Copy(temp, 0, ret, 16, 4);
		Array.Copy(flags, 0, ret, 20, 4);
		temp = BitConverter.GetBytes(texture);
		Array.Copy(temp, 0, ret, 24, 4);
		temp = BitConverter.GetBytes(material);
		Array.Copy(temp, 0, ret, 28, 4);
		temp = BitConverter.GetBytes(textureScale);
		Array.Copy(temp, 0, ret, 32, 4);
		temp = BitConverter.GetBytes(unknown);
		Array.Copy(temp, 0, ret, 36, 4);
		temp = BitConverter.GetBytes(lightStyles);
		Array.Copy(temp, 0, ret, 40, 4);
		temp = BitConverter.GetBytes(lightMaps);
		Array.Copy(temp, 0, ret, 44, 4);
		return ret;
	}
	
	public static Lump<Face> createLump(byte[] data) {
		int structLength = 48;
		int offset = 0;
		Lump<Face> lump = new Lump<Face>(data.Length, structLength, data.Length / structLength);
		byte[] bytes = new byte[structLength];
		for (int i = 0; i < data.Length / structLength; i++) {
			for (int j = 0; j < structLength; j++) {
				bytes[j] = data[offset + j];
			}
			lump.Add(new Face(bytes));
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

	virtual public int FirstVertex {
		get {
			return firstVertex;
		}
		set {
			firstVertex = value;
		}
	}

	virtual public int NumVertices {
		get {
			return numVertices;
		}
		set {
			numVertices = value;
		}
	}

	virtual public int FirstIndex {
		get {
			return firstIndex;
		}
		set {
			firstIndex = value;
		}
	}

	virtual public int NumIndices {
		get {
			return numIndices;
		}
		set {
			numIndices = value;
		}
	}

	virtual public byte[] Flags {
		get {
			return flags;
		}
		set {
			flags = value;
		}
	}

	virtual public int Texture {
		get {
			return texture;
		}
		set {
			texture = value;
		}
	}

	virtual public int Material {
		get {
			return material;
		}
		set {
			material = value;
		}
	}

	virtual public int TextureScale {
		get {
			return textureScale;
		}
		set {
			textureScale = value;
		}
	}

	public int Unknown {
		get {
			return unknown;
		}
		set {
			unknown = value;
		}
	}

	public int LightStyles {
		get {
			return lightStyles;
		}
		set {
			lightStyles = value;
		}
	}

	public int LightMaps {
		get {
			return lightMaps;
		}
		set {
			lightMaps = value;
		}
	}
}