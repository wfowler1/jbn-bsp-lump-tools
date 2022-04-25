using System;
using System.Numerics;
using System.Collections.Generic;
using System.Linq;

using LibBSP;

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
		
		public static void Corrupt(BSP me, CorruptionMode mode, CorruptionValue values, float range, float percentage) {
			if (percentage == 0.0) { return; }
			if (mode == CorruptionMode.RANDOM) {
				RandomCorruption(me, values, range, percentage);
			} else {
				ReplacingCorruption(me, values, range, percentage);
			}
		}

		public static void RandomCorruption(BSP me, CorruptionValue values, float range, float percentage) {
			Random rand = new Random();
			for (int i = 0; i < me.Vertices.Count; ++i) {
				if (values == CorruptionValue.ZERO) {
					Vertex v = me.Vertices[i];
					Vector3 position = v.position;
					double probability = rand.NextDouble();

					if (probability < percentage) {
						position.X = 0;
						position.Y = 0;
						position.Z = 0;
					} else if (values == CorruptionValue.RELATIVE) {
						position.X += (float)(rand.NextDouble() * 2.0f * range) - range;
						position.Y += (float)(rand.NextDouble() * 2.0f * range) - range;
						position.Z += (float)(rand.NextDouble() * 2.0f * range) - range;
					} else {
						position.X = (float)(rand.NextDouble() * 2.0f * range) - range;
						position.Y = (float)(rand.NextDouble() * 2.0f * range) - range;
						position.Z = (float)(rand.NextDouble() * 2.0f * range) - range;
					}

					v.position = position;
					me.Vertices[i] = v;
				}
			}

			for (int i = 0; i < me.Planes.Count; ++i) {
				Plane p = me.Planes[i];
				double probability = rand.NextDouble();

				if (probability < percentage) {
					if (values == CorruptionValue.ZERO) {
						p.D = 0.0f;
					} else if (values == CorruptionValue.RELATIVE) {
						p.D += (float)(rand.NextDouble() * 2.0f * range) - range;
					} else {
						p.D = (float)(rand.NextDouble() * 2.0f * range) - range;
					}
				}

				me.Planes[i] = p;
			}

		}

		public static void ReplacingCorruption(BSP me, CorruptionValue values, float range, float percentage) {
			Random rand = new Random();

			// Populate a list with all possible values to be replaced
			List<float> valuesToReplace = new List<float>();
			
			foreach (Vertex v in me.Vertices) {
				Vector3 position = v.position;
				for (int i = 0; i < 2; i++) {
					if (!valuesToReplace.Contains(position.X)) {
						valuesToReplace.Add(position.X);
					}
					if (!valuesToReplace.Contains(position.Y)) {
						valuesToReplace.Add(position.Y);
					}
					if (!valuesToReplace.Contains(position.Z)) {
						valuesToReplace.Add(position.Z);
					}
				}
			}

			foreach (Plane p in me.Planes) {
				if (!valuesToReplace.Contains(p.D)) {
					valuesToReplace.Add(p.D);
				}
			}

			// Figure out, based on the probability of corruption, how many values to corrupt from the list
			int numCorruptions = (int)Math.Ceiling(valuesToReplace.Count * percentage);

			// Perform that many corruptions
			for (int i = 0; i < numCorruptions; i++) {
				int indexToCorrupt = rand.Next(0, valuesToReplace.Count);
				float valueToCorrupt = valuesToReplace[indexToCorrupt];
				float newValue = 0.0f;
				if (values == CorruptionValue.RELATIVE) {
					newValue = valueToCorrupt + (float)(rand.NextDouble() * 2.0f * range) - range;
				} else if (values == CorruptionValue.RANDOM) {
					newValue = (float)(rand.NextDouble() * 2.0f * range) - range;
				}

				// Replace EVERY instance of the value with the corruption
				valuesToReplace[indexToCorrupt] = newValue;

				for (int j = 0; j < me.Vertices.Count; ++j) {
					Vertex v = me.Vertices[j];
					Vector3 position = v.position;

					if (position.X == valueToCorrupt) {
						position.X = newValue;
					}
					if (position.Y == valueToCorrupt) {
						position.Y = newValue;
					}
					if (position.Z == valueToCorrupt) {
						position.Z = newValue;
					}

					v.position = position;
					me.Vertices[j] = v;
				}

				for (int j = 0; j < me.Planes.Count; ++j) {
					Plane p = me.Planes[j];
					
					if (p.D == valueToCorrupt) {
						p.D = newValue;
					}

					me.Planes[j] = p;
				}
			}

		}
	}
}
