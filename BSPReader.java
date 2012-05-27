// BSPReader class

// Does the actual reading of the BSP file and takes appropriate
// action based primarily on BSP version number. It also feeds all
// appropriate data to the different BSP version classes. This
// does not actually do any data processing or analysis, it simply
// reads from the hard drive and sends the data where it needs to go.
// Deprecates the LS class, and doesn't create a file for every lump!

import java.io.File;
import java.io.FileInputStream;

public class BSPReader {
	
	// INITIAL DATA DECLARATION AND DEFINITION OF CONSTANTS
	
	private File BSP; // Where's my BSP?
	private String folder;
	
	public final int OFFSET=0;
	public final int LENGTH=1;
	
	protected NFBSP thebsp;
	
	// CONSTRUCTORS
	
	// Takes a String in and assumes it is a path. That path is the path to the file
	// that is the BSP and its name minus the .BSP extension is assumed to be the folder.
	// See comments below for clarification. Case does not matter on the extension, so it
	// could be .BSP, .bsp, etc.
	public BSPReader(String in) {
		BSP=new File(in); // The read String points directly to the BSP file.
		if(!BSP.exists()) {
			System.out.println("Unable to open source BSP file, please ensure the BSP exists.");
		} else {
			folder=BSP.getParent(); // The read string minus the .BSP is the lumps folder
			if(folder==null) {
				folder="";
			}
		}
	}
	
	public BSPReader(File in) {
		BSP=in;
		if(!BSP.exists()) {
			System.out.println("Unable to open source BSP file, please ensure the BSP exists.");
		} else {
			folder=BSP.getParent(); // The read string minus the .BSP is the lumps folder
			if(folder==null) {
				folder="";
			}
		}
	}
	
	// METHODS
	
	public NFBSP readBSP() {
		try {
			int version=getVersion();
			byte[] read=new byte[4];
			int offset;
			int length;
			switch(version) {
				case 42: // JBN
					System.out.println("BSP v42 found (Nightfire)");
					FileInputStream offsetReader = new FileInputStream(BSP);
					thebsp = new NFBSP();
					offsetReader.skip(4); // Skip the file header, putting the reader into the offset/length pairs
					
					// Lump 00
					offsetReader.read(read); // Read 4 bytes
					offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
					offsetReader.read(read); // Read 4 more bytes
					length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
					thebsp.setLump00(readLump(offset, length));
							
					// Lump 01
					offsetReader.read(read); // Read 4 bytes
					offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
					offsetReader.read(read); // Read 4 more bytes
					length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
					thebsp.setLump01(readLump(offset, length));
							
					// Lump 02
					offsetReader.read(read); // Read 4 bytes
					offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
					offsetReader.read(read); // Read 4 more bytes
					length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
					thebsp.setLump02(readLump(offset, length));
							
					// Lump 03
					offsetReader.read(read); // Read 4 bytes
					offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
					offsetReader.read(read); // Read 4 more bytes
					length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
					thebsp.setLump03(readLump(offset, length));
							
					// Lump 04
					offsetReader.read(read); // Read 4 bytes
					offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
					offsetReader.read(read); // Read 4 more bytes
					length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
					thebsp.setLump04(readLump(offset, length));
							
					// Lump 05
					offsetReader.read(read); // Read 4 bytes
					offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
					offsetReader.read(read); // Read 4 more bytes
					length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
					thebsp.setLump05(readLump(offset, length));
							
					// Lump 06
					offsetReader.read(read); // Read 4 bytes
					offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
					offsetReader.read(read); // Read 4 more bytes
					length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
					thebsp.setLump06(readLump(offset, length));
					
					offsetReader.skip(8);
							
					// Lump 08
					offsetReader.read(read); // Read 4 bytes
					offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
					offsetReader.read(read); // Read 4 more bytes
					length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
					thebsp.setLump08(readLump(offset, length));
					
					offsetReader.skip(8);
							
					// Lump 10
					offsetReader.read(read); // Read 4 bytes
					offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
					offsetReader.read(read); // Read 4 more bytes
					length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
					thebsp.setLump10(readLump(offset, length));
					
					offsetReader.skip(8);
					
					// Lump 12
					offsetReader.read(read); // Read 4 bytes
					offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
					offsetReader.read(read); // Read 4 more bytes
					length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
					thebsp.setLump12(readLump(offset, length));
					
					// Lump 13
					offsetReader.read(read); // Read 4 bytes
					offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
					offsetReader.read(read); // Read 4 more bytes
					length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
					thebsp.setLump13(readLump(offset, length));
					
					// Lump 14
					offsetReader.read(read); // Read 4 bytes
					offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
					offsetReader.read(read); // Read 4 more bytes
					length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
					thebsp.setLump14(readLump(offset, length));
		
					// Lump 15
					offsetReader.read(read); // Read 4 bytes
					offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
					offsetReader.read(read); // Read 4 more bytes
					length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
					thebsp.setLump15(readLump(offset, length));
					
					// Lump 16
					offsetReader.read(read); // Read 4 bytes
					offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
					offsetReader.read(read); // Read 4 more bytes
					length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
					thebsp.setLump16(readLump(offset, length));
					
					// Lump 17
					offsetReader.read(read); // Read 4 bytes
					offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
					offsetReader.read(read); // Read 4 more bytes
					length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
					thebsp.setLump17(readLump(offset, length));
					
					offsetReader.close();
					FileInputStream SecondReader = new FileInputStream(BSP);
					SecondReader.skip(76);
							
					// Lump 09
					SecondReader.read(read); // Read 4 bytes
					offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
					SecondReader.read(read); // Read 4 more bytes
					length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
					thebsp.setLump09(readLump(offset, length), thebsp.getLump14().getModel(0).getNumFaces());
					
					SecondReader.skip(8);
					
					// Lump 11
					SecondReader.read(read); // Read 4 bytes
					offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
					SecondReader.read(read); // Read 4 more bytes
					length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
					thebsp.setLump11(readLump(offset, length), thebsp.getLump14().getModel(0).getNumLeafs());
					
					SecondReader.close();
					FileInputStream ThirdReader = new FileInputStream(BSP);
					ThirdReader.skip(60);
							
					// Lump 07
					ThirdReader.read(read); // Read 4 bytes
					offset=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
					ThirdReader.read(read); // Read 4 more bytes
					length=(read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
					thebsp.setLump07(readLump(offset, length), thebsp.getLump11().getLeaf(2).getPVS());
					
					ThirdReader.close();
					thebsp.printBSPReport();
					break;
				default:
					System.out.println("Not a BSP version 42! Sorry."+(char)0x0D+(char)0x0A+"If trying to decompile, please use the decompiler branch!");
			}
		} catch(java.io.IOException e) {
			System.out.println("Unable to access BSP file! Is it open in another program?");
		}
		return thebsp;
	}
	
	// +readLump(int, int)
	// Reads the lump length bytes long at offset in the file
	public byte[] readLump(int offset, int length) {
		byte[] input=new byte[length];
		try {
			FileInputStream fileReader=new FileInputStream(BSP);
			fileReader.skip(offset);
			fileReader.read(input);
			fileReader.close();
		} catch(java.io.IOException e) {
			System.out.println("Unknown error reading BSP, it was working before!");
		}
		return input;
	}
				
	// ACCESSORS/MUTATORS
	public int getVersion() throws java.io.IOException {
		byte[] read=new byte[4];
		FileInputStream versionNumberReader=new FileInputStream(BSP); // This filestream will be used to read version number only
		versionNumberReader.read(read);
		versionNumberReader.close();
		return (read[3] << 24) | ((read[2] & 0xff) << 16) | ((read[1] & 0xff) << 8) | (read[0] & 0xff);
	}
	
	public NFBSP getBSP() {
		return thebsp;
	}
}
