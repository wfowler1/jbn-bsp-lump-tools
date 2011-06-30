// Entity class

// This class holds data on ONE entity. It's only really useful when
// used in an array along with many others. Each value is stored as
// a separate attribute, in an array.

// A small note, I was tempted to use an Attribute class and just
// make an array of that, but that's breaking it down too far.

import java.util.Scanner; // Perfect for String handling
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class Entity {
	
	// INITIAL DATA DECLARATION AND DEFINITION OF CONSTANTS
	
	private String[] attributes;
	private int numAttributes=0;
	
	// CONSTRUCTORS
	
	public Entity(byte[] in) {
		String me="";
		for(int i=0;i<in.length;i++) {
			me+=(char)in[i];
		}
		setData(me);
	}

	public Entity(String me) {
		setData(me);
	}
	
	// METHODS
	
	// ACCESSORS/MUTATORS
	
	// +setData(String)
	// Used by constructors and can be used by outside classes to set or change the data
	// in this specific entity. It takes a String and parses it into the attributes array.
	// This input String CAN include the { and } from the entity structure, in that it 
	// won't cause any errors in the program. However if and when you want to write the
	// entities back into a lump you must remember whether you included them or not. The
	// default behavior is not to include them.
	public void setData(String in) {
		Scanner reader=new Scanner(in);
		reader.useDelimiter((char)0x0A+"");
		Scanner counter=new Scanner(in);
		counter.useDelimiter((char)0x0A+"");
		
		numAttributes=0;
		while(counter.hasNext()) {
			counter.next();
			numAttributes++;
		}
		
		attributes=new String[numAttributes];
		for(int i=0;i<numAttributes;i++) {
			String current=reader.next();
			// This will trim all the 0D bytes before the 0A delimiters if they exist,
			// since the Windows newline sequence is 0D0A and all its text editors use
			// that. This keeps the data from getting confusing to this program, but
			// also saves a small amount of space in the output map itself.
			if(current.charAt(current.length()-1)==(char)0x0D) {
				current=current.substring(0,current.length()-1);
			}
			attributes[i]=current;
		}
	}

	// +getEntity()
	// Returns the entity as an ASCII entity structure. The output of this method
	// reads as a complete entity that could be put into a map with no problems,
	// and that will be the primary use case for this method. Be sure to add the
	// newlines around the entity if using this method to create a new entities
	// lump file.
	public String getEntity() {
		String out="{\n";
		for(int i=0;i<attributes.length;i++) {
			out+=attributes[i]+"\n";
		}
		return out+"}";
	}
	
	// +printEntity()
	// Simply prints
	public void printEntity() {
		System.out.println(getEntity());
	}
	
	// +getAttribute(String)
	// Takes in an attribute as a String and returns the value of that attribute,
	// if it exists. If not, throw a not found exception.
	public String getAttribute(String attribute) throws AttributeNotFoundException {
		String output="";
		for(int i=0;i<numAttributes;i++) {
			try {
				if(attributes[i].substring(0,attribute.length()+2).compareToIgnoreCase("\""+attribute+"\"")==0) {
					output=attributes[i].substring(attribute.length()+4,attributes[i].length()-1);
					break;
				}
			} catch(StringIndexOutOfBoundsException e) { // for cases where the whole String is shorter than
				;                                         // the name of the attribute we're looking for
			}
		}
		if (output.equals("")) {
			throw new AttributeNotFoundException();
		}
		return output;
	}
	
	// +getModelNumber()
	// If there's a model number in the attributes list, this method fetches it
	// and returns it. If there is no model defined, or it's not a numerical 
	// value, then -1 is returned.
	public int getModelNumber() {
		int number=-1;
		for(int i=0;i<numAttributes;i++) {
			try {
				if(attributes[i].substring(0,7).compareToIgnoreCase("\"model\"")==0) {
					// This substring skips the "model" "* and gets to the number
					number=Integer.parseInt(attributes[i].substring(10,attributes[i].length()-1));
					break;
				}
			} catch(StringIndexOutOfBoundsException e) {
				;
			} catch(NumberFormatException e) { 
				;
			}
		}
		
		return number;
	}
	
	// setAttribute()
	// Set an attribute. If it doesn't exist, it is added. If it does, it is
	// overwritten with the new one, since that's much easier to do than edit
	// the preexisting one.
	public void setAttribute(String attribute, String value) {
		boolean done=false;
		for(int i=0;i<numAttributes && !done;i++) {
			try {
				if(attributes[i].substring(0,attribute.length()+2).compareToIgnoreCase("\""+attribute+"\"")==0) {
					attributes[i]="\""+attribute+"\" \""+value+"\"";
					done=true;
				}
			} catch(StringIndexOutOfBoundsException e) {
				;
			}
		}
		if(!done) {
			String[] newAttributes=new String[attributes.length+1];
			for(int i=0;i<attributes.length;i++) {
				newAttributes[i]=attributes[i];
			}
			newAttributes[attributes.length]="\""+attribute+"\" \""+value+"\"";
			numAttributes++;
			attributes=newAttributes;
		}
	}
}