using System;
using System.Collections.Generic;
// Texture class
//
// Holds one string, a texture or material name.

public class Texture:LumpObject, IEquatable<Texture> {

	// INITIAL DATA DECLARATION AND DEFINITION OF CONSTANTS
	private string name = "";
	
	// CONSTRUCTORS
	public Texture() {
	}

	public Texture(string texture):base(Convert.FromBase64String(texture)) {
		this.name = texture;
	}
	
	public Texture(LumpObject data):base(data.Data) {
		new Texture(data.Data);
	}
	
	public Texture(byte[] data):base(data) {
		name = DataReader.readNullTerminatedString(new byte[]{data[0], data[1], data[2], data[3], data[4], data[5], data[6], data[7], data[8], data[9], data[10], data[11], data[12], data[13], data[14], data[15], data[16], data[17], data[18], data[19], data[20], data[21], data[22], data[23], data[24], data[25], data[26], data[27], data[28], data[29], data[30], data[31], data[32], data[33], data[34], data[35], data[36], data[37], data[38], data[39], data[40], data[41], data[42], data[43], data[44], data[45], data[46], data[47], data[48], data[49], data[50], data[51], data[52], data[53], data[54], data[55], data[56], data[57], data[58], data[59], data[60], data[61], data[62], data[63]});
	}
	
	// METHODS
	public static Lump<Texture> createLump(byte[] data) {
		int structLength = 64;
		int offset = 0;
		Lump<Texture> lump = new Lump<Texture>(data.Length, structLength, data.Length / structLength);
		byte[] bytes=new byte[structLength];
		for(int i=0;i<data.Length / structLength;i++) {
			for (int j=0;j<structLength;j++) {
				bytes[j]=data[offset+j];
			}
			lump.Add(new Texture(bytes));
			offset+=structLength;
		}
		return lump;
	}

	public static bool operator ==(Texture t1, Texture t2) {
		if(Object.ReferenceEquals(t1, null) ^ Object.ReferenceEquals(t2, null)) { return false; }
		if(Object.ReferenceEquals(t1, null) && Object.ReferenceEquals(t2, null)) { return true; }
		return t1.Name.Equals(t2.Name, StringComparison.InvariantCultureIgnoreCase);
	}

	public static bool operator !=(Texture t1, Texture t2) {
		if(Object.ReferenceEquals(t1, null) ^ Object.ReferenceEquals(t2, null)) { return true; }
		if(Object.ReferenceEquals(t1, null) && Object.ReferenceEquals(t2, null)) { return false; }
		return !t1.Name.Equals(t2.Name, StringComparison.InvariantCultureIgnoreCase);
	}

	public bool Equals(Texture t2) {
		return this==t2;
	}

	public byte[] toByteArray() {
		byte[] ret = new byte[64];
		int offset = 0;
		while(offset < name.Length && offset < 64) {
			ret[offset] = (byte)name[offset++];
		}
		return ret;
	}
	
	// ACCESSORS/MUTATORS
	virtual public string Name {
		get {
			return name;
		}
		set {
			name = value;
		}
	}

	public char this[int index] {
		get {
			return name[index];
		}
	}
}