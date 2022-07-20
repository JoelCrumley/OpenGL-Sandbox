package joel.opengl.test;

import org.lwjgl.openal.*;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_decode_filename;
import static org.lwjgl.system.libc.LibCStdlib.free;

public class AudioTesting {

    // https://github.com/LWJGL/lwjgl3-wiki/wiki/2.1.-OpenAL

    private final String audioFolder = "/res/audio/";

    private long device, context;
    private ALCCapabilities alcCapabilities;
    private ALCapabilities alCapabilities;

    private void init() {
        String defaultDeviceName = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
        System.out.println("Found default device name: " + defaultDeviceName);
        device = alcOpenDevice(defaultDeviceName);

        int[] attributes = {0};
        context = alcCreateContext(device, attributes);
        alcMakeContextCurrent(context);

        alcCapabilities = ALC.createCapabilities(device);
        alCapabilities = AL.createCapabilities(alcCapabilities);
    }

    private int bufferPointer, sourcePointer;

    private void loadSounds() {
//
//        Figure out how the fuck to load audio data into a buffer with correct format and sample rate.
//
//        //Request space for the buffer
//        bufferPointer = alGenBuffers();
//
//        //Send the data to OpenAL
//        alBufferData(bufferPointer, format, rawAudioBuffer, sampleRate);
//
//        //Free the memory allocated by STB
//        free(rawAudioBuffer);
//
//        //Request a source
//        sourcePointer = alGenSources();
//
//        //Assign our buffer to the source
//        alSourcei(sourcePointer, AL_BUFFER, bufferPointer);
    }

    public void playSound() {
        alSourcePlay(sourcePointer);
    }

    private void close() {

        // Clears sound data from memory.
        // Buffer can only be deleted after its linked sources have been deleted.
        // One buffer can be linked to many sources.
        alDeleteSources(sourcePointer);
        alDeleteBuffers(bufferPointer);

        alcDestroyContext(context);
        alcCloseDevice(device);
    }

    private void checkErrors(String print) {
        System.out.println(print);
        int error = alGetError();
        if(error != AL_NO_ERROR) {
            System.out.println("ERROR: " + error);
        }
    }

    private AudioTesting() {
    }

    public static void main(String[] args) {

//        AudioTesting inst = new AudioTesting();
//
//        inst.init();
////        inst.checkErrors();
//        inst.loadSounds();
////        inst.checkErrors();
//
//        try {
//            Thread.sleep(2000L);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        System.out.println("Starting sounds");
//        for (int i = 0; i < 3; i++); {
//            inst.playSound();
//            inst.checkErrors("looperr");
//            try {
//                Thread.sleep(5000L);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//                inst.close();
//                return;
//            }
//        }
//
//        inst.checkErrors("lasterr");
//        inst.close();

    }

}
