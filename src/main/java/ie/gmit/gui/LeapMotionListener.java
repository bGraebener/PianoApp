package ie.gmit.gui;

import com.leapmotion.leap.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class LeapMotionListener extends Listener {
    private Consumer keyTapListener = null;
    private Consumer fingerMoveListener = null;

    @Override
    public void onConnect(final Controller controller) {
        System.out.println("Connected");
        //Setup the controller for Key Tap gestures and image projection
        controller.enableGesture(Gesture.Type.TYPE_KEY_TAP);
        controller.config().setFloat("Gesture.KeyTap.MinDownVelocity", 40.0f);
        controller.config().setFloat("Gesture.KeyTap.HistorySeconds", .2f);
        controller.config().setFloat("Gesture.KeyTap.MinDistance", 0.1f);
        controller.config().save();
    }

    @Override
    public void onFrame(final Controller controller) {
        final com.leapmotion.leap.Frame frame = controller.frame();
        final Map<Character, List<Vector>> hands = new HashMap<>();
        //Stream the hands
        frame.hands().forEach((h) -> {
            final char side = h.isLeft() ? 'L' : 'R';
            //Add finger positions to each hand side
            h.fingers().forEach((f) ->
                    hands.computeIfAbsent(side, k -> new ArrayList<>())
                            .add(f.bone(Bone.Type.TYPE_DISTAL).center())
            );

        });
        if (this.fingerMoveListener != null) {
            this.fingerMoveListener.accept(hands);
        }

        //Stream the gestures
        frame.gestures().forEach((g) -> {
            if (g.type() == Gesture.Type.TYPE_KEY_TAP) {
                final KeyTapGesture ktp = new KeyTapGesture(g);
                //Get a finger from the keytap
                final Finger finger = new Finger(ktp.pointable());
                if (this.keyTapListener != null) {
                    //Notify listener with the finger's position
                    this.keyTapListener.accept(finger.bone(Bone.Type.TYPE_DISTAL).center());
                }
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
