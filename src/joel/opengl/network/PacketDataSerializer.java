package joel.opengl.network;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class PacketDataSerializer {

    public static final int MAX_BUFFER_SIZE = 64000;

    private byte[] buffer;
    private int position = 0;

    public PacketDataSerializer(int bufferSize) {
        this.buffer = new byte[bufferSize];
    }

    public PacketDataSerializer() {
        this(MAX_BUFFER_SIZE);
    }

    public PacketDataSerializer(byte[] buffer) {
        this.buffer = buffer;
    }

    public byte[] getData() {
        return buffer;
    }

    /**
     * To be called when finished writing data to buffer. Removes unused space in byte buffer.
     * @return New buffer length.
     */
    public int trimBuffer() {
        byte[] buff = new byte[position];
        for (int i = 0; i < buff.length; i++) buff[i] = buffer[i];
        this.buffer = buff;
        this.position = 0;
        return buff.length;
    }

    public byte readByte() {
        return buffer[position++];
    }

    public byte[] readBytes(int length) {
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++) bytes[i] = buffer[position + i];
        position += length;
        return bytes;
    }

    public void writeByte(byte b) {
        buffer[position++] = b;
    }

    public void writeBytes(byte[] bytes) {
        for (int i = 0; i < bytes.length; i++) buffer[position + i] = bytes[i];
        position += bytes.length;
    }

    public boolean readBoolean() {
        return readByte() == 1;
    }

    public void writeBoolean(boolean b) {
        writeByte(b ? (byte) 1 : (byte) 0);
    }

//    /**
//     * It is better to batch your boolean into groups of 8 and interpret each batch as a byte.
//     */
//    @Deprecated
//    public void writeBooleans(boolean[] booleans) {
//        for (int i = 0; i < booleans.length; i++) buffer[position + i] = booleans[i] ? (byte) 1 : (byte) 0;
//        position += booleans.length;
//    }

    public int readUnsignedShort() {
        int i =
                ((buffer[position + 0] & 255) << 8) +
                ((buffer[position + 1] & 255) << 0);
        position += 2;
        return i;
    }

    public int[] readUnsignedShorts(int length) {
        return bytesToUnsignedShorts(readBytes(length * 2));
    }

    public void writeUnsignedShort(int s) {
        buffer[position] = (byte) (s >>> 8);
        buffer[position + 1] = (byte) (s >>> 0);
        position += 2;
    }

    public void writeUnsignedShorts(int[] shorts) {
        for (int i = 0; i < shorts.length; i++) {
            int s = shorts[i];
            buffer[position + 2*i] = (byte) (s >>> 8);
            buffer[position + 2*i + 1] = (byte) (s);
        }
        position += shorts.length * 2;
    }

    public int readInt() {
        int i =
                ((buffer[position] & 255) << 24) +
                ((buffer[position + 1] & 255) << 16) +
                ((buffer[position + 2] & 255) << 8) +
                ((buffer[position + 3] & 255) << 0);
        position += 4;
        return i;
    }

    public int[] readInts(int length) {
        return bytesToInts(readBytes(length * 4));
    }

    public void writeInt(int i) {
        buffer[position] = (byte) (i >>> 24);
        buffer[position + 1] = (byte) (i >>> 16);
        buffer[position + 2] = (byte) (i >>> 8);
        buffer[position + 3] = (byte) (i >>> 0);
        position += 4;
    }

    public void writeInts(int[] ints) {
        for (int i = 0; i < ints.length; i++) {
            int integer = ints[i];
            buffer[position + 4*i] = (byte) (integer >>> 24);
            buffer[position + 4*i + 1] = (byte) (integer >>> 16);
            buffer[position + 4*i + 2] = (byte) (integer >>> 8);
            buffer[position + 4*i + 3] = (byte) (integer);
        }
        position += ints.length * 4;
    }

    public float readFloat() {
        return Float.intBitsToFloat(readInt());
    }

    public float[] readFloats(int length) {
        return bytesToFloats(readBytes(length * 4));
    }

    public void writeFloat(float f) {
        writeInt(Float.floatToIntBits(f));
    }

    public void writeFloats(float[] floats) {
        int[] ints = new int[floats.length];
        for (int i = 0; i < ints.length; i++) ints[i] = Float.floatToIntBits(floats[i]);
        writeInts(ints);
    }

    public long readLong() {
        long l =
                ((long)(buffer[position + 0] & 255) << 56) +
                ((long)(buffer[position + 1] & 255) << 48) +
                ((long)(buffer[position + 2] & 255) << 40) +
                ((long)(buffer[position + 3] & 255) << 32) +
                ((long)(buffer[position + 4] & 255) << 24) +
                ((long)(buffer[position + 5] & 255) << 16) +
                ((long)(buffer[position + 6] & 255) <<  8) +
                ((long)(buffer[position + 7] & 255) <<  0);
        position += 8;
        return l;
    }

    public long[] readLongs(int length) {
        return bytesToLongs(readBytes(length * 8));
    }

    public void writeLong(long l) {
        buffer[position] = (byte) (l >>> 56);
        buffer[position + 1] = (byte) (l >>> 48);
        buffer[position + 2] = (byte) (l >>> 40);
        buffer[position + 3] = (byte) (l >>> 32);
        buffer[position + 4] = (byte) (l >>> 24);
        buffer[position + 5] = (byte) (l >>> 16);
        buffer[position + 6] = (byte) (l >>> 8);
        buffer[position + 7] = (byte) (l >>> 0);
        position += 8;
    }

    public void writeLongs(long[] longs) {
        for (int i = 0; i < longs.length; i++) {
            long l = longs[i];
            buffer[position + 4*i] = (byte) (l >>> 56);
            buffer[position + 4*i + 1] = (byte) (l >>> 48);
            buffer[position + 4*i + 2] = (byte) (l >>> 40);
            buffer[position + 4*i + 3] = (byte) (l >>> 32);
            buffer[position + 4*i + 4] = (byte) (l >>> 24);
            buffer[position + 4*i + 5] = (byte) (l >>> 16);
            buffer[position + 4*i + 6] = (byte) (l >>> 8);
            buffer[position + 4*i + 7] = (byte) (l);
        }
        position += longs.length * 8;
    }

    public double readDouble() {
        return Double.longBitsToDouble(readLong());
    }

    public double[] readDoubles(int length) {
        return bytesToDoubles(readBytes(length * 8));
    }

    public void writeDouble(double d) {
        writeLong(Double.doubleToLongBits(d));
    }

    public void writeDoubles(double[] doubles) {
        long[] longs = new long[doubles.length];
        for (int i = 0; i < longs.length; i++) longs[i] = Double.doubleToLongBits(doubles[i]);
        writeLongs(longs);
    }

    public String readUTF() {
        int length = readUnsignedShort();
        byte[] strData = readBytes(length);
        return new String(strData, StandardCharsets.UTF_8);
    }

    public void writeUTF(String string) {
        byte[] strData = string.getBytes(StandardCharsets.UTF_8);
        writeUnsignedShort(strData.length);
        writeBytes(strData);
    }

    public <T> void writeOptional(Optional<T> optional, PacketDataSerializer.b<T> packetdataserializer_b) {
        if (optional.isPresent()) {
            writeBoolean(true);
            packetdataserializer_b.accept(this, optional.get());
        } else {
            writeBoolean(false);
        }

    }

    public <T> Optional<T> readOptional(PacketDataSerializer.a<T> packetdataserializer_a) {
        return readBoolean() ? Optional.of(packetdataserializer_a.apply(this)) : Optional.empty();
    }

    @FunctionalInterface
    public interface a<T> extends Function<PacketDataSerializer, T> {

        default PacketDataSerializer.a<Optional<T>> asOptional() {
            return (packetdataserializer) -> {
                return packetdataserializer.readOptional(this);
            };
        }
    }

    @FunctionalInterface
    public interface b<T> extends BiConsumer<PacketDataSerializer, T> {

        default PacketDataSerializer.b<Optional<T>> asOptional() {
            return (packetdataserializer, optional) -> {
                packetdataserializer.writeOptional(optional, this);
            };
        }
    }

    public static int[] bytesToUnsignedShorts(byte[] data) {
        int[] shorts = new int[data.length / 2];
        for (int i = 0; i < shorts.length; i++) shorts[i] = (data[2*i] & 255) << 8 + (data[2*i + 1] & 255);
        return shorts;
    }

    public static int[] bytesToInts(byte[] data) {
        int[] ints = new int[data.length / 4];
        for (int i = 0; i < ints.length; i++) {
            ints[i] =
                    (data[4*i] & 255) << 24 +
                    (data[4*i + 1] & 255) << 16 +
                    (data[4*i + 2] & 255) << 8 +
                    (data[4*i + 3] & 255);
        }
        return ints;
    }

    public static float[] bytesToFloats(byte[] data) {
        float[] floats = new float[data.length / 4];
        for (int i = 0; i < floats.length; i++) {
            floats[i] = Float.intBitsToFloat(
                    (data[4*i + 0] & 255) << 24 +
                    (data[4*i + 1] & 255) << 16 +
                    (data[4*i + 2] & 255) <<  8 +
                    (data[4*i + 3] & 255));
        }
        return floats;
    }

    public static long[] bytesToLongs(byte[] data) {
        long[] longs = new long[data.length / 8];
        for (int i = 0; i < longs.length; i++) {
            longs[i] =
                    ((long) (data[8*i + 0] & 255)) << 56 +
                    ((long) (data[8*i + 1] & 255)) << 48 +
                    ((long) (data[8*i + 2] & 255)) << 40 +
                    ((long) (data[8*i + 3] & 255)) << 32 +
                    ((long) (data[8*i + 4] & 255)) << 24 +
                    ((long) (data[8*i + 5] & 255)) << 16 +
                    ((long) (data[8*i + 6] & 255)) << 8 +
                    ((long) (data[8*i + 7] & 255));
        }
        return longs;
    }

    public static double[] bytesToDoubles(byte[] data) {
        double[] doubles = new double[data.length / 8];
        for (int i = 0; i < doubles.length; i++) {
            doubles[i] = Double.longBitsToDouble(
                    ((long) (data[8*i + 0] & 255)) << 56 +
                    ((long) (data[8*i + 1] & 255)) << 48 +
                    ((long) (data[8*i + 2] & 255)) << 40 +
                    ((long) (data[8*i + 3] & 255)) << 32 +
                    ((long) (data[8*i + 4] & 255)) << 24 +
                    ((long) (data[8*i + 5] & 255)) << 16 +
                    ((long) (data[8*i + 6] & 255)) << 8 +
                    ((long) (data[8*i + 7] & 255))
            );
        }
        return doubles;
    }

}
