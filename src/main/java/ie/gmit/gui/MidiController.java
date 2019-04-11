package ie.gmit.gui;

import javax.sound.midi.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Class that handles all midi related actions.
 * <p>
 * There must be a midi driver called 'PianoApp' installed on the executing system for the application to work.
 * The
 */
public class MidiController {

    //    the midi receiver that receives the midi message
    private Receiver midiReceiver;

    public MidiController() {
        this.getMidiReceiver();
    }

    /**
     * Sends a midi events to the midi receiver.
     * <p>
     * Launches a new thread first to turn off any previously played note after one second.
     * Then sends a midi note on event to the receiver with the note parameter and a moderate velocity of 93.
     *
     * @param note the note to be send to the midi receiver
     */
    public void sendMessage(final int note) {
        final ShortMessage myMsg = new ShortMessage();
        final long timeStamp = -1;

        // moderately loud (velocity = 93).
        try {
            myMsg.setMessage(ShortMessage.NOTE_ON, 0, note, 93);
        } catch (final InvalidMidiDataException e) {
            System.err.println("Could not send midi message! ");
            System.err.println(e.getMessage());
        }
        this.midiReceiver.send(myMsg, timeStamp);

//        turn the note off after one second of playing
        final ExecutorService service = Executors.newFixedThreadPool(1);
        service.submit(() -> {
            try {
                Thread.sleep(1000);
                //stop old note from playing
                myMsg.setMessage(ShortMessage.NOTE_OFF, 0, note, 0);
                this.midiReceiver.send(myMsg, timeStamp);
            } catch (final InterruptedException | InvalidMidiDataException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Searches the system for a midi driver called 'PianoApp'.
     * <p>
     * If it is found it tries to open it and retrieve the midi receiver from the driver.
     */
    private void getMidiReceiver() {

        final MidiDevice.Info[] midiDeviceInfo = MidiSystem.getMidiDeviceInfo();
        MidiDevice midiDevice;
        this.midiReceiver = null;

        //Loop each midi device info
        for (final MidiDevice.Info i : midiDeviceInfo) {
            //Find piano app
            if (i.toString().equals(("PianoApp"))) {
                //Try to get the receiver
                try {
                    midiDevice = MidiSystem.getMidiDevice(i);
                    midiDevice.open();
                    this.midiReceiver = midiDevice.getReceiver();
                } catch (final MidiUnavailableException ignored) {
                }
            }
        }
        if (midiReceiver == null) {
            System.err.println("Could not get Midi Receiver");
        }
    }
}
