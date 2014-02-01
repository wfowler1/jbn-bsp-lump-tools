// MAPMaker class
// Takes Entities classes and uses map writer classes to output editor mapfiles.
using System;
using System.IO;
using System.Runtime.Serialization.Formatters.Binary;

public class MAPMaker {
	
	// INITIAL DATA DECLARATION AND DEFINITION OF CONSTANTS
	
	// CONSTRUCTORS
	
	// METHODS
	public static void outputMaps(Entities data, string mapname, string mapfolder)
	{
		MAP510Writer GCMAPMaker;
		if (Settings.outputFolder.Equals("default"))
		{
			GCMAPMaker = new MAP510Writer(data, mapfolder + mapname);
		}
		else
		{
			GCMAPMaker = new MAP510Writer(data, Settings.outputFolder + "\\" + mapname);
		}
		GCMAPMaker.write();
	}
	
	// If only one thread is allowed to use new Object() method at once, only one map will be saved at once, meaning less
	// jumping hard drive seek time used.
	public static void write(byte[] data, string destinationString) {
		try
		{
			if (!destinationString.Substring(destinationString.Length - 4).ToUpper().Equals(".map".ToUpper()) && !destinationString.Substring(destinationString.Length - 4).ToUpper().Equals(".vmf".ToUpper()))
			{
				destinationString = destinationString + ".map";
			}
		}
		catch (System.ArgumentOutOfRangeException)
		{
			destinationString = destinationString + ".map";
		}
		Console.WriteLine("Saving " + destinationString+"...");
		try {
			FileStream stream = new FileStream(destinationString, FileMode.Create, FileAccess.Write);
			BinaryWriter bw = new BinaryWriter(stream);
			stream.Seek(0, SeekOrigin.Begin);
			bw.Write(data);
			bw.Close();
		} catch(System.IO.IOException e) {
			Console.WriteLine("ERROR: Could not save "+destinationString+", ensure the file is not open in another program.");
			throw e;
		}
	}
	
	// ACCESSORS/MUTATORS
	
	// INTERNAL CLASSES
}