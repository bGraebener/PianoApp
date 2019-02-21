import com.leapmotion.leap.*;

import javax.sound.midi.*;
import java.util.*;
import java.io.IOException;

public class Runner {
    private static int oldNote = 0;

    public static void main(final String[] args) throws Exception {
        final MidiDevice.Info[] midiDeviceInfo = MidiSystem.getMidiDeviceInfo();
        System.out.println(Arrays.toString(midiDeviceInfo));

        final MidiDevice.Info pianoAppMidiDriver = Arrays.stream(midiDeviceInfo)
                .filter(mdi -> mdi.toString().equals("PianoApp")).skip(1).findFirst().orElseThrow(RuntimeException::new);

        MidiDevice midiDevice = MidiSystem.getMidiDevice(pianoAppMidiDriver);
        midiDevice.open();
        Receiver receiver = midiDevice.getReceiver();

        Scanner scanner = new Scanner(System.in);

        String note;
        do {
            note = scanner.nextLine();

            switch (note) {
                case "a":
                    sendMessage(receiver, 69);
                    break;
                case "c":
                    sendMessage(receiver, 60);
                    break;
                case "d":
                    sendMessage(receiver, 62);
                    break;
                default:
                    sendMessage(receiver, 64);
            }
        } while (!note.equals("q"));

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

    private static void sendMessage(Receiver receiver, int note) throws InvalidMidiDataException {
        ShortMessage myMsg = new ShortMessage();
        long timeStamp = -1;

        //stop old note from playing
        myMsg.setMessage(ShortMessage.NOTE_OFF, 0, oldNote, 0);
        receiver.send(myMsg, timeStamp);
        oldNote = note;

        // Start playing the note Middle C (60),
        // moderately loud (velocity = 93).
        myMsg.setMessage(ShortMessage.NOTE_ON, 0, note, 93);
        receiver.send(myMsg, timeStamp);
    }
}
