package chameleon.core.validation;

/**
 * A class representing the status of a valid model.
 * 
 * @author Marko van Dooren
 */
public class Valid extends VerificationResult {
	
	private Valid() {
	}

	/**
	 * Create a new Valid object.
	 */
	public static Valid create() {
		return _instance;
	}
	
	private static Valid _instance = new Valid();
	

	/**
	 * The string representation of a valid verification is "valid".
	 */
 /*@
   @ public behavior
   @
   @ post \result.equals("valid");
   @*/
	public String toString() {
		return "valid";
	}

	/**
	 * The conjunction of a valid verification result with another verification result
	 * is the other verification result.
	 */
 /*@
   @ public behavior
   @
   @ pre other != null;
   @
   @ post \result == other;
   @*/
	@Override
	public VerificationResult and(VerificationResult other) {
		return other;
	}

	/**
	 * The conjunction of a valid verification result with an invalid verification result
	 * is the invalid verification result.
	 */
 /*@
   @ public behavior
   @
   @ pre problem != null;
   @
   @ post \result == problem;
   @*/
	@Override
	protected VerificationResult andInvalid(Invalid problem) {
		return problem;
	}

}
