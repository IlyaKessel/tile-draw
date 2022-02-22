package draw;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;

public class RecentColorsPicker extends JPanel implements MouseListener{
	private LinkedList<Color> colors = new LinkedList<Color>();
	private IColorChange ich;
	final int tileW = 20;
	private Settings settings;
	
	public static interface IColorChange {

		void colorChanged(Color color);
		
	}

	public RecentColorsPicker(IColorChange ich) {
		setSize(200, 200);
		setLocation(0, 50);
		this.settings = new Settings();
		List<String> colorsStrs = this.settings.getColors();
		for (String colorStr : colorsStrs) {
			colors.add(colorFromStr(colorStr));
		}
		this.ich = ich;
		this.addMouseListener(this);
	}
	
	public void addColor(Color c) {
//		String cStr = this.colorToStr(c);
		if(!colors.contains(c)) {
			if(colors.size() < 100) {
				colors.add(c);
			} else {
				colors.removeLast();
				colors.addFirst(c);
			}
			saveState();
			repaint();
		}
	}

	private void saveState() {
		ArrayList<String> strs = new ArrayList<String>();
		for (Color color : colors) {
			strs.add(colorToStr(color));
		}
		this.settings.saveColors(strs);
	}

	private String colorToStr(Color c) {
		return c.getRed() + ":" + c.getGreen() + ":" + c.getBlue() + ":" + c.getAlpha();
	}
	
	private Color colorFromStr(String str) {
		String[] p = str.split(":");
		return new Color(Integer.parseInt(p[0]), Integer.parseInt(p[1]), Integer.parseInt(p[2]), Integer.parseInt(p[3]));
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		

		final int h = getHeight();
		final int w = getWidth();
		final int c = w / tileW;
		
		for (int i = 0; i < c; i++) {
			for (int j = 0; j < c; j++) {
				int ind = i * c + j;
				if(ind < colors.size()) {
					g.setColor(colors.get(ind));
				} else {
					g.setColor(Color.WHITE);
				}
				g.fillRect(j * tileW, i * tileW, tileW, tileW);
			}
		}
		g.setColor(Color.gray);
		
		for (int i = 0; i < c; i++) {
			g.drawLine(i * tileW, 0, i * tileW, h);
		}
		for (int i = 0; i < c + 1; i++) {
			g.drawLine(0, i * tileW, w, i * tileW);
		}
		
		
//		g.drawRect(0, 0, getWidth(), getHeight());
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		int i = x / tileW;
		int j = y / tileW;
		final int w = getWidth();
		final int c = w / tileW;
		int ind = j * c + i;
		if(ind < colors.size()) {
			this.ich.colorChanged(colors.get(ind));
		} else {
			this.ich.colorChanged(Color.WHITE);
		}
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
