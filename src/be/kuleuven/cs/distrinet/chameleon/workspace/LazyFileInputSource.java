package be.kuleuven.cs.distrinet.chameleon.workspace;

import java.io.File;

import be.kuleuven.cs.distrinet.chameleon.core.namespace.InputSourceNamespace;

public class LazyFileInputSource extends LazyStreamInputSource implements IFileInputSource {

	public LazyFileInputSource(File file, String declarationName, InputSourceNamespace ns,DocumentLoader loader) throws InputException {
		super(file, declarationName, ns, loader);
		_file = file;
	}
	
	public File file() {
		return _file;
	}
	
	private File _file;

}
