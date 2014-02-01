using System.Collections.Generic;

// MAP510Writer class
//
// Writes a Gearcraft .MAP file from a passed Entities object
using System;

public class MAP510Writer {
	
	// INITIAL DATA DECLARATION AND DEFINITION OF CONSTANTS
	
	public const int A = 0;
	public const int B = 1;
	public const int C = 2;
	
	public const int X = 0;
	public const int Y = 1;
	public const int Z = 2;
	
	// These are lowercase so as not to conflict with A B and C
	// Light entity attributes; red, green, blue, strength (can't use i for intensity :P)
	public const int r = 0;
	public const int g = 1;
	public const int b = 2;
	public const int s = 3;
	
	private string path;
	private Entities data;
	
	private int currentEntity;
	
	private static bool ctfEnts = false;
	
	// CONSTRUCTORS
	
	public MAP510Writer(Entities from, string to) {
		this.data = from;
		this.path = to;
	}
	
	// METHODS
	
	// write()
	// Saves the lump to the specified path.
	// Handling file I/O with Strings is generally a bad idea. If you have maybe a couple hundred
	// Strings to write then it'll probably be okay, but when you have on the order of 10,000 Strings
	// it gets VERY slow, even if you concatenate them all before writing.
	public virtual void write()
	{
		// Preprocessing entity corrections
		if (!Settings.noEntCorrection) {
			// Correct some attributes of entities
			for (int i = 0; i < data.Count; i++)
			{
				Entity current = data[i];
				current = ent42ToEntM510(current);
			}
		}
		
		byte[][] entityBytes = new byte[data.Count][];
		int totalLength = 0;
		for (currentEntity = 0; currentEntity < data.Count; currentEntity++)
		{
			try
			{
				entityBytes[currentEntity] = entityToByteArray(data[currentEntity], currentEntity);
			}
			catch (System.IndexOutOfRangeException)
			{
				// This happens when entities are added after the array is made
				byte[][] newList = new byte[data.Count][]; // Create a new array with the new length
				for (int j = 0; j < entityBytes.Length; j++)
				{
					newList[j] = entityBytes[j];
				}
				newList[currentEntity] = entityToByteArray(data[currentEntity], currentEntity);
				entityBytes = newList;
			}
			totalLength += entityBytes[currentEntity].Length;
		}
		byte[] allEnts = new byte[totalLength];
		int offset = 0;
		for (int i = 0; i < data.Count; i++)
		{
			for (int j = 0; j < entityBytes[i].Length; j++)
			{
				allEnts[offset + j] = entityBytes[i][j];
			}
			offset += entityBytes[i].Length;
		}
		MAPMaker.write(allEnts, path);
	}
	
	// -entityToByteArray()
	// Converts the entity and its brushes into byte arrays rather than Strings,
	// which can then be written to a file much faster. Concatenating Strings is
	// a costly operation, especially when hundreds of thousands of Strings are
	// in play. This is one of two parts to writing a file quickly. The second
	// part is to call the FileOutputStream.write() method only once, with a
	// gigantic array, rather than several times with many small arrays. File I/O
	// from a hard drive is another costly operation, best done by handling
	// massive amounts of data in one go, rather than tiny amounts of data thousands
	// of times.
	private byte[] entityToByteArray(Entity inputData, int num)
	{
		byte[] outputData;
		Vector3D origin = inputData.Origin;
		if (inputData["classname"].Equals("worldspawn", StringComparison.InvariantCultureIgnoreCase))
		{
			inputData["mapversion"] = "510";
			if (ctfEnts)
			{
				inputData["defaultctf"] = "1";
			}
		}
		string temp = "{ // Entity "+num;
		int len = temp.Length+5; // Closing curly brace and two newlines
		// Get the lengths of all attributes together
		//for (int i = 0; i < inputData.Attributes.Count; i++) {
		foreach (string key in inputData.Attributes.Keys) {
			len += key.Length + inputData[key].Length + 7; // Four quotes, a space and a newline
		}
		outputData = new byte[len];
		int offset = 0;
		for (int i = 0; i < temp.Length; i++) {
			outputData[offset++] = (byte)temp[i];
		}
		outputData[offset++] = (byte)0x0D;
		outputData[offset++] = (byte)0x0A;
		foreach (string key in inputData.Attributes.Keys) {
			// For each attribute
			outputData[offset++] = (byte)'\"'; // 1
			for (int j = 0; j < key.Length; j++) {
				// Then for each byte in the attribute
				outputData[j + offset] = (byte) key[j]; // add it to the output array
			}
			offset += key.Length;
			outputData[offset++] = (byte)'\"'; // 2
			outputData[offset++] = (byte)' '; // 3
			outputData[offset++] = (byte)'\"'; // 4
			for (int j = 0; j < inputData.Attributes[key].Length; j++) {
				// Then for each byte in the attribute
				outputData[j + offset] = (byte) inputData.Attributes[key][j]; // add it to the output array
			}
			offset += inputData.Attributes[key].Length;
			outputData[offset++] = (byte)'\"'; // 5
			outputData[offset++] = (byte)0x0D; // 6
			outputData[offset++] = (byte)0x0A; // 7
		}
		int brushArraySize = 0;
		byte[][] brushes = new byte[inputData.Brushes.Count][];
		for (int j = 0; j < inputData.Brushes.Count; j++)
		{
			brushes[j] = brushToByteArray(inputData.Brushes[j], j);
			brushArraySize += brushes[j].Length;
		}
		int brushoffset = 0;
		byte[] brushArray = new byte[brushArraySize];
		for (int j = 0; j < inputData.Brushes.Count; j++) {
			// For each brush in the entity
			for (int k = 0; k < brushes[j].Length; k++)
			{
				brushArray[brushoffset + k] = brushes[j][k];
			}
			brushoffset += brushes[j].Length;
		}
		if (brushArray.Length != 0)
		{
			len += brushArray.Length;
			byte[] newOut = new byte[len];
			for (int j = 0; j < outputData.Length; j++)
			{
				newOut[j] = outputData[j];
			}
			for (int j = 0; j < brushArray.Length; j++)
			{
				newOut[j + outputData.Length - 3] = brushArray[j];
			}
			offset += brushArray.Length;
			outputData = newOut;
		}
		outputData[offset++] = (byte)'}';
		outputData[offset++] = (byte)0x0D;
		outputData[offset++] = (byte)0x0A;
		return outputData;
	}
	
	private byte[] brushToByteArray(MAPBrush inputData, int num) {
		if (inputData.NumSides < 4) {
			// Can't create a brush with less than 4 sides
			Console.WriteLine("WARNING: Tried to create brush from " + inputData.NumSides + " sides!");
			return new byte[0];
		}
		string brush = "{ // Brush " + num + (char) 0x0D + (char) 0x0A;
		if ((inputData.Detail) && currentEntity == 0)
		{
			brush += ("\"BRUSHFLAGS\" \"DETAIL\"" + (char) 0x0D + (char) 0x0A);
		}
		for (int i = 0; i < inputData.NumSides; i++)
		{
			brush += (brushSideToString(inputData[i]) + (char) 0x0D + (char) 0x0A);
		}
		brush += ("}" + (char) 0x0D + (char) 0x0A);
		if (brush.Length < 45) {
			// Any brush this short contains no sides.
			Console.WriteLine("WARNING: Brush with no sides being written! Oh no!");
			return new byte[0];
		}
		else
		{
			byte[] brushbytes = new byte[brush.Length];
			for (int i = 0; i < brush.Length; i++)
			{
				brushbytes[i] = (byte) brush[i];
			}
			return brushbytes;
		}
	}
	
	private string brushSideToString(MAPBrushSide inputData)
	{
		try
		{
			Vector3D[] triangle = inputData.Triangle;
			string texture = inputData.Texture;
			Vector3D textureS = inputData.TextureS;
			Vector3D textureT = inputData.TextureT;
			double textureShiftS = inputData.TextureShiftS;
			double textureShiftT = inputData.TextureShiftT;
			float texRot = inputData.TexRot;
			double texScaleX = inputData.TexScaleX;
			double texScaleY = inputData.TexScaleY;
			int flags = inputData.Flags;
			string material = inputData.Material;
			double lgtScale = inputData.LgtScale;
			double lgtRot = inputData.LgtRot;
			if(Double.IsInfinity(texScaleX) || Double.IsNaN(texScaleX)) {
				texScaleX = 1;
			}
			if(Double.IsInfinity(texScaleY) || Double.IsNaN(texScaleY)) {
				texScaleY = 1;
			}
			if(Double.IsInfinity(textureShiftS) || Double.IsNaN(textureShiftS)) {
				textureShiftS = 0;
			}
			if(Double.IsInfinity(textureShiftT) || Double.IsNaN(textureShiftT)) {
				textureShiftT = 0;
			}
			if(Double.IsInfinity(textureS.X) || Double.IsNaN(textureS.X) || Double.IsInfinity(textureS.Y) || Double.IsNaN(textureS.Y) || Double.IsInfinity(textureS.Z) || Double.IsNaN(textureS.Z)) {
				textureS = TexInfo.textureAxisFromPlane(inputData.Plane)[0];
			}
			if(Double.IsInfinity(textureT.X) || Double.IsNaN(textureT.X) || Double.IsInfinity(textureT.Y) || Double.IsNaN(textureT.Y) || Double.IsInfinity(textureT.Z) || Double.IsNaN(textureT.Z)) {
				textureT = TexInfo.textureAxisFromPlane(inputData.Plane)[1];
			}
			if (Settings.roundNums)
			{
				return "( " + Math.Round(triangle[0].X, 6, MidpointRounding.AwayFromZero) + " " + Math.Round(triangle[0].Y, 6, MidpointRounding.AwayFromZero) + " " + Math.Round(triangle[0].Z, 6, MidpointRounding.AwayFromZero) + " ) " + 
					   "( " + Math.Round(triangle[1].X, 6, MidpointRounding.AwayFromZero) + " " + Math.Round(triangle[1].Y, 6, MidpointRounding.AwayFromZero) + " " + Math.Round(triangle[1].Z, 6, MidpointRounding.AwayFromZero) + " ) " + 
					   "( " + Math.Round(triangle[2].X, 6, MidpointRounding.AwayFromZero) + " " + Math.Round(triangle[2].Y, 6, MidpointRounding.AwayFromZero) + " " + Math.Round(triangle[2].Z, 6, MidpointRounding.AwayFromZero) + " ) " + 
					   texture + 
					   " [ " + Math.Round(textureS.X, 6, MidpointRounding.AwayFromZero) + " " + Math.Round(textureS.Y, 6, MidpointRounding.AwayFromZero) + " " + Math.Round(textureS.Z, 6, MidpointRounding.AwayFromZero) + " " + Math.Round(textureShiftS, MidpointRounding.AwayFromZero) + " ]" + 
					   " [ " + Math.Round(textureT.X, 6, MidpointRounding.AwayFromZero) + " " + Math.Round(textureT.Y, 6, MidpointRounding.AwayFromZero) + " " + Math.Round(textureT.Z, 6, MidpointRounding.AwayFromZero) + " " + Math.Round(textureShiftT, MidpointRounding.AwayFromZero) + " ] " + 
					   Math.Round(texRot, 4, MidpointRounding.AwayFromZero) + " " + Math.Round(texScaleX, 4, MidpointRounding.AwayFromZero) + " " + Math.Round(texScaleY, 4, MidpointRounding.AwayFromZero) + " " + flags + " " + material + " [ " + Math.Round(lgtScale, 4, MidpointRounding.AwayFromZero) + " " + Math.Round(lgtRot, 4, MidpointRounding.AwayFromZero) + " ]";
			}
			else
			{
				return "( " + triangle[0].X + " " + triangle[0].Y + " " + triangle[0].Z + " ) " + "( " + triangle[1].X + " " + triangle[1].Y + " " + triangle[1].Z + " ) " + "( " + triangle[2].X + " " + triangle[2].Y + " " + triangle[2].Z + " ) " + texture + " [ " + textureS.X + " " + textureS.Y + " " + textureS.Z + " " + textureShiftS + " ]" + " [ " + textureT.X + " " + textureT.Y + " " + textureT.Z + " " + textureShiftT + " ] " + texRot + " " + texScaleX + " " + texScaleY + " " + flags + " " + material + " [ " + lgtScale + " " + lgtRot + " ]";
			}
		}
		catch (System.NullReferenceException)
		{
			Console.WriteLine("WARNING: Side with bad data! Not exported!");
			return "";
		}
	}
	
	public virtual Entity ent42ToEntM510(Entity inputData)
	{
		if (inputData.BrushBased)
		{
			Vector3D origin = inputData.Origin;
			inputData.Attributes.Remove("origin");
			inputData.Attributes.Remove("model");
			if ((origin[0] != 0 || origin[1] != 0 || origin[2] != 0) && !Settings.noOriginBrushes)
			{
				// If this brush uses the "origin" attribute
				MAPBrush newOriginBrush = MAPBrush.createBrush(new Vector3D(- Settings.originBrushSize, - Settings.originBrushSize, - Settings.originBrushSize), new Vector3D(Settings.originBrushSize, Settings.originBrushSize, Settings.originBrushSize), "special/origin");
				inputData.Brushes.Add(newOriginBrush);
			}
			for (int i = 0; i < inputData.Brushes.Count; i++)
			{
				MAPBrush currentBrush = inputData.Brushes[i];
				currentBrush.translate(new Vector3D(origin));
			}
		}
		return inputData;
	}
	
	// ACCESSORS/MUTATORS
}