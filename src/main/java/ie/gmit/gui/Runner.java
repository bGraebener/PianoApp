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
import java.util.Arrays;
import java.util.Scanner;


public class Runner {
    private static int oldNote = 0;
    //The distance to detect the fingers between. Its is -DETECT <> DETECT
    private static final int DETECT = 150;
    //The number of keys on the piano
    private static final int KEYS = 12;

    public static void main(final String[] args) throws Exception {
        final MidiDevice.Info[] midiDeviceInfo = MidiSystem.getMidiDeviceInfo();
        System.out.println(Arrays.toString(midiDeviceInfo));

        final MidiDevice.Info pianoAppMidiDriver = Arrays.stream(midiDeviceInfo)
                .filter(mdi -> mdi.toString().equals("PianoApp")).skip(1).findFirst().orElseThrow(RuntimeException::new);

        final MidiDevice midiDevice = MidiSystem.getMidiDevice(pianoAppMidiDriver);
        midiDevice.open();
        final Receiver receiver = midiDevice.getReceiver();

        final Scanner scanner = new Scanner(System.in);

        String note;
        do {
            note = scanner.nextLine();

            switch (note) {
                case "a":
                    Runner.sendMessage(receiver, 69);
                    break;
                case "c":
                    Runner.sendMessage(receiver, 60);
                    break;
                case "d":
                    Runner.sendMessage(receiver, 62);
                    break;
                default:
                    Runner.sendMessage(receiver, 64);
            }
        } while (!note.equals("q"));

        if (NativeLibrary.loadSystem("native")) {
            final Controller c = new Controller();

            final LeapMotionListener l = new LeapMotionListener();
            c.addListener(l);
            //Register to finger movement
            l.onFingerMove((hands) -> {
                //System.out.println("Right middle finger is at: " + hands.get('R').get(3).getX());
            });
            //Register to key tap
            l.onKeyTap((pos) -> {
                System.out.println("Tapped at: " + pos.getX() + "->" + Runner.whichKey(pos.getX()));
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

    /**
     * Determines which key has been pressed.
     * X coordinate is generally between -150 and 150
     * Returns -1 if a key was not pressed.
     *
     * @param x
     * @return
     */
    private static int whichKey(final float x) {
        final int keyWidth = Runner.DETECT * 2 / Runner.KEYS;
        int baseX = -Runner.DETECT;
        int i = 1;
        //Check if within bounds
        if (-Runner.DETECT <= x && x <= Runner.DETECT) {
            //Loop from left to right
            while (baseX <= Runner.DETECT) {
                if ((x <= baseX && x >= baseX + keyWidth) || (x >= baseX && x <= baseX + keyWidth)) {
                    return i;
                }
                baseX += keyWidth;
                i++;
            }
        }
        return -1;
    }


    private static void sendMessage(final Receiver receiver, final int note) throws InvalidMidiDataException {
        final ShortMessage myMsg = new ShortMessage();
        final long timeStamp = -1;

        //stop old note from playing
        myMsg.setMessage(ShortMessage.NOTE_OFF, 0, Runner.oldNote, 0);
        receiver.send(myMsg, timeStamp);
        Runner.oldNote = note;

        // Start playing the note Middle C (60),
        // moderately loud (velocity = 93).
        myMsg.setMessage(ShortMessage.NOTE_ON, 0, note, 93);
        receiver.send(myMsg, timeStamp);
    }
}
