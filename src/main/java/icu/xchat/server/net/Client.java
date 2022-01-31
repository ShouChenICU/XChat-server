package icu.xchat.server.net;

import icu.xchat.server.entities.UserInfo;
import icu.xchat.server.net.tasks.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 网络客户端实体
 *
 * @author shouchen
 */
public class Client {
    private static final Logger LOGGER = LoggerFactory.getLogger(Client.class);
    private SelectionKey selectionKey;
    private final SocketChannel channel;
    private final ByteBuffer buffer;
    private final Map<Integer, Task> taskMap;
    private UserInfo userInfo;
    private int packetStatus;
    private int packetLength;
    private byte[] packetData;

    public Client(SocketChannel channel) throws IOException {
        this.channel = channel;
        this.buffer = ByteBuffer.allocateDirect(256);
        this.taskMap = new HashMap<>();
        this.userInfo = null;
        this.packetStatus = 0;
        this.packetLength = 0;
        this.packetData = null;
        this.selectionKey = NetCore.getInstance().register(channel, SelectionKey.OP_READ, this);
    }

    public SelectionKey getSelectionKey() {
        return selectionKey;
    }

    public SocketChannel getChannel() {
        return channel;
    }

    public boolean isLogin() {
        return userInfo != null;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public Client setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
        return this;
    }

    /**
     * 读取并预处理数据
     *
     * @throws IOException IO异常
     */
    public void doRead() throws IOException {
        int len;
        while ((len = channel.read(buffer)) != 0) {
            if (len == -1) {
                throw new IOException("通道关闭");
            }
            buffer.flip();
            while (buffer.hasRemaining()) {
                switch (packetStatus) {
                    case 0:
                        packetLength = buffer.get() & 0xff;
                        packetStatus = 1;
                        break;
                    case 1:
                        packetLength += (buffer.get() & 0xff) << 8;
                        packetData = new byte[packetLength];
                        packetLength = 0;
                        packetStatus = 2;
                        break;
                    case 2:
                        for (; buffer.hasRemaining() && packetLength < packetData.length; packetLength++) {
                            packetData[packetLength] = buffer.get();
                        }
                        if (packetLength == packetData.length) {
                            System.out.println("done");
                            // TODO: 2022/1/30
                            packetStatus = 0;
                        }
                        break;
                }
            }
            buffer.compact();
        }
        selectionKey = NetCore.getInstance().register(channel, SelectionKey.OP_READ, this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return Objects.equals(channel, client.channel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(channel);
    }
}