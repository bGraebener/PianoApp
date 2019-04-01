package ie.gmit.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class NativeLibraryLoader {
    /**
     * Loads the content of the native directory in the resource path.
     *
     * @return boolean
     */
    public static boolean loadNativeFiles() {
        final String dir = "/native";
        final String osArch = System.getProperty("sun.arch.data.model");
        final List<String> fileNames = new ArrayList<>(9);
        fileNames.add("Leap.dll");
        fileNames.add("LeapC.dll");
        fileNames.add("LeapJava.dll");
        fileNames.add("msvcp120.dll");
        fileNames.add("msvcr120.dll");
        final AtomicBoolean loaded = new AtomicBoolean(true);
        fileNames.forEach((name) -> {
            try {
                NativeUtils.loadLibraryFromJar(dir + "/win/" + osArch + "/" + name);
            } catch (final IOException e) {
                loaded.set(false);
            }
        });
        return loaded.get();
    }
}
