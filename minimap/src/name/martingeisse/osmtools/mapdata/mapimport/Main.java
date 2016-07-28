/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.osmtools.mapdata.mapimport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

/**
 * This tool converts a .osm file to a .mapdata file. 
 */
public class Main {

	/**
	 * Main method.
	 * @param args command line arguments.
	 * @throws IOException on I/O errors
	 * @throws SAXException ...
	 * @throws ParserConfigurationException ... 
	 */
	public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {
	
		// consume arguments
		if (args.length < 1 || args.length > 2) {
			System.err.println("usage: java ...Main infile.osm [outfile.mapdata] ");
			System.exit(1);
		}
		if (!args[0].endsWith(".osm")) {
			System.err.println("input filename must end with .osm");
			System.exit(1);
		}
		if (args.length == 2 && !args[1].endsWith(".mapdata")) {
			System.err.println("output filename must end with .mapdata");
			System.exit(1);
		}
		String inputFilename = args[0];
		String outputFilename = (args.length == 2 ? args[1] : (args[0].substring(0, args[0].length() - 4) + ".mapdata"));
		
		// do the conversion
		FileOutputStream outStream = new FileOutputStream(outputFilename);
		OutputStreamWriter outWriter = new OutputStreamWriter(outStream, "utf-8");
		MySaxHandler mySaxHandler = new MySaxHandler(outWriter);
		SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
		// saxParser.getXMLReader().setContentHandler(mySaxHandler);
		saxParser.parse(new File(inputFilename), mySaxHandler);
		outWriter.flush();
		outWriter.close();
		outStream.flush();
		outStream.close();
		  
	}

}
