using System;
using System.Collections;
using System.Collections.Generic;
using System.Text;
// Entity class

// This class holds data on ONE entity. It's only really useful when
// used in an array along with many others. Each value is stored as
// a separate attribute, in a dictionary.

// I've also added the ability to add MAPBrush objects to the entity,
// to be processed later on.
[Serializable]
public class Entity:LumpObject {
	
	// INITIAL DATA DECLARATION AND DEFINITION OF CONSTANTS
	
	private Dictionary<string, string> attributes = new Dictionary<string, string>(StringComparer.InvariantCultureIgnoreCase);
	private List<MAPBrush> brushes = new List<MAPBrush>();
	
	// For building more Source engine connections, a simple state machine.
	private bool fired = false;
	
	// CONSTRUCTORS
	
	public Entity(byte[] data):this(Encoding.ASCII.GetString(data).Split((char)0x0A)) {
	}
	
	public Entity(string classname):base(new byte[0]) {
		attributes.Add("classname", classname);
	}
	
	public Entity(string[] atts):base(new byte[0]) {
		attributes = new Dictionary<string, string>(atts.Length, StringComparer.InvariantCultureIgnoreCase);
		for (int i = 0; i < atts.Length; i++) {
			addAttribute(atts[i]);
		}
	}
	
	public Entity():base(new byte[0]) {
	}
	
	public Entity(Entity copy):base(copy.Data) {
		attributes = new Dictionary<string, string>(copy.Attributes);
		brushes = new List<MAPBrush>(copy.Brushes);
	}

	public Entity(Dictionary<string, string> atts, List<MAPBrush> brshs):base(new byte[0]) {
		attributes = new Dictionary<string,string>(atts);
		brushes = new List<MAPBrush>(brshs);
	}

	public Entity(Dictionary<string, string> atts):this(atts, new List<MAPBrush>()) {
	}
	
	// METHODS
	
	// renameAttribute(String, String)
	// Renames the specified attribute to the second String.
	public virtual void renameAttribute(string attribute, string to) {
		if(attributes.ContainsKey(attribute)) {
			string val = attributes[attribute];
			attributes.Remove(attribute);
			if(attributes.ContainsKey(to)) { attributes.Remove(to); Console.WriteLine("WARNING: Attribute "+to+" already existed in entity, overwritten!"); }
			attributes.Add(to, val);
		}
	}
	
	// addAttribute(String)
	// Simply adds the input String to the attribute list. This String can be anything,
	// even containing newlines or curly braces. BE CAREFUL.
	public virtual void addAttribute(string st) {
		string key = "";
		string val = "";
		bool inQuotes = false;
		bool isVal = false;
		for(int i = 0; i < st.Length; i++) {
			// Some entity values in Source can use escape sequenced quotes. Need to make sure not to parse those.
			if(st[i]=='\"' && (i==0 || st[i-1]!='\\')) {
				if(inQuotes) {
					if(isVal) {
						break;
					}
					isVal=true;
				}
				inQuotes = !inQuotes;
			} else {
				if(inQuotes) {
					if(!isVal) {
						key+=st[i];
					} else {
						val+=st[i];
					}
				}
			}
		}
		val.Replace("\\\"", "\"");
		if(!string.IsNullOrWhiteSpace(key)) {
			if(!attributes.ContainsKey(key)) {
				attributes.Add(key, val);
			}
		}
	}

	public bool Remove(string target) {
		return attributes.Remove(target);
	}

	public void Add(string key, string val) {
		if(attributes.ContainsKey(key)) {
			attributes.Remove(key);
		}
		attributes.Add(key, val);
	}
	
	// +toString()
	// Returns the entity as an ASCII entity structure. The output of this method
	// reads as a complete entity that could be put into a map with no problems,
	// and that will be the primary use case for this method. Be sure to add the
	// newlines and curly braces around the entity if using this method to create
	// a new entities lump file, unless curly braces are part of the attributes
	// array.
	[Obsolete]
	public override string ToString() {
		string output = "entity {\n";
		foreach (string st in attributes.Keys) {
			output += "\""+st+"\" \""+attributes[st]+"\"\n";
		}
		foreach (MAPBrush brush in brushes) {
			output += "brush {\n"+brush.ToString()+"\n}\n";
		}
		return output+"}";
	}
	
	// attributeIs(String, String)
	// Returns true if the attribute String1 exists and is equivalent to String2
	public bool attributeIs(string attribute, string check) {
		return check.Equals(this[attribute], StringComparison.InvariantCultureIgnoreCase);
	}
	
	// Returns true if the bits in "spawnflags" corresponding to the set bits in 'check' are set
	public bool spawnflagsSet(int check) {
		return ((Spawnflags & check) == check);
	}
	
	// Toggles the bits in "spawnflags" which are set in "check"
	public void toggleSpawnflags(int toggle) {
		this["spawnflags"]=(Spawnflags ^ toggle).ToString();
	}
	
	// Disables the bits in "spawnflags" which are set in "check"
	// Alternate method: spawnflags = (disable ^ 0xFFFFFFFF) & spawnflags
	public void disableSpawnflags(int disable) {
		toggleSpawnflags(Spawnflags & disable);
	}
	
	// Enables the bits in "spawnflags" which are set in "check"
	public void enableSpawnflags(int enable) {
		this["spawnflags"]=(Spawnflags | enable).ToString();
	}
	
	public static Entity cloneNoBrushes(Entity copy) {
		return new Entity(copy.Attributes);
	}
	
	public virtual Entity cloneNoBrushes() {
		return new Entity(attributes);
	}

	public static Entity parseString(string data) {
		return new Entity(data.Split((char)0x0A));
	}
	
	public static Entities createLump(byte[] data) {
		int count = 0;
		bool inQuotes = false; // Keep track of whether or not we're currently in a set of quotation marks.
		// I came across a map where the idiot map maker used { and } within a value. This broke the code prior to revision 55.
		for (int i = 0; i < data.Length; i++) {
			if (inQuotes) {
				if (data[i] == '\"' && inQuotes) {
					inQuotes = false;
				}
			} else {
				if (data[i] == '\"') {
					inQuotes = true;
				} else {
					if (data[i] == '{') {
						count++;
					}
				}
			}
		}
		Entities lump = new Entities(data.Length, count);
		char currentChar; // The current character being read in the file. This is necessary because
		// we need to know exactly when the { and } characters occur and capture
		// all text between them.
		int offset = 0;
		for (int i = 0; i < count; i++) {
			// For every entity
			string current = ""; // This will be the resulting entity, fed into the Entity class
			currentChar = (char)data[offset]; // begin reading the file
			while (currentChar != '{') {
				// Eat bytes until we find the beginning of an entity structure
				offset++;
				currentChar = (char)data[offset];
			}
			inQuotes = false;
			do {
				if (currentChar == '\"') {
					inQuotes = !inQuotes;
				}
				current += (currentChar + ""); // adds characters to the current string
				offset++;
				currentChar = (char)data[offset];
			} while (currentChar != '}' || inQuotes); // Read bytes until we find the end of the current entity structure
			current += (currentChar + ""); // adds the '}' to the current string
			lump.Add(Entity.parseString(current));
		}
		return lump;
	}

	public byte[] toByteArray() {
		int len = 4;
		foreach(string key in attributes.Keys) {
			len += key.Length + attributes[key].Length + 6;
		}
		byte[] res = new byte[len];
		int offset = 0;
		res[offset++] = (byte)'{';
		res[offset++] = (byte)0x0A;
		foreach(string key in attributes.Keys) {
			res[offset++] = (byte)'\"'; // 1
			for(int i=0;i<key.Length;i++) {
				res[offset++] = (byte)key[i];
			}
			res[offset++] = (byte)'\"'; // 2
			res[offset++] = (byte)' '; // 3
			res[offset++] = (byte)'\"'; // 4
			for(int i=0;i<attributes[key].Length;i++) {
				res[offset++] = (byte)attributes[key][i];
			}
			res[offset++] = (byte)'\"'; // 5
			res[offset++] = (byte)0x0A; // 6
		}
		res[offset++] = (byte)'}';
		res[offset++] = (byte)0x0A;
		return res;
	}
	
	// ACCESSORS/MUTATORS
	virtual public bool BrushBased {
		get {
			return (brushes.Count > 0 || ModelNumber >= 0);
		}
	}

	virtual public int Spawnflags {
		get {
			try {
				return System.Int32.Parse(attributes["spawnflags"]);
			} catch {
				return 0;
			}
		}
	}

	virtual public bool Fired {
		get {
			return fired;
		}
		set {
			fired = value;
		}
	}

	virtual public Dictionary<string, string> Attributes {
		get {
			return attributes;
		}
	}

	virtual public List<MAPBrush> Brushes {
		get {
			return brushes;
		}
	}

	virtual public int ModelNumber {
		// If there's a model number in the attributes list, this method fetches it
		// and returns it. If there is no model defined, or it's not a numerical 
		// value, then -1 is returned. If it's the worldspawn then a 0 is returned.
		get {
			try {
				if(attributes["classname"] == "worldspawn") {
					return 0;
				} else {
					if(attributes.ContainsKey("model")) {
						string st = attributes["model"];
						if(st[0]=='*') {
							try {
								return System.Int32.Parse(st.Substring(1));
							} catch(System.FormatException) {
								return -1;
							}
						} else {
							return -1;
						}
					} else {
						return -1;
					}
				}
			} catch {
				return -1;
			}
		}
	}

	virtual public Vector3D Origin {
		// Returns the three components of the entity's "origin" attribute as a Vector3D
		get {
			double[] point = new double[3];
			if (attributes.ContainsKey("origin") && !string.IsNullOrWhiteSpace(attributes["origin"])) {
				string[] origin = attributes["origin"].Split(' ');
				for (int i = 0; i < 3 && i < origin.Length; i++) {
					try {
						point[i] = System.Double.Parse(origin[i]);
					} catch {
						return Vector3D.ZERO;
					}
				}
			}
			return new Vector3D(point);
		}
		set {
			this["origin"] = value.X+" "+value.Y+" "+value.Z;
		}
	}

	virtual public Vector3D Angles {
		// Returns the three components of the entity's "angles" attribute as a Vector3D
		get {
			double[] euler = new double[3];
			if (attributes.ContainsKey("angles") && !string.IsNullOrWhiteSpace(attributes["angles"])) {
				string[] angles = attributes["angles"].Split(' ');
				for (int i = 0; i < 3 && i < angles.Length; i++) {
					try {
						euler[i] = System.Double.Parse(angles[i]);
					} catch {
						return Vector3D.ZERO;
					}
				}
			}
			return new Vector3D(euler);
		}
		set {
			this["angles"] = value.X+" "+value.Y+" "+value.Z;
		}
	}
	
	// Set an attribute. If it doesn't exist, it is added. If it does, it is
	// overwritten with the new one, since that's much easier to do than edit
	// the preexisting one.
	public virtual string this[string attribute] {
		set {
			if(attributes.ContainsKey(attribute)) {
				attributes[attribute] = value;
			} else {
				attributes.Add(attribute, value);
			}
		}
		get {
			if(attributes.ContainsKey(attribute)) {
				return attributes[attribute];
			} else {
				return "";
			}
		}
	}
}