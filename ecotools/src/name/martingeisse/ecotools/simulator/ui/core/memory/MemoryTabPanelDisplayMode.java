/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.simulator.ui.core.memory;

import name.martingeisse.ecotools.simulator.bus.BusAccessSize;
import name.martingeisse.ecotools.simulator.ui.util.memory.AsciiMemoryVisualizationTextConversionStrategy;
import name.martingeisse.ecotools.simulator.ui.util.memory.DefaultMemoryVisualizationTextConversionStrategy;
import name.martingeisse.ecotools.simulator.ui.util.memory.IMemoryVisualizationTextConversionStrategy;
import name.martingeisse.ecotools.simulator.ui.util.memory.InstructionMemoryVisualizationTextConversionStrategy;

/**
 * This enum defines the display modes that a {@link MemoryTabPanel} can use.
 */
/*
 * Implementation note: this enum uses virtual methods to return the various properties
 * of the different display modes. This is especially important for the text conversion
 * strategy; the others behave the same way for uniformity. If the text conversion strategy
 * was set at construction time, the statically created singleton instance of that
 * strategy would be used in the constructor of the statically created enumeration elements
 * of this enum. This creates a race condition between the static intializers of
 * DefaultMemoryVisualizationTextConversionStrategy and this enum.
 */
public enum MemoryTabPanelDisplayMode {

	/**
	 * This mode displays the memory in word-sized units printed as hexadecimal numbers.
	 */
	WORD {

		/* (non-Javadoc)
		 * @see name.martingeisse.ecotools.simulator.ui.core.MemoryTabPanelDisplayMode#getBusAccessSize()
		 */
		@Override
		public BusAccessSize getBusAccessSize() {
			return BusAccessSize.WORD;
		}

		/* (non-Javadoc)
		 * @see name.martingeisse.ecotools.simulator.ui.core.MemoryTabPanelDisplayMode#getRowCountMultiplier()
		 */
		@Override
		public int getRowCountMultiplier() {
			return 1;
		}

		/* (non-Javadoc)
		 * @see name.martingeisse.ecotools.simulator.ui.core.MemoryTabPanelDisplayMode#getTextConversionStrategy()
		 */
		@Override
		public IMemoryVisualizationTextConversionStrategy getTextConversionStrategy() {
			return DefaultMemoryVisualizationTextConversionStrategy.getInstance();
		}

		/* (non-Javadoc)
		 * @see name.martingeisse.ecotools.simulator.ui.core.MemoryTabPanelDisplayMode#getUnitsPerRow()
		 */
		@Override
		public int getUnitsPerRow() {
			return 4;
		}

	},

	/**
	 * This mode displays the memory in halfword-sized units printed as hexadecimal numbers.
	 */
	HALFWORD {

		/* (non-Javadoc)
		 * @see name.martingeisse.ecotools.simulator.ui.core.MemoryTabPanelDisplayMode#getBusAccessSize()
		 */
		@Override
		public BusAccessSize getBusAccessSize() {
			return BusAccessSize.HALFWORD;
		}

		/* (non-Javadoc)
		 * @see name.martingeisse.ecotools.simulator.ui.core.MemoryTabPanelDisplayMode#getRowCountMultiplier()
		 */
		@Override
		public int getRowCountMultiplier() {
			return 1;
		}

		/* (non-Javadoc)
		 * @see name.martingeisse.ecotools.simulator.ui.core.MemoryTabPanelDisplayMode#getTextConversionStrategy()
		 */
		@Override
		public IMemoryVisualizationTextConversionStrategy getTextConversionStrategy() {
			return DefaultMemoryVisualizationTextConversionStrategy.getInstance();
		}

		/* (non-Javadoc)
		 * @see name.martingeisse.ecotools.simulator.ui.core.MemoryTabPanelDisplayMode#getUnitsPerRow()
		 */
		@Override
		public int getUnitsPerRow() {
			return 8;
		}

	},

	/**
	 * This mode displays the memory in byte-sized units printed as hexadecimal numbers.
	 */
	BYTE {

		/* (non-Javadoc)
		 * @see name.martingeisse.ecotools.simulator.ui.core.MemoryTabPanelDisplayMode#getBusAccessSize()
		 */
		@Override
		public BusAccessSize getBusAccessSize() {
			return BusAccessSize.BYTE;
		}

		/* (non-Javadoc)
		 * @see name.martingeisse.ecotools.simulator.ui.core.MemoryTabPanelDisplayMode#getRowCountMultiplier()
		 */
		@Override
		public int getRowCountMultiplier() {
			return 1;
		}

		/* (non-Javadoc)
		 * @see name.martingeisse.ecotools.simulator.ui.core.MemoryTabPanelDisplayMode#getTextConversionStrategy()
		 */
		@Override
		public IMemoryVisualizationTextConversionStrategy getTextConversionStrategy() {
			return DefaultMemoryVisualizationTextConversionStrategy.getInstance();
		}

		/* (non-Javadoc)
		 * @see name.martingeisse.ecotools.simulator.ui.core.MemoryTabPanelDisplayMode#getUnitsPerRow()
		 */
		@Override
		public int getUnitsPerRow() {
			return 16;
		}

	},

	/**
	 * This mode displays the memory in word-sized units printed as CPU instructions.
	 */
	INSTRUCTION {

		/* (non-Javadoc)
		 * @see name.martingeisse.ecotools.simulator.ui.core.MemoryTabPanelDisplayMode#getBusAccessSize()
		 */
		@Override
		public BusAccessSize getBusAccessSize() {
			return BusAccessSize.WORD;
		}

		/* (non-Javadoc)
		 * @see name.martingeisse.ecotools.simulator.ui.core.MemoryTabPanelDisplayMode#getRowCountMultiplier()
		 */
		@Override
		public int getRowCountMultiplier() {
			return 4;
		}

		/* (non-Javadoc)
		 * @see name.martingeisse.ecotools.simulator.ui.core.MemoryTabPanelDisplayMode#getTextConversionStrategy()
		 */
		@Override
		public IMemoryVisualizationTextConversionStrategy getTextConversionStrategy() {
			return InstructionMemoryVisualizationTextConversionStrategy.getInstance();
		}

		/* (non-Javadoc)
		 * @see name.martingeisse.ecotools.simulator.ui.core.MemoryTabPanelDisplayMode#getUnitsPerRow()
		 */
		@Override
		public int getUnitsPerRow() {
			return 1;
		}

	},

	/**
	 * This mode displays the memory in byte-sized units printed as ASCII characters.
	 */
	ASCII {

		/* (non-Javadoc)
		 * @see name.martingeisse.ecotools.simulator.ui.core.MemoryTabPanelDisplayMode#getBusAccessSize()
		 */
		@Override
		public BusAccessSize getBusAccessSize() {
			return BusAccessSize.BYTE;
		}

		/* (non-Javadoc)
		 * @see name.martingeisse.ecotools.simulator.ui.core.MemoryTabPanelDisplayMode#getRowCountMultiplier()
		 */
		@Override
		public int getRowCountMultiplier() {
			return 1;
		}

		/* (non-Javadoc)
		 * @see name.martingeisse.ecotools.simulator.ui.core.MemoryTabPanelDisplayMode#getTextConversionStrategy()
		 */
		@Override
		public IMemoryVisualizationTextConversionStrategy getTextConversionStrategy() {
			return AsciiMemoryVisualizationTextConversionStrategy.getInstance();
		}

		/* (non-Javadoc)
		 * @see name.martingeisse.ecotools.simulator.ui.core.MemoryTabPanelDisplayMode#getUnitsPerRow()
		 */
		@Override
		public int getUnitsPerRow() {
			return 16;
		}

	};

	/**
	 * Returns the access size of this mode.
	 */
	protected abstract BusAccessSize getBusAccessSize();

	/**
	 * Returns the units per row of this mode.
	 */
	protected abstract int getUnitsPerRow();

	/**
	 * Returns the row count multiplier of this mode.
	 */
	protected abstract int getRowCountMultiplier();

	/**
	 * Returns the text conversion strategy of this mode.
	 */
	protected abstract IMemoryVisualizationTextConversionStrategy getTextConversionStrategy();

}
