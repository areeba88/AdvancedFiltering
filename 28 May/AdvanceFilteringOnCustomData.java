/*
 * @(#)GroupableTableModelDemo.java 4/13/2007
 *
 * Copyright 2002 - 2007 JIDE Software Inc. All rights reserved.
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;


import com.jidesoft.combobox.CheckBoxListChooserPanel;
import com.jidesoft.combobox.PopupPanel;
import com.jidesoft.converter.ConverterContext;
import com.jidesoft.converter.ObjectConverter;
import com.jidesoft.converter.ObjectConverterManager;
import com.jidesoft.dialog.StandardDialog;
import com.jidesoft.filter.AbstractFilter;
import com.jidesoft.filter.CustomFilterEditor;
import com.jidesoft.filter.Filter;
import com.jidesoft.filter.FilterFactory;
import com.jidesoft.filter.FilterFactoryManager;
import com.jidesoft.filter.WildcardFilter;
import com.jidesoft.grid.AbstractDynamicTableFilter;
import com.jidesoft.grid.AutoFilterBox;
import com.jidesoft.grid.AutoFilterTableHeader;
import com.jidesoft.grid.AutoFilterTableHeaderEditor;
import com.jidesoft.grid.AutoFilterTableHeaderRenderer;
import com.jidesoft.grid.AutoResizePopupMenuCustomizer;
import com.jidesoft.grid.CalculatedTableModel;
import com.jidesoft.grid.CellStyle;
import com.jidesoft.grid.DefaultGroupRow;
import com.jidesoft.grid.DefaultGroupTableModel;
import com.jidesoft.grid.FilterableTableModel;
import com.jidesoft.grid.GroupTable;
import com.jidesoft.grid.GroupTableHeader;
import com.jidesoft.grid.GroupTablePopupMenuCustomizer;
import com.jidesoft.grid.IFilterableTableModel;
import com.jidesoft.grid.JideTable;
import com.jidesoft.grid.Row;
import com.jidesoft.grid.SelectTablePopupMenuCustomizer;
import com.jidesoft.grid.SingleValueFilter;
import com.jidesoft.grid.StyleModel;
import com.jidesoft.grid.TableColumnChooserPopupMenuCustomizer;
import com.jidesoft.grid.TableHeaderPopupMenuInstaller;
import com.jidesoft.grid.TableModelWrapperUtils;
import com.jidesoft.grid.TableUtils;
import com.jidesoft.grid.TreeTable;
import com.jidesoft.grid.TreeTableCellRenderer;
import com.jidesoft.grid.TreeTableUtils;
import com.jidesoft.icons.IconsFactory;
import com.jidesoft.plaf.LookAndFeelFactory;
import com.jidesoft.popup.JidePopup;
import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JideMenu;
import com.jidesoft.swing.JidePopupMenu;
import com.jidesoft.swing.JideSplitPane;
import com.jidesoft.swing.JideSwingUtilities;
import com.jidesoft.swing.Searchable;
import com.jidesoft.swing.SearchableBar;
import com.jidesoft.swing.SearchableUtils;

/**
 * Demoed Component: {@link com.jidesoft.grid.JideTable} <br> Required jar files: jide-common.jar, jide-grids.jar <br>
 * Required L&F: any L&F
 */
public class AdvancedFilteringOnCustomData extends AbstractDemo {
	public GroupTable _table;
	public JLabel _message;
	protected TableModel _tableModel;
	private static DefaultGroupTableModel _groupTableModel;
	private boolean _useStyle;
	private String _lastDirectory = ".";
	private static final long serialVersionUID = 256315903870338341L;


	Vector filterFactories=new Vector<String>();

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
		"10:49:11.246 07-18-2012",
		"10:50:40.265 07-18-2012",
		"10:50:40.265 11-18-2012",
		"10:50:40.265 12-18-2012",
		"10:50:55.266 10-18-2012",
		"10:51:10.282 07-18-2012",

		"10:48:55.246 06-16-2012",
		"10:48:55.246 05-17-2012",
		"10:48:55.246 10-12-2012",
		"10:49:10.246 10-12-2012",
		"10:49:01.246 10-11-2012",
		"10:50:40.265 10-11-2012",
		"10:50:40.265 10-07-2012",
		"10:50:40.265 10-08-2012",
		"10:50:55.266 10-09-2012",
		"10:51:10.282 10-10-2012"
	};

	JCheckBox searchAsUType =  new JCheckBox("Search As you Type");

	JCheckBox advanceFiltering =  new JCheckBox("Advance Filtering");

	Vector<String>  stringTypeFilters =new  Vector <String>();
	
	GroupTableHeader header =null;

	AdvancedFilteringOnCustomData oAdvancedFilteringOnCustomData;
	public AdvancedFilteringOnCustomData(AdvancedFilteringOnCustomData oAdvancedFilteringOnCustomData){

		this.oAdvancedFilteringOnCustomData = oAdvancedFilteringOnCustomData;	
	}


	public AdvancedFilteringOnCustomData() {
		ObjectConverterManager.initDefaultConverter();
		// instead of using the default toString method provided by DefaultGroupRow, we add our own
		// converter so that we can display the number of items.
		ObjectConverterManager.registerConverter(DefaultGroupRow.class, new ObjectConverter() {
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

			public boolean supportToString(Object object, ConverterContext context) {
				return true;
			}

			public Object fromString(String string, ConverterContext context) {
				return null;
			}

			public boolean supportFromString(String string, ConverterContext context) {
				return false;
			}
		});
		ObjectConverterManager.registerConverter(Date.class, new ObjectConverter() {
			public String toString(Object object, ConverterContext context) {
				if (object instanceof Date) {
					String timeDateFormat = "HH:mm:ss.SSS MM-dd-yyyy";
					DateFormat format = new SimpleDateFormat(timeDateFormat);
					return format.format(object);
				}
				return null;
			}

			public boolean supportToString(Object object, ConverterContext context) {
				return true;
			}

			public Object fromString(String string, ConverterContext context) {
				return null;
			}

			public boolean supportFromString(String string, ConverterContext context) {
				return false;
			}
		});
		//   ObjectGrouperManager.registerGrouper(Date.class, new DateHourGrouper());
		//    ObjectGrouperManager.registerGrouper(Date.class, new DateMonthGrouper());

	}

	@Override
	public int getAttributes() {
		return ATTRIBUTE_UPDATED;
	}

	public String getName() {
		return "GroupTable Demo";
	}

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



	public static Date parseDate(String time) {
		String timeDateFormat = "HH:mm:ss.SSS MM-dd-yyyy";
		Date date = null;
		String jk=null;
		try {

			DateFormat format = new SimpleDateFormat(timeDateFormat);
			date = format.parse(time);

		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return date;

	}

	@Override
	public Component getOptionsPanel() {
		JPanel checkBoxPanel = new JPanel(new GridLayout(0, 1, 0, 2));


		searchAsUType.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED ){


					header.setAcceptTextInput(true);
					header.setUseNativeHeaderRenderer(false);



				}else {

					header.setAcceptTextInput(false);
					header.setUseNativeHeaderRenderer(true);

				}

			}
		});

		advanceFiltering.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED ){

					header.setAutoFilterEnabled(true);
					// customizeQuickFiltering();
				}else {

					header.setAutoFilterEnabled(false);

				}

			}
		});
		advanceFiltering.setSelected(header.isAutoFilterEnabled());


		checkBoxPanel.add(advanceFiltering);

		checkBoxPanel.add(searchAsUType);


		return checkBoxPanel;
	}
	public static Date parseDate(String s, String dateFormat) {
		final SimpleDateFormat dateformat = new SimpleDateFormat(dateFormat);
		Date parsedDate = null;
		try {
			parsedDate = dateformat.parse(s);
		} catch (ParseException e) {		
			e.printStackTrace();
		}
		if (parsedDate == null) {
			parsedDate = new Date(0);
		}
		return parsedDate;
	}

	
		private void addDynamicFilter(){
			JMenuItem item;
			
		/*	for(FilterFactory filter : FilterFactoryManager.getDefaultInstance().getFilterFactories(Date.class)) {
				item = new JMenuItem(filter.getConditionString(Locale.getDefault()));
				//System.out.println("Non String "+item.getActionCommand());
				if(item.getActionCommand().startsWith("on Jan")||  //all theses Filters were belonging to the Date Type Month's Category so are being collected in one menu  
						item.getActionCommand().startsWith("on Feb")||
						item.getActionCommand().startsWith("on March")||
						item.getActionCommand().startsWith("on Ap")||
						item.getActionCommand().startsWith("on May")||
						item.getActionCommand().startsWith("on Jun")||
						item.getActionCommand().startsWith("on Jul")||    for testing
						item.getActionCommand().startsWith("on Aug")||
						item.getActionCommand().startsWith("on Sep")||
						item.getActionCommand().startsWith("on Oct")||
						item.getActionCommand().startsWith("on Nov")||
						item.getActionCommand().startsWith("on Dec")){
					
					monthMenu.add(item);
								
				}else if(!(item.getActionCommand().equals("is at anytime")||  //all these Filters were supporting the String Data Type so they are being removed
						item.getActionCommand().equals("is on")||
						item.getActionCommand().equals("is in")||
						item.getActionCommand().equals("isn't in")||
						item.getActionCommand().equals("doesn't exist")||
						item.getActionCommand().equals("exist")||
						item.getActionCommand().equals("is after")||
						item.getActionCommand().equals("is on or after")||
						item.getActionCommand().equals("is before")||
						item.getActionCommand().equals("is on or before")||
						item.getActionCommand().equals("is between")||
						item.getActionCommand().equals("doesn't equal")||
						item.getActionCommand().equals("is not between")
						)||
						item.getActionCommand().startsWith("on Jul"))
						
				{
					
					dateFilteringMenu.add(item);
				}		
				item.addActionListener(new FilterActionListener(this.oAdvancedFilteringOnCustomData,item));
				
			}*/
			
		}
	
	
	private JTableHeader customizeQuickFiltering(){
		header = new GroupTableHeader(_table){

			//////////////Renderer And Editor
		
			@Override
			protected TableCellEditor createDefaultEditor() {

				if (isAutoFilterEnabled()) {
					
					return new CustomizedAutoFilterTableHeaderEditor();
					
				}
				else {
					return null;
				}
			}


		};

		return header;
	}

	private JTableHeader defaultQuickFiltering(){

		header = new GroupTableHeader(_table);
		// header.setAutoFilterEnabled(true);
		//   header.setUseNativeHeaderRenderer(true);
		header.setAllowMultipleValues(true);	   	
		return header;	
	}

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
		FilterableTableModel filterableTableModel = new FilterableTableModel(calculatedTableModel);

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
		FilterFactoryManager	  _filterManager = new FilterFactoryManager();
		_filterManager.registerDefaultFilterFactories();

		//default Header 
		header =(GroupTableHeader) customizeQuickFiltering();
		// header =(GroupTableHeader) defaultQuickFiltering();




		header.setGroupHeaderEnabled(true);

		//this flag will do not let our Custom Renderer to Work
		//header.setUseNativeHeaderRenderer(true);



		_table.setTableHeader(header);
		_table.setSpecialColumnsHidable(false);
		//	setFilterFactories();
		_groupTableModel.setDisplaySeparateGroupColumn(true);
		_groupTableModel.groupAndRefresh();
		TableUtils.saveColumnOrders(_table, false);
		_table.expandAll();



		TableHeaderPopupMenuInstaller installer = new TableHeaderPopupMenuInstaller(_table) {
			@Override
			protected void customizeMenuItems(final JTableHeader header, final JPopupMenu popup, final int clickingColumn) {
				super.customizeMenuItems(header, popup, clickingColumn);

				addSeparatorIfNecessary(popup);

			}
		};

		installer.addTableHeaderPopupMenuCustomizer(new AutoResizePopupMenuCustomizer());
		installer.addTableHeaderPopupMenuCustomizer(new GroupTablePopupMenuCustomizer());
		installer.addTableHeaderPopupMenuCustomizer(new TableColumnChooserPopupMenuCustomizer());
		installer.addTableHeaderPopupMenuCustomizer(new SelectTablePopupMenuCustomizer());

		_table.setExpandedIcon(IconsFactory.getImageIcon(AdvancedFilteringOnCustomData.class, "icons/outlook_collapse.png"));
		_table.setCollapsedIcon(IconsFactory.getImageIcon(AdvancedFilteringOnCustomData.class, "icons/outlook_expand.png"));

		_table.setAutoResizeMode(JideTable.AUTO_RESIZE_FILL);
		_table.setOptimized(true);

		// hide the grid lines is good for performance
		_table.setShowLeafNodeTreeLines(false);
		_table.setShowTreeLines(false);
		_table.setExportCollapsedRowsToExcel(true);

		_table.expandAll();



		panel.add(JideSwingUtilities.createLabeledComponent(new JLabel("Right click on the table header to see more options"),
				new JScrollPane(_table), BorderLayout.BEFORE_FIRST_LINE));

		panel.add(getOptionsPanel());
		return panel;
	}

	/*public void setFilterFactories(){
		// begins with
				FilterFactoryManager.getDefaultInstance().registerFilterFactory(ChargeDefinition.class, new FilterFactory() {

					public Filter<String> createFilter(Object... objects) {
						String input = ObjectConverterManager.toString(objects[0], ChargeDefinition.class);
						WildcardFilter<String> beginWith = new WildcardFilter<String>(input);
						beginWith.setBeginWith(true);
						beginWith.setEndWith(false);
						return beginWith;
					}

					public String getConditionString(Locale locale) {
						return AbstractFilter.getConditionString(locale, "string", "beginWith");
					}

					public Class[] getExpectedDataTypes() {
						return new Class[] { String.class };
					}

					public String getName() {
						return "beginWith@@@@@@";
					}
				});

	}*/

	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				LookAndFeelFactory.installDefaultLookAndFeelAndExtension();
				//	UIManager.getDefaults().put("TableHeader.autoFilterTableHeaderUIDelegate", CustomAutoFilterTableHeaderUIDelegate.class.getName());
				JFrame frame = new JFrame();

				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				JPanel panel = new JPanel(new BorderLayout());
				panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
				panel.add(new AdvancedFilteringOnCustomData().getDemoPanel());

				JButton btn = new JButton("Invoke Custom DateFilter");
				panel.add(btn);
				btn.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						int rowCount=AdvancedFilteringOnCustomData._groupTableModel.getRowCount();

						for (int i = 0; i < rowCount; i++) {

							AdvancedFilteringOnCustomData._groupTableModel.setValueAt(AdvancedFilteringOnCustomData.parseDate(dateData[i]), i, 8);
						}

					}

				});

				frame.add(panel);
				frame.setPreferredSize(new Dimension(800, 600));
				frame.pack();
				frame.setVisible(true);


			}
		});


	}



	private CalculatedTableModel setupProductDetailsCalculatedTableModel(TableModel tableModel) {
		CalculatedTableModel calculatedTableModel = new CalculatedTableModel(tableModel);
		calculatedTableModel.addAllColumns();
		return calculatedTableModel;
	}

	private class StyledGroupTableModel extends DefaultGroupTableModel implements StyleModel  {
		private static final long serialVersionUID = 4936234855874300579L;

		public StyledGroupTableModel(TableModel tableModel) {
			super(tableModel);
		}
		@Override
		public Class<?> getColumnClass(int columnIndex) {

			int actualColumnIndex = TableModelWrapperUtils.getActualColumnAt(this, columnIndex);

			switch (actualColumnIndex) {
			case 0:
				return Integer.class;

			case 1:
				return String.class;

			case 2:
				return String.class;

			case 3:
				return Integer.class;

			case 4:
				return String.class;

			case 5:
				return Float.class;
			case 6:
				return String.class;

			case 7:
				return String.class;
			case 8:
				return Date.class;

			}
			if (columnIndex < 0) {
				throw new IllegalArgumentException("Incorrect column index");
			}
			return super.getColumnClass(columnIndex);
		}


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

		public boolean isCellStyleOn() {
			return _useStyle;
		}
	}
	
	
	protected void customizeAutoFilterBox(AutoFilterBox autoFilterBox)
	{
			int colIndex = _groupTableModel.findColumn("DateTime");
	
			if(autoFilterBox.getModelColumnIndex()==colIndex){
				//(2)
				autoFilterBox.addDynamicTableFilter(new DynamicDateFilter(this));
				  
			}
	} 	

class CustomizedAutoFilterTableHeaderEditor extends AutoFilterTableHeaderEditor{
		
		@Override
		protected AutoFilterBox createAutoFilterBox() {
			return new CustomizedAutoFilterBox();
		}

		/*@Override	//(1)
		protected void customizeAutoFilterBox(AutoFilterBox autoFilterBox) {

			super.customizeAutoFilterBox(autoFilterBox);
			//this will remove the Custom Option that is generating the custom Filter GUI
			//autoFilterBox.setAllowCustomFilter(false);
			AdvancedFilteringOnCustomData.this.customizeAutoFilterBox(autoFilterBox);
			
			
			
		}*/
		
		
		
	}


class CustomizeJMenuItem implements MenuElement{

	@Override
	public Component getComponent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MenuElement[] getSubElements() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void menuSelectionChanged(boolean isIncluded) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processKeyEvent(KeyEvent event, MenuElement[] path,
			MenuSelectionManager manager) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processMouseEvent(MouseEvent event, MenuElement[] path,
			MenuSelectionManager manager) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	
}
	class DynamicDateFilter  extends AbstractDynamicTableFilter{
		
		AdvancedFilteringOnCustomData oAdvancedFilteringOnCustomData;
		public DynamicDateFilter(AdvancedFilteringOnCustomData oAdvancedFilteringOnCustomData){
			
			this.oAdvancedFilteringOnCustomData = oAdvancedFilteringOnCustomData;	
		
			}
 			
				@Override
				public String getName() {

					return "Test-Filter";
				}

				@Override
				public boolean initializeFilter(TableModel tableModel, int i, Object[] objects) {

					if(i==_groupTableModel.findColumn("DateTime")) {
				
						JPopupMenu dateFilteringMenu = new JPopupMenu("DateFilters");
						JMenu monthMenu =new  JMenu("is in Month of");

						JMenuItem item ;
						for(FilterFactory filter : FilterFactoryManager.getDefaultInstance().getFilterFactories(Date.class)) {
							item = new JMenuItem(filter.getConditionString(Locale.getDefault()));
							//System.out.println("Non String "+item.getActionCommand());
							if(item.getActionCommand().startsWith("on Jan")||  //all theses Filters were belonging to the Date Type Month's Category so are being collected in one menu  
									item.getActionCommand().startsWith("on Feb")||
									item.getActionCommand().startsWith("on March")||
									item.getActionCommand().startsWith("on Ap")||
									item.getActionCommand().startsWith("on May")||
									item.getActionCommand().startsWith("on Jun")||
									/*item.getActionCommand().startsWith("on Jul")||    for testing*/
									item.getActionCommand().startsWith("on Aug")||
									item.getActionCommand().startsWith("on Sep")||
									item.getActionCommand().startsWith("on Oct")||
									item.getActionCommand().startsWith("on Nov")||
									item.getActionCommand().startsWith("on Dec")){
								
								monthMenu.add(item);
											
							}else if(!(item.getActionCommand().equals("is at anytime")||  //all these Filters were supporting the String Data Type so they are being removed
									item.getActionCommand().equals("is on")||
									item.getActionCommand().equals("is in")||
									item.getActionCommand().equals("isn't in")||
									item.getActionCommand().equals("doesn't exist")||
									item.getActionCommand().equals("exist")||
									item.getActionCommand().equals("is after")||
									item.getActionCommand().equals("is on or after")||
									item.getActionCommand().equals("is before")||
									item.getActionCommand().equals("is on or before")||
									item.getActionCommand().equals("is between")||
									item.getActionCommand().equals("doesn't equal")||
									item.getActionCommand().equals("is not between")
									)||
									item.getActionCommand().startsWith("on Jul"))
									
							{
								
								dateFilteringMenu.add(item);
							}		
							item.addActionListener(new FilterActionListener(this.oAdvancedFilteringOnCustomData,item));
							
						}
						dateFilteringMenu.add(monthMenu);
						
						
						

						dateFilteringMenu.setLocation(660,100);
						dateFilteringMenu.setVisible(true);




						//	header.setAutoFilterEnabled(true);


						//	header.getFilterableTableModel().addFilter(0, new SingleValueFilter<String>("10:48:55.246 03-18-2012"));
						//	header.getFilterableTableModel().addFilter(0, new SingleValueFilter<String>("on June"));


						header.getFilterableTableModel().setFiltersApplied(true); 

						return true;
					}
					return false;
				}


				@Override
				public boolean isValueFiltered(Object value) {
					//System.out.println(""+((Date)value).getMonth());

					if(((Date)value).getMonth() != 6)//On July 
						return true;
					else
						return false;

					//Date startHours = parseDate("10:48:55.246 03-18-2012");

					//				return !startHours.equals(value);

				}
			}
	
	class CustomizedAutoFilterBox extends AutoFilterBox{
		
		
		@Override
		protected void customizeList(JList list) {
			super.customizeList(list);
		}
		
		@Override
		protected PopupPanel createPopupPanel(TableModel tableModel, int columnIndex, Object[] possibleValues)
		{
			PopupPanel  popUpPanel = super.createPopupPanel(tableModel, columnIndex, possibleValues);
			
		/*JMenuBar menuBar=new JMenuBar();
		
		JideMenu subMenu2 =  new JideMenu("DateFilters");
		JMenuItem	item = new JMenuItem("WEL") ;
		JMenuItem	item2 = new JMenuItem("WEL2") ;
		JMenuItem	item3 = new JMenuItem("WEL3") ;
		JMenuItem	item4 = new JMenuItem("WEL4") ;
		JMenuItem	item5 = new JMenuItem("WEL5") ;
		JMenuItem	item6 = new JMenuItem("WEL6") ;
		JMenuItem	item7 = new JMenuItem("WEL7") ;
		JMenuItem	item8 = new JMenuItem("WEL8") ;
		
		
			subMenu2.add(item);
			subMenu2.add(item2);
			subMenu2.add(item3);
			subMenu2.add(item4);
			subMenu2.add(item5);
			subMenu2.add(item6);
			subMenu2.add(item7);
			subMenu2.add(item8);
			subMenu2.add(item);
		
			item.addActionListener(new FilterActionListener22(item));
			item2.addActionListener(new FilterActionListener22(item2));
			item3.addActionListener(new FilterActionListener22(item3));
			item4.addActionListener(new FilterActionListener22(item4));
			item5.addActionListener(new FilterActionListener22(item5));
			item6.addActionListener(new FilterActionListener22(item6));
			item7.addActionListener(new FilterActionListener22(item7));
			item8.addActionListener(new FilterActionListener22(item8));
			
			
			
			subMenu2.setAutoscrolls(true) ;
			//subMenu2.setEnabled(true);
			//subMenu2.setHideActionText(false);
			subMenu2.setPopupMenuVisible(true);
			subMenu2.setVisible(true);
			
			menuBar.add(subMenu2,BorderLayout.NORTH);
			popUpPanel.add(menuBar,BorderLayout.AFTER_LAST_LINE);
			
			*/
			
			
			AddingJMenuToJPanel menu =new AddingJMenuToJPanel("DateFilters");
			JMenuItem	item = new JMenuItem("WEL") ;
			JMenuItem	item2 = new JMenuItem("WEL2") ;
			JMenuItem	item3 = new JMenuItem("WEL3") ;
			JMenuItem	item4 = new JMenuItem("WEL4") ;
			JMenuItem	item5 = new JMenuItem("WEL5") ;
			JMenuItem	item6 = new JMenuItem("WEL6") ;
			JMenuItem	item7 = new JMenuItem("WEL7") ;
			JMenuItem	item8 = new JMenuItem("WEL8") ;
			
			menu.add(item);
			menu.add(item2);
			menu.add(item3);
			menu.add(item4);
			menu.add(item5);
			menu.add(item6);
			menu.add(item7);
			menu.add(item8);
			menu.add(item);
			
			//menu.add(subMenu2,BorderLayout.NORTH);
			popUpPanel.add(menu,BorderLayout.AFTER_LAST_LINE);
		
			
		
			return popUpPanel;
		}
			
	}
	class FilterActionListener22 implements ActionListener
	{
		JMenuItem item;
		
		public FilterActionListener22(){}

		public FilterActionListener22( JMenuItem item)
		{
			this.item=item;
			
		}


		@Override
		public void actionPerformed(ActionEvent e) 
		{

			String strCommand = e.getActionCommand();

			JOptionPane.showMessageDialog(null, ""+strCommand);

			

			}
		}

	}
	
	class FilterActionListener implements ActionListener
	{
		JMenuItem item;
		AdvancedFilteringOnCustomData oAdvancedFilteringOnCustomData;
		public FilterActionListener(){}

		public FilterActionListener(AdvancedFilteringOnCustomData oAdvancedFilteringOnCustomData , JMenuItem item)
		{
			this.item=item;
			this.oAdvancedFilteringOnCustomData = oAdvancedFilteringOnCustomData;
		}


		@Override
		public void actionPerformed(ActionEvent e) 
		{

			String strCommand = e.getActionCommand();

			for(int i=0; i<oAdvancedFilteringOnCustomData.filterFactories.size();i++){
				if(e.getActionCommand().equals(oAdvancedFilteringOnCustomData.filterFactories.get(i)))//June
				{
					JOptionPane.showMessageDialog(null, "yahhh");

				}

			}
		}

	}	
	

