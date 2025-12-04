package audio;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;

public class SoundManager {
    private static final Preferences prefs = Preferences.userRoot().node("Echec_Ptut");
    private static float volume = prefs.getFloat("volume", 1.0f);
    private static boolean muted = prefs.getBoolean("muted", false);

    public static synchronized void setVolume(float v) {
        if (v < 0f) v = 0f;
        if (v > 1f) v = 1f;
        volume = v;
        prefs.putFloat("volume", volume);
    }

    public static synchronized float getVolume() {
        return volume;
    }

    public static synchronized void setMuted(boolean m) {
        muted = m;
        prefs.putBoolean("muted", muted);
    }

    public static synchronized boolean isMuted() {
        return muted;
    }

    public static void playSound(String soundFilePath) {
        try {
            File soundFile = new File(soundFilePath);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);

            // Apply volume/mute if supported
            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                if (muted || volume <= 0f) {
                    gainControl.setValue(gainControl.getMinimum());
                } else {
                    // convert linear volume (0.0-1.0) to decibels
                    float dB = (float) (20.0 * Math.log10(Math.max(volume, 0.0001))); // avoid log(0)
                    dB = Math.max(gainControl.getMinimum(), Math.min(dB, gainControl.getMaximum()));
                    gainControl.setValue(dB);
                }
            } else if (clip.isControlSupported(FloatControl.Type.VOLUME)) {
                FloatControl vol = (FloatControl) clip.getControl(FloatControl.Type.VOLUME);
                if (muted) vol.setValue(0f);
                else vol.setValue(volume);
            }

            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
}