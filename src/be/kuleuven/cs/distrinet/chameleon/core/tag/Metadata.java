package be.kuleuven.cs.distrinet.chameleon.core.tag;

import be.kuleuven.cs.distrinet.chameleon.core.element.Element;

/**
 * An interface for metadata tags that can be attached to elements.
 * 
 * @author Marko van Dooren
 */
public interface Metadata {

	/**
	 * Return the element to which this tag is attached.
	 * @return
	 */
	public Element getElement();
	
	public void setElement(Element element, String name);
	
	/**
	 * Disconnect this metadata from its element.
	 */
	public void disconnect();

}