// Lump00 class

// This class handles and maintains an array of entities

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.util.Scanner;
import java.util.Date;

public class Lump00 {
	
	// INITIAL DATA DECLARATION AND DEFINITION OF CONSTANTS
	
	private File data;
	private int length;
	private int numEnts=0;
	private Entity[] entities;
	
	// CONSTRUCTORS
	
	// This one accepts the lump path as a String
	public Lump00(String in) {
		data=new File(in);
		try {
			FileInputStream reader=new FileInputStream(data); // reads the file
			byte[] thedata=new byte[(int)data.length()];
			reader.read(thedata);
			length=thedata.length;
			reader.close();
			numEnts=getNumElements(thedata);
			entities = new Entity[numEnts];
			populateEntityList(thedata);
		} catch(java.io.FileNotFoundException e) {
			System.out.println("ERROR: File "+data.getPath()+" not found!");
		} catch(java.io.IOException e) {
			System.out.println("ERROR: File "+data.getPath()+" could not be read, ensure the file is not open in another program");
		}
	}
	
	// This one accepts the input file path as a File
	public Lump00(File in) {
		data=in;
		try {
			FileInputStream reader=new FileInputStream(data); // reads the file
			byte[] thedata=new byte[(int)data.length()];
			reader.read(thedata);
			length=thedata.length;
			reader.close();
			numEnts=getNumElements(thedata);
			entities = new Entity[numEnts];
			populateEntityList(thedata);
		} catch(java.io.FileNotFoundException e) {
			System.out.println("ERROR: File "+data.getPath()+" not found!");
		} catch(java.io.IOException e) {
			System.out.println("ERROR: File "+data.getPath()+" could not be read, ensure the file is not open in another program");
		}
	}
	
	// This one accepts a Lump00 and copies it
	public Lump00(Lump00 in) {
		entities=new Entity[in.getNumElements()];
		numEnts=entities.length;
		for(int i=0;i<numEnts;i++) {
			entities[i]=new Entity(in.getEntity(i));
		}
	}
	
	// This one just takes an array of byte[]
	public Lump00(byte[] thedata) {
		length=thedata.length;
		numEnts=getNumElements(thedata);
		entities = new Entity[numEnts];
		populateEntityList(thedata);
	}
	
	public Lump00() {
		length=0;
		numEnts=0;
		entities = new Entity[0];
	}
	
	// METHODS
	
	// -populateEntityList()
	// Uses the instance data to populate an array of Entity classes. This
	// will eat a shitton of memory but works surprisingly fast even for
	// large maps. This method takes advantage of the constructors in the
	// Entity class, look there to see how deep the nested loops really go.
	// Even so, this method is a complete mess, so documentation is provided
	// whenever possible.
	// TODO: Rewrite this, try to make it faster.
	private void populateEntityList(byte[] thedata) {
		System.out.print("Populating entity list... ");
		Date begin=new Date();
		// I'd love to use Scanner here, but Scanner doesn't like using delimiters
		// with "{" or "}" in them, which I NEED
		char currentChar; // The current character being read in the file. This is necessary because
		                  // we need to know exactly when the { and } characters occur and capture
								// all text between them.
		int offset=0;
		for(int i=0;i<numEnts;i++) { // For every entity
			String current=""; // This will be the resulting entity, fed into the Entity class
			currentChar=(char)thedata[offset]; // begin reading the file
			while(currentChar!='{') { // Eat bytes until we find the beginning of an entity structure
				offset++;
				currentChar=(char)thedata[offset];
			}
			boolean inQuotes=false; // Keep track of whether or not we're in a set of quotation marks.
			// I came across a map where the idiot map maker used { and } in a value. This broke the code prior to revision 55.
			do {
				if(currentChar=='\"') {
					inQuotes=!inQuotes;
				}
				current+=currentChar+""; // adds characters to the current string
				offset++;
				currentChar=(char)thedata[offset];
			} while(currentChar!='}' || inQuotes); // Read bytes until we find the end of the current entity structure
			current+=currentChar+""; // adds the '}' to the current string
			entities[i]=new Entity(); // puts the resulting String into the constructor of the Entity class
			entities[i].setData(current);
		}
		Date end=new Date();
		System.out.println(end.getTime()-begin.getTime()+"ms");
	}

	// +add(String)
	// Parses the string and adds it to the entity list.
	// input Strings should be of the format:
	//   {0x0A
	//   "attribute" "value"0x0A
	//   "anotherattribute" "more values"0x0A
	//   etc.0x0A
	//   }
	public void add(String in) {
		Entity newEnt=new Entity(); // puts the String into the constructor of the Entity class
		newEnt.setData(in);
		add(newEnt);
	}
	
	// +add(Entity)
	// This is definitely the easiest to do. Adds an entity to the list
	// which is already of type Entity. It can be assumed it's already
	// been parsed and is valid the way it is.
	public void add(Entity in) {
		numEnts++;
		Entity[] newList=new Entity[numEnts];
		for(int i=0;i<numEnts-1;i++) {
			newList[i]=entities[i];
		}
		newList[numEnts-1]=in;
		entities=newList;
	}
	
	// +add(Lump00)
	// Adds every entity in the passed Lump00 into this one
	// This was the hardest lump to add the ability to combine, since all
	// references to other lumps are Strings contained in ATTRIBUTES. agh
	public void add(Lump00 in) throws java.io.FileNotFoundException, java.io.IOException {
		Entity[] newlist=new Entity[numEnts+in.getNumElements()];
		for(int i=0;i<numEnts;i++) { // copy the entities from this lump into a new array
			newlist[i]=entities[i];
		}
		File myLump14=new File(data.getParent()+"\\14 - Models.hex");
		int num14objs=(int)myLump14.length()/56;
		for(int i=0;i<in.getEntities().length;i++) {
			int oldModelNumber=in.getEntity(i).getModelNumber();
			if(oldModelNumber>0) {
				int newModelNumber=in.getEntity(i).getModelNumber()+num14objs-1; // Must subtract 1 from model number,
				in.getEntity(i).setAttribute("model", "*"+newModelNumber);       // since model 0 is the world, and that
			}                                                                   // is getting combined into one model
		}                                                                      // for both maps
		for(int i=0;i<in.getNumElements();i++) {
			newlist[i+numEnts]=in.getEntity(i);
		}
		
		numEnts=numEnts+in.getNumElements();
		entities=newlist;
	}
	
	// +deleteAllOfType(String, String)
	// Deletes all entities with attribute set to value
	public void deleteAllWithAttribute(String attribute, String value) {
		deleteEnts(findAllWithAttribute(attribute, value));
	}
	
	// +deleteEnts(int[])
	// Deletes the entities specified at all indices in the int[] array.
	public void deleteEnts(int[] in) {
		for(int i=0;i<in.length;i++) { // For each element in the array
			delete(in[i]); // Delete the element
			for(int j=i+1;j<in.length;j++) { // for each element that still needs to be deleted
				if(in[i]<in[j]) { // if the element that still needs deleting has an index higher than what was just deleted
					in[j]--; // Subtract one from that element's index to compensate for the changed list
				}
			}
		}
	}
	
	// +delete(int)
	// Deletes the entity at the specified index
	public void delete(int index) {
		Entity[] newList=new Entity[numEnts-1];
		for(int i=0;i<numEnts-1;i++) {
			if(i<index) {
				newList[i]=entities[i];
			} else {
				newList[i]=entities[i+1];
			}
		}
		numEnts-=1;
		entities=newList;
	}
	
	// +findAllWithAttribute(String, String)
	// Returns an array of indices of the entities with the specified attribute set to
	// the specified value
	public int[] findAllWithAttribute(String attribute, String value) {
		int[] indices;
		int num=0;
		for(int i=0;i<numEnts;i++) {
			if(entities[i].getAttribute(attribute).equalsIgnoreCase(value)) {
				num++;
			}
		}
		indices=new int[num];
		int current=0;
		for(int i=0;i<numEnts && current<num;i++) {
			if(entities[i].getAttribute(attribute).equalsIgnoreCase(value)) {
				indices[current]=i;
				current++;
			}
		}
		return indices;
	}
	
	// Save(String)
	// Saves the lump to the specified path.
	// Handling file I/O with Strings is generally a bad idea. If you have maybe a couple hundred
	// Strings to write then it'll probably be okay, but when you have on the order of 10,000 Strings
	// it gets VERY slow, even if you concatenate them all before writing.
	public void save(String path) {
		File newFile;
		if(path.substring(path.length()-4).equalsIgnoreCase(".map")) {
			newFile=new File(path);
		} else {
			newFile=new File(path+"\\00 - Entities.txt");
		}
		try {
			if(!newFile.exists()) {
				newFile.createNewFile();
			} else {
				newFile.delete();
				newFile.createNewFile();
			}
			
			// PrintWriter entityWriter=new PrintWriter(newFile);
			FileOutputStream entityWriter=new FileOutputStream(newFile);
			for(int i=0;i<numEnts;i++) {
				byte[] temp;
				if(!path.substring(path.length()-4).equals(".map")) {
					temp=new byte[2];
					temp[0]=(byte)'{';
					temp[1]=(byte)0x0A;
				} else {
					String tempString="{ // Entity "+i+"\n";
					temp=tempString.getBytes();
				}
				entityWriter.write(temp);
				entityWriter.write(entities[i].toString().getBytes());
				byte [] temp2=new byte[2];
				temp2[0]=(byte)'}';
				temp2[1]=(byte)0x0A;
				entityWriter.write(temp2);
			}
			if(!path.substring(path.length()-4).equals(".map")) {
				byte[] temp=new byte[1];
				temp[0]=(byte)0x00;
				entityWriter.write(temp); // The entities lump always ends with a 00 byte,
				                          // otherwise the game engine could start reading into
				                          // the next lump, looking for another entity. It's
				                          // kind of stupid that way, since lump sizes are
				                          // clearly defined in the BSP header.
			}
			entityWriter.close();
		} catch(java.io.IOException e) {
			System.out.println("ERROR: Could not save "+newFile+", ensure the file is not open in another program and the path "+path+" exists");
		}
	}
	
	public byte[] toByteArray() {
		int length=1;
		String[] entsAsStrings=new String[entities.length];
		for(int i=0;i<entities.length;i++) {
			entsAsStrings[i]=entities[i].toString();
			length+=entsAsStrings[i].length()+1;
		}
		byte[] out=new byte[length];
		int offset=0;
		for(int i=0;i<entities.length;i++) {
			for(int j=0;j<entsAsStrings[i].length();j++) {
				out[offset++]=(byte)entsAsStrings[i].charAt(j);
			}
			out[offset++]=(char)0x0A;
		}
		out[offset++]=(char)0x00;
		return out;
	}
	
	public void printents() {
		for(int i=0;i<entities.length;i++) {
			System.out.println(entities[i].toString());
		}
	}
	
	// save()
	// Saves the lump, overwriting the one data was read from
	public void save() {
		save(data.getParent());
	}
	
	// ACCESSORS/MUTATORS
		
	// Returns the length (in bytes) of the lump
	public int getLength() {
		return length;
	}
	
	// Returns the number of entities.
	public int getNumElements(byte[] thedata) {
		if (numEnts==0) {
			System.out.print("Counting entities... ");
			Date begin=new Date();
			int count=0;
			boolean inQuotes=false; // Keep track of whether or not we're in a set of quotation marks.
			// I came across a map where the idiot map maker used { and } in a value. This broke the code prior to revision 55.
			for(int i=0;i<thedata.length;i++) {
				if(inQuotes) {
					if(thedata[i]=='\"' && inQuotes) {
						inQuotes=false;
					}
				} else {
					if(thedata[i]=='\"') {
						inQuotes=true;
					} else {
						if(thedata[i] == '{') {
							count++;
						}
					}
				}
			}
			Date end=new Date();
			System.out.println(end.getTime()-begin.getTime()+"ms");
			return count;
		} else {
			return numEnts;
		}
	}
	
	public int getNumElements() {
		return numEnts;
	}
	
	// Returns a specific entity as an Entity object.
	public Entity getEntity(int i) {
		return entities[i];
	}
	
	// Returns a reference to the entities array
	public Entity[] getEntities() {
		return entities;
	}
}
