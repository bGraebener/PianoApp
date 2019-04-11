package ie.gmit.gui;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Runner {

    private final MidiController midiController;
    //The start note which allocates to the first key
    private int startNoteOffset = 59;
    //The distance to detect the fingers between. Its is -detectRange <> detectRange
    private int detectRange = 175;
    //The number of keys on the piano
    private int numberOfKeys = 12;
    private LeapMotionInitialiser lmi;

    /**
     * Sets up a midi receiver and the leap motion key tap
     *
     * @param args
     */
    private Runner(final String[] args) {
        //Initialise piano properties
        this.setPropertiesFromArgs(args);

        this.midiController = new MidiController();
        try {
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
        } catch (final Exception e) {
            e.printStackTrace();

        }
    }

    public static void main(final String[] args) {
        new Runner(args);

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

            if (key > 0 && key <= this.numberOfKeys) {

                //Play sound
                this.midiController.sendMessage(key + this.startNoteOffset);
                System.out.println("Key tapped: " + key);
            }
        });
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
        possibleArgs.put("-s", (n) -> this.startNoteOffset = n);
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
