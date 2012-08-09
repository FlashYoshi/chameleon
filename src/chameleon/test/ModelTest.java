package chameleon.test;

import java.io.IOException;

import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;

import chameleon.input.ParseException;
import chameleon.workspace.Project;
import chameleon.workspace.ProjectException;
import chameleon.workspace.ProjectBuilder;

/**
 * The top level test class for Chameleon tests. This class provides the infrastructure
 * that allows tests to be reused for different languages and different test configurations.
 * 
 * @author Marko van Dooren
 */
public abstract class ModelTest extends TestSuite {
	
	private static Logger _logger = Logger.getLogger("chameleon.test");
	static {
  	BasicConfigurator.configure();
	}
	
	public static Logger getLogger() {
		return _logger;
	}

	 /**
	  * Create a new test that uses the given provider to create models
	  * for testing.
	 * @throws IOException 
	 * @throws ParseException 
	  */
	/*@
	  @ public behavior
	  @
	  @ post modelProvider() == provider;
    @ post baseRecursive();
    @ post customRecursive();
	  @*/
	 public ModelTest(Project provider) throws ProjectException {
     _project = provider;
     setUp();
	 }
	 
//	 /**
//	  * Return the model provider for this test.
//	  */
//	/*@
//	  @ public behavior
//	  @
//	  @ post \result != null;
//	  @*/
//	 public Project projectBuilder() {
//		 return _provider;
//	 }
//	 
//	 private Project _provider;
	
   /**
    * This method is invoked during setup to set the levels of the loggers.
    * It allows subclasses to easily changes those levels if tests fail, without
    * having to change this class.
    * 
    * The default behavior is to leave log levels untouched. They are DEBUG by default.
    */
   public void setLogLevels() {
    	// do nothing by default
   }

   /**
    * Use the model provider to create a model, and store its language object
    * in this test.
    * 
    * This method also invokes setLogLevels() to set the log levels.
   * @throws IOException 
   * @throws ParseException 
    * @throws Exception
    */
  /*@
    @ public behavior
    @
    @ // not quite correct
    @ post language() == provider().model();
    @*/
   @Before
   public void setUp() throws ProjectException {
    	setLogLevels();
//    	long start = System.nanoTime();
//    	_project = project();
//    	long stop = System.nanoTime();
//    	System.out.println("Model input took "+(stop-start)/1000000+" milliseconds.");
    }
    
   @After
   public void tearDown() {
  	 _project = null;
   }
   
   /**
    * Return the language object of the model being tested.
    */
  /*@
    @ public behavior
    @
    @ post \result != null;
    @*/
   public Project project() {
   	 return _project;
   }
  
   private Project _project;
    
}
