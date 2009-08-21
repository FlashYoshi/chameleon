package chameleon.input;

import java.io.File;
import java.util.Set;

import chameleon.core.namespace.Namespace;

public interface MetaModelFactory {

//	public Namespace getMetaModel(ILinkageFactory fact, Set<File> files) throws Exception;
	
	public Set loadFiles(String path, String extension, boolean recursive);
}
