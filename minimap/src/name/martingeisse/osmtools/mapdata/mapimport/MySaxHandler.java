/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.osmtools.mapdata.mapimport;

import java.io.IOException;
import java.io.Writer;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This SAX handler does the actual data conversion.
 */
class MySaxHandler extends DefaultHandler {

	/**
	 * the out
	 */
	private Writer out;
	
	/**
	 * the builder
	 */
	private StringBuilder builder;
	
	/**
	 * Constructor.
	 * @param out the writer to write the mapdata to
	 */
	public MySaxHandler(Writer out) {
		this.out = out;
		this.builder = new StringBuilder();
	}
	
	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		String name = qName;
		if (name.equals("node")) {
			handleElement(attributes, 'N', "id", "lat", "lon");
		} else if (name.equals("way")) {
			handleElement(attributes, 'W', "id");
		} else if (name.equals("nd")) {
			handleElement(attributes, 'n', "ref");
		} else if (name.equals("tag")) {
			handleElement(attributes, 'T', "k", "v");
		} else if (name.equals("relation")) {
			handleElement(attributes, 'R', "id");
		} else if (name.equals("member")) {
			handleElement(attributes, 'M', "type", "ref", "role");
		} else if (name.equals("osm")) {
			// ignore
		} else {
			throw new SAXException("unknown element: " + name);
		}
	}

	private void handleElement(Attributes attributes, char code, String... attributeNames) throws SAXException {
		try {
			builder.setLength(0);
			builder.append(code);
			for (String attributeName : attributeNames) {
				builder.append(',');
				builder.append(attributes.getValue(attributeName));
			}
			builder.append('\n');
			out.write(builder.toString());
		} catch (IOException e) {
			throw new SAXException(e);
		}
	}
	
}
