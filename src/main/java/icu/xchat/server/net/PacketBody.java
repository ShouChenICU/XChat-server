package icu.xchat.server.net;

import java.io.Serializable;

/**
 * 网络传输内容帧
 *
 * @author shouchen
 */
public class PacketBody implements Serializable {
    /**
     * 任务id
     */
    private int taskId;
    /**
     * 包id
     */
    private int id;
    /**
     * 负载类型
     */
    private int payloadType;
    /**
     * 负载数据
     */
    private byte[] data;

    public PacketBody() {
        this.taskId = 0;
        this.id = 0;
        this.data = null;
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

    public int getPayloadType() {
        return payloadType;
    }

    public PacketBody setPayloadType(int payloadType) {
        this.payloadType = payloadType;
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
