
package dlr.ses.peseditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicInternalFrameUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.mxgraph.util.mxUndoManager;

import dlr.ses.core.About;
import dlr.ses.core.Console;
import dlr.ses.core.Constraint;
import dlr.ses.core.DynamicTree;
import dlr.ses.core.FileConvertion;
import dlr.ses.core.FindByName;
import dlr.ses.core.GraphWindow;
import dlr.ses.core.EditorUndoableEditListener;
import dlr.ses.core.ProjectTree;
import dlr.ses.core.Variable;
import dlr.ses.core.XMLViewer;
import dlr.xml.schema.TypeInfoWriter;
import dlr.xml.xslt.XsltTrasfromation;

/**
 * <h1>PESEditor</h1>
 * <p>
 * This is the main class of the PESEditor project and it is handling everything of
 * the Editor. Graphical user interface of the editor is designed in this class.
 * All the menu actions and toolbar actions are implemented in this class.
 * </p>
 * 
 * @author Bikash Chandra Karmokar
 * @version 1.0
 *
 */
public class PESEditor extends JPanel {

	private static final long serialVersionUID = 1L;

	public static DynamicTree treePanel;
	public static String nodeName = "NewNode ";
	public static int openClicked = 0;
	public static String openFileName = "Scenario";
	public static String nodeAddDetector = "";

	public static Variable scenarioVariable;
	public static Constraint scenarioConstraint;
	public static JTabbedPane tabbedPane = new JTabbedPane();

	public static dlr.ses.peseditor.JtreeToGraph jtreeTograph;
	public static String projName = "Main";
	public static JFrame framew = new JFrame();

	public static Path path = Paths.get("").toAbsolutePath();
	public static String repFslas = path.toString().replace("\\", "/");
	public static String fileLocation = repFslas;
	public static String importFileLocation = "";
	public static String importFileName = "";
	public static int importDone = 0;

	public static UndoManager undoJtree = new UndoManager();
	public static boolean undoControlForSubTree = false;

	public static XMLViewer xmlview;
	public static XMLViewer ontologyview;
	public static XMLViewer sesview;
	public static XMLViewer schemaview;
	
	public static ProjectTree projectPanel;

	public static int controllingMainWIndow = 0;
	public static int sesValidationControl = 0;
	public static int errorPresentInSES = 0;

	public static int dividerLocation = 0;

	/**
	 * Initialize objects of DynamicTree and ProjectTree classes.
	 */
	public PESEditor() {
		super(new BorderLayout());
		DynamicTree.projectFileName = JtreeToGraph.newFileName;

		treePanel = new DynamicTree();
		treePanel.addUndoableEditListener(new EditorUndoableEditListener());

		// for project window tree
		projectPanel = new ProjectTree();
		projectPanel.setBackground(Color.white);
		projectPanel.setPreferredSize(new Dimension(300, 150));
	}

	public static void popUpActionAdd() {
		PESEditor.nodeName = JOptionPane.showInputDialog(framew, "Node Name:", "New Node",
				JOptionPane.INFORMATION_MESSAGE);
		if (PESEditor.nodeName != null) {
			PESEditor.nodeName = PESEditor.nodeName.replaceAll("\\s+", "");
		}

		if ((PESEditor.nodeName != null) && (!PESEditor.nodeName.trim().isEmpty())) {
			TreePath currentSelection = treePanel.tree.getSelectionPath();
			System.out.println(currentSelection);

			if (currentSelection != null) {
				DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) (currentSelection.getLastPathComponent());

				TreeNode[] nodes = currentNode.getPath();
				String[] nodesToSelectedNode = new String[nodes.length];

				for (int i = 0; i < nodes.length; i++) {
					nodesToSelectedNode[i] = (nodes[i].toString());
				}

				if (currentNode.toString().endsWith("Spec")) {
					treePanel.addObject(nodeName);

					jtreeTograph.addNodeWithJtreeAddition(nodeName, nodesToSelectedNode);

				} else if (currentNode.toString().endsWith("Dec")) {
					treePanel.addObject(nodeName);

					jtreeTograph.addNodeWithJtreeAddition(nodeName, nodesToSelectedNode);

				} else if (currentNode.toString().endsWith("MAsp")) {
					treePanel.addObject(nodeName);

					jtreeTograph.addNodeWithJtreeAddition(nodeName, nodesToSelectedNode);

				} else {
					treePanel.addObject(nodeName);

					jtreeTograph.addNodeWithJtreeAddition(nodeName, nodesToSelectedNode);

				}
			}

		}

	}

	public static void addNodeWIthGraphAddition(String childNode, String[] nodePath) {
		treePanel.addObjectWIthGraphAddition(childNode, nodePath);
	}

	public static void popUpActionAddVariable() { // have to change this function so that it will like add variable of
													// graph cell popup.
		String variableName = null;
		String variableType = null;
		String variableValue = null;
		String variableLowerBound = null;
		String variableUpperBound = null;

		// multiple input for variable---------------------------------
		JTextField variableField = new JTextField();
		JTextField variableTypeField = new JTextField();
		JTextField valueField = new JTextField();
		JTextField lowerBoundField = new JTextField();
		JTextField upperBoundField = new JTextField();

		Object[] message = { "Variable Name:", variableField, "Variable Type:", variableTypeField, "Value:", valueField,
				"Lower Bound:", lowerBoundField, "Upper Bound:", upperBoundField };

		int option = JOptionPane.showConfirmDialog(PESEditor.framew, message, "Please Enter",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (option == JOptionPane.OK_OPTION) {
			variableName = variableField.getText();
			variableType = variableTypeField.getText();
			variableValue = valueField.getText();
			variableLowerBound = lowerBoundField.getText();
			variableUpperBound = upperBoundField.getText();

			// added inside IF block so that if variable window closed without adding then
			// nothing will happen.
			variableName = variableName + "," + variableType + "," + variableValue + "," + variableLowerBound + ","
					+ variableUpperBound;

			TreePath currentSelection = treePanel.tree.getSelectionPath();

			boolean validInput = (variableField.getText() != null) && (!variableField.getText().trim().isEmpty())
					&& (variableTypeField.getText() != null) && (!variableTypeField.getText().trim().isEmpty())
					&& (valueField.getText() != null) && (!valueField.getText().trim().isEmpty())
					&& (lowerBoundField.getText() != null) && (!lowerBoundField.getText().trim().isEmpty())
					&& (upperBoundField.getText() != null) && (!upperBoundField.getText().trim().isEmpty());

			if (!validInput)
				JOptionPane.showMessageDialog(PESEditor.framew, "Please input all values correctly.");

			// end of multiple input for variable----------------------------
			if (validInput) {
				// have to handle added variable in a list

				if (currentSelection != null) {
					DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) (currentSelection
							.getLastPathComponent());
					// System.out.println(currentNode);
					// System.out.println(variableName);
					DynamicTree.varMap.put(currentSelection, variableName);
					// have to call a function to refresh the table view
					PESEditor.treePanel.refreshVariableTable(currentSelection);

				}
			}
		}

	}

	public static void popUpActionDeleteVariable() {
		String variableName = JOptionPane.showInputDialog(framew, "Variable Name:", "New Variable",
				JOptionPane.INFORMATION_MESSAGE);
		TreePath currentSelection = treePanel.tree.getSelectionPath();

		DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) (currentSelection.getLastPathComponent());
		TreeNode[] nodes = currentNode.getPath();

		// for handling cancel button and whitespace only words
		if ((variableName != null) && (!variableName.trim().isEmpty())) {

			int yv = 0;
			TreePath keyDel = null;
			for (TreePath key : DynamicTree.varMap.keySet()) {
				int a = 0;
				DefaultMutableTreeNode currentNode2 = (DefaultMutableTreeNode) (key.getLastPathComponent());
				// System.out.println(currentNode2.toString());
				TreeNode[] nodes2 = currentNode2.getPath();

				if (nodes.length == nodes2.length) {
					for (int i = 0; i < nodes.length; i++) {

						if (nodes[i].toString().equals(nodes2[i].toString())) {
							a = 1;

						} else
							a = 0;

					}
				}
				if (a == 1) {
					for (String value : DynamicTree.varMap.get(key)) {
						if (value.equals(variableName)) {
							yv = 1;
							keyDel = key; // to avoid java.util.ConcurrentModificationException
						}
					}
				}

			}
			if (yv == 1) {
				// DynamicTree.varMap.asMap().remove(keyDel);
				DynamicTree.varMap.remove(keyDel, variableName);// for removing only one values
				// http://tomjefferys.blogspot.de/2011/09/multimaps-google-guava.html
				// http://www.techiedelight.com/google-guava-multimap-class-java/
				yv = 0;
			}

			// have to call a function to refresh the table view
			PESEditor.treePanel.refreshVariableTable(currentSelection);

		}
	}

	public static void popUpActionDeleteAllVariables() {
		TreePath currentSelection = treePanel.tree.getSelectionPath();

		DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) (currentSelection.getLastPathComponent());
		TreeNode[] nodes = currentNode.getPath();

		List<TreePath> delKeys = new ArrayList<TreePath>();
		int yv = 0;

		for (TreePath key : DynamicTree.varMap.keySet()) {
			int a = 0;
			DefaultMutableTreeNode currentNode2 = (DefaultMutableTreeNode) (key.getLastPathComponent());
			// System.out.println(currentNode2.toString());
			TreeNode[] nodes2 = currentNode2.getPath();

			if (nodes.length == nodes2.length) {
				for (int i = 0; i < nodes.length; i++) {

					if (nodes[i].toString().equals(nodes2[i].toString())) {
						a = 1;
					} else
						a = 0;
				}
			}
			if (a == 1) {
				yv = 1;
				delKeys.add(key);
			}

		}
		if (yv == 1) {
			for (TreePath k : delKeys) {
				DynamicTree.varMap.asMap().remove(k);
				yv = 0;
			}
		}

		// for refreshing the table view
		PESEditor.treePanel.refreshVariableTable(currentSelection);

	}

	public static void popUpActionDelete() {
		treePanel.removeCurrentNode();
	}

	public static void popUpActionDeleteProjectTree() {
		projectPanel.removeCurrentNode();
	}

	public static void popUpActionDeleteAll() {
		treePanel.clear();
	}

	public static void addMenuBar(JFrame frame) {

		// final File ssdFile = new File("Scenario.ssd");
		JMenuBar menuBar;
		JMenu menuFile, menuEdit, menuHelp;
		JMenuItem menuItemNew, menuItemSave, menuItemSaveAs, menuItemOpen, menuItemImport, menuItemExport, menuItemExit,
				menuItemAbout, menuItemGenerateXML, menuItemSaveXMLFile, menuItemOpenXMLFile, validateXMLFile,
				menuItemUndo, menuItemRedo, helpItemTutorial;

		// Create the menu bar.
		menuBar = new JMenuBar();

		// Creating the menus.
		menuFile = new JMenu("File");
		menuFile.setMnemonic(KeyEvent.VK_F);

		// menuEdit = new JMenu("Edit");
		// menuEdit.setMnemonic(KeyEvent.VK_E);


		menuItemOpen = new JMenuItem("Open", KeyEvent.VK_O);
		KeyStroke ctrlSKeyStrokeOpen = KeyStroke.getKeyStroke("control O");
		menuItemOpen.setAccelerator(ctrlSKeyStrokeOpen);
		menuItemOpen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				// filechooser
				String fileName = "";
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fileChooser.setAcceptAllFileFilterUsed(false);
				fileChooser.setCurrentDirectory(new File(repFslas));// this is ok because normally all the file will be in
																	// default location. so don't need to add
																	// fileLocation

				int result = fileChooser.showOpenDialog(PESEditor.framew);

				if (result == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fileChooser.getSelectedFile();
					fileName = selectedFile.getName();
					System.out.println("Selected file: " + selectedFile.getName());

					String oldProjectTreeProjectName = projName;

					projName = fileName;

					fileLocation = selectedFile.getParentFile().getAbsolutePath();

					jtreeTograph.openExistingProject(fileName, oldProjectTreeProjectName);
					
					jtreeTograph.checkMoreThanOneAspectNodeAsChildOfEntityForColoring();

					jtreeTograph.undoManager = new mxUndoManager();				
					
										
					sesview.textArea.setText("");
					Console.consoleText.setText(">>");
					Variable.setNullToAllRows();
					Constraint.setNullToAllRows();
					
				}

			}

		});
		menuFile.add(menuItemOpen);

		menuItemSave = new JMenuItem("Save", KeyEvent.VK_S);
		KeyStroke ctrlSKeyStroke = KeyStroke.getKeyStroke("control S");
		menuItemSave.setAccelerator(ctrlSKeyStroke);
		menuItemSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				// this code is also present in convert to xml button click action.

				treePanel.saveTreeModel();
				jtreeTograph.saveGraph();

				convertTreeToXML();// this function is using for converting project tree into xml file
				jtreeTograph.graphToXML();
				jtreeTograph.graphToXMLWithUniformity();
				JOptionPane.showMessageDialog(frame, "Saved Successfully.", "Save",
						JOptionPane.INFORMATION_MESSAGE);

			}

		});
		menuFile.add(menuItemSave);

		// before saving have to create a file with the name then have to select the
		// folder
		// have to make it more user friendly.
		menuItemSaveAs = new JMenuItem("Save As...", KeyEvent.VK_A);
		menuItemSaveAs.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				JFileChooser fileChooser = new JFileChooser();
				// fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				// fileChooser.setAcceptAllFileFilterUsed(false);

				fileChooser.setCurrentDirectory(new File(PESEditor.fileLocation));
				int result = fileChooser.showSaveDialog(PESEditor.framew);
				if (result == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fileChooser.getSelectedFile();

					fileLocation = selectedFile.getParentFile().getAbsolutePath();

					String newProjectName = selectedFile.getName();
					String oldProjectTreeProjectName = PESEditor.projName;
					// System.out.println("oldProjectTreeProjectName: "+oldProjectTreeProjectName);

					projName = newProjectName;
					JtreeToGraph.newFileName = newProjectName;
					JtreeToGraph.projectFileNameGraph = newProjectName;

					jtreeTograph.ssdFileGraph = new File(
							fileLocation + "/" + projName + "/" + newProjectName + "Graph.xml");
					treePanel.ssdFile = new File(fileLocation + "/" + projName + "/" + newProjectName + ".xml");
					treePanel.ssdFileVar = new File(fileLocation + "/" + projName + "/" + newProjectName + ".ssdvar");
					treePanel.ssdFileCon = new File(fileLocation + "/" + projName + "/" + newProjectName + ".ssdcon");
					treePanel.ssdFileFlag = new File(fileLocation + "/" + projName + "/" + newProjectName + ".ssdflag");

					ProjectTree.projectName = newProjectName;
					projectPanel.changeCurrentProjectFileName(newProjectName, oldProjectTreeProjectName);

					PESEditor.newProjectFolderCreation();

					treePanel.saveTreeModel();
					jtreeTograph.saveGraph();
					// System.out.println("Graph Saved");

					// also it will convert after saving from here
					// this code is also present in convert to xml button click action.
					convertTreeToXML();// this function is using for converting project tree into xml file
					jtreeTograph.graphToXML();
					jtreeTograph.graphToXMLWithUniformity();
					JOptionPane.showMessageDialog(frame, "Saved Successfully.", "Save",
							JOptionPane.INFORMATION_MESSAGE);

				}
			}

		});
		menuFile.add(menuItemSaveAs);

		// menuItemImport = new JMenuItem("Import", KeyEvent.VK_I);
		// menuItemImport.addActionListener(new ActionListener() {
		// @Override
		// public void actionPerformed(ActionEvent e) {
		//
		// //ImportProject impProj = new ImportProject();
		// //impProj.importProject();
		//
		// }
		// });
		// menuFile.add(menuItemImport);

//		menuItemExport = new JMenuItem("Export", KeyEvent.VK_E);
//		menuItemExport.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				// System.out.println("ProjName"+DynamicTreeDemo.projName);
//				String fileName = projName;// don't know why not fetching the file name here
//
//				JFileChooser fileChooser = new JFileChooser();
//				FileNameExtensionFilter xmlfilter = new FileNameExtensionFilter("xml files (*.xml)", "xml");
//				fileChooser.setFileFilter(xmlfilter);
//				fileChooser.setSelectedFile(new File(fileName));// not working because filename is null
//				fileChooser.setCurrentDirectory(new File(PESEditor.fileLocation + "/" + PESEditor.projName));
//				int result = fileChooser.showSaveDialog(PESEditor.framew);
//				if (result == JFileChooser.APPROVE_OPTION) {
//					File selectedFile = fileChooser.getSelectedFile();
//
//					System.out.println("Exported file path: " + selectedFile.getAbsolutePath());
//
//					PrintWriter f0 = null;
//					try {
//						f0 = new PrintWriter(
//								new FileWriter(selectedFile.getAbsolutePath() + "/" + selectedFile.getName() + ".xml"));
//						// System.out.println("output file generated");
//					} catch (IOException e1) {
//						// TODO Auto-generated catch block
//						e1.printStackTrace();
//					}
//
//					Scanner in = null;
//					try {
//						in = new Scanner(
//								new File(PESEditor.fileLocation + "/" + PESEditor.projName + "/xmlforxsd.xml"));
//						// System.out.println("my read complete");
//					} catch (FileNotFoundException e2) {
//						// TODO Auto-generated catch block
//						e2.printStackTrace();
//					}
//
//					while (in.hasNext()) { // Iterates each line in the file
//
//						String line = in.nextLine();
//
//						f0.println(line);
//					}
//
//					in.close();
//					f0.close();
//
//				}
//
//			}
//		});
		menuItemExport = new JMenuItem("Export", KeyEvent.VK_E);
		KeyStroke ctrlSKeyStrokeExport = KeyStroke.getKeyStroke("control E");
		menuItemExport.setAccelerator(ctrlSKeyStrokeExport);
		menuItemExport.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// System.out.println("ProjName"+DynamicTreeDemo.projName);
				String fileName = projName;// don't know why not fetching the file name here

				JFileChooser fileChooser = new JFileChooser();
				FileNameExtensionFilter xmlfilter = new FileNameExtensionFilter("xsl files (*.xsl)", "xsl");
				fileChooser.setFileFilter(xmlfilter);
				fileChooser.setSelectedFile(new File(fileName));// not working because filename is null
				fileChooser.setCurrentDirectory(new File(PESEditor.fileLocation + "/" + PESEditor.projName));
				int result = fileChooser.showSaveDialog(PESEditor.framew);
				if (result == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fileChooser.getSelectedFile();

					System.out.println("Exported file path: " + selectedFile.getAbsolutePath());

					// for xslt...........................
					try {
						XsltTrasfromation.executeXSLT(selectedFile.getAbsolutePath());
						XsltTrasfromation.convertXMLtoXHTML();
						// System.out.println("xslt worked");
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						System.out.println(e1);
					}


				}

			}
		});
		menuFile.add(menuItemExport);
		
		menuItemExit = new JMenuItem("Exit", KeyEvent.VK_X);
		KeyStroke ctrlSKeyStrokeExit = KeyStroke.getKeyStroke("control X");
		menuItemExit.setAccelerator(ctrlSKeyStrokeExit);
		menuItemExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				System.exit(1);
				//jtreeTograph.checkMoreThanOneAspectNodeAsChildOfEntity();

			}
		});
		menuFile.add(menuItemExit);
		

		menuHelp = new JMenu("Help");
		menuHelp.setMnemonic(KeyEvent.VK_H);

		helpItemTutorial = new JMenuItem("Manual", KeyEvent.VK_M);
		KeyStroke ctrlSKeyStrokeManual = KeyStroke.getKeyStroke("control M");
		helpItemTutorial.setAccelerator(ctrlSKeyStrokeManual);
		helpItemTutorial.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
//				try {
//					Desktop desktop = Desktop.getDesktop();
//					File manual = new File(fileLocation+"/src/dlr/resources/docs/manual.pdf");					
//					desktop.open(manual);
//				} catch (IOException e1) {
//					e1.printStackTrace();
//				}
				
				if (Desktop.isDesktopSupported())    
		        {    
		            InputStream jarPdf = getClass().getClassLoader().getResourceAsStream("dlr/resources/docs/manual.pdf");
		 
		            try { 
		                File pdfTemp = new File("manual.pdf");
		                // Extraction du PDF qui se situe dans l'archive 
		                FileOutputStream fos = new FileOutputStream(pdfTemp);
		                while (jarPdf.available() > 0) {
		                      fos.write(jarPdf.read());
		                }   // while (pdfInJar.available() > 0) 
		                fos.close();
		                // Ouverture du PDF 
		                Desktop.getDesktop().open(pdfTemp);
		            }   // try 
		 
		            catch (IOException e1) {
		                System.out.println("erreur : " + e1);
		            }   // catch (IOException e) 
		        } 

			}
		});
		menuHelp.add(helpItemTutorial);

		menuItemAbout = new JMenuItem("About", KeyEvent.VK_B);
		KeyStroke ctrlSKeyStrokeAbout = KeyStroke.getKeyStroke("control B");
		menuItemAbout.setAccelerator(ctrlSKeyStrokeAbout);
		menuItemAbout.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				About about = new About("PESEditor");
			}
		});

		menuHelp.add(menuItemAbout);

		menuBar.add(menuFile);
		menuBar.add(menuHelp);
		frame.setJMenuBar(menuBar);

	}

	public static void importProjectStart() {
		Scanner in = null;
		try {
			in = new Scanner(new File(importFileLocation + "/" + importFileName)); // outputgraphxmlforxsd
			// DynamicTreeDemo.fileLocation + "/" + DynamicTreeDemo.projName)); //
			// outputgraphxmlforxsd

		} catch (FileNotFoundException e1) {

			e1.printStackTrace();
		}

		PrintWriter f0 = null;
		try {
			f0 = new PrintWriter(new FileWriter(
					PESEditor.fileLocation + "/" + PESEditor.projName + "/" + PESEditor.projName + ".xml"));
			// new FileWriter(selectedFile.getParentFile().getAbsolutePath() +
			// "/TestMain.xml"));
			f0.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");

		} catch (IOException e1) {

			e1.printStackTrace();
		}

		Stack stackEntity = new Stack();
		Stack stackAspect = new Stack();
		Stack stackMultiAspect = new Stack();
		Stack stackSpecialization = new Stack();

		while (in.hasNext()) {
			String line = in.nextLine();
			String[] partsOfLine = line.split(" ");
			int len = partsOfLine.length;

			Pattern p = Pattern.compile("\"([^\"]*)\"");
			Matcher m = p.matcher(partsOfLine[len - 1]);
			String element = "";
			while (m.find()) {
				element = m.group(1);
				// System.out.println(element);

			}

			if (line.startsWith("<entity")) {
				f0.println("<" + element + ">");
				stackEntity.push("</" + element + ">");

			} else if (line.startsWith("<aspect")) {
				f0.println("<" + element + ">");
				stackAspect.push("</" + element + ">");

			} else if (line.startsWith("<multiAspect")) {
				f0.println("<" + element + ">");
				stackMultiAspect.push("</" + element + ">");

			} else if (line.startsWith("<specialization")) {
				f0.println("<" + element + ">");
				stackSpecialization.push("</" + element + ">");

			} else if (line.startsWith("</entity")) {
				String pop = (String) stackEntity.pop();
				f0.println(pop);

			} else if (line.startsWith("</aspect")) {
				String pop = (String) stackAspect.pop();
				f0.println(pop);

			} else if (line.startsWith("</multiAspect")) {
				String pop = (String) stackMultiAspect.pop();
				f0.println(pop);

			} else if (line.startsWith("</specialization")) {
				String pop = (String) stackSpecialization.pop();
				f0.println(pop);

			}

		}

		in.close();
		f0.close();

		// below function is working. Have to make a xml file like projectName.xml for
		// example Main.xml
		// treePanel.importExistingProject(fileName,selectedFile.getParentFile().getAbsolutePath());
		jtreeTograph.importExistingProjectIntoGraph(importFileLocation);
	}

	/**
	 * This function saves all the required files based on current addition.
	 */
	public static void saveChanges() {
		treePanel.saveTreeModel();
		jtreeTograph.saveGraph();
		convertTreeToXML();
		jtreeTograph.graphToXML();
		jtreeTograph.graphToXMLWithUniformity();
	}

	public static void newProjectFolderCreation() {
		// creating directory
		// -------------------------------------------------------------
		// Create a java.io.File object, specify the name of the folder
		File f = new File(fileLocation + "/" + projName);
		// Create directory with specified name, true is returned if created.
		boolean flag = f.mkdirs();// for hierarchy of folder structure use mkdirs, for single folder mkdir()
		// Print whether true/false
		System.out.println("Project folder created?-" + flag);
		// -------------------------------------------------------------
	}

	// For saving and retrieving graph and tree now i am not using this, but now i
	// will use this for.
	// For Retrieving project tree this below function is using
	public static void convertTreeToXML() {
		TreeNode thisTreeNode = (TreeNode) projectPanel.projectTree.getModel().getRoot();
		// System.out.println(thisTreeNode);

		Document calendarDOMDoc = null;
		try {
			DOMImplementation domImpl = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.getDOMImplementation();

			calendarDOMDoc = domImpl.createDocument(null, "start", null);

		} catch (ParserConfigurationException e1) {
			e1.printStackTrace(System.err);
		} catch (DOMException e2) {
			e2.printStackTrace(System.err);
		}

		calendarDOMDoc.getDocumentElement().appendChild(saveAllTreeNodes(calendarDOMDoc, thisTreeNode));
		try {
			saveToXMLFile(calendarDOMDoc, fileLocation + "/" + projName + "/projectTree.xml");

		} catch (TransformerException ex) {
			Logger.getLogger(PESEditor.class.getName()).log(Level.SEVERE, null, ex);
		}

		modifyXmlOutput();
	}

	public static Element saveAllTreeNodes(Document thisDoc, TreeNode thisTreeNode) {
		Element thisElement = null;

		String nodeName = ((DefaultMutableTreeNode) thisTreeNode).getUserObject().toString();

		thisElement = thisDoc.createElement(nodeName);

		if (thisTreeNode.getChildCount() >= 0) {
			for (Enumeration e = thisTreeNode.children(); e.hasMoreElements();) {
				TreeNode n = (TreeNode) e.nextElement();
				// System.out.println(n.toString());
				// visitAllNodes(thisElement, n);
				thisElement.appendChild(saveAllTreeNodes(thisDoc, n));
				// Node node = visitAllNodes(thisElement, n);
				// System.out.println(thisElement.getNodeName());
				// System.out.println(thisElement.getChildNodes().getLength());
			}
			// System.out.println(thisElement.getNodeName());
			// System.out.println(thisElement.getChildNodes().getLength());

		}

		return thisElement;

	}

	// method for saving Xml DOM documents: Convert to XML
	public static boolean saveToXMLFile(Document doc, String filePath) throws TransformerException {
		if (doc != null) {
			try {
				javax.xml.transform.TransformerFactory tFactory = javax.xml.transform.TransformerFactory.newInstance();
				// javax.xml.transform.stream.StreamSource();
				javax.xml.transform.Transformer transformer = tFactory.newTransformer();
				javax.xml.transform.dom.DOMSource source = new javax.xml.transform.dom.DOMSource(doc);
				javax.xml.transform.stream.StreamResult result = new javax.xml.transform.stream.StreamResult(
						new File(filePath));
				// transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
				// transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "0");// important for xml
																								// style maintaining

				transformer.transform(source, result);
				return true;

			} catch (TransformerFactoryConfigurationError ex) {
				return false;
			}

		} else {
			return false;
		}
	}

	// for modifying the generated xml output from the project tree. it is not using
	// for graph tree now
	// graph tree are generating from mxGraph files
	public static void modifyXmlOutput() {
		// System.out.println("Modify called");

		PrintWriter f0 = null;
		try {
			f0 = new PrintWriter(
					new FileWriter(fileLocation + "/" + projName + "/" + JtreeToGraph.newFileName + "Project.xml"));
			// PrintWriter f00 = new PrintWriter(new
			// FileWriter("eclipse/runtimefiles/output.xml"));
			// System.out.println("output file generated");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Scanner in = null;
		try {
			in = new Scanner(new File(fileLocation + "/" + projName + "/projectTree.xml"));
			// System.out.println("my read complete");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		while (in.hasNext()) { // Iterates each line in the file
			// System.out.println("inside while");
			String line = in.nextLine();
			// System.out.println("Line: " + line);

			if (line.endsWith("start>"))
				continue;
			else if (line.endsWith("/>")) {
				// System.out.println(line);
				String result = line.replaceAll("[</>]", "");
				// String result = line.replaceAll("[\\<\\/\\>]","");
				result = result.replaceAll("\\s+", "");
				String line1 = "<" + result + ">";
				String line2 = "</" + result + ">";
				f0.println(line1);
				f0.println(line2);

			} else
				f0.println(line);
		}

		in.close();
		f0.close();
	}

	public static void showGeneratedFile(String file) {
		Scanner in = null;
		try {
			in = new Scanner(new File(fileLocation + "/" + projName + "/" + file + ".xml"));

		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}

		StringBuilder xmlcontent = new StringBuilder();

		while (in.hasNext()) {

			String line = in.nextLine();

			xmlcontent.append(line);
			xmlcontent.append("\n");

		}

		xmlview.textArea.setText(xmlcontent.toString());
		// System.out.println(xmlcontent);

		in.close();
	}

	public static void showSESOntologytoOntologViewer() {

		Scanner in = null;
		try {
			in = new Scanner(new File(fileLocation + "/" + projName + "/ses.xsd"));
			// in = new Scanner(new File("src/xsd/ses.xsd"));

		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}

		StringBuilder xmlcontent = new StringBuilder();

		while (in.hasNext()) {

			String line = in.nextLine();

			xmlcontent.append(line);
			xmlcontent.append("\n");

		}

		ontologyview.textArea.setText(xmlcontent.toString());
		// System.out.println(xmlcontent);

		in.close();

	}

	// showing xml to viewer
	public static void showXMLtoXMLViewer() {

		Scanner in = null;
		try {
			in = new Scanner(new File(fileLocation + "/" + projName + "/xmlforxsd.xml"));
			// in = new Scanner(new File(fileLocation + "/" + projName + "/testcon.xml"));

		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}

		StringBuilder xmlcontent = new StringBuilder();

		while (in.hasNext()) {

			String line = in.nextLine();

			xmlcontent.append(line);
			xmlcontent.append("\n");

		}

		sesview.textArea.setText(xmlcontent.toString());
		// System.out.println(xmlcontent);

		in.close();

	}

	// showing xml to viewer
	public static void showXSDtoXMLViewer() {

		Scanner in = null;
		try {
			in = new Scanner(new File(fileLocation + "/" + projName + "/xsdfromxml.xsd"));

		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}

		StringBuilder xmlcontent = new StringBuilder();

		while (in.hasNext()) {

			String line = in.nextLine();

			xmlcontent.append(line);
			xmlcontent.append("\n");

		}

		schemaview.textArea.setText(xmlcontent.toString());
		// System.out.println(xmlcontent);

		in.close();

	}

	/**
	 * Receives a array of string and return the TreePath of the specific node.
	 */
	public static TreePath getTreeNodePath(String[] nodePath) {
		TreePath parentPath;
		FindByName obj = new FindByName(treePanel.tree, nodePath);
		parentPath = FindByName.path;
		return parentPath;
	}

	/**
	 * Used to add all the buttons in the tool bar below the MenuBar.
	 */

	public static void addToolBar(JFrame frame) {

		// System.out.println("image path test: "+repFslas);

		JToolBar toolbar = new JToolBar();

		/*
		 * ImageIcon deselectIcon = new
		 * ImageIcon(PESEditor.class.getResource("/dlr/resources/images/cursor.png"));
		 * JButton deselect = new JButton(deselectIcon);
		 * deselect.setToolTipText("Free Mouse Pointer from Any Selected Element");
		 * 
		 * toolbar.add(deselect); deselect.addActionListener(new ActionListener() {
		 * 
		 * @Override public void actionPerformed(ActionEvent e) {
		 * 
		 * nodeAddDetector = "";
		 * 
		 * } });
		 * 
		 * ImageIcon entityIcon = new
		 * ImageIcon(PESEditor.class.getResource("/dlr/resources/images/en.png"));
		 * JButton entity = new JButton(entityIcon);
		 * entity.setToolTipText("Add Entity"); toolbar.add(entity);
		 * entity.addActionListener(new ActionListener() {
		 * 
		 * @Override public void actionPerformed(ActionEvent e) {
		 * 
		 * nodeAddDetector = "entity";
		 * 
		 * }
		 * 
		 * });
		 * 
		 * ImageIcon aspectIcon = new
		 * ImageIcon(PESEditor.class.getResource("/dlr/resources/images/as16.png"));
		 * JButton aspect = new JButton(aspectIcon);
		 * aspect.setToolTipText("Add Aspect"); toolbar.add(aspect);
		 * aspect.addActionListener(new ActionListener() {
		 * 
		 * @Override public void actionPerformed(ActionEvent e) {
		 * 
		 * nodeAddDetector = "aspect";
		 * 
		 * } });
		 * 
		 * ImageIcon specializationIcon = new
		 * ImageIcon(PESEditor.class.getResource("/dlr/resources/images/sp.png"));
		 * JButton specialization = new JButton(specializationIcon);
		 * specialization.setToolTipText("Add Specialization");
		 * toolbar.add(specialization); specialization.addActionListener(new
		 * ActionListener() {
		 * 
		 * @Override public void actionPerformed(ActionEvent e) {
		 * 
		 * nodeAddDetector = "specialization";
		 * 
		 * } });
		 * 
		 * ImageIcon multiaspectIcon = new
		 * ImageIcon(PESEditor.class.getResource("/dlr/resources/images/ma.png"));
		 * JButton multiaspect = new JButton(multiaspectIcon);
		 * multiaspect.setToolTipText("Add Multi-Aspect"); toolbar.add(multiaspect);
		 * multiaspect.addActionListener(new ActionListener() {
		 * 
		 * @Override public void actionPerformed(ActionEvent e) {
		 * 
		 * nodeAddDetector = "multiaspect";
		 * 
		 * } });
		 * 
		 * ImageIcon deleteIcon = new
		 * ImageIcon(PESEditor.class.getResource("/dlr/resources/images/delete.png"));
		 * JButton delete = new JButton(deleteIcon);
		 * delete.setToolTipText("Delete Node From Graph"); toolbar.add(delete);
		 * delete.addActionListener(new ActionListener() {
		 * 
		 * @Override public void actionPerformed(ActionEvent e) {
		 * 
		 * nodeAddDetector = "delete";
		 * 
		 * } });
		 * 
		 */

		ImageIcon savegraphicon = new ImageIcon(PESEditor.class.getResource("/dlr/resources/images/save.png"));
		JButton savegraph = new JButton(savegraphicon);
		savegraph.setToolTipText("Save Graph");
		toolbar.add(savegraph);
		savegraph.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				treePanel.saveTreeModel();
				jtreeTograph.saveGraph();

				convertTreeToXML();
				jtreeTograph.graphToXML();
				jtreeTograph.graphToXMLWithUniformity();
				JOptionPane.showMessageDialog(frame, "Saved Successfully.", "Save",
						JOptionPane.INFORMATION_MESSAGE);

			}
		});

		ImageIcon undoIcon = new ImageIcon(PESEditor.class.getResource("/dlr/resources/images/undo.png"));
		JButton undo = new JButton(undoIcon);
		undo.setToolTipText("Undo");
		toolbar.add(undo);

		ImageIcon redoIcon = new ImageIcon(PESEditor.class.getResource("/dlr/resources/images/redo.png"));
		JButton redo = new JButton(redoIcon);
		redo.setToolTipText("Redo");
		toolbar.add(redo);

		undo.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				// System.out.println("undo: if there is any node without input/output edge then
				// undo tree will not work-have to do it");

				// undo actions for jtree
				jtreeTograph.undo();

				// undo actions for graph
				try {
					if (undoJtree.canUndo()) {
						undoJtree.undo();
						treePanel.expandTree();
					}
				} catch (CannotUndoException ex) {
					System.out.println("Unable to undo: " + ex);
					ex.printStackTrace();
				}

			}

		});

		redo.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				// redo actions for jtree
				jtreeTograph.redo();

				// redo actions for graph
				try {
					if (undoJtree.canRedo()) {
						undoJtree.redo();
						treePanel.expandTree();
					}
				} catch (CannotRedoException ex) {
					System.out.println("Unable to redo: " + ex);
					ex.printStackTrace();
				}

			}
		});

		ImageIcon zoominIcon = new ImageIcon(PESEditor.class.getResource("/dlr/resources/images/zoom-in.png"));
		JButton zoomin = new JButton(zoominIcon);
		zoomin.setToolTipText("Zoom In");

		toolbar.add(zoomin);
		zoomin.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				System.out.println("zoomin ");
				jtreeTograph.zoomIn();

			}
		});

		ImageIcon zoomoutIcon = new ImageIcon(PESEditor.class.getResource("/dlr/resources/images/zoom-out.png"));
		JButton zoomout = new JButton(zoomoutIcon);
		zoomout.setToolTipText("Zoom Out");

		toolbar.add(zoomout);
		zoomout.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				System.out.println("zoomout: ");
				jtreeTograph.zoomOut();

			}
		});

		ImageIcon validateSESIcon = new ImageIcon(PESEditor.class.getResource("/dlr/resources/images/validation.png"));
		JButton validateSES = new JButton(validateSESIcon);
		validateSES.setToolTipText("Validation");
		validateSES.setFont(new Font("Serif", Font.BOLD, 14));
		toolbar.add(validateSES);
		validateSES.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FileConvertion fileConversion = new FileConvertion();
				// this is SES validation
				tabbedPane.setSelectedIndex(0);
				sesview.setTitle("PES XML");
				saveChanges();
				fileConversion.createSES();

				// have to fix this--------------------------------------
				fileConversion.modifyXmlOutputForXSD(); // changed the input file to graphxmluniformity
				jtreeTograph.rootToEndNodeSequenceSolve();
				jtreeTograph.rootToEndNodeVariable();
				// fileConversion.xmlToXSDConversion();
				// fileConversion.placeAssertInRightPosition();

				// have to fix this end not needed all-----------------------------------

				// this is important here. others above have to check

				/*
				 * if i use modifyXmlOutputFixForSameNameNode then var is added as entity and
				 * SES is not generated showing error. have to check why
				 */
				jtreeTograph.modifyXmlOutputFixForSameNameNode();

				jtreeTograph.xmlOutputForXSD();

				jtreeTograph.addconstraintToSESStructure();

				PESEditor.sesValidationControl = 1;

				TypeInfoWriter.validateXML();

				if (PESEditor.errorPresentInSES == 1) {
					// xmlview.textArea.setFont(new Font("Serif",Font.BOLD,20));
					sesview.textArea.setText("Error presents in the SES. Check console output for details.");
					// xmlview.textArea.setFont(new Font("Serif",Font.PLAIN,12));
					PESEditor.errorPresentInSES = 0;
				} else {
					showXMLtoXMLViewer();
				}

			}
		});

		FileConvertion fileConversion = new FileConvertion();

		tabbedPane.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {

				if (tabbedPane.getSelectedIndex() == 0) {
					sesview.setTitle("PES XML");
					saveChanges();
					fileConversion.createSES();

					// have to fix this--------------------------------------
					fileConversion.modifyXmlOutputForXSD(); // changed the input file to graphxmluniformity
					jtreeTograph.rootToEndNodeSequenceSolve();
					jtreeTograph.rootToEndNodeVariable();
					// fileConversion.xmlToXSDConversion();
					// fileConversion.placeAssertInRightPosition();

					// have to fix this end not needed all-----------------------------------

					// this is important here. others above have to check

					/*
					 * if i use modifyXmlOutputFixForSameNameNode then var is added as entity and
					 * SES is not generated showing error. have to check why
					 */
					jtreeTograph.modifyXmlOutputFixForSameNameNode();

					jtreeTograph.xmlOutputForXSD();

					jtreeTograph.addconstraintToSESStructure();

					PESEditor.sesValidationControl = 1;

					TypeInfoWriter.validateXML();

					if (PESEditor.errorPresentInSES == 1) {
						// xmlview.textArea.setFont(new Font("Serif",Font.BOLD,20));
						sesview.textArea.setText("Error presents in the SES. Check console output for details.");
						// xmlview.textArea.setFont(new Font("Serif",Font.PLAIN,12));
						PESEditor.errorPresentInSES = 0;
					} else {
						showXMLtoXMLViewer();
					}

					// for xslt...........................
					// try {
					// XsltTrasfromation.executeXSLT();
					// XsltTrasfromation.convertXMLtoXHTML();
					// //System.out.println("xslt worked");
					// } catch (Exception e1) {
					// // TODO Auto-generated catch block
					// e1.printStackTrace();
					// System.out.println(e1);
					// }

				} else if (tabbedPane.getSelectedIndex() == 1) {
					schemaview.setTitle("PES Schema");
					saveChanges();
					fileConversion.modifyXmlOutputForXSD();
					jtreeTograph.rootToEndNodeSequenceSolve();
					jtreeTograph.rootToEndNodeVariable(); // have to try using saving keys in a list like i did in
															// delete
															// variable

					// fileConversion.modifyXmlOutputForRefNode();

					jtreeTograph.modifyXmlOutputFixForSameNameNode();
					fileConversion.xmlToXSDConversion();
					fileConversion.placeAssertInRightPosition();

					showXSDtoXMLViewer();
				}

			}
		});

		toolbar.setBorder(new EtchedBorder());

		frame.add(toolbar, BorderLayout.NORTH);
	}

	/**
	 * This function will footer of the Editor which contains the pin option button.
	 * 
	 */

	public static void addToolBarFooter(JFrame frame) {
		JToolBar toolbarFooter = new JToolBar();

		ImageIcon pinIcon = new ImageIcon(PESEditor.class.getResource("/dlr/resources/images/black-pin.png"));
		JButton pin = new JButton(pinIcon);

		toolbarFooter.add(Box.createHorizontalGlue());
		toolbarFooter.add(pin);

		toolbarFooter.setBorder(new EtchedBorder());
		toolbarFooter.setSize(500, 500);

		frame.add(toolbarFooter, BorderLayout.SOUTH);
	}

	public static void main(String[] args) {
		// look and feel
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e) {
			// If Nimbus is not available, then have to set another look and feel.
		}

		SplashScreen splash = new SplashScreen(3000);
		splash.showSplash();

		jtreeTograph = new JtreeToGraph();

		// Create and set up the window.
		JFrame frame = new JFrame("PES Editor");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Create and set up the content pane.
		PESEditor newContentPane = new PESEditor();
		newContentPane.setOpaque(true); // content panes must be opaque
		frame.setContentPane(newContentPane);

		// treePanel = new DynamicTree();
		// treePanel.addUndoableEditListener(new MyUndoableEditListener());
		treePanel.setPreferredSize(new Dimension(200, 600));

		scenarioVariable = new Variable();
		scenarioVariable.setPreferredSize(new Dimension(100, 200));
		scenarioVariable.setBorder(new EtchedBorder());

		scenarioConstraint = new Constraint();
		scenarioConstraint.setPreferredSize(new Dimension(100, 200));
		scenarioConstraint.setBorder(new EtchedBorder());

		// Adding jgraph window in the center
		GraphWindow graphWindow = new GraphWindow();
		graphWindow.setPreferredSize(new Dimension(800, 600));
		// this is for removing the top-left icon of the internal frame
		BasicInternalFrameUI ui = (BasicInternalFrameUI) graphWindow.getUI();
		Container north = (Container) ui.getNorthPane();
		north.remove(0);
		north.validate();
		north.repaint();
		// end of removing top left icon

		graphWindow.pack();
		graphWindow.setVisible(true);
		jtreeTograph.createGraph(graphWindow);

		// Console		
		Console console = new Console();
		console.setPreferredSize(new Dimension(200, 200));
		// this is for removing the top-left icon of the internal frame
		BasicInternalFrameUI uiCon = (BasicInternalFrameUI) console.getUI();
		Container northCon = (Container) uiCon.getNorthPane();
		northCon.remove(0);
		northCon.validate();
		northCon.repaint();
		console.pack();
		console.setVisible(true);
		// end of removing top left icon

		xmlview = new XMLViewer();
		xmlview.setPreferredSize(new Dimension(200, 200));
		// this is for removing the top-left icon of the internal frame
		BasicInternalFrameUI uixml = (BasicInternalFrameUI) xmlview.getUI();
		Container northxml = (Container) uixml.getNorthPane();
		northxml.remove(0);
		northxml.validate();
		northxml.repaint();
		// end of removing top left icon
		xmlview.pack();
		xmlview.setVisible(true);
		xmlview.setTitle("XML");// SES Ontology / Schema Viewer
		xmlview.textArea.setEditable(true);

		ontologyview = new XMLViewer();
		ontologyview.setPreferredSize(new Dimension(200, 200));
		// this is for removing the top-left icon of the internal frame
		BasicInternalFrameUI uiontology = (BasicInternalFrameUI) ontologyview.getUI();
		Container northontology = (Container) uiontology.getNorthPane();
		northontology.remove(0);
		northontology.validate();
		northontology.repaint();
		// end of removing top left icon
		ontologyview.pack();
		ontologyview.setVisible(true);
		ontologyview.setTitle("SES Ontology");// SES Ontology / Schema Viewer
		ontologyview.textArea.setEditable(false);

		sesview = new XMLViewer();
		sesview.setPreferredSize(new Dimension(200, 200));
		// this is for removing the top-left icon of the internal frame
		BasicInternalFrameUI uises = (BasicInternalFrameUI) sesview.getUI();
		Container northses = (Container) uises.getNorthPane();
		northses.remove(0);
		northses.validate();
		northses.repaint();
		// end of removing top left icon
		sesview.pack();
		sesview.setVisible(true);
		sesview.setTitle("PES XML");// SES Ontology / Schema Viewer
		sesview.textArea.setEditable(false);

		schemaview = new XMLViewer();
		schemaview.setPreferredSize(new Dimension(200, 200));
		// this is for removing the top-left icon of the internal frame
		BasicInternalFrameUI uischama = (BasicInternalFrameUI) schemaview.getUI();
		Container northschama = (Container) uischama.getNorthPane();
		northschama.remove(0);
		northschama.validate();
		northschama.repaint();
		// end of removing top left icon
		schemaview.pack();
		schemaview.setVisible(true);
		schemaview.setTitle("PES Schema");// SES Ontology / Schema Viewer
		schemaview.textArea.setEditable(false);

		// creating tab window
		//tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		tabbedPane.addTab("PES XML", sesview);
		tabbedPane.addTab("PES Schema", schemaview);
		// end of tab window

		// --------------------------------------------------------------------------------------------

		JSplitPane projectPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, projectPanel, treePanel);
		projectPane.setOneTouchExpandable(true);
		projectPane.setDividerLocation(250); // define project Explorer height, so treePanel will be about 450
		// grapConsole.setResizeWeight(.1);
		projectPane.setDividerSize(6);// width of the line which split the window
		projectPane.setBorder(null);

		JSplitPane grapConsole = new JSplitPane(JSplitPane.VERTICAL_SPLIT, graphWindow, console);
		grapConsole.setOneTouchExpandable(true);
		grapConsole.setDividerLocation(750); // define graph window height, so console will be about 150
		// grapConsole.setResizeWeight(.1);
		grapConsole.setDividerSize(6);// width of the line which split the window
		grapConsole.setBorder(null);

		JSplitPane graphtree = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, projectPane, grapConsole);
		graphtree.setOneTouchExpandable(true);
		graphtree.setDividerLocation(200);// define tree panel width
		// graphtree.setResizeWeight(.1);
		graphtree.setDividerSize(6);// width of the line which split the window
		graphtree.setBorder(null);
		dividerLocation = graphtree.getDividerLocation();
		graphtree.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						dividerLocation = graphtree.getDividerLocation();
					}
				});
			}
		});

		JSplitPane variableAndCOnstraint = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scenarioVariable,
				scenarioConstraint);
		variableAndCOnstraint.setOneTouchExpandable(true);
		variableAndCOnstraint.setDividerLocation(150);
		// xml.setResizeWeight(.2);
		variableAndCOnstraint.setDividerSize(6);// width of the line which split the window
		variableAndCOnstraint.setBorder(null);

		JSplitPane xml = new JSplitPane(JSplitPane.VERTICAL_SPLIT, variableAndCOnstraint, tabbedPane);//
		xml.setOneTouchExpandable(true);
		xml.setDividerLocation(300);// 150 for variable and 150 for constraint
		// xml.setResizeWeight(.2);
		xml.setDividerSize(6);// width of the line which split the window
		xml.setBorder(null);

		JSplitPane graphVariable = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, graphtree, xml);
		graphVariable.setOneTouchExpandable(true);
		// graphVariable.setPreferredSize(new Dimension(200, 200));
		graphVariable.setDividerLocation(1400);// width of the graph window
		// graphVariable.setResizeWeight(.2);
		graphVariable.setDividerSize(6);// width of the line which split the window
		graphVariable.setBorder(null);
		// --------------------------------------------------------------------------------------------

		frame.add(graphVariable, BorderLayout.CENTER);

		addMenuBar(frame);
		addToolBar(frame);
		addToolBarFooter(frame);
		// addTreeGraph(frame);

		frame.pack();
		frame.setSize(1200, 1200);
		frame.setLocationRelativeTo(null);
		// This works in a multi-monitor setup and setLocationRelativeTo
		// must be called after pack() and setSize() if they are called at all.
		ImageIcon windowIcon = new ImageIcon(PESEditor.class.getResource("/dlr/resources/images/dlrapplication.gif"));
		frame.setIconImage(windowIcon.getImage());
		frame.setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
		frame.setVisible(true);
		framew = frame;

		Variable.setNullRowsToVariableTable();

		// Create a java.io.File object, specify the name of the folder
		File f = new File("Main"); // Create directory with specified name, true is returned if created.
		boolean flag = f.mkdirs();// for hierarchy of folder structure use mkdirs, for single folder mkdir() //
									// Print whether true/false
		System.out.println("Project folder created?-" + flag);
		// -------------------------------------------------------------

	}

}
