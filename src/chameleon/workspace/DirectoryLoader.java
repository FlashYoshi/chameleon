package chameleon.workspace;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;

import org.antlr.runtime.RecognitionException;
import org.rejuse.predicate.SafePredicate;

import chameleon.input.ParseException;
import chameleon.util.concurrent.CallableFactory;
import chameleon.util.concurrent.FixedThreadCallableExecutor;
import chameleon.util.concurrent.QueuePollingCallableFactory;
import chameleon.util.concurrent.UnsafeAction;

/**
 * A class for building projects that reside in a directory.
 * 
 * @author Marko van Dooren
 */
public class DirectoryLoader extends DocumentLoaderImpl implements FileLoader {
	
//	/**
//	 * Create a new model provider with the given factory.
//	 * @throws ProjectException 
//	 */
// /*@
//   @ public behavior
//   @
//   @ pre factory != null;
//   @
//   @ post factory() == factory;
//   @*/
//	public DirectoryLoader(String fileExtension, String root, FileInputSourceFactory factory) {
//		this(root, factory);
//		addFileExtension(fileExtension);
//	}
	
	public DirectoryLoader(String root, FileInputSourceFactory factory,SafePredicate<? super String> filter) {
		setPath(root);
		setInputSourceFactory(factory);
		setFilter(filter);
	}
	
	protected void setFilter(SafePredicate<? super String> filter) {
		if(filter == null) {
			throw new IllegalArgumentException("The file name filter of a directory loader cannot be null");
		}
		_filter = filter;
	}
	
	public SafePredicate<? super String> filter() {
		return _filter;
	}
	
	private SafePredicate<? super String> _filter;
	
	protected void notifyViewAdded(View view) throws ProjectException {
		setRoot(view.project().absoluteFile(path()));
		includeCustom(root());
	}

	private void setInputSourceFactory(FileInputSourceFactory factory) {
		if(factory == null) {
			throw new IllegalArgumentException("The given file input source factory is null.");
		}
		_inputSourceFactory = factory;
	}
	
	private File _root;
	
	private String _path;
	
	public String path() {
		return _path;
	}
	
	private void setPath(String path) {
		if(path == null) {
			throw new IllegalArgumentException();
		}
		_path = path;
	}
	
	public File root() {
		return _root;
	}
	
	private void setRoot(File root) {
		if(root == null) {
			throw new IllegalArgumentException();
		}
		_root = root;
	}
	
	
	public FileInputSourceFactory inputSourceFactory() {
		return _inputSourceFactory;
	}

	private FileInputSourceFactory _inputSourceFactory;
	
	
	private void includeCustom(File root) throws ProjectException {
		inputSourceFactory().initialize(view().namespace());
		doIncludeCustom(root);
	}

	private boolean responsibleFor(File file) {
		boolean result = false;
		if(file != null) {
			result = filter().eval(file.getName());
		}
		return result;
	}

	/**
	 * Add the given directory to the list of directories that contain the custom model.
	 * @throws ParseException 
	 * @throws IOException 
	 */
  private void doIncludeCustom(File root) throws ProjectException {
  	File[] files = root.listFiles(new FilenameFilter(){
			@Override
			public boolean accept(File file, String extension) {
				return filter().eval(extension);
			}
		});
  	File[] subdirs = root.listFiles(new FileFilter(){
			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		});
  	if(files != null) {
  		for(File file: files) {
  			try {
  				addToModel(file);
  			} catch (InputException e) {
  				throw new ProjectException(e);
  			}
  		}
  	}
  	if(subdirs != null) {
  		for(File subDir: subdirs) {
  			// push dir
  			inputSourceFactory().pushDirectory(subDir.getName());
  			// recurse
  			doIncludeCustom(subDir);
  			// pop dir
  			inputSourceFactory().popDirectory();
  		}
  	}
//  	try {
//  		addToModel(new DirectoryScanner().scan(dirName, fileExtension(), customRecursive()));
//		} catch (IOException e) {
//			throw new ProjectException(e);
//		} catch (ParseException e) {
//			throw new ProjectException(e);
//		}
  }
  
  /**
   * Return whether the custom files will be scanned recursively.
   */
  public boolean customRecursive() {
  	return _recursive;
  }
  
  private boolean _recursive = true;

	private void addToModel(Collection<File> files) throws IOException, ParseException {
		final int size = files.size();
		class Counter {
			private int count;
			
			synchronized void increase() {
				this.count++;
			}
			synchronized int get() {
				return count;
			}
		}
		final Counter counter = new Counter();
		final BlockingQueue<File> fileQueue = new ArrayBlockingQueue<File>(files.size(), true, files);

		UnsafeAction<File,Exception> unsafeAction = new UnsafeAction<File,Exception>() {
			private boolean _debug = false;
			public void actuallyPerform(File file) throws InputException {
				counter.increase();
				if(_debug) {System.out.println(counter.get()+" of "+size+" :"+file.getAbsolutePath());};
				addToModel(file);
			} 
		};
	  CallableFactory factory = new QueuePollingCallableFactory<File,Exception>(unsafeAction,fileQueue);
	  try {
	  	new FixedThreadCallableExecutor<Exception>(factory).run();
	  } catch (IOException e) {
	  	throw e;
	  } catch (ParseException e) {
	  	throw e;
	  } catch (ExecutionException e) {
	  	Throwable cause = e.getCause();
			cause.printStackTrace();
	  	if(cause instanceof IOException) {
	  		throw (IOException)cause;
	  	} else if(cause instanceof ParseException) {
	  		throw (ParseException)cause;
	  	} 
	  } catch (Exception e) {
	  	e.printStackTrace();
	  }
	}
	
	/**
	 * Return the top of the metamodel when parsing the given file.
	 *
	 * @param files
	 *            A set containing all .java files that should be parsed.
	 * @pre The given set may not be null | files != null
	 * @pre The given set may only contain effective files | for all o in files: |
	 *      o instanceof File && | ! ((File)o).isDirectory()
	 * @return The result will not be null | result != null
	 * @return All given files will be parsed and inserted into the metamodel
	 *
	 * @throws TokenStreamException
	 *             Something went wrong
	 * @throws RecognitionException
	 *             Something went wrong
	 * @throws MalformedURLException
	 *             Something went wrong
	 * @throws FileNotFoundException
	 *             Something went wrong
	 * @throws RecognitionException 
	 */
	private void addToModel(File file) throws InputException {
    addInputSource(inputSourceFactory().create(file,this));
	}

	@Override
	public synchronized void tryToAdd(File file) throws InputException {
		try {
			if(responsibleFor(file) && ! file.isHidden()) {
				File relative = file.getParentFile();
				List<String> names = new ArrayList<String>();
				while(relative != null && (! relative.equals(_root))) {
					String relativeName = relative.getName();
					if(new File(relativeName).isHidden()) {
						return;
					}
					names.add(relativeName);
					relative = relative.getParentFile();
				}
				if(relative != null) {
					int size = names.size();
					for(int i = size - 1; i >= 0; i--) {
						_inputSourceFactory.pushDirectory(names.get(i));
					}
					_inputSourceFactory.create(file,this);		
				}
			}
		}
		finally {
			_inputSourceFactory.initialize(view().namespace());
		}
	}
	
	@Override
	public synchronized void tryToRemove(File file) throws InputException {
		InputSource toRemove = null;
		for(InputSource source: inputSources()) {
			IFileInputSource fsource = ((IFileInputSource)source);
			if(fsource.file().equals(file)) {
				toRemove = source; 
				break;
			}
		}
		if(toRemove != null) {
			removeInputSource(toRemove);
		}
	}
	
	
//	public static void main(String[] args) throws URISyntaxException {
//		URL objectLocation = Object.class.getResource("/java/lang/Object.class");
//		String fileName = objectLocation.getFile();
//		File file = new File(fileName.substring(5,fileName.indexOf('!')));
////		file = new File("/Users/marko");
//		System.out.println(file);
//	}

}
