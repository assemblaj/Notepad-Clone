import javax.swing.event.*;
import javax.swing.*;
import javax.swing.undo.*;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.*;
import javax.swing.text.*;
import javax.swing.border.*;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javax.swing.JCheckBoxMenuItem;

public class EditMenu extends NotepadMenu {	
	private NotepadScreen input;
	
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
	
	public EditMenu(NotepadScreen input) {
		super("Edit", input);
		this.input = input;
		
		uManager = new UndoManager();
		makeMenu();
	}
	
	public void makeMenu() {
		JMenuItem undoItem = new JMenuItem("Undo", KeyEvent.VK_Z);
		undoItem.setActionCommand("undo");
		undoItem.addActionListener(input);
		undoItem.setAccelerator(KeyStroke.getKeyStroke(
		    KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
		add(undoItem);
		
		JMenuItem redoItem = new JMenuItem("Redo", KeyEvent.VK_Y);
		redoItem.setActionCommand("redo");
		redoItem.addActionListener(input);
		redoItem.setAccelerator(KeyStroke.getKeyStroke(
		    KeyEvent.VK_Y, ActionEvent.CTRL_MASK));
		add(redoItem);
		
		addSeparator();
		
		// These few will be a bit weirder than the others. 
		cutItem = new JMenuItem(new DefaultEditorKit.CutAction());
		cutItem.setText("Cut");
		cutItem.setMnemonic(KeyEvent.VK_X);
		cutItem.setAccelerator(KeyStroke.getKeyStroke(
		    KeyEvent.VK_X, ActionEvent.CTRL_MASK));
		cutItem.setEnabled(false); // since document is empty at first
		add(cutItem);	
		
		copyItem = new JMenuItem(new DefaultEditorKit.CopyAction());
		copyItem.setText("Copy");
		copyItem.setAccelerator(KeyStroke.getKeyStroke(
		     KeyEvent.VK_C, ActionEvent.CTRL_MASK));
	    copyItem.setEnabled(false);
		add(copyItem);
		
		JMenuItem pasteItem = new JMenuItem(new DefaultEditorKit.PasteAction());
		pasteItem.setText("Paste");
		pasteItem.setAccelerator(KeyStroke.getKeyStroke(
		     KeyEvent.VK_V, ActionEvent.CTRL_MASK));
		add(pasteItem);
		
		deleteItem = new JMenuItem("Delete", KeyEvent.VK_D);
		deleteItem.setActionCommand("delete");
		deleteItem.addActionListener(input);
		deleteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE,0));
		deleteItem.setEnabled(false);
		add(deleteItem);
		
		addSeparator();
		
		findItem = new JMenuItem("Find", KeyEvent.VK_F);
		findItem.setActionCommand("find");
		findItem.addActionListener(input);
		findItem.setAccelerator(KeyStroke.getKeyStroke(
		    KeyEvent.VK_F, ActionEvent.CTRL_MASK));
		findItem.setEnabled(false);
		add(findItem);
		
		findNextItem = new JMenuItem("Find Next", KeyEvent.VK_F);
		findNextItem.setActionCommand("find next");
		findNextItem.addActionListener(input);
		findNextItem.setAccelerator(KeyStroke.getKeyStroke(
		    KeyEvent.VK_F3, 0));
		findNextItem.setEnabled(false);
	    add(findNextItem);
		
		replaceItem = new JMenuItem("Replace", KeyEvent.VK_R);
		replaceItem.setActionCommand("replace");
		replaceItem.addActionListener(input);
		replaceItem.setAccelerator(KeyStroke.getKeyStroke(
		    KeyEvent.VK_H, ActionEvent.CTRL_MASK));
		//replaceItem.setEnabled(false);
		add(replaceItem);
		
		JMenuItem goToItem = new JMenuItem("Go To...", KeyEvent.VK_G);
		goToItem.setActionCommand("goto");
		goToItem.addActionListener(input);
		goToItem.setAccelerator(KeyStroke.getKeyStroke(
		    KeyEvent.VK_G, ActionEvent.CTRL_MASK));
		add(goToItem);
		
		addSeparator();
		
		JMenuItem selectAllItem = new JMenuItem("Select All", KeyEvent.VK_S);
		selectAllItem.setActionCommand("select all");
		selectAllItem.addActionListener(input);
		selectAllItem.setAccelerator(KeyStroke.getKeyStroke(
		   KeyEvent.VK_A, ActionEvent.CTRL_MASK));
		add(selectAllItem);
		
		JMenuItem timeDateItem = new JMenuItem("Time/Date", KeyEvent.VK_T);
		timeDateItem.setActionCommand("time date");
		timeDateItem.addActionListener(input);
		timeDateItem.setAccelerator(KeyStroke.getKeyStroke(
		   KeyEvent.VK_F5, 0));
	    add(timeDateItem);	
	}
	
	public UndoManager initUndoManager() {
		return uManager;
	}
	

	public void undo() {
	   try {
          uManager.undo();
	   }catch (CannotUndoException e) {}
	}
	
	public void redo() {
	   try {
	      uManager.redo();
	   }catch (CannotRedoException e) {}
	}

	public void delete() {
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
	    find = new JDialog(input.getFrame(), "Find", false);
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
	         JOptionPane.showMessageDialog(input.getFrame(), 
			 "Cannot find \" " + value + "\".",
			 "NotepadClone", JOptionPane.INFORMATION_MESSAGE);
		  } catch (HeadlessException e) {}
	   } else {	      
	      input.setCaretPosition(findStart);//;
	      input.select(findStart, findStart+findLength);
	   }
	}
	
	public void find() {
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
	
	public void findNext() {
	   // This just automatically skips to the next thing   
	   if (value != null) {
	      findValue();
	   } else {
	      find();
	   }
	}
	
	public void timeDate() {
	   DateFormat dateFormat = new SimpleDateFormat(
	      "hh:mm a MM/dd/yyy");
	   Date date = new Date();
	   
	   input.insert(
	      dateFormat.format(date),
	      input.getCaretPosition());
	}
	
	public void selectAll() {
	   input.selectAll();
	}
	
	public void replace() {
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
	   replace = new JDialog(input.getFrame(), "Replace", false);
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
	   
	   //findPanel.add(findLabel);
	   //findPanel.add(searchValue2);
	   labelPanel.add(findLabel);
	   labelPanel.add(Box.createRigidArea(new Dimension(0,10)));
	   
	   fieldPanel.add(searchValue2);
	   
	   JPanel replacePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	   JLabel replaceLabel = new JLabel("Replace With");
	   replaceValue = new JTextField();
	   replaceValue.setPreferredSize(new Dimension(250,25));
	   //replacePanel.add(replaceLabel);
	   //replacePanel.add(replaceValue);
	   labelPanel.add(replaceLabel);
	   fieldPanel.add(replaceValue);
	   
	   //inputPanel.add(findPanel);
	   //inputPanel.add(replacePanel);
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
		     JOptionPane.showMessageDialog(input.getFrame(),
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
	
	public void goTo() {
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
		goTo = new JDialog(input.getFrame(), "Go To Line", false);
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
	         JOptionPane.showMessageDialog(input.getFrame(), 
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

	// Enables and disables edit functionality based on the state
	// of the text area.
	class EditValidator implements CaretListener
	{
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
	}
	
	public EditValidator initEditValidator() {
		return new EditValidator();
	}
}