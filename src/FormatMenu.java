import javax.swing.JCheckBoxMenuItem;
import javax.swing.*;

public class FormatMenu extends NotepadMenu {
	private NotepadScreen input;

	/*for word wrapped feature */
	private boolean wrapped = false;
	
	public FormatMenu(NotepadScreen input) {
		super("Format", input);
		this.input = input;
		
		makeMenu();
	}
	
	public void makeMenu() {
		JCheckBoxMenuItem wordWrapItem = new JCheckBoxMenuItem("Word Wrap");
		wordWrapItem.setActionCommand("word wrap");
		wordWrapItem.addActionListener(input);
		add(wordWrapItem);
		
		JMenuItem fontItem = new JMenuItem("Font");
		fontItem.setActionCommand("font");
		fontItem.addActionListener(input);
		add(fontItem);	
	}
	
	public void wordWrap() {
	   if (wrapped) {
	      input.setLineWrap(false);
	      input.setWrapStyleWord(false);
	   } else {
	      input.setLineWrap(true);
		  input.setWrapStyleWord(true);
		  wrapped = true;
	   }
	}
}