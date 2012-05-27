// Lump08 class

// Stores references to each node object in a BSP.

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;

public class Lump08 {

	// INITIAL DATA DECLARATION AND DEFINITION OF CONSTANTS
	
	public final int X=0;
	public final int Y=1;
	public final int Z=2;
	
	private File data;
	private int numNodes=0;
	private Node[] nodes;
	
	// CONSTRUCTORS
	
	// This one accepts the lump path as a String
	public Lump08(String in) {
		data=new File(in);
		try {
			numNodes=getNumElements();
			nodes=new Node[numNodes];
			populateNodeList();
		} catch(java.io.FileNotFoundException e) {
			System.out.println("ERROR: File "+data+" not found!");
		} catch(java.io.IOException e) {
			System.out.println("ERROR: File "+data+" could not be read, ensure the file is not open in another program");
		}
	}
	
	// This one accepts the input file path as a File
	public Lump08(File in) {
		data=in;
		try {
			numNodes=getNumElements();
			nodes=new Node[numNodes];
			populateNodeList();
		} catch(java.io.FileNotFoundException e) {
			System.out.println("ERROR: File "+data+" not found!");
		} catch(java.io.IOException e) {
			System.out.println("ERROR: File "+data+" could not be read, ensure the file is not open in another program");
		}
	}
	
	// Accepts an array of nodes and makes a lump out of it. As a side effect
	// of this, no data file is specified, so save() MUST be called with a path.
	public Lump08(Node[] in) {
		nodes=in;
		numNodes=nodes.length;
	}
	
	public Lump08(byte[] in) {
		int offset=0;
		numNodes=in.length/36;
		nodes=new Node[numNodes];
		for(int i=0;i<numNodes;i++) {
			byte[] bytes=new byte[36];
			for(int j=0;j<36;j++) {
				bytes[j]=in[offset+j];
			}
			nodes[i]=new Node(bytes);
			offset+=36;
		}
	}
	
	// METHODS
	
	// -populateNodeList()
	// Creates an array of Node objects which hold the data for all nodes.
	private void populateNodeList() throws java.io.FileNotFoundException, java.io.IOException {
		FileInputStream reader=new FileInputStream(data);
		for(int i=0;i<numNodes;i++) {
			byte[] datain=new byte[36];
			reader.read(datain);
			nodes[i]=new Node(datain);
		}
		reader.close();
	}
	
	// add(Node)
	// Adds a Node object to the array.
	public void add(Node in) {
		Node[] newList=new Node[numNodes+1];
		for(int i=0;i<numNodes;i++) {
			newList[i]=in;
		}
		newList[numNodes]=in;
		numNodes++;
	}
	
	// add (int int int float float float float float float)
	// Adds a node defined by data alone.
	public void add(int inPlane, int inChild0, int inChild1, float inMinX, float inMinY, float inMinZ, float inMaxX, float inMaxY, float inMaxZ) {
		add(new Node(inPlane, inChild0, inChild1, inMinX, inMinY, inMinZ, inMaxX, inMaxY, inMaxZ));
	}
	
	// Save(String)
	// Saves the lump to the specified path.
	public void save(String path) {
		File newFile=new File(path+"\\08 - Nodes.hex");
		try {
			if(!newFile.exists()) {
				newFile.createNewFile();
			} else {
				newFile.delete();
				newFile.createNewFile();
			}
			FileOutputStream nodeWriter=new FileOutputStream(newFile);
			byte[] data=toByteArray();
			nodeWriter.write(data);
			nodeWriter.close();
		} catch(java.io.IOException e) {
			System.out.println("ERROR: Could not save "+newFile+", ensure the file is not open in another program and the path "+path+" exists");
		}
	}
	
	public byte[] toByteArray() {
		byte[] data=new byte[numNodes*36];
		for(int i=0;i<numNodes;i++) {
			// This is MUCH faster than using DataOutputStream
			int out=nodes[i].getPlane();
			data[(i*36)+3]=(byte)((out >> 24) & 0xFF);
			data[(i*36)+2]=(byte)((out >> 16) & 0xFF);
			data[(i*36)+1]=(byte)((out >> 8) & 0xFF);
			data[i*36]=(byte)((out >> 0) & 0xFF);
			// It's occurred to me that I use this often enough to justify having a separate method for this stuff.
			out=nodes[i].getChild1();
			data[(i*36)+7]=(byte)((out >> 24) & 0xFF);
			data[(i*36)+6]=(byte)((out >> 16) & 0xFF);
			data[(i*36)+5]=(byte)((out >> 8) & 0xFF);
			data[(i*36)+4]=(byte)((out >> 0) & 0xFF);
			out=nodes[i].getChild2();
			data[(i*36)+11]=(byte)((out >> 24) & 0xFF);
			data[(i*36)+10]=(byte)((out >> 16) & 0xFF);
			data[(i*36)+9]=(byte)((out >> 8) & 0xFF);
			data[(i*36)+8]=(byte)((out >> 0) & 0xFF);
			out=Float.floatToRawIntBits(nodes[i].getMinX());
			data[(i*36)+15]=(byte)((out >> 24) & 0xFF);
			data[(i*36)+14]=(byte)((out >> 16) & 0xFF);
			data[(i*36)+13]=(byte)((out >> 8) & 0xFF);
			data[(i*36)+12]=(byte)((out >> 0) & 0xFF);
			out=Float.floatToRawIntBits(nodes[i].getMinY());
			data[(i*36)+19]=(byte)((out >> 24) & 0xFF);
			data[(i*36)+18]=(byte)((out >> 16) & 0xFF);
			data[(i*36)+17]=(byte)((out >> 8) & 0xFF);
			data[(i*36)+16]=(byte)((out >> 0) & 0xFF);
			out=Float.floatToRawIntBits(nodes[i].getMinZ());
			data[(i*36)+23]=(byte)((out >> 24) & 0xFF);
			data[(i*36)+22]=(byte)((out >> 16) & 0xFF);
			data[(i*36)+21]=(byte)((out >> 8) & 0xFF);
			data[(i*36)+20]=(byte)((out >> 0) & 0xFF);
			out=Float.floatToRawIntBits(nodes[i].getMaxX());
			data[(i*36)+27]=(byte)((out >> 24) & 0xFF);
			data[(i*36)+26]=(byte)((out >> 16) & 0xFF);
			data[(i*36)+25]=(byte)((out >> 8) & 0xFF);
			data[(i*36)+24]=(byte)((out >> 0) & 0xFF);
			out=Float.floatToRawIntBits(nodes[i].getMaxY());
			data[(i*36)+31]=(byte)((out >> 24) & 0xFF);
			data[(i*36)+30]=(byte)((out >> 16) & 0xFF);
			data[(i*36)+29]=(byte)((out >> 8) & 0xFF);
			data[(i*36)+28]=(byte)((out >> 0) & 0xFF);
			out=Float.floatToRawIntBits(nodes[i].getMaxZ());
			data[(i*36)+35]=(byte)((out >> 24) & 0xFF);
			data[(i*36)+34]=(byte)((out >> 16) & 0xFF);
			data[(i*36)+33]=(byte)((out >> 8) & 0xFF);
			data[(i*36)+32]=(byte)((out >> 0) & 0xFF);
		}
		return data;
	}
	
	// ACCESSORS/MUTATORS
	
	// Returns the length (in bytes) of the lump
	public long getLength() {
		return numNodes*36;
	}
	
	// Returns the number of nodes.
	public int getNumElements() {
		if(numNodes==0) {
			return (int)data.length()/36;
		} else {
			return numNodes;
		}
	}
	
	public Node getNode(int i) {
		return nodes[i];
	}
	
	public Node[] getNodes() {
		return nodes;
	}
	
	public void setNode(int i, Node in) {
		nodes[i]=in;
	}
	
	public void setNodes(Node[] in) {
		nodes=in;
		numNodes=nodes.length;
	}
}
