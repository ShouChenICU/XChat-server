package icu.xchat.server.net;

/**
 * 网络传输内容帧
 *
 * @author shouchen
 */
public class PacketBody {
    private int taskId;
    private int id;
    private int type;
    private byte[] data;

    public PacketBody() {
    }

    public PacketBody(int id, int type, byte[] data) {
        this.id = id;
        this.type = type;
        this.data = data;
    }

    public int getTaskId() {
        return taskId;
    }

    public PacketBody setTaskId(int taskId) {
        this.taskId = taskId;
        return this;
    }

    public int getId() {
        return id;
    }

    public PacketBody setId(int id) {
        this.id = id;
        return this;
    }

    public int getType() {
        return type;
    }

    public PacketBody setType(int type) {
        this.type = type;
        return this;
    }

    public byte[] getData() {
        return data;
    }

    public PacketBody setData(byte[] data) {
        this.data = data;
        return this;
    }
}
