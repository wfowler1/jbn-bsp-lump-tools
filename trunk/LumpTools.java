// LumpTools class (to be replaced with a GUI)

// Sets up the basic interface for loading a set of BSP lumps into the program.
// Deprecate this, nobody wants a console-based program.

// TODO list for code:
// complete add() methods for all Lump classes after and including Lump12
// create a pixel class to be used by Lump10 class
// add accessors and mutators for all helper classes used by Lump classes after and including Lump12
// complete save() methods for everything after and including Lump12
// write Exception classes for invalid inputs

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
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

		// System.out.print("Path to other lumps folder: ");
		// String otherfilepath=keyboard.nextLine();
		
		// NFBSP otherBSP = new NFBSP(otherfilepath);
		
		// myBSP.combineBSP(otherBSP);
		
		System.out.print("Path to save lumps to: ");
		myBSP.saveLumps(keyboard.nextLine());
		
	}
}
