/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.ecotools.simulator.ui.profiler;

import name.martingeisse.ecotools.common.util.HexNumberUtil;
import name.martingeisse.ecotools.simulator.profiling.FlatCpuProfiler;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * This panel shows profiling results.
 */
public class ProfilerPanel extends Composite {

	/**
	 * the START_TEXT
	 */
	private static final String START_TEXT = "Start";

	/**
	 * the STOP_TEXT
	 */
	private static final String STOP_TEXT = "Stop";
	
	/**
	 * the profiler
	 */
	private final FlatCpuProfiler profiler;
	
	/**
	 * the startAddressText
	 */
	private Text startAddressText;
	
	/**
	 * the endAddressText
	 */
	private Text endAddressText;
	
	/**
	 * the granularityText
	 */
	private Text granularityText;
	
	/**
	 * the startButton
	 */
	private final Button startButton;
	
	/**
	 * the outputText
	 */
	private Text outputText;
	
	/**
	 * Constructor.
	 * @param parent the parent composite
	 * @param profiler the profiler object
	 */
	public ProfilerPanel(Composite parent, FlatCpuProfiler profiler) {
		super(parent, 0);
		this.profiler = profiler;

		setLayout(new GridLayout(1, false));
		Composite controlBar = new Composite(this, 0);
		controlBar.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		controlBar.setLayout(new GridLayout(7, false));
		outputText = new Text(this, SWT.LEFT | SWT.READ_ONLY | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		outputText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		Label startAddressLabel = new Label(controlBar, SWT.SINGLE | SWT.LEFT);
		startAddressLabel.setText("from");
		startAddressLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		startAddressText = new Text(controlBar, SWT.LEFT | SWT.BORDER);
		startAddressText.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		startAddressText.setText(HexNumberUtil.unsignedWordToString(profiler.getStartAddress()));
		
		Label endAddressLabel = new Label(controlBar, 0);
		endAddressLabel.setText("to");
		endAddressLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		endAddressText = new Text(controlBar, SWT.LEFT | SWT.BORDER);
		endAddressText.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		endAddressText.setText(HexNumberUtil.unsignedWordToString(profiler.getEndAddress()));
		
		Label granularityLabel = new Label(controlBar, 0);
		granularityLabel.setText("granularity");
		granularityLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		granularityText = new Text(controlBar, SWT.LEFT | SWT.BORDER);
		granularityText.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		granularityText.setText(HexNumberUtil.unsignedWordToString(profiler.getGranularity()));
		
		ModifyListener modifyListener = new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				try {
					ProfilerPanel.this.profiler.setStartAddress((int)Long.parseLong(startAddressText.getText(), 16));
				} catch (NumberFormatException e) {
				}
				try {
					ProfilerPanel.this.profiler.setEndAddress((int)Long.parseLong(endAddressText.getText(), 16));
				} catch (NumberFormatException e) {
				}
				try {
					ProfilerPanel.this.profiler.setGranularity((int)Long.parseLong(granularityText.getText(), 16));
				} catch (NumberFormatException e) {
				}
			}
		};
		startAddressText.addModifyListener(modifyListener);
		endAddressText.addModifyListener(modifyListener);
		granularityText.addModifyListener(modifyListener);
		
		startButton = new Button(controlBar, SWT.PUSH | SWT.CENTER);
		startButton.setText(START_TEXT);
		startButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		startButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				onButonClicked();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				onButonClicked();
			}
			
		});
	
	}
	
	/**
	 * 
	 */
	private void onButonClicked() {
		if (profiler.getCurrentRun() == null) {
			start();
		} else {
			stop();
		}
	}
	
	/**
	 * 
	 */
	private void start() {
		if (profiler.getStartAddress() <= profiler.getEndAddress() && profiler.getGranularity() > 0) {
			setTextFieldsEnabled(false);
			startButton.setText(STOP_TEXT);
			profiler.start();
		}
	}
	
	/**
	 * 
	 */
	private void stop() {
		setTextFieldsEnabled(true);
		startButton.setText(START_TEXT);
		outputText.setText(profiler.createReport(true));
		profiler.stop();
	}

	/**
	 * @param enabled
	 */
	private void setTextFieldsEnabled(boolean enabled) {
		startAddressText.setEnabled(enabled);
		endAddressText.setEnabled(enabled);
		granularityText.setEnabled(enabled);
	}
	
}
