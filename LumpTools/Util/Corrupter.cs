using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace LumpTools.Util {

	public enum CorruptionMode : int {
		RANDOM = 0,
		REPLACE = 1
	}

	public enum CorruptionValue : int {
		ZERO = 0,
		RELATIVE = 1,
		RANDOM = 2
	}

	public static class Corrupter {
		
		public static void Corrupt(BSP me, CorruptionMode mode, CorruptionValue values, double range, double percentage) {
			if(percentage == 0.0) { return; }
			if(mode == CorruptionMode.RANDOM) {
				RandomCorruption(me, values, range, percentage);
			} else {
				ReplacingCorruption(me, values, range, percentage);
			}
		}

		public static void RandomCorruption(BSP me, CorruptionValue values, double range, double percentage) {
			Random rand = new Random();
			foreach(Vertex v in me.Vertices) {
				for(int i=0;i<3;i++) {
					double probability = rand.NextDouble();
					if(probability < percentage) {
						if(values == CorruptionValue.ZERO) {
							v[i] = 0.0;
						} else if(values == CorruptionValue.RELATIVE) {
							v[i] += (rand.NextDouble()*2.0*range) - range;
						} else {
							v[i] = (rand.NextDouble()*2.0*range) - range;
						}
					}
				}
			}

			foreach(Plane p in me.Planes) {
				double probability = rand.NextDouble();
				if(probability < percentage) {
					if(values == CorruptionValue.ZERO) {
						p.Dist = 0.0;
					} else if(values == CorruptionValue.RELATIVE) {
						p.Dist += (rand.NextDouble()*2.0*range) - range;
					} else {
						p.Dist = (rand.NextDouble()*2.0*range) - range;
					}
				}
			}

		}

		public static void ReplacingCorruption(BSP me, CorruptionValue values, double range, double percentage) {
			Random rand = new Random();

			// Populate a list with all possible values to be replaced
			List<double> valuesToReplace = new List<double>();
			foreach(Vertex v in me.Vertices) {
				for(int i=0;i<2;i++) {
					if(!valuesToReplace.Contains(v[i])) {
						valuesToReplace.Add(v[i]);
					}
				}
			}
			foreach(Plane p in me.Planes) {
				if(!valuesToReplace.Contains(p.Dist)) {
					valuesToReplace.Add(p.Dist);
				}
			}

			// Figure out, based on the probability of corruption, how many values to corrupt from the list
			int numCorruptions = (int)Math.Ceiling(valuesToReplace.Count * percentage);

			// Perform that many corruptions
			for(int i=0;i<numCorruptions;i++) {
				int indexToCorrupt = rand.Next(0, valuesToReplace.Count);
				double valueToCorrupt = valuesToReplace[indexToCorrupt];
				double newValue = 0.0;
				if(values == CorruptionValue.RELATIVE) {
					newValue = valueToCorrupt + (rand.NextDouble()*2.0*range) - range;
				} else if(values == CorruptionValue.RANDOM) {
					newValue = (rand.NextDouble()*2.0*range) - range;
				}
				// Replace EVERY instance of the value with the corruption
				valuesToReplace[indexToCorrupt] = newValue;
				foreach(Vertex v in me.Vertices) {
					for(int j=0;j<3;j++) {
						if(v[j] == valueToCorrupt) {
							v[j] = newValue;
						}
					}
				}
				foreach(Plane p in me.Planes) {
					if(p.Dist == valueToCorrupt) {
						p.Dist = newValue;
					}
				}
			}

		}
	}
}
