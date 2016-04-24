package sticky.gui;

import java.awt.BorderLayout;
import java.awt.Frame;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class LoadPluginDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    public LoadPluginDialog(Frame owner, String message) {
        super(owner, "Loading plugin", false);
        JPanel mainPanel = new JPanel(new BorderLayout());
        setContentPane(mainPanel);
        mainPanel.add(new JLabel(message), BorderLayout.CENTER);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 20, 40, 20));
        pack();
    }
}
