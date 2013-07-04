import java.util.Date;

import javax.swing.CellRendererPane;
import javax.swing.JComboBox;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;

import com.jidesoft.combobox.PopupPanel;
import com.jidesoft.converter.ConverterContext;
import com.jidesoft.filter.CustomFilterEditor;
import com.jidesoft.filter.Filter;
import com.jidesoft.filter.FilterFactoryManager;
import com.jidesoft.grid.FilterEditor;
import com.jidesoft.plaf.basic.BasicAutoFilterTableHeaderUIDelegate;

public class DelegateCustom extends BasicAutoFilterTableHeaderUIDelegate {
	public DelegateCustom(JTableHeader header, CellRendererPane rendererPane) {
		super(header, rendererPane);
	}
boolean isFilter =false;
	@Override
	protected FilterEditor createCustomFilterEditor(FilterFactoryManager filterFactoryManager, Class<?> type, ConverterContext converterContext, Object[] possibleValues) {
		
		isFilter = 	 JPopupAdvancedFilteringOnCustomData.DateSpecifFilterApplied;
		
		return new WESCustomFilterEditor(filterFactoryManager, type, converterContext, possibleValues) ;
	}

	@Override
	protected void applyFilter(Filter filter, int columnIndex) {
		super.applyFilter(filter, columnIndex);
	}

	public static PopupPanel popupPanel;

	public PopupPanel getPopupPanel() {
		return popupPanel;
	}

	public void setPopupPanel(PopupPanel _popupPanel) {
		this.popupPanel = _popupPanel;
	}

	@Override
	protected PopupPanel createPopupPanel(TableModel tableModel, int columnIndex, Object[] possibleValues) {


		PopupPanel	_popupPanel = super.createPopupPanel(tableModel, columnIndex, possibleValues);
		
		if(JPopupAdvancedFilteringOnCustomData.DateSpecifFilterApplied){
			//some how checked the DATE Filter checkbox
			//_popupPanel.getComponent(2)
		}
		this.popupPanel = _popupPanel;

		return _popupPanel;
	
	}
	
	
	
	
	class WESCustomFilterEditor extends CustomFilterEditor{


		private Class<?> type;
		public WESCustomFilterEditor(FilterFactoryManager filterFactoryManager, Class<?> type, ConverterContext converterContext, Object[] possibleValues) {

			super(filterFactoryManager, type, converterContext, possibleValues);

			this.type = type;

		}

		@Override
		public void setFilter(Filter filter) {

			//this will set the default operator in case of custom Filter opening  

			super.setFilter(filter);
			if (filter == null && this.type.equals(String.class)) {
				((JComboBox) _conditionComboBox).setSelectedIndex(9);
			}
			else  if (filter == null && this.type.equals(Integer.class)) {
				((JComboBox) _conditionComboBox).setSelectedIndex(11);
			}
			else  if (filter == null && this.type.equals(Date.class)){

				((JComboBox) _conditionComboBox).setSelectedIndex(10);
			}
		}

	}

}