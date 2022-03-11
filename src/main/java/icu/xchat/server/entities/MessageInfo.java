package icu.xchat.server.entities;

import icu.xchat.server.utils.BsonUtils;
import org.bson.BSONObject;
import org.bson.BasicBSONObject;

/**
 * 消息实体
 *
 * @author shouchen
 */
public class MessageInfo implements Serialization {
    /**
     * 索引id
     */
    private Integer id;
    /**
     * 房间id
     */
    private Integer rid;
    /**
     * 发送者识别码
     */
    private String sender;
    /**
     * 消息类型
     */
    private Integer type;
    /**
     * 内容
     */
    private String content;
    /**
     * 签名
     */
    private String signature;
    /**
     * 时间戳
     */
    private Long timeStamp;

    public Integer getId() {
        return id;
    }

    public MessageInfo setId(Integer id) {
        this.id = id;
        return this;
    }

    public Integer getRid() {
        return rid;
    }

    public MessageInfo setRid(Integer rid) {
        this.rid = rid;
        return this;
    }

    public String getSender() {
        return sender;
    }

    public MessageInfo setSender(String sender) {
        this.sender = sender;
        return this;
    }

    public Integer getType() {
        return type;
    }

    public MessageInfo setType(Integer type) {
        this.type = type;
        return this;
    }

    public String getContent() {
        return content;
    }

    public MessageInfo setContent(String content) {
        this.content = content;
        return this;
    }

    public String getSignature() {
        return signature;
    }

    public MessageInfo setSignature(String signature) {
        this.signature = signature;
        return this;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public MessageInfo setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
        return this;
    }

    /**
     * 对象序列化
     *
     * @return 数据
     */
    @Override
    public byte[] serialize() {
        BSONObject object = new BasicBSONObject();
        object.put("RID", rid);
        object.put("SENDER", sender);
        object.put("TYPE", type);
        object.put("CONTENT", content);
        object.put("SIGNATURE", signature);
        object.put("TIMESTAMP", timeStamp);
        return BsonUtils.encode(object);
    }

    /**
     * 反序列化为对象
     *
     * @param data 数据
     */
    @Override
    public void deserialize(byte[] data) {
        BSONObject object = BsonUtils.decode(data);
        this.rid = (Integer) object.get("RID");
        this.sender = (String) object.get("SENDER");
        this.type = (Integer) object.get("TYPE");
        this.content = (String) object.get("CONTENT");
        this.signature = (String) object.get("SIGNATURE");
        this.timeStamp = (Long) object.get("TIMESTAMP");
    }

    @Override
    public String toString() {
        return "MessageInfo{" +
                "id=" + id +
                ", rid=" + rid +
                ", sender='" + sender + '\'' +
                ", type=" + type +
                ", content='" + content + '\'' +
                ", signature='" + signature + '\'' +
                ", timeStamp=" + timeStamp +
                '}';
    }
}
