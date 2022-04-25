using System.Numerics;

using LibBSP;

namespace LumpTools.Util {
	public static class Flipper {
		public static void Flip(BSP me, bool flipX, bool flipY, bool flipZ) {
			// Entities
			foreach(Entity entity in me.Entities) {
				Vector3 origin = entity.Origin;
				if (flipX) {
					origin = new Vector3(-origin.X, origin.Y, origin.Z);
				}
				if (flipY) {
					origin = new Vector3(origin.X, -origin.Y, origin.Z);
				}
				if (flipZ) {
					origin = new Vector3(origin.X, origin.Y, -origin.Z);
				}
				entity.Origin = origin;
			}

			// Planes
			for (int i = 0; i < me.Planes.Count; ++i) {
				Plane plane = me.Planes[i];
				Vector3 normal = plane.Normal;
				if (flipX) {
					normal = new Vector3(-normal.X, normal.Y, normal.Z);
				}
				if (flipY) {
					normal = new Vector3(normal.X, -normal.Y, normal.Z);
				}
				if (flipZ) {
					normal = new Vector3(normal.X, normal.Y, -normal.Z);
				}
				plane.Normal = normal;
				me.Planes[i] = plane;
			}

			// Vertices
			for (int i = 0; i < me.Vertices.Count; ++i) {
				Vertex vertex = me.Vertices[i];
				Vector3 position = vertex.position;
				if (flipX) {
					position = new Vector3(-position.X, position.Y, position.Z);
				}
				if (flipY) {
					position = new Vector3(position.X, -position.Y, position.Z);
				}
				if (flipZ) {
					position = new Vector3(position.X, position.Y, -position.Z);
				}
				vertex.position = position;
				me.Vertices[i] = vertex;
			}

			// Nodes
			for (int i = 0; i < me.Nodes.Count; ++i) {
				Node node = me.Nodes[i];
				Vector3 mins = node.Minimums;
				Vector3 maxs = node.Maximums;
				if (flipX) {
					mins = new Vector3(-maxs.X, mins.Y, mins.Z);
					maxs = new Vector3(-mins.X, maxs.Y, maxs.Z);
				}
				if (flipY) {
					mins = new Vector3(mins.X, -maxs.Y, mins.Z);
					maxs = new Vector3(maxs.X, -mins.Y, maxs.Z);
				}
				if (flipZ) {
					mins = new Vector3(mins.X, mins.Y, -maxs.Z);
					maxs = new Vector3(maxs.X, maxs.Y, -mins.Z);
				}
				node.Minimums = mins;
				node.Maximums = maxs;
				me.Nodes[i] = node;
			}

			// Leaves
			for (int i = 0; i < me.Leaves.Count; ++i) {
				Leaf leaf = me.Leaves[i];
				Vector3 mins = leaf.Minimums;
				Vector3 maxs = leaf.Maximums;
				if (flipX) {
					mins = new Vector3(-maxs.X, mins.Y, mins.Z);
					maxs = new Vector3(-mins.X, maxs.Y, maxs.Z);
				}
				if (flipY) {
					mins = new Vector3(mins.X, -maxs.Y, mins.Z);
					maxs = new Vector3(maxs.X, -mins.Y, maxs.Z);
				}
				if (flipZ) {
					mins = new Vector3(mins.X, mins.Y, -maxs.Z);
					maxs = new Vector3(maxs.X, maxs.Y, -mins.Z);
				}
				leaf.Minimums = mins;
				leaf.Maximums = maxs;
				me.Leaves[i] = leaf;
			}

			// Models
			for (int i = 0; i < me.Models.Count; ++i) {
				Model model = me.Models[i];
				Vector3 mins = model.Minimums;
				Vector3 maxs = model.Maximums;
				if (flipX) {
					mins = new Vector3(-maxs.X, mins.Y, mins.Z);
					maxs = new Vector3(-mins.X, maxs.Y, maxs.Z);
				}
				if (flipY) {
					mins = new Vector3(mins.X, -maxs.Y, mins.Z);
					maxs = new Vector3(maxs.X, -mins.Y, maxs.Z);
				}
				if (flipZ) {
					mins = new Vector3(mins.X, mins.Y, -maxs.Z);
					maxs = new Vector3(maxs.X, maxs.Y, -mins.Z);
				}
				model.Minimums = mins;
				model.Maximums = maxs;
				me.Models[i] = model;
			}

			// Texinfos
			for (int i = 0; i < me.TextureInfo.Count; ++i) {
				TextureInfo textureInfo = me.TextureInfo[i];
				Vector3 sAxis = textureInfo.UAxis;
				Vector3 tAxis = textureInfo.VAxis;
				if (flipX) {
					sAxis = new Vector3(-sAxis.X, sAxis.Y, sAxis.Z);
					tAxis = new Vector3(-tAxis.X, tAxis.Y, tAxis.Z);
				}
				if (flipY) {
					sAxis = new Vector3(sAxis.X, -sAxis.Y, sAxis.Z);
					tAxis = new Vector3(tAxis.X, -tAxis.Y, tAxis.Z);
				}
				if (flipZ) {
					sAxis = new Vector3(sAxis.X, sAxis.Y, -sAxis.Z);
					tAxis = new Vector3(tAxis.X, tAxis.Y, -tAxis.Z);
				}
				textureInfo.UAxis = sAxis;
				textureInfo.VAxis = tAxis;
				me.TextureInfo[i] = textureInfo;
			}
		}
	}
}