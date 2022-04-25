using System;
using System.Linq;
using System.Numerics;

using LibBSP;

namespace LumpTools.Util {
	public static class Optimizer {
		public static void Optimize(BSP me) {
			OptimizeEntities(me);
			OptimizePlanes(me);
			OptimizeTextures(me);
			OptimizeMaterials(me);
			OptimizeNormals(me);
			FastOptimizeIndices(me);
			OptimizeVis(me);
			OptimizeTexInfo(me);

		}

		// Each entity has a unique instance in the engine, therefore even identical
		// entities are unique. However some attributes can be culled, for example
		// an "angles" attribute of "0 0 0".
		public static void OptimizeEntities(BSP me) {
			for (int i = 0; i < me.Entities.Count; ++i) {
				Entity ent = me.Entities[i];
				switch (ent["classname"]) {
					case "multi_manager":
					case "multisource":
					case "env_fade":
					case "trigger_relay":
					case "multi_kill_manager":
					case "trigger_counter":
					case "trigger_auto": {
						ent.Remove("angles");
						ent.Remove("origin");
						break;
					}
					case "light":
					case "light_spot": {
						if (ent["targetname"] == "") {
							me.Entities.RemoveAt(i);
							--i;
							continue;
						}
						break;
					}

				}
				if (ent.Angles == Vector3.Zero) {
					ent.Remove("angles");
				}
				if (ent.Origin == Vector3.Zero) {
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
		public static void OptimizePlanes(BSP me) {
			int numDeleted = 0;
			for (int i = 0; i < me.Planes.Count; ++i) {
				for (int j = i + 1; j < me.Planes.Count; ++j) {
					if (me.Planes[i] == me.Planes[j]) {
						me.Planes.RemoveAt(j);
						++numDeleted;
						// Must now correct all references to this plane, and all those after it
						for (int k = 0; k < me.Faces.Count; ++k) {
							Face face = me.Faces[k];
							if (face.PlaneIndex > j) {
								face.PlaneIndex -= 1;
							} else if (face.PlaneIndex == j) {
								face.PlaneIndex = i;
							}
							me.Faces[k] = face;
						}
						for (int k = 0; k < me.Nodes.Count; ++k) {
							Node node = me.Nodes[k];
							if (node.PlaneIndex > j) {
								node.PlaneIndex -= 1;
							} else if (node.PlaneIndex == j) {
								node.PlaneIndex = i;
							}
							me.Nodes[k] = node;
						}
						for (int k = 0; k < me.BrushSides.Count; ++k) {
							BrushSide brushSide = me.BrushSides[k];
							if (brushSide.PlaneIndex > j) {
								brushSide.PlaneIndex -= 1;
							} else if (brushSide.PlaneIndex == j) {
								brushSide.PlaneIndex = i;
							}
							me.BrushSides[k] = brushSide;
						}
						--j;
					}
				}
			}

			Console.WriteLine("Deleted " + numDeleted + " duplicate planes");
		}
		
		// Usually the compiler doesn't duplicate textures or materials. But if something strange
		// is going on, or the BSP was modified by an outside source (such as this program) then
		// they may exist and are trivial to reuse.
		public static void OptimizeTextures(BSP me) {
			int numDeleted = 0;
			for (int i = 0; i < me.Textures.Count; ++i) {
				for (int j = i + 1; j < me.Textures.Count; ++j) {
					if (me.Textures[i].Name == me.Textures[j].Name) {
						me.Textures.RemoveAt(j);
						++numDeleted;
						// Fix references
						for (int k = 0; k < me.Faces.Count; ++k) {
							Face face = me.Faces[k];
							if (face.TextureIndex > j) {
								face.TextureIndex -= 1;
							} else if (face.TextureIndex == j) {
								face.TextureIndex = i;
							}
							me.Faces[k] = face;
						}
						--j;
					}
				}
			}

			Console.WriteLine("Deleted " + numDeleted + " duplicate textures");
		}
		
		public static void OptimizeMaterials(BSP me) {
			int numDeleted = 0;
			for (int i = 0; i < me.Materials.Count; ++i) {
				for (int j = i + 1; j < me.Materials.Count; ++j) {
					if (me.Materials[i].Name == me.Materials[j].Name) {
						me.Materials.RemoveAt(j);
						++numDeleted;
						for (int k = 0; k < me.Faces.Count; ++k) {
							Face face = me.Faces[k];
							if (face.MaterialIndex > j) {
								face.MaterialIndex -= 1;
							} else if (face.MaterialIndex == j) {
								face.MaterialIndex = i;
							}
							me.Faces[k] = face;
						}
						--j;
					}
				}
			}

			Console.WriteLine("Deleted " + numDeleted + " duplicate materials");
		}

		// The bloated unused "null" lump. We can just set its length to 0.
		// Depending on the complexity of the map, this can reduce size a good deal.
		public static void OptimizeNormals(BSP me) {
			me.Normals.Clear();
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
		public static void OptimizeIndices(BSP me) {
			// TODO
		}

		// This is the "fast" method that cuts corners and assumes way too much about the data. I'll
		// document the assumptions as I make them, though they seem to be safe anyway.
		public static void FastOptimizeIndices(BSP me) {
			long largestIndex = 2; // Assumption: Highest index will always be at least 2.

			for (int i = 0; i < me.Indices.Count / 3; ++i) {
				if (me.Indices[(i * 3) + 2] > largestIndex) { // Assumption: Every third index will be greater than the previous two
					largestIndex = me.Indices[(i * 3) + 2];
				}
			}

			me.Indices.Clear();
			for (int i = 0; i < largestIndex - 1; ++i) { // Assumption: If the highest index is N, there will be N-1 sets of three ints
				me.Indices.Add(0); // Assumption: The first index in a triple will always be 0
				me.Indices.Add(i + 1); // Assumption: The second index is always the current set of three plus one
				me.Indices.Add(i + 2); // Assumption: The third index is always the current set of three plus two
			}

			for (int i = 0; i < me.Faces.Count; ++i) {
				Face face = me.Faces[i];
				face.FirstIndexIndex = 0; // Assumption: Every face starts at the beginning of the pattern, and will take what it needs
				me.Faces[i] = face;
			}

			Console.WriteLine("Indices optimized to " + (me.Indices.Count * 4) + " bytes.");
		}

		// This will delete duplicate "potentially visible sets" for visleafs. Though it is rare
		// for visleaves to have exactly the same PVS it usually happens at least once in a map.
		// I'm not sure how the engine or the compiler determine exactly how many bytes are in each
		// visibility list, but I can determine it easily from a quick analysis of the Leaves lump.
		// Therefore it's a quick array comparison to determine whether one is equivalent to another
		// and if it can therefore be reused.
		public static void OptimizeVis(BSP me) {
			int numDeleted = 0;
			int length = me.Leaves[2].Visibility;

			for (int i = 0; i < me.Leaves.Count; ++i) {
				Leaf leaf = me.Leaves[i];
				int visOffset = leaf.Visibility;
				if (visOffset < 0) {
					continue;
				}
				for (int j = i + 1; j < me.Leaves.Count; ++j) {
					Leaf other = me.Leaves[j];
					int otherVisOffset = other.Visibility;
					if (otherVisOffset < 0 || visOffset == otherVisOffset) {
						continue;
					}
					bool same = true;
					for (int k = 0; k < length; ++k) {
						if (me.Visibility.Data[visOffset + k] != me.Visibility.Data[otherVisOffset + k]) {
							same = false;
							break;
						}
					}
					if (same) {
						byte[] newData = new byte[me.Visibility.Data.Length - length];
						Array.Copy(me.Visibility.Data, 0, newData, 0, otherVisOffset);
						Array.Copy(me.Visibility.Data, otherVisOffset + length, newData, otherVisOffset, me.Visibility.Data.Length - otherVisOffset - length);
						me.Visibility.Data = newData;
						other.Visibility = visOffset;
						for (int k = j + 1; k < me.Leaves.Count; ++k) {
							Leaf nextLeaf = me.Leaves[k];
							nextLeaf.Visibility -= length;
							me.Leaves[k] = nextLeaf;
						}
						++numDeleted;
					}
					me.Leaves[j] = other;
				}
			}

			Console.WriteLine("Deleted " + numDeleted + " duplicate PVS lists for " + (numDeleted * length) + " bytes.");
		}

		public static void OptimizeTexInfo(BSP me) {
			int numDeleted = 0;
			for (int i = 0; i < me.TextureInfo.Count; ++i) {
				TextureInfo texInfo = me.TextureInfo[i];
				for (int j = i + 1; j < me.TextureInfo.Count; ++j) {
					TextureInfo otherTexInfo = me.TextureInfo[j];
					if (texInfo.UAxis == otherTexInfo.UAxis
						&& texInfo.VAxis == otherTexInfo.VAxis
						&& texInfo.Translation == otherTexInfo.Translation) {
						me.TextureInfo.RemoveAt(j);
						++numDeleted;
						for (int k = 0; k < me.Faces.Count; ++k) {
							Face face = me.Faces[k];
							if (face.TextureInfoIndex > j) {
								face.TextureInfoIndex -= 1;
							} else if(face.TextureInfoIndex == j) {
								face.TextureInfoIndex = i;
							}
							if (face.LightmapTextureInfoIndex > j) {
								face.LightmapTextureInfoIndex -= 1;
							} else if (face.LightmapTextureInfoIndex == j) {
								face.LightmapTextureInfoIndex = i;
							}
							me.Faces[k] = face;
						}
						--j;
					}
				}
			}

			Console.WriteLine("Deleted " + numDeleted + " duplicate texinfos");
		}
	}
}
