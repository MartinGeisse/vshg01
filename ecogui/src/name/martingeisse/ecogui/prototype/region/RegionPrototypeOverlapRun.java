/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.ecogui.prototype.region;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Region test run that draws overlapping regions.
 */
public class RegionPrototypeOverlapRun {

	/**
	 * the container
	 */
	private final RegionContainer container;
	
	/**
	 * Constructor.
	 */
	public RegionPrototypeOverlapRun() {
		container = new RegionContainer();
	}

	/**
	 * Getter method for the container.
	 * @return the container
	 */
	public RegionContainer getContainer() {
		return container;
	}
	
	/**
	 * @return the image
	 */
	private BufferedImage generateImage() {
		container.prepare();
		BufferedImage image = new BufferedImage(200, 150, BufferedImage.TYPE_INT_RGB);
		container.startFrame();
		for (int y=0; y<image.getHeight(); y++) {
			container.startLine();
			for (int x=0; x<image.getWidth(); x++) {
				container.startPixel();
				int layer = container.getCurrentVisibleRegionIndex();
				image.getRaster().setSample(x, y, 0, (layer == 0) ? 255 : 0);
				image.getRaster().setSample(x, y, 1, (layer == 1) ? 255 : 0);
				image.getRaster().setSample(x, y, 2, (layer == 2) ? 255 : 0);
			}
		}
		return image;
	}

	private static void saveImage(BufferedImage image, String filename) throws IOException {
		ImageIO.write(image, "png", new File(filename));
	}
	
	/**
	 * Generates an image of overlapping regions.
	 * @param filename  the output filename
	 * @throws IOException on I/O errors
	 * 
	 */
	public void run(String filename) throws IOException {
		saveImage(generateImage(), filename);
	}
	
}
