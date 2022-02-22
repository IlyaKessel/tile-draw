package draw;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import draw.ProjectView.IProjectViewChange;
import draw.RecentColorsPicker.IColorChange;

public class Main {

	private static Color curentColor = Color.BLACK;
	private static Drawer drawer = null;
	private static ProjectView projView;
	private static IProjectViewChange ipch = new IProjectViewChange() {
		
		@Override
		public void selectedFileChanged(String selectedFile) {
			try {
				drawer.setSelectedFile(selectedFile);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
	private static RecentColorsPicker recentColorsPicker;
	
	
	
	
	public static void main(String[] args) {
		JFrame window = new JFrame();
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setSize(new Dimension(1424, 800));
		window.setLayout(new BorderLayout());
		
		JPanel contenPane = new JPanel(new BorderLayout());
		window.setContentPane(contenPane);
		drawer = new Drawer(curentColor);
		contenPane.add(drawer, BorderLayout.CENTER);
		
		JPanel left = new JPanel(new BorderLayout());
		left.setPreferredSize(new Dimension(200, 1));
		contenPane.add(left, BorderLayout.WEST);
		
		projView = new ProjectView(ipch );
		left.add(projView , BorderLayout.CENTER);
		
		JPanel right = new JPanel(null);
		right.setPreferredSize(new Dimension(200, 1));
		recentColorsPicker = new RecentColorsPicker(new IColorChange() {
			
			@Override
			public void colorChanged(Color color) {
				drawer.setColor(color);
			}
		});
		right.add(recentColorsPicker);
		contenPane.add(right, BorderLayout.EAST);
		
		
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("File");
		menuBar.add(menu);
		
		
		JMenuItem menuItem = new JMenuItem("New File",
				KeyEvent.VK_T);
		menuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					projView.createNewFile();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			
		}});
		menu.add(menuItem);
		
		
		menuItem = new JMenuItem("Color",
				KeyEvent.VK_T);
		menuItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Color newColor = JColorChooser.showDialog(
						 window,
	                     "Choose Background Color",
	                     curentColor);
				if(newColor != null) {
					curentColor = newColor;
					recentColorsPicker.addColor(curentColor);
				}
				drawer.setColor(curentColor);
				
				
			}
		});
		menu.add(menuItem);
		
		
		menuItem = new JMenuItem("Save",
				KeyEvent.VK_T);
		menuItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JDialog dialog = new JDialog(window, "", Dialog.ModalityType.DOCUMENT_MODAL);
				JPanel content = new JPanel();
				content.setLayout(new BorderLayout());
				Drawer expDrawer = new Drawer(drawer.getPoints(), true);
				expDrawer.setPreferredSize(new Dimension(window.getSize()));
				content.add(expDrawer, BorderLayout.CENTER);
				
				
				JPanel controls = new JPanel();
				controls.setLayout(new GridLayout(0, 10));
				
				String [] pixelSizes = new String[64];
				for (int i = 0; i < pixelSizes.length; i++) {
					pixelSizes[i] = (i + 1) + "";
				}
				
				JComboBox<String> sizeList = new JComboBox<String>(pixelSizes);
				sizeList.setSelectedIndex(15);
				
				controls.add(new JLabel("Pxiel Size"));
				controls.add(sizeList);
				
				content.add(controls, BorderLayout.NORTH);

				JPanel buttons = new JPanel();
				buttons.setLayout(new BorderLayout());
				
				
				content.add(buttons, BorderLayout.SOUTH);
				
				JButton ok = new JButton("Save");
				buttons.add(ok, BorderLayout.WEST);
				
				ok.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						
						String err = expDrawer.canSave();
						if(err != null) {
							JOptionPane.showMessageDialog(dialog,
									err,
								    "Error",
								    JOptionPane.WARNING_MESSAGE);
							return;
						}
						
						JFileChooser fileChooser = new JFileChooser();
						fileChooser.setDialogTitle("Specify a file to save");
						 
						int userSelection = fileChooser.showSaveDialog(dialog);
						 
						if (userSelection == JFileChooser.APPROVE_OPTION) {
						    File fileToSave = fileChooser.getSelectedFile();
						    System.out.println("Save as file: " + fileToSave.getAbsolutePath());
						    
						try {
							boolean res = expDrawer.save(sizeList.getSelectedIndex() + 1,
									fileToSave.getAbsolutePath());
							if(res) {
								dialog.setVisible(false);
							}
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
					}
				});
				
				JButton cancel = new JButton("Cancel");
				cancel.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						dialog.setVisible(false);
					}
				});
				buttons.add(cancel, BorderLayout.EAST);
				
				dialog.setContentPane(content);
				dialog.pack();
				dialog.setLocationRelativeTo(null);
				dialog.setVisible(true);
			}
		});
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Load",
				KeyEvent.VK_T);
		menuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileFilter(new FileNameExtensionFilter(Drawer.EXT, Drawer.EXT.replace(".", "")));
				int result = fileChooser.showOpenDialog(window);
				if (result == JFileChooser.APPROVE_OPTION) {
				    File selectedFile = fileChooser.getSelectedFile();
				    try {
						drawer.loadFile(selectedFile.getAbsolutePath());
					} catch (Exception e1) {
						e1.printStackTrace();
						JOptionPane.showMessageDialog(window,
								e.toString(),
							    "Error",
							    JOptionPane.WARNING_MESSAGE);
					}
				}
			}
			
		});
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Project Save",
				KeyEvent.VK_T);
		menuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setDialogTitle("Specify a file to save");
				 
				int userSelection = fileChooser.showSaveDialog(window);
				 
				if (userSelection == JFileChooser.APPROVE_OPTION) {
				    File fileToSave = fileChooser.getSelectedFile();
				    System.out.println("Save as file: " + fileToSave.getAbsolutePath());
				    
				try {
					drawer.saveProj(
							fileToSave.getAbsolutePath());
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			
		}});
		menu.add(menuItem);
		
		window.setJMenuBar(menuBar);
		window.setLocationRelativeTo(null);
		window.setVisible(true);
	}
}
