// LumpTools class (to be replaced with a GUI)

// Sets up the basic interface for loading a set of BSP lumps into the program.
// Deprecate this, nobody wants a console-based program.

// TODO list for code:
// Move all BSP lump combining code to NFBSP class
// Move lump saving code to NFBSP class, this will make saving unnecessary between steps
// in the NFBSP class, create methods to turn ints/floats into byte arrays and vice versa, then change all the loading/saving methods to use those
// Make sure to warn user if a lump doesn't have the proper size.
// Code in a lump separator?

import java.util.Scanner;
import java.util.Date;

public class LumpTools {
	public static void main(String[] args) throws java.io.FileNotFoundException, java.io.IOException {
		Scanner keyboard = new Scanner(System.in);

		System.out.print("Path to lumps folder: ");
		String filepath=keyboard.nextLine();
		
		// All lump analysis and manipulation is handled through the
		// NFBSP class. That way if something is wrong once, it's wrong
		// every time and only needs to be fixed once.
		NFBSP myBSP = new NFBSP(filepath);

		//System.out.print("Path to other lumps folder: ");
		//String otherfilepath=keyboard.nextLine();
		
		//NFBSP otherBSP = new NFBSP(otherfilepath);
		
		//System.out.println("\nCombining BSP data...");
		//myBSP.combineBSP(otherBSP);
		
		// Force planar decompilation
		/*for(int i=0;i<myBSP.getLump09().getNumElements();i++) {
			myBSP.getLump09().getFace(i).setVert(0);
			myBSP.getLump09().getFace(i).setNumVerts(0);
			myBSP.getLump09().getFace(i).setMeshs(0);
			myBSP.getLump09().getFace(i).setNumMeshs(0);
		}*/
		
		System.out.print("Path to save decompiled map to: ");
		String decompfolder=keyboard.nextLine();
		myBSP.decompile(decompfolder);
		
		//System.out.println("Optimizing BSP");
		//myBSP.optimizeBSP();
		
		//System.out.print("Path to save lumps to: ");
		//String savefolder=keyboard.nextLine();
		//myBSP.saveLumps(savefolder);
		
		//NFBSP newBSP=new NFBSP(savefolder);
	}
}
