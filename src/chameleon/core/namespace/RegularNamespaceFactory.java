package chameleon.core.namespace;


public class RegularNamespaceFactory implements NamespaceFactory {

	@Override
	public Namespace create(String name) {
		return new RegularNamespace(name);
	}

}
