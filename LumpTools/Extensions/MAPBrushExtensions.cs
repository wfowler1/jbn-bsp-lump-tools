using System.Numerics;

using LibBSP;

namespace LumpTools {
	/// <summary>
	/// Static class containing helper functions for working with <see cref="MAPBrush"/> objects.
	/// </summary>
	public static class MAPBrushExtensions {

		/// <summary>
		/// Moves this <see cref="MAPBrush"/> object in the world by the vector <paramref name="v"/>.
		/// </summary>
		/// <param name="mapBrush">This <see cref="MAPBrush"/>.</param>
		/// <param name="v">Translation vector.</param>
		public static void Translate(this MAPBrush mapBrush, Vector3 v) {
			if (v == Vector3.Zero) {
				return;
			}
			foreach (MAPBrushSide side in mapBrush.sides) {
				side.Translate(v);
				if (side.displacement != null) {
					side.displacement.start += v;
				}
			}
		}

		/// <summary>
		/// Creates an axis-aligned cubic brush with bounds from <paramref name="mins"/> to <paramref name="maxs"/>.
		/// </summary>
		/// <param name="mins">The minimum extents of the new brush.</param>
		/// <param name="maxs">The maximum extents of the new brush.</param>
		/// <param name="texture">The texture to use on this brush.</param>
		/// <returns>The resulting <see cref="MAPBrush"/> object.</returns>
		public static MAPBrush CreateCube(Vector3 mins, Vector3 maxs, string texture) {
			MAPBrush newBrush = new MAPBrush();
			Vector3[][] planes = new Vector3[6][];
			for (int i = 0; i < 6; ++i) {
				planes[i] = new Vector3[3];
			} // Six planes for a cube brush, three vertices for each plane
			float[][] textureS = new float[6][];
			for (int i = 0; i < 6; ++i) {
				textureS[i] = new float[3];
			}
			float[][] textureT = new float[6][];
			for (int i = 0; i < 6; ++i) {
				textureT[i] = new float[3];
			}
			// The planes and their texture scales
			// I got these from an origin brush created by Gearcraft. Don't worry where these numbers came from, they work.
			// Top
			planes[0][0] = new Vector3(mins.X, maxs.Y, maxs.Z);
			planes[0][1] = new Vector3(maxs.X, maxs.Y, maxs.Z);
			planes[0][2] = new Vector3(maxs.X, mins.Y, maxs.Z);
			textureS[0][0] = 1;
			textureT[0][1] = -1;
			// Bottom
			planes[1][0] = new Vector3(mins.X, mins.Y, mins.Z);
			planes[1][1] = new Vector3(maxs.X, mins.Y, mins.Z);
			planes[1][2] = new Vector3(maxs.X, maxs.Y, mins.Z);
			textureS[1][0] = 1;
			textureT[1][1] = -1;
			// Left
			planes[2][0] = new Vector3(mins.X, maxs.Y, maxs.Z);
			planes[2][1] = new Vector3(mins.X, mins.Y, maxs.Z);
			planes[2][2] = new Vector3(mins.X, mins.Y, mins.Z);
			textureS[2][1] = 1;
			textureT[2][2] = -1;
			// Right
			planes[3][0] = new Vector3(maxs.X, maxs.Y, mins.Z);
			planes[3][1] = new Vector3(maxs.X, mins.Y, mins.Z);
			planes[3][2] = new Vector3(maxs.X, mins.Y, maxs.Z);
			textureS[3][1] = 1;
			textureT[3][2] = -1;
			// Near
			planes[4][0] = new Vector3(maxs.X, maxs.Y, maxs.Z);
			planes[4][1] = new Vector3(mins.X, maxs.Y, maxs.Z);
			planes[4][2] = new Vector3(mins.X, maxs.Y, mins.Z);
			textureS[4][0] = 1;
			textureT[4][2] = -1;
			// Far
			planes[5][0] = new Vector3(maxs.X, mins.Y, mins.Z);
			planes[5][1] = new Vector3(mins.X, mins.Y, mins.Z);
			planes[5][2] = new Vector3(mins.X, mins.Y, maxs.Z);
			textureS[5][0] = 1;
			textureT[5][2] = -1;

			for (int i = 0; i < 6; i++) {
				MAPBrushSide currentSide = new MAPBrushSide() {
					vertices = planes[i],
					plane = Plane.CreateFromVertices(planes[i][0], planes[i][2], planes[i][1]),
					texture = texture,
					textureInfo = new TextureInfo(new Vector3(textureS[i][0], textureS[i][1], textureS[i][2]), new Vector3(textureT[i][0], textureT[i][1], textureT[i][2]), Vector2.Zero, Vector2.One, 0, 0, 0),
					material = "wld_lightmap",
					lgtScale = 16,
					lgtRot = 0
				};
				newBrush.sides.Add(currentSide);
			}
			return newBrush;
		}

	}
}