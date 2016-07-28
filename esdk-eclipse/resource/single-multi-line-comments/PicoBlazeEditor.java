package name.martingeisse.esdk.eclipse.picoblaze.editor;

import org.eclipse.ui.editors.text.TextEditor;

/**
 * Editor for PicoBlaze assembler source code (.psm) files.
 */
public class PicoBlazeEditor extends TextEditor {

	/**
	 * Constructor.
	 */
	public PicoBlazeEditor() {
		setDocumentProvider(new PicoBlazeDocumentProvider());
		setSourceViewerConfiguration(new PicoBlazeSourceViewerConfiguration());
	}

}
