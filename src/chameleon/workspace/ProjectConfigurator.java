package chameleon.workspace;

import java.io.File;

import chameleon.core.language.Language;
import chameleon.plugin.LanguagePlugin;
import chameleon.workspace.BootstrapProjectConfig.BaseLibraryConfiguration;

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
	public ProjectConfiguration createConfigElement(String projectName, File root, ProjectInitialisationListener listener, BaseLibraryConfiguration baseLibraryConfiguration) throws ConfigException;

//	 * @param loadBaseLibrary A boolean that indicates whether the base library of the language
//	 *                        must be loaded. This is typically only set to false when loading
//	 *                        the base library itself.
}
