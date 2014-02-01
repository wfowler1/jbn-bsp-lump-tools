using System;

public static class Settings {
	public static int numThreads = Environment.ProcessorCount;
	public static double planePointCoef = 32;
	public static bool skipPlaneFlip = true;
	public static bool noDetail = false;
	public static bool noWater = false;
	public static bool replaceWithNull = false;
	public static bool planarDecomp = false;
	public static bool noFaceFlags = false;
	public static bool calcVerts = false;
	public static bool brushesToWorld = false;
	public static bool noTexCorrection = false;
	public static bool noEntCorrection = false;
	public static bool roundNums = true;
	public static bool noOriginBrushes = false;
	public static bool dumpCrashLump = false;
	public static double originBrushSize=16;
	public static int verbosity=0;
	public static string outputFolder="default";
	public static double precision=0.05;
}