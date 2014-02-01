using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace LumpTools.Util {
	public static class Optimizer {
		public static void optimize(BSP me) {
			optimizeEntities(me);
			optimizePlanes(me);
			optimizeTextures(me);
			optimizeMaterials(me);
			optimizeNormals(me);
			fastOptimizeIndices(me);
			optimizeVis(me);
			//optimizeTexInfo(me);

		}

		// Each entity has a unique instance in the engine, therefore even identical
		// entities are unique. However some attributes can be culled, for example
		// an "angles" attribute of "0 0 0".
		public static void optimizeEntities(BSP me) {
			for(int i=0;i<me.Entities.Count;i++) {
				Entity ent = me.Entities[i];
				switch(ent["classname"]) {
					case "multi_manager":
					case "multisource":
					case "env_fade":
					case "trigger_relay":
					case "multi_kill_manager":
					case "trigger_counter":
					case "trigger_auto":
						ent.Remove("angles");
						ent.Remove("origin");
						break;
					case "light":
					case "light_spot":
						if(ent["targetname"] == "") {
							me.Entities.RemoveAt(i);
							i--;
							continue;
						}
						break;

				}
				if(ent.Angles[0]==0 && ent.Angles[1]==0 && ent.Angles[2]==0) {
					ent.Remove("angles");
				}
				if(ent.Origin[0]==0 && ent.Origin[1]==0 && ent.Origin[2]==0) {
					ent.Remove("origin");
				}
				ent.Remove("sequencename");
			}
		}

		// Finds duplicate planes (even if they're flipped to face the opposite direction)
		// and deletes them, then corrects all references to the deleted plane. The compiler
		// is usually pretty good about recycling identical planes, but many planes have their
		// backward counterpart included in the map for some reason. For instance, a plane
		// ((1,0,0),456) will be the same as ((-1,0,0),-456)
		public static void optimizePlanes(BSP me) {
			int numDeleted = 0;
			for(int i=0;i<me.Planes.Count;i++) {
				for(int j=i+1;j<me.Planes.Count;j++) {
					if(me.Planes[i] == me.Planes[j]) {
						me.Planes.RemoveAt(j);
						numDeleted++;
						// Must now correct all references to this plane, and all those after it
						for(int k=0;k<me.Faces.Count;k++) {
							if(me.Faces[k].Plane > j) {
								me.Faces[k].Plane -= 1;
							} else if(me.Faces[k].Plane == j) {
								me.Faces[k].Plane = i;
							}
						}
						for(int k=0;k<me.Nodes.Count;k++) {
							if(me.Nodes[k].Plane > j) {
								me.Nodes[k].Plane -= 1;
							} else if(me.Nodes[k].Plane == j) {
								me.Nodes[k].Plane = i;
							}
						}
						for(int k=0;k<me.BrushSides.Count;k++) {
							if(me.BrushSides[k].Plane > j) {
								me.BrushSides[k].Plane -= 1;
							} else if(me.BrushSides[k].Plane == j) {
								me.BrushSides[k].Plane = i;
							}
						}
						j--;
					}
				}
			}
			Console.WriteLine("Deleted "+numDeleted+" duplicate planes");
		}
		
		// Usually the compiler doesn't duplicate textures or materials. But if something strange
		// is going on, or the BSP was modified by an outside source (such as this program) then
		// they may exist and are trivial to reuse.
		public static void optimizeTextures(BSP me) {
			int numDeleted = 0;
			for(int i=0;i<me.Textures.Count;i++) {
				for(int j=i+1;j<me.Textures.Count;j++) {
					if(me.Textures[i] == me.Textures[j]) {
						me.Textures.RemoveAt(j);
						numDeleted++;
						// Fix references
						for(int k=0;k<me.Faces.Count;k++) {
							if(me.Faces[k].Texture > j) {
								me.Faces[k].Texture -= 1;
							} else if(me.Faces[k].Texture==j) {
								me.Faces[k].Texture = i;
							}
						}
						j--;
					}
				}
			}
			Console.WriteLine("Deleted "+numDeleted+" duplicate textures");
		}
		
		public static void optimizeMaterials(BSP me) {
			int numDeleted = 0;
			for(int i=0;i<me.Materials.Count;i++) {
				for(int j=i+1;j<me.Materials.Count;j++) {
					if(me.Materials[i] == me.Materials[j]) {
						me.Materials.RemoveAt(j);
						numDeleted++;
						for(int k=0;k<me.Faces.Count;k++) {
							if(me.Faces[k].Material > j) {
								me.Faces[k].Material -= 1;
							} else if(me.Faces[k].Material==j) {
								me.Faces[k].Material = i;
							}
						}
						j--;
					}
				}
			}
			Console.WriteLine("Deleted "+numDeleted+" duplicate materials");
		}

		// The bloated unused "null" lump. We can just set its length to 0.
		// Depending on the complexity of the map, this can reduce size a good deal.
		public static void optimizeNormals(BSP me) {
			me.Normals.Length = 0;
			Console.WriteLine("Deleted normals lump");
		}

		// There's two ways to optimize indices: the quick, potentially erroneous way, or the slow,
		// safe way. The quick way simply finds the largest number in the indices lump, creates a 
		// new lump which only goes that high, then sets all face "indices" references to zero. Since
		// all index groups follow the same pattern (0,1,2,0,2,3,0,3,4,etc.) then the face will simply
		// use as much of the index list as it needs.
		// The long way is to analyze each group to ensure commonality, and reuse parts of one group
		// if it matches another.
		// In theory, only the slow way should be used because the quick way makes too many assumptions
		// about the data, but in practice the data ALWAYS follows the same pattern. Therefore either
		// method should give the same result, and should crush this lump to 200 bytes or less.
		public static void optimizeIndices(BSP me) {
			// TODO
		}

		// This is the "fast" method that cuts corners and assumes way too much about the data. I'll
		// document the assumptions as I make them, though they seem to be safe anyway.
		public static void fastOptimizeIndices(BSP me) {
			long largestIndex = 2; // Assumption: Highest index will always be at least 2.
			for(int i=0;i<me.Indices.Count/3;i++) {
				if(me.Indices[(i*3) + 2] > largestIndex) { // Assumption: Every third index will be greater than the previous two
					largestIndex = me.Indices[(i*3) + 2];
				}
			}
			NumList newIndices = new NumList(NumList.dataType.UINT);
			for(int i=0;i<largestIndex-1;i++) { // Assumption: If the highest index is N, there will be N-1 sets of three ints
				newIndices.Add(0); // Assumption: The first index in a triple will always be 0
				newIndices.Add(i+1); // Assumption: The second index is always the current set of three plus one
				newIndices.Add(i+2); // Assumption: The third index is always the current set of three plus two
			}
			me.Indices = newIndices;
			for(int i=0;i<me.Faces.Count;i++) {
				me.Faces[i].FirstIndex = 0; // Assumption: Every face starts at the beginning of the pattern, and will take what it needs
			}
			Console.WriteLine("Indices optimized to "+(newIndices.Count*4)+" bytes.");
		}

		// This will delete duplicate "potentially visible sets" for visleafs. Though it is rare
		// for visleaves to have exactly the same PVS it usually happens at least once in a map.
		// I'm not sure how the engine or the compiler determine exactly how many bytes are in each
		// visibility list, but I can determine it easily from a quick analysis of the Leaves lump.
		// Therefore it's a quick array comparison to determine whether one is equivalent to another
		// and if it can therefore be reused.
		public static void optimizeVis(BSP me) {
			int numDeleted = 0;
			for(int i=0;i<me.Vis.Count;i++) {
				for(int j=i+1;j<me.Vis.Count;j++) {
					if(me.Vis[i] == me.Vis[j]) {
						me.Vis.RemoveAt(j); // Since PVSes are stored as bare LumpObjects, their data arrays will be compared using ==
						numDeleted++;
						for(int k=0;k<me.Leaves.Count;k++) {
							if(me.Leaves[k].PVS > me.Vis[i].Length*j) {
								me.Leaves[k].PVS -= me.Vis[i].Length;
							} else if(me.Leaves[k].PVS == me.Vis[i].Length*j) {
								me.Leaves[k].PVS = me.Vis[i].Length*i;
							}
						}
						j--;
					}
				}
			}
			Console.WriteLine("Deleted "+numDeleted+" duplicate PVS lists for "+(numDeleted*me.Vis[0].Length)+" bytes.");
		}

		// Also remove duplicate TexInfos. This always gave me "AllocLightmap: Full" errors before,
		// hopefully that doesn't happen this time.
		public static void optimizeTexInfo(BSP me) {
			int numDeleted = 0;
			for(int i=0;i<me.TexInfo.Count;i++) {
				for(int j=i+1;j<me.TexInfo.Count;j++) {
					if(me.TexInfo[i] == me.TexInfo[j]) {
						me.TexInfo.RemoveAt(j);
						numDeleted++;
						for(int k=0;k<me.Faces.Count;k++) {
							if(me.Faces[k].TextureScale > j) {
								me.Faces[k].TextureScale -= 1;
							} else if(me.Faces[k].TextureScale == j) {
								me.Faces[k].TextureScale = i;
							}
						}
					}
				}
			}
			Console.WriteLine("Deleted "+numDeleted+" duplicate texinfos");
		}
	}
}
