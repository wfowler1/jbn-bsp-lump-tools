using System;
// Node class
// Contains all data needed for a node in a BSP tree. Should be usable by any format.

public class Node:LumpObject {
	// INITIAL DATA DECLARATION AND DEFINITION OF CONSTANTS
	
	private int plane = - 1;
	private int child1 = 0; // Negative values are valid here. However, the child can never be zero,
	private int child2 = 0; // since that would reference the head node causing an infinite loop.
	private Vector3D mins = Vector3D.UNDEFINED;
	private Vector3D maxs = Vector3D.UNDEFINED;
	
	// CONSTRUCTORS
	public Node() {
	}

	public Node(LumpObject data):base(data.Data) {
		new Node(data.Data);
	}
	
	public Node(byte[] data):base(data) {
		this.plane = DataReader.readInt(data[0], data[1], data[2], data[3]);
		this.child1 = DataReader.readInt(data[4], data[5], data[6], data[7]);
		this.child2 = DataReader.readInt(data[8], data[9], data[10], data[11]);
		this.mins = DataReader.readPoint3F(data[12], data[13], data[14], data[15], data[16], data[17], data[18], data[19], data[20], data[21], data[22], data[23]);
		this.maxs = DataReader.readPoint3F(data[24], data[25], data[26], data[27], data[28], data[29], data[30], data[31], data[32], data[33], data[34], data[35]);
	}
	
	// METHODS
	public byte[] toByteArray() {
		byte[] ret = new byte[36];
		byte[] temp = BitConverter.GetBytes(plane);
		Array.Copy(temp, 0, ret, 0, 4);
		temp = BitConverter.GetBytes(child1);
		Array.Copy(temp, 0, ret, 4, 4);
		temp = BitConverter.GetBytes(child2);
		Array.Copy(temp, 0, ret, 8, 4);
		temp = BitConverter.GetBytes((float)mins.X);
		Array.Copy(temp, 0, ret, 12, 4);
		temp = BitConverter.GetBytes((float)mins.Y);
		Array.Copy(temp, 0, ret, 16, 4);
		temp = BitConverter.GetBytes((float)mins.Z);
		Array.Copy(temp, 0, ret, 20, 4);
		temp = BitConverter.GetBytes((float)maxs.X);
		Array.Copy(temp, 0, ret, 24, 4);
		temp = BitConverter.GetBytes((float)maxs.Y);
		Array.Copy(temp, 0, ret, 28, 4);
		temp = BitConverter.GetBytes((float)maxs.Z);
		Array.Copy(temp, 0, ret, 32, 4);
		return ret;
	}

	public static Lump<Node> createLump(byte[] data) {
		int structLength = 36;
		int offset = 0;
		Lump<Node> lump = new Lump<Node>(data.Length, structLength, data.Length / structLength);
		byte[] bytes = new byte[structLength];
		for (int i = 0; i < data.Length / structLength; i++) {
			for (int j = 0; j < structLength; j++) {
				bytes[j] = data[offset + j];
			}
			lump.Add(new Node(bytes));
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
	virtual public int Child1 {
		get {
			return child1;
		}
		set {
			child1 = value;
		}
	}
	virtual public int Child2 {
		get {
			return child2;
		}
		set {
			child2 = value;
		}
	}
	virtual public Vector3D Mins {
		get {
			return mins;
		}
		set {
			mins = value;
		}
	}
	virtual public Vector3D Maxs {
		get {
			return maxs;
		}
		set {
			maxs = value;
		}
	}
}