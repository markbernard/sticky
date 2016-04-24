package sticky.gui;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.JButton;

/**
 * Custom button implementation to allow for displaying only images without the standard button decorations.
 *
 * @author Mark
 *
 */
public class ImageButton extends JButton {
    private static final long serialVersionUID = 1L;
    private Icon icon;
    private Dimension size;

    /**
     * Creates a new ImageButton object.
     *
     * @param icon Icon to set as the default
     */
    public ImageButton() {
        enableEvents(AWTEvent.MOUSE_EVENT_MASK);
        setBorder(null);
        setOpaque(false);
        setBackground(new Color(0, 0, 0, 0));
        
    }

    /**
     * Creates a new ImageButton object.
     *
     * @param icon Icon to set as the default
     */
    public ImageButton(Icon icon) {
        this.icon = icon;
        setIcon(icon);
        enableEvents(AWTEvent.MOUSE_EVENT_MASK);
        setBorder(null);
        setOpaque(false);
        setBackground(new Color(0, 0, 0, 0));
    }

    /**
     * Override paint to provide a button that only draws an image.
     *
     * @param g Graphics object to draw with
     */
    public void paint(Graphics g) {
        Color oldColor = g.getColor();
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());

        if(icon != null) {
            int x = (getWidth() - icon.getIconWidth()) / 2;
            int y = (getHeight() - icon.getIconHeight()) / 2;
            g.setColor(oldColor);
            icon.paintIcon(this, g, x, y);
        }
    }

    /**
     * Ensure the button only takes as much space as it needs.
     *
     * @return A Dimension object representing the preferred size
     */
    public Dimension getPreferredSize() {
        return size;
    }

    /**
     * Allow the button to take on sizes other than the image size
     *
     * @param size Set the button to this size
     */
    public void setPreferredSize(Dimension size) {
        this.size = size;
    }

    /**
     * Set the default icon
     *
     * @param icon Icon to use as default
     */
    public void setIcon(Icon icon) {
        super.setIcon(icon);
        this.icon = icon;
        size = new Dimension(icon.getIconWidth(), icon.getIconHeight());
        repaint();
    }

    /**
     * Whether to enable the button or not.
     * If button is disabled then the disabled icon is used.  Otherwise use the default icon.
     *
     * @param enabled true to enable; false to disable
     */
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        if(enabled) {
            icon = getIcon();
        }
        else {
            icon = getDisabledIcon();
        }

        repaint();
    }

    /**
     * Process all events for the mouse on the button.  This will set the appropriate icon for drawing.
     *
     * @param e event object
     */
    public void processMouseEvent(MouseEvent e) {
        super.processMouseEvent(e);

        int id = e.getID();

        if(isEnabled()) {
            if(id == MouseEvent.MOUSE_ENTERED) {
                icon = getRolloverIcon();

                if(icon == null) {
                    icon = getIcon();
                }
            }
            else if(id == MouseEvent.MOUSE_EXITED) {
                icon = getIcon();
            }
            else if(id == MouseEvent.MOUSE_PRESSED) {
                icon = getPressedIcon();

                if(icon == null) {
                    icon = getIcon();
                }
            }
            else if(id == MouseEvent.MOUSE_RELEASED) {
                icon = getRolloverIcon();

                if(icon == null) {
                    icon = getIcon();
                }
            }
        }
        else {
            icon = getDisabledIcon();
        }

        repaint();
    }
}
