package icu.xchat.server.net;

import icu.xchat.server.entities.Serialization;
import icu.xchat.server.utils.BsonUtils;
import org.bson.BSONObject;
import org.bson.BasicBSONObject;

/**
 * 网络传输内容帧
 *
 * @author shouchen
 */
public class PacketBody implements Serialization {
    /**
     * 任务id
     */
    private int taskId;
    /**
     * 包id
     */
    private int id;
    /**
     * 任务类型
     */
    private int taskType;
    /**
     * 负载数据
     */
    private byte[] data;

    public PacketBody() {
        this.taskId = 0;
        this.id = 0;
        this.data = null;
    }

    public PacketBody(byte[] data) {
        this.deserialize(data);
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

    public int getTaskType() {
        return taskType;
    }

    public PacketBody setTaskType(int taskType) {
        this.taskType = taskType;
        return this;
    }

    public byte[] getData() {
        return data;
    }

    public PacketBody setData(byte[] data) {
        this.data = data;
        return this;
    }

    @Override
    public byte[] serialize() {
        BSONObject object = new BasicBSONObject();
        object.put("ID", id);
        object.put("TASK_ID", taskId);
        object.put("TASK_TYPE", taskType);
        object.put("DATA", data);
        return BsonUtils.encode(object);
    }

    @Override
    public void deserialize(byte[] data) {
        BSONObject object = BsonUtils.decode(data);
        this.taskId = (int) object.get("TASK_ID");
        this.id = (int) object.get("ID");
        this.taskType = (int) object.get("TASK_TYPE");
        this.data = (byte[]) object.get("DATA");
    }
}
