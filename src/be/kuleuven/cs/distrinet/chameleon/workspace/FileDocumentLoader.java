package be.kuleuven.cs.distrinet.chameleon.workspace;

import java.io.File;
import java.io.InputStream;

import be.kuleuven.cs.distrinet.chameleon.core.namespace.DocumentLoaderNamespace;

public class FileDocumentLoader extends StreamDocumentLoader implements IFileDocumentLoader {

	public FileDocumentLoader(File file, DocumentScanner scanner) throws InputException {
		_file = file;
		init(scanner);
	}
	
	@Override
   public File file() {
		return _file;
	}
	
	private File _file;
	
	public FileDocumentLoader(File file, DocumentLoaderNamespace ns, DocumentScanner scanner) throws InputException {
		this(file,scanner);
		setNamespace(ns);
	}	
	
  @Override
  public String toString() {
  	return "file: "+ _file.toString();
  }
  
  @Override
  public InputStream inputStream() throws InputException {
  	return convert(_file); 
  }
}
