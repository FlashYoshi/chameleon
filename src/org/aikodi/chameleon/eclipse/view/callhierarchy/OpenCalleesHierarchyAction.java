/**
 * Created on 22-jun-07
 * @author Tim Vermeiren
 */
package org.aikodi.chameleon.eclipse.view.callhierarchy;

import org.aikodi.chameleon.eclipse.ChameleonEditorPlugin;
import org.aikodi.chameleon.eclipse.project.ChameleonProjectNature;
import org.eclipse.jface.viewers.IContentProvider;

/**
 * This action will open the hierarchy of the callers (the methods that call this method)
 * 
 * @author Tim Vermeiren
 */
public class OpenCalleesHierarchyAction extends OpenCallHierarchyAction {

	public OpenCalleesHierarchyAction(CallHierarchyView view) {
		super(view);
		setText("Open Callees Hierarchy");
		setImageDescriptor(ChameleonEditorPlugin.getImageDescriptor("call_hierarchy_callees.gif"));
	}

	@Override
	protected IContentProvider getContentProvider(ChameleonProjectNature projectNature) {
		return new CalleesContentProvider();
	}
	
	
	
}
