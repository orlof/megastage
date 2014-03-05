package org.megastage.client;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import java.util.HashMap;

public class SoundManager {
    public static final int GYROSCOPE = 0;
    public static final int KEYBOARD = 1;
    public static final int RETRO_COMPUTER = 2;
    public static final int SPACE_ENGINE = 3;
    public static final int FANFARE = 4;
    public static final int EXPLOSION = 5;
    public static final int LASER_BEAM = 6;
    public static final int FORCE_FIELD = 7;
    public static final int EXPLOSION_3 = 8;
    
    private static AudioData[] audioData;
    
    public static void init(AssetManager am) {
        assetManager = am;
        
        audioData = new AudioData[] {
            new AudioData("gyro-machine.ogg", false),
            new AudioData("keyboard.ogg", false),
            new AudioData("retro-computer.ogg", false),
            new AudioData("space-engine.ogg", false),
            new AudioData("fanfare.ogg", true),
            new AudioData("explosion.ogg", false),
            new AudioData("syncopated-laser.wav", false),
            null,
            new AudioData("explosion3.wav", false)
        };
    }
    
    public static AudioNode get(int index) {
        return audioData[index].node;
    }
    
    private static AssetManager assetManager;
    
    private static class AudioData {
        public String filename;
        public boolean streaming;
        public AudioNode node;

        public AudioData(String filename, boolean streaming) {
            this.filename = filename;
            this.streaming = streaming;
            node = new AudioNode(assetManager, "Sounds/" + filename, streaming);
        }
        
        
    }
}
