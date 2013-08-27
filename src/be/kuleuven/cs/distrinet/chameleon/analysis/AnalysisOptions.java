package be.kuleuven.cs.distrinet.chameleon.analysis;

import java.util.List;

import be.kuleuven.cs.distrinet.chameleon.core.element.Element;

public interface AnalysisOptions<E extends Element, R extends Result<R>> {

	public List<? extends OptionGroup> optionGroups();

	public Analysis<Element, R> createAnalysis();
	
	public void setContext(Object context);
}
