package chameleon.plugin.build;

import java.io.File;
import java.io.IOException;
import java.util.List;

import chameleon.core.document.Document;
import chameleon.exception.ModelException;
import chameleon.plugin.Plugin;
import chameleon.plugin.PluginImpl;

public class NullBuilder extends PluginImpl implements Builder {
	@Override
	public void build(List<Document> compilationUnits, List<Document> allProjectCompilationUnits, File outputDir, BuildProgressHelper buildProgressHelper)
			throws ModelException, IOException {
	}

	@Override
	public int totalAmountOfWork(List<Document> compilationUnits, List<Document> allProjectCompilationUnits) {
		return 0;
	}

	@Override
	public Plugin clone() {
		return new NullBuilder();
	}
	
}

