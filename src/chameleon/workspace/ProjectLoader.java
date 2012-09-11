package chameleon.workspace;

import java.io.IOException;

import chameleon.input.ParseException;



/**
 * An abstract super class for creating projects.
 * 
 * @author Marko van Dooren
 */
public interface ProjectLoader {

	/**
	 * Return the project built by this builder.
	 * 
	 * @throws ProjectException 
	 * @throws ParseException 
	 * @throws IOException 
	 */
 /*@
   @ public behavior
   @
   @ post \result != null;
   @*/
	public Project project() throws ProjectException;
	
}
