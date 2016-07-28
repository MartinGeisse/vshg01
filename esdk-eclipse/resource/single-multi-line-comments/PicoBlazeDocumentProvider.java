package name.martingeisse.esdk.eclipse.picoblaze.editor;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.ui.editors.text.FileDocumentProvider;

/**
 * Eclipse editor document provider for .psm files.
 */
public class PicoBlazeDocumentProvider extends FileDocumentProvider {

	/* (non-Javadoc)
	 * @see org.eclipse.ui.editors.text.StorageDocumentProvider#createDocument(java.lang.Object)
	 */
	@Override
	protected IDocument createDocument(final Object element) throws CoreException {
		final IDocument document = super.createDocument(element);
		if (document != null) {
			final IDocumentPartitioner partitioner = new PicoBlazeDocumentPartitioner();
			partitioner.connect(document);
			document.setDocumentPartitioner(partitioner);
		}
		return document;
	}

}