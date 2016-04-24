package sticky.gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jdesktop.jdic.tray.SystemTray;
import org.jdesktop.jdic.tray.TrayIcon;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Main extends JFrame implements WindowListener, ActionListener, Runnable {
    private static final long serialVersionUID = 1L;
    
    private static final String FILE_SEPARATOR = System.getProperty("file.separator");
    private static final String X_POS = "x";
    private static final String Y_POS = "y";
    private static final String WIDTH = "width";
    private static final String HEIGHT = "height";
    private static final String MAXIMIZED = "maximized";
    private static final String PLUGIN_COUNT = "pluginCount";
    private static final String PLUGIN_PREFIX = "plugin.";
    private static final String STICKY_INSTALL_PATH = System.getProperty("user.dir");
    private static final long FIVE_MINUTES = 300000;

    private Logger logger;
    
    private JTabbedPane tabPanel;
    private JPanel mainPanel;
    private List<DeskPanel> plugins;
    private Map<String, Boolean> enablePlugin;
    private Map<String, Boolean> readEnablePlugin;
    
    //task bar tray icon
    private SystemTray tray;
    private TrayIcon trayIcon;
    private JPopupMenu trayMenu;
    private JMenuItem trayMenuOpen;
    
    private JButton controlPanelButton;
    private JButton exitButton;
    private ControlPanelDialog controlPanelDialog;
    
    public Main() {
        super("Sticky");
        try {
            BufferedImage image = ImageIO.read(getClass().getResourceAsStream("/images/stickylogo.png"));
            setIconImage(image);
            if( Integer.parseInt(System.getProperty("java.version").substring(2,3)) >=5 ) {
                System.setProperty("javax.swing.adjustPopupLocationToFit", "false");
            }
            tray = SystemTray.getDefaultSystemTray();
            trayMenu = new JPopupMenu("Sticky tray menu");
            trayMenuOpen = new JMenuItem("Open Sticky window");
            trayMenu.add(trayMenuOpen);
            trayMenuOpen.addActionListener(this);
            trayIcon = new TrayIcon(new ImageIcon(image), "Sticky", trayMenu);
            trayIcon.setIconAutoSize(true);
            tray.addTrayIcon(trayIcon);
        }
        catch(IOException e) {
            logger.logp(Level.SEVERE, "sticky.gui.Main", "Main", "Unable to load logo", e);
        }
        //prefs
        Preferences prefs = Preferences.systemNodeForPackage(this.getClass());
        int x = prefs.getInt(X_POS, 0);
        int y = prefs.getInt(Y_POS, 0);
        int width = prefs.getInt(WIDTH, 640);
        int height = prefs.getInt(HEIGHT, 480);
        boolean max = prefs.getBoolean(MAXIMIZED, false);
        setBounds(x, y, width, height);
        if(max) {
            setExtendedState(MAXIMIZED_BOTH);
        }
        
        readEnablePlugin = new HashMap<String, Boolean>();
        enablePlugin = new TreeMap<String, Boolean>();
        int pluginCount = prefs.getInt(PLUGIN_COUNT, 0);
        ArrayList<String> pluginNames = new ArrayList<String>();
        for(int i=0;i<pluginCount;i++) {
            pluginNames.add(prefs.get(PLUGIN_PREFIX + i, ""));
        }
        for(Iterator<String> it=pluginNames.iterator();it.hasNext();) {
            String key = it.next();
            readEnablePlugin.put(key, prefs.getBoolean(PLUGIN_PREFIX + key, false));
        }
        
        //
        logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        logger.setLevel(Level.ALL);
        addWindowListener(this);
        mainPanel = new JPanel(new BorderLayout());
        setContentPane(mainPanel);
        mainPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        tabPanel = new JTabbedPane();
        tabPanel.setFont(new Font("Arial", Font.PLAIN, 10));
        mainPanel.add(tabPanel, BorderLayout.CENTER);
        plugins = new ArrayList<DeskPanel>();
        createToolbar();
        loadPlugins();
        Timer timer = new Timer("updater");
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                for(int i=0;i<plugins.size();i++) {
                    DeskPanel tempDesk = plugins.get(i);
                    if(enablePlugin.get(tempDesk.getViewName())) {
                        tempDesk.update();
                    }
                }
            }
        }, FIVE_MINUTES, FIVE_MINUTES);
    }
    public void run() {
        for(int i=0;i<plugins.size();i++) {
            DeskPanel tempDesk = plugins.get(i);
            if(enablePlugin.get(tempDesk.getViewName())) {
                tempDesk.startUp();
            }
        }
    }
    private void createToolbar() {
        JToolBar toolBar = new JToolBar(JToolBar.HORIZONTAL);
        mainPanel.add(toolBar, BorderLayout.NORTH);
        toolBar.setFloatable(false);
        controlPanelButton = new ImageButton();
        controlPanelButton.addActionListener(this);
        try {
            controlPanelButton.setIcon(new ImageIcon(ImageIO.read(getClass().getResourceAsStream("/images/controlpanel-default.png"))));
            controlPanelButton.setPressedIcon(new ImageIcon(ImageIO.read(getClass().getResourceAsStream("/images/controlpanel-down.png"))));
            controlPanelButton.setRolloverIcon(new ImageIcon(ImageIO.read(getClass().getResourceAsStream("/images/controlpanel-hover.png"))));
        }
        catch(IOException e) {
            logger.logp(Level.SEVERE, "sticky.gui.Main", "createToolbar", "Unable to load toolbar button icons.", e);
            controlPanelButton.setText("CP");
        }
        exitButton = new ImageButton();
        exitButton.addActionListener(this);
        try {
            exitButton.setIcon(new ImageIcon(ImageIO.read(getClass().getResourceAsStream("/images/exit-default.png"))));
            exitButton.setPressedIcon(new ImageIcon(ImageIO.read(getClass().getResourceAsStream("/images/exit-down.png"))));
            exitButton.setRolloverIcon(new ImageIcon(ImageIO.read(getClass().getResourceAsStream("/images/exit-hover.png"))));
        }
        catch(IOException e) {
            logger.logp(Level.SEVERE, "Main", "createToolbar", "Unable to load toolbar button icons.", e);
            exitButton.setText("E");
        }

        toolBar.add(controlPanelButton);
        toolBar.add(exitButton);
    }
    private void loadPlugins() {
        String pluginDirectory = STICKY_INSTALL_PATH + FILE_SEPARATOR + "plugin";
        File files[] = new File(pluginDirectory).listFiles();
        for(int i=0;i<files.length;i++) {
            if(files[i].isDirectory()) {
                parsePlugin(new File(files[i].getAbsolutePath() + "/plugin.xml"));
            }
        }
        Thread t = new Thread(this);
        t.start();
    }
    private DeskPanel parsePlugin(File file) {
        DeskPanel panel = null;
        try {
            String jar = "";
            String className = "";
            String view = "";
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
            NodeList list = doc.getChildNodes();
            Node node = list.item(0);
            String nodeName = node.getNodeName();
            if(nodeName.equals("plugin")) {
                list = node.getChildNodes();
                for(int i=0;i<list.getLength();i++) {
                    node = list.item(i);
                    if(node.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element)node;
                        nodeName = element.getNodeName();
                        if(nodeName.equals("jar")) {
                            jar = element.getAttribute("name");
                        }
                        else if(nodeName.equals("class")) {
                            className = element.getAttribute("name");
                        }
                        else if(nodeName.equals("view")) {
                            view = element.getAttribute("name");
                        }
                    }
                }
                try {
                    URLClassLoader loader = URLClassLoader.newInstance(new URL[] {new URL("file:/" + file.getParent() + FILE_SEPARATOR + jar)});
                    Class<?> clazz = loader.loadClass(className);
                    DeskPanel deskPanel = (DeskPanel)clazz.newInstance();
                    plugins.add(deskPanel);
                    boolean enable = true;
                    if(readEnablePlugin.containsKey(deskPanel.getViewName())) {
                        enable = readEnablePlugin.get(deskPanel.getViewName());
                    }
                    enablePlugin.put(deskPanel.getViewName(), enable);
                    if(enable) {
                        tabPanel.addTab(view, deskPanel);
                    }
                }
                catch(Exception e) {
                    logger.logp(Level.SEVERE, "sticky.Main", "parsePlugin", "Unable to load plugin called " + view, e);
                    e.printStackTrace();
                }
            }
            else {
                System.out.println("plugin.xml format is invalid");
            }
            
        }
        catch(SAXException e) {
            e.printStackTrace();
        }
        catch(ParserConfigurationException e) {
            e.printStackTrace();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        
        return panel;
    }
    private void exit() {
        for(Iterator<DeskPanel> it=plugins.iterator();it.hasNext();) {
            it.next().finish();
        }
        int x = getX();
        int y = getY();
        int width = getWidth();
        int height = getHeight();
        
        Preferences prefs = Preferences.systemNodeForPackage(this.getClass());
        if((getExtendedState() & MAXIMIZED_BOTH) == MAXIMIZED_BOTH) {
            prefs.putBoolean(MAXIMIZED, true);
        }
        else {
            prefs.putInt(X_POS, x);
            prefs.putInt(Y_POS, y);
            prefs.putInt(WIDTH, width);
            prefs.putInt(HEIGHT, height);
            prefs.putBoolean(MAXIMIZED, false);
        }

        int count = 0;
        for(Iterator<String> it=enablePlugin.keySet().iterator();it.hasNext();) {
            String key = it.next();
            prefs.put(PLUGIN_PREFIX + count, key);
            prefs.putBoolean(PLUGIN_PREFIX + key, enablePlugin.get(key));
            count++;
        }
        prefs.putInt(PLUGIN_COUNT, count);
        
        System.exit(0);
    }
    public void windowActivated(WindowEvent e) {
        if(trayIcon != null) {
            tray.removeTrayIcon(trayIcon);
        }
    }

    public void windowClosed(WindowEvent e) {
    }

    public void windowClosing(WindowEvent e) {
        if(trayIcon != null) {
            tray.addTrayIcon(trayIcon);
        }
    }

    public void windowDeactivated(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
    }

    public void windowIconified(WindowEvent e) {
        if(trayIcon != null) {
            tray.addTrayIcon(trayIcon);
        }
        setVisible(false);
        setExtendedState(JFrame.NORMAL);
    }

    public void windowOpened(WindowEvent e) {
    }
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        
        if(source.equals(exitButton)) {
            exit();
        }
        if(source.equals(controlPanelButton)) {
            if(controlPanelDialog == null) {
                controlPanelDialog = new ControlPanelDialog(this, plugins, enablePlugin);
            }
            controlPanelDialog.setVisible(true);
        }
        else if(source.equals(trayMenuOpen)) {
            setVisible(true);
        }
    }
    public static void main(String args[]) {
        try {
            JFrame.setDefaultLookAndFeelDecorated(true);
            JDialog.setDefaultLookAndFeelDecorated(true);
            UIManager.setLookAndFeel("com.birosoft.liquid.LiquidLookAndFeel");
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        Main m = new Main();
        m.setVisible(true);
    }
}
