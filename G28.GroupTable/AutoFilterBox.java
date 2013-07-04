/*
 * @(#)FilterableTableHeaderEditor.java 2/3/2007
 *
 * Copyright 2002 - 2007 JIDE Software Inc. All rights reserved.
 */


import com.jidesoft.combobox.CheckBoxListChooserPanel;
import com.jidesoft.combobox.ListChooserPanel;
import com.jidesoft.combobox.PopupPanel;
import com.jidesoft.comparator.ObjectComparatorManager;
import com.jidesoft.converter.ConverterContext;
import com.jidesoft.converter.ObjectConverterManager;
import com.jidesoft.filter.Filter;
import com.jidesoft.grid.ContextSensitiveTableModel;
import com.jidesoft.grid.DynamicTableFilter;
import com.jidesoft.grid.FilterListCellRenderer;
import com.jidesoft.grid.FilterableTableModel;
import com.jidesoft.grid.FilterableTableModelEvent;
import com.jidesoft.grid.FilterableTableModelListener;
import com.jidesoft.grid.GroupableTableModel;
import com.jidesoft.grid.ISortableTableModel;
import com.jidesoft.grid.MultipleValuesFilter;
import com.jidesoft.grid.SingleValueFilter;
import com.jidesoft.grid.SortEvent;
import com.jidesoft.grid.SortListener;
import com.jidesoft.grid.SortableTableModel;
import com.jidesoft.grid.TableModelWrapperUtils;
import com.jidesoft.grid.ValueFilterListSelectionModel;
import com.jidesoft.grouper.GrouperContext;
import com.jidesoft.grouper.ObjectGrouper;
import com.jidesoft.grouper.ObjectGrouperManager;
import com.jidesoft.icons.JideIconsFactory;
import com.jidesoft.plaf.UIDefaultsLookup;
import com.jidesoft.plaf.basic.ThemePainter;
import com.jidesoft.popup.JidePopup;
import com.jidesoft.swing.*;
import com.jidesoft.utils.PortingUtils;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.basic.BasicButtonListener;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Array;
import java.util.*;
import java.util.List;

/**
 * <code>AutoFilterBox</code> is used as the cell editor component for <code>AutoFilterTableHeader</code>.
 */
public class AutoFilterBox extends HeaderBox implements SortListener, FilterableTableModelListener, MouseListener {
    public static final String PROPERTY_TITLE = "title";
    public static final String PROPERTY_ICON = "icon";
    public static final String PROPERTY_SORT_ARROW_VISIBLE = "sortArrowVisible";
    public static final String PROPERTY_FILTER_INDICATOR_VISIBLE = "filterIndicatorVisible";
    public static final String PROPERTY_FILTER_BUTTON_VISIBLE = "filterButtonVisible";
    public static final String PROPERTY_ASCENDING = "ascending";
    public static final String PROPERTY_SORTED = "sorted";
    public static final String PROPERTY_POSSIBLE_VALUES = "possibleValues";
    public static final String PROPERTY_SELECTED_POSSIBLE_VALUES = "selectedPossibleValues";

    private JLabel _label;
    private ClickThroughStyledLabel _sortArrow;
    private AbstractButton _filterButton;

    private Icon _downIcon = new ArrowIcon(SwingConstants.SOUTH);
    private Icon _upIcon = new ArrowIcon(SwingConstants.NORTH);

    private boolean _sortArrowVisible = true;
    private boolean _filterIndicatorVisible = true;
    private boolean _filterButtonVisible = true;

    private TableModel _tableModel;
    private int _tableColumnIndex;

    private FilterableTableModel _filterableTableModel;
    private int _filterableTableModelColumnIndex;

    private ISortableTableModel _sortableTableModel;
    private int _sortableTableModelColumnIndex;

    private GroupableTableModel _groupableTableModel;
    private int _groupableTableModelColumnIndex;

    private TableModel _actualTableModel;
    private int _actualTableModelColumnIndex;

    private Object[] _possibleValues;
    private MouseInputListener _toggleListener;
    private PropertyChangeListener _propertyChangeListener;

    private boolean _showFilterName = false;
    private boolean _showFilterIcon = false;
    private boolean _allowMultipleValues = false;

    /**
     * Creates a button with no set text or icon.
     */
    public AutoFilterBox() {
         setHorizontalAlignment(SwingConstants.LEADING);
         setVerticalAlignment(SwingConstants.CENTER);
         initComponents("", null, false, false, false);
     }

     /**
      * Sets the table model onto <code>AutoFilterBox</code>.
      *
      * @param tableModel   the table model.
     * @param columnIndex  the column index for this auto fitler
      * @param isCellEditor whether this is used as cell editor. The opposite is to be used as cell renderer.
      */
     public void setTableModel(TableModel tableModel, int columnIndex, boolean isCellEditor) {
         _tableModel = tableModel;
         _tableColumnIndex = columnIndex;
         _actualTableModel = TableModelWrapperUtils.getActualTableModel(tableModel);
         _actualTableModelColumnIndex = TableModelWrapperUtils.getActualColumnAt(tableModel, columnIndex);
         _filterableTableModel = (FilterableTableModel) TableModelWrapperUtils.getActualTableModel(tableModel, FilterableTableModel.class);
         _filterableTableModelColumnIndex = TableModelWrapperUtils.getActualColumnAt(tableModel, columnIndex, _filterableTableModel);
         _sortableTableModel = (ISortableTableModel) TableModelWrapperUtils.getActualTableModel(tableModel, ISortableTableModel.class);
         if (_sortableTableModel instanceof SortableTableModel)
             _sortableTableModelColumnIndex = TableModelWrapperUtils.getActualColumnAt(tableModel, columnIndex, (SortableTableModel)_sortableTableModel);
         _groupableTableModel = (GroupableTableModel) TableModelWrapperUtils.getActualTableModel(tableModel, GroupableTableModel.class);
         _groupableTableModelColumnIndex = TableModelWrapperUtils.getActualColumnAt(tableModel, columnIndex, _groupableTableModel);

         if (_filterableTableModel != null) {
             FilterableTableModelListener[] listeners = _filterableTableModel.getFilterableTableModelListeners();
             for (FilterableTableModelListener listener : listeners) {
                 if (listener instanceof AutoFilterBox) {
                     _filterableTableModel.removeFilterableTableModelListener(listener);
                 }
             }
             _filterableTableModel.addFilterableTableModelListener(this);
         }
         if (isCellEditor) {
             if (_sortableTableModel != null) {
                 SortListener[] listeners = _sortableTableModel.getSortListeners();
                 for (SortListener listener : listeners) {
                     if (listener instanceof AutoFilterBox) {
                         _sortableTableModel.removeSortListener(listener);
                     }
                 }
                 _sortableTableModel.addSortListener(this);
             }
         }

         initComponents(_tableModel.getColumnName(_tableColumnIndex), null, _sortableTableModel != null, _filterableTableModel != null && _filterableTableModel.isColumnAutoFilterable(_filterableTableModelColumnIndex), isCellEditor);
     }


     public void sortChanging(SortEvent event) {
     }

     public void sortChanged(SortEvent event) {
         updateSortArrow();
     }

     public void filterableTableModelChanged(FilterableTableModelEvent event) {
         updateFilterIndicator();
     }

     /**
      * Gets the table column index of this AutoFilterBox.
      *
      * @return the table column index.
      */
     public int getTableColumnIndex() {
         return _tableColumnIndex;
     }

     private void updateSortArrow() {
         if (_sortableTableModel != null) {
             boolean sorted = _sortableTableModel.isColumnSorted(_sortableTableModelColumnIndex);
             if (sorted) {
                 boolean ascending = _sortableTableModel.isColumnAscending(_sortableTableModelColumnIndex);
                 int rank = _sortableTableModel.getColumnSortRank(_sortableTableModelColumnIndex);
                 if (_sortArrow != null) {
                     setSortArrowVisible(true);
                     _sortArrow.setIcon(ascending ? _upIcon : _downIcon);
                     if (_sortableTableModel.getSortingColumns().size() > 1) {
                         _sortArrow.setText("" + (rank + 1));
                         StyleRange styleRange = new StyleRange(Font.PLAIN, StyleRange.STYLE_SUPERSCRIPT, 1.1f);
                         _sortArrow.addStyleRange(styleRange);
                     }
                 }
             }
             else {
                 if (_sortArrow != null) {
                     setSortArrowVisible(false);
                 }
             }
         }
     }

     private void updateFilterIndicator() {
         if (_filterableTableModel != null && isShowFilterName()) {
             Filter[] filters = _filterableTableModel.getFilters(_filterableTableModelColumnIndex);
             setText(formatColumnTitle(_filterableTableModel.getColumnName(_filterableTableModelColumnIndex), filters));
         }
         if (_filterableTableModel != null && isShowFilterIcon()) {
             setIcon(isFiltered() ? _filterableTableModel.getFilterIcon(_filterableTableModelColumnIndex) : null);
         }
     }

     /**
      * Formats the string when the filter name is displayed. By default, we will
      * display the column name first, followed by a ":" then the filter name.
      *
      * @param columName the column name.
      * @param filters   the filters
      * @return the string after formatted.
      */
     protected String formatColumnTitle(String columName, Filter[] filters) {
         if (filters == null || filters.length == 0) {
             return columName;
         }
         else {
             return columName + ": " + filters[0].getName();
         }
     }

     protected boolean isFiltered() {
         if (_filterableTableModel != null) {
             Filter[] filters = _filterableTableModel.getFilters(_filterableTableModelColumnIndex);
             if (filters != null && filters.length > 0) {
                 return true;
             }
         }
         return false;
     }

     protected void initComponents(String text, Icon icon, boolean sortArrowVisible, boolean filterButtonVisible, boolean isCellEditor) {
         removeAll();
         setBorder(BorderFactory.createEmptyBorder(3, 6, 3, 6));
         setLayout(new JideBoxLayout(this, JideBoxLayout.X_AXIS, 2));
         _label = new ClickThroughLabel(text, icon, SwingConstants.LEADING);
         _label.setHorizontalAlignment(getHorizontalAlignment());
         _label.setVerticalAlignment(getVerticalAlignment());
         ((ClickThroughLabel) _label).setTarget(this);
         _label.setOpaque(false);
         _label.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
         add(_label);

         _sortArrow = new ClickThroughStyledLabel(_upIcon);
         _sortArrow.setForeground(new Color(172, 168, 153));
         ((ClickThroughStyledLabel) _sortArrow).setTarget(this);
         _sortArrow.setOpaque(false);
         _sortArrow.setVerticalAlignment(JLabel.CENTER);
         add(_sortArrow, JideBoxLayout.FIX);
         _sortArrow.setVisible(sortArrowVisible);

         _filterButton = createDefaultButton();
         if (isCellEditor) {
             _filterButton.addActionListener(new AbstractAction() {
                 public void actionPerformed(ActionEvent e) {
                     if (isPopupVisible()) {
                         hidePopup();
                     }
                     else {
                         Object[] possibleValues = _filterableTableModel.getPossibleValues(_filterableTableModelColumnIndex, null);
                         if (possibleValues != null) {
                             setPossibleValues(possibleValues);
                             showPopupPanelAsPopup();
                         }
                     }
                 }
             });
             _filterButton.addMouseMotionListener(new MouseMotionAdapter() {
                 @Override
                 public void mouseMoved(MouseEvent e) {
                     getModel().setRollover(true);
                 }
             });
         }
         add(_filterButton, JideBoxLayout.FIX);
         _filterButton.setVisible(filterButtonVisible);

         if (isCellEditor) {
             installListeners();
         }

         updateSortArrow();
         updateFilterIndicator();
     }

     protected void installListeners() {
         if (_toggleListener == null) {
             _toggleListener = new MouseInputAdapter() {
                 @Override
                 public void mouseClicked(MouseEvent e) {
                     if (SwingUtilities.isLeftMouseButton(e)) {
                         boolean extend = (e.getModifiers() & Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()) != 0;
                         toggleSortOrder(extend);
                         e.consume();
                     }
                 }

                 @Override
                 public void mouseReleased(MouseEvent e) {
                 }
             };
         }
         if (!JideSwingUtilities.isListenerRegistered(this, MouseListener.class, _toggleListener)) {
             addMouseListener(_toggleListener);
         }

         if (_propertyChangeListener != null) {
             _propertyChangeListener = new PropertyChangeListener() {
                 public void propertyChange(PropertyChangeEvent evt) {
                     if (PROPERTY_ASCENDING.equals(evt.getPropertyName())) {
                         if (_sortArrow != null) {
                             _sortArrow.setIcon(Boolean.TRUE.equals(evt.getNewValue()) ? _upIcon : _downIcon);
                         }
                     }
                     else if (PROPERTY_SORTED.equals(evt.getPropertyName())) {
                         if (_sortArrow != null) {
                             setSortArrowVisible(Boolean.TRUE.equals(evt.getNewValue()));
                         }
                     }
                     else if (HORIZONTAL_ALIGNMENT_CHANGED_PROPERTY.equals(evt.getPropertyName())) {
                         _label.setHorizontalAlignment(getHorizontalAlignment());
                     }
                     else if (VERTICAL_ALIGNMENT_CHANGED_PROPERTY.equals(evt.getPropertyName())) {
                         _label.setVerticalAlignment(getVerticalAlignment());
                     }
                 }
             };
         }
         if (!JideSwingUtilities.isListenerRegistered(this, PropertyChangeListener.class, _propertyChangeListener)) {
             addPropertyChangeListener(_propertyChangeListener);
         }

         if (!JideSwingUtilities.isMouseListenerRegistered(this, this)) {
             addMouseListener(this);
         }
     }

     public boolean isSortArrowVisible() {
         return _sortArrowVisible;
     }

     public void setSortArrowVisible(boolean sortArrowVisible) {
         boolean old = _sortArrowVisible;
         if (old != sortArrowVisible) {
             _sortArrowVisible = sortArrowVisible;
             firePropertyChange(PROPERTY_SORT_ARROW_VISIBLE, old, _sortArrowVisible);
         }
         _sortArrow.setVisible(_sortArrowVisible);
     }

     public boolean isFilterIndicatorVisible() {
         return _filterIndicatorVisible;
     }

     public void setFilterIndicatorVisible(boolean filterIndicatorVisible) {
         boolean old = _filterIndicatorVisible;
         if (old != filterIndicatorVisible) {
             _filterIndicatorVisible = filterIndicatorVisible;
             firePropertyChange(PROPERTY_FILTER_INDICATOR_VISIBLE, old, _filterIndicatorVisible);
         }
     }

     public boolean isFilterButtonVisible() {
         return _filterButtonVisible;
     }

     public void setFilterButtonVisible(boolean filterButtonVisible) {
         boolean old = _filterButtonVisible;
         if (old != filterButtonVisible) {
             _filterButtonVisible = filterButtonVisible;
             firePropertyChange(PROPERTY_FILTER_BUTTON_VISIBLE, old, filterButtonVisible);
         }
         _filterButton.setVisible(_filterButtonVisible);
     }

     @Override
     public void setText(String title) {
         if (_label != null) {
             _label.setText(title);
         }
         else {
             super.setText(title);
         }
     }


     @Override
     public String getText() {
         if (_label != null) {
             return _label.getText();
         }
         else {
             return super.getText();
         }
     }

     @Override
     public void setIcon(Icon icon) {
         _label.setIcon(icon);
     }

     public boolean isAscending() {
         if (_sortableTableModel != null) {
             _sortableTableModel.isColumnAscending(_sortableTableModelColumnIndex);
         }
         return false;
     }

     public void toggleSortOrder() {
         if (_sortableTableModel != null) {
             _sortableTableModel.toggleSortOrder(_sortableTableModelColumnIndex, false);
         }
     }

     public void toggleSortOrder(boolean extend) {
         if (_sortableTableModel != null) {
             _sortableTableModel.toggleSortOrder(_sortableTableModelColumnIndex, extend);
         }
     }

     public void setAscending(boolean ascending) {
         boolean old = isAscending();
         if (old != ascending) {
             if (_sortableTableModel != null) {
                 if (ascending != _sortableTableModel.isColumnAscending(_sortableTableModelColumnIndex)) {
                     _sortableTableModel.sortColumn(_sortableTableModelColumnIndex, true, ascending);
                 }
                 firePropertyChange(PROPERTY_ASCENDING, old, ascending);
             }
         }
     }

     public boolean isSorted() {
         if (_sortableTableModel != null) {
             _sortableTableModel.isColumnSorted(_sortableTableModelColumnIndex);
         }
         return false;
     }

     public void setSorted(boolean sorted) {
         boolean old = isSorted();
         if (old != sorted) {
             if (_sortableTableModel != null) {
                 boolean wasSorted = _sortableTableModel.isColumnSorted(_sortableTableModelColumnIndex);
                 if (sorted && !wasSorted) {
                     _sortableTableModel.sortColumn(_sortableTableModelColumnIndex);
                 }
                 else if (!sorted && wasSorted) {
                     _sortableTableModel.unsortColumn(_sortableTableModelColumnIndex);
                 }
                 firePropertyChange(PROPERTY_SORTED, old, sorted);
             }
         }
     }

     public Object[] getPossibleValues() {
         return _possibleValues;
     }

     public void setPossibleValues(Object[] possibleValues) {
         Object[] old = _possibleValues;
         _possibleValues = possibleValues;
         firePropertyChange(PROPERTY_POSSIBLE_VALUES, old, possibleValues);
         if (_filterButton != null) {
             _filterButton.setEnabled(_possibleValues != null);
         }
     }

     /**
      * Applies the filter to the specified column index of the <code>FilterableTableModel</code>.
      *
      * @param filter      the filter.
      * @param columnIndex the column index.
      */
      protected void applyFilter(Filter filter, int columnIndex) {
          applyFilter(filter, columnIndex, true);
      }

      protected void applyFilter(Filter filter, int columnIndex, boolean clearFirst) {
          if (_filterableTableModel != null) {
              _filterableTableModel.setFiltersApplied(false);
              if (clearFirst) {
                  Filter[] filters = _filterableTableModel.getFilters(columnIndex);
                  if (filters != null) {
                      for (Filter f : filters) {
                          if (f instanceof SingleValueFilter) {
                              _filterableTableModel.removeFilter(columnIndex, f);
                          }
                          else if (f instanceof MultipleValuesFilter) {
                              _filterableTableModel.removeFilter(columnIndex, f);
                          }
                          else if (f instanceof DynamicTableFilter) {
                              _filterableTableModel.removeFilter(columnIndex, f);
                          }
                      }
                  }
              }
              if (filter != null) {
                  _filterableTableModel.addFilter(columnIndex, filter);
              }
              _filterableTableModel.setFiltersApplied(true);
          }
      }

      public class ArrowIcon implements Icon {
          private int _direction = SwingConstants.NORTH;

          public ArrowIcon(int direction) {
              _direction = direction;
          }

          public int getIconWidth() {
              return 7;
          }

          public int getIconHeight() {
              //                return 5;  // triangle
              return 8; // arrow
          }

          public synchronized void paintIcon(Component c, Graphics g, int x, int y) {
              g.setColor(new Color(172, 168, 153));
              switch (_direction) {
                  case SwingConstants.NORTH:
                      // a triangle
                      //                        for (int i = 0; i < 5; i ++) {
                      //                            g.drawLine(mx + i, my + i, mx + 8 - i, my + i);
                      //                        }

                      // arrow
                      for (int i = 0; i < 7; i++) {
                          g.drawLine(x + i, y + Math.abs(3 - i), x + i, y + Math.abs(3 - i) + 1);
                      }
                      g.drawLine(x + 3, y, x + 3, y + getIconHeight());
                      break;
                  case SwingConstants.SOUTH:
                      // a triangle
                      //                        for (int i = 0; i < 5; i ++) {
                      //                            g.drawLine(mx + 4 - i, my + i, mx + 4 + i, my + i);
                      //                        }
                      // arrow
                      for (int i = 0; i < 7; i++) {
                          g.drawLine(x + i, y + 8 - Math.abs(3 - i), x + i, y + 8 - Math.abs(3 - i) + 1);
                      }
                      g.drawLine(x + 3, y, x + 3, y + getIconHeight());
                      break;
              }
          }
      }

      /**
       * Creates the default combobox button. This method is used only if createButtonComponent() returns null.
       * The idea is each combobox can implement createButtonComponent() to provide its own button. However the default
       * implementation should still be the button created by this method.
       *
       * @return the default combobox button.
       */
      protected AbstractButton createDefaultButton() {
          AbstractButton defaultButton = null;
          boolean useDefaultButton = /*UIManager.getLookAndFeel() instanceof WindowsLookAndFeel && */!UIDefaultsLookup.getBoolean("AbstractComboBox.useJButton");
          try {
              JComboBox comboBox = new JComboBox();
              comboBox.setEnabled(isEnabled());
              comboBox.setEditable(true);
              comboBox.doLayout();
              Component[] components = comboBox.getComponents();
              for (int i = 0; i < components.length; i++) {
                  Component component = components[i];
                  if (component instanceof AbstractButton) {
                      defaultButton = (AbstractButton) component;

                      // try to figure out the correct size of the button.
                      Dimension size = defaultButton.getSize();
                      int height = comboBox.getPreferredSize().height;
                      Insets insets = comboBox.getInsets();
                      int buttonSize = height - (insets.top + insets.bottom);
                      if (size.height < 16) {
                          size.height = buttonSize;
                      }
                      if (size.width < 16) {
                          size.width = buttonSize;
                      }
                      defaultButton.setPreferredSize(size);

                      MouseListener[] mouseListeners = defaultButton.getMouseListeners();
                      for (int j = 0; j < mouseListeners.length; j++) {
                          MouseListener l = mouseListeners[j];
                          if (l instanceof BasicButtonListener) {
                              continue;
                          }
                          defaultButton.removeMouseListener(l);
                      }

                      defaultButton.setRequestFocusEnabled(false);
                      defaultButton.setFocusable(false);
                      break;
                  }
              }
          }
          catch (UnsupportedOperationException e) {
              // ingore
          }
          if (!useDefaultButton || defaultButton == null) {
              JButton button = new JButton(JideIconsFactory.getImageIcon(JideIconsFactory.Arrow.DOWN));
              button.setRequestFocusEnabled(false);
              button.setFocusable(false);
              button.setMargin(defaultButton != null ? defaultButton.getMargin() : new Insets(0, 0, 0, 0));
              if (defaultButton != null) {
                  Dimension preferredSize = defaultButton.getPreferredSize();
                  if (preferredSize.height < 16) {
                      int height = getHeight();
                      Insets insets = getInsets();
                      int buttonSize = height - (insets.top + insets.bottom);
                      preferredSize.height = buttonSize;
                      preferredSize.width = buttonSize;
                  }
                  button.setPreferredSize(preferredSize);
              }
              else {
                  button.setPreferredSize(new Dimension(16, 16));
              }
              button.setEnabled(isEnabled());
              return button;
          }
          else {
              return defaultButton;
          }
      }

      /**
       * The actual panel inside the PopupWindow.
       */
      private PopupPanel _popupPanel;

      /**
       * The popup window that will be shown when button is pressed.
       */
      private JidePopup _popup;

      /**
       * Creates the popup window. By default it will create a JidePopup which is not detached and not resizable.
       * Subclass can override it to create your own JidePopup or customize the default one.
       *
       * @return the popup window.
       */
      protected JidePopup createPopupWindow() {
          JidePopup popup = new JidePopup();
          popup.setDetached(false);
          popup.setResizable(false);
          popup.setPopupBorder(UIDefaultsLookup.getBorder("PopupMenu.border"));
          return popup;
      }


      private void showPopupPanelAsPopup() {
          Object[] possibleValues = getPossibleValues();
          if (possibleValues == null) {
              return;
          }

        _popupPanel = createPopupPanel(_tableModel, _tableColumnIndex, possibleValues);

          requestFocus();

          _popup = createPopupWindow();
          _popup.setOwner(_filterButton);
          _popup.add(_popupPanel);

         /* if (_popupPanel.stretchToFit()) {
              if (_popupPanel.getActualPreferredSize().width != getWidth()) {
                  Border border = _popup.getPopupBorder();
                  int offset = 0;
                  if (border != null) {
                      Insets borderInsets = border.getBorderInsets(_popupPanel);
                      offset = borderInsets.left + borderInsets.right;
                  }
                  int width = Math.max(_popupPanel.getActualPreferredSize().width, getWidth() - offset);
                  _popupPanel.setPreferredSize(new Dimension(width, _popupPanel.getPreferredSize().height));
              }
          }*/

          if (!_popup.isPopupVisible()) {
              Point p = calculatePopupLocation();
              if (_popupPanel.getDefaultFocusComponent() != null) {
                  _popup.setDefaultFocusComponent(_popupPanel.getDefaultFocusComponent());
              }
              _popup.setResizable(_popupPanel.isResizable());
              try {
                  _popup.showPopup(p.x, p.y);
                  if (_popupPanel.isResizable()) {
                      _popup.setupResizeCorner(_popupPanel.getResizableCorners());
                  }
              }
              catch (IllegalComponentStateException e) {
                  // exception will happen when user drags the filter button to reorder
              }
          }
          else {
              hidePopup();
              _popup = null;
          }
      }

      public Object[] getPossibleValues(ObjectGrouper objectGrouper, Object[] values, Comparator comparator) {
          Map cache = new HashMap();
          for (int i = 0; i < values.length; i++) {
              Object value = objectGrouper.getValue(values[i]);
              if (value != null) {
                  cache.put(value, "");
              }
          }
          Set set = cache.keySet();
          Iterator iterator = set.iterator();
          Object[] objects = new Object[set.size()];
          int i = 0;
          boolean canBeSorted = true;
          while (iterator.hasNext()) {
              Object o = iterator.next();
              if (canBeSorted && comparator == null && !(o instanceof Comparable)) {
                  canBeSorted = false;
              }
              objects[i++] = o;
          }
          if (canBeSorted) {
              Arrays.sort(objects, comparator);
          }
          return objects;
      }

    protected PopupPanel createPopupPanel(final TableModel tableModel, final int columnIndex, final Object[] possibleValues) {
          final GrouperContext grouperContext = _groupableTableModel != null ? _groupableTableModel.getGrouperContext(_groupableTableModelColumnIndex) : null;
          final ObjectGrouper objectGrouper = _groupableTableModel != null ? ObjectGrouperManager.getGrouper(_groupableTableModel.getColumnClass(_groupableTableModelColumnIndex), grouperContext) : null;
          Object[] values;
          if (objectGrouper != null) {
              Comparator comparator = ObjectComparatorManager.getComparator(objectGrouper.getType(), objectGrouper.getComparatorContext());
              values = getPossibleValues(objectGrouper, possibleValues, comparator);
          }
          else {
              values = possibleValues;
          }
          final DynamicTableFilter[] dynamicFilters = getDynamicTableFilters();
          final DefaultComboBoxModel comboBoxModel = new DefaultComboBoxModel(values);
          comboBoxModel.insertElementAt(Filter.ALL, 0);
          PopupPanel popupPanel = null;
          CheckBoxListChooserPanel checkBoxListChooserPanel = null;
          ListChooserPanel listChooserPanel = null;

          if (isAllowMultipleValues()) {
            checkBoxListChooserPanel = new CheckBoxListChooserPanel(comboBoxModel, objectGrouper != null ? objectGrouper.getType() : tableModel.getColumnClass(columnIndex)) {
                  @Override
                  protected JList createList(ComboBoxModel comboBoxModel) {
                      final JList list = super.createList(comboBoxModel);
                      SearchableUtils.installSearchable(list);
                      ConverterContext converterContext = null;
                    if (tableModel instanceof ContextSensitiveTableModel) {
                        converterContext = ((ContextSensitiveTableModel) tableModel).getConverterContextAt(0, columnIndex);
                      }
                    final FilterListCellRenderer cellRenderer = new FilterListCellRenderer(
                            objectGrouper != null ? objectGrouper.getType() : tableModel.getColumnClass(columnIndex),
                                                                           objectGrouper != null ? objectGrouper.getConverterContext() : converterContext);
                      setRenderer(cellRenderer);
                      new ListSearchable(list) {
                          @Override
                          protected String convertElementToString(Object object) {
                              return cellRenderer.convertElementToString(Locale.getDefault(), object);
                          }
                      };

                      Filter[] filters = _filterableTableModel.getFilters(_filterableTableModelColumnIndex);
                      if (list instanceof CheckBoxList) {
                        ((CheckBoxList) list).setCheckBoxListSelectionModel(new ValueFilterListSelectionModel());
                          ((CheckBoxList) list).clearCheckBoxListSelection();
                          if (filters.length == 0) {
                              ((CheckBoxList) list).addCheckBoxListSelectedIndex(0);
                          }
                          else {
                              for (Filter filter : filters) {
                                  if (filter instanceof MultipleValuesFilter) {
                                      ((MultipleValuesFilter) filter).setObjectGrouper(objectGrouper);
                                      ((CheckBoxList) list).addCheckBoxListSelectedValues(((MultipleValuesFilter) filter).getValues());
                                  }
                              }
                          }
                      }

                      return list;
                  }
              };
              checkBoxListChooserPanel.setMaximumRowCount(8);
              checkBoxListChooserPanel.setDefaultFocusComponent(checkBoxListChooserPanel.getList());
              checkBoxListChooserPanel.addItemListener(new ItemListener() {
                  public void itemStateChanged(ItemEvent e) {
                      if (e.getStateChange() == ItemEvent.SELECTED) {
                          Object item = e.getItem();
                          Object[] objects = null;
                          if (item.getClass().isArray()) {
                              objects = new Object[Array.getLength(item)];
                              for (int i = 0; i < objects.length; i++) {
                                  objects[i] = Array.get(item, i);
                              }
                          }
                          if (objects == null) {
                              return;
                          }
                          if (objects.length == 1 && comboBoxModel.getIndexOf(objects[0]) == 0) {
                              applyFilter(null, _filterableTableModelColumnIndex, true);
                          }
                          else {
                              MultipleValuesFilter filter = new MultipleValuesFilter(objects);
                              if (objectGrouper != null) {
                                  filter.setObjectGrouper(objectGrouper);
                              }
                              applyFilter(filter, _filterableTableModelColumnIndex);
                          }
                          hidePopup();
                          updateFilterIndicator();
                      }
                  }
              });
              checkBoxListChooserPanel.setCancelAction(new AbstractAction(UIDefaultsLookup.getString("OptionPane.cancelButtonText")) {
                  public void actionPerformed(ActionEvent e) {
                      hidePopup();
                  }
              });
              popupPanel = checkBoxListChooserPanel;
          }
          else {
              for (int i = 0; i < dynamicFilters.length; i++) {
                  DynamicTableFilter factory = dynamicFilters[i];
                  comboBoxModel.insertElementAt(factory, i + 1);
              }

            listChooserPanel = new ListChooserPanel(comboBoxModel, objectGrouper != null ? objectGrouper.getType() : tableModel.getColumnClass(columnIndex)) {
                  @Override
                  protected JList createList(ComboBoxModel comboBoxModel) {
                      final JList list = super.createList(comboBoxModel);
                      SearchableUtils.installSearchable(list);
                      ConverterContext converterContext = null;
                    if (tableModel instanceof ContextSensitiveTableModel) {
                        converterContext = ((ContextSensitiveTableModel) tableModel).getConverterContextAt(0, columnIndex);
                      }
                      final FilterListCellRenderer cellRenderer = new FilterListCellRenderer(
                            objectGrouper != null ? objectGrouper.getType() : tableModel.getColumnClass(columnIndex),
                                                                                                                   objectGrouper != null ? objectGrouper.getConverterContext() : converterContext);
                      setRenderer(cellRenderer);
                      new ListSearchable(list) {
                          @Override
                          protected String convertElementToString(Object object) {
                              return cellRenderer.convertElementToString(Locale.getDefault(), object);
                          }
                      };

                      Filter[] filters = _filterableTableModel.getFilters(_filterableTableModelColumnIndex);
                      if (filters.length == 0) {
                          list.setSelectedIndex(0);
                      }
                      else if (filters[0] instanceof SingleValueFilter) {
                          SingleValueFilter filter = (SingleValueFilter) filters[0];
                          filter.setObjectGrouper(objectGrouper);
                          list.setSelectedValue(filter.getValue(), true);
                      }
                      else if (filters[0] instanceof DynamicTableFilter) {
                          list.setSelectedValue(filters[0], true);
                      }

                      return list;
                  }
              };
              listChooserPanel.setMaximumRowCount(8);
              listChooserPanel.setDefaultFocusComponent(listChooserPanel.getList());
              listChooserPanel.addItemListener(new ItemListener() {
                  public void itemStateChanged(ItemEvent e) {
                      if (e.getStateChange() == ItemEvent.SELECTED) {
                          Object object = e.getItem();
                          int index = comboBoxModel.getIndexOf(object);
                          if (index == 0) { // if there is only one value, no need to filter
                              applyFilter(null, _filterableTableModelColumnIndex);
                          }
                          else if (index >= 1 && index <= dynamicFilters.length) {
                              if (dynamicFilters[index - 1].initializeFilter(_actualTableModel, _actualTableModelColumnIndex, getPossibleValues())) {
                                  applyFilter(dynamicFilters[index - 1], _filterableTableModelColumnIndex);
                              }
                              else {
                                  applyFilter(null, _filterableTableModelColumnIndex);
                              }
                          }
                          else if (comboBoxModel.getSize() <= 2 + dynamicFilters.length) {
                              applyFilter(null, _filterableTableModelColumnIndex);
                          }
                          else {
                              SingleValueFilter filter = new SingleValueFilter(object);
                              if (objectGrouper != null) {
                                  filter.setObjectGrouper(objectGrouper);
                                  filter.setName(ObjectConverterManager.toString(object, objectGrouper.getType(), objectGrouper.getConverterContext()));
                              }
                              else {
                                  filter.setName(convertElementToString(object));
                              }

                              applyFilter(filter, _filterableTableModelColumnIndex);
                          }
                          hidePopup();
                          updateFilterIndicator();
                      }
                  }
              });
              popupPanel = listChooserPanel;
          }

          popupPanel.setResizable(true);
          popupPanel.setResizableCorners(Resizable.LOWER_RIGHT);
          return popupPanel;
      }

      /**
       * Converts the element from object to string.
       *
       * @param item the item
       * @return the string.
       */
      protected String convertElementToString(Object item) {
          if (_actualTableModel instanceof ContextSensitiveTableModel) {
              return ObjectConverterManager.toString(item,
                                                     ((ContextSensitiveTableModel) _actualTableModel).getCellClassAt(0, _tableColumnIndex),
                                                     ((ContextSensitiveTableModel) _actualTableModel).getConverterContextAt(0, _tableColumnIndex)); // have to use 0 as row index as we don't know which row the value is from
          }
          else {
              return "" + item;
          }
      }

      /**
       * calculate the popup location.
       *
       * @return the location of popup.
       */
      protected Point calculatePopupLocation() {
          Border border = _popup.getPopupBorder();
          int xOffset = 0;
          int yOffset = 0;
          if (border != null) {
              Insets borderInsets = border.getBorderInsets(_popupPanel);
              xOffset = borderInsets.left + borderInsets.right;
              yOffset = borderInsets.top + borderInsets.bottom;
          }
          Point p;
          try {
              p = getLocationOnScreen();
          }
          catch (IllegalComponentStateException e) {
              p = getLocation();
          }
          p.y += getHeight();
          Dimension size = _popupPanel.getPreferredSize();
          p.x -= size.width - getWidth() + xOffset;

          int bottom = p.y + size.height;

          if (!PortingUtils.isInitalizationThreadStarted() || PortingUtils.isInitializationThreadAlive()) {
              PortingUtils.initializeScreenArea();
              Rectangle easyBounds = PortingUtils.getLocalScreenBounds();
              // use a fast calculation to find out if it is possible out of bounds.
              if (bottom > easyBounds.y + easyBounds.height || p.x < easyBounds.x || p.x + size.width > easyBounds.x + easyBounds.width) {
                  Rectangle screenBounds = PortingUtils.getContainingScreenBounds(new Rectangle(p, size), true);
                  if (bottom > screenBounds.y + screenBounds.height) {
                      p.y = p.y - size.height - getHeight() - yOffset; // flip to upward
                  }

                  Rectangle bounds = PortingUtils.ensureOnScreen(new Rectangle(p, size));
                  p.x = bounds.x;
                  p.y = bounds.y;
              }
          }
          else {
              Rectangle screenBounds = PortingUtils.getContainingScreenBounds(new Rectangle(p, size), true);
              if (bottom > screenBounds.y + screenBounds.height) {
                  p.y = p.y - size.height - getHeight() - yOffset; // flip to upward
              }

              Rectangle bounds = PortingUtils.ensureOnScreen(new Rectangle(p, size));
              p.x = bounds.x;
              p.y = bounds.y;
          }

          return p;
      }

      /**
       * Causes the combo box to close its popup window.
       */
      public void hidePopup() {
          if (isPopupVisible()) {
              _popup.hidePopup();
          }
      }

      /**
       * Determines the visibility of the popup.
       *
       * @return true if the popup is visible, otherwise returns false
       */
      public boolean isPopupVisible() {
          return _popup != null && _popup.isPopupVisible();
      }

      /**
       * Gets the possible values of the specified columnIndex. The values are sorted.
       *
       * @param tableModel  the table model
       * @param columnIndex the column index.
       * @return the possible values of in the column.
       * @deprecated we no longer use this as a public api to calculate the possible values of the table model.
       *             The method is now at {@link FilterableTableModel#getPossibleValues(int,java.util.Comparator)}. We do so because different filterable table models
       *             can provide their own way to calculate the possible values for each column. For example, for FilterableTreeTableModel
       *             the possable values are from the actual Row object which may or may not be visible on the table.
       */
      public static Object[] calculatePossibleValues(TableModel tableModel, int columnIndex) {
          return calculatePossibleValues(tableModel, columnIndex, null);
      }

      /**
       * Gets the possible values of the specified columnIndex. The values are sorted using the comparator passed in as paraemter.
       *
       * @param tableModel  the table model
       * @param columnIndex the column index.
       * @param comparator  the comparator which is used to sort the possible values.
       * @return the possible values of in the column.
       * @deprecated we no longer use this as a public api to calculate the possible values of the table model.
       *             The method is now at {@link FilterableTableModel#getPossibleValues(int,java.util.Comparator)}. We do so because different filterable table models
       *             can provide their own way to calculate the possible values for each column. For example, for FilterableTreeTableModel
       *             the possable values are from the actual Row object which may or may not be visible on the table.
       */
      public static Object[] calculatePossibleValues(TableModel tableModel, int columnIndex, Comparator comparator) {
          Map cache = new HashMap();
          for (int i = 0; i < tableModel.getRowCount(); i++) {
              Object value = tableModel.getValueAt(i, columnIndex);
              if (value != null) {
                  cache.put(value, "");
              }
          }
          Set set = cache.keySet();
          Iterator iterator = set.iterator();
          Object[] objects = new Object[set.size()];
          int i = 0;
          while (iterator.hasNext()) {
              Object o = iterator.next();
              objects[i++] = o;
          }

          if (comparator != null) {
              Arrays.sort(objects, comparator);
          }
          return objects;
      }

      private List _dynamicFilters;

      private void initializeDynamicTableFilters() {
          if (_dynamicFilters == null) {
              _dynamicFilters = new ArrayList();
          }
      }

      /**
       * Add a <code>DynamicTableFilter</code>. <code>DynamicTableFilter</code> allows
       * to add your own customize filter to the drop down filter list. Any <code>DynamicTableFilter</code>
       * will become an entry in the list. If user clicks on that entry, the filter will be used to filter
       * the column. What's special about <code>DynamicTableFilter</code> is it allows to
       * to create a filter on fly. For example, in initializeFilter method of DynamicTableFilter, you can pop up
       * a dialog to allow user to select certain information and you return a filter based on user selection.
       * If returning null, no filter will be added. If not null, the filter you just created will be added to the
       * <code>FilterableTableModel</code>.
       *
       * @param filter a <code>DynamicTableFilter</code>.
       */
      public void addDynamicTableFilter(DynamicTableFilter filter) {
          initializeDynamicTableFilters();
          _dynamicFilters.add(filter);
      }

      /**
       * Removes a <code>DynamicTableFilter</code> which was added earlier.
       *
       * @param filter a <code>DynamicTableFilter</code>.
       */
      public void removeDynamicTableFilter(DynamicTableFilter filter) {
          initializeDynamicTableFilters();
          _dynamicFilters.remove(filter);
      }

      /**
       * Gets all the <code>DynamicTableFilter</code>s.
       *
       * @return an array of <code>DynamicTableFilter</code>s.
       */
      public DynamicTableFilter[] getDynamicTableFilters() {
          initializeDynamicTableFilters();
          return (DynamicTableFilter[]) _dynamicFilters.toArray(new DynamicTableFilter[_dynamicFilters.size()]);
      }

      private ThemePainter _painter;

      @Override
      public void updateUI() {
          super.updateUI();
          _painter = (ThemePainter) UIDefaultsLookup.get("Theme.painter");
      }

      @Override
      protected void paintComponent(Graphics g) {
          super.paintComponent(g);
          paintFilterIndicator(g);
      }

      /**
       * Paints the filter indicator. By default, we will paint a line above the filter list button to
       * indicate there is filter on this column.
       *
       * @param g the Graphics object
       */
      protected void paintFilterIndicator(Graphics g) {
      }

      /**
       * Checks if the filter name is visible on the box as part of the title.
       *
       * @return true or false.
       */
      public boolean isShowFilterName() {
          return _showFilterName;
      }

      /**
       * Sets the flag if the filter name is shown on the title.
       *
       * @param showFilterName true to show the filter name. False to not show it.
       */
      public void setShowFilterName(boolean showFilterName) {
          _showFilterName = showFilterName;
      }

      /**
       * Checks if the filter icon is visible on the box as part of the title.
       *
       * @return true or false.
       */
      public boolean isShowFilterIcon() {
          return _showFilterIcon;
      }

      /**
       * Sets the flag if the filter icon is shown on the title.
       *
       * @param showFilterIcon true to show the filter icon. False to not show it.
       */
      public void setShowFilterIcon(boolean showFilterIcon) {
          _showFilterIcon = showFilterIcon;
      }

      private Component _target;

      public Component getTarget() {
          return _target;
      }

      public void setTarget(Component target) {
          _target = target;
      }

      public void mouseClicked(MouseEvent e) {
      }

      public void mousePressed(MouseEvent e) {
          if (e.isPopupTrigger()) {
              JideSwingUtilities.retargetMouseEvent(e.getID(), e, getTarget());
          }
      }

      public void mouseReleased(MouseEvent e) {
          if (e.isPopupTrigger()) {
              JideSwingUtilities.retargetMouseEvent(e.getID(), e, getTarget());
          }
      }

      public void mouseEntered(MouseEvent e) {
      }

      public void mouseExited(MouseEvent e) {
      }

      /**
       * Checks if the <code>AutoFilterTableHeader</code> allows multiple values as the filter.
       * The difference will be to use a CheckBoxList or a regular JList as the popup panel when clicking
       * on the filter button.
       *
       * @return true or false.
       */
      public boolean isAllowMultipleValues() {
          return _allowMultipleValues;
      }

      /**
       * Set the flag if the <code>AutoFilterTableHeader</code> allows multiple values as the filter.
       * The difference will be to use a CheckBoxList or a regular JList as the popup panel when clicking
       * on the filter button.
       *
       * @param allowMultipleValues true to allow multiple value filters. False to disallow it. Default is false.
       */
      public void setAllowMultipleValues(boolean allowMultipleValues) {
          boolean old = _allowMultipleValues;
          if (old != allowMultipleValues) {
              _allowMultipleValues = allowMultipleValues;
          }
      }

      @Override
      public void setVerticalAlignment(int alignment) {
          super.setVerticalAlignment(alignment);
          if (_label != null) {
              _label.setVerticalAlignment(alignment);
          }
      }

      @Override
      public void setHorizontalAlignment(int alignment) {
          super.setHorizontalAlignment(alignment);
          if (_label != null) {
              _label.setHorizontalAlignment(alignment);
          }
      }
}