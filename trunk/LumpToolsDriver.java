import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.util.Scanner;

public class LumpToolsDriver {
	public static void main(String[] args) {		
		/*
		JFrame frame = new JFrame("Lump Tools by 005");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Window window = new Window(frame.getContentPane());

		frame.setPreferredSize(new Dimension(800, 400));

		frame.pack();
		frame.setVisible(true);
		*/
		
		Scanner keyboard=new Scanner(System.in);
		System.out.print("BSP Path: ");
		String BSP=keyboard.nextLine();
		BSPReader bspreader=new BSPReader(BSP);
		NFBSP myBSP=bspreader.readBSP();
		myBSP.setPath(BSP);
		myBSP.optimizeBSP();
		System.out.println("\nWriting "+myBSP.getFolder()+myBSP.getMapName()+"_optim.bsp");
		BSPWriter myWriter = new BSPWriter(myBSP);
		myWriter.writeFile(myBSP.getFolder()+myBSP.getMapName()+"_optim.bsp");
	}
}