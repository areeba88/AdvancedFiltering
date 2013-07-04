
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import com.jidesoft.converter.ConverterContext;
import com.jidesoft.converter.ObjectConverter;
import com.jidesoft.converter.ObjectConverterManager;
import com.jidesoft.grid.CalculatedTableModel;
import com.jidesoft.grid.CellStyle;
import com.jidesoft.grid.DefaultGroupRow;
import com.jidesoft.grid.DefaultGroupTableModel;
import com.jidesoft.grid.FilterableTableModel;
import com.jidesoft.grid.GroupTable;
import com.jidesoft.grid.GroupTableHeader;
import com.jidesoft.grid.JideTable;
import com.jidesoft.grid.QuickFilterPane;
import com.jidesoft.grid.QuickTableFilterField;
import com.jidesoft.grid.Row;
import com.jidesoft.grid.SortableTableModel;
import com.jidesoft.grid.StyleModel;
import com.jidesoft.grid.TreeTable;
import com.jidesoft.grid.TreeTableCellRenderer;
import com.jidesoft.grid.TreeTableUtils;
import com.jidesoft.plaf.LookAndFeelFactory;
import com.jidesoft.swing.JideSplitPane;
import com.jidesoft.swing.JideSwingUtilities;
import com.jidesoft.swing.JideTitledBorder;
import com.jidesoft.swing.PartialEtchedBorder;
import com.jidesoft.swing.PartialSide;


public class FilterAsYouType   extends AbstractDemo {
	public GroupTable _table;
	public JLabel _message;
	protected TableModel _tableModel;
	private DefaultGroupTableModel _groupTableModel;
	private boolean _useStyle;
	private static final long serialVersionUID = 256315903870338341L;


	public GroupTableHeader header=null;
	public static String[] STUDENT_COLUMNS = new String[]{" ID ", " First Name ",
		" Last Name ", " Age ", " Program ", " CGPA ", " Institute", "Skill", "DateTime"};

	public static Object[][] STUDENTS = new Object[][]{

		new Object[]{"0", "Loark", "Barrosan", "21", "COMPUTER ENGINEER", 3.54, "HOWARD UNIVERSITY", "PHP"},
		new Object[]{"0", "Lara", "Balacqua", "21", "COMPUTER ENGINEER", 3.54, "HOWARD UNIVERSITY", "PHP"},

		new Object[]{"1", "Navail", "Longbottom", "21", "COMPUTER ENGINEER", 3.54, "HOWARD UNIVERSITY", "PHP"},
		new Object[]{"1", "David", "Zooro", "21", "COMPUTER ENGINEER", 3.54, "HOWARD UNIVERSITY", "PHP"},
		new Object[]{"1", "Navail", "Longbottom", "20", "COMPUTER ENGINEER", 3.54, "HOWARD UNIVERSITY", "PHP"},
		new Object[]{"1", "Clark", "Millie", "19", "COMPUTER ENGINEER", 3.54, "HOWARD UNIVERSITY", "PHP"},
		new Object[]{"3", "Hudson", "Hook", "21", "COMPUTER ENGINEER", 3.77, "HOWARD UNIVERSITY", "JAVA,C++,.Net"},
		new Object[]{"3", "Billie", "Jazz", "21", "COMPUTER ENGINEER", 3.54, "HOWARD UNIVERSITY", "PHP"},
		new Object[]{"3", "Rose", "Hangery", "21", "COMPUTER ENGINEER", 3.54, "HOWARD UNIVERSITY", "PHP"},
		new Object[]{"3", "Hanna", "Longbottom", "20", "COMPUTER ENGINEER", 3.54, "HOWARD UNIVERSITY", "PHP"},
		new Object[]{"2", "Smith", "Ouun", "19", "COMPUTER ENGINEER", 3.54, "HOWARD UNIVERSITY", "PHP"},
		new Object[]{"2", "Roger", "Gazz", "21", "COMPUTER ENGINEER", 3.77, "HOWARD UNIVERSITY", "JAVA,C++,.Net"},

		new Object[]{"2", "Boult", "Gum", "21", "COMPUTER ENGINEER", 3.54, "HOWARD UNIVERSITY", "PHP"},
		new Object[]{"4", "Backham", "Roger", "21", "COMPUTER ENGINEER", 3.54, "HOWARD UNIVERSITY", "PHP"},
		new Object[]{"4", "Flitch", "Bravoo", "20", "COMPUTER ENGINEER", 3.54, "HOWARD UNIVERSITY", "PHP"},
		new Object[]{"4", "River", "Jack", "19", "COMPUTER ENGINEER", 3.54, "HOWARD UNIVERSITY", "PHP"},
		new Object[]{"4", "Clear", "Golmand", "21", "COMPUTER ENGINEER", 3.77, "HOWARD UNIVERSITY", "JAVA,C++,.Net"},
		new Object[]{"5", "Aurther", "Bond", "21", "COMPUTER ENGINEER", 3.54, "HOWARD UNIVERSITY", "PHP"},
		new Object[]{"5", "Flip", "LockHeart", "21", "COMPUTER ENGINEER", 3.54, "HOWARD UNIVERSITY", "PHP"},
		new Object[]{"5", "Boonty", "Ballias", "20", "COMPUTER ENGINEER", 3.54, "HOWARD UNIVERSITY", "PHP"},
		new Object[]{"6", "Ron", "Carter", "19", "COMPUTER ENGINEER", 3.54, "HOWARD UNIVERSITY", "PHP"},
		new Object[]{"6", "Fred", "Zook", "21", "COMPUTER ENGINEER", 3.77, "HOWARD UNIVERSITY", "JAVA,C++,.Net"},
	};

	public static String dateData[] = {
		"10:48:55.246 03-18-2012",
		"06:06:06.246 06-06-2006",

		"10:48:55.246 07-18-2012",
		"10:48:55.246 02-18-2012",
		"10:48:55.246 03-18-2012",
		"10:49:10.246 04-18-2012",
		"10:49:11.246 01-15-2012",
		"10:50:40.265 01-13-2012",
		"10:50:40.265 11-18-2012",
		"10:50:40.265 12-18-2012",
		"10:50:55.266 10-18-2012",
		"10:51:10.282 0-18-2012",

		"10:48:55.246 01-27-2013",
		"10:48:55.246 02-27-2013",
		"10:48:55.246 03-27-2013",
		"10:49:10.246 04-28-2013",
		"10:49:01.246 05-28-2013",
		"10:50:40.265 06-11-2012",
		"10:50:40.265 07-07-2012",
		"10:50:40.265 08-08-2012",
		"10:50:55.266 09-09-2012",
		"10:51:10.282 10-10-2012"
	};


	public FilterAsYouType() {
		ObjectConverterManager.initDefaultConverter();
		// instead of using the default toString method provided by DefaultGroupRow, we add our own
		// converter so that we can display the number of items.
		ObjectConverterManager.registerConverter(DefaultGroupRow.class, new ObjectConverter() {
			@Override
			public String toString(Object object, ConverterContext context) {
				if (object instanceof DefaultGroupRow) {
					DefaultGroupRow row = (DefaultGroupRow) object;
					StringBuffer buf = new StringBuffer(row.toString());
					int allVisibleChildrenCount = TreeTableUtils.getDescendantCount(_table.getModel(), row, true, true);
					buf.append(" (").append(allVisibleChildrenCount).append(" items)");
					return buf.toString();
				}
				return null;
			}
			@Override
			public boolean supportToString(Object object, ConverterContext context) {
				return true;
			}
			@Override
			public Object fromString(String string, ConverterContext context) {
				return null;
			}

			@Override
			public boolean supportFromString(String string, ConverterContext context) {
				return false;
			}
		});
	}

	@Override
	public int getAttributes() {
		return ATTRIBUTE_UPDATED;
	}

	@Override
	public String getName() {
		return "GroupTable Demo";
	}

	@Override
	public String getProduct() {
		return PRODUCT_NAME_GRIDS;
	}


	protected static final Color BACKGROUND1 = new Color(159, 155, 217);
	protected static final Color BACKGROUND2 = new Color(197, 194, 232);

	public static final CellStyle style1 = new CellStyle();
	public static final CellStyle style2 = new CellStyle();
	public static final CellStyle styleGroup1 = new CellStyle();
	public static final CellStyle styleGroup2 = new CellStyle();

	static {
		style1.setBackground(BACKGROUND1);
		style2.setBackground(BACKGROUND2);
		style1.setHorizontalAlignment(SwingConstants.CENTER);
		style2.setHorizontalAlignment(SwingConstants.CENTER);
		styleGroup1.setBackground(BACKGROUND1);
		styleGroup2.setBackground(BACKGROUND2);
		styleGroup1.setFontStyle(Font.BOLD);
		styleGroup2.setFontStyle(Font.BOLD);
	}
	JCheckBox keepColumnOrder =  new JCheckBox("Advance Filtering");
	@Override
	public Component getOptionsPanel() {
		JPanel checkBoxPanel = new JPanel(new GridLayout(0, 1, 0, 2));


		return checkBoxPanel;
	}
	FilterableTableModel filterableTableModel;
	SortableTableModel sortModel;
	QuickTableFilterField  _filterField1;
	JTextField txt;

	@Override
	public Component getDemoPanel() {
		final JideSplitPane panel = new JideSplitPane(JideSplitPane.VERTICAL_SPLIT);

		_tableModel = new DefaultTableModel(STUDENTS, STUDENT_COLUMNS) {
			@Override
			public Class<?> getColumnClass(int columnIndex) {
				if (columnIndex == 8) {
					return Date.class;
				}
				return super.getColumnClass(columnIndex);
			}
		}; 


		final CalculatedTableModel calculatedTableModel = setupProductDetailsCalculatedTableModel(_tableModel);
		sortModel = new SortableTableModel(calculatedTableModel); 
		filterableTableModel = new FilterableTableModel(sortModel);

		_groupTableModel = new StyledGroupTableModel(filterableTableModel);

		_table = new GroupTable(_groupTableModel) {
			@Override
			protected TableCellRenderer createCellRenderer() {
				return new TreeTableCellRenderer() {
					@Override
					protected Color getPaddingBackground(JTable table, int rowIndex, int columnIndex) {
						if (table instanceof TreeTable && !(((TreeTable) table).getRowAt(rowIndex) instanceof DefaultGroupRow) && !table.isCellSelected(rowIndex, columnIndex)) {
							return new Color(253, 253, 244);
						}
						return super.getPaddingBackground(table, rowIndex, columnIndex);
					}
				};
			}
		};
//////////////////////
		QuickFilterPane	_quickFilterPane = new QuickFilterPane(_groupTableModel, new int[]{3, 1, 2});

		 JPanel quickSearchPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		 QuickTableFilterField     _filterField = new QuickTableFilterField(_quickFilterPane.getDisplayTableModel(), new int[]{0,1,2});
	         quickSearchPanel.add(_filterField);
	        quickSearchPanel.setBorder(new JideTitledBorder(new PartialEtchedBorder(PartialEtchedBorder.LOWERED, PartialSide.NORTH), "QuickTableFilterField", JideTitledBorder.LEADING, JideTitledBorder.ABOVE_TOP));

	        JideSplitPane pane = new JideSplitPane(JideSplitPane.VERTICAL_SPLIT);
	        _quickFilterPane.setBorder(BorderFactory.createCompoundBorder(new JideTitledBorder(new PartialEtchedBorder(PartialEtchedBorder.LOWERED, PartialSide.NORTH), "QuickFilterPane", JideTitledBorder.LEADING, JideTitledBorder.ABOVE_TOP),
	                BorderFactory.createEmptyBorder(6, 0, 0, 0)));
	        pane.addPane(_quickFilterPane);

	        JPanel tablePanel = new JPanel(new BorderLayout(2, 2));
	        tablePanel.setBorder(BorderFactory.createCompoundBorder(new JideTitledBorder(new PartialEtchedBorder(PartialEtchedBorder.LOWERED, PartialSide.NORTH), "Filtered Song List", JideTitledBorder.LEADING, JideTitledBorder.ABOVE_TOP),
	                BorderFactory.createEmptyBorder(0, 0, 0, 0)));
	     
	     //   _groupTable = createTable(_filterField.getDisplayTableModel());
	             
	        _filterField.setTable(_table); // this is not necessary but with this call, the selection will be preserved when filter changes.

	        JScrollPane scrollPane = new JScrollPane(_table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

	         tablePanel.add(scrollPane);
	        pane.addPane(tablePanel);

	      //  JPanel panel = new JPanel(new BorderLayout(3, 3));
	        panel.add(quickSearchPanel, BorderLayout.BEFORE_FIRST_LINE);
	        panel.add(pane);

		//////////////////////


		//default Header 
		header = new GroupTableHeader(_table);

		header.setGroupHeaderEnabled(true);
		header.setAutoFilterEnabled(true);
		header.setUseNativeHeaderRenderer(true);
		header.setAllowMultipleValues(true);


		_table.setTableHeader(header);


		_groupTableModel.setDisplaySeparateGroupColumn(true); 


		_table.setAutoResizeMode(JideTable.AUTO_RESIZE_FILL);
		_table.setOptimized(true);

		// hide the grid lines is good for performance
		_table.setShowLeafNodeTreeLines(false);
		_table.setShowTreeLines(false);
		_table.setExportCollapsedRowsToExcel(true);

		_table.expandAll();



	//	panel.add(JideSwingUtilities.createLabeledComponent(new JLabel("Right click on the table header to see more options"),
		//		new JScrollPane(_table), BorderLayout.BEFORE_FIRST_LINE));

		/*QuickFilterPane	 _quickFilterPane = new QuickFilterPane(_groupTableModel, new int[]{0,1,2,3,4});

		JPanel quickSearchPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		_filterField1 = new QuickTableFilterField(_quickFilterPane.getDisplayTableModel());
_filterField1.setTable(_table);
		//filterableTableModel.setFiltersApplied(true);
		txt=new JTextField(10);

		quickSearchPanel.add(_filterField1);
		quickSearchPanel.add(txt);

		txt.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent eTyped) {

			}

			@Override
			public void keyReleased(KeyEvent eRelease) {
			//	_filterField1.setSearchingText(txt.getText());
			//	_filterField1.applyFilter("Jazz");
			//	filterableTableModel.setFiltersApplied(true);


			}

			@Override
			public void keyPressed(KeyEvent ePressed) {


			}
		}); 
*/

	//	panel.add(quickSearchPanel, BorderLayout.LINE_END);


		return panel;
	}

	@Override
	public String getDemoFolder() {
		return "G28.GroupableTableModel";
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				LookAndFeelFactory.installDefaultLookAndFeelAndExtension();
				showAsFrame(new FilterAsYouType());
			}
		});

	} 




	private CalculatedTableModel setupProductDetailsCalculatedTableModel(TableModel tableModel) {
		CalculatedTableModel calculatedTableModel = new CalculatedTableModel(tableModel);
		calculatedTableModel.addAllColumns();

		return calculatedTableModel;
	}

	private class StyledGroupTableModel extends DefaultGroupTableModel implements StyleModel {
		private static final long serialVersionUID = 4936234855874300579L;

		public StyledGroupTableModel(TableModel tableModel) {
			super(tableModel);
		}

		@Override
		public CellStyle getCellStyleAt(int rowIndex, int columnIndex) {
			if (_useStyle && hasGroupColumns()) {
				Row row = getRowAt(rowIndex);
				boolean topLevel = false;
				if (row instanceof DefaultGroupRow) {
					topLevel = true;
				}
				while (!(row instanceof DefaultGroupRow)) {
					row = (Row) row.getParent();
				}
				if (getOriginalRows().indexOf(row) % 2 == 0) {
					return topLevel ? styleGroup1 : style1;
				}
				else {
					return topLevel ? styleGroup2 : style2;
				}
			}
			else {
				return null;
			}
		}

		@Override
		public boolean isCellStyleOn() {
			return _useStyle;
		}
	}



}
