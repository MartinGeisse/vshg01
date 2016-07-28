/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.ecobuild;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

/**
 * Test class for {@link ModulePath}.
 */
public class ModulePathTest {

	/**
	 * @param path
	 * @param expectedEscapeLevel
	 * @param expectedSegments
	 */
	private void check(ModulePath path, int expectedEscapeLevel, String... expectedSegments) {
		assertEquals(path.isAbsolute(), expectedEscapeLevel < 0);
		assertEquals(path.getEscapeLevel(), expectedEscapeLevel);
		assertEquals(path.getSegmentCount(), expectedSegments.length);
		for (int i=0; i<expectedSegments.length; i++) {
			assertEquals("segment " + i, expectedSegments[i], path.getSegment(i));
		}
	}
	
	private void testSimpleSupport(int escapeLevel, String... segments) {
		ModulePath path = new ModulePath(escapeLevel, segments);
		check(path, escapeLevel, segments);
	}
	
	/**
	 * @throws Exception on errors
	 */
	@Test
	public void testFixed() throws Exception {
		check(ModulePath.createHere(), 0);
		check(ModulePath.createRoot(), -1);
	}
	
	/**
	 * @throws Exception on errors
	 */
	@Test
	public void testSimple() throws Exception {
		for (int i=-1; i<3; i++) {
			testSimpleSupport(i);
			testSimpleSupport(i, "foo");
			testSimpleSupport(i, "foo", "bar");
		}
	}
	
	/**
	 * @throws Exception on errors
	 */
	@Test
	public void testArrayCopy() throws Exception {
		String[] segments = new String[] {"foo"};
		ModulePath path = new ModulePath(0, segments);
		segments[0] = "bar";
		assertEquals("foo", path.getSegment(0));
	}

	/**
	 * @throws Exception on errors
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testNullSegments() throws Exception {
		new ModulePath(0, null);
	}
	
	/**
	 * @throws Exception on errors
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testInvalidEscapeLevel() throws Exception {
		new ModulePath(-2, new String[0]);
	}
	
	/**
	 * @throws Exception on errors
	 */
	@Test(expected = IndexOutOfBoundsException.class)
	public void testGetSegmentNegative() throws Exception {
		new ModulePath(0, new String[] {"abc", "def"}).getSegment(-1);
	}
	
	/**
	 * @throws Exception on errors
	 */
	@Test(expected = IndexOutOfBoundsException.class)
	public void testGetSegmentOutOfBounds() throws Exception {
		new ModulePath(0, new String[] {"abc", "def"}).getSegment(2);
	}
	
	/**
	 * @throws Exception on errors
	 */
	@Test
	public void testGetParent() throws Exception {
		
		// absolute
		ModulePath absolutePath2 = new ModulePath(-1, new String[] {"abc", "def"});
		check(absolutePath2, -1, "abc", "def");
		ModulePath absolutePath1 = absolutePath2.getParent();
		check(absolutePath1, -1, "abc");
		ModulePath absolutePath0 = absolutePath1.getParent();
		check(absolutePath0, -1);
		assertNull(absolutePath0.getParent());
		
		// relative
		ModulePath relativePath2 = new ModulePath(6, new String[] {"abc", "def"});
		check(relativePath2, 6, "abc", "def");
		ModulePath relativePath1 = relativePath2.getParent();
		check(relativePath1, 6, "abc");
		ModulePath relativePath0 = relativePath1.getParent();
		check(relativePath0, 6);
		ModulePath relativePathA = relativePath0.getParent();
		check(relativePathA, 7);
		ModulePath relativePathB = relativePathA.getParent();
		check(relativePathB, 8);
		
	}
	
	/**
	 * @throws Exception on errors
	 */
	@Test
	public void testGetChild() throws Exception {
	
		ModulePath absolutePath = new ModulePath(-1, new String[] {"abc", "def"});
		ModulePath absolutePathChild = absolutePath.getChild("foo");
		check(absolutePathChild, -1, "abc", "def", "foo");
		check(absolutePathChild.getChild("bar"), -1, "abc", "def", "foo", "bar");

		ModulePath relativePath = new ModulePath(2, new String[] {"abc", "def"});
		ModulePath relativePathChild = relativePath.getChild("foo");
		check(relativePathChild, 2, "abc", "def", "foo");
		check(relativePathChild.getChild("bar"), 2, "abc", "def", "foo", "bar");

		ModulePath purelyEscapingPath = new ModulePath(2, new String[] {});
		ModulePath purelyEscapingPathChild = purelyEscapingPath.getChild("foo");
		check(purelyEscapingPathChild, 2, "foo");
		check(purelyEscapingPathChild.getChild("bar"), 2, "foo", "bar");
		
	}
	
	/**
	 * @throws Exception on errors
	 */
	@Test
	public void testGetChildOfParent() throws Exception {

		// first create all paths, then check -- this tests that no path gets destroyed by array sharing
		
		ModulePath absolutePath = new ModulePath(-1, new String[] {"abc", "def"});
		ModulePath absoluteTest1 = absolutePath.getParent().getChild("foo");
		ModulePath absoluteTest2 = absolutePath.getParent().getChild("foo").getChild("bar");
		ModulePath absoluteTest3 = absolutePath.getParent().getParent().getChild("foo").getChild("bar");
		check(absoluteTest1, -1, "abc", "foo");
		check(absoluteTest2, -1, "abc", "foo", "bar");
		check(absoluteTest3, -1, "foo", "bar");

		ModulePath relativePath = new ModulePath(1, new String[] {"abc", "def"});
		ModulePath relativeTest1 = relativePath.getParent().getChild("foo");
		ModulePath relativeTest2 = relativePath.getParent().getChild("foo").getChild("bar");
		ModulePath relativeTest3 = relativePath.getParent().getParent().getChild("foo").getChild("bar");
		ModulePath relativeTest4 = relativePath.getParent().getParent().getParent().getChild("foo").getChild("bar");
		check(relativeTest1, 1, "abc", "foo");
		check(relativeTest2, 1, "abc", "foo", "bar");
		check(relativeTest3, 1, "foo", "bar");
		check(relativeTest4, 2, "foo", "bar");
		
	}
	
	/**
	 * @throws Exception on errors
	 */
	@Test
	public void testGetParentOfChild() throws Exception {
		
		ModulePath absolutePath = new ModulePath(-1, new String[] {"abc", "def"});
		check(absolutePath.getChild("foo").getParent(), -1, "abc", "def");
		check(absolutePath.getChild("foo").getChild("bar").getParent(), -1, "abc", "def", "foo");
		check(absolutePath.getChild("foo").getParent().getParent(), -1, "abc");

		ModulePath relativePath = new ModulePath(2, new String[] {"abc", "def"});
		check(relativePath.getChild("foo").getParent(), 2, "abc", "def");
		check(relativePath.getChild("foo").getChild("bar").getParent(), 2, "abc", "def", "foo");
		check(relativePath.getChild("foo").getParent().getParent(), 2, "abc");
		check(relativePath.getChild("foo").getParent().getParent().getParent(), 2);
		check(relativePath.getChild("foo").getParent().getParent().getParent().getParent(), 3);
		
	}

	/**
	 * @param x
	 * @param y
	 */
	private void checkConcatFail(ModulePath x, ModulePath y) {
		try {
			x.concat(y);
			fail();
		} catch (UserMessageBuildException e) {
		}
	}
	
	/**
	 * @throws Exception on errors
	 */
	@Test
	public void testConcat() throws Exception {

		ModulePath rootPath = ModulePath.createRoot();
		ModulePath herePath = ModulePath.createHere();
		ModulePath absolutePath = new ModulePath(-1, new String[] {"abc"});
		ModulePath forwardRelativePath = new ModulePath(0, new String[] {"foo", "bar"});
		ModulePath escapingRelativePath = new ModulePath(1, new String[] {"def"});
		ModulePath purelyEscaping = new ModulePath(1, new String[] {});
		
		// anything concatenated with an absolute path yields the latter
		check(rootPath.concat(rootPath), -1);
		check(herePath.concat(rootPath), -1);
		check(absolutePath.concat(rootPath), -1);
		check(forwardRelativePath.concat(rootPath), -1);
		check(escapingRelativePath.concat(rootPath), -1);
		check(purelyEscaping.concat(rootPath), -1);

		// anything concatenated with an absolute path yields the latter
		check(rootPath.concat(absolutePath), -1, "abc");
		check(herePath.concat(absolutePath), -1, "abc");
		check(absolutePath.concat(absolutePath), -1, "abc");
		check(forwardRelativePath.concat(absolutePath), -1, "abc");
		check(escapingRelativePath.concat(absolutePath), -1, "abc");
		check(purelyEscaping.concat(absolutePath), -1, "abc");
		
		// anything concatenated with the "here" path yields the former
		check(rootPath.concat(herePath), -1);
		check(herePath.concat(herePath), 0);
		check(absolutePath.concat(herePath), -1, "abc");
		check(forwardRelativePath.concat(herePath), 0, "foo", "bar");
		check(escapingRelativePath.concat(herePath), 1, "def");
		check(purelyEscaping.concat(herePath), 1);
		
		// other cases missing above
		check(rootPath.concat(forwardRelativePath), -1, "foo", "bar");
		checkConcatFail(rootPath, escapingRelativePath);
		checkConcatFail(rootPath, purelyEscaping);
		check(herePath.concat(forwardRelativePath), 0, "foo", "bar");
		check(herePath.concat(escapingRelativePath), 1, "def");
		check(herePath.concat(purelyEscaping), 1);
		check(absolutePath.concat(forwardRelativePath), -1, "abc", "foo", "bar");
		check(absolutePath.concat(escapingRelativePath), -1, "def");
		check(absolutePath.concat(purelyEscaping), -1);
		check(forwardRelativePath.concat(forwardRelativePath), 0, "foo", "bar", "foo", "bar");
		check(forwardRelativePath.concat(escapingRelativePath), 0, "foo", "def");
		check(forwardRelativePath.concat(purelyEscaping), 0, "foo");
		check(escapingRelativePath.concat(forwardRelativePath), 1, "def", "foo", "bar");
		check(escapingRelativePath.concat(escapingRelativePath), 1, "def");
		check(escapingRelativePath.concat(purelyEscaping), 1);
		check(purelyEscaping.concat(forwardRelativePath), 1, "foo", "bar");
		check(purelyEscaping.concat(escapingRelativePath), 2, "def");
		check(purelyEscaping.concat(purelyEscaping), 2);

		// a complex case
		ModulePath complexPath1 = new ModulePath(15, new String[] {"abc", "def", "ghi", "jkl"});
		ModulePath complexPath2 = new ModulePath(2, new String[] {"foo", "bar", "baz"});
		ModulePath complexResult = complexPath1.concat(complexPath2);
		check(complexResult, 15, "abc", "def", "foo", "bar", "baz");
		
	}
	
	/**
	 * @throws Exception on errors
	 */
	@Test
	public void testConcatParent() throws Exception {
		ModulePath path1 = new ModulePath(15, new String[] {"abc", "def", "ghi", "jkl"});
		ModulePath path2 = new ModulePath(2, new String[] {"foo", "bar", "baz"});
		ModulePath result = path1.getParent().concat(path2.getParent());
		check(result, 15, "abc", "foo", "bar");
	}
	
	/**
	 * @throws Exception on errors
	 */
	@Test
	public void testResolveAbsolute() throws Exception {
		ModulePath modulePath = new ModulePath(-1, new String[] {"foo", "bar"});
		File rootFilePath = new File(new File("/abc"), "def");
		File result = modulePath.resolveAbsolute(rootFilePath);
		assertEquals("/abc/def/foo/bar", result.getPath());
	}
	
	/**
	 * @throws Exception on errors
	 */
	@Test(expected = UserMessageBuildException.class)
	public void testResolveAbsoluteOnRelative() throws Exception {
		new ModulePath(0, new String[] {"foo", "bar"}).resolveAbsolute(new File("/abc"));
	}
	
	/**
	 * @throws Exception on errors
	 */
	@Test
	public void testResolveAbsoluteParent() throws Exception {
		ModulePath modulePath = new ModulePath(-1, new String[] {"foo", "bar"}).getParent();
		File rootFilePath = new File(new File("/abc"), "def");
		File result = modulePath.resolveAbsolute(rootFilePath);
		assertEquals("/abc/def/foo", result.getPath());
	}
	
	/**
	 * @throws Exception on errors
	 */
	@Test
	public void testResolveOnAbsolute() throws Exception {
		ModulePath modulePath = new ModulePath(-1, new String[] {"foo", "bar", "baz"}).getParent();
		File rootFolderPath = new File(new File("/abc"), "def");
		File currentFolderPath = new File(new File(rootFolderPath, "/ghi"), "jkl");
		File result = modulePath.resolve(rootFolderPath, currentFolderPath);
		assertEquals("/abc/def/foo/bar", result.getPath());
	}
	
	/**
	 * @throws Exception on errors
	 */
	@Test
	public void testResolveOnRelative() throws Exception {
		ModulePath modulePath = new ModulePath(1, new String[] {"foo", "bar", "baz"}).getParent();
		File rootFolderPath = new File(new File("/abc"), "def");
		File currentFolderPath = new File(new File(rootFolderPath, "/ghi"), "jkl");
		File result = modulePath.resolve(rootFolderPath, currentFolderPath);
		assertEquals("/abc/def/ghi/foo/bar", result.getPath());
	}
	
	/**
	 * @throws Exception on errors
	 */
	@Test(expected = UserMessageBuildException.class)
	public void testResolveOnRelativeEscapeRoot() throws Exception {
		ModulePath modulePath = new ModulePath(3, new String[] {"foo"});
		File rootFolderPath = new File(new File("/abc"), "def");
		File currentFolderPath = new File(new File(rootFolderPath, "/ghi"), "jkl");
		modulePath.resolve(rootFolderPath, currentFolderPath);
	}
	
	/**
	 * @throws Exception on errors
	 */
	@Test
	public void testToString() throws Exception {
		
		ModulePath rootPath = ModulePath.createRoot();
		assertEquals("/", rootPath.toString());
		
		ModulePath herePath = ModulePath.createHere();
		assertEquals(".", herePath.toString());
		
		ModulePath absolutePath = new ModulePath(-1, new String[] {"abc"});
		assertEquals("/abc", absolutePath.toString());
		
		ModulePath forwardRelativePath = new ModulePath(0, new String[] {"foo", "bar"});
		assertEquals("foo/bar", forwardRelativePath.toString());
		
		ModulePath escapingRelativePath = new ModulePath(1, new String[] {"def"});
		assertEquals("../def", escapingRelativePath.toString());
		
		ModulePath purelyEscapingPath = new ModulePath(1, new String[] {});
		assertEquals("..", purelyEscapingPath.toString());
		
		ModulePath purelyEscapingTwicePath = new ModulePath(2, new String[] {});
		assertEquals("../..", purelyEscapingTwicePath.toString());
		
		ModulePath complexAbsolutePath = new ModulePath(-1, new String[] {"abc", "def", "ghi"});
		assertEquals("/abc/def/ghi", complexAbsolutePath.toString());
		
		ModulePath complexRelativePath = new ModulePath(2, new String[] {"jkl", "mno", "pqr"});
		assertEquals("../../jkl/mno/pqr", complexRelativePath.toString());
		
	}

	private void checkEquals(ModulePath path1, ModulePath path2, boolean expectedResult) {
		assertEquals(expectedResult, path1.equals(path2));
		assertEquals(expectedResult, path2.equals(path1));
		if (expectedResult) {
			assertEquals(path1.hashCode(), path2.hashCode());
		}
	}
	
	/**
	 * @throws Exception on errors
	 */
	@Test
	public void testHashCodeEquals() throws Exception {

		ModulePath rootPath = ModulePath.createRoot();
		ModulePath herePath = ModulePath.createHere();
		ModulePath absolutePath = new ModulePath(-1, new String[] {"abc"});
		ModulePath forwardRelativePath = new ModulePath(0, new String[] {"foo", "bar"});
		ModulePath escapingRelativePath = new ModulePath(1, new String[] {"def"});
		ModulePath purelyEscaping = new ModulePath(1, new String[] {});
		ModulePath[] paths = {
			rootPath,
			herePath,
			absolutePath,
			forwardRelativePath,
			escapingRelativePath,
			purelyEscaping,
		};

		// the above paths are all different
		for (ModulePath path1 : paths) {
			for (ModulePath path2 : paths) {
				checkEquals(path1, path2, path1 == path2);
			}
		}
		
		// compare each path with a second equal instance
		checkEquals(rootPath, ModulePath.createRoot(), true);
		checkEquals(herePath, ModulePath.createHere(), true);
		checkEquals(absolutePath, new ModulePath(-1, new String[] {"abc"}), true);
		checkEquals(forwardRelativePath, new ModulePath(0, new String[] {"foo", "bar"}), true);
		checkEquals(escapingRelativePath, new ModulePath(1, new String[] {"def"}), true);
		checkEquals(purelyEscaping, new ModulePath(1, new String[] {}), true);
		
		// test each path property individually
		checkEquals(new ModulePath(-1, new String[] {"foo", "bar"}), new ModulePath(-1, new String[] {"foo", "bar"}), true);
		checkEquals(new ModulePath(-1, new String[] {"foo", "bar"}), new ModulePath(-1, new String[] {"foo", "baz"}), false);
		checkEquals(new ModulePath(-1, new String[] {"foo", "bar"}), new ModulePath(3, new String[] {"foo", "bar"}), false);
		checkEquals(new ModulePath(2, new String[] {"foo", "bar"}), new ModulePath(3, new String[] {"foo", "bar"}), false);
		checkEquals(new ModulePath(3, new String[] {"foo", "bar"}), new ModulePath(3, new String[] {"foo", "bar"}), true);
		
		// test parent paths
		checkEquals(new ModulePath(-1, new String[] {"foo", "bar"}), new ModulePath(-1, new String[] {"foo", "bar"}).getParent(), false);
		checkEquals(new ModulePath(-1, new String[] {"foo", "bar"}).getParent(), new ModulePath(-1, new String[] {"foo", "baz"}).getParent(), true);
		checkEquals(new ModulePath(2, new String[] {"foo", "bar"}), new ModulePath(2, new String[] {"foo", "bar"}).getParent(), false);
		checkEquals(new ModulePath(2, new String[] {"foo", "bar"}).getParent(), new ModulePath(2, new String[] {"foo", "baz"}).getParent(), true);
		checkEquals(new ModulePath(2, new String[] {}).getParent(), new ModulePath(3, new String[] {}), true);

		// equals null
		assertFalse(new ModulePath(2, new String[] {"foo", "bar"}).equals(null));
		
	}
	
	
	/**
	 * @throws Exception on errors
	 */
	@Test
	public void testParseConstructor() throws Exception {
		
		// special syntaxes
		check(new ModulePath("/"), -1);
		check(new ModulePath("."), 0);

		// absolute paths
		check(new ModulePath("/abc"), -1, "abc");
		check(new ModulePath("/abc/def"), -1, "abc", "def");
		check(new ModulePath("/abc/def/ghi"), -1, "abc", "def", "ghi");

		// purely escaping paths
		check(new ModulePath(".."), 1);
		check(new ModulePath("../.."), 2);
		check(new ModulePath("../../.."), 3);
		
		// purely nonescaping relative paths
		check(new ModulePath("abc"), 0, "abc");
		check(new ModulePath("abc/def"), 0, "abc", "def");
		check(new ModulePath("abc/def/ghi"), 0, "abc", "def", "ghi");
		
		// impure relative paths
		check(new ModulePath("../abc"), 1, "abc");
		check(new ModulePath("../abc/def"), 1, "abc", "def");
		check(new ModulePath("../../abc"), 2, "abc");
		check(new ModulePath("../../abc/def"), 2, "abc", "def");
		
	}
	
	private void checkParseError(String text) {
		try {
			new ModulePath(text);
			fail();
		} catch (UserMessageBuildException e) {
		}
	}
	
	/**
	 * @throws Exception on errors
	 */
	@Test
	public void testParseError() throws Exception {
		
		checkParseError("");
		checkParseError("//");
		checkParseError("/../abc");
		checkParseError("abc//def");
		checkParseError("abc/def/");
		
		checkParseError("abc/../def");
		checkParseError("abc/..");
		checkParseError("abc/.");
		checkParseError("abc/./def");
		checkParseError("./def");
		
	}

	private void checkGetChildInvalidSegment(String segment) {
		try {
			new ModulePath("/abc/def").getChild(segment);
			fail();
		} catch (UserMessageBuildException e) {
		}
	}
	
	/**
	 * @throws Exception on errors
	 */
	@Test
	public void testGetChildInvalidSegment() throws Exception {
		checkGetChildInvalidSegment(".");
		checkGetChildInvalidSegment("/");
		checkGetChildInvalidSegment("..");
		checkGetChildInvalidSegment("abc/");
		checkGetChildInvalidSegment("abc/def");
		checkGetChildInvalidSegment("/def");
	}

	/**
	 * @throws Exception on errors
	 */
	@Test
	public void testBackMappingConstructor() throws Exception {
		File root = new File(new File("foo"), "bar");
		File sub = new File(root, "abc");
		File subsub = new File(sub, "def");
		check(new ModulePath(root, root), -1);
		check(new ModulePath(root, sub), -1, "abc");
		check(new ModulePath(root, subsub), -1, "abc", "def");
	}
	
}
