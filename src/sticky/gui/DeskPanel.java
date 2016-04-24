package sticky.gui;

import java.awt.BorderLayout;
import java.awt.LayoutManager;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JPanel;

public abstract class DeskPanel extends JPanel {
    private String viewName;
    private boolean initialized;
    protected Logger logger;

    public DeskPanel(String viewName) {
        super();
        this.viewName = viewName;
        setLayout(new BorderLayout());
        logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    }
    public DeskPanel(String viewName, boolean isDoubleBuffered) {
        super(isDoubleBuffered);
        this.viewName = viewName;
        setLayout(new BorderLayout());
    }
    public DeskPanel(String viewName, LayoutManager layoutManager) {
        super(layoutManager);
        this.viewName = viewName;
    }
    public DeskPanel(String viewName, LayoutManager layoutManager, boolean isDoubleBuffered) {
        super(layoutManager, isDoubleBuffered);
        this.viewName = viewName;
    }
    public abstract void init();
    
    public abstract void shutdown();

    public abstract void update();
    
    /**
     * Override to provide control panel functionality
     * 
     * @return
     */
    public ControlPanel getControlPanel() {
        return null;
    }

    public void finish() {
        if(initialized) {
            logger.log(Level.INFO, "DeskPanel.start: stopping " + viewName);
            shutdown();
        }
    }
    public void startUp() {
        logger.log(Level.INFO, "DeskPanel.start: initializing " + viewName);
        init();
        initialized = true;
        validateTree();
    }
    public boolean isInitialized() {
        return initialized;
    }
    public String getViewName() {
        return viewName;
    }
    protected File getStorageLocation() {
        File storageLocation = new File(System.getProperty("user.home") + "/sticky/" + viewName.replace(' ', '_'));
        if(!storageLocation.exists()) {
            storageLocation.mkdirs();
        }
        
        return storageLocation;
    }

}
