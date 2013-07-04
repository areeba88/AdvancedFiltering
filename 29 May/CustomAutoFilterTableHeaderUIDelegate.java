import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.Locale;

import javax.swing.CellRendererPane;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;

import com.jidesoft.combobox.PopupPanel;
import com.jidesoft.filter.FilterFactory;
import com.jidesoft.filter.FilterFactoryManager;
import com.jidesoft.plaf.basic.BasicAutoFilterTableHeaderUIDelegate;
import com.jidesoft.swing.JideButton;
import com.jidesoft.swing.JideMenu;

public class CustomAutoFilterTableHeaderUIDelegate extends BasicAutoFilterTableHeaderUIDelegate {
	public CustomAutoFilterTableHeaderUIDelegate(JTableHeader header, CellRendererPane rendererPane) {
		super(header, rendererPane);
	}


	//private PopupPanel _popupPanel;
	@Override
	protected PopupPanel createPopupPanel(TableModel tableModel, int columnIndex, Object[] possibleValues) {
		PopupPanel	_popupPanel = super.createPopupPanel(tableModel, columnIndex, possibleValues);

		AdvancedFilteringOnCustomData oAdvancedFilteringOnCustomData = new   AdvancedFilteringOnCustomData();



		int ColiIndex = AdvancedFilteringOnCustomData._groupTableModel.findColumn("DateTime");

		if(columnIndex==ColiIndex) {
			AddingJMenuToJPanel dateFilteringMenu =new AddingJMenuToJPanel("DateFilters");


			JideMenu monthMenu =new  JideMenu("is in Month of");

			JMenuItem item ;

			for(FilterFactory filter : FilterFactoryManager.getDefaultInstance().getFilterFactories(Date.class)) {
				item = new JMenuItem(filter.getConditionString(Locale.getDefault()));

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
				item.addActionListener(new AdvancedFilteringOnCustomData().new DateFilterListener(item,AdvancedFilteringOnCustomData._groupTableModel));

			}
			dateFilteringMenu.add(monthMenu);


//Case#1
		_popupPanel.add(dateFilteringMenu,BorderLayout.NORTH);

//Case#2			
			JideButton btn = new JideButton("DateFilters");
			
			btn.addActionListener(new BtnListener(dateFilteringMenu));
		
		//	_popupPanel.add(btn,BorderLayout.NORTH);
		
		}		



		return _popupPanel;
	}
class BtnListener implements ActionListener{

	AddingJMenuToJPanel dateMenu;
	
	public BtnListener(AddingJMenuToJPanel dateMenu){
		
		this.dateMenu =dateMenu; 
	
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
	
		
		this.dateMenu.setVisible(true); 
		this.dateMenu.show();
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
