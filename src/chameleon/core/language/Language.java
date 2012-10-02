package chameleon.core.language;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.rejuse.association.Association;
import org.rejuse.association.MultiAssociation;
import org.rejuse.property.PropertyMutex;
import org.rejuse.property.PropertySet;
import org.rejuse.property.PropertyUniverse;

import chameleon.core.element.Element;
import chameleon.core.lookup.LookupStrategyFactory;
import chameleon.core.namespace.RootNamespace;
import chameleon.core.property.ChameleonProperty;
import chameleon.core.property.PropertyRule;
import chameleon.core.validation.VerificationResult;
import chameleon.core.validation.VerificationRule;
import chameleon.exception.ChameleonProgrammerException;
import chameleon.plugin.Plugin;
import chameleon.plugin.Processor;
import chameleon.workspace.Project;
import chameleon.workspace.View;

public interface Language extends PropertyUniverse<ChameleonProperty> {
	
	/**
	 * Clone this language.
	 * @return
	 */
 /*@
   @ public behavior
   @
   @ post \result != null;
   @*/
	public Language clone();
	
	/**
	 * Return the name of this language.
	 */
 /*@
   @ public behavior
   @
   @ post \result != null;
   @*/
	public String name();
	
	/**
	 * Return the default properties of the given element.
	 * @return
	 */
 /*@
   @ public behavior
   @
   @ pre element != null;
   @
   @ (* The properties of all rules are added to the result.*)
   @ post (\forall PropertyRule rule; propertyRules().contains(rule);
   @         \result.containsAll(rule.properties(element)));
   @ (* Only the properties given by the property rules are in the result *);
   @ post (\forall Property<Element> p; \result.contains(p);
   @        \exists(PropertyRule rule; propertyRules().contains(rule);
   @           rule.properties(element).contains(p)));
   @*/
	public PropertySet<Element,ChameleonProperty> defaultProperties(Element element);
	
	/**
	 * Return the list of rule that determine the default properties of an element.
	 * @return
	 */
 /*@
   @ public behavior
   @
   @ post \result != null;
   @*/
	public List<PropertyRule> propertyRules();
	
	/**
	 * Add a property rule to this language object.
	 * @param rule
	 */
 /*@
   @ public behavior
   @
   @ pre rule != null;
   @
   @ post propertyRules().contains(rule);
   @*/
	public void addPropertyRule(PropertyRule rule);
	
	/**
	 * Remove a property rule from this language object.
	 * @param rule
	 */
 /*@
   @ public behavior
   @
   @ pre rule != null;
   @
   @ post ! propertyRules().contains(rule);
   @*/
	public void removePropertyRule(PropertyRule rule);
	
	/**
	 * Set the name of this language.
	 * @param name
	 *        The new name of this language
	 */
 /*@
   @ public behavior
   @
   @ pre name != null;
   @
   @ post getName() == name;
   @*/
	public void setName(String name);
	
	/**
	 * Return the default namespace attached to this language. A language is always attached to a default namespace because a language
	 * may need access to predefined elements, which are somewhere in the model.
	 * @return
	 */
	public RootNamespace defaultNamespace();

	/**
	 * A property mutex for the scope property.
	 */
	public PropertyMutex<ChameleonProperty> SCOPE_MUTEX();


  /**
   * Return the connector corresponding to the given connector interface.
   */
 /*@
   @ public behavior
   @
   @ pre connectorInterface != null;
   @*/
  public <T extends Plugin> T plugin(Class<T> pluginInterface);

  /**
   * Remove the plugin corresponding to the given plugin interface. The
   * bidirectional relation is kept in a consistent state.
   * 
   * @param <T>
   * @param pluginInterface
   */
 /*@
   @ public behavior
   @
   @ pre pluginInterface != null;
   @
   @ post plugin(pluginInterface) == null;
   @*/
  public <T extends Plugin> void removePlugin(Class<T> pluginInterface);

  /**
   * Set the plugin corresponding to the given plugin interface. The bidirectional relation is 
   * kept in a consistent state.
   * 
   * @param <T>
   * @param pluginInterface
   * @param plugin
   */
 /*@
   @ public behavior
   @
   @ pre pluginInterface != null;
   @ pre plugin != null;
   @
   @ post plugin(pluginInterface) == plugin; 
   @*/
  public <T extends Plugin> void setPlugin(Class<T> pluginInterface, T plugin);
  
  public Set<Entry<Class<? extends Plugin>,Plugin>> pluginEntrySet();
  
	public <S extends Plugin> void clonePluginsFrom(Language from);

  /**
   * Return all plugins attached to this language object.
   * @return
   */
 /*@
   @ public behavior
   @
   @ post \result != null;
   @ post (\forall Plugin c; ; \result.contains(c) == 
   @           (\exists Class<? extends Plugin> pluginInterface;; plugin(pluignInterface) == c)
   @      ); 
   @*/
  public Collection<Plugin> plugins();
  

  /**
   * Check if this language has a plugin for the given plugin type. Typically
   * the type is an interface or abstract class for a specific tool.
   */
 /*@
   @ public behavior
   @
   @ pre connectorInterface != null;
   @
   @ post \result == connector(connectorInterface) != null;
   @*/
  public <T extends Plugin> boolean hasPlugin(Class<T> pluginInterface);

  /**
   * Check if this language object has any plugins.
   */
 /*@
   @ public behavior
   @
   @ post \result ==  
   @*/
  public boolean hasPlugins();

  /**************
   * PROCESSORS *
   **************/
  
  /**
   * Return the processors corresponding to the given processor interface.
   */
 /*@
   @ public behavior
   @
   @ post \result.equals(processorMap().get(connectorInterface));
   @*/
  public <T extends Processor> List<T> processors(Class<T> connectorInterface);

  /**
   * Remove the given processor. The
   * bidirection relation is kept in a consistent state.
   * 
   * @param <T>
   * @param connectorInterface
   */
 /*@
   @ public behavior
   @
   @ pre connectorInterface != null;
   @ pre processor != null;
   @
   @ post !processor(connectorInterface).contains(processor); 
   @*/
  public <T extends Processor> void removeProcessor(Class<T> connectorInterface, T processor);


  /**
   * Add the given processor to the list of processors correponding to the given connector interface. 
   * The bidirectional relation is kept in a consistent state.
   * 
   * @param <T>
   * @param connectorInterface
   * @param connector
   */
 /*@
   @ public behavior
   @
   @ pre connectorInterface != null;
   @ pre processor != null;
   @
   @ post processor(connectorInterface).contains(processor); 
   @*/
  public <T extends Processor> void addProcessor(Class<T> connectorInterface, T processor);

  /**
   * Copy the processor mapping from the given language to this language.
   */
 /*@
   @ public behavior
   @
   @ post (\forall Class<? extends Processor> cls; from.processorMap().containsKey(cls);
   @         processors(cls).containsAll(from.processorMap().valueSet());
   @*/
	public <S extends Processor> void cloneProcessorsFrom(Language from);
	
	/**
	 * Return the mapping of classes/interfaces to the processors of that kind.
	 */
 /*@
   @ public behavior
   @
   @ post \result != null;
   @*/
	public Map<Class<? extends Processor>, List<? extends Processor>> processorMap();
	
  /**************************************************************************
   *                                 PROPERTIES                             *
   **************************************************************************/
  
  /**
   * Return the properties that can be used for elements in this model.
   * 
   * For every class of properties, one object is in the set.
   * @return
   */
  public Set<ChameleonProperty> properties();

  /**
   * Return the object representing the association between this language and the
   * properties to which it is attached.
   * 
   * DO NOT MODIFY THE RESULTING OBJECT. IT IS ACCESSIBLE ONLY BECAUSE OF THE 
   * VERY DUMB ACCESS CONTROL IN JAVA.
   */
  public MultiAssociation<Language,ChameleonProperty> propertyLink();
  
  /**
   * 
   * @param name
   * @return
   * @throws ChameleonProgrammerException
   *         There is no property with the given name.
   */
  public ChameleonProperty property(String name) throws ChameleonProgrammerException;
  
  
  /**************************************************************************
   *                           DEFAULT NAMESPACE                            *
   **************************************************************************/
  
  /**
   * Set the default namespace.
   */
//  public void setDefaultNamespace(RootNamespace defaultNamespace);

  /**
   * Return the association object that represents that association with the
   * default (root) namespace.
   */
  public Association<Language, View> viewLink();
  
  public View view();
  
  public Project project();
  /**
   * Return the factory for creating lookup strategies.
   */
  public LookupStrategyFactory lookupFactory();
  
  /**
	 * Returns true if the given character is a valid character
	 * for an identifier.
	 */
	public boolean isValidIdentifierCharacter(char character);
	
	/**
	 * Return the list of rule that determine the language specific validity conditions of an element.
	 * @return
	 */
 /*@
   @ public behavior
   @
   @ post \result != null;
   @*/
	public List<VerificationRule> validityRules();
	
	/**
	 * Add a property rule to this language object.
	 * @param rule
	 */
 /*@
   @ public behavior
   @
   @ pre rule != null;
   @
   @ post propertyRules().contains(rule);
   @*/
	public void addValidityRule(VerificationRule rule);
	
	/**
	 * Remove a property rule from this language object.
	 * @param rule
	 */
 /*@
   @ public behavior
   @
   @ pre rule != null;
   @
   @ post ! propertyRules().contains(rule);
   @*/
	public void removeValidityRule(VerificationRule rule);
	
	/**
	 * Verify the given element. 
	 * 
	 * This method verifies constraints on the element that are specific for the language.
	 * One example is the validity of the name of an element. Different languages may have different
	 * rules with respect to the validity of a name.
	 * 
	 * @param element
	 * @return
	 */
	public VerificationResult verify(Element element);
	
	/**
	 * Flush the caches kept by this language. Caches of model elements are flushed separately. 
	 * The default behavior is to do nothing.
	 */
	public void flushCache();

}
