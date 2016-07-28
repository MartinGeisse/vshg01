/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.esdk.eclipse.picoblaze.editor;

import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.Token;

/**
 * Eclipse editor document partitioner for .psm files.
 */
public class PicoBlazeDocumentPartitioner extends FastPartitioner {

	/**
	 * the TOKEN_TYPE_SINGLE_LINE_COMMENT
	 */
	public static final String TOKEN_TYPE_SINGLE_LINE_COMMENT = "SINGLE_LINE_COMMENT";

	/**
	 * the LEGAL_CONTENT_TYPES
	 */
	private static final String[] LEGAL_CONTENT_TYPES = {
		TOKEN_TYPE_SINGLE_LINE_COMMENT,
	};
	
	/**
	 * Constructor.
	 */
	public PicoBlazeDocumentPartitioner() {
		super(new MyPartitionScanner(), LEGAL_CONTENT_TYPES);
	}
	
	/**
	 * Internally used partition scanner.
	 */
	private static class MyPartitionScanner extends RuleBasedPartitionScanner {

		/**
		 * Constructor.
		 */
		public MyPartitionScanner() {
			IPredicateRule[] rules = new IPredicateRule[] {
				new EndOfLineRule(";", new Token(TOKEN_TYPE_SINGLE_LINE_COMMENT)),
			};
			setPredicateRules(rules);
		}
		
	}
}
