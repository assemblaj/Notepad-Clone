import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.BorderFactory;
import javax.swing.border.*;

public class ViewMenu extends NotepadMenu {
	
	private NotepadScreen input;
	private boolean statusBarEnabled = false;
	private JPanel statusBar;
	private JLabel status;
	private String statusString;
		
	public ViewMenu(NotepadScreen input) {
		super("View", input);
		this.input = input;
		
		statusBar = new JPanel();
		statusString = "" + 
			"Ln " + ( getLineNumber() + 1 ) + 
			", Col " + getColumnNumber();

		status = new JLabel(statusString);
				
		statusBar.add(status);
		
		makeMenu();
	}
	
	public void makeMenu() {
		JCheckBoxMenuItem statusBarItem = new JCheckBoxMenuItem("Status Bar");
		statusBarItem.setActionCommand("status bar");
		statusBarItem.addActionListener(input);
		add(statusBarItem);
	}
	
	public void statusBar() {
		setStatusBar();
		if (statusBarEnabled) {
			input.getFrame().remove(statusBar);
			updateFrame();
			statusBarEnabled = false;
		} else {
			input.getFrame().add(statusBar, BorderLayout.SOUTH);
			updateFrame();
			statusBarEnabled = true;
		}
	}
	

	public void setStatusBar() {
		statusString = 
		   "Ln " + (getLineNumber() + 1 ) +
		   ", Col " + (getColumnNumber() + 1);
		status.setText(statusString);	
	}
	
	// Need to do these after the addition or 
	// removal of a frame in order for results 
	// to show.
	public void updateFrame() {
		input.getFrame().validate();
		input.getFrame().repaint();	
	}
	
	public int getLineNumber() {
		try {
			int caretPos = input.getCaretPosition();
			int lineNum = input.getLineOfOffset(caretPos);
			return lineNum;
		} catch (BadLocationException e) {}
		return 0;
	}
	
	public int getColumnNumber() {
		try {
			int caretPos = input.getCaretPosition();
			int lineNum = input.getLineOfOffset(caretPos);
			int columnNum  = caretPos - input.getLineStartOffset(lineNum);
			return columnNum;
		} catch (BadLocationException e) {}
		return 0;
	} 
	
	class StatusMonitor implements CaretListener 
	{
		@Override
		public void caretUpdate(CaretEvent e) {
			if (statusBarEnabled) {
				setStatusBar();
			}
		}
	} 
	
	public StatusMonitor initStatusMonitor() {
		return new StatusMonitor();
	}
}