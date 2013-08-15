package be.kuleuven.cs.distrinet.chameleon.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import be.kuleuven.cs.distrinet.chameleon.core.element.Element;
import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.core.namespace.Namespace;
import be.kuleuven.cs.distrinet.chameleon.input.ParseException;
import be.kuleuven.cs.distrinet.chameleon.test.provider.ElementProvider;
import be.kuleuven.cs.distrinet.chameleon.workspace.InputException;
import be.kuleuven.cs.distrinet.chameleon.workspace.Project;
import be.kuleuven.cs.distrinet.chameleon.workspace.ProjectException;
import be.kuleuven.cs.distrinet.rejuse.action.Action;
import be.kuleuven.cs.distrinet.rejuse.action.Nothing;

/**
 * A test class for the clone and children methods of elements. It test all elements
 * in the namespaces of its namespace provider.
 * 
 * @author Marko van Dooren
 */
public class CloneAndChildTest extends ModelTest {



	/**
	 * Create a new clone a child tester with the given model provider and namespace provider.
	 * @throws IOException 
	 * @throws ParseException 
	 */
 /*@
   @ public behavior
   @
   @ pre provider != null;
   @ pre namespaceProvider != null;
   @
   @ post modelProvider() == provider;
   @ post namespaceProvider() == namespaceProvider;
   @*/
	public CloneAndChildTest(Project project, ElementProvider<Namespace> namespaceProvider) throws ProjectException {
		super(project);
		_namespaceProvider = namespaceProvider;
	}
	
	private ElementProvider<Namespace> _namespaceProvider;

	public ElementProvider<Namespace> namespaceProvider() {
		return _namespaceProvider;
	}
	
	@Test
	public void testClone() throws LookupException, Nothing, InputException {
		project().applyToSource(new Action<Element, Nothing>(Element.class) {
			@Override
			public void doPerform(Element object) throws Nothing {
				test(object);
			}
		});
	}

	/**
	 * Test the clone method of the given element.
	 * 
	 * The test fails if the clone method modifies the given element.
   * The test fails if the element has null as its one of its children.
   * The test fails if the clone does not have the same amount of children as
   * the given element.
   * The test fails if the element is derived. Derived element should never be reachable
   * from the model through the lexical navigation methods of Element.
   * The test fails if the clone is null.
   * The test fails if the clone has null as one of its children.  
	 */
	private void test(Element element) {
		String msg = "element type:"+element.getClass().getName();
		assertFalse(element.isDerived());
		List<Element> children = (List<Element>) element.children();
		assertNotNull(msg,children);
		// Testing for null in the children is already done by the children test.
		//assertFalse(msg,children.contains(null));
		Element clone = element.clone();
		clone.setUniParent(element);
		assertNotNull(msg,clone);
		List<Element> clonedChildren = (List<Element>) clone.children();
		List<Element> newChildren = (List<Element>) element.children();
		assertNotNull(msg,clonedChildren);
		assertFalse(msg,clonedChildren.contains(null));
		assertEquals(msg,children.size(), newChildren.size());
		try {
		  assertEquals(msg,children, newChildren);
		} catch(AssertionError err) {
			children.get(1).equals(newChildren.get(1));
			throw err;
		}
		assertEquals(msg,children.size(), clonedChildren.size());
	}
	
}
