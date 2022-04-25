using System.Numerics;

using LibBSP;

namespace LumpTools.Util {
	public static class Swapper {
		public static void Swap(BSP me, bool swapXY, bool swapXZ, bool swapYZ) {
			// Entities
			foreach(Entity e in me.Entities) {
				e.Origin = Swap(e.Origin, swapXY, swapXZ, swapYZ);
			}

			// Planes
			for (int i = 0; i < me.Planes.Count; ++i) {
				Plane p = me.Planes[i];
				p.Normal = Swap(p.Normal, swapXY, swapXZ, swapYZ);
				me.Planes[i] = p;
			}

			// Vertices
			for (int i = 0; i < me.Vertices.Count; ++i) {
				Vertex vertex = me.Vertices[i];
				vertex.position = Swap(vertex.position, swapXY, swapXZ, swapYZ);
				me.Vertices[i] = vertex;
			}

			// Nodes
			for (int i = 0; i < me.Nodes.Count; ++i) {
				Node node = me.Nodes[i];
				node.Minimums = Swap(node.Minimums, swapXY, swapXZ, swapYZ);
				node.Maximums = Swap(node.Maximums, swapXY, swapXZ, swapYZ);
				me.Nodes[i] = node;
			}

			// Leaves
			for (int i = 0; i < me.Leaves.Count; ++i) {
				Leaf leaf = me.Leaves[i];
				leaf.Minimums = Swap(leaf.Minimums, swapXY, swapXZ, swapYZ);
				leaf.Maximums = Swap(leaf.Maximums, swapXY, swapXZ, swapYZ);
				me.Leaves[i] = leaf;
			}

			// Models
			for (int i = 0; i < me.Models.Count; ++i) {
				Model model = me.Models[i];
				model.Minimums = Swap(model.Minimums, swapXY, swapXZ, swapYZ);
				model.Maximums = Swap(model.Maximums, swapXY, swapXZ, swapYZ);
				me.Models[i] = model;
			}

			// Texinfos
			for (int i = 0; i < me.TextureInfo.Count; ++i) {
				TextureInfo info = me.TextureInfo[i];
				info.UAxis = Swap(info.UAxis, swapXY, swapXZ, swapYZ);
				info.VAxis = Swap(info.VAxis, swapXY, swapXZ, swapYZ);
				me.TextureInfo[i] = info;
			}
		}

		public static Vector3 Swap(Vector3 v, bool swapXY, bool swapXZ, bool swapYZ) {
			if (swapXY) {
				float x = v.X;
				v.X = v.Y;
				v.Y = x;
			}
			if (swapXZ) {
				float x = v.X;
				v.X = v.Z;
				v.Z = x;
			}
			if (swapYZ) {
				float y = v.Y;
				v.Y = v.Z;
				v.Z = y;
			}

			return v;
		}
	}
}
