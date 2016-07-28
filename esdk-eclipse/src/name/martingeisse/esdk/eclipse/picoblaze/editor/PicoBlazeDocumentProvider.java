package name.martingeisse.esdk.eclipse.picoblaze.editor;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.editors.text.FileDocumentProvider;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.ResourceMarkerAnnotationModel;

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

	/* (non-Javadoc)
	 * @see org.eclipse.ui.editors.text.FileDocumentProvider#createAnnotationModel(java.lang.Object)
	 */
	@Override
	protected IAnnotationModel createAnnotationModel(Object element) throws CoreException {
		if (element instanceof FileEditorInput) {
			FileEditorInput fileEditorInput = (FileEditorInput)element;
			return new ResourceMarkerAnnotationModel(fileEditorInput.getFile());
		} else {
			return super.createAnnotationModel(element);
		}
	}
	
}