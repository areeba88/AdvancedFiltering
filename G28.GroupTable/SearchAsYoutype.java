/*
 * @(#)GroupableTableModelDemo.java 4/13/2007
 *
 * Copyright 2002 - 2007 JIDE Software Inc. All rights reserved.
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;


import com.jidesoft.combobox.PopupPanel;
import com.jidesoft.converter.ConverterContext;
import com.jidesoft.converter.ObjectConverter;
import com.jidesoft.converter.ObjectConverterManager;
import com.jidesoft.filter.AbstractFilter;
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
import com.jidesoft.swing.JideSplitPane;
import com.jidesoft.swing.JideSwingUtilities;
import com.jidesoft.swing.Searchable;
import com.jidesoft.swing.SearchableBar;
import com.jidesoft.swing.SearchableUtils;

/**
 * Demoed Component: {@link com.jidesoft.grid.JideTable} <br> Required jar files: jide-common.jar, jide-grids.jar <br>
 * Required L&F: any L&F
 */
public class SearchAsYoutype extends AbstractDemo {
	public GroupTable _table;
	public JLabel _message;
	protected TableModel _tableModel;
	private static DefaultGroupTableModel _groupTableModel;
	private boolean _useStyle;
	private String _lastDirectory = ".";
	private static final long serialVersionUID = 256315903870338341L;
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
	
	GroupTableHeader header =null;




	public SearchAsYoutype() {
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


		/*searchAsUType.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
			
				 header = new GroupTableHeader(_table);
				 
				if(e.getStateChange() == ItemEvent.SELECTED ){
					header.setAutoFilterEnabled(true);
					header.setUseNativeHeaderRenderer(false);
					header.setAcceptTextInput(true);
					



				}else {

					header.setAcceptTextInput(false);
					header.setUseNativeHeaderRenderer(true);


					//customizeQuickFiltering();

				}

			}
		});*/
		searchAsUType.addItemListener(new ItemListener() {
	            public void itemStateChanged(ItemEvent e) {
	              if(e.getStateChange() == ItemEvent.SELECTED ){
	               
	            	 
	            	  header.setAcceptTextInput(true);
	            	  header.setUseNativeHeaderRenderer(false);
	                 
	               
	            	  
	              }else {
	            	  
	            	  header.setAcceptTextInput(false);
	            	  header.setUseNativeHeaderRenderer(true);
	            	 
	            	  
	            	 // customizeQuickFiltering();
	                
	              }

	            }
	        });

		

		advanceFiltering.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED ){
					
					 header.setAutoFilterEnabled(true);
					 customizeQuickFiltering();
				}else {

					 header.setAutoFilterEnabled(false);

				}

			}
		});
	//	advanceFiltering.setSelected(header.isAutoFilterEnabled());
		
		
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
	
	protected void customizeAutoFilterBox(AutoFilterBox autoFilterBox)
	{



//	int colIndex = _groupTableModel.findColumn("DateTime");
		autoFilterBox.addDynamicTableFilter(new AbstractDynamicTableFilter()
		{
			@Override
			public String getName() {
				
				return "Test-Filter";
			}

			@Override
			public boolean initializeFilter(TableModel tableModel, int i, Object[] objects) {

				if(i==_groupTableModel.findColumn("DateTime")) {
					JOptionPane.showMessageDialog(SearchAsYoutype.this.getDemoPanel(), "Hello this is a Test");
					header.setAutoFilterEnabled(true);
			
					
					header.getFilterableTableModel().addFilter(0, new SingleValueFilter<String>("10:48:55.246 03-18-2012"));
					header.getFilterableTableModel().setFiltersApplied(true); 
				this.setName("Test-Filter");
					return true;
				}
				return false;
			}

		
			@Override
			public boolean isValueFiltered(Object value) {
				
				Date startHours = parseDate("10:48:55.246 03-18-2012");
			
				return !startHours.equals(value);

			}
		});
	} 		
	private JTableHeader customizeQuickFiltering(){
		header = new GroupTableHeader(_table){
	
			//////////////Renderer And Editor
		/*	@Override
			protected TableCellRenderer createDefaultRenderer() {
				if (isAutoFilterEnabled()) {
					return new AutoFilterTableHeaderRenderer() {
						@Override
						protected void customizeList(JList list) {
							super.customizeList(list);

						}

						@Override
						protected void customizeAutoFilterBox(AutoFilterBox autoFilterBox) {
							super.customizeAutoFilterBox(autoFilterBox);
							autoFilterBox.applyComponentOrientation(this.getComponentOrientation());
						}

					};
				}
				else {
					return super.createDefaultRenderer();
				}
			}*/
			 /* @Override
		        protected TableCellRenderer createDefaultRenderer() {
		            if (isAutoFilterEnabled()) {
		                return new AutoFilterTableHeaderRenderer() {
		                    @Override
		                    protected void customizeList(JList list) {
		                        super.customizeList(list);
		                    }

		                    @Override
		                    protected void customizeAutoFilterBox(AutoFilterBox autoFilterBox) {
		                        super.customizeAutoFilterBox(autoFilterBox);
		                       AdvancedFilteringOnCustomData.this.customizeAutoFilterBox(autoFilterBox);
		                        autoFilterBox.applyComponentOrientation(this.getComponentOrientation());
		                    }
		                };
		            }
		            else {
		                return super.createDefaultRenderer();
		            }
		        }*/
			@Override
			protected TableCellEditor createDefaultEditor() {

				if (isAutoFilterEnabled()) {
					return new AutoFilterTableHeaderEditor() {

						@Override
						protected AutoFilterBox createAutoFilterBox() {
							return new AutoFilterBox() {
								@Override
								protected void customizeList(JList list) {
									super.customizeList(list);
								}
							
								protected PopupPanel createPopupPanel(TableModel arg0, int arg1, Object[] arg2)
							      {
									PopupPanel  popUpPanel = super.createPopupPanel(arg0, arg1, arg2);
								
									JMenu subMenu2 = new JMenu("DateFilters");
									
															
						               // for(FilterFactory filter : FilterFactoryManager.getDefaultInstance().getFilterFactories(Date.class)) {
						                	
						                //	subMenu2.add(new JMenuItem(filter.getConditionString(Locale.getDefault())));
						               // }
						            subMenu2.add(new JMenuItem("WEL"));
						            subMenu2.add(new JMenuItem("WEL1"));
									
									popUpPanel.add(subMenu2, BorderLayout.AFTER_LAST_LINE);
						                
									
						                
						                
									return popUpPanel;
							      }
											
							
							};
						}

						@Override
						protected void customizeAutoFilterBox(AutoFilterBox autoFilterBox) {

							super.customizeAutoFilterBox(autoFilterBox);
							//this will remove the Custom Option that is generating the custom Filter GUI
							//autoFilterBox.setAllowCustomFilter(false);
							SearchAsYoutype.this.customizeAutoFilterBox(autoFilterBox);
						}
					};
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
			 header.setAutoFilterEnabled(true);
		     header.setUseNativeHeaderRenderer(true);
		     header.setAllowMultipleValues(true);	   	
	    return header;	
	    }
	    
	public Component getDemoPanel() {
		final JideSplitPane panel = new JideSplitPane(JideSplitPane.VERTICAL_SPLIT);

		//_tableModel = DemoData.createProductReportsTableModel(true, 0);

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
		// header =(GroupTableHeader) customizeQuickFiltering();
		 header =(GroupTableHeader) defaultQuickFiltering();

	       
			
		
		header.setGroupHeaderEnabled(true);
		
		//this flag will do not let our Custom Renderer to Work
		//header.setUseNativeHeaderRenderer(true);
	
		//header.setAllowMultipleValues(true);
		//header.setAutoFilterEnabled(true);
	

		
		
		
        
        
        
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
						int rowCount=SearchAsYoutype._groupTableModel.getRowCount();

						for (int i = 0; i < rowCount; i++) {

							SearchAsYoutype._groupTableModel.setValueAt(AdvancedFilteringOnCustomData.parseDate(dateData[i]), i, 8);
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

}
