package ie.gmit.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class CamPanel extends JPanel {

    java.awt.Image Image = null;
    private static final long serialVersionUID = -7771996424537203936L;


    public CamPanel() {
        this.setBackground(Color.WHITE);
        this.setDoubleBuffered(true); //Each image is buffered twice to avoid tearing / stutter
    }

    @Override
    public void paintComponent(final Graphics g) {
        super.paintComponent(g);
        final Graphics2D g2 = (Graphics2D) g;
        if (this.Image != null) {
            g2.drawImage(this.Image, 0, 0, null);
        }

    }

    public void setImage(final BufferedImage Image) {
        this.Image = Image;
        this.repaint();
    }
}
