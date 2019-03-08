package ie.gmit.gui;

import com.leapmotion.leap.*;
/*import com.leapmotion.leap.Image;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;*/
import java.util.function.Consumer;

public class LeapMotionListener extends Listener {
    private Consumer keyTapListener = null;
/*
    CamPanel cp = new CamPanel();
    CamPanel cp2 = new CamPanel();*/

    @Override
    public void onConnect(final Controller controller) {
        System.out.println("Connected");
        //Setup the controller for Key Tap gestures and image projection
        controller.enableGesture(Gesture.Type.TYPE_KEY_TAP);
        controller.setPolicy(Controller.PolicyFlag.POLICY_IMAGES);
        controller.config().setFloat("Gesture.KeyTap.MinDownVelocity", 40.0f);
        controller.config().setFloat("Gesture.KeyTap.HistorySeconds", .2f);
        controller.config().setFloat("Gesture.KeyTap.MinDistance", 0.1f);
        controller.config().save();
        /*//Create a new window
        javax.swing.SwingUtilities.invokeLater(() -> {
            final JFrame f = new JFrame("Preview");
            final Dimension d = new Dimension(640, 480);
            f.setPreferredSize(d);
            f.setMinimumSize(d);
            f.setMaximumSize(d);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.getContentPane().setLayout(new FlowLayout());
            f.setSize(1000, 1000);
            f.setLocation(100, 100);
            f.pack();
            f.setVisible(true);
            f.add(this.cp);
            f.add(this.cp2);
            this.cp.setPreferredSize(new Dimension(640, 240));
            this.cp2.setPreferredSize(new Dimension(640, 240));
        });*/
    }

    @Override
    public void onFrame(final Controller controller) {
        final com.leapmotion.leap.Frame frame = controller.frame();
       /* if (frame.isValid()) {
            final ImageList images = frame.images();
            for (int i = 0; i < 2; i++) {
                final Image image = images.get(i);
                final java.awt.image.BufferedImage bi = new BufferedImage(image.width(), image.height(), BufferedImage.TYPE_INT_RGB);
                final byte[] imageData = image.data();
                for (int y = 0; y < image.height() * image.width(); y++) {
                    final int r = (imageData[y] & 0xFF) << 16; //convert to unsigned and shift into place
                    final int g = (imageData[y] & 0xFF) << 8;
                    final int b = imageData[y] & 0xFF;
                    bi.setRGB(y / image.width(), y % image.height(), r | g | b);
                }

                if (i == 0) {
                    this.cp.setImage(bi);
                } else {
                    this.cp2.setImage(bi);
                }
            }
        }*/

        //System.out.println(frame.gestures().count());
        frame.gestures().forEach((g) -> {
            if (g.type() == Gesture.Type.TYPE_KEY_TAP) {
                final KeyTapGesture ktp = new KeyTapGesture(g);
                final Vector pos = ktp.position();
                System.out.println("Tapped at: " + pos.getX() + "  " + pos.getY());
                if (this.keyTapListener != null) {
                    this.keyTapListener.accept(pos);
                }
            }
            System.out.println("Gesture name:" + g.type().name());
        });
    }

    /**
     * Register a listener for key tap
     *
     * @param c
     */
    public void onKeyTap(final Consumer<? extends Vector> c) {
        this.keyTapListener = c;
    }

}
