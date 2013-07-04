import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.table.*;

import com.jidesoft.comparator.ObjectComparatorManager;
import com.jidesoft.converter.ObjectConverterManager;
import com.jidesoft.grid.*;
import com.jidesoft.grouper.ObjectGrouperManager;
import com.jidesoft.plaf.basic.ThemePainter;
import com.jidesoft.swing.*;

public class AutofilteringIssueDemo {
  
  private static final char SPACE = ' ';
  private static final char NEW_LINE = '\n';

  private JPanel contentPanel;

  private TableScrollPane scrollPane;
  private BasketSymbolDataTableModel tableModel;
  private FilterableTableModel filterableModel;

  private JTable mainTable;
  
  private JideButton toggleAutoFilterButton;
  
  private final BasketSymbolData[] TABLE_DATA = new BasketSymbolData[] {
      new BasketSymbolData("T", "Buy", 100000),
      new BasketSymbolData("AAPL", "Buy", 2000000),
      new BasketSymbolData("DELL", "Buy", 300000000),
      new BasketSymbolData("MSFT", "Sell", 200000),
      new BasketSymbolData("ORCL", "Sell", 500000),
      new BasketSymbolData("GOOG", "Buy", 3000000)
  };

  public AutofilteringIssueDemo() {
    createComponents();
    layoutComponents();
  }

  public static void main(String... args) {
    final AutofilteringIssueDemo demo = new AutofilteringIssueDemo();

    EventQueue.invokeLater(new Runnable() {
      public void run() {
        demo.showDemo();
      }
    });
  }

  // --------------------------------------------------------------------------
  // Utility methods
  // --------------------------------------------------------------------------

  private void showDemo() {
    // Show demo in a frame
    JFrame frame = new JFrame("Autofiltering Issues Demo");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setContentPane(contentPanel);
    frame.pack();
    frame.setSize(new Dimension(500, 300));
    
    JideSwingUtilities.centerWindow(frame);
    frame.setVisible(true);
  }

  private void createComponents() {
    this.contentPanel = new JPanel(new BorderLayout(2, 2));
    contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    this.tableModel = new BasketSymbolDataTableModel();
    tableModel.setData(TABLE_DATA);
    
    this.filterableModel = new FilterableTableModel(tableModel) {
      private static final long serialVersionUID = 1L;

      @Override
      public boolean isColumnAutoFilterable(int column) { 
        return column > 0;
      }

      @Override
      public boolean isValuePredetermined(int column) { return false; }
    };

    this.scrollPane = new TableScrollPane(filterableModel, true) {
      private static final long serialVersionUID = 1L;

      @Override
      protected JTable createTable(TableModel model, boolean sortable, int type) {

        SortableTable table = new SortableTable(model) {
          private static final long serialVersionUID = 1L;

          @Override
          public boolean getScrollableTracksViewportWidth() {
            if (autoResizeMode != AUTO_RESIZE_OFF) {
              if (getParent() instanceof JViewport) {
                return getParent().getWidth() > getPreferredSize().width;
              }
            }
            return false;
          }

          @Override
          protected ISortableTableModel createSortableTableModel(
              TableModel model) {
            return new SortableTableModel(model) {
              private static final long serialVersionUID = 1L;

              @Override
              public boolean isColumnSortable(int column) {
                return column > 0;
              }
            };
          }
        };

        // ... configure table
        table.setRowHeight(24);
        table.setFont(new Font("Tahoma", Font.PLAIN, 11));
        table.setTableStyleProvider(new RowStripeTableStyleProvider());
        table.setRowResizable(false);
        table.setColumnSelectionAllowed(false);
        table.setRowSelectionAllowed(true);
        table.setGridColor(Color.LIGHT_GRAY);
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.setAutoStartCellEditing(true);
        table.setAlwaysRequestFocusForEditor(true);
        table.setClickCountToStart(1);
        table.setFillsGrids(false);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        AutoFilterTableHeader header = new AutoFilterTableHeader(table) {
          private static final long serialVersionUID = 1L;
/*

          @Override
          protected IFilterableTableModel createFilterableTableModel(TableModel model) {
            FilterableTableModel filterableModel = new FilterableTableModel(model);

            // automatically resize columns when a filter is either added/removed
            filterableModel.addFilterableTableModelListener(new FilterableTableModelListener() {
              @Override
              public void filterableTableModelChanged(FilterableTableModelEvent event) {
                updateColumnWidths();
              }
            });

            return filterableModel;
          }
*/
        };

        // ... configure header
        header.setRolloverEnabled(false);
        header.setClickToStartEditing(true);

        header.setShowFilterIcon(true);
        header.setShowSortArrow(true);
        header.setShowFilterName(true);
        header.setAllowMultipleValues(true);

        // disable reordering of columns as we do in RolloverTableScrollPane
        header.setReorderingAllowed(false);

        table.setTableHeader(header);

        return table;
      }
    };

    this.mainTable = scrollPane.getMainTable();
    registerTableComponents();

    this.toggleAutoFilterButton = new JideButton("Toggle Filtering");
    toggleAutoFilterButton.setButtonStyle(JideButton.TOOLBOX_STYLE);
    toggleAutoFilterButton.setFocusPainted(false);
    toggleAutoFilterButton.setHorizontalAlignment(SwingConstants.CENTER);
    toggleAutoFilterButton.setForegroundOfState(ThemePainter.STATE_DEFAULT, Color.BLACK);
    toggleAutoFilterButton.addActionListener(new ActionListener() {
      
      @Override
      public void actionPerformed(ActionEvent e) {
        toggleAutoFiltering();
      }
    });
  }
  
  private void toggleAutoFiltering() {
    // ... main table
    JTableHeader header = mainTable.getTableHeader();
    if (header instanceof AutoFilterTableHeader) {
      AutoFilterTableHeader filterHeader = (AutoFilterTableHeader)header;

      boolean filteringEnabled = filterHeader.isAutoFilterEnabled();
      if (filteringEnabled) {
        filterHeader.clearFilters();
      }
      filterHeader.setAutoFilterEnabled(! filteringEnabled);

      updateColumnWidths();
    }
  }
  
  private void registerTableComponents() {
    CellEditorManager.initDefaultEditor();
    CellRendererManager.initDefaultRenderer();
    ObjectConverterManager.initDefaultConverter();
    ObjectComparatorManager.initDefaultComparator();
    ObjectGrouperManager.initDefaultGrouper();
    
    // setup initial column widths    
    updateColumnWidths();
  }

  private void updateColumnWidths() {
    // Expand the columns of the main table
    TableUtils.autoResizeAllColumns(mainTable);

    // ... Delete column
    TableColumnModel tcm = mainTable.getColumnModel();
    TableColumn column = tcm.getColumn(0);

    column.setCellRenderer(new ButtonCellRenderer());
    column.setCellEditor(new DeleteDataRowEditor());

    setMaxColumnWidth(column, 30);
  }

  private void setMaxColumnWidth(TableColumn column, int maxWidth) {
    column.setPreferredWidth(maxWidth);
    column.setResizable(false);
  }

  private void layoutComponents() {
    contentPanel.add(scrollPane, BorderLayout.CENTER);
    
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 2));
    buttonPanel.add(toggleAutoFilterButton);
    
    contentPanel.add(buttonPanel, BorderLayout.AFTER_LAST_LINE);
  }

  private String wordWrap(int width, String originalString) {
    StringBuilder builder = new StringBuilder(originalString);
    int lastSpace = -1;
    int lineStart = 0;
    int index = 0;

    while (index < builder.length()) {
      if ( builder.charAt(index) == SPACE ) {
        lastSpace = index;
      }
      
      if ( builder.charAt(index) == NEW_LINE ) {
        lastSpace = -1;
        lineStart = index + 1;
      }
      
      if (index > lineStart + width - 1 ) {
        if (lastSpace != -1) {
          builder.setCharAt(lastSpace, NEW_LINE);
          lineStart = lastSpace + 1;
          lastSpace = -1;
        } else {
          builder.insert(index, NEW_LINE);
          lineStart = index + 1;
        }
      }
      
      index++;
    }
    
    return builder.toString();
  }

  // --------------------------------------------------------------------------
  // Utility classes
  // --------------------------------------------------------------------------

  private class BasketSymbolData {
    private String symbol;
    private String side;
    private long totalShares;

    public BasketSymbolData(String symbol, String side, long totalShares) {
      this.symbol = symbol;
      this.side = side;
      this.totalShares = totalShares;
    }

    public String getSymbol() {
      return symbol;
    }

    public String getSide() {
      return side;
    }

    public long getTotalShares() {
      return totalShares;
    }
  }

  private class BasketSymbolDataTableModel extends AbstractMultiTableModel {
    private static final long serialVersionUID = 1L;

    private final String[] COLUMNS = {
        " ", "Symbol", "Side", "Total Shares", "UDF1", "UDF2", "UDF3"  
    };

    private List<BasketSymbolData> dataList = new ArrayList<BasketSymbolData>();
    
    public void setData(final BasketSymbolData... dataItems) {
      if (! EventQueue.isDispatchThread()) {
        EventQueue.invokeLater(new Runnable() {
          
          @Override
          public void run() {
            setData(dataItems);
          }
        });
        return;
      }
      
      List<BasketSymbolData> newDataList = new ArrayList<BasketSymbolData>();
      for (BasketSymbolData datum : dataItems) {
        newDataList.add(datum);
      }
      
      this.dataList.clear();
      this.dataList = newDataList;
      
      fireTableDataChanged();
    }

    // ------------------------------------------------------------------------
    // Implementation of TableModel
    // ------------------------------------------------------------------------

    @Override
    public int getRowCount() {
      return (dataList == null) ? 0 : dataList.size();
    } 

    @Override
    public int getColumnCount() {
      return COLUMNS.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
      BasketSymbolData bean = getBeanAtRow(rowIndex);

      switch (columnIndex) {
      case 0:     // the "Delete" button
        return "" + (rowIndex + 1);
      case 1:     // Symbol
        return bean.getSymbol();
      case 2:     // Side
        return bean.getSide();
      case 3:     // Total Shares
        return bean.getTotalShares();
      }

      return null;
    }

    @Override
    public String getColumnName(int column) {
      return COLUMNS[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
      if (columnIndex == 3) {
        return Long.class;
      }

      return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
      return columnIndex == 0;
    }

    // ------------------------------------------------------------------------
    // Implementation of MultiTableModel
    // ------------------------------------------------------------------------

    @Override
    public int getColumnType(int columnIndex) {
      return REGULAR_COLUMN;
    }

    @Override
    public int getTableIndex(int columnIndex) {
      return 0;
    }

    // ------------------------------------------------------------------------
    // Utility methods
    // ------------------------------------------------------------------------

    public BasketSymbolData getBeanAtRow(int rowIndex) {
      if (dataList == null) return null;  // Sanity check

      return ((rowIndex < 0) || (dataList.size() <= rowIndex)) ? null 
          : dataList.get(rowIndex);
    }
  }

  private class ButtonCellRenderer extends JButton implements TableCellRenderer { 

    private static final long serialVersionUID = 1L;

    protected final JLabel nullLabel = new JLabel(" ");

    protected Border regBorder;
    protected Border selectedBorder = 
        BorderFactory.createBevelBorder(BevelBorder.RAISED);

    public ButtonCellRenderer() {
      regBorder = getBorder();

      setText("\u00d7");
      nullLabel.setOpaque(true);
      nullLabel.setBorder(UIManager.getBorder("TableHeader.cellBorder"));
      setFocusPainted(false);
      setAlignmentX(0);
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus, int row, int column) {

      boolean cellEditable = table.getModel().isCellEditable(row, 
          table.convertColumnIndexToModel(column));

      if (cellEditable) {
        setBorder(isSelected ? selectedBorder : regBorder);
        return this;
      }
      return nullLabel;
    }
  }

  private abstract class AbstractButtonEditor extends DefaultCellEditor implements ActionListener {

    private static final long serialVersionUID = 0L;

    protected JButton button;
    protected Object value;   
    protected int rowIndex;
    protected JTable table;

    public AbstractButtonEditor() {
      super(new JCheckBox());

      this.button = new JButton();

      button.setText("\u00d7");

      button.setIconTextGap(0);  // there won't be any text
      button.setHorizontalAlignment(SwingConstants.CENTER);

      button.addActionListener(this);

      button.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          fireEditingStopped();      
          table.clearSelection();
        }
      });
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
        boolean isSelected, int row, int column) {
      this.table = table;
      if (isSelected) {
        button.setForeground(table.getSelectionForeground());
        button.setBackground(table.getSelectionBackground());
      } else{
        button.setForeground(table.getForeground());
        button.setBackground(table.getBackground());
      }

      this.rowIndex = row;
      this.value = value;

      return button;
    }

    @Override
    public Object getCellEditorValue() {
      return value;
    }
  }
  
  private abstract class AbstractDeleteRowButtonEditor extends AbstractButtonEditor {
    private static final long serialVersionUID = 1L;
    
    protected AbstractDeleteRowButtonEditor() {
      super();
    }
    
    @Override
    public void actionPerformed(ActionEvent event) {
      table.getSelectionModel().addSelectionInterval(rowIndex, rowIndex);
      
      int response = JOptionPane.showConfirmDialog(getParentComponent(), 
          wordWrap(60, getConfirmationMessage()),
          "Confirm deletion...", JOptionPane.YES_NO_OPTION);

      if (response != JOptionPane.YES_OPTION) {
        return;
      }
      
      removeRow(table.getModel());
      
      table.getSelectionModel().removeSelectionInterval(rowIndex, rowIndex);
    }

    protected abstract Component getParentComponent();
    protected abstract String getConfirmationMessage();
    protected abstract void removeRow(TableModel model);
  }
  
  private class DeleteDataRowEditor extends AbstractDeleteRowButtonEditor {
    private static final long serialVersionUID = 1L;

    public DeleteDataRowEditor() {
      super();
    }

    @Override
    protected Component getParentComponent() {
      return scrollPane;
    }

    @Override
    protected String getConfirmationMessage() {
      int actualRowIndex = convertRowIndex();
      
      return MessageFormat.format("Are you sure you want to " +
          "delete the data Row {0}?", (actualRowIndex + 1));
    }

    @Override
    protected void removeRow(TableModel model) {
        int actualRow = TableModelWrapperUtils.getActualRowAt(table.getModel(), rowIndex);
        System.out.printf("RowIndex: %d - ActualRow: %d%n", rowIndex, actualRow);

      // Delete the selected row
      //tableModel.removeRow(rowIndex);
    }
    
    private int convertRowIndex() {
      boolean isFiltersApplied = filterableModel.isFiltersApplied();
      
      return isFiltersApplied ? 
          filterableModel.getActualRowAt(rowIndex) : rowIndex;
    }
  }
}