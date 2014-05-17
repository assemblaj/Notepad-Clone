import javax.swing.*;

public class NotepadClone extends JFrame {
	private NotepadScreen textArea;
	private JScrollPane scroller;
	
	public NotepadClone() {
		textArea = new NotepadScreen(this);
		
		// adding text area to scrollbar
		scroller = new JScrollPane(textArea, 
		           ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				   ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		
		// Adding scroller to the frame
		add(scroller);
	}
	
	public static void main(String[] args) {
		NotepadClone frame = new NotepadClone();
		frame.setSize(800, 600);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setLocationRelativeTo(null); // Center the frame
		frame.setVisible(true);
	}
} 