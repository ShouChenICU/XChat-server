package icu.xchat.server.net;

import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.TimeoutException;

/**
 * 抽象网络IO类
 *
 * @author shouchen
 */
public abstract class AbstractNetIO {
    private static final int TIME_OUT_LENGTH = 100;
    private static final int TIME_OUT_COUNT = 10;
    private static final int BUFFER_SIZE = 4096;
    protected SelectionKey key;
    protected ByteBuffer readBuffer;
    protected ByteBuffer writeBuffer;
    private int packetStatus;
    private int packetLength;
    private byte[] packetData;

    public AbstractNetIO() {
    }

    public AbstractNetIO(SelectionKey key) {
        this.key = key;
        this.readBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
        this.writeBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
    }

    public void update(AbstractNetIO abstractNetIO) {
        this.key = abstractNetIO.key;
        this.readBuffer = abstractNetIO.readBuffer;
        this.writeBuffer = abstractNetIO.writeBuffer;
    }

    /**
     * 写操作
     */
    @SuppressWarnings("BusyWait")
    void doWrite(byte[] data) throws Exception {
        int length = data.length;
        SocketChannel channel = (SocketChannel) key.channel();
        synchronized (this) {
            writeBuffer.clear();
            writeBuffer.put((byte) (length % 256))
                    .put((byte) (length / 256));
            int offset = 0;
            while (offset < data.length) {
                length = Math.min(writeBuffer.remaining(), data.length - offset);
                writeBuffer.put(data, offset, length);
                offset += length;
                writeBuffer.flip();
                int waitCount = 0;
                while (writeBuffer.hasRemaining()) {
                    if (channel.write(writeBuffer) == 0) {
                        if (waitCount >= TIME_OUT_COUNT) {
                            throw new TimeoutException("write time out!");
                        }
                        Thread.sleep(TIME_OUT_LENGTH);
                        waitCount++;
                    } else {
                        waitCount = 0;
                    }
                }
                writeBuffer.clear();
            }
        }
    }

    /**
     * 断开连接
     */
    public void disconnect() throws Exception {
        key.cancel();
        key.selector().wakeup();
        key.channel().close();
    }

    /**
     * 是否连接
     */
    public boolean isConnect() {
        return key != null && key.isValid();
    }

    /**
     * 读操作
     */
    public void doRead() throws Exception {
        int len;
        SocketChannel channel = (SocketChannel) key.channel();
        while (true) {
            if (!channel.isConnected()) {
                return;
            }
            len = channel.read(readBuffer);
            if (len == 0) {
                break;
            }
            if (len == -1) {
                throw new ClosedChannelException();
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
                            dataHandler(packetData);
                            packetStatus = 0;
                        }
                        break;
                }
            }
            readBuffer.clear();
        }
        key.interestOps(SelectionKey.OP_READ);
        key.selector().wakeup();
    }

    /**
     * 数据处理
     *
     * @param data 数据
     */
    protected abstract void dataHandler(byte[] data) throws Exception;

    /**
     * 异常处理
     */
    protected abstract void exceptionHandler(Exception exception);
}
