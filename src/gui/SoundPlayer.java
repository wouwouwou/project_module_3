package gui;

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import java.io.InputStream;

/**
 * Play a sound from the GUI.
 * @author Tristan de Boer
 * @since 13-4-15
 */
public class SoundPlayer {
    /**
     * Plays a sound (in this case pingu.wav).
     * @param b
     */
    public static synchronized void playSound(final boolean b) {
        new Thread(new Runnable() {
            // The wrapper thread is unnecessary, unless it blocks on the
            // Clip finishing; see comments.
            public void run() {
                try
                {
                    // get the sound file as a resource out of my jar file;
                    // the sound file must be in the same directory as this class file.
                    // the input stream portion of this recipe comes from a javaworld.com article.
                    InputStream inputStream;
                    if(b){
                        inputStream = getClass().getResourceAsStream("/gui/sources/pinguplay.wav");
                    }else {
                        inputStream = getClass().getResourceAsStream("/gui/sources/pingu.wav");
                    }
                    AudioStream audioStream = new AudioStream(inputStream);
                    AudioPlayer.player.start(audioStream);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Play a sound (test)
     * @param args None
     */
    public static void main(String[] args){
        playSound(false);
    }
}
