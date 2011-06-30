import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Scanner;

public class LumpTools {
	public static void main(String[] args) {
		Scanner keyboard = new Scanner(System.in);
		
		System.out.print("Path to lumps folder: ");
		String filepath=keyboard.nextLine();
		NFBSP myBSP = new NFBSP(filepath);
	}
}