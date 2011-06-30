// Lump08 class

// Stores references to each node object in a BSP.

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.util.*;

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
	public Lump08(String in) throws java.io.FileNotFoundException, java.io.IOException {
		data=new File(in);
		numNodes=getNumElements();
		nodes=new Node[numNodes];
		populateNodeList();
	}
	
	// This one accepts the input file path as a File
	public Lump08(File in) throws java.io.FileNotFoundException, java.io.IOException {
		data=in;
		numNodes=getNumElements();
		nodes=new Node[numNodes];
		populateNodeList();
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