package sticky.plugin.notepad;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import sticky.gui.DeskPanel;

public class Main  extends DeskPanel implements FocusListener {
    private static final long serialVersionUID = 7303651645926113603L;

    private JTextArea notes;
    private JScrollPane notesScroll;
    private String notesContent;
    
    public Main() {
        super("Notepad");
    }
    @Override
    public void init() {
        notes = new JTextArea();
        notes.addFocusListener(this);
        notesScroll = new JScrollPane(notes);
        add(notesScroll, BorderLayout.CENTER);
        
        JPanel labelPanel = new JPanel(new FlowLayout());
        add(labelPanel, BorderLayout.NORTH);
        labelPanel.add(new JLabel("Enter freeform text below"));
        loadText();
    }

    @Override
    public void shutdown() {
        saveText();
    }

    @Override
    public void update() {
        saveText();
    }

    private void saveText() {
        String tempContent = notes.getText();
        if(!tempContent.equals(notesContent)) {
            notesContent = tempContent;
            try {
                FileWriter fout = new FileWriter(getDataFile());
                fout.write(notesContent);
                fout.flush();
                fout.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void loadText() {
        File dataFile = getDataFile();
        if(dataFile.exists()) {
            try {
                FileInputStream fin = new FileInputStream(dataFile);
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                byte buffer[] = new byte[8192];
                int read = fin.read(buffer);
                while(read != -1) {
                    bout.write(buffer, 0, read);
                    read = fin.read(buffer);
                }
                notesContent = new String(bout.toByteArray());
                fin.close();
                notes.setText(notesContent);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private File getDataFile() {
        return new File(getStorageLocation(), "notes");
    }
    @Override
    public void focusGained(FocusEvent e) {
    }
    @Override
    public void focusLost(FocusEvent e) {
        saveText();
    }
}
