using System;
using System.Collections.Generic;
// BSP class
// Holds data for a Nightfire BSP.

public class BSP {
	// INITIAL DATA DECLARATION AND DEFINITION OF CONSTANTS
	
	private string filePath;
	private bool modified = false;
	
	// Map structures
	private Entities entities;
	private Lump<Plane> planes;
	private Lump<Texture> textures;
	private Lump<Texture> materials;
	private Lump<Vertex> vertices;
	private Lump<LumpObject> normals;
	private NumList indices;
	private Lump<LumpObject> vis;
	private Lump<Node> nodes;
	private Lump<Face> faces;
	private NumList lightmaps;
	private Lump<Leaf> leaves;
	private NumList markSurfaces;
	private NumList markBrushes;
	private Lump<Model> models;
	private Lump<Brush> brushes;
	private Lump<BrushSide> brushSides;
	private Lump<TexInfo> texInfo;

	// CONSTRUCTORS
	public BSP(string filePath) {
		this.filePath = filePath;
	}
	
	// METHODS
	
	public virtual void printBSPReport() {
		// If there's a NullReferenceException here, the BSPReader class didn't initialize the object and therefore
		// there's an error which will become apparent.
		if(entities != null) {
			Console.WriteLine("Entities lump: " + entities.Length + " bytes, " + entities.Count + " items");
		}
		if(planes != null) {
			Console.WriteLine("Planes lump: " + planes.Length + " bytes, " + planes.Count + " items");
			if (planes.hasFunnySize()) {
				Console.WriteLine("WARNING: Funny lump size in Planes");
			}
		}
		if(textures != null) {
			Console.WriteLine("Texture lump: " + textures.Length + " bytes, " + textures.Count + " items");
			if (textures.hasFunnySize()) {
				Console.WriteLine("WARNING: Funny lump size in Texture");
			}
		}
		if(materials != null) {
			Console.WriteLine("Materials lump: " + materials.Length + " bytes, " + materials.Count + " items");
			if (materials.hasFunnySize()) {
				Console.WriteLine("WARNING: Funny lump size in Materials");
			}
		}
		if(vertices != null) {
			Console.WriteLine("Vertices lump: " + vertices.Length + " bytes, " + vertices.Count + " items");
			if (vertices.hasFunnySize()) {
				Console.WriteLine("WARNING: Funny lump size in Vertices");
			}
		}
		if(normals != null) {
			Console.WriteLine("Normals lump: " + normals.Length + " bytes");
		}
		if(indices != null) {
			Console.WriteLine("Indices lump: " + indices.Length + " bytes, " + indices.Count + " items");
			if (indices.hasFunnySize()) {
				Console.WriteLine("WARNING: Funny lump size in Indices");
			}
		}
		if(vis != null) {
			Console.WriteLine("Visibility lump: " + vis.Length + " bytes, " + vis.Count + " items");
			if (vis.hasFunnySize()) {
				Console.WriteLine("WARNING: Funny lump size in Visibility");
			}
		}
		if(nodes != null) {
			Console.WriteLine("Nodes lump: " + nodes.Length + " bytes, " + nodes.Count + " items");
			if (nodes.hasFunnySize()) {
				Console.WriteLine("WARNING: Funny lump size in Nodes");
			}
		}
		if(faces != null) {
			Console.WriteLine("Faces lump: " + faces.Length + " bytes, " + faces.Count + " items");
			if (faces.hasFunnySize()) {
				Console.WriteLine("WARNING: Funny lump size in Faces");
			}
		}
		if(lightmaps != null) {
			Console.WriteLine("Lightmaps lump: " + lightmaps.Length + " bytes, " + lightmaps.Count + " items");
			if (lightmaps.hasFunnySize()) {
				Console.WriteLine("WARNING: Funny lump size in Lightmaps");
			}
		}
		if(leaves != null) {
			Console.WriteLine("Leaves lump: " + leaves.Length + " bytes, " + leaves.Count + " items");
			if (leaves.hasFunnySize()) {
				Console.WriteLine("WARNING: Funny lump size in Leaves");
			}
		}
		if(markSurfaces != null) {
			Console.WriteLine("Mark surfaces lump: " + markSurfaces.Length + " bytes, " + markSurfaces.Count + " items");
			if (markSurfaces.hasFunnySize()) {
				Console.WriteLine("WARNING: Funny lump size in Mark surfaces");
			}
		}
		if(markBrushes != null) {
			Console.WriteLine("Mark brushes lump: " + markBrushes.Length + " bytes, " + markBrushes.Count + " items");
			if (markBrushes.hasFunnySize()) {
				Console.WriteLine("WARNING: Funny lump size in Mark brushes");
			}
		}
		if(models != null) {
			Console.WriteLine("Models lump: " + models.Length + " bytes, " + models.Count + " items");
			if (models.hasFunnySize()) {
				Console.WriteLine("WARNING: Funny lump size in Models");
			}
		}
		if(brushes != null) {
			Console.WriteLine("Brushes lump: " + brushes.Length + " bytes, " + brushes.Count + " items");
			if (brushes.hasFunnySize()) {
				Console.WriteLine("WARNING: Funny lump size in Brushes");
			}
		}
		if(brushSides != null) {
			Console.WriteLine("Brush sides lump: " + brushSides.Length + " bytes, " + brushSides.Count + " items");
			if (brushSides.hasFunnySize()) {
				Console.WriteLine("WARNING: Funny lump size in Brush sides");
			}
		}
		if(texInfo != null) {
			Console.WriteLine("Texture info lump: " + texInfo.Length + " bytes, " + texInfo.Count + " items");
			if (texInfo.hasFunnySize()) {
				Console.WriteLine("WARNING: Funny lump size in Texture info");
			}
		}
	}
	
	// +getLeavesInModel(int)
	// Returns an array of Leaf containing all the leaves referenced from
	// this model's head node. This array cannot be referenced by index numbers
	// from other lumps, but if simply iterating through, getting information
	// it'll be just fine.
	public virtual Leaf[] getLeavesInModel(int model) {
		Leaf[] list = new Leaf[models[model].NumLeaves];
		for(int i=0;i<list.Length;i++) {
			list[i] = Leaves[models[model].FirstLeaf + i];
		}
		return list;
	}
	
	// +getLeavesInNode(int)
	// Returns an array of Leaf containing all the leaves referenced from
	// this node. Since nodes reference other nodes, this may recurse quite
	// some ways. Eventually every node will boil down to a set of leaves,
	// which is what this method returns.
	
	// This is an iterative preorder traversal algorithm modified from the Wikipedia page at:
	// http://en.wikipedia.org/wiki/Tree_traversal on April 19, 2012.
	// The cited example has since been removed but can still be found at
	// http://en.wikipedia.org/w/index.php?title=Tree_traversal&oldid=488219889#Inorder_Traversal
	// I needed an iterative algorithm because recursive ones commonly gave stack overflows.
	public virtual Leaf[] getLeavesInNode(int head) {
		Node headNode;
		Leaf[] nodeLeaves = new Leaf[0];
		if(head<0) { // WHY does this happen? What does it mean if this is negative?
			return nodeLeaves;
		} else {
			headNode = nodes[head];
		}
		Stack<Node> nodestack = new Stack<Node>();
		nodestack.Push(headNode);
		
		Node currentNode;
		
		while (!(nodestack.Count == 0)) {
			currentNode = nodestack.Pop();
			int right = currentNode.Child2;
			if (right >= 0) {
				nodestack.Push(nodes[right]);
			} else {
				Leaf[] newList = new Leaf[nodeLeaves.Length + 1];
				for (int i = 0; i < nodeLeaves.Length; i++) {
					newList[i] = nodeLeaves[i];
				}
				newList[nodeLeaves.Length] = leaves[(right * (- 1)) - 1]; // Quake 2 subtracts 1 from the index
				nodeLeaves = newList;
			}
			int left = currentNode.Child1;
			if (left >= 0) {
				nodestack.Push(nodes[left]);
			} else {
				Leaf[] newList = new Leaf[nodeLeaves.Length + 1];
				for (int i = 0; i < nodeLeaves.Length; i++) {
					newList[i] = nodeLeaves[i];
				}
				newList[nodeLeaves.Length] = leaves[(left * (- 1)) - 1]; // Quake 2 subtracts 1 from the index
				nodeLeaves = newList;
			}
		}
		return nodeLeaves;
	}

	public void Modified() {
		modified = true;
	}

	public void Saved() {
		modified = false;
	}

	// ACCESSORS/MUTATORS
	public virtual string Path {
		get {
			return filePath;
		}
	}

	public virtual string MapName {
		get {
			int i;
			for (i = 0; i < filePath.Length; i++) {
				if (filePath[filePath.Length - 1 - i] == '\\') {
					break;
				}
				if (filePath[filePath.Length - 1 - i] == '/') {
					break;
				}
			}
			return filePath.Substring(filePath.Length - i, (filePath.Length) - (filePath.Length - i));
		}
	}

	public virtual string MapNameNoExtension {
		get {
			string name = MapName;
			int i;
			for (i = 0; i < name.Length; i++) {
				if (name[name.Length - 1 - i] == '.') {
					break;
				}
			}
			return name.Substring(0, (name.Length - 1 - i) - (0));
		}
	}

	public virtual string Folder {
		get {
			int i;
			for (i = 0; i < filePath.Length; i++) {
				if (filePath[filePath.Length - 1 - i] == '\\') {
					break;
				}
				if (filePath[filePath.Length - 1 - i] == '/') {
					break;
				}
			}
			return filePath.Substring(0, (filePath.Length - i) - (0));
		}
	}

	public virtual Lump<Plane> Planes {
		set {
			planes = value;
		}
		get {
			return planes;
		}
	}

	public virtual Lump<Vertex> Vertices {
		set {
			vertices = value;
		}
		get {
			return vertices;
		}
	}

	public virtual Lump<Node> Nodes {
		set {
			nodes = value;
		}
		get {
			return nodes;
		}
	}

	public virtual Lump<TexInfo> TexInfo {
		set {
			texInfo = value;
		}
		get {
			return texInfo;
		}
	}

	public virtual Lump<Face> Faces {
		set {
			faces = value;
		}
		get {
			return faces;
		}
	}

	public virtual Lump<Leaf> Leaves {
		set {
			leaves = value;
		}
		get {
			return leaves;
		}
	}

	public virtual Lump<Model> Models {
		set {
			models = value;
		}
		get {
			return models;
		}
	}

	public virtual Lump<Brush> Brushes {
		set {
			brushes = value;
		}
		get {
			return brushes;
		}
	}

	public virtual Lump<BrushSide> BrushSides {
		set {
			brushSides = value;
		}
		get {
			return brushSides;
		}
	}

	public virtual Entities Entities {
		set {
			entities = value;
		}
		get {
			return entities;
		}
	}

	public virtual Lump<Texture> Textures {
		set {
			textures = value;
		}
		get {
			return textures;
		}
	}

	public virtual Lump<Texture> Materials {
		set {
			materials = value;
		}
		get {
			return materials;
		}
	}
	
	public virtual NumList MarkSurfaces {
		set {
			//switch (version) {
			//	case TYPE_QUAKE: 
					markSurfaces = value;
			//		markSurfaces = new NumList(data, NumList.TYPE_USHORT);
			//		break;
			//}
		}
		get {
			return markSurfaces;
		}
	}

	public virtual NumList MarkBrushes {
		get {
			return markBrushes;
		}
		set {
			markBrushes = value;
		}
	}

	public virtual Lump<LumpObject> Normals {
		get {
			return normals;
		}
		set {
			normals = value;
		}
	}

	public virtual Lump<LumpObject> Vis {
		get {
			return vis;
		}
		set {
			vis = value;
		}
	}

	public virtual NumList Lightmaps {
		get {
			return lightmaps;
		}
		set {
			lightmaps = value;
		}
	}

	public virtual NumList Indices {
		get {
			return indices;
		}
		set {
			indices = value;
		}
	}
}