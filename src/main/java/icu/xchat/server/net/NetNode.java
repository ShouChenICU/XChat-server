package icu.xchat.server.net;

import icu.xchat.server.exceptions.PacketException;
import icu.xchat.server.utils.Encryptor;

/**
 * 网络节点
 *
 * @author shouchen
 */
public abstract class NetNode extends AbstractNetIO {
    private Encryptor encryptor;
    private long heartTime;

    public NetNode() {
        super();
        this.heartTime = System.currentTimeMillis();
    }

    public NetNode updateEncryptor(Encryptor encryptor) {
        this.encryptor = encryptor;
        return this;
    }

    @Override
    protected void dataHandler(byte[] data) throws Exception {
        packageHandler(new PacketBody(encryptor.decode(data)));
    }

    /**
     * 处理一个包
     *
     * @param packetBody 数据包
     */
    protected abstract void packageHandler(PacketBody packetBody) throws Exception;

    /**
     * 发送一个包
     *
     * @param packetBody 包
     */
    public boolean postPacket(PacketBody packetBody) {
        try {
            synchronized (this) {
                byte[] dat = encryptor.encode(packetBody.serialize());
                int length = dat.length;
                if (length > 65535) {
                    throw new PacketException("Packet Length Exceeded: " + length);
                }
                doWrite(dat);
            }
            this.heartTime = System.currentTimeMillis();
            return true;
        } catch (Exception e) {
            exceptionHandler(e);
            return false;
        }
    }

    public long getHeartTime() {
        return heartTime;
    }
}
