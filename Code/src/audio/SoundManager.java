package audio;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;

/**
 * Gestionnaire de sons de l'application.
 * <p>
 * Cette classe fournit des méthodes statiques pour gérer le volume, l'état muet
 * et la lecture de sons via l'API Java Sound. Les préférences utilisateur sont
 * persistées via {@link java.util.prefs.Preferences} sous le nœud « Echec_Ptut ».
 * </p>
 */
public class SoundManager {
    /** Préférences systèmes utilisées pour persister le volume et l'état muet. */
    private static final Preferences prefs = Preferences.userRoot().node("Echec_Ptut");
    /** Volume linéaire compris entre 0.0 (silence) et 1.0 (maximum). */
    private static float volume = prefs.getFloat("volume", 1.0f);
    /** Indique si le son est coupé. */
    private static boolean muted = prefs.getBoolean("muted", false);

    /**
     * Définit le volume global.
     * <p>
     * La valeur fournie est automatiquement bornée dans l'intervalle [0.0, 1.0].
     * La valeur est également sauvegardée dans les préférences.
     * </p>
     * @param v volume souhaité (linéaire 0.0 à 1.0)
     */
    public static synchronized void setVolume(float v) {
        if (v < 0f) v = 0f;
        if (v > 1f) v = 1f;
        volume = v;
        prefs.putFloat("volume", volume);
    }

    /**
     * Retourne le volume actuel (linéaire 0.0 à 1.0).
     * @return le volume courant
     */
    public static synchronized float getVolume() {
        return volume;
    }

    /**
     * Active ou désactive l'état muet.
     * <p>
     * La valeur est sauvegardée dans les préférences.
     * </p>
     * @param m true pour couper le son, false pour l'activer
     */
    public static synchronized void setMuted(boolean m) {
        muted = m;
        prefs.putBoolean("muted", muted);
    }

    /**
     * Indique si le son est actuellement coupé.
     * @return true si muet, false sinon
     */
    public static synchronized boolean isMuted() {
        return muted;
    }

    /**
     * Lit un fichier son au chemin spécifié.
     * <p>
     * Si le périphérique audio prend en charge un contrôle de gain (MASTER_GAIN),
     * le volume est converti en décibels et appliqué. Sinon, si un contrôle VOLUME
     * est disponible, une valeur linéaire est utilisée. Lorsque l'état muet est actif
     * (ou volume nul), le gain minimum ou 0 est appliqué selon le contrôle disponible.
     * </p>
     * @param soundFilePath chemin du fichier son à lire (format supporté par Java Sound)
     */
    public static void playSound(String soundFilePath) {
        try {
            File soundFile = new File(soundFilePath);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);

            // Application du volume/mute si supporté
            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                if (muted || volume <= 0f) {
                    gainControl.setValue(gainControl.getMinimum());
                } else {
                    // conversion volume linéaire (0.0-1.0) -> décibels
                    float dB = (float) (20.0 * Math.log10(Math.max(volume, 0.0001))); // éviter log(0)
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