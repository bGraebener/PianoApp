package ie.gmit.gui;

import com.leapmotion.leap.*;
//import com.leapmotion.leap.Image;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class LeapMotionListener extends Listener {
    private Consumer keyTapListener = null;
    private Consumer fingerMoveListener = null;
/*
    CamPanel cp = new CamPanel();
    CamPanel cp2 = new CamPanel();*/

    @Override
    public void onConnect(final Controller controller) {
        System.out.println("Connected");
        //Setup the controller for Key Tap gestures and image projection
        controller.enableGesture(Gesture.Type.TYPE_KEY_TAP);
        controller.enableGesture(Gesture.Type.TYPE_SWIPE);
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
        //System.out.println(frame.interactionBox().width());
        final Map<Character, List<Vector>> hands = new HashMap<>();
        frame.hands().forEach((h) -> {
            final char side = h.isLeft() ? 'L' : 'R';
            if (hands.get(side) == null) {
                hands.put(side, new ArrayList<>());
            }
            h.fingers().forEach((f) ->
                    hands.get(side).add(f.bone(Bone.Type.TYPE_DISTAL).center())
            );

        });
        if (this.fingerMoveListener != null) {
            this.fingerMoveListener.accept(hands);
        }

        //System.out.println(frame.gestures().count());
        frame.gestures().forEach((g) -> {
            if (g.type() == Gesture.Type.TYPE_KEY_TAP) {
                final KeyTapGesture ktp = new KeyTapGesture(g);
                final Finger finger = new Finger(ktp.pointable());
                if (this.keyTapListener != null) {
                    this.keyTapListener.accept(finger.bone(Bone.Type.TYPE_DISTAL).center());
                }
            }else if(g.type() == Gesture.Type.TYPE_SWIPE){
                //System.out.println("Swiped");
            }
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

    /**
     * Register a listener for finger move
     *
     * @param c
     */
    public void onFingerMove(final Consumer<? extends Map<Character, List<Vector>>> c) {
        this.fingerMoveListener = c;
    }

}
