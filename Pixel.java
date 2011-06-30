// Pixel class

// Holds a byte3, for a pixel.

public class Pixel {
	
	// INITIAL DATA DECLARATION AND DEFINITION OF CONSTANTS
	
	public static final int R=0;
	public static final int G=1;
	public static final int B=2;
	
	private byte[] pixel=new byte[3];
	
	// CONSTRUCTORS
	
	// This constructor takes three bytes
	public Pixel(byte inR, byte inG, byte inB) {
		pixel[R]=inR;
		pixel[G]=inG;
		pixel[B]=inB;
	}
	
	// This constructor takes one byte[3]
	public Pixel(byte[] in) throws InvalidPixelException {
		if(in.length!=3) {
			throw new InvalidPixelException();
		}
		pixel=in;
	}
	
	// METHODS
	
	// ACCESSORS/MUTATORS
	
	public byte getR() {
		return pixel[R];
	}
	
	public byte getG() {
		return pixel[G];
	}
	
	public byte getB() {
		return pixel[B];
	}
	
	public void setR(byte in) {
		pixel[R]=in;
	}
	
	public void setG(byte in) {
		pixel[G]=in;
	}
	
	public void setB(byte in) {
		pixel[B]=in;
	}
}