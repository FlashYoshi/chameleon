package be.kuleuven.cs.distrinet.chameleon.workspace;

import java.util.List;

import be.kuleuven.cs.distrinet.chameleon.core.document.Document;
import be.kuleuven.cs.distrinet.chameleon.core.element.Element;
import be.kuleuven.cs.distrinet.chameleon.core.namespace.Namespace;
import be.kuleuven.cs.distrinet.rejuse.action.Action;
import be.kuleuven.cs.distrinet.rejuse.association.OrderedMultiAssociation;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableSet;

public class CompositeDocumentLoader extends DocumentLoaderImpl implements DocumentLoaderContainer {

	public CompositeDocumentLoader(String label) {
		_label = label;
	}
	
	@Override
	public String label() {
		return _label;
	}
	
	private String _label;
	
	public void addLoader(DocumentLoader loader) {
		if(loader != null) {
			_loaders.add(loader.containerLink());
		}
	}
	
	public List<DocumentLoader> loaders() {
		return _loaders.getOtherEnds();
	}

	private OrderedMultiAssociation<CompositeDocumentLoader, DocumentLoader> _loaders = new OrderedMultiAssociation<>(this);
	{
		_loaders.enableCache();
	}
	
	@Override
	public void notifyContainerRemoved(DocumentLoaderContainer project) throws ProjectException {
		for(DocumentLoader loader: loaders()) {
			loader.notifyContainerRemoved(project);
		}
	}
	
	@Override
	public void notifyContainerConnected(DocumentLoaderContainer project) throws ProjectException {
		for(DocumentLoader loader: loaders()) {
			loader.notifyContainerConnected(project);
		}
	}
	
	@Override
	public void notifyProjectReplaced(DocumentLoaderContainer old,
			DocumentLoaderContainer newProject) throws ProjectException {
		for(DocumentLoader loader: loaders()) {
			loader.notifyProjectReplaced(old, newProject);
		}
	}
	
	
	@Override
	public <E extends Exception> void apply(Action<? extends Element, E> action)
			throws E, InputException {
		for(DocumentLoader loader: loaders()) {
			loader.apply(action);
		}
	}
	
	@Override
	public int nbInputSources() {
		int result = 0;
		for(DocumentLoader loader: loaders()) {
			result += loader.nbInputSources();
		}
		return result;
	}
	
	@Override
	public boolean isBaseLoader() {
		return false;
	}
	
	@Override
	public void addAndSynchronizeListener(InputSourceListener listener) {
		for(DocumentLoader loader: loaders()) {
			loader.addAndSynchronizeListener(listener);
		}
	}
	
	@Override
	public void addListener(InputSourceListener listener) {
		super.addListener(listener);
		for(DocumentLoader loader: loaders()) {
			loader.addListener(listener);
		}
	}
	
	@Override
	public void removeListener(InputSourceListener listener) {
		super.removeListener(listener);
		for(DocumentLoader loader: loaders()) {
			loader.removeListener(listener);
		}
	}
	
	@Override
	public List<Document> documents() throws InputException {
		Builder<Document> builder = ImmutableList.builder();
		for(DocumentLoader loader: loaders()) {
			builder.addAll(loader.documents());
		}
		return builder.build();
	}
	
	@Override
	public void flushCache() {
		for(DocumentLoader loader: loaders()) {
			loader.flushCache();
		}
	}
	
	@Override
	public List<InputSource> inputSources() {
		Builder<InputSource> builder = ImmutableList.builder();
		for(DocumentLoader loader: loaders()) {
			builder.addAll(loader.inputSources());
		}
		return builder.build();
	}
	
	@Override
	public List<Namespace> topLevelNamespaces() {
		ImmutableSet.Builder<Namespace> builder = ImmutableSet.builder();
		for(DocumentLoader loader: loaders()) {
			builder.addAll(loader.topLevelNamespaces());
		}
		return ImmutableList.copyOf(builder.build());
	}
	
//	public boolean canAddLoader(DocumentLoader loader) {
//		return ! _loaders.containsObject(loader);
//	}
	
	@Override
	public boolean loadsSameAs(DocumentLoader loader) {
		for(DocumentLoader l: loaders()) {
			if(l.loadsSameAs(loader)) {
				return true;
			}
		}
		return false;
	}
}