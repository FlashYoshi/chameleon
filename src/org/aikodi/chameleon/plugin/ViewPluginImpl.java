package org.aikodi.chameleon.plugin;

import org.aikodi.chameleon.workspace.View;

public abstract class ViewPluginImpl extends PluginImpl<View, ViewPlugin> implements ViewPlugin {

	@Override
	public View view() {
		return container();
	}

	@Override
	public <T extends ViewPlugin> void setView(View view, Class<T> keyInterface) {
		setContainer(view, keyInterface);
	}

	@Override
	public abstract ViewPluginImpl clone();

}
