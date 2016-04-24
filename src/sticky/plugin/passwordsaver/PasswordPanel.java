package sticky.plugin.passwordsaver;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
 
public class PasswordPanel extends JPanel implements ActionListener {
    private static final long serialVersionUID = 1L;
    
    private PasswordBean passwordBean;
    private JTextField locationTextfield;
    private JButton passwordButton;
    private JCheckBox selectBox;
    
    public PasswordPanel() {
        passwordBean = new PasswordBean();
        createUI();
    }
    public PasswordPanel(PasswordBean passwordBean) {
        this.passwordBean = passwordBean;
        createUI();
    }
    public void actionPerformed(ActionEvent e) {
        String password = passwordBean.getPassword().trim();
        password = (String)JOptionPane.showInputDialog(
                this, 
                "Please enter a password or remove it to clear the password.",
                "Password entry",
                JOptionPane.QUESTION_MESSAGE,
                null, null,
                password);
        if(password != null) {
            passwordBean.setPassword(password.trim());
            if(password.trim().length() <= 0) {
                passwordButton.setText("Enter password");
            }
            else {
                passwordButton.setText("Show password");
            }
            passwordButton.repaint();
        }
    }
    private void resetUI() {
        removeAll();
    }
    private void createUI() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 1));
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        add(mainPanel);
        JPanel finalPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.add(finalPanel, BorderLayout.NORTH);
        selectBox = new JCheckBox();
        finalPanel.add(selectBox, BorderLayout.WEST);
        
        JPanel topPanel = new JPanel(new BorderLayout());
        finalPanel.add(topPanel, BorderLayout.CENTER);
        
        JPanel labelPanel = new JPanel(new FlowLayout());
        topPanel.add(labelPanel, BorderLayout.WEST);
        labelPanel.add(new JLabel("Location for password"));

        JPanel textPanel = new JPanel(new FlowLayout());
        topPanel.add(textPanel, BorderLayout.CENTER);
        locationTextfield = new JTextField(10);
        textPanel.add(locationTextfield);
        locationTextfield.setText(passwordBean.getLocationForPassword());

        JPanel buttonPanel = new JPanel(new FlowLayout());
        topPanel.add(buttonPanel, BorderLayout.EAST);
        passwordButton = new JButton();
        buttonPanel.add(passwordButton);
        passwordButton.addActionListener(this);
        passwordButton.setMargin(new Insets(4, 6, 4, 6));
        if(passwordBean.getPassword().trim().length() <= 0) {
            passwordButton.setText("Enter password");
        }
        else {
            passwordButton.setText("Show password");
        }
    }
    public void store(ObjectOutputStream oout) throws IOException {
        passwordBean.setLocationForPassword(locationTextfield.getText().trim());
        oout.writeObject(passwordBean);
    }
    public void retrieve(ObjectInputStream oin) throws IOException, ClassNotFoundException {
        passwordBean = (PasswordBean)oin.readObject();
        resetUI();
        createUI();
        validateTree();
    }
    public boolean isSelected() {
        return selectBox.isSelected();
    }
}
