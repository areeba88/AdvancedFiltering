/*
 * @(#)StringSetFilter.java 3/4/2013
 *
 * Copyright 2002 - 2013 JIDE Software Inc. All rights reserved.
 */

import java.util.Locale;
import java.util.ResourceBundle;

import com.jidesoft.grid.AbstractTableFilter;
import com.jidesoft.grid.FilterEvent;
import com.jidesoft.grid.GridResource;
import org.apache.commons.lang3.ArrayUtils;


/**
 * Represents a filter to filter the field content of a pivot table by the selected
 * items of the possible values.
 */
public class StringSetFilter extends AbstractTableFilter<Object> {
	private static final long serialVersionUID = 1L;
	
	
	private Object[] selectedPossibleValues = ArrayUtils.EMPTY_OBJECT_ARRAY;
	
	private Object[] possibleValues = ArrayUtils.EMPTY_OBJECT_ARRAY;
	
	/**
	 * Create a new String set filter.
	 */
	public StringSetFilter() {
	}
	


	@Override
	public boolean isValueFiltered(final Object value) {
		
		final Object[] selected = getSelectedPossibleValues();
		
        if (selected == null) {
            return false;
        }
        final boolean empty = selected.length <= 0;
        final boolean complete = selected.length >= possibleValues.length;
        final boolean all;
        if (selected.length == 1) {
        	final Locale locale = Locale.getDefault();
			final ResourceBundle bundle = GridResource.getResourceBundle(locale);
			final String filterAll = bundle.getString("Filter.all");
        	final String first = (String) selected[0];
            all = filterAll.equals(first);
        }
        else {
            all = false;
        }
        if (empty || complete || all) {
            return false;
        }
        return contains(value, selected);
	}

	/**
	 * Check wether the String value contains one of selectedPossibleValues.
	 * 
	 * @param value The value.
	 * @param possibleValues The possible values.
	 * @return true if value contains one of selectedPossibleValues,
	 * 		   false otherwise.
	 */
	protected boolean contains(final Object value, final Object[] possibleValues)
			throws IllegalStateException {
		boolean found;
		try {
			final String v = (String) value;
			found = false;
			for (Object possibleValue : possibleValues) {
				final String selected = (String) possibleValue;
				final boolean selectedFound = v.contains(selected);
				found = found || selectedFound;
			}
		} 
		catch (final Exception e) {
			throw new IllegalStateException("isValueFiltered(): Error during filtering.", e);
		}
		return !found;
	}


	public Object[] getPossibleValues() {
		if (ArrayUtils.isEmpty(possibleValues)) {
			return possibleValues;
		}
		else {
			return possibleValues.clone();
		}
	}


	public void setPossibleValues(final Object[] possibleValues) {
		if (ArrayUtils.isEmpty(possibleValues)) {
			this.possibleValues = ArrayUtils.EMPTY_OBJECT_ARRAY;
		}
		else {
			this.possibleValues = possibleValues.clone();
		}
		final FilterEvent event = new FilterEvent(this, FilterEvent.FILTER_CONTENT_CHANGED);
		this.fireFilterChanged(event);
	}


	public Object[] getSelectedPossibleValues() {
		if (ArrayUtils.isEmpty(selectedPossibleValues)) {
			return selectedPossibleValues;
		}
		else {
			return selectedPossibleValues.clone();
		}
	}


	public void setSelectedPossibleValues(final Object[] selectedPossibleValues) {
		if (ArrayUtils.isEmpty(selectedPossibleValues)) {
			this.selectedPossibleValues = ArrayUtils.EMPTY_OBJECT_ARRAY;
		}
		else {
			this.selectedPossibleValues = selectedPossibleValues.clone();
		}
		final FilterEvent event = new FilterEvent(this, FilterEvent.FILTER_CONTENT_CHANGED);
		this.fireFilterChanged(event);
	}
}
