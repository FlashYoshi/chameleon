/**
 * 
 */
package chameleon.core.lookup;

import java.util.ArrayList;
import java.util.List;

import chameleon.core.declaration.Declaration;
import chameleon.core.declaration.DeclarationContainer;
import chameleon.core.declaration.Signature;
import chameleon.core.relation.WeakPartialOrder;

public class DeclarationContainerSkipper<D extends Declaration> extends DeclarationSelector<D> {

	private DeclarationSelector<D> _original;
	
	private DeclarationContainer _skipped;
	
	public DeclarationContainerSkipper(DeclarationSelector<D> original, DeclarationContainer skipped) {
		_original = original;
		_skipped = skipped;
	}

	@Override
	public List<D> declarations(DeclarationContainer container) throws LookupException {
		if(container.equals(_skipped)) {
			return new ArrayList<D>();
		} else {
			return super.declarations(container);
		}
	}

	@Override
	public WeakPartialOrder order() {
		return _original.order();
	}

	@Override
	public boolean selectedBasedOnName(Signature signature) throws LookupException {
		return _original.selectedBasedOnName(signature);
	}

	@Override
	public Class selectedClass() {
		return _original.selectedClass();
	}

	@Override
	public boolean selectedRegardlessOfName(D declaration) throws LookupException {
		return _original.selectedRegardlessOfName(declaration);
	}

	@Override
	public String selectionName() throws LookupException {
		return _original.selectionName();
	}
	
}