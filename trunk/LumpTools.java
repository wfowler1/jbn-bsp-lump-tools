// LumpTools class (to be replaced with a GUI)

// Sets up the basic interface for loading a set of BSP lumps into the program.
// Deprecate this, nobody wants a console-based program.

// TODO list for code:
// Make sure to warn user if a lump doesn't have the proper size.
// Code in a lump separator?

import java.util.Scanner;

public class LumpTools {
	public static void main(String[] args) throws java.io.FileNotFoundException, java.io.IOException {
		Scanner keyboard = new Scanner(System.in);

		System.out.print("Path to lumps folder: ");
		String filepath=keyboard.nextLine();
		
		// All lump analysis and manipulation is handled through the
		// NFBSP class. That way if something is wrong once, it's wrong
		// every time and only needs to be fixed once.
		NFBSP myBSP = new NFBSP(filepath);

		System.out.print("Path to other lumps folder: ");
		String otherfilepath=keyboard.nextLine();
		
		NFBSP otherBSP = new NFBSP(otherfilepath);
		
		System.out.println("\nCombining BSP data...");
		myBSP.combineBSP(otherBSP);
		
		System.out.print("Path to save lumps to: ");
		String savefolder=keyboard.nextLine();
		myBSP.saveLumps(savefolder);
		
		NFBSP newBSP=new NFBSP(savefolder);
	}
}
