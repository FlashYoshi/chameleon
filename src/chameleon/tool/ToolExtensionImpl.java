package chameleon.tool;

import chameleon.core.language.Language;

/**
 * @author Marko van Dooren
 * @author Koen Vanderkimpen
 */
public abstract class ToolExtensionImpl implements ToolExtension {

    private Language language;

    public final Language getLanguage() {
        return language;
    }

    public void setLanguage(Language lang, Class<? extends ToolExtension> toolExtensionClass) {
        if (lang!=language) {
            if ((language!=null) && (language.getToolExtension(toolExtensionClass)==this)) {
                language.removeToolExtension(toolExtensionClass);
            }
            language = lang;
            if ((language!=null) && (language.getToolExtension(toolExtensionClass)!=this)) {
                language.setToolExtension(toolExtensionClass, this);
            }
        }
    }

    public abstract ToolExtension clone();
}
