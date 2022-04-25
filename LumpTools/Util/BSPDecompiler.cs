using System;
using System.Collections.Generic;
using System.Linq;
using System.Numerics;

using LibBSP;

namespace LumpTools {
	/// <summary>
	/// Class for decompiling the data in a <see cref="BSP"/> object.
	/// </summary>
	public class BSPDecompiler {

		private BSP _bsp;
		private int _itemsToProcess = 0;
		private int _itemsProcessed = 0;

		/// <summary>
		/// Creates a new instance of a <see cref="Decompiler"/> object.
		/// </summary>
		/// <param name="bsp">The <see cref="BSP"/> object which will be processed.</param>
		public BSPDecompiler(BSP bsp) {
			this._bsp = bsp;

			if (bsp.Entities != null) { _itemsToProcess += bsp.Entities.Count; }
			if (bsp.Brushes != null) { _itemsToProcess += bsp.Brushes.Count; }
		}

		/// <summary>
		/// Begins the decompiling process on the <see cref="BSP"/> object passed to the constructor.
		/// </summary>
		/// <returns>An <see cref="Entities"/> object containing all the processed data.</returns>
		public Entities Decompile() {
			// There's no need to deepcopy; only one process will run on these entities and this will not be saved back to a BSP file
			Entities entities = _bsp.Entities;
			foreach (Entity entity in entities) {
				ProcessEntity(entity);
				++_itemsProcessed;
			}

			return entities;
		}

		/// <summary>
		/// Processes an <see cref="Entity"/> into a state where it can be output into a file that map editors can read.
		/// </summary>
		/// <param name="entity">The <see cref="Entity"/> to process.</param>
		/// <remarks>This method does not return anything, since the <see cref="Entity"/> object is modified by reference.</remarks>
		private void ProcessEntity(Entity entity) {
			int modelNumber = entity.ModelNumber;
			// If this Entity has no modelNumber, then this is a no-op. No processing is needed.
			// A modelnumber of 0 indicates the world entity.
			if (modelNumber >= 0) {
				Model model = _bsp.Models[modelNumber];

				if (_bsp.Brushes != null) {
					List<Brush> brushes = _bsp.GetBrushesInModel(model);
					if (brushes != null) {
						foreach (Brush brush in brushes) {
							MAPBrush result = ProcessBrush(brush, entity.Origin);
							entity.brushes.Add(result);
							++_itemsProcessed;
						}
					}
				}

				entity.Remove("model");
			}

			PostProcessNightfireEntity(entity);
		}

		/// <summary>
		/// Processes a <see cref="Brush"/> into a state where it can be output into a file that map editors can read.
		/// </summary>
		/// <param name="brush">The <see cref="Brush"/> to process.</param>
		/// <param name="worldPosition">The position of the parent <see cref="Entity"/> in the world. This is important for calculating UVs on solids.</param>
		/// <returns>The processed <see cref="MAPBrush"/> object, to be added to an <see cref="Entity"/> object.</returns>
		private MAPBrush ProcessBrush(Brush brush, Vector3 worldPosition) {
			List<BrushSide> sides;
			sides = _bsp.GetReferencedObjects<BrushSide>(brush, "BrushSides");
			MAPBrush mapBrush = new MAPBrush();
			mapBrush.isDetail = ((brush.Contents & (1 << 9)) != 0);
			int sideNum = 0;
			foreach (BrushSide side in sides) {
				MAPBrushSide mapBrushSide = ProcessBrushSide(side, worldPosition, sideNum);
				if (mapBrushSide != null) {
					mapBrush.sides.Add(mapBrushSide);
				}
				++sideNum;
			}

			return mapBrush;
		}

		/// <summary>
		/// Processes a <see cref="BrushSide"/> into a state where it can be output into a file that map editors can read.
		/// </summary>
		/// <param name="brushSide">The <see cref="BrushSide"/> to process.</param>
		/// <param name="worldPosition">The position of the parent <see cref="Entity"/> in the world. This is important for calculating UVs on solids.</param>
		/// <param name="sideIndex">The index of this side reference in the parent <see cref="Brush"/>. Important for Call of Duty series maps, since
		/// the first six <see cref="BrushSide"/>s in a <see cref="Brush"/> don't contain <see cref="Plane"/> references.</param>
		/// <returns>The processed <see cref="MAPBrushSode"/> object, to be added to a <see cref="Brush"/> object.</returns>
		private MAPBrushSide ProcessBrushSide(BrushSide brushSide, Vector3 worldPosition, int sideIndex) {
			if (brushSide.IsBevel) { return null; }
			MAPBrushSide mapBrushSide;
			// The things we'll need to define a .MAP brush side
			string texture;
			string material = "wld_lightmap";
			TextureInfo texInfo;
			Vector3[] threePoints;
			Plane plane;
			int flags = 0;

			Face face = _bsp.Faces[brushSide.FaceIndex];
			flags = face.Type;
			// In Nightfire, faces with "256" flag set should be ignored
			if ((flags & (1 << 8)) != 0) { return null; }
			texture = _bsp.Textures[face.TextureIndex].Name;
			threePoints = GetPointsForFace(face, brushSide);
			if (face.PlaneIndex >= 0 && face.PlaneIndex < _bsp.Planes.Count) {
				plane = _bsp.Planes[face.PlaneIndex];
			} else if (brushSide.PlaneIndex >= 0 && brushSide.PlaneIndex < _bsp.Planes.Count) {
				plane = _bsp.Planes[brushSide.PlaneIndex];
			} else {
				plane = new Plane(0, 0, 0, 0);
			}
			if (_bsp.TextureInfo != null) {
				texInfo = _bsp.TextureInfo[face.TextureInfoIndex];
			} else {
				Vector3[] newAxes = TextureInfo.TextureAxisFromPlane(plane);
				texInfo = new TextureInfo(newAxes[0], newAxes[1], Vector2.Zero, Vector2.One, flags, -1, 0);
			}
			if (face.MaterialIndex >= 0) {
				material = _bsp.Materials[face.MaterialIndex].Name;
			}

			TextureInfo outputTexInfo;
			if (texInfo.Data != null && texInfo.Data.Length > 0) {
				outputTexInfo = texInfo.BSP2MAPTexInfo(worldPosition);
			} else {
				Vector3[] newAxes = TextureInfo.TextureAxisFromPlane(plane);
				outputTexInfo = new TextureInfo(newAxes[0], newAxes[1], Vector2.Zero, Vector2.One, 0, -1, 0);
			}

			mapBrushSide = new MAPBrushSide() {
				vertices = threePoints,
				plane = plane,
				texture = texture,
				textureInfo = outputTexInfo,
				material = material,
				lgtScale = 16,
				lgtRot = 0
			};

			return mapBrushSide;
		}

		/// <summary>
		/// Looks at the information in the passed <paramref name="face"/> and tries to find the triangle defined
		/// by <paramref name="face"/> with the greatest area. If <paramref name="face"/> does not reference any
		/// vertices then we generate a triangle through the referenced <see cref="Plane"/> instead.
		/// </summary>
		/// <param name="face">The <see cref="Face"/> to find a triangle for.</param>
		/// <returns>Three points defining a triangle which define the plane which <paramref name="face"/> lies on.</returns>
		private Vector3[] GetPointsForFace(Face face, BrushSide brushSide) {
			Vector3[] ret;
			if (face.NumVertices > 2) {
				ret = new Vector3[3];
				float bestArea = 0;
				for (int i = 0; i < face.NumIndices / 3; ++i) {
					Vector3[] temp = new Vector3[] {
						_bsp.Vertices[(int)(face.FirstVertexIndex + _bsp.Indices[face.FirstIndexIndex + (i * 3)])].position,
						_bsp.Vertices[(int)(face.FirstVertexIndex + _bsp.Indices[face.FirstIndexIndex + 1 + (i * 3)])].position,
						_bsp.Vertices[(int)(face.FirstVertexIndex + _bsp.Indices[face.FirstIndexIndex + 2 + (i * 3)])].position
					};
					float area = Vector3Extensions.TriangleAreaSquared(temp[0], temp[1], temp[2]);
					if (area > bestArea) {
						bestArea = area;
						ret = temp;
					}
				}
				if (bestArea > 0.001) {
					return ret;
				}
			}
			if (face.PlaneIndex >= 0 && face.PlaneIndex < _bsp.Planes.Count) {
				ret = _bsp.Planes[face.PlaneIndex].GenerateThreePoints();
			} else if (brushSide.PlaneIndex >= 0 && brushSide.PlaneIndex < _bsp.Planes.Count) {
				ret = _bsp.Planes[brushSide.PlaneIndex].GenerateThreePoints();
			} else {
				return new Vector3[3];
			}
			return ret;
		}

		/// <summary>
		/// Postprocesser to convert an <see cref="Entity"/> from a Nightfire BSP to one for Gearcraft.
		/// </summary>
		/// <param name="entity">The <see cref="Entity"/> to parse.</param>
		private void PostProcessNightfireEntity(Entity entity) {
			if (entity.IsBrushBased) {
				Vector3 origin = entity.Origin;
				entity.Remove("origin");
				entity.Remove("model");
				if (origin != Vector3.Zero) {
					MAPBrush neworiginBrush = MAPBrushExtensions.CreateCube(new Vector3(-16, -16, -16), new Vector3(16, 16, 16), "special/origin");
					entity.brushes.Add(neworiginBrush);
				}
				foreach (MAPBrush brush in entity.brushes) {
					brush.Translate(origin);
				}
			}
		}

	}
}
