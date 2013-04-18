package be.kuleuven.cs.distrinet.chameleon.workspace;

import java.io.File;

import be.kuleuven.cs.distrinet.chameleon.core.language.Language;
import be.kuleuven.cs.distrinet.chameleon.plugin.LanguagePlugin;
import be.kuleuven.cs.distrinet.chameleon.workspace.BootstrapProjectConfig.BaseLibraryConfiguration;
import be.kuleuven.cs.distrinet.rejuse.predicate.SafePredicate;

/**
 * A class of plugins to create language specific project configurations.
 * 
 * When a project configuration file is read, the initial parser determines
 * the language of the project, and looks up the corresponding {@link Language} object
 * in a {@link LanguageRepository}. Finally, the {@link ProjectConfigurator} plugin is requested
 * and the {@link #createConfigElement(String, File, ProjectInitialisationListener)} method is
 * used to create the language specific {@link ProjectConfiguration} object. 
 * 
 * @author Marko van Dooren
 */
public interface ProjectConfigurator extends LanguagePlugin {

	/**
	 * Create a {@link ProjectConfiguration} object for the language to which this project configurator
	 * is attached. The result already contains all required {@link DocumentLoader} objects to
	 * load the base library of the language.
	 *  
	 * @param projectName The name of the project being loaded.
	 * @param root The root directory of the project being loaded.
	 * @param workspace The workspace of which the new project will become part.
	 * @param listener A listener that reacts on changes in the ProjectConfig.
	 * @param baseLibraryConfiguration An object the determines which base libraries must be loaded. In case
	 *                                 of languages that build upon other languages and also define their own
	 *                                 base library, the individual base libraries may each be enabled or disabled.
	 * @return A ProjectConfig object 
	 * @throws ConfigException Thrown when the project cannot be configured, possibly because a
	 *                         base library cannot be found.
	 */
 /*@
   @ public behavior
   @
   @ post \result != null;
   @*/
	public ProjectConfiguration createConfigElement(String projectName, File root, Workspace workspace, ProjectInitialisationListener listener, BaseLibraryConfiguration baseLibraryConfiguration) throws ConfigException;

	/**
	 * Return a predicate to select source files of the language based on the relative 
	 * path with respect to its corresponding source root.
	 * @return
	 */
	public SafePredicate<? super String> sourceFileFilter();

	/**
	 * Return a predicate to select binary files of the language based on the relative 
	 * path with respect to its corresponding source root.
	 * @return
	 */
	public SafePredicate<? super String> binaryFileFilter();
	
}
