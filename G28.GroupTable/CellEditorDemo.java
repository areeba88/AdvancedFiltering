/*
 * @(#)CellEditorDemo.java 5/18/2012
 *
 * Copyright 2002 - 2012 JIDE Software Inc. All rights reserved.
 */

import com.jidesoft.combobox.AbstractComboBox;
import com.jidesoft.combobox.CheckBoxTreeExComboBox;
import com.jidesoft.combobox.ExComboBox;
import com.jidesoft.converter.ConverterContext;
import com.jidesoft.converter.ObjectConverter;
import com.jidesoft.converter.ObjectConverterManager;
import com.jidesoft.filter.*;
import com.jidesoft.filter.Filter;
import com.jidesoft.grid.*;
import com.jidesoft.plaf.LookAndFeelFactory;
import com.jidesoft.swing.JideLabel;
import org.apache.commons.collections.CollectionUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.List;
import java.util.*;

/**
 * Created with IntelliJ IDEA. User: JIDE-010 Date: 5/18/12 Time: 12:21 PM To change this template use File | Settings |
 * File Templates.
 */
public class CellEditorDemo {

    public static void main(String[] args) {
        // Install Look and Feel
        LookAndFeelFactory.installDefaultLookAndFeel();

        // Register Object Converter
        ObjectConverterManager.initDefaultConverter();

        // Register Cell Editor
        CellEditorManager.initDefaultEditor();
        CellEditorManager.registerEditor(String[].class, new CellEditorFactory() {
            @Override
            public CellEditor create() {
                CheckBoxTreeComboBoxCellEditor cellEditor = new CheckBoxTreeComboBoxCellEditor() {
                    @Override
                    public ExComboBox createExComboBox() {
                        ExComboBox comboBox = super.createExComboBox();
                        ((CheckBoxTreeExComboBox) comboBox).setSearchUserObjectToSelect(true);
                        comboBox.setEditable(true);
                        return comboBox;
                    }
                };
                return cellEditor;
            }
        }, CheckBoxTreeComboBoxCellEditor.CONTEXT);

        // Create The Table
        SortableTableModel model = new SortableTableModel(new DefaultTableModel(2,2)){
            @Override
            public EditorContext getEditorContextAt(int rowIndex, int columnIndex) {
                if (columnIndex == 0) {
                    return CheckBoxTreeComboBoxCellEditor.CONTEXT;
                }
                else if (columnIndex == 1) {
                    return new EditorContext("CFT_String");
                }
                return super.getEditorContextAt(rowIndex, columnIndex);
            }

            @Override
            public Class<?> getCellClassAt(int row, int column) {
                if (column == 0) {
                    return String[].class;
                }
                else if (column == 1) {
                    return String.class;
                }
                return super.getCellClassAt(row, column);    //To change body of overridden methods use File | Settings | File Templates.
            }
        };
        ContextSensitiveTable table = new ContextSensitiveTable(model);

        // Test FilterFactory
        FilterFactoryManager fm = new FilterFactoryManager();
        final java.util.List<FilterFactory> lfString = fm.getFilterFactories(String.class);

        lfString.add(new FilterFactory() {
            @Override
            public Filter createFilter(Object... objects) {
                return null;            }

            @Override
            public String getConditionString(Locale locale) {
                return "";
            }

            @Override
            public String getName() {
                return "Starts with";
            }

            @Override
            public Class[] getExpectedDataTypes() {
                return new Class[0];
            }
        });
        lfString.add(new FilterFactory() {
            @Override
            public Filter createFilter(Object... objects) {
                return null;            }

            @Override
            public String getConditionString(Locale locale) {
                return "";
            }

            @Override
            public String getName() {
                return "Contains";
            }

            @Override
            public Class[] getExpectedDataTypes() {
                return new Class[0];
            }
        });
        lfString.add(new FilterFactory() {
            @Override
            public Filter createFilter(Object... objects) {
                return null;            }

            @Override
            public String getConditionString(Locale locale) {
                return "";
            }

            @Override
            public String getName() {
                return "Ends with";
            }

            @Override
            public Class[] getExpectedDataTypes() {
                return new Class[0];
            }
        });
        // register cell editor
        CellEditorManager.registerEditor(String.class, new CellEditorFactory() {
            @Override
            public CellEditor create() {
                return new ListComboBoxCellEditor(lfString.toArray());
            }
        }, new EditorContext("CFT_String"));
        // register object converter
        ObjectConverterManager.registerConverter(FilterFactory.class, new ObjectConverter() {
            @Override
            public String toString(Object object, ConverterContext context) {
                return (object instanceof FilterFactory)? ((FilterFactory)object).getName():"";
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
        // register cell renderer
        CellRendererManager.registerRenderer(String.class, new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                return new JideLabel((value instanceof FilterFactory) ? ((FilterFactory)value).getName() : "");
            }
        });


        // Construct the Window
        JFrame frame = new JFrame("CellEditorDemo");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().add(new JScrollPane(table));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }
}
