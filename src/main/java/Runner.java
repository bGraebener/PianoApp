import com.leapmotion.leap.*;

import javax.sound.midi.*;
import java.util.*;
import java.io.IOException;

public class Runner {
    public static void main(final String[] args) throws Exception {
        System.out.println(Arrays.toString(MidiSystem.getMidiDeviceInfo()));

        final MidiDevice midiDevice = MidiSystem.getMidiDevice(MidiSystem.getMidiDeviceInfo()[6]);
        midiDevice.open();
        System.out.println(midiDevice.getDeviceInfo());
        final Receiver receiver = midiDevice.getReceiver();

        final Scanner scanner = new Scanner(System.in);

        String note;
        do {
            note = scanner.nextLine();

            switch (note) {
                case "a":

            }
        } while (!note.equals("q"));

        Runner.sendMessage(receiver);

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

    private static void sendMessage(final Receiver receiver) throws InvalidMidiDataException {
        final ShortMessage myMsg = new ShortMessage();
        // Start playing the note Middle C (60),
        // moderately loud (velocity = 93).
        myMsg.setMessage(ShortMessage.NOTE_ON, 0, 60, 93);
        final long timeStamp = -1;
        receiver.send(myMsg, timeStamp);
    }
}
