/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.util.memory;

import name.martingeisse.ecotools.simulator.bus.BusAccessSize;
import name.martingeisse.ecotools.simulator.bus.BusTimeoutException;
import name.martingeisse.ecotools.simulator.devices.memory.Ram;
import name.martingeisse.swtlib.util.test.wizard.AbstractTestWizardApplication;
import name.martingeisse.swtlib.util.test.wizard.ITestWizardItem;

import org.eclipse.swt.widgets.Composite;

/**
 * Test for the console contributor.
 */
public class MemoryVisualizationTestApplication extends AbstractTestWizardApplication {

	/**
	 * Constructor
	 */
	public MemoryVisualizationTestApplication() {
	}

	/* (non-Javadoc)
	 * @see name.martingeisse.swtlib.util.test.wizard.AbstractTestWizardApplication#createItems()
	 */
	@Override
	protected ITestWizardItem[] createItems() {
		return new ITestWizardItem[] {
			new TestBlackTextItem(), new TestHighlightSingleAddressItem(), new TestInstructionRenderingItem(), new TestUpdateItem(),
		};
	}

	/**
	 * Main method.
	 * @param args ...
	 */
	public static void main(String[] args) {
		MemoryVisualizationTestApplication app = new MemoryVisualizationTestApplication();
		app.create();
		app.open();
		app.mainLoop();
		app.dispose();
		app.exit();
	}

	/**
	 * 
	 */
	private class TestBlackTextItem implements ITestWizardItem {

		/* (non-Javadoc)
		 * @see name.martingeisse.swtlib.util.test.wizard.ITestWizardItem#createComponent(org.eclipse.swt.widgets.Composite)
		 */
		@Override
		public void createComponent(Composite parent) {
			Ram ram = new Ram(10);
			ram.randomizeContents();
			MemoryVisualizationPanel panel = new MemoryVisualizationPanel(parent);
			panel.setStorageStrategy(new DefaultMemoryVisualizationStorageStrategy(ram));
			panel.setTextConversionStrategy(DefaultMemoryVisualizationTextConversionStrategy.getInstance());
			panel.setColoringStrategy(BlackTextMemoryVisualizationColoringStrategy.getInstance());
			panel.setStartAddress(0);
			panel.setUnitsPerRow(5);
			panel.setRowCount(3);
			panel.setAccessSize(BusAccessSize.BYTE);
			panel.updateContents();
		}

		/* (non-Javadoc)
		 * @see name.martingeisse.swtlib.util.test.wizard.ITestWizardItem#getDescription()
		 */
		@Override
		public String getDescription() {
			return "You should see the memory contents as 5 bytes per row, 3 rows, written in black text.";
		}

	}

	/**
	 * 
	 */
	private class TestHighlightSingleAddressItem implements ITestWizardItem {

		/* (non-Javadoc)
		 * @see name.martingeisse.swtlib.util.test.wizard.ITestWizardItem#createComponent(org.eclipse.swt.widgets.Composite)
		 */
		@Override
		public void createComponent(Composite parent) {
			Ram ram = new Ram(10);
			ram.randomizeContents();
			MemoryVisualizationPanel panel = new MemoryVisualizationPanel(parent);
			panel.setStorageStrategy(new DefaultMemoryVisualizationStorageStrategy(ram));
			panel.setTextConversionStrategy(DefaultMemoryVisualizationTextConversionStrategy.getInstance());
			panel.setColoringStrategy(new HighlightSingleAddressMemoryVisualizationStrategy(7));
			panel.setStartAddress(0);
			panel.setUnitsPerRow(5);
			panel.setRowCount(3);
			panel.setAccessSize(BusAccessSize.BYTE);
			panel.updateContents();
		}

		/* (non-Javadoc)
		 * @see name.martingeisse.swtlib.util.test.wizard.ITestWizardItem#getDescription()
		 */
		@Override
		public String getDescription() {
			return "The byte at address 7 should be highlighted.";
		}

	}

	/**
	 * 
	 */
	private class TestInstructionRenderingItem implements ITestWizardItem {

		/* (non-Javadoc)
		 * @see name.martingeisse.swtlib.util.test.wizard.ITestWizardItem#createComponent(org.eclipse.swt.widgets.Composite)
		 */
		@Override
		public void createComponent(Composite parent) {
			Ram ram = new Ram(10);
			ram.randomizeContents();

			MemoryVisualizationPanel panel = new MemoryVisualizationPanel(parent);
			panel.setStorageStrategy(new DefaultMemoryVisualizationStorageStrategy(ram));
			panel.setTextConversionStrategy(InstructionMemoryVisualizationTextConversionStrategy.getInstance());
			panel.setColoringStrategy(BlackTextMemoryVisualizationColoringStrategy.getInstance());
			panel.setStartAddress(0);
			panel.setUnitsPerRow(1);
			panel.setRowCount(10);
			panel.setAccessSize(BusAccessSize.WORD);
			panel.updateContents();

			MemoryVisualizationPanel panel2 = new MemoryVisualizationPanel(parent);
			panel2.setStorageStrategy(new DefaultMemoryVisualizationStorageStrategy(ram));
			panel2.setTextConversionStrategy(DefaultMemoryVisualizationTextConversionStrategy.getInstance());
			panel2.setColoringStrategy(BlackTextMemoryVisualizationColoringStrategy.getInstance());
			panel2.setStartAddress(0);
			panel2.setUnitsPerRow(1);
			panel2.setRowCount(10);
			panel2.setAccessSize(BusAccessSize.WORD);
			panel2.updateContents();
		}

		/* (non-Javadoc)
		 * @see name.martingeisse.swtlib.util.test.wizard.ITestWizardItem#getDescription()
		 */
		@Override
		public String getDescription() {
			return "You should see instructions in the left panel and the corresponding word values in the right panel.";
		}

	}

	/**
	 * 
	 */
	private class TestUpdateItem implements ITestWizardItem {

		/* (non-Javadoc)
		 * @see name.martingeisse.swtlib.util.test.wizard.ITestWizardItem#createComponent(org.eclipse.swt.widgets.Composite)
		 */
		@Override
		public void createComponent(Composite parent) {
			Ram ram = new Ram(10);
			ram.randomizeContents();

			MemoryVisualizationPanel panel = new MemoryVisualizationPanel(parent);
			panel.setStorageStrategy(new DefaultMemoryVisualizationStorageStrategy(ram));
			panel.setTextConversionStrategy(DefaultMemoryVisualizationTextConversionStrategy.getInstance());
			panel.setColoringStrategy(BlackTextMemoryVisualizationColoringStrategy.getInstance());
			panel.setStartAddress(0);
			panel.setUnitsPerRow(1);
			panel.setRowCount(10);
			panel.setAccessSize(BusAccessSize.WORD);
			panel.updateContents();

			MemoryVisualizationPanel panel2 = new MemoryVisualizationPanel(parent);
			panel2.setStorageStrategy(new DefaultMemoryVisualizationStorageStrategy(ram));
			panel2.setTextConversionStrategy(DefaultMemoryVisualizationTextConversionStrategy.getInstance());
			panel2.setColoringStrategy(BlackTextMemoryVisualizationColoringStrategy.getInstance());
			panel2.setStartAddress(0);
			panel2.setUnitsPerRow(1);
			panel2.setRowCount(10);
			panel2.setAccessSize(BusAccessSize.WORD);
			panel2.updateContents();

			try {
				ram.write(0, BusAccessSize.WORD, 0x11223344);
			} catch (BusTimeoutException e) {
				throw new RuntimeException(e);
			}
			panel2.updateContents();

		}

		/* (non-Javadoc)
		 * @see name.martingeisse.swtlib.util.test.wizard.ITestWizardItem#getDescription()
		 */
		@Override
		public String getDescription() {
			return "You should not see the value 11223344 in the first cell of the left panel because the panel hasn't been updated. The right panel has been updated.";
		}

	}

}
