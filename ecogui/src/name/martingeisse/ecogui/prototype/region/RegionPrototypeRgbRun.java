/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.ecogui.prototype.region;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

/**
 * Region test run that only determines the final masks and displays
 * the masks by mixing R, G, B colors. Uses exactly 3 regions.
 */
public class RegionPrototypeRgbRun {

	/**
	 * the redRegion
	 */
	private final RegionPrototypeRegion redRegion;
	
	/**
	 * the greenRegion
	 */
	private final RegionPrototypeRegion greenRegion;
	
	/**
	 * the blueRegion
	 */
	private final RegionPrototypeRegion blueRegion;
	
	/**
	 * Constructor.
	 */
	public RegionPrototypeRgbRun() {
		this.redRegion = new RegionPrototypeRegion();
		this.greenRegion = new RegionPrototypeRegion();
		this.blueRegion = new RegionPrototypeRegion();
	}

	/**
	 * Getter method for the redRegion.
	 * @return the redRegion
	 */
	public RegionPrototypeRegion getRedRegion() {
		return redRegion;
	}

	/**
	 * Getter method for the greenRegion.
	 * @return the greenRegion
	 */
	public RegionPrototypeRegion getGreenRegion() {
		return greenRegion;
	}

	/**
	 * Getter method for the blueRegion.
	 * @return the blueRegion
	 */
	public RegionPrototypeRegion getBlueRegion() {
		return blueRegion;
	}
	
	/**
	 * Creates a prepared region container with the R, G, B regions.
	 * @return the region container
	 */
	public RegionContainer createRegionContainer() {
		RegionContainer container = new RegionContainer();
		container.setRegions(new ArrayList<RegionPrototypeRegion>());
		container.getRegions().add(redRegion);
		container.getRegions().add(greenRegion);
		container.getRegions().add(blueRegion);
		container.prepare();
		return container;
	}
	
	/**
	 * @return the image
	 */
	public BufferedImage generateExperimentalMaskImage() {
		RegionContainer regionContainer = createRegionContainer();
		BufferedImage image = new BufferedImage(200, 150, BufferedImage.TYPE_INT_RGB);
		regionContainer.startFrame();
		for (int y=0; y<image.getHeight(); y++) {
			regionContainer.startLine();
			for (int x=0; x<image.getWidth(); x++) {
				regionContainer.startPixel();
				int mask = regionContainer.getCurrentMask();
				image.getRaster().setSample(x, y, 0, (mask & 1) != 0 ? 255 : 0);
				image.getRaster().setSample(x, y, 1, (mask & 2) != 0 ? 255 : 0);
				image.getRaster().setSample(x, y, 2, (mask & 4) != 0 ? 255 : 0);
			}
		}
		return image;
	}
	
	/**
	 * @return the image
	 */
	public BufferedImage generateReferenceMaskImage() {
		BufferedImage image = new BufferedImage(200, 150, BufferedImage.TYPE_INT_RGB);
		drawReferenceMask(image, redRegion, 0);
		drawReferenceMask(image, greenRegion, 1);
		drawReferenceMask(image, blueRegion, 2);
		return image;
	}
	
	private void drawReferenceMask(BufferedImage image, RegionPrototypeRegion region, int band) {
		for (int dx=0; dx<region.getWidth(); dx++) {
			for (int dy=0; dy<region.getHeight(); dy++) {
				image.getRaster().setSample(region.getX() + dx, region.getY() + dy, band, 255);
			}
		}
	}
	
	private static boolean imagesEqual(BufferedImage a, BufferedImage b) {
		
		// different image sizes signal a fundamental usage error, not a rendering error
		if (a.getWidth() != b.getWidth() || a.getHeight() != b.getHeight()) {
			throw new IllegalArgumentException("different image sizes");
		}
		int width = a.getWidth(), height = a.getHeight();
		
		// compare pixels
		for (int x=0; x<width; x++) {
			for (int y=0; y<height; y++) {
				if (a.getRGB(x, y) != b.getRGB(x, y)) {
					return false;
				}
			}
		}
		
		return true;
	}

	private static void saveImage(BufferedImage image, String filename) throws IOException {
		ImageIO.write(image, "png", new File(filename));
	}
	
	/**
	 * Generates experimental and reference images, compares them (results written to stdout),
	 * and saves both images to a file.
	 * @param filenamePattern  the filename pattern containing a dollar sign that is replaced
	 * by an 'x' (experimental) or 'r' (reference).
	 * @throws IOException on I/O errors
	 * 
	 */
	public void run(String filenamePattern) throws IOException {
		
		// generate images
		BufferedImage experimentalMaskImage = generateExperimentalMaskImage();
		BufferedImage referenceMaskImage = generateReferenceMaskImage();
		
		// compare
		System.out.println("* " + imagesEqual(experimentalMaskImage, referenceMaskImage));
		
		// save
		saveImage(experimentalMaskImage, filenamePattern.replace('$', 'x'));
		saveImage(referenceMaskImage, filenamePattern.replace('$', 'r'));
		
	}
	
}
