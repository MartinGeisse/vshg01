/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.swtlib.panel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

/**
 * This panel implements the "tabbed browsing" metaphor. It contains
 * an address bar at the top and a tab panel below it. The address
 * bar shows the address currently displayed in the active tab.
 * A button allows to open a new tab, and the tab headers contain
 * a button each to close that tab.
 * 
 * This class contains various abstract methods to implement the
 * primitive operations necessary for this panel to work.
 * 
 * @param <T> the static type of the SWT control used as the
 * top-level control of a single tab.
 */
public abstract class AbstractTabbedBrowsingPanel<T extends Control> extends Composite {

	/**
	 * This key is used to store the current address of a tab in the SWT TabItem.
	 */
	private static final String CURRENT_ADDRESS_KEY = "name.martingeisse.ecotools.simulator.ui.util";
	
	/**
	 * the tabControlClass
	 */
	private Class<T> tabControlClass;
	
	/**
	 * the addressBar
	 */
	private Text addressBar;
	
	/**
	 * the tabFolder
	 */
	private TabFolder tabFolder;
	
	/**
	 * Constructor
	 * @param parent the parent composite
	 * @param tabControlClass the class object of the SWT control class used as
	 * the top-level control of a single tab.
	 */
	public AbstractTabbedBrowsingPanel(Composite parent, Class<T> tabControlClass) {
		super(parent, 0);
		this.tabControlClass = tabControlClass;
		
		GridLayout layout = new GridLayout(3, false);
		setLayout(layout);

		addressBar = new Text(this, SWT.LEFT | SWT.SINGLE | SWT.BORDER);
		addressBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		addressBar.addKeyListener(new MyAddressBarKeyListener());

		Button createTabButton = new Button(this, SWT.CENTER | SWT.PUSH);
		createTabButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		createTabButton.setText("+");
		createTabButton.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent event) {
				createTab();
			}
			
		});

		Button closeTabButton = new Button(this, SWT.CENTER | SWT.PUSH);
		closeTabButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		closeTabButton.setText("-");
		closeTabButton.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent event) {
				closeActiveTab();
			}
			
		});

		tabFolder = new TabFolder(this, SWT.TOP);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
		tabFolder.addSelectionListener(new MyTabSelectionListener());
		
	}

	/**
	 * Creates a new tab with an empty initial address.
	 */
	public final void createTab() {
		createTab(getDefaultAddress());
	}
	
	/**
	 * @return Returns the default address for new tabs.
	 */
	protected String getDefaultAddress() {
		return "";
	}

	/**
	 * Creates a new tab with the specified initial address.
	 * @param initialAddress the initial address for the new tab
	 */
	public void createTab(String initialAddress) {
		if (initialAddress == null) {
			initialAddress = "";
		}
		TabItem tabItem = new TabItem(tabFolder, 0);
		tabItem.setControl(createNewTabControl(tabFolder));
		setTabAddress(tabItem, initialAddress);
		tabFolder.setSelection(tabItem);
		addressBar.setText(initialAddress);
	}
	
	/**
	 * This method must be implemented to create the control for a new tab.
	 * @param parent the parent composite of the control to create
	 * @return Returns the created control.
	 */
	protected abstract T createNewTabControl(Composite parent);
	
	/**
	 * Returns the address currently used by the specified tab.
	 * @param tabItem the tab item whose address shall be returned.
	 * @return Returns the current address of the specified tab.
	 */
	public String getTabAddress(TabItem tabItem) {
		return (String)tabItem.getData(CURRENT_ADDRESS_KEY);
	}
	
	/**
	 * Sets a new address for the specified tab.
	 * @param tabItem the tab whose address to set
	 * @param address the address to set
	 */
	public void setTabAddress(TabItem tabItem, String address) {
		if (address == null) {
			address = "";
		}
		tabItem.setData(CURRENT_ADDRESS_KEY, address);
		tabItem.setText(address);
		makeAddressActive(tabControlClass.cast(tabItem.getControl()), address);
	}
	
	/**
	 * Makes the specified address active for a tab. This typically
	 * changes the displayed content of the tab control based on the
	 * address.
	 * @param tabControl the tab control used to show content based on the address
	 * @param address the address to make active
	 */
	protected abstract void makeAddressActive(T tabControl, String address);
	
	/**
	 * Closes the active tab and selects a neighbour tab according to platform-specific
	 * rules.
	 */
	public void closeActiveTab() {
		TabItem[] selectedItems = tabFolder.getSelection();
		if (selectedItems.length == 1 && selectedItems[0] != null) {
			selectedItems[0].dispose();
		}
	}
	
	/**
	 * This listener updates the address bar on tab selection.
	 */
	private class MyTabSelectionListener extends SelectionAdapter {

		/* (non-Javadoc)
		 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
		 */
		@Override
		public void widgetSelected(SelectionEvent event) {
			TabItem tabItem = (TabItem)event.item;
			String tabAddress = getTabAddress(tabItem);
			if (tabAddress != null) {
				addressBar.setText(tabAddress);
			}
		}
		
	}
	
	/**
	 * This listener sets the address of the current tab when the user presses
	 * enter in the address bar. If no tab is selected (e.g. no tab exists
	 * at all), a new tab is created for that address.
	 */
	private class MyAddressBarKeyListener extends KeyAdapter {

		/* (non-Javadoc)
		 * @see org.eclipse.swt.events.KeyListener#keyPressed(org.eclipse.swt.events.KeyEvent)
		 */
		@Override
		public void keyPressed(KeyEvent event) {
			if (event.character == SWT.CR || event.character == SWT.LF) {
				TabItem[] selectedItems = tabFolder.getSelection();
				if (selectedItems.length == 1 && selectedItems[0] != null) {
					TabItem currentTab = selectedItems[0];
					setTabAddress(currentTab, addressBar.getText());
				} else {
					createTab(addressBar.getText());
				}
			}
		}
		
	}
	
}
