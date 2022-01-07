package draw;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

public class Main {

	private static Color curentColor = Color.BLACK;
	private static Drawer drawer = null;
	public static void main(String[] args) {
		JFrame window = new JFrame();
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setSize(new Dimension(1024, 800));
		window.setLayout(new BorderLayout());
		drawer = new Drawer(curentColor);
		window.setContentPane(drawer);
		
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("File");
		menuBar.add(menu);
		JMenuItem menuItem = new JMenuItem("Color",
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
				Drawer expDrawer = new Drawer(drawer.getPoints());
				expDrawer.setPreferredSize(new Dimension(window.getSize()));
				content.add(expDrawer, BorderLayout.CENTER);
				
				
				JPanel controls = new JPanel();
				controls.setLayout(new GridLayout());
				
				JPanel buttons = new JPanel();
				buttons.setLayout(new BorderLayout());
				
				content.add(buttons, BorderLayout.SOUTH);
				
				JButton ok = new JButton("Save");
				buttons.add(ok, BorderLayout.WEST);
				
				ok.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							boolean res = expDrawer.save(32, "C:\\Users\\User\\Pictures\\darkWorldmages\\image" + System.currentTimeMillis() +".png");
							if(res) {
								dialog.setVisible(false);
							}
						} catch (IOException e1) {
							e1.printStackTrace();
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
				dialog.setVisible(true);
			}
		});
		menu.add(menuItem);
		window.setJMenuBar(menuBar);
		window.setVisible(true);
	}
}
