/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.esdk.eclipse.picoblaze.editor;

import name.martingeisse.esdk.eclipse.Colors;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;

/**
 * TODO: document me
 *
 */
public class PicoBlazeSyntaxHighlightingScanner extends RuleBasedScanner {

	/**
	 * Constructor.
	 */
	public PicoBlazeSyntaxHighlightingScanner() {
		IRule[] rules = new IRule[] {
//			new
//			new EndOfLineRule(";", new Token(TOKEN_TYPE_SINGLE_LINE_COMMENT)),
		};
		setRules(rules);
		setDefaultReturnToken(new Token(new TextAttribute(Colors.green)));
	}
	
}
