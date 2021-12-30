package icu.xchat.server.net;

/**
 * 传输任务抽象类
 *
 * @author shouchen
 */
public abstract class Task {
    private int id;
    private int packetCount;
    private int packetSum;

    public abstract void getPacket();
}
