/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.ecogui.prototype.region;

import java.io.IOException;
import java.util.ArrayList;

/**
 * TODO: document me
 *
 */
public class Main {

	/**
	 * @param args ...
	 * @throws IOException on I/O errors
	 */
	public static void main(String[] args) throws IOException {
		
		// 10, 35, 60, 90, 115, 140
		RegionPrototypeRgbRun run = new RegionPrototypeRgbRun();
		run.getRedRegion().setX(10);
		run.getRedRegion().setY(10);
		run.getRedRegion().setWidth(80);
		run.getRedRegion().setHeight(60);
		run.getGreenRegion().setX(60);
		run.getGreenRegion().setY(30);
		run.getGreenRegion().setWidth(80);
		run.getGreenRegion().setHeight(60);
		run.getBlueRegion().setX(35);
		run.getBlueRegion().setY(50);
		run.getBlueRegion().setWidth(80);
		run.getBlueRegion().setHeight(60);
		run.run("test-a-$.png");

		// 10, 35, 60, 90, 115, 140
		run = new RegionPrototypeRgbRun();
		run.getRedRegion().setX(10);
		run.getRedRegion().setY(10);
		run.getRedRegion().setWidth(100);
		run.getRedRegion().setHeight(60);
		run.getGreenRegion().setX(10);
		run.getGreenRegion().setY(30);
		run.getGreenRegion().setWidth(30);
		run.getGreenRegion().setHeight(30);
		run.getBlueRegion().setX(80);
		run.getBlueRegion().setY(30);
		run.getBlueRegion().setWidth(30);
		run.getBlueRegion().setHeight(30);
		run.run("test-b-$.png");
		
		RegionPrototypeOverlapRun run2 = new RegionPrototypeOverlapRun();
		run2.getContainer().setRegions(new ArrayList<RegionPrototypeRegion>());
		RegionPrototypeRegion region1 = new RegionPrototypeRegion();
		region1.setX(10);
		region1.setY(10);
		region1.setWidth(80);
		region1.setHeight(60);
		run2.getContainer().getRegions().add(region1);
		RegionPrototypeRegion region2 = new RegionPrototypeRegion();
		region2.setX(60);
		region2.setY(30);
		region2.setWidth(80);
		region2.setHeight(60);
		run2.getContainer().getRegions().add(region2);
		RegionPrototypeRegion region3 = new RegionPrototypeRegion();
		region3.setX(35);
		region3.setY(50);
		region3.setWidth(80);
		region3.setHeight(60);
		run2.getContainer().getRegions().add(region3);
		run2.run("run2.png");
		
	}
	
}
