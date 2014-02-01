namespace LumpTools.Util {
	public static class Swapper {
		public static void Swap(BSP me, int a1, int a2) {
			// Entities
			foreach(Entity e in me.Entities) {
				Vector3D origin = e.Origin;
				double temp = origin[a1];
				origin[a1] = origin[a2];
				origin[a2] = temp;
				e.Origin = origin;
			}
			// Planes
			foreach(Plane p in me.Planes) {
				Vector3D normal = p.Normal;
				double temp = normal[a1];
				normal[a1] = normal[a2];
				normal[a2] = temp;
				p.Normal = normal;
			}
			// Vertices
			foreach(Vertex v in me.Vertices) {
				Vector3D vertex = v.Vector;
				double temp = vertex[a1];
				vertex[a1] = vertex[a2];
				vertex[a2] = temp;
				v.Vector = vertex;
			}
			// Nodes
			foreach(Node n in me.Nodes) {
				Vector3D mins = n.Mins;
				Vector3D maxs = n.Maxs;
				double mintemp = mins[a1];
				mins[a1] = mins[a2];
				mins[a2] = mintemp;
				double maxtemp = maxs[a1];
				maxs[a1] = maxs[a2];
				maxs[a2] = maxtemp;
				n.Mins = mins;
				n.Maxs = maxs;
			}
			// Leaves
			foreach(Leaf l in me.Leaves) {
				Vector3D mins = l.Mins;
				Vector3D maxs = l.Maxs;
				double mintemp = mins[a1];
				mins[a1] = mins[a2];
				mins[a2] = mintemp;
				double maxtemp = maxs[a1];
				maxs[a1] = maxs[a2];
				maxs[a2] = maxtemp;
				l.Mins = mins;
				l.Maxs = maxs;
			}
			// Models
			foreach(Model m in me.Models) {
				Vector3D mins = m.Mins;
				Vector3D maxs = m.Maxs;
				double mintemp = mins[a1];
				mins[a1] = mins[a2];
				mins[a2] = mintemp;
				double maxtemp = maxs[a1];
				maxs[a1] = maxs[a2];
				maxs[a2] = maxtemp;
				m.Mins = mins;
				m.Maxs = maxs;
			}
			// Texinfos
			foreach(TexInfo ti in me.TexInfo) {
				Vector3D sAxis = ti.SAxis;
				Vector3D tAxis = ti.TAxis;
				double stemp = sAxis[a1];
				sAxis[a1] = sAxis[a2];
				sAxis[a2] = stemp;
				double ttemp = tAxis[a1];
				tAxis[a1] = tAxis[a2];
				tAxis[a2] = ttemp;
				ti.SAxis = sAxis;
				ti.TAxis = tAxis;
			}
		}
	}
}
