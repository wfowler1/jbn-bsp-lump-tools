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
	
	// METHODS
	
	// -populateNodeList()
	// Creates an array of Node objects which hold the data for all nodes.
	private void populateNodeList() throws java.io.FileNotFoundException, java.io.IOException {
		FileInputStream reader=new FileInputStream(data);
		try {
			for(int i=0;i<numNodes;i++) {
				byte[] datain=new byte[36];
				reader.read(datain);
				nodes[i]=new Node(datain);
			}
		} catch(InvalidNodeException e) {
			System.out.println("WARNING: Funny lump size in "+data+", ignoring last node.");
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
	
	// add(Lump08)
	// Adds every node in another Lump08 object.
	// TODO: Find a way to make the game engine actually load the added lump data.
	// As it is right now, the new map's nodes are just piled on top of the current
	// map's data without ever being referenced. However, this may involve adding a
	// plane and setting every node to be on the proper side. Nightfire engine
	// documentation is nonexistant, so I don't exaclty know what would be involved,
	// but my experimentation leads me to believe the plane referenced in this lump
	// is very VERY important.
	public void add(Lump08 in) {
		File myLump14=new File(data.getParent()+"\\14 - Models.hex");
		File myLump11=new File(data.getParent()+"\\11 - Leaves.hex");
		int sizeL11=(int)myLump11.length()/48;
		Node[] newList=new Node[numNodes+in.getNumElements()];
		for(int i=0;i<numNodes;i++) {
			newList[i]=nodes[i];
		}
		for(int i=0;i<in.getNumElements();i++) {
			newList[i+numNodes]=in.getNode(i);
			if(newList[i+numNodes].getChild1()<0) { // Child1 is a Leaf
				// leaf indices are negative, so subtract the size of the old leaf list from the new index
				newList[i+numNodes].setChild1(newList[i+numNodes].getChild1()-sizeL11);
			} else { // child1 will be a Node
				newList[i+numNodes].setChild1(newList[i+numNodes].getChild1()+getNumElements());
			}
			if(newList[i+numNodes].getChild2()<0) { // Child2 is a Leaf
				newList[i+numNodes].setChild2(newList[i+numNodes].getChild2()-sizeL11);
			} else { // child2 will be a Node
				newList[i+numNodes].setChild2(newList[i+numNodes].getChild2()+getNumElements());
			}
		}
		numNodes=numNodes+in.getNumElements();
		nodes=newList;
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
			for(int i=0;i<numNodes;i++) {
				// This is MUCH faster than using DataOutputStream
				int out=nodes[i].getPlane();
				byte[] output={(byte)((out >> 0) & 0xFF), (byte)((out >> 8) & 0xFF), (byte)((out >> 16) & 0xFF), (byte)((out >> 24) & 0xFF)};
				nodeWriter.write(output);
				// It's occurred to me that I use this often enough to justify having a separate method for this stuff.
				out=nodes[i].getChild1();
				output[3]=(byte)((out >> 24) & 0xFF);
				output[2]=(byte)((out >> 16) & 0xFF);
				output[1]=(byte)((out >> 8) & 0xFF);
				output[0]=(byte)((out >> 0) & 0xFF);
				nodeWriter.write(output);
				out=nodes[i].getChild2();
				output[3]=(byte)((out >> 24) & 0xFF);
				output[2]=(byte)((out >> 16) & 0xFF);
				output[1]=(byte)((out >> 8) & 0xFF);
				output[0]=(byte)((out >> 0) & 0xFF);
				nodeWriter.write(output);
				out=Float.floatToRawIntBits(nodes[i].getMinX());
				output[3]=(byte)((out >> 24) & 0xFF);
				output[2]=(byte)((out >> 16) & 0xFF);
				output[1]=(byte)((out >> 8) & 0xFF);
				output[0]=(byte)((out >> 0) & 0xFF);
				nodeWriter.write(output);
				out=Float.floatToRawIntBits(nodes[i].getMinY());
				output[3]=(byte)((out >> 24) & 0xFF);
				output[2]=(byte)((out >> 16) & 0xFF);
				output[1]=(byte)((out >> 8) & 0xFF);
				output[0]=(byte)((out >> 0) & 0xFF);
				nodeWriter.write(output);
				out=Float.floatToRawIntBits(nodes[i].getMinZ());
				output[3]=(byte)((out >> 24) & 0xFF);
				output[2]=(byte)((out >> 16) & 0xFF);
				output[1]=(byte)((out >> 8) & 0xFF);
				output[0]=(byte)((out >> 0) & 0xFF);
				nodeWriter.write(output);
				out=Float.floatToRawIntBits(nodes[i].getMaxX());
				output[3]=(byte)((out >> 24) & 0xFF);
				output[2]=(byte)((out >> 16) & 0xFF);
				output[1]=(byte)((out >> 8) & 0xFF);
				output[0]=(byte)((out >> 0) & 0xFF);
				nodeWriter.write(output);
				out=Float.floatToRawIntBits(nodes[i].getMaxY());
				output[3]=(byte)((out >> 24) & 0xFF);
				output[2]=(byte)((out >> 16) & 0xFF);
				output[1]=(byte)((out >> 8) & 0xFF);
				output[0]=(byte)((out >> 0) & 0xFF);
				nodeWriter.write(output);
				out=Float.floatToRawIntBits(nodes[i].getMaxZ());
				output[3]=(byte)((out >> 24) & 0xFF);
				output[2]=(byte)((out >> 16) & 0xFF);
				output[1]=(byte)((out >> 8) & 0xFF);
				output[0]=(byte)((out >> 0) & 0xFF);
				nodeWriter.write(output);
			}
			nodeWriter.close();
		} catch(java.io.IOException e) {
			System.out.println("ERROR: Could not save "+newFile+", ensure the file is not open in another program and the path "+path+" exists");
		}
	}
	
	// save()
	// Saves the lump, overwriting the one data was read from
	public void save() {
		save(data.getParent());
	}
	
	// ACCESSORS/MUTATORS
	
	// Returns the length (in bytes) of the lump
	public long getLength() {
		return data.length();
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
}
