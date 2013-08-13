package be.kuleuven.cs.distrinet.chameleon.workspace;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import be.kuleuven.cs.distrinet.chameleon.core.document.Document;
import be.kuleuven.cs.distrinet.chameleon.core.element.Element;
import be.kuleuven.cs.distrinet.chameleon.util.Handler;
import be.kuleuven.cs.distrinet.rejuse.action.Action;
import be.kuleuven.cs.distrinet.rejuse.association.AssociationListener;
import be.kuleuven.cs.distrinet.rejuse.association.OrderedMultiAssociation;
import be.kuleuven.cs.distrinet.rejuse.association.SingleAssociation;

/**
 * A class that represents the concept of a project. A project
 * keeps a collection of input sources and is an input source itself.
 * 
 * @author Marko van Dooren
 */
public class Project {
	
	/**
	 * Create a new Chameleon project with the given name, root directory, and view.
	 */
 /*@
   @ public behavior
   @
   @ pre name != null;
   @ pre root != null;
   @ pre view != null;
   @
   @ post name() == name;
   @ post root() == root;
   @ post views().contains(view);
   @*/
	public Project(String name, File root, View view) {
		this(name, root);
		addView(view);
	}
	
	/**
	 * Create a new Chameleon project with the given name, and root directory.
	 */
 /*@
   @ public behavior
   @
   @ pre name != null;
   @ pre root != null;
   @
   @ post name() == name;
   @ post root() == root;
   @*/
	public Project(String name, File root) {
		setName(name);
		setRoot(root);
		_views.addListener(new AssociationListener<View>() {

			@Override
			public void notifyElementAdded(View element) {
				notifyViewAdded(element);
			}

			@Override
			public void notifyElementRemoved(View element) {
				notifyViewRemoved(element);
			}
		});

	}
	
	/**
	 * Notify the project listeners that a view was added.
	 * 
	 * @param view The view that was added.
	 */
	protected void notifyViewAdded(View view) {
		for(ProjectListener listener: _listeners) {
			listener.viewAdded(view);
		}
	}
	
	/**
	 * Notify the project listeners that a view was removed.
	 * 
	 * @param view The view that was removed.
	 */
	protected void notifyViewRemoved(View view) {
		for(ProjectListener listener: _listeners) {
			listener.viewRemoved(view);
		}
	}
	
	/**
	 * Add the given project listener.
	 * 
	 * @param listener The listener to be added.
	 */
	public void addProjectListener(ProjectListener listener) {
		if(listener == null) {
			throw new IllegalArgumentException();
		}
		_listeners.add(listener);
	}
	
	/**
	 * Remove the given project listener.
	 * 
	 * @param listener The listener to be removed.
	 */
	public void removeProjectListener(ProjectListener listener) {
		_listeners.remove(listener);
	}
	
	
	private List<ProjectListener> _listeners = new ArrayList<ProjectListener>();
	
	/**
	 * Add the given view to the project. The project listeners
	 * will be notified afterwards.
	 * 
	 * @param view The view to be added.
	 */
 /*@
   @ public behavior
   @
   @ pre view != null;
   @ pre ! views().contains(view);
   @
   @ post views().contains(view);
   @ post views().size() == \old(views().size()) + 1;
   @*/
	public void addView(View view) {
		if(view != null) {
			_views.add(view.projectLink());
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	/**
	 * Return the views of this project.
	 */
 /*@
   @ public behavior
   @
   @ post \result != null;
   @*/
	public List<View> views() {
		return _views.getOtherEnds();
	}
	
	private OrderedMultiAssociation<Project, View> _views = new OrderedMultiAssociation<Project, View>(this);

	private SingleAssociation<Project, Workspace> _workspaceLink = new SingleAssociation<Project, Workspace>(this);
	
	/**
	 * Return the root directory of the project. The root directory is
	 * used to transform relative paths used in project files to
	 * absolute paths. 
	 */
	public File root() {
		return _root;
	}
	
	private File _root;
	
	/**
	 * Set the root directory of this project.
	 * 
	 * @param rootDirectory The new root directory of the project.
	 */
 /*@
   @ public behavior
   @
   @ pre rootDirectory != null;
   @
   @ post root() == rootDirectory;
   @*/
	protected void setRoot(File rootDirectory) {
		if(rootDirectory != null && rootDirectory.isFile()) {
			throw new IllegalArgumentException("The root directory of a project should not be a normal file");
		}
		_root = rootDirectory;
	}
	
	SingleAssociation<Project, Workspace> workspaceLink() {
		return _workspaceLink;
	}
	
	/**
	 * Return the workspace that contains this project.
	 */
	public Workspace workspace() {
		return _workspaceLink.getOtherEnd();
	}

	/**
	 * Set the workspace that contains this project.
	 * 
	 * @param workspace The workspace that will contain this project.
	 */
 /*@
   @ public behavior
   @
   @ post workspace() == workspace;
   @*/
	public void setWorkspace(Workspace workspace) {
		if(workspace != null) {
			workspace.add(this);
		} else {
			_workspaceLink.connectTo(null);
		}
	}

	/**
	 * Return the name of this project.
	 */
 /*@
   @ public behavior
   @
   @ 
   @*/
	public String getName() {
		return _name;
	}
	
	/**
	 * Set the name of this project. If null is provided,
	 * the name is set to "Unnamed Project".
	 * 
	 * @param name The new name of this project.
	 */
	public void setName(String name) {
		if(name == null) {
			_name="Unnamed Project";
		} else {
			_name = name;
		}
	}
	
	private String _name;

//	private MultiAssociation<Project, ProjectReference> _projectDependencies;
//	
//	public List<ProjectReference> dependencyReferences() {
//		return _projectDependencies.getOtherEnds();
//	}
//	
//	public void add(ProjectReference projectReference) {
//		_projectDependencies.add(projectReference.projectLink());
//	}
//	
//	public void remove(ProjectReference projectReference) {
//		_projectDependencies.remove(projectReference.projectLink());
//	}

//	public List<Project> dependencies() throws LookupException {
//		List<Project> result = new ArrayList<Project>();
//		for(ProjectReference ref: dependencyReferences()) {
//			result.add(ref.getElement());
//		}
//		return result;
//	}
	


//	/**
//	 * Refresh the project. This performs a refresh on all 
//	 * input sources.
//	 */
//	public void refresh() throws ParseException, IOException {
//		for(InputSource input: _inputSources) {
//			input.refresh();
//		}
//	}

	/**
	 * Try to add the given file to the project. Each FileLoader in each view
	 * will be given the opportunity to add the file. The source caches
	 * of the project are flushed afterwards.
	 * 
	 * FIXME: Can we avoid letting the Project know about files and FileLoader?
	 *        Yes, but ideally we want to apply a closure to the view which in
	 *        turn apply a closure to their loaders.
	 *        
	 * @param file The file to be added.
	 */
	public void tryToAdd(File file) {
		for(View view: views()) {
			for(FileLoader loader: view.sourceLoaders(FileLoader.class)) {
				try {
					loader.tryToAdd(file);
				} catch (InputException e) {
					throw new IllegalArgumentException(e);
				}
			}
		}
		flushSourceCache();
	}
	
	/**
	 * Try to remove the given file from the project.
	 * 
	 * @param file The file to be removed. Each FileLoader in each view
	 * will be given the opportunity to remove the file. The source caches
	 * of the project are flushed afterwards.
	 */
	public void tryToRemove(File file) {
		for(View view: views()) {
			for(FileLoader loader: view.sourceLoaders(FileLoader.class)) {
				try {
					loader.tryToRemove(file);
				} catch (InputException e) {
					throw new IllegalArgumentException(e);
				}
			}
		}
		flushSourceCache();
	}
	
	/**
	 * Flush all caches in the sources of this project. Each
	 * view is sent a message to flush its caches.
	 */
	public void flushSourceCache() {
		for(View view: views()) {
			view.flushSourceCache();
		}
	}
	
	/**
	 * Return the source elements of the given kind. The given {@link Handler} is used to
	 * deal with possible exceptions.
	 *  
	 * @param kind The type of source elements that is requested.
	 * @param handler A handler to deal with exceptions.
	 * @throws InputException
	 */
 /*@
   @ public behavior
   @
   @ pre kind != null;
   @ pre handler != null;
   @
   @ post \result != null;
   @ post (\forall T t;; \result.contains(t) <==> (\exists View v;; v.sourceElements(kind,handler).contains(t)));
   @*/
	public <T extends Element> List<T> sourceElements(Class<T> kind, Handler handler) throws InputException {
		List<T> result = new ArrayList<T>();
		for(View view: views()) {
			result.addAll(view.sourceElements(kind, handler));
		}
		return result;
	}
	
	/**
	 * Return the source documents of this project.
	 * @throws InputException
	 */
 /*@
   @ public behavior
   @
   @ post \result.equals(sourceElements(Document.class,Handler.PROPAGATE));
   @*/
	public List<Document> sourceDocuments() throws InputException {
		return sourceElements(Document.class, Handler.PROPAGATE);
	}
	
	public <E extends Exception> void applyToSource(Action<? extends Element, E> action) throws E, InputException {
		for(View view: views()) {
			view.applyToSource(action);
		}
	}
	
	/**
	 * Return a path that is an absolute for the given path.
	 * If the given path is already absolute, it is returned directly. 
	 * Otherwise, the project root path is prefixed, along with {@link File.separator}.
	 *  
	 * @param path The path of the file for which an absolute file must be returned.
	 */
 /*@
   @ public behavior
   @
   @ pre path != null;
   @
   @ post \result != null;
   @ post new File(path).isAbsolute() ==> \result == path;
   @ post ! new File(path).isAbsolute() ==> \result.equals(
   @ project().root().getAbsolutePath()+File.separator+path);
   @*/
	public String absolutePath(String path) {
		File root = new File(path);
		if(!root.isAbsolute()) {
			return root().getAbsolutePath()+File.separator+path;
		} else {
			return path;
		}
	}

	/**
	 * Return a file whose path is absolute for the given path.
	 * If the given path is already absolute, it is used directly
	 * to create the resulting file. Otherwise, the project root
	 * path is prefixed, along with {@link File.separator}.
	 *  
	 * @param path The path of the file for which an absolute file must be returned.
	 */
 /*@
   @ public behavior
   @
   @ pre path != null;
   @ post \result != null;
   @ post \result.getAbsolutePath().equals(absolutePath(path));
   @*/
	public File absoluteFile(String path) {
		return new File(absolutePath(path));
	}

	/**
	 * Return an absolute file for the given file.
	 * If the given file is already absolute, it is returned directly.
	 * Otherwise, the project root path is prefixed, along with {@link File.separator}.
	 *  
	 * @param file The file for which an absolute file must be returned.
	 */
 /*@
   @ public behavior
   @
   @ pre file != null;
   @ post \result != null;
   @ post \result.getAbsolutePath().equals(absolutePath(file.getPath()));
   @*/
	public File absoluteFile(File file) {
		if(! file.isAbsolute()) {
			return new File(root().getAbsoluteFile(),file.getPath());
		} else {
			return file;
		}
	}
}