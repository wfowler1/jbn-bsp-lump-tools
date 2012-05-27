// BSPWriter class

// Writes a BSP file from an NFBSP class.

import java.io.File;
import java.io.FileOutputStream;

public class BSPWriter {

	// INITIAL DATA DECLARATION AND DEFINITION OF CONSTANTS

	private NFBSP toWrite;
	private File newBSP;
	
	// CONSTRUCTORS
	public BSPWriter(NFBSP in) {
		this.toWrite=in;
	}
	
	// METHODS
	public void writeFile(String path) {
		writeFile(new File(path));
	}
	
	public void writeFile(File where) {
		newBSP=where;
		try {
			File absolutepath=new File(newBSP.getParent()+"\\");
			if(!absolutepath.exists()) {
				absolutepath.mkdir();
			}
			if(!newBSP.exists()) {
				newBSP.createNewFile();
			} else {
				newBSP.delete();
				newBSP.createNewFile();
			}
			
			byte[][] alldata=new byte[18][];
			
			alldata[0]=toWrite.getLump00().toByteArray();
			alldata[1]=toWrite.getLump01().toByteArray();
			alldata[2]=toWrite.getLump02().toByteArray();
			alldata[3]=toWrite.getLump03().toByteArray();
			alldata[4]=toWrite.getLump04().toByteArray();
			alldata[5]=toWrite.getLump05().toByteArray();
			alldata[6]=toWrite.getLump06().toByteArray();
			alldata[7]=toWrite.getLump07().toByteArray();
			alldata[8]=toWrite.getLump08().toByteArray();
			alldata[9]=toWrite.getLump09().toByteArray();
			alldata[10]=toWrite.getLump10().toByteArray();
			alldata[11]=toWrite.getLump11().toByteArray();
			alldata[12]=toWrite.getLump12().toByteArray();
			alldata[13]=toWrite.getLump13().toByteArray();
			alldata[14]=toWrite.getLump14().toByteArray();
			alldata[15]=toWrite.getLump15().toByteArray();
			alldata[16]=toWrite.getLump16().toByteArray();
			alldata[17]=toWrite.getLump17().toByteArray();
			
			byte[] header=new byte[148];
			header[0]=0x2A;
			
			int[] offsets=new int[alldata.length];
			offsets[0]=148;
			header[4] =(byte)((offsets[0] >> 0) & 0xFF);
			header[5] =(byte)((offsets[0] >> 8) & 0xFF);
			header[6] =(byte)((offsets[0] >> 16) & 0xFF);
			header[7] =(byte)((offsets[0] >> 24) & 0xFF);
			header[8] =(byte)((alldata[0].length >> 0) & 0xFF);
			header[9] =(byte)((alldata[0].length >> 8) & 0xFF);
			header[10]=(byte)((alldata[0].length >> 16) & 0xFF);
			header[11]=(byte)((alldata[0].length >> 24) & 0xFF);
			
			int alldatalength=148+alldata[0].length;
			
			for(int i=1;i<alldata.length;i++) {
				offsets[i]=offsets[i-1]+alldata[i-1].length;
				header[4+(i*8)] =(byte)((offsets[i] >> 0) & 0xFF);
				header[5+(i*8)] =(byte)((offsets[i] >> 8) & 0xFF);
				header[6+(i*8)] =(byte)((offsets[i] >> 16) & 0xFF);
				header[7+(i*8)] =(byte)((offsets[i] >> 24) & 0xFF);
				header[8+(i*8)] =(byte)((alldata[i].length >> 0) & 0xFF);
				header[9+(i*8)] =(byte)((alldata[i].length >> 8) & 0xFF);
				header[10+(i*8)]=(byte)((alldata[i].length >> 16) & 0xFF);
				header[11+(i*8)]=(byte)((alldata[i].length >> 24) & 0xFF);
				alldatalength+=alldata[i].length;
			}
			
			byte[] BSPFileContents = new byte[alldatalength];
			
			for(int i=0;i<header.length;i++) {
				BSPFileContents[i]=header[i];
			}
			
			for(int i=0;i<alldata.length;i++) {
				for(int j=0;j<alldata[i].length;j++) {
					BSPFileContents[offsets[i]+j]=alldata[i][j];
				}
			}
			
			FileOutputStream fileWriter = new FileOutputStream(newBSP);
			System.out.println("Writing "+BSPFileContents.length+" bytes");
			fileWriter.write(BSPFileContents);
			fileWriter.close();
		} catch(java.io.IOException e) {
			System.out.println("Unable to create file!");
		}
	}
	
	// ACCESSORS/MUTATORS
	
}