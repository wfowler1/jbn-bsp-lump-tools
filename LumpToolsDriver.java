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
		NFBSP myBSP=new NFBSP(BSP);
		myBSP.flipX();
		myBSP.saveLumps(BSP+"FLIPX.BSP");
	}
}