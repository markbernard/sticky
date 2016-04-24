package sticky.plugin.passwordsaver;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import sticky.gui.DeskPanel;

public class Main extends DeskPanel implements ActionListener {
    private static final long serialVersionUID = -9144329444851667953L;

    // Salt
//    private static final byte[] salt = {
//        (byte)0xc7, (byte)0x73, (byte)0x21, (byte)0x8c,
//        (byte)0x7e, (byte)0xc8, (byte)0xee, (byte)0x99
//    };
//
    // Iteration count
//    private static final int count = 20;
    
    private List<PasswordPanel> passwordPanels;
    private JButton insert;
    private JButton delete;
    private JButton passwordProtectButton;
//    private JPasswordField password;
    private JPanel passwordPanel;

    public Main() {
        super("Password Saver");
    }
    public void init() {
        passwordPanels = new ArrayList<PasswordPanel>();
        JPanel mainPanel = new JPanel(new BorderLayout());
        JScrollPane scroll = new JScrollPane(mainPanel);
        add(scroll, BorderLayout.CENTER);
        passwordPanel = new JPanel(new GridLayout(0, 1));
        mainPanel.add(passwordPanel, BorderLayout.NORTH);
//        password = new JPasswordField(15);
        loadPasswordPanel();
        
        JPanel buttonPanel = new JPanel();
        add(buttonPanel, BorderLayout.SOUTH);
        insert = new JButton("Add line");
        buttonPanel.add(insert);
        insert.addActionListener(this);
        delete = new JButton("Delete selected");
        buttonPanel.add(delete);
        delete.addActionListener(this);
        validateTree();
//        JPanel passwordProtectPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
//        passwordProtectButton = new JButton("Password");
//        passwordProtectPanel.add(passwordProtectButton);
//        passwordProtectButton.addActionListener(this);
//        passwordProtectPanel.add(password);
//        buttonPanel.add(passwordProtectPanel);
    }

    public void shutdown() {
        try {
            FileOutputStream out = new FileOutputStream(getStorageLocation() + "/pw.bin");
            ObjectOutputStream oout = new ObjectOutputStream(out);
            oout.writeInt(passwordPanels.size());
            for(Iterator<PasswordPanel> it=passwordPanels.iterator();it.hasNext();) {
                it.next().store(oout);
            }
            out.flush();
            out.close();
        }
        catch(Exception e) {
            logger.logp(Level.SEVERE, "sticky.plugin.passwordsaver.Main", "finish", "Error attempting to store password list.", e);
        }
    }

    @Override
    public void update() {
    }

    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        
        if(source.equals(insert)) {
            PasswordPanel tempPanel = new PasswordPanel();
            passwordPanels.add(tempPanel);
            passwordPanel.add(tempPanel);
            validateTree();
        }
        else if(source.equals(delete)) {
            if(JOptionPane.showConfirmDialog(this, "Are you sure you want to delete the selected passwords?", "Delete", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                for(Iterator<PasswordPanel> it=passwordPanels.iterator();it.hasNext();) {
                    PasswordPanel tempPanel = it.next();
                    if(tempPanel.isSelected()) {
                        it.remove();
                        passwordPanel.remove(tempPanel);
                    }
                }
                validateTree();
            }
        }
        else if(source.equals(passwordProtectButton)) {
            
        }
    }
    private void loadPasswordPanel() {
        loadPasswords();
        for(Iterator<PasswordPanel> it=passwordPanels.iterator();it.hasNext();) {
            passwordPanel.add(it.next());
        }
        if(passwordPanels.size() <= 0) {
            PasswordPanel tempPanel = new PasswordPanel();
            passwordPanels.add(tempPanel);
            passwordPanel.add(tempPanel);
        }
    }
    private boolean loadPasswords() {
        boolean result = false;
        
        try {
            File fin = new File(getStorageLocation() + "/pw.bin");
            if(fin.exists()) {
                FileInputStream in = new FileInputStream(fin);
                ObjectInputStream oin = new ObjectInputStream(in);
                int length = oin.readInt();
                for(int i=0;i<length;i++) {
                    PasswordPanel tempPanel = new PasswordPanel();
                    tempPanel.retrieve(oin);
                    passwordPanels.add(tempPanel);
                }
            }
            result = true;
        }
        catch(Exception e) {
            logger.logp(Level.SEVERE, "sticky.plugin.passwordsaver.Main", "loadPasswords", "Error attempting to retrieve password list.", e);
        }
        
        return result;
    }
//    private InputStream getCipherInputStream(InputStream in) {
//        InputStream result = in;
//        
//        char pass[] = password.getPassword();
//        if(pass.length >0) {
//            try {
//                PBEKeySpec pbeKeySpec;
//                PBEParameterSpec pbeParamSpec;
//                SecretKeyFactory keyFac;
//
//                // Create PBE parameter set
//                pbeParamSpec = new PBEParameterSpec(salt, count);
//
//                // convert password
//                // into a SecretKey object, using a PBE key
//                // factory.
//                pbeKeySpec = new PBEKeySpec(pass);
//                keyFac = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
//                SecretKey pbeKey = keyFac.generateSecret(pbeKeySpec);
//
//                // Create PBE Cipher
//                Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
//
//                // Initialize PBE Cipher with key and parameters
//                pbeCipher.init(Cipher.ENCRYPT_MODE, pbeKey, pbeParamSpec);
//                
//                CipherInputStream cin = new CipherInputStream(in, pbeCipher);
//                result = cin;
//            }
//            catch(Exception e) {
//                logger.logp(Level.SEVERE, "sticky.plugin.passwordsaver.Main", "getCipherInputStream", "Error attempting to read from password protected file.", e);
//                result = in;
//            }
//        }
//        
//        return result;
//    }
//    private OutputStream getCipherOutputStream(OutputStream out) {
//        OutputStream result = out;
//        
//        char pass[] = password.getPassword();
//        if(pass.length >0) {
//            try {
//                PBEKeySpec pbeKeySpec;
//                PBEParameterSpec pbeParamSpec;
//                SecretKeyFactory keyFac;
//
//                // Create PBE parameter set
//                pbeParamSpec = new PBEParameterSpec(salt, count);
//
//                // convert password
//                // into a SecretKey object, using a PBE key
//                // factory.
//                pbeKeySpec = new PBEKeySpec(pass);
//                keyFac = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
//                SecretKey pbeKey = keyFac.generateSecret(pbeKeySpec);
//
//                // Create PBE Cipher
//                Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
//
//                // Initialize PBE Cipher with key and parameters
//                pbeCipher.init(Cipher.ENCRYPT_MODE, pbeKey, pbeParamSpec);
//                
//                CipherOutputStream cout = new CipherOutputStream(out, pbeCipher);
//                result = cout;
//            }
//            catch(Exception e) {
//                logger.logp(Level.SEVERE, "sticky.plugin.passwordsaver.Main", "getCipherOutputStream", "Error attempting to write to password protected file.", e);
//                result = out;
//            }
//        }
//        
//        return result;
//    }
}
