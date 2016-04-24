package sticky.gui;

import java.awt.LayoutManager;

import javax.swing.JPanel;

public abstract class ControlPanel extends JPanel {

    /**
     * 
     */
    public ControlPanel() {
        super();
    }

    /**
     * @param isDoubleBuffered
     */
    public ControlPanel(boolean isDoubleBuffered) {
        super(isDoubleBuffered);
    }

    /**
     * @param layout
     * @param isDoubleBuffered
     */
    public ControlPanel(LayoutManager layout, boolean isDoubleBuffered) {
        super(layout, isDoubleBuffered);
    }

    /**
     * @param layout
     */
    public ControlPanel(LayoutManager layout) {
        super(layout);
    }

    public abstract void init();
    public abstract void shutdown();
    
    public void startUp() {
        init();
    }
    public void finish() {
        shutdown();
    }
}
