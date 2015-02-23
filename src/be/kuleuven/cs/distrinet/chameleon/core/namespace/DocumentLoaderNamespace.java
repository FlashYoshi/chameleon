package be.kuleuven.cs.distrinet.chameleon.core.namespace;

import java.util.List;

import be.kuleuven.cs.distrinet.chameleon.workspace.InputException;
import be.kuleuven.cs.distrinet.chameleon.workspace.DocumentLoader;

/**
 * An interface for namespaces that cooperate with document loaders
 * to enable lazy loading of documents.
 * 
 * @author Marko van Dooren
 */
public interface DocumentLoaderNamespace extends Namespace {

   /**
    * Add the given document loader.
    * 
    * @param loader The document loader to be added.
    * @throws InputException The document loader could not be added because
    *                        an exception was thrown while asking the loader
    *                        for the names of the top-level declarations.
    */
	public void addDocumentLoader(DocumentLoader loader) throws InputException;
	
	/**
	 * @return the document loaders that load documents into this namespace.
	 */
	public List<DocumentLoader> documentLoaders();
}
