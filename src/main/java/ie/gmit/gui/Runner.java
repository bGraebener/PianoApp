package ie.gmit.gui;

import com.leapmotion.leap.*;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Image;
import com.leapmotion.leap.Vector;

import javax.sound.midi.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;


public class Runner {
    public static void main(final String[] args) throws Exception {
        /*System.out.println(Arrays.toString(MidiSystem.getMidiDeviceInfo()));

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

        ie.gmit.gui.Runner.sendMessage(receiver);
*/

        if (NativeLibrary.loadSystem("native")) {
            final Controller c = new Controller();
            final LeapMotionListener l = new LeapMotionListener();
            c.addListener(l);

            l.addKeyTapListener((pos) -> {
                System.out.println("External listener is called");
            });
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
