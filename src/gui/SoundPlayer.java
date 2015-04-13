package gui;

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import java.io.InputStream;

/**
 * @author Tristan de Boer
 * @since 13-4-15
 */
public class SoundPlayer {
    public static synchronized void playSound() {
        new Thread(new Runnable() {
            // The wrapper thread is unnecessary, unless it blocks on the
            // Clip finishing; see comments.
            public void run() {
                try
                {
                    // get the sound file as a resource out of my jar file;
                    // the sound file must be in the same directory as this class file.
                    // the input stream portion of this recipe comes from a javaworld.com article.
                    InputStream inputStream = getClass().getResourceAsStream("/gui/sources/pingu.wav");
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

    public static void main(String[] args){
        playSound();
    }
}
