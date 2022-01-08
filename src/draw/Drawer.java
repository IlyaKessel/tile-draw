package draw;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.event.MouseInputAdapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Drawer extends JPanel {

	public static final String EXT = ".pxd";
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
			
			Graphics2D g2d = (Graphics2D) g;

			Stroke dashed = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,
					0, new float[]{9}, 0);
			g2d.setStroke(dashed);
			g2d.setColor(Color.RED);
			g.drawRect(startSelect.x * pixelWidth, startSelect.y * pixelWidth,
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
		if(!imageName.endsWith(".png")) {
			imageName = imageName + ".png";
		}
		if(startSelect == null || endSelect == null) {
			return false;
		}
		
		String jsonStr = serializePoints(false);
		int i = imageName.lastIndexOf(".");
		String fileName = imageName.substring(0, i) + EXT;
		try (PrintWriter out = new PrintWriter(fileName)) {
		    out.println(jsonStr);
		}
		
		int sizeX = (Math.abs(startSelect.x - endSelect.x) + 1) * pixelSize;
		int sizeY = (Math.abs(startSelect.y - endSelect.y) + 1) * pixelSize;
		final BufferedImage res = new BufferedImage( sizeX, sizeY, BufferedImage.TYPE_INT_ARGB );
		Graphics2D g = res.createGraphics();
		drawImage(g, pixelSize, true);
		ImageIO.write(res, "png", new File(imageName));
		return true;
	}
	
	private String serializePoints(boolean all) throws JsonProcessingException {
		Set<Point> set = points.keySet();
		HashMap<String, HashMap<String, Integer>> data = new HashMap<>();
		
		int minX = 0, minY = 0, maxX = Integer.MAX_VALUE, maxY = Integer.MAX_VALUE;
		if(!all) {
			minX = Math.min(startSelect.x, endSelect.x);
			minY = Math.min(startSelect.y, endSelect.y);
			
			maxX = Math.max(startSelect.x, endSelect.x);
			maxY = Math.max(startSelect.y, endSelect.y);
		}
		
		for (Point p : set) {
			if(p.x < minX || p.x > maxX ||
			   p.y < minY || p.y > maxY) {
				continue;
			}
			Color c = points.get(p);
			HashMap<String, Integer> val = new HashMap<>();
			val.put("r", c.getRed());
			val.put("g", c.getGreen());
			val.put("b", c.getBlue());
			val.put("a", c.getAlpha());
			String key = p.x + ":" + p.y; 
			data.put(key, val);

		}
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(data);
	}
	
	public void loadFile(String filePath) throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		Map<?, ?> map = mapper.readValue(Paths.get(filePath).toFile(), Map.class);
		for (Map.Entry<?, ?> entry : map.entrySet()) {
			String cords = (String) entry.getKey();
			Map<String, Integer> clr = (Map<String, Integer>) entry.getValue();
			String p[] = cords.split(":");
			int x = Integer.parseInt(p[0]);
			int y = Integer.parseInt(p[1]);
			Point point = new Point(x, y);
			Color c = new Color(clr.get("r"), clr.get("g"), clr.get("b"), clr.get("a"));
			points.put(point, c);
			
//	        System.out.println(entry.getKey() + "=" + entry.getValue());
	    }
		repaint();
	}
	public String canSave() {
		if(startSelect == null || endSelect == null) {
			return "Select area to save";
		}

		return null;
	}
	public boolean saveProj(String fileName) throws Exception {
		if(!fileName.endsWith(EXT)) {
			fileName = fileName + EXT;
		}
		if(startSelect == null || endSelect == null) {
			return false;
		}
		
		String jsonStr = serializePoints(true);
		
		try (PrintWriter out = new PrintWriter(fileName)) {
		    out.println(jsonStr);
		}
		return true;
	}
}
