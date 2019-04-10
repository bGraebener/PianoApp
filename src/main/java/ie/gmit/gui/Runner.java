package ie.gmit.gui;

import javax.sound.midi.InvalidMidiDataException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Runner {
    private static int oldNote = 0;
    //The start note which allocates to the first key
    private int startNote = 59;
    //The distance to detect the fingers between. Its is -detectRange <> detectRange
    private int detectRange = 175;
    //The number of keys on the piano
    private int numberOfKeys = 12;
//    private Receiver midiReceiver;

    private MidiController midiController;
    private LeapMotionInitialiser lmi;

    /**
     * Sets up a midi receiver and the leap motion key tap
     *
     * @param args
     */
    private Runner(final String[] args) {
        //Initialise piano properties
        this.setPropertiesFromArgs(args);

//        final MidiDevice.Info[] midiDeviceInfo = MidiSystem.getMidiDeviceInfo();
//        MidiDevice midiDevice;
//        this.midiReceiver = null;
//        //Loop each rdevice info
//        for (final MidiDevice.Info i : midiDeviceInfo) {
//            //Find piano app
//            if (i.toString().equals(("PianoApp"))) {
//                //Try to get the receiver
//                try {
//                    midiDevice = MidiSystem.getMidiDevice(i);
//                    midiDevice.open();
//                    this.midiReceiver = midiDevice.getReceiver();
//                    break;
//                } catch (final Exception e) {
//
//                }
//            }
//        }

        this.midiController = new MidiController();
        try {
            //Exit if receiver was not found
//            if (this.midiReceiver == null) {
//                throw new IOException("Could not load midi driver");
//            }

            // Try to setup leap motion
            this.setUpOnTapListener();
        } catch (final IOException e) {
            //Exit with a message if the midi receiver could not be set or the leap motion listener could not be registered
            System.out.println(e.getMessage());
            System.exit(0);
        }

        try {
            System.in.read();
            this.lmi.getC().removeListener(this.lmi.getL());
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    /**
     * Initialises Leap Motion and sets up the key tap listener
     *
     * @throws IOException
     */
    private void setUpOnTapListener() throws IOException {
        //Initialise leap motion
        this.lmi = new LeapMotionInitialiser(this.detectRange, this.numberOfKeys);
        //Register to key tap
        this.lmi.onKeyTap((pos) -> {
            //Get the key tapped
            final int key = this.lmi.whichKey(pos.getX());
            if (key > 0 && key < this.numberOfKeys) {
                try {
                    //Play sound
//                    Runner.sendMessage(this.midiReceiver, key + this.startNote);
                    this.midiController.sendMessage(key + this.startNote);
                } catch (final InvalidMidiDataException e) {
                    e.printStackTrace();
                }
                System.out.println("Key tapped: " + key);
            }
        });
    }

//    private static void sendMessage(final Receiver receiver, final int note) throws InvalidMidiDataException {
//        final ShortMessage myMsg = new ShortMessage();
//        final long timeStamp = -1;
//
//        final ExecutorService service = Executors.newFixedThreadPool(1);
//        service.submit(() -> {
//
//            try {
//                Thread.sleep(1000);
//                //stop old note from playing
//                myMsg.setMessage(ShortMessage.NOTE_OFF, 0, Runner.oldNote, 0);
//                receiver.send(myMsg, timeStamp);
//                Runner.oldNote = note;
//            } catch (final InterruptedException | InvalidMidiDataException e) {
//                e.printStackTrace();
//            }
//
//        });
//
//        // Start playing the note Middle C (60),
//        // moderately loud (velocity = 93).
//        myMsg.setMessage(ShortMessage.NOTE_ON, 0, note, 93);
//        receiver.send(myMsg, timeStamp);
//    }

    public static void main(final String[] args) {
        /*//Load leap motion native files
        if (NativeLibraryLoader.loadNativeFiles()) {
            //if (NativeLibrary.loadSystem("native")) {
            Controller c = new Controller();
            LeapMotionListener l = new LeapMotionListener();
            c.addListener(l);

            //Register to key tap
            l.onKeyTap((pos) -> {
                System.out.println("tapped");
            });

            // Keep this process running until Enter is pressed
            try {
                System.in.read();
            } catch (final IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Leap Native is not loaded");
        }*/
        new Runner(args);

    }

    /**
     * Checks if the args array contains arguments.
     * If it does then sets the argument to be the provided one
     *
     * @param args
     */
    private void setPropertiesFromArgs(final String[] args) {
        final Map<String, Consumer<Integer>> possibleArgs = new HashMap<>();
        possibleArgs.put("-r", (n) -> this.detectRange = n);
        possibleArgs.put("-s", (n) -> this.startNote = n);
        possibleArgs.put("-k", (n) -> this.numberOfKeys = n);
        for (int i = 0; i < args.length; i++) {
            try {
                //Try to parse the parameter to int and call the consumer
                possibleArgs.get(args[i]).accept(Integer.valueOf(args[++i]));
            } catch (final Exception ignored) {
                //This will catch null consumer, invalid number format and index out of bounds
            }
        }
    }
}
