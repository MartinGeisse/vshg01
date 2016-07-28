/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.swtlib.panel.prototyping;

import name.martingeisse.swtlib.util.ClassAssociatedResourceUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Label;

/**
 * An abstract 1x1-cell light that can be set to different colors. This class
 * does not update automatically from any kind of model, but subclasses can
 * easily add such behavior.
 */
public class Light extends AbstractPrototypingComponent<Label> {

	/**
	 * iconState
	 */
	private IconState iconState;

	/**
	 * Constructor.
	 * @param prototypingPanel the prototyping panel that should contain this light
	 */
	public Light(final PrototypingPanel prototypingPanel) {
		super(prototypingPanel);
		setControlInternal(new Label(prototypingPanel, SWT.LEFT));
		setIconState(IconState.RED);
	}

	/**
	 * Getter method for the iconState.
	 * @return the iconState
	 */
	public IconState getIconState() {
		return iconState;
	}

	/**
	 * Setter method for the iconState.
	 * @param iconState the iconState to set
	 */
	public void setIconState(final IconState iconState) {
		this.iconState = iconState;
		final Label label = getControl();
		label.setImage(iconState.getImage(label.getDisplay()));
	}

	/**
	 * This enum selects a current icon for a light component.
	 */
	public enum IconState {

		/**
		 * the RED
		 */
		RED("red"),

		/**
		 * the GREEN
		 */
		GREEN("green");

		/**
		 * the imageResourceSubName
		 */
		private final String imageResourceSubName;

		/**
		 * the image
		 */
		private Image image;

		/**
		 * Constructor.
		 */
		private IconState(final String imageResourceSubName) {
			this.imageResourceSubName = imageResourceSubName;
		}

		/**
		 * Getter method for the imageResourceSubName.
		 * @return the imageResourceSubName
		 */
		public String getImageResourceSubName() {
			return imageResourceSubName;
		}

		/**
		 * Getter method for the image.
		 * @param device the SWT device
		 * @return the image
		 */
		private Image getImage(final Device device) {
			if (image == null) {
				image = ClassAssociatedResourceUtil.loadImage(Light.class, imageResourceSubName, "png", device);
			}
			return image;
		}

	}
}
