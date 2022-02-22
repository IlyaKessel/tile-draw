package draw;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class ProjectView extends JPanel implements TreeSelectionListener{
	private JTree tree;
	private IProjectViewChange ipch;
	private static final String DEFAULT_PROJECT = "DefaultProject";
	
	public static interface IProjectViewChange {

		void selectedFileChanged(String selectedFile);
		
	}

	public ProjectView(IProjectViewChange ipch) {
		super(new BorderLayout());
		this.ipch = ipch;
		setBorder(BorderFactory.createLineBorder(Color.red));
		
		CustomTreeModel model = new CustomTreeModel();
		tree = new JTree(model);
		tree.addTreeSelectionListener(this);
		
		add(new JScrollPane(tree), BorderLayout.CENTER);
	}
	
	class CustomTreeModel extends DefaultTreeModel
	{
		private Settings settings;

		public CustomTreeModel() {
			super(new DefaultMutableTreeNode("Default Project"));
			this.setup();
		}
		
		public void createNewFile() throws Exception {
			String m = JOptionPane.showInputDialog("Input new file name");
			if(m != null) {
				Path path = Paths.get(DEFAULT_PROJECT, m + Drawer.EXT);
				try (PrintWriter out = new PrintWriter(path.toString())) {
				    out.println("{}");
				}
				
				this.setup();
				this.fireTreeNodesChanged(this, null, new int[0], new Object[0]);
			}
		}

		private void setup() {
			DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) getRoot();
			rootNode.removeAllChildren();
			rootNode.setAllowsChildren(true);
			
			File dir = new File(DEFAULT_PROJECT);
			if(!dir.isDirectory()) {
				dir.mkdirs();
			}
			
			File[] files = dir.listFiles();
			for (File file : files) {
				if(file.isFile() && file.getName().endsWith(Drawer.EXT)) {
					final DefaultMutableTreeNode child = new DefaultMutableTreeNode(file.getName());
					child.setAllowsChildren(false);
					rootNode.add(child);
				}
			}
			
		}
		
	}

	public void createNewFile() throws Exception {
		String m = JOptionPane.showInputDialog("Input new file name");
		if(m != null) {
			Path path = Paths.get(DEFAULT_PROJECT, m + Drawer.EXT);
			try (PrintWriter out = new PrintWriter(path.toString())) {
			    out.println("{}");
			}
			CustomTreeModel model = new CustomTreeModel();
			tree.setModel(model);
		}
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		String selectedFile = e.getPath().getLastPathComponent().toString();
		if(this.ipch != null) {
			this.ipch.selectedFileChanged(Paths.get(DEFAULT_PROJECT, selectedFile).toString());
		}
	}
}
