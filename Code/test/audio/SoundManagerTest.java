package audio;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class SoundManagerTest {

    private float oldVolume;
    private boolean oldMuted;

    @Before
    public void setUp() {
        // Snapshot current values to restore later (preferences-backed)
        oldVolume = SoundManager.getVolume();
        oldMuted = SoundManager.isMuted();
    }

    @After
    public void tearDown() {
        // Restore previous state
        SoundManager.setVolume(oldVolume);
        SoundManager.setMuted(oldMuted);
    }

    @Test
    public void testVolumeClampLow() {
        SoundManager.setVolume(-1.0f);
        assertEquals(0.0f, SoundManager.getVolume(), 0.0001f);
    }

    @Test
    public void testVolumeClampHigh() {
        SoundManager.setVolume(2.0f);
        assertEquals(1.0f, SoundManager.getVolume(), 0.0001f);
    }

    @Test
    public void testMuteToggle() {
        SoundManager.setMuted(true);
        assertTrue(SoundManager.isMuted());
        SoundManager.setMuted(false);
        assertFalse(SoundManager.isMuted());
    }
}
