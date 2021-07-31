package co.mcsky.townyportal.gui.base;

import me.lucko.helper.menu.Gui;

/**
 * Represents a view of the GUI.
 */
public interface GuiView {
    /**
     * Draws the view for the GUI.
     * <p>
     * It should be called within {@link Gui#redraw()}.
     */
    void render();
}
