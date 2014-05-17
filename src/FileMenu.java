import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.Scanner;
import javax.swing.text.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileFilter; // need to specify which FileFilter
import javax.swing.filechooser.*;


public class FileMenu extends NotepadMenu {
	private boolean changesMade = false;
	
	private NotepadScreen input;
	
	private JFileChooser fc;
	private String filePath;
	private FileFilter filter;

	private Scanner inputFile = null;
	private PrintWriter outputFile = null;
	private String content;
	private boolean hasSaved = false; // if not, we need to get the filepath
	
	private boolean firstDoc = true; // is this the first document in the session?
	
	public FileMenu(NotepadScreen input) {
		super("File", input);
		
		this.input = input;
		
		// Creating File Chooser
		fc = new JFileChooser();
		
		// Creating and setting its choosable filter
		filter = new FileNameExtensionFilter("Text Document (\".txt\")", "txt");
		fc.addChoosableFileFilter(filter);
		
		makeMenu();	
	}
	
	public void makeMenu() {
		// Creating File menu and its MenuItems
		//JMenu fileMenu = new JMenu("File");
		
		JMenuItem newItem = new JMenuItem("New", KeyEvent.VK_N);
		newItem.setActionCommand("new");
		newItem.addActionListener(input);
		newItem.setAccelerator(KeyStroke.getKeyStroke(
		   KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		add(newItem);
		
		JMenuItem openItem = new JMenuItem("Open", KeyEvent.VK_O);
		openItem.setActionCommand("open");
		openItem.addActionListener(input);
		openItem.setAccelerator(KeyStroke.getKeyStroke(
			KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		add(openItem);
		
		JMenuItem saveItem = new JMenuItem("Save", KeyEvent.VK_S);
		saveItem.setActionCommand("save");
		saveItem.addActionListener(input);
		saveItem.setAccelerator(KeyStroke.getKeyStroke(
		    KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		add(saveItem);
		
		// no accelerators or mnemonics on this one
		JMenuItem saveAsItem = new JMenuItem("Save As");
		saveAsItem.setActionCommand("save as");
		saveAsItem.addActionListener(input); 
		add(saveAsItem);
		
		addSeparator();
		
		JMenuItem exitItem = new JMenuItem("Exit", KeyEvent.VK_E);
		exitItem.setActionCommand("exit");
		exitItem.addActionListener(input);
		exitItem.setAccelerator(KeyStroke.getKeyStroke(
		    KeyEvent.VK_F5, ActionEvent.ALT_MASK));
		add(exitItem);
		
	}
	
	//CONVERTED
	// File Menu Stuff 
	public void exit() {
		if (changesMade) { // if changesMade, prompt to save
		   int choice = saveWarning();
		   if (choice == JOptionPane.YES_OPTION) {
		      save();
			  System.exit(0);
		   } else if (choice == JOptionPane.NO_OPTION) {
		      System.exit(0);
		   }
		} else {
		   System.exit(0);
		}
	}
	
	//CONVERTED
	// give a default name like document 1, as well
	public void newFile() {
		if (changesMade) { 
		   int choice = saveWarning();
		   if (choice == JOptionPane.YES_OPTION) {
		      save();
              resetDocument();
		   } else if (choice == JOptionPane.NO_OPTION) {
		      resetDocument();
		   }
		} else if (!changesMade) { 		
		   resetDocument();
		} 
	}
	
	public void resetDocument() {
	    clearDoc();
		changesMade = false; 
		hasSaved = false;
		filePath = "Untitled";
		input.setTitle(filePath + " - NotepadClone");
	}
	
	public int saveWarning() {
		try {
		  return JOptionPane.showConfirmDialog(
		          input.getFrame(), "Save file \"" + filePath + "\" ?", 
		          "Save", JOptionPane.YES_NO_CANCEL_OPTION,
			      JOptionPane.QUESTION_MESSAGE);
	    } catch (HeadlessException e) {}
		return 0; // need to do this
	}
	
	//CONVERTED
	public void save() {
	   if (!hasSaved) {
          int returnVal = fc.showSaveDialog(input.getFrame());
		
	      if (returnVal == JFileChooser.APPROVE_OPTION) {
	         File file = fc.getSelectedFile();
		     if (file.exists()) {
			    int choice = replaceWarning();
				if (choice == JOptionPane.YES_OPTION) {
				   filePath = file.getAbsolutePath();
				   saveFile();
				   hasSaved = true;
				} else if (choice == JOptionPane.NO_OPTION) {
				   save();
				}
			 } else {
	            filePath = file.getAbsolutePath();
		        saveFile();
				hasSaved = true;
			 }
	      }
	   } else {
	      saveFile();
	   }
	}
	
	//CONVERTED
	public void saveAs() {
       int returnVal = fc.showDialog(input.getFrame(), "Save As");
	   
	   if (returnVal == JFileChooser.APPROVE_OPTION) {
	      File file = fc.getSelectedFile();
		  if (file.exists()) {
		      int choice = replaceWarning();
			  if (choice == JOptionPane.YES_OPTION) {
			     filePath = file.getAbsolutePath();
				 saveFile();
			  } else if (choice == JOptionPane.NO_OPTION) {
				saveAs();
			  }
		  } else {
		    filePath = file.getAbsolutePath();
			saveFile();
		  }
	   }
	}
	
	public int replaceWarning() {
	   try {
	      return JOptionPane.showConfirmDialog(
		     input.getFrame(), "File already exists. " +
		     "Do you want to replace it?", "Replace file",
		     JOptionPane.YES_NO_CANCEL_OPTION, 
		     JOptionPane.QUESTION_MESSAGE);
	   }catch (HeadlessException e) {}
       return 0; // need to see this	
	}
	
	public void saveFile() {
		try {
		   outputFile = new PrintWriter(filePath);
		   try {
		      content = input.getText(
		           input.getDocument().getStartPosition().getOffset(),
		           input.getDocument().getLength());
		   } catch (BadLocationException e) {System.out.println("hello");}
		   outputFile.print(content);
		   outputFile.close();
		   changesMade = false; // changesMade = false 
		   hasSaved = true;
		   input.setTitle(filePath + " - NotepadClone");
		} catch (FileNotFoundException e) {
		} finally {outputFile.close();}
		
	}
	
	// The document must be cleared at several points
	// This is how
	public void clearDoc() {
       try{
	      input.getDocument().remove(
		     input.getDocument().getStartPosition().getOffset(), 
		     input.getDocument().getLength());}
	    catch (BadLocationException e) {}
	}
	
	//  This Works
	//   Need to implement scroll bar now 
	public void open() {
		int returnVal = fc.showOpenDialog(input.getFrame());
		
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			filePath = file.getAbsolutePath();
			
			try {	
			   clearDoc();
			   inputFile = new Scanner(file);
				
			   while (inputFile.hasNextLine()) {
			      input.append(inputFile.nextLine() + "\n");
			   }				
			} catch (FileNotFoundException e) {
			} finally { inputFile.close();}
			changesMade = false;
			input.setTitle(filePath + " - NotepadClone"); // set title to file name
	    } 
	}
		
	// Monitors whether any changes have been made since the last save.
	class SaveMonitor implements DocumentListener 
	{

	   @Override
	   public void insertUpdate(DocumentEvent e) {
		   changesMade = true;	   //changesMade = true; // Belongs to FileMenu
	   }
	   
	   @Override
	   public void removeUpdate(DocumentEvent e) {
		   changesMade = true;	   //changesMade = true;
	   }
	   
	   @Override
	   public void changedUpdate(DocumentEvent e) {
	   }
	
	}
	
	public SaveMonitor initSaveMonitor() {
		return new SaveMonitor();
	}
	
	// If the user hasn't saved, make sure they're asked
	// to do some before they've pressed the red x. 	
	class ExitMonitor extends WindowAdapter {
	   public void windowClosing(WindowEvent e) {
		  exit();		  
	   }	
	}

	public ExitMonitor initExitMonitor() {
		return new ExitMonitor();
	}
}