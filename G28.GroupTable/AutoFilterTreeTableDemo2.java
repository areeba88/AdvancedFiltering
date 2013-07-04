/*
 * @(#)AutoFilterTreeTableDemo.java 4/10/2007
 *
 * Copyright 2002 - 2007 JIDE Software Inc. All rights reserved.
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.zip.GZIPInputStream;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.Popup;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;

import com.jidesoft.combobox.PopupPanel;
import com.jidesoft.converter.ConverterContext;
import com.jidesoft.converter.PercentConverter;
import com.jidesoft.grid.AbstractDynamicTableFilter;
import com.jidesoft.grid.AutoFilterBox;
import com.jidesoft.grid.AutoFilterTableHeader;
import com.jidesoft.grid.AutoFilterTableHeaderEditor;
import com.jidesoft.grid.CellStyle;
import com.jidesoft.grid.DefaultExpandableRow;
import com.jidesoft.grid.Expandable;
import com.jidesoft.grid.ExpandableRow;
import com.jidesoft.grid.Filter;
import com.jidesoft.grid.FilterableTableModel;
import com.jidesoft.grid.FilterableTableModelEvent;
import com.jidesoft.grid.FilterableTreeTableModel;
import com.jidesoft.grid.IndexChangeEvent;
import com.jidesoft.grid.IndexChangeListener;
import com.jidesoft.grid.QuickTableFilterField;
import com.jidesoft.grid.Row;
import com.jidesoft.grid.SortableTreeTableModel;
import com.jidesoft.grid.StyleModel;
import com.jidesoft.grid.TableModelWrapperUtils;
import com.jidesoft.grid.TreeTable;
import com.jidesoft.grid.TreeTableModel;
import com.jidesoft.plaf.LookAndFeelFactory;
import com.jidesoft.swing.JideTitledBorder;
import com.jidesoft.swing.PartialEtchedBorder;
import com.jidesoft.swing.PartialSide;
import com.jidesoft.swing.StyledLabel;
import com.jidesoft.swing.StyledLabelBuilder;

/**
 * Demoed Component: {@link com.jidesoft.grid.SortableTable} <br> Required jar files: jide-common.jar, jide-grids.jar
 * <br> Required L&F: any L&F
 */
public class AutoFilterTreeTableDemo2 extends AbstractDemo 
{
    private AutoFilterTableHeader _header;
    TreeTable _table;
    StyledLabel _filterTimesLabel;
    int _filterAppliedRowCount = 0;
    AutoFilterTreeTableDemo2 myself;

    public AutoFilterTreeTableDemo2() {
       
       this.myself = this;
    }

    public String getName() {
        return "AutoFilterTableHeader (TreeTable) Demo";
    }

    public String getProduct() {
        return PRODUCT_NAME_GRIDS;
    }

    @Override
    public String getDescription() {
        return "This is a demo of QuickTableFilterField, AutoFilterTableHeader working with FilterableTreeTableModel.\n" +
                "\n" +
                "Demoed classes:\n" +
                "com.jidesoft.grid.FilterableTreeTableModel\n" +
                "com.jidesoft.grid.QuickTableFilterField\n" +
                "com.jidesoft.grid.AutoFilterTableHeader";
    }


    @Override
    public Component getOptionsPanel() {
        JPanel checkBoxPanel = new JPanel(new GridLayout(0, 1));
        JCheckBox autoFilterCheckBox = new JCheckBox("Auto Filter");
        autoFilterCheckBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                _header.setAutoFilterEnabled(e.getStateChange() == ItemEvent.SELECTED);
            }
        });
        autoFilterCheckBox.setSelected(_header.isAutoFilterEnabled());

        JCheckBox showFilterNameCheckBox = new JCheckBox("Show Filter Name on Header");
        showFilterNameCheckBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                _header.setShowFilterName(e.getStateChange() == ItemEvent.SELECTED);
            }
        });
        showFilterNameCheckBox.setSelected(_header.isShowFilterName());

        checkBoxPanel.add(autoFilterCheckBox);
        checkBoxPanel.add(showFilterNameCheckBox);

        AbstractAction buttonActionAddRowsSelection = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                int rowIndex = _table.getSelectedRow();
                if (rowIndex == -1) {
                    return;
                }
                int actualRowIndex = TableModelWrapperUtils.getActualRowAt(_table.getModel(), rowIndex, TaskTreeTableModel.class);
                TaskTreeTableModel treeModel = (TaskTreeTableModel) TableModelWrapperUtils.getActualTableModel(_table.getModel(), TaskTreeTableModel.class);

                Row selectedRow = treeModel.getRowAt(actualRowIndex);
                treeModel.addRows(selectedRow, generateRows());
                _table.expandAll();
            }
        };
        buttonActionAddRowsSelection.putValue(Action.NAME, "Add 3 rows under selected row");
        JButton addRowsSelection = new JButton("Add 3 rows under selected row");
        addRowsSelection.setAction(buttonActionAddRowsSelection);

        AbstractAction buttonActionAddRowsRandomlyInFirstLevel = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                TaskTreeTableModel treeModel = (TaskTreeTableModel) TableModelWrapperUtils.getActualTableModel(_table.getModel(), TaskTreeTableModel.class);
                Random random = new Random();
                int position = random.nextInt() % ((Expandable) treeModel.getRoot()).getChildrenCount();
                if (position < 0) {
                    position += ((Expandable) treeModel.getRoot()).getChildrenCount();
                }
                System.out.println("Added rows in the first level: " + position + "th place before: " + ((Row) ((Expandable) treeModel.getRoot()).getChildAt(position)).getValueAt(0));

                treeModel.addRows((Row) treeModel.getRoot(), position, generateRows());
                _table.expandAll();
            }
        };
        buttonActionAddRowsRandomlyInFirstLevel.putValue(Action.NAME, "Add 3 rows randomly in root level");
        JButton addRowsRandomlyInFirstLevel = new JButton("Add 3 rows randomly in root level");
        addRowsRandomlyInFirstLevel.setAction(buttonActionAddRowsRandomlyInFirstLevel);

        AbstractAction buttonActionAddRowsRandomlyInSecondLevel = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                TaskTreeTableModel treeModel = (TaskTreeTableModel) TableModelWrapperUtils.getActualTableModel(_table.getModel(), TaskTreeTableModel.class);
                Random random = new Random();
                Row selectedRows;
                do {
                    int position = random.nextInt() % treeModel.getRowCount();
                    if (position < 0) {
                        position += treeModel.getRowCount();
                    }
                    selectedRows = treeModel.getRowAt(position);
                    if (selectedRows.getParent() == null) {
                        continue;
                    }
                    if ("Specification Stage".equals(((Row) selectedRows.getParent()).getValueAt(0))) {
                        continue;
                    }
                    if (selectedRows.getParent().getParent() == null) {
                        continue;
                    }
                    if (selectedRows.getParent().getParent().getParent() == null) {
                        break;
                    }
                }
                while (true);
                System.out.println("Added rows in the second level after: " + selectedRows.getValueAt(0));

                treeModel.addRows(treeModel.getRowIndex(selectedRows), generateRows());
                _table.expandAll();
            }
        };
        buttonActionAddRowsRandomlyInSecondLevel.putValue(Action.NAME, "Add 3 rows randomly in second level");
        JButton addRowsRandomlyInSecondLevel = new JButton("Add 3 rows randomly in second level");
        addRowsRandomlyInSecondLevel.setAction(buttonActionAddRowsRandomlyInSecondLevel);

        AbstractAction buttonActionDeleteRow = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                int rowIndex = _table.getSelectedRow();
                if (rowIndex == -1) {
                    return;
                }
                int actualRowIndex = TableModelWrapperUtils.getActualRowAt(_table.getModel(), rowIndex, TaskTreeTableModel.class);
                TaskTreeTableModel treeModel = (TaskTreeTableModel) TableModelWrapperUtils.getActualTableModel(_table.getModel(), TaskTreeTableModel.class);
                treeModel.removeRow(actualRowIndex);
            }
        };
        buttonActionDeleteRow.putValue(Action.NAME, "Delete the selected row(s)");
        JButton deleteRow = new JButton("Delete the selected row(s)");
        deleteRow.setAction(buttonActionDeleteRow);

        AbstractAction buttonActionUpdateRow = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                int rowIndex = _table.getSelectedRow();
                if (rowIndex == -1) {
                    return;
                }
                int actualRowIndex = TableModelWrapperUtils.getActualRowAt(_table.getModel(), rowIndex, TaskTreeTableModel.class);
                TaskTreeTableModel treeModel = (TaskTreeTableModel) TableModelWrapperUtils.getActualTableModel(_table.getModel(), TaskTreeTableModel.class);
                Row row = treeModel.getRowAt(actualRowIndex);
                row.setValueAt(0.5, 1);
                treeModel.fireTableRowsUpdated(actualRowIndex, actualRowIndex);
            }
        };
        buttonActionUpdateRow.putValue(Action.NAME, "Update the selected row percentage");
        JButton updateRow = new JButton("Update the selected row percentage");
        updateRow.setAction(buttonActionUpdateRow);

        checkBoxPanel.add(addRowsSelection);
        checkBoxPanel.add(addRowsRandomlyInFirstLevel);
        checkBoxPanel.add(addRowsRandomlyInSecondLevel);
        checkBoxPanel.add(deleteRow);
        checkBoxPanel.add(updateRow);

        return checkBoxPanel;
    }

    private List<Task> generateRows() {
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy kk:mm");
        Date date = new Date();
        try {
            date = format.parse("18/02/2009 17:49");
        }
        catch (ParseException exception) {
            exception.printStackTrace();
        }
        Task parentRow = new Task("Specification Stage", 0.8, 12, 3, date, date);
        Task childRow1 = new Task("Review documents", 0.9, 10, 5, date, date);
        Task childRow2 = new Task("Create documents", 1, 17, 8, date, date);
        parentRow.addChild(childRow1);
        parentRow.addChild(childRow2);
        List<Task> rows = new ArrayList<Task>();
        rows.add(parentRow);
        return rows;
    }

    public Component getDemoPanel() {
        List list = createTaskList();
        if (list == null) {
            return new JLabel("Failed to read Project.txt.gz");
        }
        final TaskTreeTableModel tableModel = new TaskTreeTableModel(list);

        JPanel quickSearchPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        QuickTableFilterField filterField = new QuickTableFilterField(tableModel);
        filterField.setObjectConverterManagerEnabled(true);
        quickSearchPanel.add(filterField);
        quickSearchPanel.setBorder(new JideTitledBorder(new PartialEtchedBorder(PartialEtchedBorder.LOWERED, PartialSide.NORTH), "QuickTableFilterField", JideTitledBorder.LEADING, JideTitledBorder.ABOVE_TOP));

        JPanel tablePanel = new JPanel(new BorderLayout(2, 2));
        tablePanel.setBorder(BorderFactory.createCompoundBorder(new JideTitledBorder(new PartialEtchedBorder(PartialEtchedBorder.LOWERED, PartialSide.NORTH), "Filtered Task List", JideTitledBorder.LEADING, JideTitledBorder.ABOVE_TOP),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        final JLabel label = new JLabel("Click on \"Auto Filter\" in option panel to enable AutoFilter feature");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));


        SortableTreeTableModel sortableTreeTableModel = new StripeSortableTreeTableModel(new FilterableTreeTableModel(filterField.getDisplayTableModel()));
        sortableTreeTableModel.setDefaultSortableOption(SortableTreeTableModel.SORTABLE_LEAF_LEVEL);
        sortableTreeTableModel.setSortableOption(0, SortableTreeTableModel.SORTABLE_ROOT_LEVEL);
        sortableTreeTableModel.addIndexChangeListener(new IndexChangeListener() {
            public void indexChanged(IndexChangeEvent event) {
                if (event.getSource() instanceof FilterableTableModel && event.getType() == IndexChangeEvent.INDEX_CHANGED_EVENT && _filterTimesLabel != null) {
                    FilterableTableModel model = (FilterableTableModel) event.getSource();
                    _filterAppliedRowCount = model.retrieveFilterApplyRecords();
                    if (_filterAppliedRowCount != 0) {
                        StyledLabelBuilder.setStyledText(_filterTimesLabel, "Filter is applied on {" + _filterAppliedRowCount + ":f:blue} rows. {" + model.getRowCount() + ":f:blue} rows are left.");
                    }
                    else {
                        StyledLabelBuilder.setStyledText(_filterTimesLabel, "Filter in this level is cleared. {" + model.getRowCount() + ":f:blue} rows are shown up from the table model it wraps.");
                    }
                }
            }
        });
        final TreeTable treeTable = new TreeTable(sortableTreeTableModel);

//        _header = new AutoFilterTableHeader(treeTable);
        //new 
        _header = new TestFilterTableHeader(treeTable); 
        _header.setAutoFilterEnabled(true);
        treeTable.setTableHeader(_header);

        // do not clear selection when filtering
        treeTable.setClearSelectionOnTableDataChanges(false);
        treeTable.setRowResizable(true);
        treeTable.setVariousRowHeights(true);
        treeTable.setShowTreeLines(false);
        treeTable.expandAll();
        treeTable.setPreferredScrollableViewportSize(new Dimension(700, 400));

        treeTable.getColumnModel().getColumn(0).setPreferredWidth(200);
        treeTable.getColumnModel().getColumn(3).setPreferredWidth(60);
        treeTable.getColumnModel().getColumn(4).setPreferredWidth(60);
        _table = treeTable;
        filterField.setTable(treeTable);
        JScrollPane scrollPane = new JScrollPane(treeTable);
        tablePanel.add(label, BorderLayout.BEFORE_FIRST_LINE);
        tablePanel.add(scrollPane);

        JPanel infoPanel = new JPanel(new BorderLayout(3, 3));
        _filterTimesLabel = new StyledLabel("Not filtered yet.");
        infoPanel.add(_filterTimesLabel);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(new JideTitledBorder(new PartialEtchedBorder(PartialEtchedBorder.LOWERED, PartialSide.NORTH), "Filtered Row Count", JideTitledBorder.LEADING, JideTitledBorder.ABOVE_TOP),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));

        JPanel panel = new JPanel(new BorderLayout(3, 3));
        panel.add(quickSearchPanel, BorderLayout.BEFORE_FIRST_LINE);
        panel.add(tablePanel);
        panel.add(infoPanel, BorderLayout.AFTER_LAST_LINE);
        return panel;
    }

    static class TaskTreeTableModel extends TreeTableModel implements StyleModel {

        public TaskTreeTableModel() {
        }

        public TaskTreeTableModel(java.util.List rows) {
            super(rows);
        }

        public int getColumnCount() {
            return 6;
        }

        @Override
        public ConverterContext getConverterContextAt(int rowIndex, int columnIndex) {
            if (columnIndex == 1) {
                return PercentConverter.CONTEXT;
            }
            return super.getConverterContextAt(rowIndex, columnIndex);
        }

        @Override
        public Class<?> getCellClassAt(int rowIndex, int columnIndex) {
            return getColumnClass(columnIndex);
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return String.class;
                case 1:
                    return Double.class;
                case 2:
                    return Double.class;
                case 3:
                    return Double.class;
                case 4:
                    return Date.class;
                case 5:
                    return Date.class;
            }
            return Object.class;
        }

        @Override
        public String getColumnName(int column) {
            switch (column) {
                case 0:
                    return "Task Name";
                case 1:
                    return "% Completed";
                case 2:
                    return "Duration";
                case 3:
                    return "Work";
                case 4:
                    return "Start";
                case 5:
                    return "Finish";
            }
            return null;
        }

        static CellStyle BOLD = new CellStyle();

        static {
            BOLD.setFontStyle(Font.BOLD);
        }

        public CellStyle getCellStyleAt(int rowIndex, int columnIndex) {
            Row row = getRowAt(rowIndex);
            if (row.getParent() == getRoot() || (row instanceof ExpandableRow && ((ExpandableRow) row).hasChildren())) {
                return BOLD;
            }
            return null;
        }

        public boolean isCellStyleOn() {
            return true;
        }
    }

    private List createTaskList() {
        List rows = new ArrayList();

        try {
            InputStream resource = this.getClass().getClassLoader().getResourceAsStream("Project.txt.gz");
            if (resource == null) {
                return null;
            }
            InputStream in = new GZIPInputStream(resource);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));

            Task[] tasks = new Task[3];
            DateFormat format = new SimpleDateFormat("dd/MM/yyyy kk:mm");

            do {
                String line = reader.readLine();
                if (line == null || line.length() == 0) {
                    break;
                }
                String[] values = line.split(",");
                String name = "";
                double completed = 0.0;
                double duration = 0.0;
                double work = 0.0;
                Date startTime = new Date();
                Date endTime = new Date();
                if (values.length >= 1) {
                    name = values[0];
                }
                if (values.length >= 2) {
                    try {
                        completed = Double.parseDouble(values[1]);
                    }
                    catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
                if (values.length >= 3) {
                    try {
                        duration = Double.parseDouble(values[2]);
                    }
                    catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
                if (values.length >= 4) {
                    try {
                        work = Double.parseDouble(values[3]);
                    }
                    catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
                if (values.length >= 5) {
                    try {
                        startTime = format.parse(values[4]);
                    }
                    catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                if (values.length >= 6) {
                    try {
                        endTime = format.parse(values[5]);
                    }
                    catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                if (name.startsWith("++")) {
                    Task project = new Task(name.substring(2), completed, duration, work, startTime, endTime);
                    tasks[1].addChild(project);
                    tasks[2] = project;
                }
                else if (name.startsWith("+")) {
                    Task task = new Task(name.substring(1), completed, duration, work, startTime, endTime);
                    tasks[0].addChild(task);
                    tasks[1] = task;
                }
                else {
                    Task task = new Task(name, completed, duration, work, startTime, endTime);
                    rows.add(task);
                    tasks[0] = task;
                }
            }
            while (true);
            return rows;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static class Task extends DefaultExpandableRow {
        String name;
        double completed;
        double duration;
        double work;
        Date startTime;
        Date endTime;

        public Task() {
        }


        public Task(String name, double completed, double duration, double work, Date startTime, Date endTime) {
            this.name = name;
            this.completed = completed;
            this.duration = duration;
            this.work = work;
            this.startTime = startTime;
            this.endTime = endTime;
        }

        public Object getValueAt(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return name;
                case 1:
                    return completed;
                case 2:
                    return duration;
                case 3:
                    return work;
                case 4:
                    return startTime;
                case 5:
                    return endTime;
            }
            return null;
        }

        @Override
        public void setValueAt(Object value, int columnIndex) {
            switch (columnIndex) {
                case 0:
                    name = "" + value;
                    break;
                case 1:
                    completed = (Double) value;
                    break;
                case 2:
                    duration = (Double) value;
                    break;
                case 3:
                    work = (Double) value;
                    break;
                case 4:
                    startTime = (Date) value;
                    break;
                case 5:
                    endTime = (Date) value;
                    break;
            }
        }
    }

    class StripeSortableTreeTableModel extends SortableTreeTableModel implements StyleModel {
        public StripeSortableTreeTableModel(TableModel model) {
            super(model);
        }

        protected final Color BACKGROUND1 = new Color(253, 253, 244);
        protected final Color BACKGROUND2 = new Color(255, 255, 255);

        CellStyle cellStyle = new CellStyle();

        @Override
        public CellStyle getCellStyleAt(int rowIndex, int columnIndex) {
            cellStyle.setHorizontalAlignment(-1);
            cellStyle.setForeground(Color.BLACK);
            if (rowIndex % 2 == 0) {
                cellStyle.setBackground(BACKGROUND1);
            }
            else {
                cellStyle.setBackground(BACKGROUND2);
            }
            return cellStyle;
        }

        @Override
        public boolean isCellStyleOn() {
            return true;
        }
    }

    @Override
    public String getDemoFolder() {
        return "G27.AutoFilterTableHeader";
    }

    static public void main(String[] s) {
        LookAndFeelFactory.installDefaultLookAndFeelAndExtension();
        showAsFrame(new AutoFilterTreeTableDemo2());
    }
    

//
//  extension
//
    
    class TestFilterTableHeader extends AutoFilterTableHeader {
       public TestFilterTableHeader(JTable table) {
           super(table);
       }

       @Override
       protected void customizeAutoFilterBox(AutoFilterBox autoFilterBox) {
           super.customizeAutoFilterBox(autoFilterBox);
           autoFilterBox.setAllowCustomFilter(false);
       }

       @Override
       protected TableCellEditor createDefaultEditor() {
           if (isAutoFilterEnabled()) {
               return new AutoFilterTableHeaderEditor() {
                   @Override
                   protected void customizeAutoFilterBox(AutoFilterBox autoFilterBox) {
                       autoFilterBox.applyComponentOrientation(TestFilterTableHeader.this.getComponentOrientation());
                       super.customizeAutoFilterBox(autoFilterBox);
                       TestFilterTableHeader.this.customizeAutoFilterBox(autoFilterBox);
                   }

                   @Override
                   protected AutoFilterBox createAutoFilterBox() {
                       return new AutoFilterBox() {
 
                          private PopupPanel popUpPanel;
                         
                          public void applyFilter(                     // !!! Does not react !!!
                                Filter filter, int columnIndex)
                          {
                             Object selValue = popUpPanel.getSelectedObject();
                             // do something ...
                             System.out.println(columnIndex);
                             super.applyFilter(filter, columnIndex);
                          }

                          
                          protected PopupPanel createPopupPanel(
                                TableModel arg0, int arg1, Object[] arg2)
                          {
                             popUpPanel = super.createPopupPanel(arg0, arg1, arg2);  // !!! recat
                           
                             JMenu subMenu2 = new JMenu("DateFilters");
                 			subMenu2.add(new JMenuItem("WEL"));
                 			subMenu2.add(new JMenuItem("WEL1"));
                 			
                 			popUpPanel.add(subMenu2);
                 			popUpPanel.setAutoscrolls(true);
                 			//popUpPanel.setInheritsPopupMenu(true);
                 		//	popUpPanel.setResizableCorners(1);
                 			popUpPanel.setStretchToFit(true);
                 			
                             return popUpPanel;
                          }
                       };
                   }
               };
           }
           else {
               return null;
           }
       }
   }
    
}
