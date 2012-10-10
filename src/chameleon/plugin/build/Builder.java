package chameleon.plugin.build;

import java.io.File;
import java.io.IOException;
import java.util.List;

import chameleon.core.document.Document;
import chameleon.exception.ModelException;
import chameleon.plugin.ViewPlugin;

public interface Builder extends ViewPlugin {

	public void build(List<Document> compilationUnits, List<Document> allProjectCompilationUnits, File outputDir, BuildProgressHelper buildProgressHelper) throws ModelException, IOException;
	public int totalAmountOfWork(List<Document> compilationUnits, List<Document> allProjectCompilationUnits);
}
