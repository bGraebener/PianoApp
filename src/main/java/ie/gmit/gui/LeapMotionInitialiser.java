package ie.gmit.gui;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Vector;

import javax.sound.midi.InvalidMidiDataException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class LeapMotionInitialiser {
    private final Controller c;
    private final LeapMotionListener l;
    //The distance to detect the fingers between. Its is -detectRange <> detectRange
    private int detectRange = 175;
    //The number of keys on the piano
    private int numberOfKeys = 12;

    public LeapMotionInitialiser() throws IOException {
        //Load leap motion native files
        if (NativeLibraryLoader.loadNativeFiles()) {
            //if (NativeLibrary.loadSystem("native")) {
            this.c = new Controller();
            this.l = new LeapMotionListener();
            this.c.addListener(this.l);
            this.c.removeListener(this.l);
        } else {
            throw new IOException("Leap Native is not loaded");
        }
    }


    public LeapMotionInitialiser(final int detectRange, final int numberOfKeys) throws IOException {
        this();
        this.detectRange = detectRange;
        this.numberOfKeys = numberOfKeys;

    }

    public void onKeyTap(final Consumer<? extends Vector> c) {
        this.l.onKeyTap(c);
    }

    public void onFingerMove(final Consumer<? extends Map<Character, List<Vector>>> c) {
        this.l.onFingerMove(c);
    }

    /**
     * Determines which key has been pressed.
     * X coordinate is generally between -150 and 150
     * Returns -1 if a key was not pressed.
     *
     * @param x
     * @return
     */
    public int whichKey(final float x) {
        final int keyWidth = this.detectRange * 2 / this.numberOfKeys;
        int baseX = -this.detectRange;
        int i = 1;
        //Check if within bounds
        if (-this.detectRange <= x && x <= this.detectRange) {
            //Loop from left to right
            while (baseX <= this.detectRange) {
                if ((x <= baseX && x >= baseX + keyWidth) || (x >= baseX && x <= baseX + keyWidth)) {
                    return i;
                }
                baseX += keyWidth;
                i++;
            }
        }
        return -1;
    }

    public int getDetectRange() {
        return this.detectRange;
    }

    public void setDetectRange(final int detectRange) {
        this.detectRange = detectRange;
    }

    public int getNumberOfKeys() {
        return this.numberOfKeys;
    }

    public void setNumberOfKeys(final int numberOfKeys) {
        this.numberOfKeys = numberOfKeys;
    }
}
