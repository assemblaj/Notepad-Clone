import javax.swing.JCheckBoxMenuItem;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import javax.swing.text.*;
import javax.swing.event.*;
import javax.swing.BorderFactory;
import javax.swing.border.*;
import java.awt.event.*;

public class FormatMenu extends NotepadMenu {
	private NotepadScreen input;

	/*for word wrapped feature */
	private boolean wrapped = false;
	
	/* for font selecter features */
	protected String selectedFont;
	protected String selectedStyle;
	protected String selectedSize;
	
	// Current font based on selected properties
	private Font currentFont;
	
	// All possible selections to be put in JLists
	private String[] allFonts; // All of the fonts on the file system.
	private String[] allStyles = {"Plain", "Bold", "Italic", "Bold Italic"};
	private String[] allSizes = {"8","9","10","11","12","14","16","18","20",
	                             "22", "24", "26", "28", "36", "48", "72"};
	
	
	private JDialog fontDialog; // Dialog for selecting fonts	
	private JLabel sampleText; // Panel for showing font samples
	
	//private boolean searching = true;
	
	public FormatMenu(NotepadScreen input) {
		super("Format", input);
		this.input = input;

		currentFont = input.getFont();
		
		selectedFont = currentFont.getName();
		selectedSize = Integer.toString(currentFont.getSize());
		selectedStyle = parseStyle(currentFont.getStyle());
		getAllFonts();
		
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
	
	// Font stuff

	public void getAllFonts() {
		GraphicsEnvironment ge; 
		ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		
		Font[] fonts = ge.getAllFonts();
		allFonts = new String[fonts.length];
		
		for (int i = 0; i < fonts.length; i++) {
			allFonts[i] = fonts[i].getName();	
		}
	}
	
	public void font() {
		createFontDialog();
	}
	
	public void createFontDialog() {
		fontDialog = new JDialog(
			input.getFrame(), "Font", false);
		fontDialog.setResizable(false);
		fontDialog.setSize(420,450);
		fontDialog.setLocationRelativeTo(null);
		JPanel jpmain = new JPanel();
		
		// ListPanels contain a JList and a JTextField 
		ListPanel fontList = new ListPanel(
			"Font:", allFonts, selectedFont, this);
		ListPanel styleList = new ListPanel(
			"Style:", allStyles, selectedStyle, this);
		ListPanel sizeList = new ListPanel(
			"Size:", allSizes, selectedSize, this);
		
		// The text in the cell will render in the font and style
		// specified by the text in the cell
		fontList.getList().setCellRenderer(
			new FontCellRenderer());
		styleList.getList().setCellRenderer(
			new StyleCellRenderer(this));
		
		// Each JTextField needs to be used to search the JList
		fontList.getTextField().getDocument().addDocumentListener(
			new ListSearcher(fontList, "Font"));
		sizeList.getTextField().getDocument().addDocumentListener(
			new ListSearcher(sizeList, "Size"));
		
		// Each JList needs to have selectable elements that
		// are displayed in the JTextField and affect the Sample
		fontList.getList().addListSelectionListener(
			new PropertySelector(fontList, this, "Font"));
		styleList.getList().addListSelectionListener(
			new PropertySelector(styleList, this, "Style"));
		sizeList.getList().addListSelectionListener(
			new PropertySelector(sizeList, this, "Size"));
		
		//Create the panel to store the sample text
		JPanel sampleBox = new JPanel(new BorderLayout());
		sampleBox.setPreferredSize(new Dimension(200,100));
		
		//Create the label for the sample text, assuring that it's 
		//centred and has the current font.
		sampleText = new JLabel("AaBbYyZz", SwingConstants.CENTER);
		sampleText.setFont(currentFont);
		
		// Add the label to the panel and put a rectangle around
		// it with a title (fieldset & legend).
		sampleBox.add(sampleText);
		sampleBox.setBorder(
			BorderFactory.createTitledBorder("Sample"));
		
		// Add padding to position it correctly in the dialog.
		JPanel samplePadding = new JPanel();
		samplePadding.setBorder(
			new EmptyBorder(10, 170, 0, 0));
		samplePadding.add(sampleBox);
		
		// Create an OK button that makes sure that 
		// sets the font on the main text area and
		// closes the font window.
		JButton OKButton = new JButton("   OK   ");
		OKButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				input.setFont(currentFont);
				fontDialog.setVisible(false);
			}
		});
		
		// Cancel button simply closes the window 
		JButton CancelButton = new JButton("Cancel");
		CancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fontDialog.setVisible(false);
			}
		});
		
		// Adding buttons to a panel.
		JPanel optionPanel = new JPanel();
		optionPanel.add(OKButton);
		optionPanel.add(CancelButton);
		
		// Padding the panel so it has the correct
		// position in the dialog.
		JPanel optionPadding = new JPanel();
		optionPadding.setBorder(new EmptyBorder(70, 220, 0, 0));
		optionPadding.add(optionPanel);
		
		// Adding everything to the main panel and then
		// Added that panel to the dialog.
		jpmain.add(fontList);	
		jpmain.add(styleList);	
		jpmain.add(sizeList);
		jpmain.add(samplePadding);
		jpmain.add(optionPadding);
		fontDialog.add(jpmain);
		fontDialog.setVisible(true);
		
		// Make sure the selected element shows at the top of the JList
		// This can only be done after the Dialog is visible 
		fontList.makeFirst(Arrays.asList(allFonts).indexOf(selectedFont));
		sizeList.makeFirst(Arrays.asList(allSizes).indexOf(selectedSize));
	}
	
	
	// Take integer style and turn it into
	// something that can be shown in the JList
	public String parseStyle(int style) {
		switch (style) {
			case 0: return "Plain";
			case 1: return "Bold";
			case 2: return "Italic";
			case 3: return "Bold Italic";
		}
		return "Error";
	}
	
	// Take something that's in the JList
	// and turn it into something that can
	// be applied to the font
	public int parseStyle(String style) {
		switch (style) {
			case "Plain": return 0;
			case "Bold" : return 1;
			case "Italic" : return 2;
			case "Bold Italic" : return 3;
		}
		return -1;
	}
	
	public String getSelectedFont() {
		return selectedFont;
	}
	
	public void setSelectedFont(String font) {
		selectedFont = font;
		currentFont = new Font(
			selectedFont,
			currentFont.getStyle(), 
			currentFont.getSize());
		sampleText.setFont(currentFont);
	}
	
	public void setSelectedStyle(String style) {
		selectedStyle = style;
		currentFont = currentFont.deriveFont(
			parseStyle(style));
		sampleText.setFont(currentFont);
	}
	
	public void setSelectedSize(String size) {
		selectedSize = size;
		currentFont = currentFont.deriveFont(
			Float.parseFloat(selectedSize));
		sampleText.setFont(currentFont);
	}
	
	
	// Panel that contains a search box + list box on top of
	// eachother, with functionality
	class ListPanel extends JPanel  {
		JTextField search;
		JList<String> listBox; 
		JScrollPane scroller;
		JLabel listName;
		FormatMenu format; 
		String[] listItems;
		boolean mouseSelected = false;
		
		public ListPanel(
			String name, String[] items, 
			String selected, FormatMenu format) 
		{
			setLayout(new BorderLayout());
			// Listbox with be added regular, search will bet NORTH
			listBox = new JList<String>(items);
			listName = new JLabel(name);
			this.format = format;
			this.listItems = items; 
			
			listBox.setFixedCellHeight(15);
			// Should start off having whatever is current Font, Style, Size
			// Etc, also, important, the dialog should start up with those 
			// selected in each JList as well. 
			search = new JTextField(selected); 
			
			JPanel searchPanel = new JPanel(new BorderLayout());
			searchPanel.add(listName, BorderLayout.NORTH);
			searchPanel.add(search);
			
			// Find selected in items and then set selected index to that. 
			listBox.setSelectedIndex(Arrays.asList(items).indexOf(selected));
			listBox.ensureIndexIsVisible(Arrays.asList(items).indexOf(selected));
			
			scroller = new JScrollPane(listBox);
			add(scroller);
			add(searchPanel, BorderLayout.NORTH);
		}
		
		/* 
		 * By default, ensureIndexIsVisible() makes the index the last
		 * column shown. Adding the (visibleRowCount - 1) pushes it upward
		 * by making the column 7 columns below it visible. 
		 */
		public void makeFirst(int index) {
			listBox.ensureIndexIsVisible(index + 
			       (listBox.getVisibleRowCount() - 1));
		}
		
		public JList<String> getList() {
			return listBox;
		}
		
		public JTextField getTextField() {
			return search;
		}
		
		public void selectFont(int index) {
			listBox.setSelectedIndex(index);
			format.setSelectedFont(listItems[index]);
		}
		
		public void selectSize(int index) {
			listBox.setSelectedIndex(index);
			listBox.ensureIndexIsVisible(index);
			format.setSelectedSize(listItems[index]);
		}
		
		public void selectStyle(int index) {
			listBox.setSelectedIndex(index);
			format.setSelectedStyle(listItems[index]);		
		}
		
		public String getItem(int index) {
			return listItems[index];
		}
		
		/* I use the jTextField for search and for 
		   storing the value that users selected. 
		 * The problem is that it would react like a search
		   whenever something is clicked (because that
		   would inevitably change the contents of the
		   textField). So I created mouseSelected to tell
		   the ListSearcher not to do anything when the
		   list item was selected by mouse */
		public void setMouseSelected(boolean condition) {
			mouseSelected = condition;
		}
		
		public boolean getMouseSelected() {
			return mouseSelected;
		}
	}
	
	class FontCellRenderer extends JLabel implements ListCellRenderer<Object> {
		Font listFont; 
		public Component getListCellRendererComponent (
			JList<?> list,        // the list
			Object value,         // value to display
			int index,            // cell index
			boolean isSelected,   // is the cell selected 
			boolean cellHasFocus) // does cell have focus  
		{
			listFont = list.getFont();
			String s = value.toString();
			setText(s);
			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}
			setEnabled(list.isEnabled());
			setFont(new Font(s, listFont.getStyle(), listFont.getSize()));

			setOpaque(true);
			return this;
		}
	}
	
	class StyleCellRenderer extends JLabel implements ListCellRenderer<Object> {
		public Font listFont; 
		public FormatMenu format;
		
		public StyleCellRenderer(FormatMenu format)  {
			this.format = format;
		}
		
		public Component getListCellRendererComponent (
			JList<?> list,        // the list
			Object value,         // value to display
			int index,            // cell index
			boolean isSelected,   // is the cell selected 
			boolean cellHasFocus) // does cell have focus  
		{
			listFont = list.getFont();
			String s = value.toString();
			setText(s);
			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}
			setEnabled(list.isEnabled());
			setFont(new Font(format.getSelectedFont(), format.parseStyle(s),
         			listFont.getSize()));
			setOpaque(true);
			return this;
		}
		
	}
	
	// Search box listener
	class ListSearcher implements DocumentListener {
		
		private ListPanel listPanel;
		private JList<String> list;
		private String listType;
		
		public ListSearcher(ListPanel listPanel, String listType) {
			this.listPanel = listPanel;
			this.listType = listType;
			list = listPanel.getList();
			
		}
		
		@Override 
		public void insertUpdate(DocumentEvent e) {
			if (!listPanel.getMouseSelected()) 
				searchList();
		}
		
		@Override
		public void removeUpdate(DocumentEvent e) {
			if (!listPanel.getMouseSelected())
				searchList();
		}
		
		@Override
		public void changedUpdate(DocumentEvent e) {
		}
		
		public void searchList() {
			String searchValue = listPanel.getTextField().getText();
			int result = findIndex(searchValue);
			listPanel.makeFirst(result);
			
			// Only select when the search value is exactly
			// equal to the element in the list
			if(searchValue.toLowerCase().equals(
				listPanel.getItem(result).toLowerCase()))
				setSelectedProperty(result);
		}
		
		/*
		select thing thing in the list
		set selected Thing to that */
		public void setSelectedProperty(int result) {
			if (listType.equals("Font")) {
				listPanel.selectFont(result);
			} else if (listType.equals("Size")) {
				listPanel.selectSize(result);
			}
		}
		
		public int findIndex(String searchValue) {
			int result;
			result = list.getNextMatch(
			    searchValue, 
				list.getFirstVisibleIndex(),
				Position.Bias.Forward);
			
			if (result != -1 ) {
				return result;
			} else {
				result = list.getNextMatch(
				    searchValue, 
					list.getFirstVisibleIndex(),
					Position.Bias.Backward);
				if (result != -1) {
					return result;
				} else {
					return 0;
				}
			}
		}
	}
	
	class PropertySelector implements ListSelectionListener {
		ListPanel listPanel;
		FormatMenu format;
		JList<String> list;
		JTextField textField;
		String propertyType;
		
		public PropertySelector(
			ListPanel listPanel, 
			FormatMenu format, String propertyType) 
		{
			this.listPanel = listPanel;
			this.format = format;
			this.propertyType = propertyType;
			list = listPanel.getList();
			textField = listPanel.getTextField();
		}
		
		public void valueChanged(ListSelectionEvent e) {
			// Get the selected item and put it in the text field
			// Also, set it to the sleectedProperty 
			// Using invokeLater to postpone execution of this code
			// until the lock on the textField is released. 
			Runnable selectCell = new Runnable() {
			
				@Override 
				public void run() {
					String selectedProperty = listPanel.getItem(
						list.getSelectedIndex());
					listPanel.setMouseSelected(true);
					textField.setText(selectedProperty);
			        listPanel.setMouseSelected(false);
					
					switch (propertyType) {
						case "Font": 
							format.setSelectedFont(selectedProperty);
							break;
						case "Style":
							format.setSelectedStyle(selectedProperty);
							break;
						case "Size":
							format.setSelectedSize(selectedProperty);
							break;
					}
				}
			};
			SwingUtilities.invokeLater(selectCell);
		}
		
	}
}