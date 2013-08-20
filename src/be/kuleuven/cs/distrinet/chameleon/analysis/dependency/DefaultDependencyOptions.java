package be.kuleuven.cs.distrinet.chameleon.analysis.dependency;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import be.kuleuven.cs.distrinet.chameleon.analysis.predicate.IsSource;
import be.kuleuven.cs.distrinet.chameleon.core.element.Element;
import be.kuleuven.cs.distrinet.chameleon.eclipse.view.dependency.DependencyConfiguration;
import be.kuleuven.cs.distrinet.chameleon.plugin.LanguagePluginImpl;
import be.kuleuven.cs.distrinet.chameleon.ui.widget.CheckboxSelector;
import be.kuleuven.cs.distrinet.chameleon.ui.widget.PredicateSelector;

public class DefaultDependencyOptions extends LanguagePluginImpl implements DependencyOptions {

	@Override
	public DependencyConfiguration createConfiguration() {
		List<PredicateSelector<? super Element>> source = new ArrayList<>();
		List<PredicateSelector<? super Element>> target = new ArrayList<>();
		target.add(onlySource());
		return new DependencyConfiguration(source, Collections.EMPTY_LIST,target,Collections.EMPTY_LIST);
	}

	public CheckboxSelector<Element> onlySource() {
		return new CheckboxSelector<>(new IsSource(), "Only source declarations");
	}

	@Override
	public LanguagePluginImpl clone() {
		return new DefaultDependencyOptions();
	}

}
