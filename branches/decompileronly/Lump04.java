// Lump04 class

// This class holds an array of vertices of the Vertex class. Really it's an array
// of float3 but that's how it is for consistency's sake.

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;

public class Lump04 {
	
	// INITIAL DATA DECLARATION AND DEFINITION OF CONSTANTS
	
	public final int X=0;
	public final int Y=1;
	public final int Z=2;
	
	private File data;
	private int numVerts=0;
	private Vertex[] vertices;
	
	// CONSTRUCTORS
	
	// This one accepts the lump path as a String
	public Lump04(String in) {
		data=new File(in);
		try {
			numVerts=getNumElements();
			vertices=new Vertex[numVerts];
			populateVertexList();
		} catch(java.io.FileNotFoundException e) {
			System.out.println("ERROR: File "+data+" not found!");
		} catch(java.io.IOException e) {
			System.out.println("ERROR: File "+data+" could not be read, ensure the file is not open in another program");
		}
	}
	
	// This one accepts the input file path as a File
	public Lump04(File in) {
		data=in;
		try {
			numVerts=getNumElements();
			vertices=new Vertex[numVerts];
			populateVertexList();
		} catch(java.io.FileNotFoundException e) {
			System.out.println("ERROR: File "+data+" not found!");
		} catch(java.io.IOException e) {
			System.out.println("ERROR: File "+data+" could not be read, ensure the file is not open in another program");
		}
	}
	
	// METHODS
	
	// +populateVertexList()
	// Parses all data into an array of Vertex.
	public void populateVertexList() throws java.io.FileNotFoundException, java.io.IOException {
		FileInputStream reader=new FileInputStream(data);
		try {
			for(int i=0;i<numVerts;i++) {
				byte[] datain=new byte[12];
				reader.read(datain);
				vertices[i]=new Vertex(datain);
			}
		} catch(InvalidVertexException e) {
			System.out.println("WARNING: Funny lump size in "+data+", ignoring last vertex.");
		}
		reader.close();
	}
	
	// ACCESSORS/MUTATORS
	
	// Returns the length (in bytes) of the lump
	public long getLength() {
		return data.length();
	}
	
	// Returns the number of vertices.
	public int getNumElements() {
		if(numVerts==0) {
			return (int)data.length()/12;
		} else {
			return numVerts;
		}
	}
	
	public Vertex getVertex(int i) {
		return vertices[i];
	}
	
	public Vertex[] getVertices() {
		return vertices;
	}
}