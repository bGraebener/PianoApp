import javax.sound.midi.*;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws MidiUnavailableException, InvalidMidiDataException {

        System.out.println(Arrays.toString(MidiSystem.getMidiDeviceInfo()));

        MidiDevice midiDevice = MidiSystem.getMidiDevice(MidiSystem.getMidiDeviceInfo()[6]);
        midiDevice.open();
        System.out.println(midiDevice.getDeviceInfo());
        Receiver receiver = midiDevice.getReceiver();

        Scanner scanner = new Scanner(System.in);

        String note;
        do {
            note = scanner.nextLine();

            switch (note) {
                case "a":

            }
        } while (!note.equals("q"));

        sendMessage(receiver);
    }

    private static void sendMessage(Receiver receiver) throws InvalidMidiDataException {
        ShortMessage myMsg = new ShortMessage();
        // Start playing the note Middle C (60),
        // moderately loud (velocity = 93).
        myMsg.setMessage(ShortMessage.NOTE_ON, 0, 60, 93);
        long timeStamp = -1;
        receiver.send(myMsg, timeStamp);
    }
}
