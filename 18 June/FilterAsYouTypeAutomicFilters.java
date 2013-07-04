
/*
 * @(#)FilterAsYouTypeAutomicFilters.java 6/6/2013
 *
 * Copyright 2002 - 2013 JIDE Software Inc. All rights reserved.
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import com.jidesoft.converter.ConverterContext;
import com.jidesoft.converter.ObjectConverter;
import com.jidesoft.converter.ObjectConverterManager;
import com.jidesoft.filter.EqualFilter;
import com.jidesoft.filter.FilterFactoryManager;
import com.jidesoft.grid.CalculatedTableModel;
import com.jidesoft.grid.CellStyle;
import com.jidesoft.grid.DefaultGroupRow;
import com.jidesoft.grid.DefaultGroupTableModel;
import com.jidesoft.grid.FilterableTableModel;
import com.jidesoft.grid.GroupTable;
import com.jidesoft.grid.GroupTableHeader;
import com.jidesoft.grid.JideTable;
import com.jidesoft.grid.QuickTableFilterField;
import com.jidesoft.grid.Row;
import com.jidesoft.grid.SortableTableModel;
import com.jidesoft.grid.StyleModel;
import com.jidesoft.grid.TableModelWrapperUtils;
import com.jidesoft.grid.TreeTableUtils;
import com.jidesoft.plaf.LookAndFeelFactory;
import com.jidesoft.swing.JideSplitPane;


public class FilterAsYouTypeAutomicFilters   extends AbstractDemo {
	public GroupTable _table;
	public JLabel _message;
	protected TableModel _tableModel;
	public static  DefaultGroupTableModel _groupTableModel;
	private boolean _useStyle;
	private static final long serialVersionUID = 256315903870338341L;


	public GroupTableHeader header=null;
	public static String[] STUDENT_COLUMNS = new String[]{"ID", "FirstName",
		"LastName", "CGPA", "DateTime"};

	public static Object[][] STUDENTS = new Object[][]{

		new Object[]{"0", "Loark", "Barrosan",  3.54 },
		new Object[]{"0", "Lara", "Balacqua", 3.54 },

		new Object[]{"1", "Navail", "Longbottom", 3.54 },
		new Object[]{"1", "David", "Zooro", 3.54 },
		new Object[]{"1", "Navail", "Longbottom", 3.54 },
		new Object[]{"1", "Clark", "Millie", 3.54 },
		new Object[]{"3", "Hudson", "Hook",  3.77 },
		new Object[]{"3", "Billie", "Jazz",  3.51 },
		new Object[]{"3", "Rose", "Hangery", 3.51 },
		new Object[]{"3", "Hanna", "Longbottom",  3.52 },
		new Object[]{"2", "Smith", "Ouun",   3.52 },
		new Object[]{"2", "Roger", "Gazz",  3.77 },

		new Object[]{"2", "Boult", "Gum", 3.52 },
		new Object[]{"4", "Backham", "Roger", 3.52 },
		new Object[]{"4", "Flitch", "Bravoo",  3.54},
		new Object[]{"4", "River", "Zook",  3.54  },
		new Object[]{"4", "Clear", "Zook", 3.77 },
		new Object[]{"5", "Aurther", "Zook", 3.54,},
		new Object[]{"5", "Ron", "Zook", 3.54 },
		new Object[]{"5", "Ron", "Zook", 3.54 },
		new Object[]{"6", "Ron", "Zook",  3.54 },
		new Object[]{"6", "Ron", "Zook",  3.77 },
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


	public FilterAsYouTypeAutomicFilters() {
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
	public static int filterColumnId = -1;


	@Override
	public Component getDemoPanel() {
		final JideSplitPane panel = new JideSplitPane(JideSplitPane.VERTICAL_SPLIT);

		_tableModel = new DefaultTableModel(STUDENTS, STUDENT_COLUMNS) {
			@Override
			public Class<?> getColumnClass(int columnIndex) {
				if (columnIndex == 4) {
					return Date.class;
				}
				return super.getColumnClass(columnIndex);
			}
		}; 
		((DefaultTableModel)_tableModel).insertRow(0,new Object[]{});

		final CalculatedTableModel calculatedTableModel = setupProductDetailsCalculatedTableModel(_tableModel);
		sortModel = new SortableTableModel(calculatedTableModel); 
		filterableTableModel = new FilterableTableModel(sortModel){

			@Override

			public boolean isColumnFilterable(int colIndex) {
				if(findColumn("GroupedColumns")==colIndex){
					return false;
				}else
					return super.isColumnFilterable(colIndex);
			}  

			@Override
			public boolean isColumnAutoFilterable(int colIndex) {
				if(findColumn("GroupedColumns")==colIndex){
					return false;
				}else
					return super.isColumnAutoFilterable(colIndex);
			} 
		};

		_groupTableModel = new StyledGroupTableModel(filterableTableModel);




		_table = new GroupTable(_groupTableModel){

			@Override
			public boolean isCellEditable(int row, int col) {

				if(row==0)
				{
					if(col == 1 || col == 2 || col == 3 || col == 4 )
					{

						filterColumnId = col;		

					}

					return true;
				}
				return false;
			}


		};

		//Col#1
		JTextField filterFiledName=new JTextField(10);
		filterFiledName.setName("FirstName");

		TableColumn col1 =	_table.getColumnModel().getColumn(1);		
		col1.setCellEditor(new TextFiledCellEditor(filterFiledName));
		addListenerWithTxtField(filterFiledName,_groupTableModel);
		//Col#2
		JTextField filterFiledID=new JTextField(10);
		filterFiledID.setName("LastName");

		TableColumn col2 =	_table.getColumnModel().getColumn(2);		
		col2.setCellEditor(new TextFiledCellEditor(filterFiledID));

		addListenerWithTxtField(filterFiledID,_groupTableModel);
		//Col#3
		JTextField filterFiledCGPA=new JTextField(10);
		filterFiledCGPA.setName("CGPA"); 

		TableColumn col3 =	_table.getColumnModel().getColumn(3);		
		col3.setCellEditor(new TextFiledCellEditor(filterFiledCGPA));


		addListenerWithTxtField(filterFiledCGPA,_groupTableModel);


		//Col#4
		JTextField filterFiledDate=new JTextField(10);
		filterFiledDate.setName("DateTime");

		TableColumn col4 =	_table.getColumnModel().getColumn(4);		
		col4.setCellEditor(new TextFiledCellEditor(filterFiledDate));

		addListenerWithTxtField(filterFiledDate,_groupTableModel);



		/*if(filterColumnId ==1 )
		{
			JTextField filterFiledName=new JTextField(10);
			filterFiledName.setName("FirstName");
			addListenerWithTxtField(filterFiledName,_groupTableModel);



		}
		if(filterColumnId ==2 )
		{
			JTextField filterFiledID=new JTextField(10);
			filterFiledID.setName("LastName");
			addListenerWithTxtField(filterFiledID,_groupTableModel);
		}
		if(filterColumnId ==3 )
		{
			JTextField filterFiledCGPA=new JTextField(10);
			filterFiledCGPA.setName("CGPA"); 
			addListenerWithTxtField(filterFiledCGPA,_groupTableModel);

		}
		if(filterColumnId ==4 )
		{
			JTextField filterFiledDate=new JTextField(10);
			filterFiledDate.setName("DateTime");

			addListenerWithTxtField(filterFiledDate,_groupTableModel);
		}
		 */





		JScrollPane scrollPane = new JScrollPane(_table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		//	panel.add(filterFiledName, BorderLayout.BEFORE_FIRST_LINE);
		//	panel.add(filterFiledID, BorderLayout.BEFORE_FIRST_LINE);
		//	panel.add(filterFiledCGPA, BorderLayout.BEFORE_FIRST_LINE);

		panel.add(scrollPane);

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


		return panel;
	}


	class TextFiledCellEditor extends DefaultCellEditor {

		JTextField textField;

		public TextFiledCellEditor(JTextField textField) {
			super(textField);
			this.textField = textField;

			// TODO Auto-generated constructor stub
		}

		public Component getTableCellEditorComponent(JTable table, Object value,
				boolean isSelected,
				int row, int column) {
			Component com= null; 
			if (row == 0) {

				if(column ==1 ||
						column ==2 ||
						column ==3 ||
						column ==4 ||
						column ==0 )
				{
					com =  textField;
					//System.out.println(""+textField.getName());
					addListenerWithTxtField(textField,_groupTableModel);

				}

			}
			else 
				com =  null;


			return com;

		}
	}


	private void addListenerWithTxtField(JTextField txt,TableModel _tableModel){


		txt.addKeyListener(new QucikFilteringKeyListener(txt, _tableModel));



	}


	class QucikFilteringKeyListener extends KeyAdapter {

		TableModel _tableModel;

		JTextField txt;

		FilterableTableModel _filterableTableModel;
		public QucikFilteringKeyListener(JTextField txt,  TableModel _tableModel){


			this.txt = txt;
			this._tableModel = _tableModel;
			_filterableTableModel = (FilterableTableModel) TableModelWrapperUtils.getActualTableModel(this._tableModel, FilterableTableModel.class);

		}

		@Override
		public void keyReleased(KeyEvent e) {

			String txtString  =   txt.getText().substring(0, (txt.getText().length()-1));

			if(txtString!=null && e.getKeyChar() == KeyEvent.VK_BACK_SPACE){


				if(txt.getName().equals("FirstName") && _filterableTableModel.hasFilter(_groupTableModel.findColumn("FirstName"))){

					_filterableTableModel.removeAllFilters(_groupTableModel.findColumn("FirstName"));
					_filterableTableModel.setFiltersApplied(true);
				}
				else if(txt.getName().equals("LastName") && _filterableTableModel.hasFilter(_groupTableModel.findColumn("LastName"))){

					_filterableTableModel.removeAllFilters(_groupTableModel.findColumn("LastName"));
					_filterableTableModel.setFiltersApplied(true);
				}	
				else if(txt.getName().equals("CGPA") && _filterableTableModel.hasFilter(_groupTableModel.findColumn("CGPA"))){

					_filterableTableModel.removeAllFilters(_groupTableModel.findColumn("CGPA"));
					_filterableTableModel.setFiltersApplied(true);
				}

			}

			else if(txtString!=null && e.getKeyChar() == KeyEvent.VK_9/*KeyEvent.VK_ENTER*/){
				if(txt.getName().equals("FirstName"))
				{

					EqualFilter  equalFilter = new EqualFilter<String>(txtString) ;
					equalFilter.setFilterFactory(FilterFactoryManager.getDefaultInstance().findFilterFactoryByName(String.class, "is"));

					_filterableTableModel.addFilter(_groupTableModel.findColumn("FirstName"), equalFilter);
					_filterableTableModel.setFiltersApplied(true);

				}
				if(txt.getName().equals("LastName"))
				{
					EqualFilter  equalFilter = new EqualFilter<String>(txtString) ;
					equalFilter.setFilterFactory(FilterFactoryManager.getDefaultInstance().findFilterFactoryByName(String.class, "is"));

					_filterableTableModel.addFilter(_groupTableModel.findColumn("LastName"), equalFilter);
					_filterableTableModel.setFiltersApplied(true);

					/*EqualFilter  equalFilter = new EqualFilter<Integer>(txt.getText(), null) ;
					equalFilter.setFilterFactory(FilterFactoryManager.getDefaultInstance().findFilterFactoryByName(Integer.class, "is"));

					_filterableTableModel.addFilter(0, equalFilter);
					_filterableTableModel.setFiltersApplied(true);*/

				}
				if(txt.getName().equals("CGPA"))
				{

					EqualFilter  equalFilter = new EqualFilter<Float>() ;
					equalFilter.setFilterFactory(FilterFactoryManager.getDefaultInstance().findFilterFactoryByName(Float.class, "is"));
					float val= ((Float) Float.parseFloat(txtString));

					equalFilter.setValue(val);
					_filterableTableModel.addFilter(_groupTableModel.findColumn("CGPA"), equalFilter);
					_filterableTableModel.setFiltersApplied(true);

				}
			}

		}


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


				JFrame frame = new JFrame();

				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				JPanel panel = new JPanel(new BorderLayout());
				panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
				panel.add(new FilterAsYouTypeAutomicFilters().getDemoPanel());

				JCheckBox filterAsUType = new JCheckBox("Filter As u Type");
				panel.add(filterAsUType);
				filterAsUType.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						if(((JCheckBox)arg0.getSource()).isSelected()){

							System.out.println("Selected");	


						}else{
							System.out.println("NOt Selected");
						}	
					}
				});


				JButton btn = new JButton("Invoke Custom DateFilter");
				panel.add(btn);
				btn.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						int rowCount=FilterAsYouTypeAutomicFilters._groupTableModel.getRowCount();

						for (int i = 1; i < rowCount-1; i++) {

							FilterAsYouTypeAutomicFilters._groupTableModel.setValueAt(FilterAsYouTypeAutomicFilters.parseDate(dateData[i]), i, 4);
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
