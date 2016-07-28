/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.ecotools.common.util;

import static org.junit.Assert.fail;

import java.util.Arrays;

import org.junit.Test;

/**
 * 
 */
public class ArrayUtilTest {

	/**
	 * 
	 */
	@Test
	public void testSelectByIndexArray() {
		String[] originalArray = {"foo", "bar", "baz", "one", "two", "three"};
		int[] indexArray = {3, 4, 1, 3, 2};
		String[] selected = ArrayUtil.selectByIndexArray(originalArray, indexArray);
		String[] expected = {"one", "two", "bar", "one", "baz"};
		if (!Arrays.equals(expected, selected)) {
			fail("expected: " + Arrays.toString(expected) + ", selected: " + Arrays.toString(selected));
		}
	}

	/**
	 * 
	 */
	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void testSelectByIndexArrayOutOfBounds() {
		String[] originalArray = {"foo", "bar", "baz", "one", "two", "three"};
		int[] indexArray = {6};
		ArrayUtil.selectByIndexArray(originalArray, indexArray);
	}

	/**
	 * 
	 */
	@Test
	public void testSelectByIndexArray_int() {
		int[] originalArray = {5, 17, 20, -1, 0, 100};
		int[] indexArray = {3, 4, 1, 3, 2};
		int[] selected = ArrayUtil.selectByIndexArray(originalArray, indexArray);
		int[] expected = {-1, 0, 17, -1, 20};
		if (!Arrays.equals(expected, selected)) {
			fail("expected: " + Arrays.toString(expected) + ", selected: " + Arrays.toString(selected));
		}
	}

	/**
	 * 
	 */
	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void testSelectByIndexArrayOutOfBounds_int() {
		int[] originalArray = {5, 17, 20, -1, 0, 100};
		int[] indexArray = {6};
		ArrayUtil.selectByIndexArray(originalArray, indexArray);
	}

}
