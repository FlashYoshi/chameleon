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

public class CompositeDocumentScanner extends DocumentScannerImpl implements DocumentScannerContainer {

	public CompositeDocumentScanner(String label) {
		_label = label;
	}
	
	@Override
	public String label() {
		return _label;
	}
	
	private String _label;
	
	public void addScanner(DocumentScanner scanner) {
		if(scanner != null) {
			_scanners.add(scanner.containerLink());
		}
	}
	
	public List<DocumentScanner> scanners() {
		return _scanners.getOtherEnds();
	}

	private OrderedMultiAssociation<CompositeDocumentScanner, DocumentScanner> _scanners = new OrderedMultiAssociation<>(this);
	{
		_scanners.enableCache();
	}
	
	@Override
	public void notifyContainerRemoved(DocumentScannerContainer project) throws ProjectException {
		for(DocumentScanner scanner: scanners()) {
			scanner.notifyContainerRemoved(project);
		}
	}
	
	@Override
	public void notifyContainerConnected(DocumentScannerContainer project) throws ProjectException {
		for(DocumentScanner scanner: scanners()) {
			scanner.notifyContainerConnected(project);
		}
	}
	
	@Override
	public void notifyProjectReplaced(DocumentScannerContainer old,
			DocumentScannerContainer newProject) throws ProjectException {
		for(DocumentScanner scanner: scanners()) {
			scanner.notifyProjectReplaced(old, newProject);
		}
	}
	
	
	@Override
	public <E extends Exception> void apply(Action<? extends Element, E> action)
			throws E, InputException {
		for(DocumentScanner scanner: scanners()) {
			scanner.apply(action);
		}
	}
	
	@Override
	public int nbInputSources() {
		int result = 0;
		for(DocumentScanner scanner: scanners()) {
			result += scanner.nbInputSources();
		}
		return result;
	}
	
	@Override
	public boolean isBaseScanner() {
		return false;
	}
	
	@Override
	public void addAndSynchronizeListener(InputSourceListener listener) {
		for(DocumentScanner scanner: scanners()) {
			scanner.addAndSynchronizeListener(listener);
		}
	}
	
	@Override
	public void addListener(InputSourceListener listener) {
		super.addListener(listener);
		for(DocumentScanner scanner: scanners()) {
			scanner.addListener(listener);
		}
	}
	
	@Override
	public void removeListener(InputSourceListener listener) {
		super.removeListener(listener);
		for(DocumentScanner scanner: scanners()) {
			scanner.removeListener(listener);
		}
	}
	
	@Override
	public List<Document> documents() throws InputException {
		Builder<Document> builder = ImmutableList.builder();
		for(DocumentScanner scanner: scanners()) {
			builder.addAll(scanner.documents());
		}
		return builder.build();
	}
	
	@Override
	public void flushCache() {
		for(DocumentScanner scanner: scanners()) {
			scanner.flushCache();
		}
	}
	
	@Override
	public List<InputSource> inputSources() {
		Builder<InputSource> builder = ImmutableList.builder();
		for(DocumentScanner scanner: scanners()) {
			builder.addAll(scanner.inputSources());
		}
		return builder.build();
	}
	
	@Override
	public List<Namespace> topLevelNamespaces() {
		ImmutableSet.Builder<Namespace> builder = ImmutableSet.builder();
		for(DocumentScanner scanner: scanners()) {
			builder.addAll(scanner.topLevelNamespaces());
		}
		return ImmutableList.copyOf(builder.build());
	}
	
	@Override
	public boolean scansSameAs(DocumentScanner scanner) {
		for(DocumentScanner myScanner: scanners()) {
			if(myScanner.scansSameAs(scanner)) {
				return true;
			}
		}
		return false;
	}
}
