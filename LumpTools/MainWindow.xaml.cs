using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using Microsoft.Win32;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;
using System.IO;
using System.Threading;
using System.Collections.ObjectModel;
using System.ComponentModel;
using System.Windows.Shell;

namespace LumpTools {
	/// <summary>
	/// Interaction logic for MainWindow.xaml
	/// </summary>
	public partial class MainWindow : Window {
		private BSP currentBSP;
		private Thread currentThread;
		private Queue<Thread> jobQueue = new Queue<Thread>();
		private Boolean working = false;

		public MainWindow() {
			InitializeComponent();
		}

		private void FileOpen_Click(object sender, RoutedEventArgs e) {
			OpenFileDialog fileOpener = new OpenFileDialog();
			fileOpener.Filter = "BSP Files|*.bsp|All Files|*.*";

			// Process open file dialog box results
			if (fileOpener.ShowDialog() == true) {
				string[] filesToOpen = fileOpener.FileNames;
				for(int i=0;i<filesToOpen.Length;i++) {
					BSPReader reader = new BSPReader(new FileInfo(filesToOpen[0]));
					reader.readBSP();
					currentBSP = reader.BSPData;
				}
			}
		}

		private void FileSave_Click(object sender, RoutedEventArgs e) {
			SaveFileDialog fileSaver = new SaveFileDialog();
			fileSaver.Filter = "BSP Files|*.bsp|All Files|*.*";

			// Process open file dialog box results 
			if (fileSaver.ShowDialog() == true) {
				string fileToSave = fileSaver.FileName;
				BSPWriter.writeBSP(currentBSP, fileToSave);
				currentBSP.Saved();
			}
		}

		private void FileClose_Click(object sender, RoutedEventArgs e) {
			currentBSP = null;
		}

		private void Optimize_Click(object sender, RoutedEventArgs e) {
			if(currentBSP != null) {
				Util.Optimizer.optimize(currentBSP);
				currentBSP.Modified();
			}
		}

		private void FlipX_Click(object sender, RoutedEventArgs e) {
			if(currentBSP != null) {
				Util.Flipper.Flip(currentBSP, 0);
				currentBSP.Modified();
			}
		}

		private void FlipY_Click(object sender, RoutedEventArgs e) {
			if(currentBSP != null) {
				Util.Flipper.Flip(currentBSP, 1);
				currentBSP.Modified();
			}
		}

		private void FlipZ_Click(object sender, RoutedEventArgs e) {
			if(currentBSP != null) {
				Util.Flipper.Flip(currentBSP, 2);
				currentBSP.Modified();
			}
		}

		private void SwapXY_Click(object sender, RoutedEventArgs e) {
			if(currentBSP != null) {
				Util.Swapper.Swap(currentBSP, 0, 1);
				currentBSP.Modified();
			}
		}

		private void SwapXZ_Click(object sender, RoutedEventArgs e) {
			if(currentBSP != null) {
				Util.Swapper.Swap(currentBSP, 0, 2);
				currentBSP.Modified();
			}
		}

		private void SwapYZ_Click(object sender, RoutedEventArgs e) {
			if(currentBSP != null) {
				Util.Swapper.Swap(currentBSP, 1, 2);
				currentBSP.Modified();
			}
		}

		private void Decompile_Click(object sender, RoutedEventArgs e) {
			if(currentBSP != null) {
				BSP42Decompiler decompiler42 = new BSP42Decompiler(currentBSP, 0);
				MAPMaker.outputMaps(decompiler42.decompile(), currentBSP.MapNameNoExtension, currentBSP.Folder);
			}
		}

		private void Quit_Click(object sender, RoutedEventArgs e) {
			Application.Current.Shutdown();
		}

		private void StartNextIfAble() {
			if(jobQueue.Count > 0 && !working) {
				working = true;
				currentThread = jobQueue.Dequeue();
				currentThread.Start();
			}
		}

		public void Corrupt_Click(object sender, RoutedEventArgs e) {
			Util.CorruptionMode mode = Util.CorruptionMode.RANDOM;
			if((bool)rad_sync.IsChecked) {
				mode = Util.CorruptionMode.REPLACE;
			}
			Util.CorruptionValue values = Util.CorruptionValue.ZERO;	
			if((bool)rad_relative.IsChecked) {
				values = Util.CorruptionValue.RELATIVE;
			}
			else if((bool)rad_random.IsChecked) {
				values = Util.CorruptionValue.RANDOM;
			}
			double range = 0.0;
			try {
				range = Double.Parse(txtVariance.Text);
			} catch { ; }
			double percentage = 1.0;
			try {
				percentage = Double.Parse(txtPercentage.Text) * 0.01;
			} catch { ; }
			Util.Corrupter.Corrupt(currentBSP, mode, values, range, percentage);
		}

		private void print(object sender, MessageEventArgs e) {
			this.Dispatcher.Invoke((Action)(() => {
				txtConsole.AppendText(e.Message+"\n");
				if(txtConsole.SelectionLength==0) {
					txtConsole.ScrollToEnd();
				}
			}));
		}

		private void threadFinished(object sender, EventArgs e) {
			this.Dispatcher.Invoke((Action)(() => {
				sender = null;
				working = false;
				StartNextIfAble();
			}));
		}
	}

	public class MessageEventArgs : EventArgs {
		private string message;
		public MessageEventArgs(string message) {
			this.message = message;
		}
		public string Message {
			get {
				return message;
			}
		}
	}
}
