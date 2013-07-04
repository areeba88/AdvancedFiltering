/*
 * @(#)NotifyFilterableTableModel.java 3/4/2013
 *
 * Copyright 2002 - 2013 JIDE Software Inc. All rights reserved.
 */

import javax.swing.table.TableModel;

import com.jidesoft.grid.FilterableTableModel;
import com.jidesoft.grid.FilterableTableModelEvent;
import com.jidesoft.grid.FilterableTableModelListener;


/**
 * Represents a filterable table model which notifies its listeners defined by 
 * {@link #getFilterableTableModelListeners()} when its filter is being applied
 * via {@link FilterableTableModel#setFiltersApplied(boolean)}.
 */
public class NotifyFilterableTableModel extends FilterableTableModel {

	private static final long serialVersionUID = 1L;
	
	public static final int FILTERS_APPLIED_EVENT = 42;
	

	/**
	 * Create a new notifiying filterable table model.
	 * 
	 * @param model The table model.
	 */
	public NotifyFilterableTableModel(final TableModel model) {
		super(model);
	}

	/**
	 * Notify all filterable table model listeners that a filter event has occurred. 
	 */
	public void notifyFilterableTableModelListeners(final FilterableTableModelEvent event) {
		final FilterableTableModelListener[] listeners = getFilterableTableModelListeners();
		for (final FilterableTableModelListener listener : listeners) {
			listener.filterableTableModelChanged(event);
		}
	}


	/**
	 * Apply filters via {@link #setFiltersApplied(boolean)} and notify listeners.
	 * 
	 * @param apply true when filters should be applied, false otherwise.
	 * @param source The sender of the event.
	 */
	public void setFiltersApplied(final boolean apply, final Object source) {
		super.setFiltersApplied(apply);
		final FilterableTableModelEvent event = new FilterableTableModelEvent(source, FILTERS_APPLIED_EVENT);
		notifyFilterableTableModelListeners(event);
	}

}
