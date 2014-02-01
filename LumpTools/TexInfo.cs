using System;
// TexInfo class
// This class contains the texture scaling information.

public class TexInfo:LumpObject,IEquatable<TexInfo> {
	
	// INITIAL DATA DECLARATION AND DEFINITION OF CONSTANTS
	public static readonly Vector3D[] baseAxes = new Vector3D[]{new Vector3D(0, 0, 1), new Vector3D(1, 0, 0), new Vector3D(0, - 1, 0), new Vector3D(0, 0, - 1), new Vector3D(1, 0, 0), new Vector3D(0, - 1, 0), new Vector3D(1, 0, 0), new Vector3D(0, 1, 0), new Vector3D(0, 0, - 1), new Vector3D(- 1, 0, 0), new Vector3D(0, 1, 0), new Vector3D(0, 0, - 1), new Vector3D(0, 1, 0), new Vector3D(1, 0, 0), new Vector3D(0, 0, - 1), new Vector3D(0, - 1, 0), new Vector3D(1, 0, 0), new Vector3D(0, 0, - 1)};
	
	private Vector3D[] axes = { Vector3D.UNDEFINED, Vector3D.UNDEFINED };
	private float[] shifts = new float[]{System.Single.NaN, System.Single.NaN};
	
	// CONSTRUCTORS
	public TexInfo() {
	}

	public TexInfo(LumpObject data):base(data.Data) {
		new TexInfo(data.Data);
	}
	
	public TexInfo(byte[] data):base(data) {
		axes = new Vector3D[2];
		axes[0] = DataReader.readPoint3F(data[0], data[1], data[2], data[3], data[4], data[5], data[6], data[7], data[8], data[9], data[10], data[11]);
		shifts[0] = DataReader.readFloat(data[12], data[13], data[14], data[15]);
		axes[1] = DataReader.readPoint3F(data[16], data[17], data[18], data[19], data[20], data[21], data[22], data[23], data[24], data[25], data[26], data[27]);
		shifts[1] = DataReader.readFloat(data[28], data[29], data[30], data[31]);
	}
	
	// METHODS
	public byte[] toByteArray() {
		byte[] ret = new byte[32];
		byte[] temp = BitConverter.GetBytes((float)axes[0].X);
		Array.Copy(temp, 0, ret, 0, 4);
		temp = BitConverter.GetBytes((float)axes[0].Y);
		Array.Copy(temp, 0, ret, 4, 4);
		temp = BitConverter.GetBytes((float)axes[0].Z);
		Array.Copy(temp, 0, ret, 8, 4);
		temp = BitConverter.GetBytes(shifts[0]);
		Array.Copy(temp, 0, ret, 12, 4);
		temp = BitConverter.GetBytes((float)axes[1].X);
		Array.Copy(temp, 0, ret, 16, 4);
		temp = BitConverter.GetBytes((float)axes[1].Y);
		Array.Copy(temp, 0, ret, 20, 4);
		temp = BitConverter.GetBytes((float)axes[1].Z);
		Array.Copy(temp, 0, ret, 24, 4);
		temp = BitConverter.GetBytes(shifts[1]);
		Array.Copy(temp, 0, ret, 28, 4);
		return ret;
	}

	public static bool operator ==(TexInfo i1, TexInfo i2) {
		if(Object.ReferenceEquals(i1, null) ^ Object.ReferenceEquals(i2, null)) { return false; }
		if(Object.ReferenceEquals(i1, null) && Object.ReferenceEquals(i2, null)) { return true; }
		return ((i1.SAxis == i2.SAxis) && (i1.TAxis == i2.TAxis) && (i1.SShift == i2.SShift) && (i1.TShift == i2.TShift));
	}

	public static bool operator !=(TexInfo i1, TexInfo i2) {
		if(Object.ReferenceEquals(i1, null) ^ Object.ReferenceEquals(i2, null)) { return true; }
		if(Object.ReferenceEquals(i1, null) && Object.ReferenceEquals(i2, null)) { return false; }
		return ((i1.SAxis != i2.SAxis) && (i1.TAxis != i2.TAxis) && (i1.SShift != i2.SShift) && (i1.TShift != i2.TShift));
	}

	public bool Equals(TexInfo i2) {
		return this==i2;
	}
	
	// textureAxisFromPlane, adapted from code in the Quake III Arena source code. Stolen without
	// permission because it falls under the terms of the GPL v2 license, because I'm not making
	// any money, just awesome tools.
	public static Vector3D[] textureAxisFromPlane(Plane p) {
		int bestaxis = 0;
		double dot; // Current dot product
		double best = 0; // "Best" dot product so far
		for (int i = 0; i < 6; i++) {
			// For all possible axes, positive and negative
			dot = p.Normal*new Vector3D(baseAxes[i * 3]);
			if (dot > best) {
				best = dot;
				bestaxis = i;
			}
		}
		Vector3D[] out_Renamed = new Vector3D[2];
		out_Renamed[0] = new Vector3D(baseAxes[bestaxis * 3 + 1]);
		out_Renamed[1] = new Vector3D(baseAxes[bestaxis * 3 + 2]);
		return out_Renamed;
	}

	public static Lump<TexInfo> createLump(byte[] data) {
		int structLength = 32;
		int offset = 0;
		Lump<TexInfo> lump = new Lump<TexInfo>(data.Length, structLength, data.Length / structLength);
		byte[] bytes = new byte[structLength];
		for (int i = 0; i < data.Length / structLength; i++) {
			for (int j = 0; j < structLength; j++) {
				bytes[j] = data[offset + j];
			}
			lump.Add(new TexInfo(bytes));
			offset += structLength;
		}
		return lump;
	}

	// ACCESSORS/MUTATORS
	virtual public Vector3D SAxis {
		get {
			return axes[0];
		}
		set {
			axes[0] = value;
		}
	}

	virtual public Vector3D TAxis {
		get {
			return axes[1];
		}
		set {
			axes[1] = value;
		}
	}

	virtual public float SShift {
		get {
			return shifts[0];
		}
		set {
			shifts[0] = value;
		}
	}

	virtual public float TShift {
		get {
			return shifts[1];
		}
		set {
			shifts[1] = value;
		}
	}
}