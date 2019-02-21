import com.leapmotion.leap.*;

import java.io.IOException;

public class Runner {
    public static void main(final String[] args) {
        final Controller c;
        if (NativeLibrary.loadSystem("native")) {
            final Listener l = new Listener() {
                @Override
                public void onConnect(final Controller controller) {
                    System.out.println("Connected");
                    controller.enableGesture(Gesture.Type.TYPE_KEY_TAP);
                    controller.config().setFloat("Gesture.KeyTap.MinDownVelocity", 40.0f);
                    controller.config().setFloat("Gesture.KeyTap.HistorySeconds", .2f);
                    controller.config().setFloat("Gesture.KeyTap.MinDistance", 0.5f);
                    controller.config().save();
                }

                @Override
                public void onFrame(final Controller controller) {
                    final Frame frame = controller.frame();
                    if (frame.hands().count() > 0) {
                        frame.hands().forEach((h) -> {
                            if (h.isLeft()) {
                                System.out.println("Left hand is present with finders: " + h.fingers().count() + ", " + h.toString());
                            } else {
                                System.out.println("Right hand is present with finders: " + h.fingers().count() + ", " + h.toString());
                            }
                        });
                    }
                }

            };
            c = new Controller();
            c.addListener(l);
            // Keep this process running until Enter is pressed
            try {
                System.in.read();
            } catch (final IOException e) {
                e.printStackTrace();
            }
            c.removeListener(l);
        } else {
            System.out.println("Leap Native is not loaded");
        }


    }
}
