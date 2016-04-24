package sticky.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;

public class ControlPanelDialog extends JDialog implements ActionListener {
    private static final long serialVersionUID = 1L;
    
    private EnablePluginModel enablePluginModel;
    private List<ControlPanel> panelList;
    
    private JButton okButton;
    private JButton applyButton;
    private JButton cancelButton;

    public ControlPanelDialog(JFrame frame, List<DeskPanel> plugins, Map<String, Boolean> enablePlugin) {
        super(frame, "Control Panel", true);
        enablePluginModel = new EnablePluginModel(enablePlugin);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        setContentPane(mainPanel);
        JTabbedPane tabPane = new JTabbedPane();
        mainPanel.add(tabPane, BorderLayout.CENTER);
        tabPane.addTab("Main", mainDialog());
        
        panelList = new ArrayList<ControlPanel>();
        for(Iterator<DeskPanel> it=plugins.iterator();it.hasNext();) {
            DeskPanel temp = it.next();
            ControlPanel controlPanel = temp.getControlPanel();
            if(controlPanel != null) {
                tabPane.addTab(temp.getViewName(), controlPanel);
                controlPanel.startUp();
                panelList.add(controlPanel);
            }
        }
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        okButton = new JButton("Ok");
        buttonPanel.add(okButton);
        okButton.addActionListener(this);
        applyButton = new JButton("Apply");
        buttonPanel.add(applyButton);
        applyButton.addActionListener(this);
        cancelButton = new JButton("Cancel");
        buttonPanel.add(cancelButton);
        cancelButton.addActionListener(this);
        setSize(400, 400);
    }
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        
        if(source.equals(okButton)) {
            for(Iterator<ControlPanel> it=panelList.iterator();it.hasNext();) {
                it.next().finish();
            }
            setVisible(false);
        }
        else if(source.equals(applyButton)) {
            for(Iterator<ControlPanel> it=panelList.iterator();it.hasNext();) {
                it.next().finish();
            }
        }
        else if(source.equals(cancelButton)) {
            setVisible(false);
        }
    }
    private JPanel mainDialog() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        JTable pluginTable = new JTable(enablePluginModel);
        JScrollPane scroll = new JScrollPane(pluginTable);
        mainPanel.add(scroll, BorderLayout.CENTER);
        scroll.setBorder(BorderFactory.createTitledBorder("Manage plugins"));
        
        return mainPanel;
    }
}
