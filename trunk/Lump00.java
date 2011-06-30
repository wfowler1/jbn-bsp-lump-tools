import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.util.*;

public class Lump00 {
	
	FileInputStream data;
	Scanner dataCounter;
	File dataFile;
	
	//CONSTRUCTORS:
	
	// This one accepts the lump path as a String
	public Lump00(String in) {
		try {
			dataFile=new File(in);
			dataCounter=new Scanner(new File(in));
		} catch(java.io.FileNotFoundException e) {
			System.out.println("SHIT");
		}
	}
	
	// This one accepts the input file path as a FileInputStream
	public Lump00(FileInputStream in) {
		data=in;
	}
	
	// This one accepts the input file path as a File
	public Lump00(File in) {
		try {
			data=new FileInputStream(in);
		} catch(java.io.FileNotFoundException e) {
			System.out.println("SHIT");
		}
	}
}