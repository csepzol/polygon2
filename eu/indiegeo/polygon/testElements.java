package eu.indiegeo.polygon;

import java.awt.Component;
import java.awt.Window;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class testElements {
	
	public void setText(JTree tree) {
		System.out.println("setText");
		Utils.clearCavesTree(tree);
	}

}
