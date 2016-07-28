/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.esdk.eclipse.editorhelpers;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;

/**
 * A scanner that returns the same token all the time.
 */
public class SingleTokenScanner extends RuleBasedScanner {

	/**
	 * Constructor.
	 * @param attribute the attribute to wrap in a token
	 */
	public SingleTokenScanner(final TextAttribute attribute) {
		this(new Token(attribute));
	}

	/**
	 * Constructor.
	 * @param token the token
	 */
	public SingleTokenScanner(final Token token) {
		setDefaultReturnToken(token);
	}

}
