

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Date;
import java.util.Locale;

import javax.swing.ButtonModel;
import javax.swing.CellRendererPane;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;

import com.jidesoft.combobox.PopupPanel;
import com.jidesoft.converter.ConverterContext;
import com.jidesoft.filter.CustomFilterEditor;
import com.jidesoft.filter.FilterFactory;
import com.jidesoft.filter.FilterFactoryManager;
import com.jidesoft.grid.FilterableTableModel;
import com.jidesoft.icons.IconsFactory;
import com.jidesoft.plaf.basic.BasicAutoFilterTableHeaderUIDelegate;
import com.jidesoft.swing.JideMenu;

public class JMenuCustomAutoFilterTableHeaderUIDelegate extends BasicAutoFilterTableHeaderUIDelegate {
	public JMenuCustomAutoFilterTableHeaderUIDelegate(JTableHeader header, CellRendererPane rendererPane) {
		super(header, rendererPane);
	}


	@Override
	protected  CustomFilterEditor createCustomFilterEditor(FilterFactoryManager filterFactoryManager, Class<?> type, ConverterContext converterContext, Object[] possibleValues) {

		System.out.println("Custom Editor");
		return (CustomFilterEditor) super.createCustomFilterEditor(filterFactoryManager, type, converterContext, possibleValues);
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

		JMenuAdvancedFilteringOnCustomData oJMenuAdvancedFilteringOnCustomData = new   JMenuAdvancedFilteringOnCustomData();



		int ColiIndex = JMenuAdvancedFilteringOnCustomData._groupTableModel.findColumn("DateTime");

		if(columnIndex==ColiIndex) {
			JMenuToJPanel dateFilteringMenu =new JMenuToJPanel("DateFilters");
			
			/*if(((FilterableTableModel)tableModel).hasFilter()){
				
				((JMenu)dateFilteringMenu.getComponent(0)).setIcon(
						IconsFactory.getImageIcon(JMenuAdvancedFilteringOnCustomData.class,
								"icons/basic-icon-box-checked.png"));
			}*/
//				((JMenu)((JMenuToJPanel)JMenuCustomAutoFilterTableHeaderUIDelegate.popupPanel.getComponent(2)).
//						getComponent(0)).setIcon(IconsFactory.getImageIcon(JMenuAdvancedFilteringOnCustomData.class,
//								"icons/basic-icon-box-checked.png"));
			
			
			
			
			JideMenu monthMenu =new  JideMenu("is in Month of");

			JMenuItem item ;

			for(FilterFactory filter : FilterFactoryManager.getDefaultInstance().getFilterFactories(Date.class)) {
				item = new JMenuItem(filter.getConditionString(Locale.getDefault()));
				//System.out.println(""+filter.getConditionString(Locale.getDefault()));
				if(item.getActionCommand().startsWith("on Jan")||  //all theses Filters were belonging to the Date Type Month's Category so are being collected in one menu  
						item.getActionCommand().startsWith("on Feb")||
						item.getActionCommand().startsWith("on March")||
						item.getActionCommand().startsWith("on Ap")||
						item.getActionCommand().startsWith("on May")||
						item.getActionCommand().startsWith("on Jun")||
						item.getActionCommand().startsWith("on Jul")||   
						item.getActionCommand().startsWith("on Aug")||
						item.getActionCommand().startsWith("on Sep")||
						item.getActionCommand().startsWith("on Oct")||
						item.getActionCommand().startsWith("on Nov")||
						item.getActionCommand().startsWith("on Dec")){

					monthMenu.add(item);

				}else if(!(item.getActionCommand().equals("is at anytime")||  //all these Filters were supporting the String Data Type so they are being removed
						item.getActionCommand().equals("is on")||
						item.getActionCommand().equals("is in")||
						item.getActionCommand().equals("isn't in")||
						item.getActionCommand().equals("doesn't exist")||
						item.getActionCommand().equals("exist")||
						item.getActionCommand().equals("is after")||
						item.getActionCommand().equals("is on or after")||
						item.getActionCommand().equals("is before")||
						item.getActionCommand().equals("is on or before")||
						item.getActionCommand().equals("is between")||
						item.getActionCommand().equals("doesn't equal")||
						item.getActionCommand().equals("is not between")
						))

				{

					dateFilteringMenu.add(item);
				}		
				item.addActionListener(new JMenuAdvancedFilteringOnCustomData().new DateFilterListener(item,JMenuAdvancedFilteringOnCustomData._groupTableModel));

			}
			dateFilteringMenu.add(monthMenu);


			dateFilteringMenu.addFocusListener(new MenuFocusListener(dateFilteringMenu));

			//Case#1
			_popupPanel.add(dateFilteringMenu,BorderLayout.NORTH);



		}		

		this.popupPanel = _popupPanel;

		return _popupPanel;
	}


	class BtnFocusListener implements FocusListener {

		@Override
		public void focusGained(FocusEvent arg0) {
			// TODO Auto-generated method stub
			System.out.println("Btn F gaing");	
		}

		@Override
		public void focusLost(FocusEvent arg0) {
			// TODO Auto-generated method stub
			System.out.println("Btn F OUTT");		
		}


	}


	class MenuFocusListener implements FocusListener {
		//	JidePopupMenu dateFilteringMenu; 
		JMenuToJPanel dateFilteringMenu;
		public MenuFocusListener(JMenuToJPanel/*JidePopupMenu*/ dateFilteringMenu){

			this.dateFilteringMenu = dateFilteringMenu;

		} 
		@Override
		public void focusGained(FocusEvent e) {
			/*		dateFilteringMenu.setFocusable(true);
			dateFilteringMenu.show();

			dateFilteringMenu.setVisible(true);
			dateFilteringMenu.setRequestFocusEnabled(true);
			 */		System.out.println("Focus Gained");
		}

		@Override
		public void focusLost(FocusEvent e) {
			/*dateFilteringMenu.setFocusable(false);
			dateFilteringMenu.hide();
			dateFilteringMenu.setVisible(false);*/

			System.out.println("Focus Lost");
		}


	}

	class BtnChangeListener implements ChangeListener{

		JMenuToJPanel dateFilteringMenu;

		//	JidePopupMenu dateFilteringMenu;
		PopupPanel _popupPanel;
		public BtnChangeListener(JMenuToJPanel/*JidePopupMenu*/ dateFilteringMenu,PopupPanel _popupPanel){

			this.dateFilteringMenu = dateFilteringMenu;
			this._popupPanel = _popupPanel; 
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			ButtonModel model = (ButtonModel) e.getSource();


			if (model.isRollover()) {


				//((CheckBoxListPanel)((JViewport)((JideScrollPane)_popupPanel.getComponent(0)).getComponent(0)).getComponent(0)).toString();


				dateFilteringMenu.show();  
				dateFilteringMenu.setLocation(682 ,75);
				dateFilteringMenu.setVisible(true);
				//dateFilteringMenu.setRequestFocusEnabled(true);


			} else {

				if(dateFilteringMenu.isVisible())
				{

					//if(!dateFilteringMenu.isFocusOwner()){
					//open these lines
					dateFilteringMenu.hide();
					dateFilteringMenu.setVisible(false);
					// }
				}
			}
			if (model.isPressed()) {




			}
		}

	}




	class BtnListener implements ActionListener{

		JMenuToJPanel dateMenu;

		public BtnListener(JMenuToJPanel dateMenu){

			this.dateMenu =dateMenu; 

		}

		@Override
		public void actionPerformed(ActionEvent e) {
			//System.out.println("tttttttttttttttttttt");
			/*JPopupMenu pop=  new JPopupMenu("JHJ");
	pop.add(new JMenuItem("dddddddd"));
	pop.show(true);*/
			///this.dateMenu.setVisible(true); 
			//this.dateMenu.show();
		}




	}
	/* @Override
	    protected Point calculatePopupLocation() {
	        Point point = super.calculatePopupLocation();
	        Dimension preferredSize = _popupPanel.getPreferredSize();
	        if (_popupPanel != null && preferredSize.width > 100) {
	            _popupPanel.setPreferredSize(new Dimension(100, preferredSize.height));
	            point.x += preferredSize.width - 100;
	        }
	        return point;
	    }*/

}
