/**********************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others.
 * Copyright (c) 2011, 2012 Ericsson.
 * 
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 * IBM - Initial API and implementation
 * Bernd Hufmann - Updated for TMF
 **********************************************************************/
package org.eclipse.linuxtools.tmf.ui.views.uml2sd.dialogs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.linuxtools.internal.tmf.ui.TmfUiPlugin;
import org.eclipse.linuxtools.tmf.ui.views.uml2sd.SDView;
import org.eclipse.linuxtools.tmf.ui.views.uml2sd.handlers.provider.ISDFilterProvider;
import org.eclipse.linuxtools.tmf.ui.views.uml2sd.util.SDMessages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IViewPart;

/**
 * This is the filters list dialog.<br>
 * It is associated to an SDView and to a ISDFilterProvider.<br>
 * 
 * @version 1.0
 * @author sveyrier
 */
public class FilterListDialog extends Dialog {

    // ------------------------------------------------------------------------
    // Constants
    // ------------------------------------------------------------------------
    /**
     * Filter list criteria property name
     */
    protected static final String FILTERS_LIST_CRITERIA = "filtersListsCriteria"; //$NON-NLS-1$
    /**
     * Filter list size property name
     */
    protected static final String FILTERS_LIST_SIZE = "filtersListSize"; //$NON-NLS-1$

    // ------------------------------------------------------------------------
    // Attributes
    // ------------------------------------------------------------------------

    /**
     * The viewer and provided are kept here as attributes
     */
    protected IViewPart fViewer = null;
    /**
     * The filter provider implementation
     */
    protected ISDFilterProvider fProvider = null;
    /**
     * The filters are the result of editing this list
     */
    protected List<FilterCriteria> fFilters;
    /**
     * The add button.
     */
    protected Button fAdd;
    /**
     * The remove button.
     */
    protected Button fRemove;
    /**
     * The edit button.
     */
    protected Button fEdit;
    /**
     * The table with list of filters.
     */
    protected Table fTable;

    // ------------------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------------------

    /**
     * Standard constructor
     * 
     * @param view The view reference
     * @param loader The filter provider implementation
     */
    public FilterListDialog(IViewPart view, ISDFilterProvider loader) {
        super(view.getSite().getShell());
        fViewer = view;
        fProvider = loader;
        fFilters = null;
        // filters = provider.getCurrentFilters();
        setShellStyle(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------
    /**
     * Adds a criteria to the table
     * 
     * @param criteria A criteria to add
     * @param checked A flag whether criteria is checked (selected) or not 
     * @param positive A flag whether criteria is for positive filter or not
     * @param loaderClassName A loader class name for the filters
     */
    protected void addCriteria(Criteria criteria, boolean checked, boolean positive, String loaderClassName) {
        CriteriaTableItem cti = new CriteriaTableItem(fTable, checked, positive, loaderClassName);
        cti.setCriteria(criteria);
    }

    /**
     * Replaces a selected criteria with a new criteria.
     * 
     * @param newCriteria A new criteria.
     */
    protected void replaceSelectedCriteria(Criteria newCriteria) {
        CriteriaTableItem cti = (CriteriaTableItem) fTable.getSelection()[0].getData();
        cti.setCriteria(newCriteria);
    }

    /**
	 * Handles table selection count.
	 */
    protected void handleTableSelectionCount() {
        int count = fTable.getSelectionCount();
        fEdit.setEnabled(count == 1);
        fRemove.setEnabled(count > 0);
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public Control createDialogArea(Composite parent) {

        Group ret = new Group(parent, SWT.NONE);
        ret.setText(SDMessages._57);
        RowLayout rowLayout = new RowLayout();
        rowLayout.wrap = false;
        rowLayout.pack = true;
        rowLayout.justify = false;
        rowLayout.type = SWT.HORIZONTAL;
        rowLayout.marginLeft = 4;
        rowLayout.marginTop = 4;
        rowLayout.marginRight = 4;
        rowLayout.marginBottom = 4;
        rowLayout.spacing = 8;
        ret.setLayout(rowLayout);

        fTable = new Table(ret, SWT.MULTI | SWT.CHECK);
        fTable.setLayoutData(new RowData(220, 84));
        fTable.setHeaderVisible(false);
        fTable.addSelectionListener(new SelectionListener() {
            /*
             * (non-Javadoc)
             * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                int count = fTable.getSelectionCount();
                if (count == 1) {
                    Criteria criteria = openFilterDialog(((CriteriaTableItem) fTable.getSelection()[0].getData()).getCriteria(), SDMessages._63);
                    if (criteria != null) {
                        replaceSelectedCriteria(criteria);
                    }
                }
            }

            /*
             * (non-Javadoc)
             * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected(SelectionEvent e) {
                handleTableSelectionCount();
            }
        });
        if (fFilters != null) {
            for (Iterator<FilterCriteria> i = fFilters.iterator(); i.hasNext();) {
                FilterCriteria filterCriteria = (FilterCriteria) i.next();
                addCriteria(filterCriteria.getCriteria(), filterCriteria.isActive(), filterCriteria.isPositive(), filterCriteria.getLoaderClassName());
            }
        }

        Composite commands = new Composite(ret, SWT.NONE);
        RowLayout rowLayoutCommands = new RowLayout();
        rowLayoutCommands.wrap = false;
        rowLayoutCommands.pack = false;
        rowLayoutCommands.justify = true;
        rowLayoutCommands.type = SWT.VERTICAL;
        rowLayoutCommands.marginLeft = 0;
        rowLayoutCommands.marginTop = 4;
        rowLayoutCommands.marginRight = 0;
        rowLayoutCommands.marginBottom = 4;
        rowLayoutCommands.spacing = 8;
        commands.setLayout(rowLayoutCommands);
        fAdd = new Button(commands, SWT.NONE);
        fAdd.setText(SDMessages._61);
        fAdd.addSelectionListener(new SelectionListener() {
            /*
             * (non-Javadoc)
             * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                // Nothing to do
            }

            /*
             * (non-Javadoc)
             * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected(SelectionEvent e) {
                Criteria init = new Criteria();
                Criteria c = openFilterDialog(init, SDMessages._62);
                if (c != null) {
                    addCriteria(c, true, false, null);
                }
            }
        });

        fEdit = new Button(commands, SWT.NONE);
        fEdit.setText(SDMessages._60);
        fEdit.addSelectionListener(new SelectionListener() {
            /*
             * (non-Javadoc)
             * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                // Nothing to do
            }
            
            /*
             * (non-Javadoc)
             * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected(SelectionEvent e) {
                Criteria c = openFilterDialog(((CriteriaTableItem) fTable.getSelection()[0].getData()).getCriteria(), SDMessages._63);
                if (c != null) {
                    replaceSelectedCriteria(c);
                }
            }
        });
        fEdit.setEnabled(false);

        fRemove = new Button(commands, SWT.NONE);
        fRemove.setText(SDMessages._64);
        fRemove.addSelectionListener(new SelectionListener() {
            /*
             * (non-Javadoc)
             * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                // Nothing to do
            }

            /*
             * (non-Javadoc)
             * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected(SelectionEvent e) {
                fTable.remove(fTable.getSelectionIndices());
                handleTableSelectionCount();
            }
        });
        fRemove.setEnabled(false);

        getShell().setText(SDMessages._65);
        /*
         * for (int i=0;i<filters.size();i++) { if (filters.get(i) instanceof FilterCriteria)
         * addCriteria(((FilterCriteria)filters.get(i)).getCriteria(),true); }
         */
        return ret;
    }

    /**
     * Opens the filter dialog box with given parameter.
     * 
     * @param criteria The criteria reference to pass
     * @param action to distinguish between "Update" and "Create"
     * @return the criteria that has been updated or created
     */
    protected Criteria openFilterDialog(Criteria criteria, String action) {
        SearchFilterDialog filter = new SearchFilterDialog((SDView) fViewer, fProvider, true, SWT.APPLICATION_MODAL);
        filter.setCriteria(criteria);
        filter.setOkText(action);
        filter.setTitle(SDMessages._66);
        filter.open();
        return filter.getCriteria();
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.window.Window#open()
     */
    @Override
    public int open() {
        create();
        getShell().pack();
        getShell().setLocation(getShell().getDisplay().getCursorLocation());
        loadFiltersCriteria();
        return super.open();
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#okPressed()
     */
    @Override
    public void okPressed() {
        if (fTable.getItemCount() > 0) {
            fFilters = new ArrayList<FilterCriteria>();
        } else {
            fFilters = null;
        }
        for (int i = 0; i < fTable.getItemCount(); i++) {
            TableItem item = fTable.getItem(i);
            CriteriaTableItem cti = (CriteriaTableItem) item.getData();
            FilterCriteria fc = new FilterCriteria(cti.getCriteria(), item.getChecked(), cti.isPositive(), cti.getLoaderClassName());
            FilterCriteria efc = FilterCriteria.find(fc, fFilters);
            if (efc == null) {
                fFilters.add(fc);
            } else {
                efc.setActive(efc.isActive() || fc.isActive());
            }
        }
        super.close();
        fProvider.filter(fFilters);
        saveFiltersCriteria(fFilters);
    }

    /**
     * Sets the list of filters.
     * 
     * @param filters The list of filters to set.
     */
    public void setFilters(List<FilterCriteria> filters) {
        fFilters = filters;
    }

    /**
     * Returns the filters list after editing.
     * 
     * @return the filters list after editing
     */
    public List<FilterCriteria> getFilters() {
        return fFilters;
    }

    /**
     * Loads the filter criteria from global filters which are saved in the dialog settings.
     */
    protected void loadFiltersCriteria() {
        List<FilterCriteria> globalFilters = getGlobalFilters();
        for (Iterator<FilterCriteria> i = globalFilters.iterator(); i.hasNext();) {
            FilterCriteria filterCriteria = (FilterCriteria) i.next();
            addCriteria(filterCriteria.getCriteria(), filterCriteria.isActive(), filterCriteria.isPositive(), filterCriteria.getLoaderClassName());
        }
    }

    /**
     * Returns the global filters which are saved in the dialog settings..
     * 
     * @return the saved global filters
     */
    public static List<FilterCriteria> getGlobalFilters() {
        DialogSettings settings = (DialogSettings) TmfUiPlugin.getDefault().getDialogSettings().getSection(FILTERS_LIST_CRITERIA);
        int i = 0;
        DialogSettings section = null;
        int size = 0;
        if (settings != null) {
            try {
                size = settings.getInt(FILTERS_LIST_SIZE);
            } catch (NumberFormatException e) {
                // This is not a problem
                size = 0;
            }
            section = (DialogSettings) settings.getSection(FILTERS_LIST_CRITERIA + i);
        }

        List<FilterCriteria> globalFilters = new ArrayList<FilterCriteria>();

        while ((section != null) && (i < size)) {
            FilterCriteria criteria = new FilterCriteria();
            criteria.setCriteria(new Criteria());
            criteria.load(section);
            globalFilters.add(criteria);
            section = (DialogSettings) settings.getSection(FILTERS_LIST_CRITERIA + (++i));
        }

        return globalFilters;
    }

    /**
     * Saves the filter criteria in the dialog settings.
     * 
     * @param globalFilters A list of filters to save.
     */
    public static void saveFiltersCriteria(List<FilterCriteria> globalFilters) {
        DialogSettings settings = (DialogSettings) TmfUiPlugin.getDefault().getDialogSettings();
        DialogSettings section = (DialogSettings) settings.getSection(FILTERS_LIST_CRITERIA);
        if (section == null) {
            section = (DialogSettings) settings.addNewSection(FILTERS_LIST_CRITERIA);
        }

        if (globalFilters == null) {
            section.put(FILTERS_LIST_SIZE, 0);
            return;
        }

        section.put(FILTERS_LIST_SIZE, globalFilters.size());

        FilterCriteria criteria;

        for (int j = 0; j < globalFilters.size(); j++) {
            if (!(globalFilters.get(j) instanceof FilterCriteria)) {
                return;
            }

            criteria = (FilterCriteria) globalFilters.get(j);
            DialogSettings subSection = (DialogSettings) section.getSection(FILTERS_LIST_CRITERIA + j);

            if (subSection == null) {
                subSection = (DialogSettings) section.addNewSection(FILTERS_LIST_CRITERIA + j);
            }
            criteria.save(subSection);
        }
    }
    
    /**
     * Deactivates the saved global filters.
     */
    public static void deactivateSavedGlobalFilters() {
     // Deactivate all filters
        List<FilterCriteria> filters = getGlobalFilters();
        for(FilterCriteria criteria : filters) {
            criteria.setActive(false);
        }
        // Save settings
        FilterListDialog.saveFiltersCriteria(filters);
    }

    // ------------------------------------------------------------------------
    // Helper classes
    // ------------------------------------------------------------------------
    /**
     * A class to map TableItems that can be toggled active or inactive and Criteria
     */
    protected class CriteriaTableItem {

        /**
         * The criteria reference
         */
        protected Criteria fCriteria;
        /**
         * The "positive" value.
         */
        protected boolean fIsPositive;
        /**
         * The loader class name
         */
        protected String fLoaderClassName;
        /**
         * The actual table item.
         */
        protected TableItem fTableItem;

        /**
         * Constructor
         * 
         * @param parent The parent table
         * @param isActive <code>true</code> if filter criteria is active else <code>false</code> 
         * @param isPositive <code>true</code> for positive filter else <code>false</code>
         * @param loaderClassName The loader class name
         */
        public CriteriaTableItem(Table parent, boolean isActive, boolean isPositive, String loaderClassName) {
            fTableItem = new TableItem(parent, SWT.NONE);
            fTableItem.setData(this);
            fTableItem.setChecked(isActive);
            fIsPositive = isPositive;
            fLoaderClassName = loaderClassName;
        }

        /**
         * Constructor
         * 
         * @param parent The parent table
         * @param isActive <code>true</code> if filter criteria is active else <code>false</code> 
         * @param isPositive <code>true</code> for positive filter else <code>false</code>
         * @param loaderClassName The loader class name
         * @param index The table item index
         */
        public CriteriaTableItem(Table parent, boolean isActive, boolean isPositive, String loaderClassName, int index) {
            fTableItem = new TableItem(parent, SWT.NONE, index);
            fTableItem.setChecked(isActive);
            fIsPositive = isPositive;
            fLoaderClassName = loaderClassName;
        }

        /**
         * Sets the criteria.
         * 
         * @param criteria The criteria to set
         */
        public void setCriteria(Criteria criteria) {
            fCriteria = criteria;
            fTableItem.setText((fIsPositive ? SDMessages._59 : SDMessages._58) + " " + fCriteria.getExpression() + " " + fCriteria.getGraphNodeSummary(fProvider, fLoaderClassName)); //$NON-NLS-1$ //$NON-NLS-2$
        }

        /**
         * Returns the criteria.
         * @return the criteria
         */
        public Criteria getCriteria() {
            return fCriteria;
        }

        /**
         * Returns whether positive filtering is active or not.
         * 
         * @return <code>true</code> for positive filter else <code>false</code>
         */
        public boolean isPositive() {
            return fIsPositive;
        }

        /**
         * Returns the loader class name.
         * 
         * @return the loader class name
         */
        public String getLoaderClassName() {
            return fLoaderClassName;
        }
    }

}