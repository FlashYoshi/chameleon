package be.kuleuven.cs.distrinet.chameleon.support.statement;

import be.kuleuven.cs.distrinet.chameleon.core.validation.Valid;
import be.kuleuven.cs.distrinet.chameleon.core.validation.Verification;
import be.kuleuven.cs.distrinet.chameleon.oo.statement.Statement;

/**
 * A class of finally clauses for a try statement.
 * 
 * @author Marko van Dooren
 */
public class FinallyClause extends Clause {

  public FinallyClause(Statement statement) {
    super(statement);
  }


  /**
   * @return
   */
  @Override
protected FinallyClause cloneSelf() {
    return new FinallyClause(null);
  }

	@Override
	public Verification verifySelf() {
		return Valid.create();
	}
}
