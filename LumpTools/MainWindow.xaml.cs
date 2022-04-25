using System;
using System.Collections.Generic;
using System.Windows;
using Microsoft.Win32;
using System.Threading;
using System.IO;

using LibBSP;

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
				for (int i = 0; i < filesToOpen.Length; ++i) {
					currentBSP = new BSP(new FileInfo(filesToOpen[0]));
				}
			}
		}

		private void FileSave_Click(object sender, RoutedEventArgs e) {
			SaveFileDialog fileSaver = new SaveFileDialog();
			fileSaver.Filter = "BSP Files|*.bsp|All Files|*.*";

			// Process open file dialog box results 
			if (fileSaver.ShowDialog() == true) {
				string fileToSave = fileSaver.FileName;
				BSPWriter writer = new BSPWriter(currentBSP);
				writer.WriteBSP(fileToSave);
			}
		}

		private void FileClose_Click(object sender, RoutedEventArgs e) {
			currentBSP = null;
		}

		private void Optimize_Click(object sender, RoutedEventArgs e) {
			if (currentBSP != null) {
				Util.Optimizer.Optimize(currentBSP);
			}
		}

		private void FlipX_Click(object sender, RoutedEventArgs e) {
			if (currentBSP != null) {
				Util.Flipper.Flip(currentBSP, true, false, false);
			}
		}

		private void FlipY_Click(object sender, RoutedEventArgs e) {
			if (currentBSP != null) {
				Util.Flipper.Flip(currentBSP, false, true, false);
			}
		}

		private void FlipZ_Click(object sender, RoutedEventArgs e) {
			if (currentBSP != null) {
				Util.Flipper.Flip(currentBSP, false, false, true);
			}
		}

		private void SwapXY_Click(object sender, RoutedEventArgs e) {
			if (currentBSP != null) {
				Util.Swapper.Swap(currentBSP, true, false, false);
			}
		}

		private void SwapXZ_Click(object sender, RoutedEventArgs e) {
			if (currentBSP != null) {
				Util.Swapper.Swap(currentBSP, false, true, false);
			}
		}

		private void SwapYZ_Click(object sender, RoutedEventArgs e) {
			if (currentBSP != null) {
				Util.Swapper.Swap(currentBSP, false, false, true);
			}
		}

		private void Decompile_Click(object sender, RoutedEventArgs e) {
			if (currentBSP != null) {
				BSPDecompiler decompiler = new BSPDecompiler(currentBSP);
				Entities entities = decompiler.Decompile();
				GearcraftMapWriter writer = new GearcraftMapWriter(entities);
				File.WriteAllText(Path.Combine(currentBSP.Reader.BspFile.Directory.FullName, currentBSP.MapName + ".map"), writer.ParseMap());
			}
		}

		private void Quit_Click(object sender, RoutedEventArgs e) {
			Application.Current.Shutdown();
		}

		private void StartNextIfAble() {
			if (jobQueue.Count > 0 && !working) {
				working = true;
				currentThread = jobQueue.Dequeue();
				currentThread.Start();
			}
		}

		public void Corrupt_Click(object sender, RoutedEventArgs e) {
			Util.CorruptionMode mode = Util.CorruptionMode.RANDOM;
			if ((bool)rad_sync.IsChecked) {
				mode = Util.CorruptionMode.REPLACE;
			}
			Util.CorruptionValue values = Util.CorruptionValue.ZERO;	
			if ((bool)rad_relative.IsChecked) {
				values = Util.CorruptionValue.RELATIVE;
			}
			else if ((bool)rad_random.IsChecked) {
				values = Util.CorruptionValue.RANDOM;
			}
			float range = 0;
			try {
				range = float.Parse(txtVariance.Text);
			} catch { ; }
			float percentage = 1;
			try {
				percentage = float.Parse(txtPercentage.Text) * 0.01f;
			} catch { ; }
			Util.Corrupter.Corrupt(currentBSP, mode, values, range, percentage);
		}

		private void print(object sender, MessageEventArgs e) {
			Dispatcher.Invoke(() => {
				txtConsole.AppendText(e.Message+"\n");
				if (txtConsole.SelectionLength==0) {
					txtConsole.ScrollToEnd();
				}
			});
		}

		private void threadFinished(object sender, EventArgs e) {
			Dispatcher.Invoke(() => {
				sender = null;
				working = false;
				StartNextIfAble();
			});
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
