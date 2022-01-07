package draw;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;

public class Drawer extends JPanel {
	
	private Color color;
	private HashMap<Point, Color> points = new HashMap<>();
	
	Color selectColor = new Color(0, 0, 0, 80);
	
	private MouseInputAdapter drawMotioListener = new MouseInputAdapter() {
		@Override
		public void mouseDragged(MouseEvent e) {

			int x = e.getX() / pixelWidth;
			int y = e.getY() / pixelWidth;

			points.put(new Point(x, y), color);

			Drawer.this.repaint();

			
		}
		@Override
		public void mouseMoved(MouseEvent e) {
			curCellX = e.getX() / pixelWidth;
			curCellY = e.getY() / pixelWidth;
			
			Drawer.this.repaint();
			
			
		}
	};
	
	private MouseAdapter drawAdapter = new MouseAdapter() {
		@Override
		public void mousePressed(MouseEvent e) {
			int butt = e.getButton();
			int x = e.getX() / pixelWidth;
			int y = e.getY() / pixelWidth;
			if(butt == MouseEvent.BUTTON1) {
				points.put(new Point(x, y), color);
			} else if(butt == MouseEvent.BUTTON3) {
				points.remove(new Point(x, y));
			}
			Drawer.this.repaint();
		}
	};
	
	private MouseInputAdapter exportMotioListener = new MouseInputAdapter() {
		@Override
		public void mouseDragged(MouseEvent e) {

			int x = e.getX() / pixelWidth;
			int y = e.getY() / pixelWidth;

			endSelect =  new Point(x, y);

			Drawer.this.repaint();

			
		}
		@Override
		public void mouseMoved(MouseEvent e) {
			curCellX = e.getX() / pixelWidth;
			curCellY = e.getY() / pixelWidth;
			
			Drawer.this.repaint();
			
			
		}
	};
	protected Point startSelect;
	protected Point endSelect;
	
	private MouseAdapter exportAdapter = new MouseAdapter() {
		@Override
		public void mousePressed(MouseEvent e) {
			int butt = e.getButton();
			int x = e.getX() / pixelWidth;
			int y = e.getY() / pixelWidth;
			if(butt == MouseEvent.BUTTON1) {
				startSelect =  new Point(x, y);
				endSelect =  new Point(x, y);
			}
			Drawer.this.repaint();
		}
	};
	public Drawer(Color color) {
		this.color = color;
		
		addMouseMotionListener(drawMotioListener);
		addMouseListener(drawAdapter);
	
	}
	public Drawer(HashMap<Point, Color> points) {
		this.points = points;
		addMouseMotionListener(exportMotioListener);
		addMouseListener(exportAdapter);
	}
	
	
	private int pixelWidth = 32;
	private int curCellX;
	private int curCellY;
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(Color.LIGHT_GRAY);
		
		int h = getHeight();
		int w = getWidth();
		int x = 0;
		int y = 0;
		
		while(x <= w) {
			g.drawLine(x, 0, x, h);
			x += pixelWidth;
		}
		
		while(y <= h) {
			g.drawLine(0, y, w, y);
			y += pixelWidth;
		}
		
		g.setColor(this.color);
		g.drawRect(curCellX * pixelWidth, curCellY * pixelWidth,
					pixelWidth, pixelWidth);
		
		drawImage(g, pixelWidth, false);
		
		if(startSelect != null && endSelect != null) {
			g.setColor(selectColor);
			g.fillRect(startSelect.x * pixelWidth, startSelect.y * pixelWidth,
					(endSelect.x - startSelect.x + 1) * pixelWidth,
					(endSelect.y - startSelect.y + 1) * pixelWidth);
		}
		
		g.setColor(Color.gray);
		g.drawRect(curCellX * pixelWidth - 1, curCellY * pixelWidth - 1,
					pixelWidth + 2, pixelWidth + 2);
		
	}
	
	private void drawImage(Graphics g, int pixelWidth1, boolean normilze) {
		Set<Point> set = points.keySet();
		for (Point p : set) {
			g.setColor(points.get(p));
			if(normilze && startSelect != null && endSelect != null) {
				int x = Math.min(startSelect.x, endSelect.x);
				int y = Math.min(startSelect.y, endSelect.y);
				
				g.fillRect((p.x - x) * pixelWidth1, (p.y - y) * pixelWidth1,
						pixelWidth1, pixelWidth1);
				
			} else {
				g.fillRect(p.x * pixelWidth1, p.y * pixelWidth1,
						pixelWidth1, pixelWidth1);
			}
			
			
		}
	}
	
	public void setColor(Color color) {
		this.color = color;
	}

	public HashMap<Point, Color> getPoints() {
		return points;
	}
	
	public boolean save(int pixelSize, String imageName) throws IOException {
		if(startSelect == null || endSelect == null) {
			return false;
		}
		int sizeX = Math.abs(startSelect.x - endSelect.x) * pixelSize;
		int sizeY = Math.abs(startSelect.y - endSelect.y) * pixelSize;
		final BufferedImage res = new BufferedImage( sizeX, sizeY, BufferedImage.TYPE_INT_ARGB );
		Graphics2D g = res.createGraphics();
		drawImage(g, pixelSize, true);
		ImageIO.write(res, "png", new File(imageName));
		return true;
	}
}
