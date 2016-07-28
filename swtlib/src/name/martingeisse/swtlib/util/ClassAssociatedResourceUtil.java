/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.swtlib.util;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;

/**
 * This utility class provides methods to load class-associated
 * resources. These are classpath resources whose name is
 * either OwnerClassName.extension or
 * OwnerClassName_subName.extension, where OwnerClassName is the
 * class with which the resource is associated, and the sub-name
 * and extension can be chosen freely.
 */
public class ClassAssociatedResourceUtil {

	/**
	 * Opens an {@link InputStream} for a class-associated resource.
	 * @param ownerClass the owner class
	 * @param subName the resource sub-name, or null to open the
	 * resource that is named like the class
	 * @param extension the file name extension
	 * @return the input stream
	 */
	public static InputStream getInputStream(Class<?> ownerClass, String subName, String extension) {
		String localName;
		if (subName == null) {
			localName = ownerClass.getSimpleName() + '.' + extension;
		} else {
			localName = ownerClass.getSimpleName() + '_' + subName + '.' + extension;
		}
		return ownerClass.getResourceAsStream(localName);
	}
	
	/**
	 * Loads a class-associated image resource.
	 * @param ownerClass the owner class
	 * @param subName the resource sub-name, or null to open the
	 * resource that is named like the class
	 * @param extension the file name extension
	 * @param device the SWT device to load the image for
	 * @return the image
	 */
	public static Image loadImage(Class<?> ownerClass, String subName, String extension, Device device) {
		InputStream imageInputStream = getInputStream(ownerClass, subName, extension);
		if (imageInputStream == null) {
			throw new RuntimeException("cannot find class-associated image for class " + ownerClass.getName() + ", sub-name " + subName + ", extension " + extension);
		}
		Image image = new Image(device, imageInputStream);
		try {
			imageInputStream.close();
		} catch (IOException e) {
		}
		return image;
	}
	
}
