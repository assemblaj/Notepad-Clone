import javax.swing.*;
import javax.imageio.*; // For icon
import java.io.*;		// for icon
import javax.swing.UIManager.*; // For look and feel

public class NotepadClone extends JFrame {
	private NotepadScreen textArea;
	private JScrollPane scroller;
	
	public NotepadClone() {
		// Use system Look and Feel
		try {UIManager.setLookAndFeel(
			UIManager.getSystemLookAndFeelClassName());} 
		catch (UnsupportedLookAndFeelException e) {e.printStackTrace();}
		catch (ClassNotFoundException e) {e.printStackTrace();}
		catch (InstantiationException e) {e.printStackTrace();}
		catch (IllegalAccessException e) {e.printStackTrace();}
		
		textArea = new NotepadScreen(this);
		
		// adding text area to scrollbar
		scroller = new JScrollPane(textArea, 
		           ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				   ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		
		// Adding scroller to the frame
		add(scroller);
		
		// Setting icon for the window
		try {
			setIconImage(
				ImageIO.read(
				NotepadClone.class.getResource(
				"res/Notepad.png")));
		} catch (IOException e) {e.printStackTrace();}
	}
	
	public static void main(String[] args) {
		NotepadClone frame = new NotepadClone();
		frame.setSize(800, 600);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setLocationRelativeTo(null); // Center the frame
		frame.setVisible(true);
	}
} 