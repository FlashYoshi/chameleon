/*
 * Created on 17-okt-06
 *
 */
package chameleon.eclipse.presentation.hyperlink;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;

import chameleon.core.declaration.Declaration;
import chameleon.core.document.Document;
import chameleon.core.reference.CrossReference;
import chameleon.eclipse.editors.ChameleonDocument;
import chameleon.eclipse.editors.ChameleonEditor;
import chameleon.exception.ModelException;

/**
 * The Hyperlink of a cross reference element in the editor. When clicked, it will open
 * the document of the element referenced by the cross reference.
 * 
 * @author Tim Vermeiren
 * @author Marko van Dooren
 */
public class ChameleonHyperlink implements IHyperlink {
	
	/**
	 * The cross reference in the model.
	 */
	CrossReference _element;
	
	IRegion _region;
	
	ChameleonDocument _document;
	
	public ChameleonHyperlink(CrossReference element, IRegion region,ChameleonDocument document){
		_element = element;
		_region = region;
		_document = document;
	}
	
	public String getHyperlinkText() {
		return getReference().toString();
	}
	
	public ChameleonDocument document() {
		return _document;
	}
	
	public CrossReference getReference(){
		return this._element;
	}
	
	public Declaration getDeclarator() throws ModelException {
		return getReference().getDeclarator();
	}
	
	public String getTypeLabel() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	/**
	 * Status of a hyperlink that refers to a valid element with a valid compilationunit
	 */
	public static final String VALID_STATUS = "valid";
	/**
	 * Status of a hyperlink refering to a valid element in the metamodel
	 * whitout a valid document or compilationunit
	 * eg.: double in java is valid but had no CU assosiated with it
	 */
	public static final String SEMIVALID_STATUS = "semivalid";
	
	/**
	 * Status of a hyperlink that doesn't refer to a valid element 
	 */
	public static final String INVALID_STATUS = "invalid";
	
	/**
	 * Returns the status of this hyperlink
	 * 
	 * @return See the static status constants of this class
	 */
	public String getStatus(){
		try {
			// perform lookup:
			Declaration refElement = getDeclarator();
			if(refElement==null)
				return INVALID_STATUS;
			// check whether the compilationUnit and the document are found:
			Document refCU = refElement.nearestAncestor(Document.class);
			ChameleonDocument refDoc = _document.getProjectNature().document(refCU);
			if(refDoc != null){
				return VALID_STATUS;
			} else {
				return SEMIVALID_STATUS;
			}
		} catch (Exception e) {
			// System.err.println(e.getMessage());
			// e.printStackTrace();
			return INVALID_STATUS;
		}
	}

	
	public void open() {
		try {
			Declaration referencedElement = getDeclarator();
			if (referencedElement != null) {
				ChameleonEditor.showInEditor(referencedElement, true, true, null);
			}
		} catch (ModelException exc) {
			// We don't do anything if the user decides to click on an unresolvable cross-reference.
		}
	}

	public IRegion getHyperlinkRegion() {
		return _region;
	}

}
