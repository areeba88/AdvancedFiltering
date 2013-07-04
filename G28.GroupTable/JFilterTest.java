/*
 * @(#)JFilterTest.java 7/20/2012
 *
 * Copyright 2002 - 2012 JIDE Software Inc. All rights reserved.
 */

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;

import com.jidesoft.converter.ConverterContext;
import com.jidesoft.converter.DefaultArrayConverter;
import com.jidesoft.converter.ObjectConverterManager;
import com.jidesoft.filter.AbstractFilter;
import com.jidesoft.filter.CustomFilterEditor;
import com.jidesoft.filter.Filter;
import com.jidesoft.filter.FilterFactory;
import com.jidesoft.filter.FilterFactoryManager;
import com.jidesoft.filter.InFilter;
import com.jidesoft.filter.LikeFilter;
import com.jidesoft.filter.NotEqualFilter;
import com.jidesoft.filter.WildcardFilter;
import com.jidesoft.grid.AutoFilterBox;
import com.jidesoft.grid.AutoFilterTableHeader;
import com.jidesoft.grid.AutoFilterTableHeaderEditor;
import com.jidesoft.grid.CellRendererManager;
import com.jidesoft.grid.ContextSensitiveTableModel;
import com.jidesoft.grid.EditorContext;
import com.jidesoft.grid.SortableTable;
import com.jidesoft.grid.SortableTableModel;
import com.jidesoft.plaf.LookAndFeelFactory;

public class JFilterTest {

	/**
	 * A charge has a charge detail record and the other information such as the date and time, qty etc.
	 */
	class Charge {

		private ChargeDefinition charge;
		private Date startDate;
		private Date endDate;
		private String orderNo;
		private Integer qty;
		private BigDecimal totalAmount;
		private BigDecimal totalTax;

		public Charge(ChargeDefinition charge, Date startDate, Date endDate, String orderNo, Integer qty,
				BigDecimal totalAmount, BigDecimal totalTax) {
			this.charge = charge;
			this.startDate = startDate;
			this.endDate = endDate;
			this.orderNo = orderNo;
			this.qty = qty;
			this.totalAmount = totalAmount;
			this.totalTax = totalTax;
		}

		public ChargeDefinition getChargeDefn() {
			return charge;
		}

		public Date getEndDate() {
			return endDate;
		}

		public String getOrderNo() {
			return orderNo;
		}

		public Integer getQty() {
			return qty;
		}

		public Date getStartDate() {
			return startDate;
		}

		public BigDecimal getTotalAmount() {
			return totalAmount;
		}

		public BigDecimal getTotalTax() {
			return totalTax;
		}

		public void setCharge(ChargeDefinition charge) {
			this.charge = charge;
		}

		public void setEndDate(Date endDate) {
			this.endDate = endDate;
		}

		public void setOrderNo(String orderNo) {
			this.orderNo = orderNo;
		}

		public void setQty(Integer qty) {
			this.qty = qty;
		}

		public void setStartDate(Date startDate) {
			this.startDate = startDate;
		}

		public void setTotalAmount(BigDecimal totalAmount) {
			this.totalAmount = totalAmount;
		}

		public void setTotalTax(BigDecimal totalTax) {
			this.totalTax = totalTax;
		}

	}

	/**
	 * Inner class that provides a renderer for the resolution summary table
	 */
	private class ChargesRowCellRenderer extends DefaultTableCellRenderer {

		private static final long serialVersionUID = -6286986203103208447L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
				boolean hasFocus, int row, int column) {
			Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if (isSelected) {
				comp.setForeground(table.getSelectionForeground());
				comp.setBackground(table.getSelectionBackground());
			} else {
				if (value instanceof ChargeDefinition) {
					ChargeDefinition definition = (ChargeDefinition) value;
					((JLabel) comp).setText(definition.toString());
				}
			}
			return comp;
		}
	}

	/**
	 * Displays a list of charges for a patient
	 */
	class ChargesTable extends JPanel {

		private static final long serialVersionUID = 4273482660171590856L;
		private ChargesTableModel tm;
		private SortableTable table;
		private SortableTableModel sm;
		private JScrollPane sp;

		ChargesTable() {
			CellRendererManager.registerRenderer(ChargeDefinition.class, new ChargesRowCellRenderer(), CHARGE_CONTEXT);
			ObjectConverterManager.registerConverter(ChargeDefinition[].class, new DefaultArrayConverter("; ",
					ChargeDefinition.class));

			unregisterDateFilters();

			setFilterFactories();
			setLayout(new BorderLayout());
			tm = new ChargesTableModel();
			tm.setData(generateSomeChargeData());
			sm = new SortableTableModel(tm);
			table = new SortableTable();
			table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			table.setPreferredScrollableViewportSize(new Dimension(400, 200));
			table.setModel(sm);
			// create the auto column filter header
			AutoFilterTableHeader filterHeader = new AutoFilterTableHeader(table);

			filterHeader.setShowFilterIcon(false);
			filterHeader.setAutoFilterEnabled(true);
			filterHeader.setUseNativeHeaderRenderer(true);
			filterHeader.setAllowMultipleValues(true);
			filterHeader.setFont(getFont().deriveFont(Font.BOLD));

			table.setTableHeader(filterHeader);
			sp = new JScrollPane(table);
			sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			add(sp, BorderLayout.CENTER);
		}

	}

	/**
	 * The charges table model used by the charge resolution JTable
	 */
	private class ChargesTableModel extends AbstractTableModel implements ContextSensitiveTableModel {

		private static final long serialVersionUID = -9115495374090498176L;

		private static final int CODE_COLUMN = 0;
		private static final int START_DATE_COLUMN = 1;
		private static final int END_DATE_COLUMN = 2;
		private static final int ORDER_NUMBER_COLUMN = 3;
		private static final int QTY_COLUMN = 4;
		private static final int TOTAL_AMOUNT_COLUMN = 5;
		private static final int TOTAL_TAX_COLUMN = 6;

		String[] columnNames = new String[7];
		Class<?>[] columnTypes = new Class<?>[7];

		List<Charge> data = new ArrayList<Charge>();

		public ChargesTableModel() {
			int i = 0;
			columnNames[i] = "Code";
			columnTypes[i++] = ChargeDefinition.class;

			columnNames[i] = "Start_Date";
			columnTypes[i++] = Date.class;

			columnNames[i] = "End_Date";
			columnTypes[i++] = Date.class;

			columnNames[i] = "Order Number";
			columnTypes[i++] = String.class;

			columnNames[i] = "Quantity";
			columnTypes[i++] = Integer.class;

			columnNames[i] = "Total Amount";
			columnTypes[i++] = BigDecimal.class;

			columnNames[i] = "Total Tax";
			columnTypes[i++] = BigDecimal.class;

		}

		public Class<?> getCellClassAt(int row, int col) {
			return columnTypes[col];
		}

		@Override
		public Class<?> getColumnClass(int col) {
			return columnTypes[col];
		}

		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public String getColumnName(int col) {
			return columnNames[col];
		}

		/**
		 * Not implemented
		 */
		public ConverterContext getConverterContextAt(int row, int col) {
			return null;
		}

		/**
		 * Part of the context sensitive table model interface
		 */
		public EditorContext getEditorContextAt(int row, int col) {
			return CHARGE_CONTEXT;
		}

		public int getRowCount() {
			return data.size();
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			Charge charge = data.get(rowIndex);
			switch (columnIndex) {
			case CODE_COLUMN:
				return charge.getChargeDefn();
			case START_DATE_COLUMN:
				return charge.getStartDate();
			case END_DATE_COLUMN:
				return charge.getEndDate();
			case ORDER_NUMBER_COLUMN:
				return charge.getOrderNo();
			case QTY_COLUMN:
				return charge.getQty();
			case TOTAL_AMOUNT_COLUMN:
				return charge.getTotalAmount();
			case TOTAL_TAX_COLUMN:
				return charge.getTotalTax();
			}
			throw new RuntimeException("Invalid row " + rowIndex + " and column " + columnIndex);
		}

		@Override
		public boolean isCellEditable(int row, int col) {
			return false;
		}

		/*
		 * Don't need to implement this method unless your table's editable.
		 */

		/**
		 * Initialise table model with charges returned from server
		 * 
		 * @param l
		 *            List of charge beans
		 */
		private void setData(List<Charge> l) {
			data = l;
			fireTableDataChanged();
		}

	}

	public static void main(String[] args) {
		new JFilterTest();
	}

	private EditorContext CHARGE_CONTEXT = new EditorContext("psd.billing.charge.context");

	public JFilterTest() {
        LookAndFeelFactory.installDefaultLookAndFeelAndExtension();
        UIManager.getDefaults().put("TableHeader.autoFilterTableHeaderUIDelegate", CustomAutoFilterTableHeaderUIDelegate.class.getName());
		JFrame frame = new JFrame("Filter Demo");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(new ChargesTable(), BorderLayout.CENTER);
		frame.setSize(800, 200);
		frame.setVisible(true);
	}

	/**
	 * Create a charge instance
	 */
	private Charge createCharge(Long encounterId, Long chargeId, String chargeCode, String chargeCodeDesc,
			Date startDate, Date endDate, String orderNo, Integer quantity, BigDecimal totalAmount, BigDecimal totalTax) {
		ChargeDefinition chargeDef = new ChargeDefinition(encounterId, chargeId, chargeCode, chargeCodeDesc);
		return new Charge(chargeDef, startDate, endDate, orderNo, quantity, totalAmount, totalTax);
	}

	private List<Charge> generateSomeChargeData() {
		List<Charge> charges = new ArrayList<Charge>();
		charges.add(createCharge(new Long(1), new Long(1), "Code_1", "Desc_1", new Date(), new Date(), "Order1", 1,
				new BigDecimal("100.00"), new BigDecimal("10.00")));
		charges.add(createCharge(new Long(1), new Long(1), "Code_2", "Desc_2", new Date(), new Date(), "Order2", 1,
				new BigDecimal("200.00"), new BigDecimal("20.00")));
		charges.add(createCharge(new Long(1), new Long(1), "Code_3", "Desc_3", new Date(), new Date(), "Order3", 1,
				new BigDecimal("300.00"), new BigDecimal("30.00")));
		charges.add(createCharge(new Long(1), new Long(1), "Code_4", "Desc_4", new Date(), new Date(), "Order4", 1,
				new BigDecimal("400.00"), new BigDecimal("40.00")));
		charges.add(createCharge(new Long(1), new Long(1), "Drugs_1", "Drugs_1", new Date(), new Date(), "Order5", 1,
				new BigDecimal("500.00"), new BigDecimal("50.00")));
		charges.add(createCharge(new Long(1), new Long(1), "Drugs_2", "Drugs_2", new Date(), new Date(), "Order6", 1,
				new BigDecimal("600.00"), new BigDecimal("60.00")));
		charges.add(createCharge(new Long(1), new Long(1), "Asprin", "Headache", new Date(), new Date(), "Order7", 1,
				new BigDecimal("700.00"), new BigDecimal("70.00")));
		charges.add(createCharge(new Long(1), new Long(1), "Amputation", "Limb removal", new Date(), new Date(),
				"Order8", 1, new BigDecimal("800.00"), new BigDecimal("80.00")));
		charges.add(createCharge(new Long(1), new Long(1), "Blood Trans", "Bloods", new Date(), new Date(), "Order9",
				1, new BigDecimal("900.00"), new BigDecimal("90.00")));
		charges.add(createCharge(new Long(1), new Long(1), "blah_1", "blah_1", new Date(), new Date(), "Order10", 1,
				new BigDecimal("1000.00"), new BigDecimal("010.00")));
		charges.add(createCharge(new Long(1), new Long(1), "more_blah_1", "more_blah_1", new Date(), new Date(),
				"Order11", 1, new BigDecimal("1100.00"), new BigDecimal("110.00")));
		charges.add(createCharge(new Long(1), new Long(1), "foo_1", "foo_1", new Date(), new Date(), "Order12", 1,
				new BigDecimal("1200.00"), new BigDecimal("120.00")));
		charges.add(createCharge(new Long(1), new Long(1), "foo_2", "foo_2", new Date(), new Date(), "Order13", 1,
				new BigDecimal("1300.00"), new BigDecimal("130.00")));
		return charges;
	}

	/**
	 * Register filter factories
	 */
	private void setFilterFactories() {

		// begins with
		FilterFactoryManager.getDefaultInstance().registerFilterFactory(ChargeDefinition.class, new FilterFactory() {

			public Filter<String> createFilter(Object... objects) {
				String input = ObjectConverterManager.toString(objects[0], ChargeDefinition.class);
				WildcardFilter<String> beginWith = new WildcardFilter<String>(input);
				beginWith.setBeginWith(true);
				beginWith.setEndWith(false);
				return beginWith;
			}

			public String getConditionString(Locale locale) {
				return AbstractFilter.getConditionString(locale, "string", "beginWith");
			}

			public Class[] getExpectedDataTypes() {
				return new Class[] { ChargeDefinition.class };
			}

			public String getName() {
				return "beginWith";
			}
		});

		// ends with
		FilterFactoryManager.getDefaultInstance().registerFilterFactory(ChargeDefinition.class, new FilterFactory() {

			public Filter<String> createFilter(Object... objects) {
				String input = ObjectConverterManager.toString(objects[0], ChargeDefinition.class);
				WildcardFilter<String> endsWith = new WildcardFilter<String>(input);
				endsWith.setBeginWith(false);
				endsWith.setEndWith(true);
				return endsWith;
			}

			public String getConditionString(Locale locale) {
				return AbstractFilter.getConditionString(locale, "string", "endWith");
			}

			public Class[] getExpectedDataTypes() {
				return new Class[] { String.class };
			}

			public String getName() {
				return "endWith";
			}
		});

		// contains
		FilterFactoryManager.getDefaultInstance().registerFilterFactory(ChargeDefinition.class, new FilterFactory() {

			public Filter createFilter(Object... objects) {
				String value = ((ChargeDefinition) objects[0]).getChargeCode();
				return new LikeFilter(value);
			}

			public String getConditionString(Locale locale) {
				return AbstractFilter.getConditionString(locale, "string", "contain");
			}

			public Class[] getExpectedDataTypes() {
				return new Class[] { ChargeDefinition.class };
			}

			public String getName() {
				return "contains";
			}
		});

		// not equals
		FilterFactoryManager.getDefaultInstance().registerFilterFactory(ChargeDefinition.class, new FilterFactory() {

			public Filter createFilter(Object... objects) {
				return new NotEqualFilter(objects[0]);
			}

			public String getConditionString(Locale locale) {
				return AbstractFilter.getConditionString(locale, "string", "not.equal");
			}

			public Class[] getExpectedDataTypes() {
				return new Class[] { ChargeDefinition.class };
			}

			public String getName() {
				return "not.equal";
			}
		});

		// Is in filter
		FilterFactoryManager.getDefaultInstance().registerFilterFactory(ChargeDefinition.class, new FilterFactory() {

			public Filter<?> createFilter(Object... objects) {
				return new InFilter<Object>((Object[]) objects[0]);
			}

			public String getConditionString(Locale locale) {
				return AbstractFilter.getConditionString(locale, "string", getName());
			}

			public Class[] getExpectedDataTypes() {
				return new Class[] { Array.newInstance(ChargeDefinition.class, 0).getClass() };
			}

			public String getName() {
				return "in";
			}
		});

	}

	/**
	 * Removes the filters for the date and calendar.
	 */
	private void unregisterDateFilters() {
		// TODO case 2: unregister this factories leaves 13 of them anyway, I don't know why.
		List<FilterFactory> filters = FilterFactoryManager.getDefaultInstance().getFilterFactories(Date.class);
		for (FilterFactory filterFactory : filters) {
			FilterFactoryManager.getDefaultInstance().unregisterFilterFactory(Date.class, filterFactory);
		}
        FilterFactoryManager.getDefaultInstance().getFilterFactories(Date.class);
		filters = FilterFactoryManager.getDefaultInstance().getFilterFactories(Calendar.class);
		for (FilterFactory filterFactory : filters) {
			FilterFactoryManager.getDefaultInstance().unregisterFilterFactory(Calendar.class, filterFactory);
		}
	}
}