/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.ecosite.page;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * This class keeps track about all pages and their meta-data.
 */
public class PageRegistry {

	/**
	 * the instance
	 */
	public static final PageRegistry instance = new PageRegistry();

	/**
	 * the metaData
	 */
	private SortedMap<String, PageMetaData> metaData;
	
	/**
	 * Constructor.
	 */
	private PageRegistry() {
		this.metaData = new TreeMap<String, PageMetaData>();
		try {
			initialize(null, new File("pages"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * @param prefix
	 * @param folder
	 */
	private void initialize(String prefix, File folder) throws IOException {
		for (File file : folder.listFiles()) {
			String path = (prefix == null ? file.getName() : (prefix + '/' + file.getName()));
			if (file.isFile()) {
				if (file.getName().endsWith(".ftl")) {
					registerFile(path.substring(0, path.length() - 4), file);
				}
			} else if (file.isDirectory()) {
				initialize(path, file);
			}
		}
	}
	
	/**
	 * @param pagePath
	 * @param file
	 */
	private void registerFile(String pagePath, File file) throws IOException {
		PageMetaData data = new PageMetaData();
		data.setPagePath(pagePath);
		
		String title = extractTitle(file);
		title = (title == null ? pagePath : title);
		data.setTitle(title);
		
		metaData.put(pagePath, data);
	}
	
	/**
	 * @param file
	 * @return
	 */
	private String extractTitle(File file) throws IOException {
		String firstLine = readFirstLine(file);
		if (firstLine == null) {
			return null;
		}
		int startIndex = firstLine.indexOf("<#--");
		if (startIndex == -1) {
			return null;
		}
		int endIndex = firstLine.indexOf("-->", startIndex);
		if (endIndex == -1) {
			return null;
		}
		return firstLine.substring(startIndex + 4, endIndex).trim();
	}
	
	/**
	 * @param file
	 * @return
	 */
	private String readFirstLine(File file) throws IOException {
		FileReader fr = new FileReader(file);
		LineNumberReader lnr = new LineNumberReader(fr);
		String firstLine = lnr.readLine();
		lnr.close();
		fr.close();
		return firstLine;
	}
	
	/**
	 * @param pagePath the page path for a page
	 * @return the meta-data for the specified page
	 */
	public PageMetaData getMetaData(String pagePath) {
		return metaData.get(pagePath);
	}

	/**
	 * @return an iterator for all page meta-datas, sorted by page path
	 */
	public Iterable<PageMetaData> getAllMetaDatas() {
		return metaData.values();
	}
	
}
