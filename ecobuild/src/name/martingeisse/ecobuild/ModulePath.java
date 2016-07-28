/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.ecobuild;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import name.martingeisse.ecobuild.util.EcobuildFileUtil;

/**
 * A reference to a module or to a file in a module. Module paths resemble file paths
 * but limit their format. Like a file path, a module path can refer to a file or
 * folder and its target need not exist. This class represents a parsed module path,
 * i.e. an instance of this class is at least guaranteed to be syntactically
 * well-formed, and allows access to path properties at a structural (not textual) level.
 * 
 * Module paths come in two basic variations, relative and absolute. These have the
 * same meaning as for file paths. The meaning of a relative module path is
 * only defined in the context of a "current folder". The meaning of either kind
 * of path is only properly defined in the context of a "root folder". This root
 * folder corresponds to a folder in the file system, but usually not the root
 * folder ofthe file system itself. One main feature of module paths is being
 * confined to the inside of that root folder. Another feature is that a single
 * module path can denote two folders when used with two root folders, such as the
 * module source root and module build root.
 * 
 * Module paths are defined in terms of segments. A segment must not include a
 * slash character. The segment ".." is the special "escape" segment. The segment
 * name "." is not allowed; however, the module path "." is a special syntax for
 * a relative path that denotes the current folder. All other segments are normal
 * segments. The segments of a path are separated by slash characters.
 * 
 * Empty module paths are not allowed. Use "." to refer to the current folder.
 * 
 * Absolute module paths begin with a slash character, followed by zero or more
 * normal segments. These segments recursively determine the subfolders to go
 * to, starting at the root folder. Absolute module paths must not contain
 * any escape segments.
 * 
 * Relative module paths do not start with a slash character but directly with
 * the first segment. All escape segments must come at the beginning. (Formally,
 * no normal segment may be followed by an escape segment.) It is entirely
 * valid for a relative module path to contain only escape segments or only
 * normal segments. Such a module path is called "purely escaping", and
 * "purely non-escaping", respectively.
 * 
 * Two module paths can be concatenated if the second path is relative, with the
 * obvious folder traversal semantics. If the first path is absolute, the
 * concatenation must not escape the root folder.
 * 
 * Any absolute module path can be converted to a file path in the context of
 * a root folder from the file system. Any relative module path can be
 * converted to a file path in the context of a root folder and current folder,
 * both from the file system, with the current folder being either the root
 * folder or a folder contained in it. For relative module paths, the root
 * folder is solely needed to prevent escaping the root folder.
 * 
 * Implementation: The segments are stored as a sequence of strings. No direct
 * access to the internal array is allowed to guarantee immutability. This class
 * also defines an escape level, which is -1 for absolute paths, and which is
 * equal to the number of escape segments at the beginning for relative paths.
 * 
 * Really internal implementation: This class also uses a usedSegments field
 * that defines the number of segments actually used. This allows to share
 * the segments array in certain operations (the array must be immutable, of
 * course).
 */
public final class ModulePath {

	/**
	 * the NO_SEGMENTS
	 */
	private static final String[] NO_SEGMENTS = new String[0];
	
	/**
	 * the escapeLevel
	 */
	private final int escapeLevel;
	
	/**
	 * the segments
	 */
	private final String[] segments;
	
	/**
	 * the usedSegments
	 */
	private final int usedSegments;
	
	/**
	 * @return a module path for the root folder
	 */
	public static final ModulePath createRoot() {
		return new ModulePath(-1, NO_SEGMENTS, 0);
	}

	/**
	 * @return a module path for the current folder
	 */
	public static final ModulePath createHere() {
		return new ModulePath(0, NO_SEGMENTS, 0);
	}
	
	/**
	 * Constructor.
	 * @param escapeLevel the escape level, or -1 to indicate an absolute path
	 * @param segments the segments of the path
	 */
	public ModulePath(int escapeLevel, String[] segments) {
		this(escapeLevel, Arrays.copyOf(segmentsNullCheck(segments), segments.length), segments.length);
	}
	
	/**
	 * @param segments
	 * @return
	 */
	private static String[] segmentsNullCheck(String[] segments) {
		if (segments == null) {
			throw new IllegalArgumentException("segments argument is null");
		}
		return segments;
	}

	/**
	 * Internal constructor.
	 * @param escapeLevel
	 * @param segments
	 * @param internal
	 */
	private ModulePath(int escapeLevel, String[] segments, int usedSegments) {
		if (escapeLevel < -1 || usedSegments < 0 || usedSegments > segments.length) {
			throw new IllegalArgumentException();
		}
		this.escapeLevel = escapeLevel;
		this.segments = segments;
		this.usedSegments = usedSegments;
	}
	
	/**
	 * Parse constructor.
	 * @param pathText the path text to parse
	 */
	public ModulePath(String pathText) {
		
		// for error messages
		String originalPathText = pathText;
		
		// null-check
		if (pathText == null) {
			throw new UserMessageBuildException("module path is null");
		}

		// an empty path is malformed
		if (pathText.isEmpty()) {
			throw new UserMessageBuildException("module path is empty");
		}
		
		// the "current folder" path
		if (pathText.equals(".")) {
			this.escapeLevel = 0;
			this.segments = NO_SEGMENTS;
			this.usedSegments = 0;
			return;
		}
		
		// absolute paths vs. relative paths
		if (pathText.charAt(0) == '/') {
			
			// absolute paths
			this.escapeLevel = -1;
			pathText = pathText.substring(1);
			
			// handle root path
			if (pathText.isEmpty()) {
				this.segments = NO_SEGMENTS;
				this.usedSegments = 0;
				return;
			}
			
		} else {
			
			// parse "../" prefixes
			int level = 0;
			while (pathText.startsWith("../")) {
				pathText = pathText.substring(3);
				level++;
			}
			
			// handle purely escaping paths
			if (pathText.equals("..")) {
				this.escapeLevel = level + 1;
				this.segments = NO_SEGMENTS;
				this.usedSegments = 0;
				return;
			}
			
			// relative paths that are not purely escaping
			this.escapeLevel = level;
			
		}
		
		// parse normal segments
		List<String> segmentList = new ArrayList<String>();
		while (true) {
			int slashIndex = pathText.indexOf('/');
			if (slashIndex == -1) {
				break;
			}
			if (slashIndex == 0) {
				throw new UserMessageBuildException("Path contains double-slash: " + originalPathText);
			}
			String segment = pathText.substring(0, slashIndex);
			ensureValidSegment(segment, originalPathText);
			segmentList.add(segment);
			pathText = pathText.substring(slashIndex + 1);
		}
		
		// parse last segment
		if (pathText.isEmpty()) {
			throw new UserMessageBuildException("Path contains trailing slash: " + originalPathText);
		}
		ensureValidSegment(pathText, originalPathText);
		segmentList.add(pathText);
		
		// initialize fields
		this.usedSegments = segmentList.size();
		this.segments = segmentList.toArray(new String[usedSegments]);
		
	}

	/**
	 * Folder back-mapping constructor. This constructor creates an absolute
	 * module path from a module folder and a root folder.
	 * 
	 * The root folder must be the folder corresponding to the root module in
	 * the same folder structure as the module folder (that is, for example,
	 * you can't take the module folder from the source tree and the root folder
	 * from the build output tree). The effect of this method is unspecified
	 * if that is not the case.
	 * 
	 * @param rootFolder the root module folder
	 * @param moduleFolder the current module folder
	 */
	public ModulePath(File rootFolder, File moduleFolder) {
		
		// make both file paths canonical, but only once for performance
		rootFolder = EcobuildFileUtil.getCanonicalFile(rootFolder);
		moduleFolder = EcobuildFileUtil.getCanonicalFile(moduleFolder);
		
		// extract some values
		String rootPath = rootFolder.getPath();
		
		// check for root
		if (moduleFolder.getPath().equals(rootPath)) {
			this.escapeLevel = -1;
			this.usedSegments = 0;
			this.segments = NO_SEGMENTS;
			return;
		}

		// move up until we hit the root folder
		LinkedList<String> segmentList = new LinkedList<String>();
		do {
			segmentList.addFirst(moduleFolder.getName());
			moduleFolder = moduleFolder.getParentFile();
		} while (!moduleFolder.getPath().equals(rootPath));
		
		// initialize fields
		this.escapeLevel = -1;
		this.usedSegments = segmentList.size();
		this.segments = segmentList.toArray(new String[usedSegments]);
		
	}
	
	/**
	 * Checks whether the specified string is a valid path segment, and throws a
	 * {@link UserMessageBuildException} if it isn't.
	 * @param s the string to check
	 * @param containingPath the path that contains the invalid segment (for error output)
	 */
	private static void ensureValidSegment(String s, String containingPath) {
		if (!isValidSegment(s)) {
			throw new UserMessageBuildException("Invalid path segment \"" + s + "\" in path: " + containingPath);
		}
	}
	
	/**
	 * Checks whether the specified string is a valid path segment.
	 * @param s the string to check
	 * @return true if s is a valid segment, false if not
	 */
	private static boolean isValidSegment(String s) {
		return (!s.equals("") && !s.equals(".") && !s.equals("..") && s.indexOf('/') == -1);
	}
	
	/**
	 * @return true if this path is absolute, false if relative
	 */
	public boolean isAbsolute() {
		return escapeLevel < 0;
	}
	
	/**
	 * Getter method for the escapeLevel. Returns -1 for absolute paths.
	 * @return the escapeLevel
	 */
	public int getEscapeLevel() {
		return escapeLevel;
	}

	/**
	 * @return the number of segments
	 */
	public int getSegmentCount() {
		return usedSegments;
	}
	
	/**
	 * Returns a single segment.
	 * @param index the index
	 * @return the segment at the specified index
	 */
	public String getSegment(int index) {
		if (index < 0 || index >= usedSegments) {
			throw new IndexOutOfBoundsException("invalid index " + index + " for " + usedSegments + " segments");
		}
		return segments[index];
	}
	
	/**
	 * Getter method for the segments.
	 * @return the segments
	 */
	private String[] getSegments() {
		return segments;
	}

	/**
	 * Returns a {@link ModulePath} instance for the parent path.
	 * 
	 * For an absolute path, this method removes the last segment. Returns
	 * null for the root path (no segments).
	 * 
	 * For a relative path, this method first tries to remove the last segment.
	 * If no segments exist, the escape level is increased. Note that there is
	 * no notion of the result being confined, since there is no current folder
	 * against which this relative path is being resolved.
	 * 
	 * @return the parent path, or null for the root path
	 */
	public ModulePath getParent() {
		
		// relative or absolute: if we have segments, remove the last one
		if (usedSegments > 0) {
			return new ModulePath(escapeLevel, segments, usedSegments - 1);
		}
		
		// absolute: no segments means root means no parent
		if (escapeLevel == -1) {
			return null;
		}
		
		// relative: no segments means purely escaping, so increase escape level
		return new ModulePath(escapeLevel + 1, NO_SEGMENTS, 0);
		
	}
	
	/**
	 * Returns a {@link ModulePath} instance for a child path.
	 * 
	 * This method returns a new instance with the specified name appended as a new segment.
	 * 
	 * @param name the name of the child segment
	 * @return the new module path
	 */
	public ModulePath getChild(String name) {
		if (!isValidSegment(name)) {
			throw new UserMessageBuildException("trying to get child path with invalid name \"" + name + "\" from path: " + this);
		}
		String[] childSegments = new String[usedSegments + 1];
		System.arraycopy(segments, 0, childSegments, 0, usedSegments);
		childSegments[usedSegments] = name;
		return new ModulePath(escapeLevel, childSegments, usedSegments + 1);
	}

	/**
	 * Returns the concatenation of this path and the other path.
	 * 
	 * If the other path is absolute, then it is returned, and this path
	 * does not matter. Otherwise, escape segments from the other path
	 * neutralize normal segments from the path until either is
	 * exhausted. The remaining segments are then concatenated.
	 * 
	 * Throws a {@link UserMessageBuildException} if this path is
	 * absolute and the other path has more escape segments than this
	 * path has normal segments, since the concatenation would
	 * escape the root folder.
	 * 
	 * @param other the other path
	 * @return the concatenation
	 */
	public ModulePath concat(ModulePath other) {
		
		// first case: the other path is absolute
		if (other.isAbsolute()) {
			return other;
		}
		
		// second case: the other path's escape segments get exhausted
		int remainingThisSegments = (usedSegments - other.getEscapeLevel());
		if (remainingThisSegments >= 0) {
			String[] resultSegments = new String[remainingThisSegments + other.getSegmentCount()];
			System.arraycopy(segments, 0, resultSegments, 0, remainingThisSegments);
			System.arraycopy(other.getSegments(), 0, resultSegments, remainingThisSegments, other.getSegmentCount());
			return new ModulePath(escapeLevel, resultSegments, resultSegments.length);
		}
		
		// third case: escaping an absolute path
		if (isAbsolute()) {
			throw new UserMessageBuildException("Concatenation of module path " + this + " and " + other + " escapes the root folder");
		}
		
		// last case: neutralizing the normal segments of a relative path and increasing its escape level
		return new ModulePath(escapeLevel - remainingThisSegments, other.getSegments(), other.getSegmentCount());
		
	}
	
	/**
	 * Resolves this module path against the specified root folder, assuming that
	 * this path is absolute. Throws an exception if this path is relative.
	 * @param rootFolder the root folder
	 * @return the resolved file path
	 */
	public File resolveAbsolute(File rootFolder) {
		if (!isAbsolute()) {
			throw new UserMessageBuildException("resolveAbsolute(): path is not absolute: " + this);
		}
		File result = rootFolder;
		for (int i=0; i<usedSegments; i++) {
			result = new File(result, segments[i]);
		}
		return result;
	}

	/**
	 * Resolves this module path against the specified root folder and current
	 * folder. Throws an exception if this path escapes the root folder -- to detect
	 * this, the currentFolder must be equal to or be a subfolder of the root folder. 
	 * @param rootFolder the root folder
	 * @param currentFolder the current folder
	 * @return the resolved file path
	 */
	public File resolve(File rootFolder, File currentFolder) {
		
		// make both file paths canonical so we can compare them
		rootFolder = EcobuildFileUtil.getCanonicalFile(rootFolder);
		currentFolder = EcobuildFileUtil.getCanonicalFile(currentFolder);

		// apply root prefix / escape segments
		File result;
		if (isAbsolute()) {
			result = rootFolder;
		} else {
			String rootPath = rootFolder.getPath();
			result = currentFolder;
			for (int i=0; i<escapeLevel; i++) {
				if (result.getPath().equals(rootPath)) {
					throw new UserMessageBuildException("resolve(): relative path " + this + " escapes the root folder " + rootFolder + ", current folder: " + currentFolder);
				}
				result = result.getParentFile();
			}
		}
		
		// apply normal segments
		for (int i=0; i<usedSegments; i++) {
			result = new File(result, segments[i]);
		}
		return result;
		
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		
		// special handling for the "here" path
		if (usedSegments == 0 && escapeLevel == 0) {
			return ".";
		}
		
		// root / escape prefix
		StringBuilder builder = new StringBuilder();
		if (isAbsolute()) {
			
			// root prefix
			builder.append('/');
			
		} else {
			
			// escape prefix
			for (int i=0; i<escapeLevel; i++) {
				builder.append("../");
			}
			
			// remove trailing slash if there are no normal segments (in that case, escapeLevel > 0 because of first check above)
			if (usedSegments == 0) {
				builder.setLength(builder.length() - 1);
			}
			
		}
		
		// append normal segments
		boolean first = true;
		for (int i=0; i<usedSegments; i++) {
			if (first) {
				first = false;
			} else {
				builder.append('/');
			}
			builder.append(segments[i]);
		}
		
		return builder.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int result = 2 + escapeLevel;
		for (int i=0; i<usedSegments; i++) {
			result = 31 * result + segments[i].hashCode();
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object otherObject) {
		
		// type check
		if (!(otherObject instanceof ModulePath)) {
			return false;
		}
		ModulePath other = (ModulePath)otherObject;
		
		// check escape levels and segment count
		if (this.escapeLevel != other.escapeLevel || this.usedSegments != other.usedSegments) {
			return false;
		}
		
		// compare segments
		for (int i=0; i<usedSegments; i++) {
			if (!segments[i].equals(other.segments[i])) {
				return false;
			}
		}
		
		return true;
	}
}
