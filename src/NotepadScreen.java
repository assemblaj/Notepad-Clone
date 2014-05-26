import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.undo.*;
import javax.swing.undo.UndoManager;

public class NotepadScreen extends JTextArea implements ActionListener {
	/*NotepadClone*/
	private NotepadClone frame;
	
	/*objects for each of the menus */
	private FileMenu file;
	private EditMenu edit;
	private FormatMenu format;
	private ViewMenu view;
	
	private UndoManager uManager;
	
	public NotepadScreen(NotepadClone frame) {
		super(new PlainDocument());
		
		this.frame = frame;
		
		// Creating menu bar
		JMenuBar menuBar = new JMenuBar();

		// Setting it's font (has to be done before 
		// creation of FormatMenu
		setFont( new Font("Consolas", Font.PLAIN, 14));
		
		// Creating Menus 
		file = new FileMenu(this);
		edit = new EditMenu(this);
		format = new FormatMenu(this);
		view = new ViewMenu(this);
		
		getDocument().addDocumentListener(file.initSaveMonitor());
		frame.addWindowListener(file.initExitMonitor());
		addCaretListener(edit.initEditValidator());
		addCaretListener(view.initStatusMonitor());
		
		// Creating undo/redo manager and etc
		getDocument().addUndoableEditListener(edit.initUndoManager());
		
		
		menuBar.add(file);
		menuBar.add(edit);
		menuBar.add(format);
		menuBar.add(view);
		frame.setJMenuBar(menuBar);
		
		file.resetDocument();
	}

	// ActionListener must be added to each individual element in 
	// a menu.
	public void actionPerformed(ActionEvent e) {
	/*
		if ("new".equals(e.getActionCommand())) {
		   file.newFile();
		} else if ("open".equals(e.getActionCommand())) {
		   file.open();
		} else if ("save".equals(e.getActionCommand())) {
		   file.save();
		} else if ("save as".equals(e.getActionCommand())) {
		   file.saveAs();
		} else if ("exit".equals(e.getActionCommand())) {
		   file.exit();
		} else if ("undo".equals(e.getActionCommand())) {
		   edit.undo();
		} else if ("redo".equals(e.getActionCommand())) {
		   edit.redo();
		} else if ("delete".equals(e.getActionCommand())) {
		   edit.delete();
		} else if ("find".equals(e.getActionCommand())) {
		   edit.find();
		} else if ("find next".equals(e.getActionCommand())) {
		   edit.findNext();
		} else if ("replace".equals(e.getActionCommand())) {
		   edit.replace();
		} else if ("goto".equals(e.getActionCommand())) {
		   edit.goTo();
		} else if ("time date".equals(e.getActionCommand())) {
		   edit.timeDate();
		} else if ("select all".equals(e.getActionCommand())) {
		   edit.selectAll();
		} else if ("word wrap".equals(e.getActionCommand())) {
		   format.wordWrap();
		} else if ("font".equals(e.getActionCommand())) {
		   format.font();
		} else if ("status bar".equals(e.getActionCommand())) {
		   view.statusBar();
		}
	*/	
		switch (e.getActionCommand()) {
			case "new": 		file.newFile(); break;
			case "open": 		file.open(); break;
			case "save": 		file.save(); break;
			case "save as": 	file.saveAs(); break;
			case "exit": 		file.exit(); break;
			case "undo": 		edit.undo(); break;
			case "redo": 		edit.redo(); break;
			case "delete": 		edit.delete(); break;
			case "find": 		edit.find(); break;
			case "find next": 	edit.findNext(); break;
			case "replace": 	edit.replace(); break;
			case "goto": 		edit.goTo(); break;
			case "time date": 	edit.timeDate(); break;
			case "select all": 	edit.selectAll(); break;
			case "word wrap": 	format.wordWrap(); break;
			case "font": 		format.font(); break;
			case "status bar": 	view.statusBar(); break;
		}
	}	
	
	public void setTitle(String title) {
		frame.setTitle(title);
	}
	
	public NotepadClone getFrame() {
		return frame;
	}
}