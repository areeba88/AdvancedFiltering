
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Date;
import java.util.Locale;

import javax.swing.Box;
import javax.swing.ButtonModel;
import javax.swing.CellRendererPane;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;

import com.jidesoft.combobox.PopupPanel;
import com.jidesoft.filter.FilterFactory;
import com.jidesoft.filter.FilterFactoryManager;
import com.jidesoft.icons.IconsFactory;
import com.jidesoft.plaf.basic.BasicGroupTableHeaderUIDelegate;
import com.jidesoft.swing.JideButton;
import com.jidesoft.swing.JideMenu;
import com.jidesoft.swing.JidePopupMenu;

public class JButtonCustomAutoFilterTableHeaderUIDelegate extends BasicGroupTableHeaderUIDelegate/*BasicAutoFilterTableHeaderUIDelegate*/ {
	public JButtonCustomAutoFilterTableHeaderUIDelegate(JTableHeader header, CellRendererPane rendererPane) {
		super(header, rendererPane);
	}


	//private PopupPanel _popupPanel;
	@Override
	protected PopupPanel createPopupPanel(TableModel tableModel, int columnIndex, Object[] possibleValues) {
		PopupPanel	_popupPanel = super.createPopupPanel(tableModel, columnIndex, possibleValues);

		JButtonAdvancedFilteringOnCustomData oAdvancedFilteringOnCustomData = new   JButtonAdvancedFilteringOnCustomData();



		int ColiIndex = JButtonAdvancedFilteringOnCustomData._groupTableModel.findColumn("DateTime");

		if(columnIndex==ColiIndex) {
			//	AddingJMenuToJPanel dateFilteringMenu =new AddingJMenuToJPanel("DateFilters");
			JidePopupMenu dateFilteringMenu =new JidePopupMenu("DateFilters");

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
				item.addActionListener(new JButtonAdvancedFilteringOnCustomData().new DateFilterListener(item,JButtonAdvancedFilteringOnCustomData._groupTableModel));

			}
			dateFilteringMenu.add(monthMenu);


			dateFilteringMenu.addFocusListener(new MenuFocusListener(dateFilteringMenu));

			final ImageIcon iconSubMenu = IconsFactory.getImageIcon(JButtonCustomAutoFilterTableHeaderUIDelegate.class, "icons/submenuArrow.gif");


			JideButton button = new JideButton();
			button.setLayout(new GridLayout());
			button.add(new JLabel("DateFilters"));
			button.setHorizontalAlignment(SwingConstants.RIGHT);
			button.setIcon(iconSubMenu); 
			button.setMargin(new Insets(3, 3, 3, 3));
			button.setRolloverEnabled(true);
			
			//disabling the Cick of Button
			button.getModel().setPressed(false);
			button.getModel().setArmed(false);
			button.getModel().setSelected(false);
			//button.addActionListener(new BtnListener(dateFilteringMenu));
			//	button.addFocusListener(new BtnFocusListener());

			button.getModel().addChangeListener(new BtnChangeListener(dateFilteringMenu,_popupPanel));
			
			
			JPanel dateFilterPanel=new JPanel();
			dateFilterPanel.setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			
			dateFilterCheckbox = new JCheckBox();
			dateFilterCheckbox.setBackground(Color.WHITE);
			
			
			if(JButtonAdvancedFilteringOnCustomData.isDateFilterApplied){
				
				dateFilterCheckbox.setEnabled(true);
				dateFilterCheckbox.setSelected(true);
				
			}else{
				dateFilterCheckbox.setEnabled(false);
				dateFilterCheckbox.setSelected(false);
			}
			
			
			
			
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 0;
			dateFilterPanel.add(dateFilterCheckbox, c);
			
			dateFilterPanel.setBackground(Color.WHITE);

			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 0.5;
			c.gridx = 1;
			c.gridy = 0;
			dateFilterPanel.add(button, c);
			
		_popupPanel.add(dateFilterPanel,BorderLayout.NORTH);

		}		

		return _popupPanel;
	}
	
	
	

	public static JCheckBox dateFilterCheckbox=null;
	
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
		JidePopupMenu dateFilteringMenu; 
		//AddingJMenuToJPanel dateFilteringMenu;
		public MenuFocusListener(JidePopupMenu dateFilteringMenu){

			this.dateFilteringMenu = dateFilteringMenu;

		} 
		@Override
		public void focusGained(FocusEvent e) {
			//		dateFilteringMenu.setFocusable(true);
			//dateFilteringMenu.show();

			//dateFilteringMenu.setVisible(true);
			//dateFilteringMenu.setRequestFocusEnabled(true);
			 		System.out.println("Focus Gained");
		}

		@Override
		public void focusLost(FocusEvent e) {
		//	dateFilteringMenu.setFocusable(false);
		//	dateFilteringMenu.hide();
		//	dateFilteringMenu.setVisible(false);

			System.out.println("Focus Lost");
		}


	}

	class BtnChangeListener implements ChangeListener{

		JidePopupMenu dateFilteringMenu;
		PopupPanel _popupPanel;
		public BtnChangeListener(JidePopupMenu dateFilteringMenu,PopupPanel _popupPanel){

			this.dateFilteringMenu = dateFilteringMenu;
			this._popupPanel = _popupPanel; 
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			ButtonModel model = (ButtonModel) e.getSource();


			if (model.isRollover()) {


				//((CheckBoxListPanel)((JViewport)((JideScrollPane)_popupPanel.getComponent(0)).getComponent(0)).getComponent(0)).toString();


//				dateFilteringMenu.show(JButtonAdvancedFilteringOnCustomData._table,682 ,75);  
//				dateFilteringMenu.setLocation(682 ,75);
//				dateFilteringMenu.setVisible(true);
				//dateFilteringMenu.setRequestFocusEnabled(true);
				//handling visibility of JPopupMneu regarding movement of of Grid

				Point p = JButtonAdvancedFilteringOnCustomData.parentPane.getLocationOnScreen();
				int x = (int) p.getX();
				int y = (int) p.getY();
				dateFilteringMenu.show(JButtonAdvancedFilteringOnCustomData._table,x+550 ,y+30);  
				dateFilteringMenu.setVisible(true);
				dateFilteringMenu.setLocation(x+678 ,y+60);

			} else {

				if(dateFilteringMenu.isVisible())
				{

					//if(!dateFilteringMenu.isFocusOwner()){
					//open these lines
					//	dateFilteringMenu.hide();
					//	dateFilteringMenu.setVisible(false);
					// }
				}
			}
			/*if (model.isPressed()) {

				Point p = JButtonAdvancedFilteringOnCustomData.parentPane.getLocationOnScreen();
				int x = (int) p.getX();
				int y = (int) p.getY();
				dateFilteringMenu.show(JButtonAdvancedFilteringOnCustomData._table,x+550 ,y+30);  
				dateFilteringMenu.setVisible(true);
				dateFilteringMenu.setLocation(x+550 ,y+30);



			}*/
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

