using System;
// Vertex class
// Constains all data necessary to handle a vertex in any BSP format.

public class Vertex:LumpObject, IEquatable<Vertex> {
	
	// INITIAL DATA DECLARATION AND DEFINITION OF CONSTANTS
	public const int X = 0;
	public const int Y = 1;
	public const int Z = 2;
	
	private Vector3D vertex = Vector3D.UNDEFINED;
	
	// CONSTRUCTORS
	public Vertex() {
	}

	public Vertex(LumpObject data):base(data.Data) {
		new Vertex(data.Data);
	}
	
	public Vertex(byte[] data):base(data) {
		vertex = DataReader.readPoint3F(data[0], data[1], data[2], data[3], data[4], data[5], data[6], data[7], data[8], data[9], data[10], data[11]);
	}
	
	// METHODS
	public static Lump<Vertex> createLump(byte[] data) {
		int structLength = 12;
		int offset=0;
		Lump<Vertex> lump = new Lump<Vertex>(data.Length, structLength, data.Length / structLength);
		byte[] bytes=new byte[structLength];
		for(int i=0;i<data.Length / structLength;i++) {
			for (int j=0;j<structLength;j++) {
				bytes[j]=data[offset+j];
			}
			lump.Add(new Vertex(bytes));
			offset+=structLength;
		}
		return lump;
	}

	public static bool operator ==(Vertex v1, Vertex v2) {
		if(Object.ReferenceEquals(v1, null) ^ Object.ReferenceEquals(v2, null)) { return false; }
		if(Object.ReferenceEquals(v1, null) && Object.ReferenceEquals(v2, null)) { return true; }
		return v1.Vector==v2.Vector;
	}

	public static bool operator !=(Vertex v1, Vertex v2) {
		if(Object.ReferenceEquals(v1, null) ^ Object.ReferenceEquals(v2, null)) { return true; }
		if(Object.ReferenceEquals(v1, null) && Object.ReferenceEquals(v2, null)) { return false; }
		return v1.Vector!=v2.Vector;
	}

	public bool Equals(Vertex v2) {
		return this==v2;
	}

	public byte[] toByteArray() {
		byte[] ret = new byte[12];
		byte[] temp = BitConverter.GetBytes((float)vertex.X);
		Array.Copy(temp, 0, ret, 0, 4);
		temp = BitConverter.GetBytes((float)vertex.Y);
		Array.Copy(temp, 0, ret, 4, 4);
		temp = BitConverter.GetBytes((float)vertex.Z);
		Array.Copy(temp, 0, ret, 8, 4);
		return ret;
	}
	
	// ACCESSORS/MUTATORS
	public virtual Vector3D Vector {
		get {
			return vertex;
		}
		set {
			vertex = value;
		}
	}
}