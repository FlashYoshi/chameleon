package be.kuleuven.cs.distrinet.chameleon.ui.widget.list;

import java.util.List;

public interface ListContentProvider<T> {

	public List<T> items(Object context);
}
