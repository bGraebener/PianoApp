package ie.gmit.gui;

import javax.sound.midi.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MidiController {

    private Receiver midiReceiver;
    private int oldNote;

    public MidiController() {

        this.init();

    }

    void sendMessage(final int note) throws InvalidMidiDataException {
        final ShortMessage myMsg = new ShortMessage();
        final long timeStamp = -1;

        final ExecutorService service = Executors.newFixedThreadPool(1);
        service.submit(() -> {

            try {
                Thread.sleep(1000);
                //stop old note from playing
                myMsg.setMessage(ShortMessage.NOTE_OFF, 0, this.oldNote, 0);
                this.midiReceiver.send(myMsg, timeStamp);
                this.oldNote = note;
            } catch (final InterruptedException | InvalidMidiDataException e) {
                e.printStackTrace();
            }

        });

        // Start playing the note Middle C (60),
        // moderately loud (velocity = 93).
        myMsg.setMessage(ShortMessage.NOTE_ON, 0, note, 93);
        this.midiReceiver.send(myMsg, timeStamp);
    }


    private void init() {

        final MidiDevice.Info[] midiDeviceInfo = MidiSystem.getMidiDeviceInfo();
        MidiDevice midiDevice;
        this.midiReceiver = null;
        //Loop each rdevice info
        for (final MidiDevice.Info i : midiDeviceInfo) {
            //Find piano app
            if (i.toString().equals(("PianoApp"))) {
                //Try to get the receiver
                try {
                    midiDevice = MidiSystem.getMidiDevice(i);
                    midiDevice.open();
                    this.midiReceiver = midiDevice.getReceiver();
                    break;
                } catch (final Exception e) {

                }
            }
        }

    }
}
