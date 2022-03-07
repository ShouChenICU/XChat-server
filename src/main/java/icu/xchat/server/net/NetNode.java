package icu.xchat.server.net;

import icu.xchat.server.exceptions.PacketException;
import icu.xchat.server.utils.PackageUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.TimeoutException;

/**
 * 网络节点
 *
 * @author shouchen
 */
public abstract class NetNode {
    private SelectionKey selectionKey;
    private final SocketChannel channel;
    private final ByteBuffer readBuffer;
    private final ByteBuffer writeBuffer;
    private final PackageUtils packageUtils;
    private long heartTime;
    private int packetStatus;
    private int packetLength;
    private byte[] packetData;

    public NetNode(SocketChannel channel) throws ClosedChannelException {
        this.channel = channel;
        this.readBuffer = ByteBuffer.allocateDirect(512);
        this.writeBuffer = ByteBuffer.allocateDirect(512);
        this.packageUtils = new PackageUtils();
        this.heartTime = System.currentTimeMillis();
        this.packetStatus = 0;
        this.packetLength = 0;
        this.packetData = null;
        this.selectionKey = NetCore.register(channel, SelectionKey.OP_READ, this);
    }

    public SelectionKey getSelectionKey() {
        return selectionKey;
    }

    public SocketChannel getChannel() {
        return channel;
    }

    public PackageUtils getPackageUtils() {
        return packageUtils;
    }

    abstract void handlePacket(PacketBody packetBody) throws Exception;

    /**
     * 读取并预处理数据
     */
    public void doRead() throws Exception {
        int len;
        while ((len = channel.read(readBuffer)) != 0) {
            if (len == -1) {
                throw new IOException("通道关闭");
            }
            readBuffer.flip();
            while (readBuffer.hasRemaining()) {
                switch (packetStatus) {
                    case 0:
                        packetLength = readBuffer.get() & 0xff;
                        packetStatus = 1;
                        break;
                    case 1:
                        packetLength += (readBuffer.get() & 0xff) << 8;
                        packetData = new byte[packetLength];
                        packetLength = 0;
                        packetStatus = 2;
                        break;
                    case 2:
                        for (; readBuffer.hasRemaining() && packetLength < packetData.length; packetLength++) {
                            packetData[packetLength] = readBuffer.get();
                        }
                        if (packetLength == packetData.length) {
                            this.heartTime = System.currentTimeMillis();
                            handlePacket(packageUtils.decodePacket(packetData));
                            packetStatus = 0;
                        }
                        break;
                }
            }
            readBuffer.clear();
        }
        selectionKey = NetCore.register(channel, SelectionKey.OP_READ, this);
    }

    /**
     * 发送一个包
     *
     * @param packetBody 包
     */
    @SuppressWarnings("BusyWait")
    public void postPacket(PacketBody packetBody) throws Exception {
        synchronized (channel) {
            byte[] dat = packageUtils.encodePacket(packetBody);
            int length = dat.length;
            if (length > 65535) {
                throw new PacketException("包长度超限: " + length);
            }
            writeBuffer.put((byte) (length % 256))
                    .put((byte) (length / 256));
            int offset = 0;
            while (offset < dat.length) {
                if (writeBuffer.hasRemaining()) {
                    length = Math.min(writeBuffer.remaining(), dat.length - offset);
                    writeBuffer.put(dat, offset, length);
                    offset += length;
                }
                writeBuffer.flip();
                int waitCount = 0;
                while (writeBuffer.hasRemaining()) {
                    if (channel.write(writeBuffer) == 0) {
                        if (waitCount >= 10) {
                            throw new TimeoutException("写入超时");
                        }
                        Thread.sleep(100);
                        waitCount++;
                    } else {
                        waitCount = 0;
                    }
                }
                writeBuffer.clear();
            }
        }
        this.heartTime = System.currentTimeMillis();
    }

    public long getHeartTime() {
        return heartTime;
    }
}
