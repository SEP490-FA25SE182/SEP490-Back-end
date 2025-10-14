package com.sep.aiservice.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PcmToWavConverter {

    public static byte[] toWavMono16LE(byte[] pcm, int sampleRate) throws IOException {
        int channels = 1;
        int bitsPerSample = 16;
        int byteRate = sampleRate * channels * bitsPerSample / 8;
        int blockAlign = channels * bitsPerSample / 8;
        int dataSize = pcm.length;
        int chunkSize = 36 + dataSize;

        ByteArrayOutputStream out = new ByteArrayOutputStream(44 + dataSize);
        // RIFF
        out.write(new byte[]{'R','I','F','F'});
        out.write(intLE(chunkSize));
        out.write(new byte[]{'W','A','V','E'});
        // fmt
        out.write(new byte[]{'f','m','t',' '});
        out.write(intLE(16));
        out.write(shortLE((short)1));
        out.write(shortLE((short)channels));
        out.write(intLE(sampleRate));
        out.write(intLE(byteRate));
        out.write(shortLE((short)blockAlign));
        out.write(shortLE((short)bitsPerSample));
        // data
        out.write(new byte[]{'d','a','t','a'});
        out.write(intLE(dataSize));
        out.write(pcm);
        return out.toByteArray();
    }

    private static byte[] intLE(int v){ return new byte[]{(byte)v,(byte)(v>>8),(byte)(v>>16),(byte)(v>>24)}; }
    private static byte[] shortLE(short v){ return new byte[]{(byte)v,(byte)(v>>8)}; }
}
