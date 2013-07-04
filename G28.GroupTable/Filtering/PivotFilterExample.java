/*
 * @(#)PivotFilterExample.java 3/4/2013
 *
 * Copyright 2002 - 2013 JIDE Software Inc. All rights reserved.
 */

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import com.jidesoft.converter.ObjectConverterManager;
import com.jidesoft.filter.Filter;
import com.jidesoft.grid.CachedTableModel;
import com.jidesoft.grid.CalculatedTableModel;
import com.jidesoft.pivot.CalculatedPivotDataModel;
import com.jidesoft.pivot.FieldBox;
import com.jidesoft.pivot.IPivotDataModel;
import com.jidesoft.pivot.PivotConstants;
import com.jidesoft.pivot.PivotField;
import com.jidesoft.pivot.PivotTablePane;
import com.jidesoft.pivot.PivotTablePersistenceUtils;
import com.jidesoft.utils.PersistenceUtilsCallback;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;


/**
 * Simple example application to explore the 
 * capabilities of a Jide Pivot Table.
 */
@SuppressWarnings("serial")
public class PivotFilterExample extends JFrame {


	private static final String PRICE_FIELD = "Price";
	private static final String ARTICLE_FIELD = "Article";
	private static final String DATE_FIELD = "Date";
	private static final String CUSTOMER_FIELD = "Customer";
	private static final String[] CUSTOMERS_DATA = new String[]{"[Susi, Peter]", "[Tanja, Peter]", "[Tanja]", "[Susi]", "[Peter]", "[Susi]"};
	private static final String[] POSSIBLE_CUSTOMERS = new String[]{"Susi", "Peter", "Tanja", "Angela"};
	
	private DefaultTableModel tableModel;
    private IPivotDataModel pivotModel;
    private PivotTablePane pivotTablePane;
    
	private File pivotXml = null;
	private JTextField xmlFileField;
	private NotifyFilterableTableModel filteredModel;
	private Map<PivotField, FilterFieldBox> fieldToBox;
	
    public PivotFilterExample() {
        // grundlegende Frame-Einstellungen
        setTitle(this.getClass().getName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(800, 600);
        setLocationRelativeTo(null);   // Frame auf Bildschirm zentrieren
        init();
    }

    protected void init() {
        tableModel = createTableModel();
		createFilterableTableModel();
        createPivotFields();
        
        fieldToBox = new HashMap<PivotField, FilterFieldBox>();
	    pivotTablePane = new PivotTablePane(pivotModel) {
		   	 @Override
		     protected FieldBox createFieldBox(PivotField field, boolean sortArrowVisible, boolean filterButtonVisible) {
		         final FilterFieldBox box = new FilterFieldBox(field, sortArrowVisible, filterButtonVisible, filteredModel);
		         fieldToBox.put(field, box);
		         return box;
			 }
	    };
        pivotTablePane.setFieldChooserVisible(false);
        add(pivotTablePane, BorderLayout.CENTER);

        
        final JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }

    
	private void createPivotFields() {
		final PivotField dateField = pivotModel.getField(DATE_FIELD);
		dateField.setAreaType(PivotConstants.AREA_ROW);
		dateField.setDescription("Buying date");
		dateField.setType(Date.class);
        final PivotField articleField = pivotModel.getField(ARTICLE_FIELD);
		articleField.setAreaType(PivotConstants.AREA_ROW);
		articleField.setDescription("Article description");
		articleField.setType(String.class);
		final PivotField customerField = pivotModel.getField(CUSTOMER_FIELD);
		customerField.setAreaType(PivotConstants.AREA_COLUMN);
		customerField.setDescription("Buyer's name");
		customerField.setType(String.class);
        customerField.setPreferSelectedPossibleValues(true);
        final PivotField priceField = pivotModel.getField(PRICE_FIELD);
		priceField.setAreaType(PivotConstants.AREA_DATA);
        priceField.setSummaryType(PivotConstants.SUMMARY_SUM);
        priceField.setGrandTotalSummaryType(PivotConstants.SUMMARY_SUM);
		priceField.setDescription("Final price");
		priceField.setType(Double.class);
        pivotModel.setShowGrandTotalForColumn(true);
        pivotModel.setShowGrandTotalForRow(true);
        
	}


	private NotifyFilterableTableModel createFilterableTableModel() {
		filteredModel = new NotifyFilterableTableModel(tableModel);
		
		final int count = filteredModel.getColumnCount();
		for (int i = 0; i < count; i++) {
			if (CUSTOMER_FIELD.equals(filteredModel.getColumnName(i))) {
				final StringSetFilter filter = new StringSetFilter();
				filter.setName(CUSTOMER_FIELD);
				filter.setPossibleValues(CUSTOMERS_DATA);
				filteredModel.addFilter(i, filter);
			}
		}
		
        final CalculatedTableModel calc = new CalculatedTableModel(filteredModel);
        calc.addAllColumns();
		final CachedTableModel cache = new CachedTableModel(calc);
        cache.setCacheEnabled(true);
        
        pivotModel = new CalculatedPivotDataModel(cache) {
			@Override
            public Object[] getPossibleValues(final PivotField field) {
				final String fieldName = field.getName();
				if (CUSTOMER_FIELD.equals(fieldName)) {
					return POSSIBLE_CUSTOMERS;
				}
				return super.getPossibleValues(field);
			}
		};
        
		return filteredModel;
	}

	private DefaultTableModel createTableModel() {
		final DefaultTableModel tableModel = new DefaultTableModel();
		final Date now = new Date();
		final Date[] dateColumnData = new Date[]{now, 
				DateUtils.addDays(now, 1),
				DateUtils.addDays(now,  2), 
				DateUtils.addDays(now,  3), 
				DateUtils.addDays(now,  4), 
				DateUtils.addDays(now,  5)};
		tableModel.addColumn(DATE_FIELD, dateColumnData);
        final String[] articleColumnData = new String[]{"Corn", "Banana", "Apples", "Corn", "Apples", "Corn"};
		tableModel.addColumn(ARTICLE_FIELD, articleColumnData);
		final String[] userColumnData = CUSTOMERS_DATA.clone();
		tableModel.addColumn(CUSTOMER_FIELD, userColumnData);
        final Double[] priceColumnData = new Double[]{355.30, 35.60, 95.15, 55.30, 5.60, 5.15};
		tableModel.addColumn(PRICE_FIELD, priceColumnData);
		return tableModel; 
	}

    private static String ATTRIBUTE_STRING_SET_FILTER = "stringSetFilter";

    public JPanel createButtonPanel() {
        final JButton saveButton = new JButton("Save XML");
        saveButton.addActionListener(new ActionListener() {

			@Override
        	public void actionPerformed(ActionEvent event) {
        		try {
					if (pivotXml == null) {
						pivotXml = File.createTempFile("JidePivot", ".xml");
					}
					final OutputStream xmlStream = new FileOutputStream(pivotXml);
					PivotTablePersistenceUtils.save(pivotTablePane, xmlStream, new PersistenceUtilsCallback.Save() {
                        @Override
                        public void save(Document document, Element element, Object object) {
                            if (object instanceof PivotField) {
                                if (CUSTOMER_FIELD.equals(((PivotField) object).getName())) {
                                    if (filteredModel != null) {
                                        final int count = filteredModel.getColumnCount();
                                        for (int i = 0; i < count; i++) {
                                            final Filter<?>[] filters = filteredModel.getFilters(i);
                                            for (final Filter<?> filter : filters) {
                                                if (StringUtils.equals(CUSTOMER_FIELD, filter.getName())) {
                                                    if (filter instanceof StringSetFilter) {
                                                        Object[] possibleValues = ((StringSetFilter) filter).getSelectedPossibleValues();
                                                        element.setAttribute(ATTRIBUTE_STRING_SET_FILTER, ObjectConverterManager.toString(possibleValues));
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    });
					xmlFileField.setText("Pivot XML saved: " + pivotXml.getAbsolutePath());
				} 
        		catch (Exception e) {
        			throw new IllegalStateException("Unable to write temp file", e);
				} 
        	}
        });
        
        final JButton loadButton = new JButton("Load XML");
        loadButton.addActionListener(new ActionListener() {
        	
        	@Override
        	public void actionPerformed(ActionEvent event) {
        		if (pivotXml == null) {
        			xmlFileField.setText("No XML file present. Please press save button first.");
        			return;
        		}
        		try {
					final InputStream xmlStream = new FileInputStream(pivotXml);
					PivotTablePersistenceUtils.load(pivotTablePane, xmlStream, new PersistenceUtilsCallback.Load() {
                        @Override
                        public void load(Document document, Element element, Object object) {
                            if (object instanceof PivotField) {
                                if (CUSTOMER_FIELD.equals(((PivotField) object).getName())) {
                                    NamedNodeMap attributes = element.getAttributes();
                                    String selectedValues = null;
                                    for (int i = 0; i < attributes.getLength(); i++) {
                                        Node attribute = attributes.item(i);
                                        if (ATTRIBUTE_STRING_SET_FILTER.equals(attribute.getNodeName())) {
                                            selectedValues = attribute.getNodeValue();
                                        }
                                    }
                                    if (selectedValues != null) {
                                        String[] possibleValues = (String[]) ObjectConverterManager.fromString(selectedValues, String[].class);
                                        if (filteredModel != null) {
                                            final int count = filteredModel.getColumnCount();
                                            for (int i = 0; i < count; i++) {
                                                final Filter<?>[] filters = filteredModel.getFilters(i);
                                                for (final Filter<?> filter : filters) {
                                                    if (StringUtils.equals(CUSTOMER_FIELD, filter.getName())) {
                                                        if (filter instanceof StringSetFilter) {
                                                            StringSetFilter f = (StringSetFilter) filter;
                                                            f.setSelectedPossibleValues(possibleValues);
                                                        }
                                                    }
                                                }
                                            }
                                            filteredModel.setFiltersApplied(true, this);
                                        }
                                    }
                                }
                            }
                        }
                    });
					pivotTablePane.fieldsUpdated();
					xmlFileField.setText("Pivot XML loaded: " + pivotXml.getAbsolutePath());
        		} 
        		catch (Exception e) {
        			throw new IllegalStateException("Unable to read temp file", e);
        		}
        		
        	}
        });
        
        
		final JButton filterButton = new JButton("Apply Filters");
        filterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filteredModel.setFiltersApplied(true);
            }
        });
        
        
        final JPanel southPanel = new JPanel();
        southPanel.setLayout(new BorderLayout());
        xmlFileField = new JTextField();
        xmlFileField.setEditable(false);
        
        final JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);
        buttonPanel.add(filterButton);
        
        southPanel.add(xmlFileField, BorderLayout.NORTH);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);
		return southPanel;
	}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				final PivotFilterExample example = new PivotFilterExample();
				example.setVisible(true);
			}
        });
    }
}