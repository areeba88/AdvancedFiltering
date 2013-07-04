import javax.swing.table.TableModel;

import com.jidesoft.combobox.PopupPanel;
import com.jidesoft.grid.AbstractDynamicTableFilter;
import com.jidesoft.grid.AutoFilterBox;
import com.jidesoft.grid.Filter;

  public class CustomAutoFB extends AutoFilterBox
   {
   /**
       * Comment for <code>popUpPanel</code>
       */
      private PopupPanel popUpPanel;

      /**
       * Konstruktor
       */
      CustomAutoFB()
      {
         addFilter();   //our custom filter
         setShowFilterIcon(true);  
      }

      public Object[] getPossibleValues()
      {
         Object[] ret = new Object[] {}; 
         repaint();
         return ret; //only (all) and (custom...) filter 
      }

      /**
       *Add custom filter. This custom filter responds only be pressed when the button "Display".
       */
      private void addFilter()
      {     
         addDynamicTableFilter(new AbstractDynamicTableFilter()
         {   
            public boolean isValueFiltered(
                  Object value)
            {
               //delegate to a extrenal methode. our logic 
               return filterChanged.isValueFiltered(value);
            }

            public String getName()
            {
               return "(Benutzerdefiniert...)"; // equal (Custom...)
            }

            public boolean initializeFilter(
                  TableModel tableModel, int columnIndex, Object[] possibleValues)
            {
               ///delegate to a extrenal methode. our logic 
               return filterChanged.initializeFilter(tableModel, columnIndex, possibleValues);
            }
         });
      }

      /* (non-Javadoc)
       * @see com.jidesoft.grid.AutoFilterBox#applyFilter(com.jidesoft.grid.Filter, int)
       */
      public void applyFilter( 
            Filter filter, int columnIndex)
      { 
         super.applyFilter(filter, columnIndex);   

// !! I imagine, if I select a filter item (All) or (Custom) this methode will be called.
// But I'am wronge, it doesn't work. 
//I need this part to react by selection of (all)  ((Custom) react by AbstractDynamicTableFilter.initializeFilter(...))

         Object selValue = popUpPanel.getSelectedObject();
         //delegate to a extrenal methode. our logic 
         filterChanged.filterChanged(columnIndex, selValue);     
      }

      /* (non-Javadoc)
       * @see com.jidesoft.grid.AutoFilterBox#createPopupPanel(javax.swing.table.TableModel, int, java.lang.Object[])
       */
      protected PopupPanel createPopupPanel(
            TableModel arg0, int arg1, Object[] arg2)
      {
         popUpPanel = super.createPopupPanel(arg0, arg1, arg2);
         return popUpPanel;
      }
   }