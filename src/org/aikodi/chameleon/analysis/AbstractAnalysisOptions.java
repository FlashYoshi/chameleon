package org.aikodi.chameleon.analysis;

import org.aikodi.chameleon.core.element.Element;

public abstract class AbstractAnalysisOptions<E extends Element, R extends Result<R>> implements AnalysisOptions<E, R>{

	@Override
	public void setContext(Object context) {
		for(OptionGroup group: optionGroups()) {
			group.setContext(context);
		}
	}

}
