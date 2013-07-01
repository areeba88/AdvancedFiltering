
	import com.jidesoft.converter.*;
	import com.jidesoft.filter.FilterFactory;
	import com.jidesoft.filter.FilterFactoryManager;
	import com.jidesoft.grid.*;
	import com.jidesoft.grouper.date.DateMonthGrouper;
	import com.jidesoft.grouper.date.DateQuarterGrouper;
	import com.jidesoft.grouper.date.DateWeekOfYearGrouper;
	import com.jidesoft.grouper.date.DateYearGrouper;
	import com.jidesoft.hssf.HssfTableUtils;
	import com.jidesoft.icons.IconsFactory;
	import com.jidesoft.plaf.LookAndFeelFactory;
	import com.jidesoft.swing.JideSplitPane;
	import com.jidesoft.swing.JideSwingUtilities;
	import com.jidesoft.swing.SearchableBar;
	import com.jidesoft.swing.TableSearchable;

	import javax.swing.*;
	import javax.swing.table.JTableHeader;
	import javax.swing.table.TableCellEditor;
	import javax.swing.table.TableCellRenderer;
	import javax.swing.table.TableModel;
	import java.awt.*;
	import java.awt.event.ActionEvent;
	import java.awt.event.ItemEvent;
	import java.awt.event.ItemListener;
	import java.awt.event.KeyEvent;
	import java.awt.event.MouseAdapter;
	import java.awt.event.MouseEvent;
	import java.awt.event.MouseListener;
	import java.io.File;
	import java.io.IOException;
import java.util.ArrayList;
	import java.util.Date;
	import java.util.List;
import java.util.Vector;
	
	
public class AdvanceFiltering  extends AbstractDemo {
	    public GroupTable _table;
	    public JLabel _message;
	    protected TableModel _tableModel;
	    private DefaultGroupTableModel _groupTableModel;
	    private boolean _useStyle;
	    private String _lastDirectory = ".";
	    private static final long serialVersionUID = 256315903870338341L;

	    
	    public GroupTableHeader header=null;
	    
	    
	    public AdvanceFiltering() {
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
	    JCheckBox keepColumnOrder =  new JCheckBox("Advance Filtering");;
	    @Override
	    public Component getOptionsPanel() {
	        JPanel checkBoxPanel = new JPanel(new GridLayout(0, 1, 0, 2));

	         
	        keepColumnOrder.addItemListener(new ItemListener() {
	            public void itemStateChanged(ItemEvent e) {
	              if(e.getStateChange() == ItemEvent.SELECTED ){
	               
	            	 
	            	  header.setAcceptTextInput(true);
	            	  header.setUseNativeHeaderRenderer(false);
	                 
	               
	            	  
	              }else {
	            	  
	            	  header.setAcceptTextInput(false);
	            	  header.setUseNativeHeaderRenderer(true);
	            	 
	            	  
	            	  customizeQuickFiltering();
	                
	              }

	            }
	        });
	        keepColumnOrder.setSelected(_groupTableModel.isKeepColumnOrder());
	        checkBoxPanel.add(keepColumnOrder);
	       
	        return checkBoxPanel;
	    }
	   
	    
	    protected void customizeAutoFilterBox(AutoFilterBox autoFilterBox)
	    {
	    	
	    	
	    	 List<FilterFactory> vfactories = new  ArrayList();
	    	 
	    	 vfactories = FilterFactoryManager.getDefaultInstance().getFilterFactories(Date.class); 
	    	 System.out.println(""+vfactories.get(0).getName());
	    	/*JMenu jm=new JMenu("uuuuuuuuuuuu");
	    	JMenuItem item=new JMenuItem("AAA");
	    	jm.add(item);
	    	 autoFilterBox.add(jm);
	    	*/ 
	         autoFilterBox.addDynamicTableFilter(new AbstractDynamicTableFilter()
	         {
	             @Override
	             public String getName() {
	                 
	            	/*  JPopupMenu popup=new JPopupMenu();
		            	JMenuItem item=new JMenuItem("AAA");
		     	    	popup.add(item);
		     	    	return popup;*/
		     	    	return "Test-Filter";
	             }

	             public boolean initializeFilter(TableModel tableModel, int i, Object[] objects) {
	            	 JPopupMenu popup=new JPopupMenu();
	            	JMenuItem item=new JMenuItem("AAA");
	     	    	popup.add(item);
	     	    	 
	            	 JOptionPane.showMessageDialog(AdvanceFiltering.this.getDemoPanel(), "Hello this is a Test");
	            	
	            	 popup.show();
	                 header.getFilterableTableModel().addFilter(0, new SingleValueFilter<String>("Beverages"));
	                 header.getFilterableTableModel().setFiltersApplied(true); 
	    	       
	                  return true;
	             }

	             public boolean isValueFiltered(Object value) {
	            	 return !"Beverages".equals(value);
	            	 
	             }
	         });
	        		
	        		

	    }
	    
	    
	    private void customizeQuickFiltering(){
	    	 header = new GroupTableHeader(_table){
	        	/* @Override
	             protected IFilterableTableModel createFilterableTableModel(TableModel model) {
	                 return new FilterableTableModel(model) {
	                    // private static final long serialVersionUID = 7072186511643823323L;

	                     @Override
	                     public boolean isColumnAutoFilterable(int column) {
	                        if(column==0 && _groupTableModel.findColumn("Grouped Columns")==column  )
	                    	 return false;
	                    System.out.println(""+column);     
	                     return true;
	                     }

	                     @Override
	                     public boolean isValuePredetermined(int column) {
	                    	 System.out.println(""+column);   
	                         return column != 2;
	                     }
	                 };
	             }*/
	        	
	        	 //////////////
	        	 @Override
	      	    protected TableCellRenderer createDefaultRenderer() {
	      	        if (isAutoFilterEnabled()) {
	      	            return new AutoFilterTableHeaderRenderer() {
	      	                @Override
	      	                protected void customizeList(JList list) {
	      	                    super.customizeList(list);
	      	                 
	      	               JPopupMenu jm=new JPopupMenu("uuuuuuuuuuuu");
	      	    	    	JMenuItem item=new JMenuItem("AAA");
	      	    	    	jm.add(item);
	      	    	    	 list.add(jm);
	      	                }

	      	                @Override
	      	                protected void customizeAutoFilterBox(AutoFilterBox autoFilterBox) {
	      	                    super.customizeAutoFilterBox(autoFilterBox);
	      	                  //  this.customizeAutoFilterBox(autoFilterBox);
	      	                    autoFilterBox.applyComponentOrientation(this.getComponentOrientation());
	      	                }
	      	            };
	      	        }
	      	        else {
	      	            return super.createDefaultRenderer();
	      	        }
	      	    }

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
	      	                    };
	      	                }

	      	                @Override
	      	                protected void customizeAutoFilterBox(AutoFilterBox autoFilterBox) {
	      	                	 
	      	                //	autoFilterBox.applyComponentOrientation(this.getComponentOrientation());
	      	                    super.customizeAutoFilterBox(autoFilterBox);
	      	                  
	      	                   // this.customizeAutoFilterBox(autoFilterBox);
	      	                 /* JMenu jm=new JMenu("uuuuuuuuuuuu");
	      	    	    	JMenuItem item=new JMenuItem("AAA");
	      	    	    	jm.add(item);
	      	    	    	 autoFilterBox.add(jm);
	      	                 */ AdvanceFiltering.this.customizeAutoFilterBox(autoFilterBox);
	      	                }
	      	            };
	      	        }
	      	        else {
	      	            return null;
	      	        }
	      	    }
	        };
	    	
	    	
	    }
	    
	    private void defaultQuickFiltering(){
	    	
	    	 header = new GroupTableHeader(_table);
	    	   	
	    	
	    }
	    
	    
	    public Component getDemoPanel() {
	        final JideSplitPane panel = new JideSplitPane(JideSplitPane.VERTICAL_SPLIT);

	        _tableModel = DemoData.createProductReportsTableModel(true, 0);
	        final CalculatedTableModel calculatedTableModel = setupProductDetailsCalculatedTableModel(_tableModel);
	        FilterableTableModel filterableTableModel = new FilterableTableModel(calculatedTableModel);

	        _groupTableModel = new StyledGroupTableModel(filterableTableModel);
	       // _groupTableModel.addGroupColumn(0, DefaultGroupTableModel.SORT_GROUP_COLUMN_ASCENDING);
	       // _groupTableModel.addGroupColumn(1, DefaultGroupTableModel.SORT_GROUP_COLUMN_DESCENDING);
	      //  _groupTableModel.groupAndRefresh();

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
	        //default Header 
	        customizeQuickFiltering();
	       //   defaultQuickFiltering();
	    	header.setGroupHeaderEnabled(true);
	        header.setAutoFilterEnabled(true);
	        header.setUseNativeHeaderRenderer(true);
	        header.setAllowMultipleValues(true);
	        
	       
	        
	        
	        
	        _table.setTableHeader(header);

	         
	        _groupTableModel.setDisplaySeparateGroupColumn(true); 
	       

	        _table.setSpecialColumnsHidable(false);
	        
	  
	             _table.setAutoResizeMode(JideTable.AUTO_RESIZE_FILL);
	        _table.setOptimized(true);

	        // hide the grid lines is good for performance
	        _table.setShowLeafNodeTreeLines(false);
	        _table.setShowTreeLines(false);
	        _table.setExportCollapsedRowsToExcel(true);

	        _table.expandAll();
	    
	            
	      
	        panel.add(JideSwingUtilities.createLabeledComponent(new JLabel("Right click on the table header to see more options"),
	                new JScrollPane(_table), BorderLayout.BEFORE_FIRST_LINE));
//	        panel.add(field, BorderLayout.AFTER_LINE_ENDS);

	        TableSearchable searchable = new GroupTableSearchable(_table);
	        searchable.setSearchColumnIndices(new int[]{2, 3});
	        searchable.setRepeats(true);
	        SearchableBar searchableBar = SearchableBar.install(searchable, KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK), new SearchableBar.Installer() {
	            public void openSearchBar(SearchableBar searchableBar) {
	                panel.add(searchableBar, BorderLayout.AFTER_LAST_LINE);
	                panel.invalidate();
	                panel.revalidate();
	            }

	            public void closeSearchBar(SearchableBar searchableBar) {
	                panel.remove(searchableBar);
	                panel.invalidate();
	                panel.revalidate();
	            }
	        });
	        searchableBar.setName("TableSearchableBar");
	        return panel;
	    }

	    @Override
	    public String getDemoFolder() {
	        return "G28.GroupableTableModel";
	    }

	    public static void main(String[] args) {
	        SwingUtilities.invokeLater(new Runnable() {
	            public void run() {
	                LookAndFeelFactory.installDefaultLookAndFeelAndExtension();
	               // UIManager.getDefaults().put("TableHeader.autoFilterTableHeaderUIDelegate", CustomAutoFilterTableHeaderUIDelegate.class.getName());
	                showAsFrame(new AdvanceFiltering());
	                UIManager.getDefaults().put("TableHeader.autoFilterTableHeaderUIDelegate", CustomAutoFilterTableHeaderUIDelegate.class.getName());
	            }
	        });

	    } 
	   

	   

	    private CalculatedTableModel setupProductDetailsCalculatedTableModel(TableModel tableModel) {
	        CalculatedTableModel calculatedTableModel = new CalculatedTableModel(tableModel);
	        calculatedTableModel.addAllColumns();
	        SingleColumn year = new SingleColumn(tableModel, "ShippedDate", "Year", new DateYearGrouper());
	        year.setConverterContext(YearNameConverter.CONTEXT);
	        calculatedTableModel.addColumn(year);
	        SingleColumn qtr = new SingleColumn(tableModel, "ShippedDate", "Quarter", new DateQuarterGrouper());
	        qtr.setConverterContext(QuarterNameConverter.CONTEXT);
	        calculatedTableModel.addColumn(qtr);
	        SingleColumn month = new SingleColumn(tableModel, "ShippedDate", "Month", new DateMonthGrouper());
	        month.setConverterContext(MonthNameConverter.CONTEXT);
	        calculatedTableModel.addColumn(month);
	        calculatedTableModel.addColumn(new SingleColumn(tableModel, "ShippedDate", "Week", new DateWeekOfYearGrouper()));

	        return calculatedTableModel;
	    }

	    private class StyledGroupTableModel extends DefaultGroupTableModel implements StyleModel {
	        private static final long serialVersionUID = 4936234855874300579L;

	        public StyledGroupTableModel(TableModel tableModel) {
	            super(tableModel);
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
	    
	 
	   /* class CustomAutoFilterTableHeader extends AutoFilterTableHeader {
	        public CustomAutoFilterTableHeader(JTable table) {
	            super(table);
	        }
	    @Override
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
	                    CustomAutoFilterTableHeader.this.customizeAutoFilterBox(autoFilterBox);
	                    autoFilterBox.applyComponentOrientation(CustomAutoFilterTableHeader.this.getComponentOrientation());
	                }
	            };
	        }
	        else {
	            return super.createDefaultRenderer();
	        }
	    }

	    @Override
	    protected TableCellEditor createDefaultEditor() {
	        if (isAutoFilterEnabled()) {
	            return new AutoFilterTableHeaderEditor() {
	                private static final long serialVersionUID = -7347944435632932543L;

	                @Override
	                protected AutoFilterBox createAutoFilterBox() {
	                    return new AutoFilterBox() {
	                        @Override
	                        protected void customizeList(JList list) {
	                            super.customizeList(list);
	                        }
	                    };
	                }

	                @Override
	                protected void customizeAutoFilterBox(AutoFilterBox autoFilterBox) {
	                    autoFilterBox.applyComponentOrientation(CustomAutoFilterTableHeader.this.getComponentOrientation());
	                    super.customizeAutoFilterBox(autoFilterBox);
	                    CustomAutoFilterTableHeader.this.customizeAutoFilterBox(autoFilterBox);
	                    GroupTableDemo.this.customizeAutoFilterBox(autoFilterBox);
	                }
	            };
	        }
	        else {
	            return null;
	        }
	    }
	    */
	   // }
	

}
