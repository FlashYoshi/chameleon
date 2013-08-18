package be.kuleuven.cs.distrinet.chameleon.plugin;

import be.kuleuven.cs.distrinet.chameleon.workspace.View;

public interface ViewProcessor extends Processor<View, ViewProcessor> {

	/**
	 * Return the view of this view processor.
	 * @return
	 */
  public View view();

  /**
   * Set the view to which this processor is connected. The bidirectional
   * relation is kept in a consistent state.
   * 
   * T, which represents the processor interface, must be a super type of the type of this object.
   * 
   * @param view
   * @param connectorInterface
   */
  public <T extends ViewProcessor> void setView(View view, Class<T> keyInterface);


}
