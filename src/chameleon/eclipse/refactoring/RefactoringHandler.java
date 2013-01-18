package chameleon.eclipse.refactoring;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import chameleon.eclipse.editors.EclipseDocument;
import chameleon.eclipse.editors.ChameleonEditor;

public class RefactoringHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		return null;
	}

	public EclipseDocument document() {
		return editor().getDocument();
	}

	public ChameleonEditor editor() {
		return ChameleonEditor.getActiveEditor();
	}
	
}
