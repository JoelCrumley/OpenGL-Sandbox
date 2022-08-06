package joel.opengl.network;

import java.util.Optional;

public class TestPacket extends Packet<TestPacketHandler> {

    public static final EnumPacket id = EnumPacket.TEST;

    private int i;
    private long l;
    private byte b;
    private double d;
    private float f;
    private boolean bool;
    private String s;
    private Optional<String> stringA, stringB;
    private int ushort;

    public TestPacket(int i, long l, byte b, double d, float f, boolean bool, String s, Optional<String> stringA, Optional<String> stringB, int ushort) {
        super(id);
        this.i = i;
        this.l = l;
        this.b = b;
        this.d = d;
        this.f = f;
        this.bool = bool;
        this.s = s;
        this.stringA = stringA;
        this.stringB = stringB;
        this.ushort = ushort;
    }

    public TestPacket(PacketDataSerializer data) {
        super(id);
        this.i = data.readInt();
        this.l = data.readLong();
        this.b = data.readByte();
        this.d = data.readDouble();
        this.f = data.readFloat();
        this.bool = data.readBoolean();
        this.s = data.readUTF();
        this.stringA = data.readOptional(PacketDataSerializer::readUTF);
        this.stringB = data.readOptional(PacketDataSerializer::readUTF);
        this.ushort = data.readUnsignedShort();
    }

    @Override
    public void writeData(PacketDataSerializer data) {
        data.writeInt(i);
        data.writeLong(l);
        data.writeByte(b);
        data.writeDouble(d);
        data.writeFloat(f);
        data.writeBoolean(bool);
        data.writeUTF(s);
        data.writeOptional(stringA, PacketDataSerializer::writeUTF);
        data.writeOptional(stringB, PacketDataSerializer::writeUTF);
        data.writeUnsignedShort(ushort);
    }

    @Override
    public void handle(TestPacketHandler testPacketHandler) {
        System.out.println("Test Packet");
        System.out.println("i:" + i);
        System.out.println("l:" + l);
        System.out.println("b:" + b);
        System.out.println("d:" + d);
        System.out.println("f:" + f);
        System.out.println("bool:" + bool);
        System.out.println("s:" + s);
        System.out.println("stringA:" + stringA.toString());
        System.out.println("stringB:" + stringB.toString());
        System.out.println("ushort:" + ushort);
    }

}
