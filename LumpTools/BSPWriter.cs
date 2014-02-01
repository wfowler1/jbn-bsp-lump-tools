using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.IO;

public static class BSPWriter {

	public static void writeBSP(BSP BSPObject, string path) {
		byte[] entityBytes = new byte[1];
		byte[] planeBytes = new byte[BSPObject.Planes.Count * 20];
		byte[] textureBytes = new byte[BSPObject.Textures.Count * 64];
		byte[] materialBytes = new byte[BSPObject.Materials.Count * 64];
		byte[] vertexBytes = new byte[BSPObject.Vertices.Count * 12];
		byte[] normalBytes = new byte[BSPObject.Normals.Length];
		byte[] indexBytes = new byte[BSPObject.Indices.Count * 4];
		byte[] visBytes = new byte[BSPObject.Vis.Count * BSPObject.Vis.StructLength];
		byte[] nodeBytes = new byte[BSPObject.Nodes.Count * 36];
		byte[] faceBytes = new byte[BSPObject.Faces.Count * 48];
		byte[] lightBytes = new byte[BSPObject.Lightmaps.Count];
		byte[] leafBytes = new byte[BSPObject.Leaves.Count * 48];
		byte[] markfaceBytes = new byte[BSPObject.MarkSurfaces.Count * 4];
		byte[] markbrushBytes = new byte[BSPObject.MarkBrushes.Count * 4];
		byte[] modelBytes = new byte[BSPObject.Models.Count * 56];
		byte[] brushBytes = new byte[BSPObject.Brushes.Count * 12];
		byte[] brushsideBytes = new byte[BSPObject.BrushSides.Count * 8];
		byte[] texinfoBytes = new byte[BSPObject.TexInfo.Count * 32];

		int offset = 0;
		foreach(Entity e in BSPObject.Entities) {
			byte[] temp = e.toByteArray();
			Array.Resize<byte>(ref entityBytes, entityBytes.Length + temp.Length);
			Array.Copy(temp, 0, entityBytes, offset, temp.Length);
			offset += temp.Length;
		}

		offset = 0;
		foreach(Plane p in BSPObject.Planes) {
			Array.Copy(p.toByteArray(), 0, planeBytes, offset, 20);
			offset += 20;
		}

		offset = 0;
		foreach(Texture t in BSPObject.Textures) {
			Array.Copy(t.toByteArray(), 0, textureBytes, offset, 64);
			offset += 64;
		}

		offset = 0;
		foreach(Texture t in BSPObject.Materials) {
			Array.Copy(t.toByteArray(), 0, materialBytes, offset, 64);
			offset += 64;
		}

		offset = 0;
		foreach(Vertex v in BSPObject.Vertices) {
			Array.Copy(v.toByteArray(), 0, vertexBytes, offset, 12);
			offset += 12;
		}

		offset = 0;
		foreach(long l in BSPObject.Indices) {
			Array.Copy(BitConverter.GetBytes((int)l), 0, indexBytes, offset, 4);
			offset += 4;
		}

		offset = 0;
		foreach(LumpObject o in BSPObject.Vis) {
			Array.Copy(o.Data, 0, visBytes, offset, BSPObject.Vis.StructLength);
			offset += BSPObject.Vis.StructLength;
		}

		offset = 0;
		foreach(Node n in BSPObject.Nodes) {
			Array.Copy(n.toByteArray(), 0, nodeBytes, offset, 36);
			offset += 36;
		}

		offset = 0;
		foreach(Face f in BSPObject.Faces) {
			Array.Copy(f.toByteArray(), 0, faceBytes, offset, 48);
			offset += 48;
		}

		offset = 0;
		foreach(long l in BSPObject.Lightmaps) {
			lightBytes[offset++] = (byte)l;
		}

		offset = 0;
		foreach(Leaf l in BSPObject.Leaves) {
			Array.Copy(l.toByteArray(), 0, leafBytes, offset, 48);
			offset += 48;
		}

		offset = 0;
		foreach(long l in BSPObject.MarkSurfaces) {
			Array.Copy(BitConverter.GetBytes((int)l), 0, markfaceBytes, offset, 4);
			offset += 4;
		}

		offset = 0;
		foreach(long l in BSPObject.MarkBrushes) {
			Array.Copy(BitConverter.GetBytes((int)l), 0, markbrushBytes, offset, 4);
			offset += 4;
		}

		offset = 0;
		foreach(Model m in BSPObject.Models) {
			Array.Copy(m.toByteArray(), 0, modelBytes, offset, 56);
			offset += 56;
		}

		offset = 0;
		foreach(Brush b in BSPObject.Brushes) {
			Array.Copy(b.toByteArray(), 0, brushBytes, offset, 12);
			offset += 12;
		}

		offset = 0;
		foreach(BrushSide b in BSPObject.BrushSides) {
			Array.Copy(b.toByteArray(), 0, brushsideBytes, offset, 8);
			offset += 8;
		}

		offset = 0;
		foreach(TexInfo t in BSPObject.TexInfo) {
			Array.Copy(t.toByteArray(), 0, texinfoBytes, offset, 32);
			offset += 32;
		}

		byte[] header = new byte[148];
		header[0] = 42;
		offset = 148;
		byte[] headertemp = BitConverter.GetBytes(offset);
		Array.Copy(headertemp, 0, header, 4, 4);
		headertemp = BitConverter.GetBytes(entityBytes.Length);
		Array.Copy(headertemp, 0, header, 8, 4);
		offset += entityBytes.Length;
		headertemp = BitConverter.GetBytes(offset);
		Array.Copy(headertemp, 0, header, 12, 4);
		headertemp = BitConverter.GetBytes(planeBytes.Length);
		Array.Copy(headertemp, 0, header, 16, 4);
		offset += planeBytes.Length;
		headertemp = BitConverter.GetBytes(offset);
		Array.Copy(headertemp, 0, header, 20, 4);
		headertemp = BitConverter.GetBytes(textureBytes.Length);
		Array.Copy(headertemp, 0, header, 24, 4);
		offset += textureBytes.Length;
		headertemp = BitConverter.GetBytes(offset);
		Array.Copy(headertemp, 0, header, 28, 4);
		headertemp = BitConverter.GetBytes(materialBytes.Length);
		Array.Copy(headertemp, 0, header, 32, 4);
		offset += materialBytes.Length;
		headertemp = BitConverter.GetBytes(offset);
		Array.Copy(headertemp, 0, header, 36, 4);
		headertemp = BitConverter.GetBytes(vertexBytes.Length);
		Array.Copy(headertemp, 0, header, 40, 4);
		offset += vertexBytes.Length;
		headertemp = BitConverter.GetBytes(offset);
		Array.Copy(headertemp, 0, header, 44, 4);
		headertemp = BitConverter.GetBytes(normalBytes.Length);
		Array.Copy(headertemp, 0, header, 48, 4);
		offset += normalBytes.Length;
		headertemp = BitConverter.GetBytes(offset);
		Array.Copy(headertemp, 0, header, 52, 4);
		headertemp = BitConverter.GetBytes(indexBytes.Length);
		Array.Copy(headertemp, 0, header, 56, 4);
		offset += indexBytes.Length;
		headertemp = BitConverter.GetBytes(offset);
		Array.Copy(headertemp, 0, header, 60, 4);
		headertemp = BitConverter.GetBytes(visBytes.Length);
		Array.Copy(headertemp, 0, header, 64, 4);
		offset += visBytes.Length;
		headertemp = BitConverter.GetBytes(offset);
		Array.Copy(headertemp, 0, header, 68, 4);
		headertemp = BitConverter.GetBytes(nodeBytes.Length);
		Array.Copy(headertemp, 0, header, 72, 4);
		offset += nodeBytes.Length;
		headertemp = BitConverter.GetBytes(offset);
		Array.Copy(headertemp, 0, header, 76, 4);
		headertemp = BitConverter.GetBytes(faceBytes.Length);
		Array.Copy(headertemp, 0, header, 80, 4);
		offset += faceBytes.Length;
		headertemp = BitConverter.GetBytes(offset);
		Array.Copy(headertemp, 0, header, 84, 4);
		headertemp = BitConverter.GetBytes(lightBytes.Length);
		Array.Copy(headertemp, 0, header, 88, 4);
		offset += lightBytes.Length;
		headertemp = BitConverter.GetBytes(offset);
		Array.Copy(headertemp, 0, header, 92, 4);
		headertemp = BitConverter.GetBytes(leafBytes.Length);
		Array.Copy(headertemp, 0, header, 96, 4);
		offset += leafBytes.Length;
		headertemp = BitConverter.GetBytes(offset);
		Array.Copy(headertemp, 0, header, 100, 4);
		headertemp = BitConverter.GetBytes(markfaceBytes.Length);
		Array.Copy(headertemp, 0, header, 104, 4);
		offset += markfaceBytes.Length;
		headertemp = BitConverter.GetBytes(offset);
		Array.Copy(headertemp, 0, header, 108, 4);
		headertemp = BitConverter.GetBytes(markbrushBytes.Length);
		Array.Copy(headertemp, 0, header, 112, 4);
		offset += markbrushBytes.Length;
		headertemp = BitConverter.GetBytes(offset);
		Array.Copy(headertemp, 0, header, 116, 4);
		headertemp = BitConverter.GetBytes(modelBytes.Length);
		Array.Copy(headertemp, 0, header, 120, 4);
		offset += modelBytes.Length;
		headertemp = BitConverter.GetBytes(offset);
		Array.Copy(headertemp, 0, header, 124, 4);
		headertemp = BitConverter.GetBytes(brushBytes.Length);
		Array.Copy(headertemp, 0, header, 128, 4);
		offset += brushBytes.Length;
		headertemp = BitConverter.GetBytes(offset);
		Array.Copy(headertemp, 0, header, 132, 4);
		headertemp = BitConverter.GetBytes(brushsideBytes.Length);
		Array.Copy(headertemp, 0, header, 136, 4);
		offset += brushsideBytes.Length;
		headertemp = BitConverter.GetBytes(offset);
		Array.Copy(headertemp, 0, header, 140, 4);
		headertemp = BitConverter.GetBytes(texinfoBytes.Length);
		Array.Copy(headertemp, 0, header, 144, 4);
		offset += texinfoBytes.Length;

		FileStream stream = new FileStream(path, FileMode.Create, FileAccess.Write);
		BinaryWriter bw = new BinaryWriter(stream);
		stream.Seek(0, SeekOrigin.Begin);
		bw.Write(header);
		bw.Write(entityBytes);
		bw.Write(planeBytes);
		bw.Write(textureBytes);
		bw.Write(materialBytes);
		bw.Write(vertexBytes);
		bw.Write(normalBytes);
		bw.Write(indexBytes);
		bw.Write(visBytes);
		bw.Write(nodeBytes);
		bw.Write(faceBytes);
		bw.Write(lightBytes);
		bw.Write(leafBytes);
		bw.Write(markfaceBytes);
		bw.Write(markbrushBytes);
		bw.Write(modelBytes);
		bw.Write(brushBytes);
		bw.Write(brushsideBytes);
		bw.Write(texinfoBytes);
		bw.Close();

		Console.WriteLine("FIINISH");
	}

}
