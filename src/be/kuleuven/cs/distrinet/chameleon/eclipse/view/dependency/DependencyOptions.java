package be.kuleuven.cs.distrinet.chameleon.eclipse.view.dependency;

import be.kuleuven.cs.distrinet.chameleon.plugin.LanguagePlugin;

public interface DependencyOptions extends LanguagePlugin {

	/**
	 * Create a default dependency analysis configuration for the language.
	 */
	public DependencyConfiguration createConfiguration();
}
