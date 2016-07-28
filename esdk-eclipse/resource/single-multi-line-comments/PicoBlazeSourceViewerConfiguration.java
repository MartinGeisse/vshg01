/**
 * Copyright (c) 2011 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.esdk.eclipse.picoblaze.editor;

import name.martingeisse.esdk.eclipse.Colors;
import name.martingeisse.esdk.eclipse.editorhelpers.SingleTokenScanner;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;

/**
 *
 */
public class PicoBlazeSourceViewerConfiguration extends TextSourceViewerConfiguration {

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getPresentationReconciler(org.eclipse.jface.text.source.ISourceViewer)
	 */
	@Override
	public IPresentationReconciler getPresentationReconciler(final ISourceViewer sourceViewer) {
		Colors.initialize(sourceViewer.getTextWidget().getDisplay());

		final PresentationReconciler reconciler = new PresentationReconciler();
		DefaultDamagerRepairer dr;

		dr = new DefaultDamagerRepairer(new SingleTokenScanner(new TextAttribute(Colors.black)));
//		reconciler.setDamager(dr, MyPartitionScanner.TOKEN_TYPE_PROPERTIES);
//		reconciler.setRepairer(dr, MyPartitionScanner.TOKEN_TYPE_PROPERTIES);
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);
		
		dr = new DefaultDamagerRepairer(new SingleTokenScanner(new TextAttribute(Colors.red)));
		reconciler.setDamager(dr, PicoBlazeDocumentPartitioner.TOKEN_TYPE_SINGLE_LINE_COMMENT);
		reconciler.setRepairer(dr, PicoBlazeDocumentPartitioner.TOKEN_TYPE_SINGLE_LINE_COMMENT);

		dr = new DefaultDamagerRepairer(new SingleTokenScanner(new TextAttribute(Colors.green)));
		reconciler.setDamager(dr, PicoBlazeDocumentPartitioner.TOKEN_TYPE_MULTI_LINE_COMMENT);
		reconciler.setRepairer(dr, PicoBlazeDocumentPartitioner.TOKEN_TYPE_MULTI_LINE_COMMENT);

		return reconciler;

	}

}
