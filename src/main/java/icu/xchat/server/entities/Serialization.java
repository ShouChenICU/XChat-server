package icu.xchat.server.entities;

/**
 * 序列化接口
 *
 * @author shouchen
 */
public interface Serialization {
    /**
     * 对象序列化
     *
     * @return 数据
     */
    byte[] serialize();

    /**
     * 反序列化为对象
     *
     * @param data 数据
     */
    void deserialize(byte[] data);
}
