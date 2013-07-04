import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import com.jidesoft.grid.AbstractDynamicTableFilter;
import com.jidesoft.grid.AutoFilterBox;
import com.jidesoft.grid.AutoFilterTableHeader;
import com.jidesoft.grid.AutoFilterTableHeaderEditor;
import com.jidesoft.grid.AutoFilterTableHeaderRenderer;
import com.jidesoft.grid.SortableTable;
import com.jidesoft.grid.IFilterableTableModel.FilterItem;
import com.jidesoft.plaf.LookAndFeelFactory;


public class CountryTableTest extends AbstractDemo {

    String data[][] = { {"1", "FIJI"},
                        {"2", "US"},
                        {"3", "CHILE"}
    };

    public Component getDemoPanel() {
        final JPanel panel = new JPanel(new BorderLayout());

        final CountryTableModel model = new CountryTableModel();
        SortableTable table = new SortableTable(model);

        AutoFilterTableHeader tableHeader = new CustomAutoFilterTableHeader(table); //AutoFilterTableHeader(table);
        tableHeader.setAutoFilterEnabled(true);
        tableHeader.setAllowMultipleValues(true);
        tableHeader.setShowFilterName(true);
        tableHeader.setShowFilterIcon(true);

        table.setTableHeader(tableHeader);

        NonUSTableFilter countryFilter = new NonUSTableFilter();
        tableHeader.getFilterableTableModel().addFilter(model.COL_COUNTRY, countryFilter);
        tableHeader.getFilterableTableModel().setFiltersApplied(true);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        panel.add(scroll, BorderLayout.CENTER);

        JButton addBtn = new JButton("Add Country");
        addBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                model.addRecord();
            }
        });
        panel.add(addBtn, BorderLayout.NORTH);

        return panel;
    }

    public String getProduct() {
        return PRODUCT_NAME_GRIDS;
    }

    public String getName() {
        return "Country Table Test";
    }


    //
    public class CountryTableModel extends DefaultTableModel {

        public int COL_NUM = 0;
        public int COL_COUNTRY = 1;
        private List<ArrayList> records = new ArrayList<ArrayList>();

        public CountryTableModel() {
            initData();
        }

          public int getColumnCount() {
            return 2;
          }

          public int getRowCount() {
            return (records != null ? records.size() : 0);
          }

          public String getColumnName(int column) {
            if (column == COL_NUM) {
                return "No.";
            }
            else if (column == COL_COUNTRY) {
                return "COUNTRY";
            }
            return null;
          }

          public Object getValueAt(int row, int column) {
              return records.get(row).get(column);
          }

          public void initData() {
              for (int i = 0; i < data.length; i++) {
                  ArrayList<String> temp = new ArrayList<String>();
                  temp.add(""+data[i][COL_NUM]);
                  temp.add(""+data[i][COL_COUNTRY]);

                  records.add(temp);
              }
          }

          public void addRecord() {
              ArrayList<String> temp = new ArrayList<String>();
              temp.add(""+getRowCount());
              if (getRowCount() % 2 == 0) {
                  temp.add("KENYA");
              }
              else {
                  temp.add("US");
              }

              records.add(temp);

              fireTableDataChanged();
          }
    }

    class NonUSTableFilter extends AbstractDynamicTableFilter {
        public boolean initializeFilter(TableModel tableModel, int colIndex, Object[] possibleValues) {
            return true;
        }

        public boolean isValueFiltered(Object value) {
            if (value != null && value.toString().equalsIgnoreCase("US")) {
                return true;
            }
            return false;
        }
    }

    class CustomAutoFilterTableHeader extends AutoFilterTableHeader {
        public CustomAutoFilterTableHeader(JTable table) {
            super(table);
        }

/*
        @Override
        protected IFilterableTableModel createFilterableTableModel(TableModel model) {
            return new FilterableTreeTableModel(model) {
                @Override
                public boolean isSameConverterAt(int columnIndex) {
                    return columnIndex != 1 && super.isSameConverterAt(columnIndex);
                }
            };
        }
*/

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
                    @Override
                    protected AutoFilterBox createAutoFilterBox() {
                        return new AutoFilterBox() {
                            @Override
                            protected void customizeList(JList list) {

                                List<FilterItem> tempList = getFilterableTableModel().getFilterItems();
                                for (FilterItem fi : tempList) {
                                    //if (fi.getFilter() instanceof MultipleValuesFilter) {
                                    if (fi.getFilter() instanceof NonUSTableFilter) {
                                        Object[] selected = list.getSelectedValues();
                                        if (selected == null || (selected != null && selected.length == 0)) {
                                            for (int i=0; i<list.getModel().getSize(); i++) {
                                                Object item = list.getModel().getElementAt(i);
                                                if (!((NonUSTableFilter) fi.getFilter()).isValueFiltered(item)) {
                                                    list.setSelectedValue(i, true);
                                                    //list.setSelectionInterval(i, i);
                                                }
                                            }
                                        }
                                    }
                                }
                                //getListCellRenderer().

                                super.customizeList(list);
                            }

                            @Override
                            public Object[] getPossibleValues() {
                                Object[] possibleValues = super.getPossibleValues();
                                return super.getPossibleValues();
                            }
                        };
                    }

                    @Override
                    protected void customizeAutoFilterBox(AutoFilterBox autoFilterBox) {
                        autoFilterBox.applyComponentOrientation(CustomAutoFilterTableHeader.this.getComponentOrientation());
                        super.customizeAutoFilterBox(autoFilterBox);
                        CustomAutoFilterTableHeader.this.customizeAutoFilterBox(autoFilterBox);
                    }
                };
            }
            else {
                return null;
            }
        }
    }


    //MAIN
    public static void main(String[] args) {
        LookAndFeelFactory.installDefaultLookAndFeelAndExtension();
        showAsFrame(new CountryTableTest());
    }

}