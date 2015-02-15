package be.kuleuven.cs.distrinet.chameleon.support.type;

import java.util.List;

import be.kuleuven.cs.distrinet.chameleon.core.lookup.LookupException;
import be.kuleuven.cs.distrinet.chameleon.core.validation.Valid;
import be.kuleuven.cs.distrinet.chameleon.core.validation.Verification;
import be.kuleuven.cs.distrinet.chameleon.oo.member.Member;
import be.kuleuven.cs.distrinet.chameleon.oo.statement.Block;
import be.kuleuven.cs.distrinet.chameleon.oo.statement.CheckedExceptionList;
import be.kuleuven.cs.distrinet.chameleon.oo.statement.ExceptionSource;
import be.kuleuven.cs.distrinet.chameleon.oo.type.Type;
import be.kuleuven.cs.distrinet.chameleon.oo.type.TypeElementImpl;
import be.kuleuven.cs.distrinet.chameleon.util.association.Single;
import be.kuleuven.cs.distrinet.rejuse.association.SingleAssociation;

import com.google.common.collect.ImmutableList;

/**
 * @author Marko van Dooren
 */
public class StaticInitializer extends TypeElementImpl implements ExceptionSource {

  public StaticInitializer(Block block) {
      setBlock(block);
  }

  public Type getNearestType() {
    return getType();
  }

  public Type getType() {
  	return nearestAncestor(Type.class);
  }

  /*********
   * BLOCK *
   *********/

  public SingleAssociation getBlockLink() {
    return _blockLink;
  }

  public Block getBlock() {
    return _blockLink.getOtherEnd();
  }


  private Single<Block> _blockLink = new Single<Block>(this);

  public void setBlock(Block block) {
    set(_blockLink,block);
  }

  /**
   * @return
   */
  @Override
protected StaticInitializer cloneSelf() {
    return new StaticInitializer(null);
  }

 /*@
   @ also public behavior
   @
   @ post \result == getBlock().getCEL();
   @*/
  @Override
public CheckedExceptionList getCEL() throws LookupException {
    return getBlock().getCEL();
  }

 /*@
   @ also public behavior
   @
   @ post \result == getBlock().getAbsCEL();
   @*/
  @Override
public CheckedExceptionList getAbsCEL() throws LookupException {
    return getBlock().getAbsCEL();
  }

  /**
   * A static initializer does not add members to a type.
   */
  @Override
public List<Member> getIntroducedMembers() {
    return ImmutableList.of();
  }

	@Override
	public Verification verifySelf() {
		return Valid.create();
	}

}
