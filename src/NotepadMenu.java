/* Stores all of the functionality for the menus */
import javax.swing.*;

public abstract class NotepadMenu extends JMenu{
	//private JMenu menu;
	
	public NotepadMenu(String name, NotepadScreen input) {
		super(name);
	}
	
	abstract void makeMenu();
	/*
	public JMenu getMenu() {
		return this.menu;
	}*/
}