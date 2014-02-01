namespace LumpTools.Util {
	public static class Flipper {
		public static void Flip(BSP me, int axis) {
			// Entities
			foreach(Entity e in me.Entities) {
				Vector3D origin = e.Origin;
				origin[axis] = -origin[axis];
				e.Origin = origin;
			}
			// Planes
			foreach(Plane p in me.Planes) {
				Vector3D normal = p.Normal;
				normal[axis] = -normal[axis];
				p.Normal = normal;
			}
			// Vertices
			foreach(Vertex v in me.Vertices) {
				Vector3D vertex = v.Vector;
				vertex[axis] = -vertex[axis];
				v.Vector = vertex;
			}
			// Nodes
			foreach(Node n in me.Nodes) {
				Vector3D mins = n.Mins;
				Vector3D maxs = n.Maxs;
				double minAxis = mins[axis];
				double maxAxis = maxs[axis];
				mins[axis] = -maxAxis;
				maxs[axis] = -minAxis;
				n.Mins = mins;
				n.Maxs = maxs;
			}
			// Leaves
			foreach(Leaf l in me.Leaves) {
				Vector3D mins = l.Mins;
				Vector3D maxs = l.Maxs;
				double minAxis = mins[axis];
				double maxAxis = maxs[axis];
				mins[axis] = -maxAxis;
				maxs[axis] = -minAxis;
				l.Mins = mins;
				l.Maxs = maxs;
			}
			// Models
			foreach(Model m in me.Models) {
				Vector3D mins = m.Mins;
				Vector3D maxs = m.Maxs;
				double minAxis = mins[axis];
				double maxAxis = maxs[axis];
				mins[axis] = -maxAxis;
				maxs[axis] = -minAxis;
				m.Mins = mins;
				m.Maxs = maxs;
			}
			// Texinfos
			foreach(TexInfo ti in me.TexInfo) {
				Vector3D sAxis = ti.SAxis;
				Vector3D tAxis = ti.TAxis;
				sAxis[axis] = -sAxis[axis];
				tAxis[axis] = -tAxis[axis];
				ti.SAxis = sAxis;
				ti.TAxis = tAxis;
			}
		}
	}
}