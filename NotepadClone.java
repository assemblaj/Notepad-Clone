import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.Scanner;
import javax.swing.text.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileFilter; // need to specify which FileFilter
import javax.swing.filechooser.*;
import javax.swing.undo.*;
import javax.swing.undo.UndoManager;
import javax.swing.text.*;
import javax.swing.border.*;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javax.swing.JCheckBoxMenuItem;

/*
  TODO- When I'm done:
  - Fix this shit up. Multiple Classes, OOP, 
  - Organized, etc 
  - Put online in a repository 
*/
public class NotepadClone extends JFrame implements ActionListener {
	
	private boolean changesMade = false;
	private JFileChooser fc;
	private String filePath;
	private JTextArea input;
	
	private Scanner inputFile = null;
	private PrintWriter outputFile = null;
	private String content;
	private boolean hasSaved = false; // if not, we need to get the filepath
	
	private boolean firstDoc = true; // is this the first document in the session?
	
    private FileFilter filter;	
	// maybe fileSaved should be changeMade?
	private JScrollPane scroller;
	private UndoManager uManager;
	
	private JMenuItem deleteItem;
	private JMenuItem cutItem;
	private JMenuItem copyItem;
	private JMenuItem findItem;
	private JMenuItem findNextItem;
	private JMenuItem replaceItem;
	
	private JDialog find;
	private JButton next;
	private JButton cancel;
	private JCheckBox mcase;
	private JRadioButton jrb1;
	private JRadioButton jrb2;
	private JTextField searchValue;
	
	private JDialog replace;
	private JTextField replaceValue;
	private JTextField searchValue2;
	private JButton findNextButton;
	private JButton replaceButton;
	private JButton replaceAllButton;
	private JButton cancelButton;
	private boolean searched = false;
	
	private JTextField lineField;
	private JButton goToButton;
	//private JButton cancelButton;
	
	private JDialog goTo;
	
	private int findStart;
	private int findLength;

	private boolean fMatchCase;
	private String value;
	
	private int finPos;
	private int touch = 0;
	
	/*for word wrapped feature */
	private boolean wrapped = false;

	public NotepadClone() {
	    // Creating File Chooser
		fc = new JFileChooser();
		
		// Creating and setting its choosable filter
		filter = new FileNameExtensionFilter("Text Document (\".txt\")", "txt");
		fc.addChoosableFileFilter(filter);
		
		// Creating menu bar
		JMenuBar mB = new JMenuBar();
		
		// Creating File menu and its MenuItems
		JMenu fileMenu = new JMenu("File");
		
		JMenuItem newItem = new JMenuItem("New", KeyEvent.VK_N);
		newItem.setActionCommand("new");
		newItem.addActionListener(this);
		newItem.setAccelerator(KeyStroke.getKeyStroke(
		   KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		fileMenu.add(newItem);
		
		JMenuItem openItem = new JMenuItem("Open", KeyEvent.VK_O);
		openItem.setActionCommand("open");
		openItem.addActionListener(this);
		openItem.setAccelerator(KeyStroke.getKeyStroke(
			KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		fileMenu.add(openItem);
		
		JMenuItem saveItem = new JMenuItem("Save", KeyEvent.VK_S);
		saveItem.setActionCommand("save");
		saveItem.addActionListener(this);
		saveItem.setAccelerator(KeyStroke.getKeyStroke(
		    KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		fileMenu.add(saveItem);
		
		// no accelerators or mnemonics on this one
		JMenuItem saveAsItem = new JMenuItem("Save As");
		saveAsItem.setActionCommand("save as");
		saveAsItem.addActionListener(this); 
		fileMenu.add(saveAsItem);
		
		fileMenu.addSeparator();
		
		JMenuItem exitItem = new JMenuItem("Exit", KeyEvent.VK_E);
		exitItem.setActionCommand("exit");
		exitItem.addActionListener(this);
		exitItem.setAccelerator(KeyStroke.getKeyStroke(
		    KeyEvent.VK_F5, ActionEvent.ALT_MASK));
		fileMenu.add(exitItem);
		
		// Creating Edit menu and its MenuItems
		JMenu editMenu = new JMenu("Edit");
		
		JMenuItem undoItem = new JMenuItem("Undo", KeyEvent.VK_Z);
		undoItem.setActionCommand("undo");
		undoItem.addActionListener(this);
		undoItem.setAccelerator(KeyStroke.getKeyStroke(
		    KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
		editMenu.add(undoItem);
		
		JMenuItem redoItem = new JMenuItem("Redo", KeyEvent.VK_Y);
		redoItem.setActionCommand("redo");
		redoItem.addActionListener(this);
		redoItem.setAccelerator(KeyStroke.getKeyStroke(
		    KeyEvent.VK_Y, ActionEvent.CTRL_MASK));
		editMenu.add(redoItem);
		
		editMenu.addSeparator();
		
		// These few will be a bit weirder than the others. 
		cutItem = new JMenuItem(new DefaultEditorKit.CutAction());
		cutItem.setText("Cut");
		cutItem.setMnemonic(KeyEvent.VK_X);
		cutItem.setAccelerator(KeyStroke.getKeyStroke(
		    KeyEvent.VK_X, ActionEvent.CTRL_MASK));
		cutItem.setEnabled(false); // since document is empty at first
		editMenu.add(cutItem);	
		
		copyItem = new JMenuItem(new DefaultEditorKit.CopyAction());
		copyItem.setText("Copy");
		copyItem.setAccelerator(KeyStroke.getKeyStroke(
		     KeyEvent.VK_C, ActionEvent.CTRL_MASK));
	    copyItem.setEnabled(false);
		editMenu.add(copyItem);
		
		JMenuItem pasteItem = new JMenuItem(new DefaultEditorKit.PasteAction());
		pasteItem.setText("Paste");
		pasteItem.setAccelerator(KeyStroke.getKeyStroke(
		     KeyEvent.VK_V, ActionEvent.CTRL_MASK));
		editMenu.add(pasteItem);
		
		deleteItem = new JMenuItem("Delete", KeyEvent.VK_D);
		deleteItem.setActionCommand("delete");
		deleteItem.addActionListener(this);
		deleteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE,0));
		deleteItem.setEnabled(false);
		editMenu.add(deleteItem);
		
		editMenu.addSeparator();
		
		findItem = new JMenuItem("Find", KeyEvent.VK_F);
		findItem.setActionCommand("find");
		findItem.addActionListener(this);
		findItem.setAccelerator(KeyStroke.getKeyStroke(
		    KeyEvent.VK_F, ActionEvent.CTRL_MASK));
		findItem.setEnabled(false);
		editMenu.add(findItem);
		
		findNextItem = new JMenuItem("Find Next", KeyEvent.VK_F);
		findNextItem.setActionCommand("find next");
		findNextItem.addActionListener(this);
		findNextItem.setAccelerator(KeyStroke.getKeyStroke(
		    KeyEvent.VK_F3, 0));
		findNextItem.setEnabled(false);
	    editMenu.add(findNextItem);
		
		replaceItem = new JMenuItem("Replace", KeyEvent.VK_R);
		replaceItem.setActionCommand("replace");
		replaceItem.addActionListener(this);
		replaceItem.setAccelerator(KeyStroke.getKeyStroke(
		    KeyEvent.VK_H, ActionEvent.CTRL_MASK));
		//replaceItem.setEnabled(false);
		editMenu.add(replaceItem);
		
		JMenuItem goToItem = new JMenuItem("Go To...", KeyEvent.VK_G);
		goToItem.setActionCommand("goto");
		goToItem.addActionListener(this);
		goToItem.setAccelerator(KeyStroke.getKeyStroke(
		    KeyEvent.VK_G, ActionEvent.CTRL_MASK));
		editMenu.add(goToItem);
		
		editMenu.addSeparator();
		
		JMenuItem selectAllItem = new JMenuItem("Select All", KeyEvent.VK_S);
		selectAllItem.setActionCommand("select all");
		selectAllItem.addActionListener(this);
		selectAllItem.setAccelerator(KeyStroke.getKeyStroke(
		   KeyEvent.VK_A, ActionEvent.CTRL_MASK));
		editMenu.add(selectAllItem);
		
		JMenuItem timeDateItem = new JMenuItem("Time/Date", KeyEvent.VK_T);
		timeDateItem.setActionCommand("time date");
		timeDateItem.addActionListener(this);
		timeDateItem.setAccelerator(KeyStroke.getKeyStroke(
		   KeyEvent.VK_F5, 0));
	    editMenu.add(timeDateItem);
		
		JMenu formatMenu = new JMenu("Format");
		
		JCheckBoxMenuItem wordWrapItem = new JCheckBoxMenuItem("Word Wrap");
		wordWrapItem.setActionCommand("word wrap");
		wordWrapItem.addActionListener(this);
		formatMenu.add(wordWrapItem);
		
		JMenuItem fontItem = new JMenuItem("Font");
		fontItem.setActionCommand("font");
		fontItem.addActionListener(this);
		formatMenu.add(fontItem);
		
		mB.add(fileMenu);
		mB.add(editMenu);
		mB.add(formatMenu);
		
		this.setJMenuBar(mB);
		
		// So now I know to do this for all of them. 
		
		// Creating text Area
		input = new JTextArea(new PlainDocument());
		
		// We need to know when stuff has changed so we can
		// ask to save at close of document.
		input.getDocument().addDocumentListener(new DocumentListener() {
		   @Override
		   public void insertUpdate(DocumentEvent e) {
			   changesMade = true;	   //changesMade = true;
		   }
		   
		   @Override
		   public void removeUpdate(DocumentEvent e) {
			   changesMade = true;	   //changesMade = true;
		   }
		   
		   @Override
		   public void changedUpdate(DocumentEvent e) {
		   }
		
		});
		
		// If the user hasn't saved, make sure they're asked
		// to do some before they've pressed the red x. 
		this.addWindowListener(new WindowAdapter() {
		   public void windowClosing(WindowEvent e) {
		      fileExit(); // it's the same thing..			  
		   }
		});
		
		// Creating undo/redo manager and etc
		uManager = new UndoManager();
		input.getDocument().addUndoableEditListener(uManager);
		
		// Need this so that I can disable certain menu items
		// based on certain conditions.
		// Only reason I'm using this is because it checks 
		// all the time, has nothing to do with 
		// the caret 
		input.addCaretListener(new CaretListener() {
		   @Override
		   public void caretUpdate(CaretEvent e) {
		      if (input.getSelectedText() == null) {
			     deleteItem.setEnabled(false);
				 cutItem.setEnabled(false);
				 copyItem.setEnabled(false);
			  }else {
			     deleteItem.setEnabled(true);
				 cutItem.setEnabled(true);
				 copyItem.setEnabled(true);
			  }
		      
			  if (input.getText() == null ||
			      input.getText().equals("")) {
			    findItem.setEnabled(false);
				findNextItem.setEnabled(false);
				replaceItem.setEnabled(false);
			  }else {
                findItem.setEnabled(true);
                findNextItem.setEnabled(true);
				replaceItem.setEnabled(true);
			  }
		   }
		
		});
		// Setting it's font
		input.setFont( new Font("Consolas", Font.PLAIN, 14));
		
		// adding text area to scrollbar
		scroller = new JScrollPane(input, 
		           ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				   ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		
		// Adding scroller to the frame
		add(scroller);
		
		// Creating Bottom empty label (for some reason)
		// Much Later, the reason that this is the status bar 
		JLabel bottom = new JLabel(" ");
		add(bottom, BorderLayout.SOUTH);
		
		resetDocument();
	}
	

	// ActionListener must be added to each individual element in 
	// a menu.
	public void actionPerformed(ActionEvent e) {
		if ("new".equals(e.getActionCommand())) {
		   fileNew();
		} else if ("open".equals(e.getActionCommand())) {
		   fileOpen();
		} else if ("save".equals(e.getActionCommand())) {
		   fileSave();
		} else if ("save as".equals(e.getActionCommand())) {
		   fileSaveAs();
		} else if ("exit".equals(e.getActionCommand())) {
		   fileExit();
		} else if ("undo".equals(e.getActionCommand())) {
		   editUndo();
		} else if ("redo".equals(e.getActionCommand())) {
		   editRedo();
		} else if ("delete".equals(e.getActionCommand())) {
		   editDelete();
		} else if ("find".equals(e.getActionCommand())) {
		   editFind();
		} else if ("find next".equals(e.getActionCommand())) {
		   editFindNext();
		} else if ("replace".equals(e.getActionCommand())) {
		   editReplace();
		} else if ("goto".equals(e.getActionCommand())) {
		   editGoTo();
		} else if ("time date".equals(e.getActionCommand())) {
		   editTimeDate();
		} else if ("select all".equals(e.getActionCommand())) {
		   editSelectAll();
		} else if ("word wrap".equals(e.getActionCommand())) {
		   formatWordWrap();
		} else if ("font".equals(e.getActionCommand())) {
		   formatFont();
		}
		
	}
	
	// File Menu Stuff 
	public void fileExit() {
		if (changesMade) { // if changesMade, prompt to save
		   int choice = saveWarning();
		   if (choice == JOptionPane.YES_OPTION) {
		      fileSave();
			  System.exit(0);
		   } else if (choice == JOptionPane.NO_OPTION) {
		      System.exit(0);
		   }
		} else {
		   System.exit(0);
		}
	}
	
	// give a default name like document 1, as well
	public void fileNew() {
		if (changesMade) { 
		   int choice = saveWarning();
		   if (choice == JOptionPane.YES_OPTION) {
		      fileSave();
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
		this.setTitle(filePath + " - NotepadClone");
	}
	
	public int saveWarning() {
		try {
		  return JOptionPane.showConfirmDialog(
		          this, "Save file \"" + filePath + "\" ?", 
		          "Save", JOptionPane.YES_NO_CANCEL_OPTION,
			      JOptionPane.QUESTION_MESSAGE);
	    } catch (HeadlessException e) {}
		return 0; // need to do this
	}
	
	public void fileSave() {
	   if (!hasSaved) {
          int returnVal = fc.showSaveDialog(this);
		
	      if (returnVal == JFileChooser.APPROVE_OPTION) {
	         File file = fc.getSelectedFile();
		     if (file.exists()) {
			    int choice = replaceWarning();
				if (choice == JOptionPane.YES_OPTION) {
				   filePath = file.getAbsolutePath();
				   save();
				   hasSaved = true;
				} else if (choice == JOptionPane.NO_OPTION) {
				   fileSave();
				}
			 } else {
	            filePath = file.getAbsolutePath();
		        save();
				hasSaved = true;
			 }
	      }
	   } else {
	      save();
	   }
	}
	
	public void fileSaveAs() {
       int returnVal = fc.showDialog(this, "Save As");
	   
	   if (returnVal == JFileChooser.APPROVE_OPTION) {
	      File file = fc.getSelectedFile();
		  if (file.exists()) {
		      int choice = replaceWarning();
			  if (choice == JOptionPane.YES_OPTION) {
			     filePath = file.getAbsolutePath();
				 save();
			  } else if (choice == JOptionPane.NO_OPTION) {
				fileSaveAs();
			  }
		  } else {
		    filePath = file.getAbsolutePath();
			save();
		  }
	   }
	}
	
	public int replaceWarning() {
	   try {
	      return JOptionPane.showConfirmDialog(
		     this, "File already exists. " +
		     "Do you want to replace it?", "Replace file",
		     JOptionPane.YES_NO_CANCEL_OPTION, 
		     JOptionPane.QUESTION_MESSAGE);
	   }catch (HeadlessException e) {}
       return 0; // need to see this	
	}
	
	public void save() {
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
		   this.setTitle(filePath + " - NotepadClone");
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
	public void fileOpen() {
		int returnVal = fc.showOpenDialog(this);
		
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
			this.setTitle(filePath + " - NotepadClone"); // set title to file name
	    } 
	}
	
	// Edit Menu Stuff
	public void editUndo() {
	   try {
          uManager.undo();
	   }catch (CannotUndoException e) {}
	}
	
	public void editRedo() {
	   try {
	      uManager.redo();
	   }catch (CannotRedoException e) {}
	}
	
	public void editDelete() {
	   // bandaid fix 
	   // I'd like to disable Delete, Copy, Cut etc when
	   // no item is selected, but I don't know how. 
	   // EDIT: Fixed it
	   if (input.getSelectedText() != null){
	   input.setText(input.getText().replace(
	         input.getSelectedText(), ""));}
	}
	
	public void createFindDialog() {
	    // I need to addPropertyChangeListener() on the  Dialog box
		// Then put logic in propertychange(PropertyChangeEvent e)
		// That says what to do if the button is "Find", "Find Next", 
		// etc
		// Just add(stuff) to Jdialog to set up the GUI
		// The Find Box needs :
		//    Label TextArea Button
		//    Check Box , Radio Buttons,  Button
	    find = new JDialog(this, "Find", false);
		find.setResizable(false);
		find.setSize(500,175);
		find.setLocationRelativeTo(null);
		JPanel jpmain = new JPanel(new BorderLayout());
		
		JPanel fpanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel fLabel = new JLabel("Find What");
		searchValue = new JTextField();
		searchValue.setPreferredSize(new Dimension(250,30));
        fpanel.add(fLabel);
		fpanel.add(searchValue);
		
		JPanel rPanel = new JPanel();
		JPanel grPanel = new JPanel();
		grPanel.setLayout(new BoxLayout(grPanel, BoxLayout.X_AXIS));
		rPanel.setBorder(new TitledBorder("Direction"));
		ButtonGroup group = new ButtonGroup();
		jrb1 = new JRadioButton("Up");
		jrb2 = new JRadioButton("Down");
		jrb2.setSelected(true);
		group.add(jrb1);
		group.add(jrb2);
		rPanel.add(jrb1);
		rPanel.add(jrb2);
		
		mcase = new JCheckBox("Match Case", false);

		grPanel.add(mcase);
		grPanel.add(Box.createRigidArea(new Dimension(50,0)));
		grPanel.add(rPanel);
		
		JPanel holder = new JPanel();
		holder.add(fpanel, BorderLayout.NORTH);
		holder.add(grPanel, BorderLayout.SOUTH);
		
		JPanel bHolder = new JPanel(new GridLayout(3,1));
		next = new JButton("Find Next");
		cancel = new JButton("Cancel");
        bHolder.add(next);
		bHolder.add(cancel);
		
		JPanel east = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTH;
		gbc.weighty = 1;
		east.add(bHolder, gbc);
				
		jpmain.add(holder);
		jpmain.add(east, BorderLayout.EAST);
		
		find.add(jpmain);
		find.setVisible(true);
	}
	
	public int findDown() {
	   value = searchValue.getText();
	   findLength = value.length();
	   
       if (mcase.isSelected())
	      return getFullDoc().indexOf(
		     value, finPos);
	   else {
	      return getFullDoc().toLowerCase().indexOf(
		     value.toLowerCase(), finPos);
	   }

	}

	
	private String getFullDoc() {
	   try {
          return input.getText(
	         input.getDocument().getStartPosition().getOffset(),
		     input.getDocument().getLength());
       } catch (BadLocationException e) {}	
	   return null;
	}
	
	public int findUp() {
	   value = searchValue.getText();
	   findLength = value.length();
	   
	   if (mcase.isSelected()) {
          return findBackwards(
		     getFullDoc(), value, finPos);
       } else {
		  return findBackwards(getFullDoc().toLowerCase(),
			 value.toLowerCase(), finPos);
	   }  
	}
	
	
	public int findBackwards(
	    String str1, String str2, 
		int startFrom) 
	{	
       if ((str1.lastIndexOf(str2, startFrom) == 
	      (finPos-findLength)) && (str1.lastIndexOf(
	      str2, startFrom) == findStart)) 
	   { 
	      // if caret would be
          // before search term
		  // for the second time, 
		  // force it to search
		  // further up.
	      return str1.lastIndexOf(
		         str2, startFrom -findLength-1);
	   } else if (
	      (str1.lastIndexOf(str2, startFrom) == 
	      (finPos-findLength)) && (str1.lastIndexOf(
		  str2, startFrom) != findStart))
	   {
	      // if caret would be
		  // before search term 
		  // for the first time, 
		  // just let it go
	      return str1.lastIndexOf(
		         str2, startFrom);
	   } else { 
	      // if caret wouldn't be 
		  // before search term,
		  // let it go
	      return str1.lastIndexOf(
		         str2, startFrom);
	   }
	}
	
	public void findValue() {
	   findLength = value.length();
	   finPos = input.getCaretPosition();
	   
	   boolean downSelected = jrb2.isSelected();
	   
       if (downSelected) {
	       findStart = findDown();
       } else if (!downSelected) {
		   findStart = findUp();
	   }
	   
	   if (findStart == -1) {
	      try {
	         JOptionPane.showMessageDialog(this, 
			 "Cannot find \" " + value + "\".",
			 "NotepadClone", JOptionPane.INFORMATION_MESSAGE);
		  } catch (HeadlessException e) {}
	   } else {	      
	      input.setCaretPosition(findStart);//;
	      input.select(findStart, findStart+findLength);
	   }
	}
	
	public void editFind() {
	   createFindDialog();
	   value = searchValue.getText();
	   
	   next.addActionListener(new ActionListener() {
	      public void actionPerformed(ActionEvent e) {
		      findValue();
		  }
	   });
	   
	   cancel.addActionListener(new ActionListener() {
	      public void actionPerformed(ActionEvent e) {
		     find.setVisible(false);
		  }
	   });
	}
	
	public void editFindNext() {
	   // This just automatically skips to the next thing   
	   if (value != null) {
	      findValue();
	   } else {
	      editFind();
	   }
	}
	
	public void editTimeDate() {
	   DateFormat dateFormat = new SimpleDateFormat(
	      "hh:mm a MM/dd/yyy");
	   Date date = new Date();
	   
	   input.insert(
	      dateFormat.format(date),
	      input.getCaretPosition());
	}
	
	public void editSelectAll() {
	   input.selectAll();
	}
	
	public void editReplace() {
	   createReplaceDialog();
	   value = searchValue2.getText();
	   
	   findNextButton.addActionListener(new ActionListener() {
		   public void actionPerformed(ActionEvent e) {
		      rFindValue();
		   }
	   });
	   
	   replaceButton.addActionListener(new ActionListener() {
		   public void actionPerformed(ActionEvent e) {
		      replaceText();
		   }
	   });
	   
	   
	   replaceAllButton.addActionListener(new ActionListener() {
		   public void actionPerformed(ActionEvent e) {
			  replaceAll();
		   }
	   });
	   
	   cancelButton.addActionListener(new ActionListener() {
		   public void actionPerformed(ActionEvent e) {
		      replace.setVisible(false);
		   }
	   });
	}
	
	public void createReplaceDialog() {
	   replace = new JDialog(this, "Replace", false);
	   replace.setResizable(false);
	   replace.setSize(500,200);
	   replace.setLocationRelativeTo(null);
	   
	   JPanel jpmain = new JPanel(new BorderLayout());
	   
	   JPanel centerPanel = new JPanel();
	   JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	   JPanel labelPanel = new JPanel();
	   labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));
	   JPanel fieldPanel = new JPanel(new GridLayout(2,1));
	   
	   JPanel findPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	   JLabel findLabel = new JLabel("Find What");
	   searchValue2 = new JTextField();
	   searchValue2.setPreferredSize(new Dimension(250,25));
	   /*
	   findPanel.add(findLabel);
	   findPanel.add(searchValue2);*/
	   labelPanel.add(findLabel);
	   labelPanel.add(Box.createRigidArea(new Dimension(0,10)));
	   
	   fieldPanel.add(searchValue2);
	   
	   JPanel replacePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	   JLabel replaceLabel = new JLabel("Replace With");
	   replaceValue = new JTextField();
	   replaceValue.setPreferredSize(new Dimension(250,25));
	   /*replacePanel.add(replaceLabel);
	   replacePanel.add(replaceValue);*/
	   labelPanel.add(replaceLabel);
	   fieldPanel.add(replaceValue);
	   /*
	   inputPanel.add(findPanel);
	   inputPanel.add(replacePanel);*/
	   inputPanel.add(labelPanel);
	   inputPanel.add(fieldPanel);
	   
	   mcase = new JCheckBox("Match Case", false);
	   JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	   leftPanel.add(mcase);
	   
	   JPanel grPanel = new JPanel();
	   grPanel.setLayout(new BoxLayout(grPanel, BoxLayout.Y_AXIS));
	   grPanel.add(inputPanel);
	   //grPanel.add(Box.createRigidArea(new Dimension(0,0)));
	   grPanel.add(leftPanel);
	  
	   JPanel buttonHolder = new JPanel(new GridLayout(4,1));
	   findNextButton = new JButton("Find Next");
	   replaceButton = new JButton("Replace");
	   replaceAllButton = new JButton("Replace All");
	   cancelButton = new JButton("Cancel");
	   
	   buttonHolder.add(findNextButton);
	   buttonHolder.add(replaceButton);
	   buttonHolder.add(replaceAllButton);
	   buttonHolder.add(cancelButton);
	   
	   JPanel east = new JPanel(new GridBagLayout());
	   GridBagConstraints gbc = new GridBagConstraints();
	   gbc.anchor = GridBagConstraints.NORTH;
	   gbc.weighty = 1;
	   east.add(buttonHolder, gbc);
	   
	   jpmain.add(grPanel);
	   jpmain.add(east, BorderLayout.EAST);
	   replace.add(jpmain);
	   replace.setVisible(true);
	}
	
	private void rFindValue() {
	   findLength = value.length();
	   finPos = input.getCaretPosition();
	   
	   findStart = rFind();
	   
	   if (findStart == -1) {
	      try {
		     JOptionPane.showMessageDialog(this,
			 "Cannot find \" " + value + "\" .",
			 "NotepadClone", JOptionPane.INFORMATION_MESSAGE);
		  }catch (HeadlessException e){}
	   } else {
	      input.setCaretPosition(findStart);
		  input.select(findStart, findStart+findLength);
		  searched = true;
	   }
	   
	}
	
	public int rFind() {
       value = searchValue2.getText();
	   findLength = value.length();
	   
	   if (mcase.isSelected()) {
		  return getFullDoc().indexOf(value,
		        finPos);
	   } else {
		  return getFullDoc().toLowerCase().indexOf(
		        value.toLowerCase(), finPos);
	   }
	}
	
	public void replaceText() {
	   if(searched){
          input.replaceRange(replaceValue.getText(), 
	         findStart, findStart+findLength);
	   } else {
	      rFindValue();
		  replaceText();
	   }
	}
	
	public void replaceAll() {
	   input.setText(
	      getFullDoc().replaceAll(
	      searchValue2.getText(), 
		  replaceValue.getText()));
	}
	
	public void editGoTo() {
	    createGoToDialog();
		
		goToButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { 
			   goToLine();
			}
		});
		
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			   goTo.setVisible(false);
			}
		});
	}
	
	public void createGoToDialog() {
		goTo = new JDialog(this, "Go To Line", false);
		goTo.setResizable(false);
		goTo.setSize(350, 150);
		goTo.setLocationRelativeTo(null);
		
		JPanel mainPanel = new JPanel();
		
		JPanel inputPanel = new JPanel(new GridLayout(2,1));
		JLabel lineLabel = new JLabel("Line Number");
		JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		labelPanel.add(lineLabel);
		lineField = new JTextField(
		   ""+(getCurrentLineNumber()+1));
		lineField.setPreferredSize(new Dimension(250, 30));
		inputPanel.add(labelPanel);
		inputPanel.add(lineField);
		JPanel holder = new JPanel();
		holder.setLayout(new BoxLayout(holder, BoxLayout.X_AXIS));
		holder.add(inputPanel);
		holder.add(Box.createRigidArea(new Dimension(50,0)));
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		goToButton = new JButton("Go To");
		cancelButton = new JButton("Cancel");
		buttonPanel.add(Box.createRigidArea(new Dimension(50,0)));
		buttonPanel.add(goToButton);
		buttonPanel.add(cancelButton);
		
		mainPanel.add(holder);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);
		goTo.add(mainPanel);
		goTo.setVisible(true);
	}
	
	public int getCurrentLineNumber() {
	   try {
	   int caretPos = input.getCaretPosition();
	   int lineNum = input.getLineOfOffset(caretPos);
	   //int columnNum = caretPos - input.getLineStartOffset(lineNum);
	   return lineNum;
	   } catch (BadLocationException e) {}
	   return 0;
	}
	
	public void goToLine() {
	   try{
	      if (Integer.parseInt(lineField.getText()) > 
		      input.getLineCount()) {
	         JOptionPane.showMessageDialog(this, 
			   "The line number is beyond the total " +
			   " number of lines.", "NotepadClone - GoTo Line", 
			   JOptionPane.INFORMATION_MESSAGE);
			   
	      } else {
		     int caretPos = input.getLineStartOffset(
	            Integer.parseInt(lineField.getText()));
		     input.setCaretPosition(caretPos-1);
		  }
	   } catch (BadLocationException e)  {}
	}
	
	public void formatWordWrap() {
	   if (wrapped) {
	      input.setLineWrap(false);
	      input.setWrapStyleWord(false);
	   } else {
	      input.setLineWrap(true);
		  input.setWrapStyleWord(true);
		  wrapped = true;
	   }
	}
	
	public void formatFont() {
	
	}
	
	public void createFontDialog() {
	
	}
	
	public static void main(String[] args) {
		NotepadClone frame = new NotepadClone();
		frame.setSize(800, 600);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setLocationRelativeTo(null); // Center the frame
		frame.setVisible(true);
	}
}