/*
 * @(#)FilterFieldBox.java 3/4/2013
 *
 * Copyright 2002 - 2013 JIDE Software Inc. All rights reserved.
 */

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

//import org.apache.commons.lang3.ArrayUtils;
//import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import com.jidesoft.combobox.CheckBoxListChooserPanel;
import com.jidesoft.combobox.PopupPanel;
import com.jidesoft.filter.Filter;
import com.jidesoft.pivot.FieldBox;
import com.jidesoft.pivot.PivotField;
import com.jidesoft.pivot.PivotTablePane;

/**
 * Represents a field box to filter planning types.
 *
 * @author x1jfransson
 */
public class FilterFieldBox extends FieldBox {

    private static final long serialVersionUID = 1L;


    private static final Log log = LogFactory.getLog(FilterFieldBox.class);


    private CheckBoxListChooserPanel chooserPanel = null;


    private NotifyFilterableTableModel filteredModel = null;


    /**
     * Create a new field box to filter planning types.
     *
     * @param field               The field.
     * @param sortArrowVisible    true, if the sort arrow should be shown, false otherwise.
     * @param filterButtonVisible true, if the filter button should be shown, false otherwise.
     */
    public FilterFieldBox(PivotField field, boolean sortArrowVisible,
                          boolean filterButtonVisible, NotifyFilterableTableModel filteredModel) {
        super(field, sortArrowVisible, filterButtonVisible);
        this.filteredModel = filteredModel;
    }

    private boolean _creatingPopupPanel = false;

    @Override
    protected PopupPanel createPopupPanel(PivotTablePane pivotTablePane, PivotField field, Object[] possibleValues) {
        _creatingPopupPanel = true;
        PopupPanel popupPanel;
        try {
            popupPanel = super.createPopupPanel(pivotTablePane, field, possibleValues);
        } finally {
            _creatingPopupPanel = false;
        }
        if (popupPanel instanceof CheckBoxListChooserPanel) {
            chooserPanel = (CheckBoxListChooserPanel) popupPanel;
            final Action okAction = chooserPanel.getOkAction();
            chooserPanel.setOkAction(new AbstractAction() {
                private static final long serialVersionUID = 1L;

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (okAction != null) {
                        okAction.actionPerformed(e);
                    }
                    applyFilter();
                }
            });
        }
        return popupPanel;
    }


    /**
     * Apply the filter to the filtered model.
     */
    public void applyFilter() {
        if (filteredModel != null) {
            final int count = filteredModel.getColumnCount();
            final String fieldName = getField().getName();
            for (int i = 0; i < count; i++) {
                final Filter<?>[] filters = filteredModel.getFilters(i);
                for (final Filter<?> filter : filters) {
                    if (StringUtils.equals(fieldName, filter.getName())) {
                        if (filter instanceof StringSetFilter) {
                            StringSetFilter f = (StringSetFilter) filter;
                            f.setSelectedPossibleValues(getSelectedPossibleValues());
                        }
                    }
                }
            }
            filteredModel.setFiltersApplied(true, this);
            if (getSelectedPossibleValues() == null) {
                setSelectedPossibleValues(new Object[] {"Peter"});
            }
            setSelectedPossibleValues(null);
        }
    }


    @Override
    public Object[] getSelectedPossibleValues() {
        if (_creatingPopupPanel) {
            if (filteredModel != null) {
                final int count = filteredModel.getColumnCount();
                final String fieldName = getField().getName();
                for (int i = 0; i < count; i++) {
                    final Filter<?>[] filters = filteredModel.getFilters(i);
                    for (final Filter<?> filter : filters) {
                        if (StringUtils.equals(fieldName, filter.getName())) {
                            if (filter instanceof StringSetFilter) {
                                Object[] possibleValues = ((StringSetFilter) filter).getSelectedPossibleValues();
                                return possibleValues.length == 0 ? null : possibleValues;
                            }
                        }
                    }
                }
            }
        }
        return super.getSelectedPossibleValues();
/*
		final Object[] values;
		if (chooserPanel != null) {
			final Object[] selectedObjects = chooserPanel.getSelectedObjects();
			final Object[] selectedObject = (Object[]) selectedObjects[0];
			values = selectedObject;
		}
		else if (super.getSelectedPossibleValues() != null) {
			values = super.getSelectedPossibleValues();
		}
        else {
            values = getField().getFilteredPossibleValues();
        }
		
		log.info("getSelectedPossibleValues(): " + ArrayUtils.toString(values));
		log.info("getPossibleValues(): " + ArrayUtils.toString(getPossibleValues()));
		return values;
*/
    }
}


