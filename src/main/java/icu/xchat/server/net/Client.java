package icu.xchat.server.net;

import icu.xchat.server.entities.UserInfo;
import icu.xchat.server.exceptions.UnknowTaskException;
import icu.xchat.server.net.tasks.CommandTask;
import icu.xchat.server.net.tasks.Task;
import icu.xchat.server.net.tasks.UserLoginTask;
import icu.xchat.server.utils.PackageUtils;
import icu.xchat.server.utils.PayloadTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 网络客户端实体
 *
 * @author shouchen
 */
public class Client {
    private static final Logger LOGGER = LoggerFactory.getLogger(Client.class);
    private SelectionKey selectionKey;
    private final SocketChannel channel;
    private final ByteBuffer readBuffer;
    private final ConcurrentHashMap<Integer, Task> taskMap;
    private final PackageUtils packageUtils;
    private UserInfo userInfo;
    private long heartTime;
    private int taskId;
    private int packetStatus;
    private int packetLength;
    private byte[] packetData;

    public Client(SocketChannel channel) throws IOException {
        this.channel = channel;
        this.readBuffer = ByteBuffer.allocateDirect(256);
        this.taskMap = new ConcurrentHashMap<>();
        this.userInfo = null;
        this.packageUtils = new PackageUtils();
        this.heartTime = System.currentTimeMillis();
        this.taskId = -1;
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

    public PackageUtils getPackageUtils() {
        return packageUtils;
    }

    /**
     * 读取并预处理数据
     *
     * @throws Exception 异常
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
     * 处理一个包
     *
     * @param packetBody 包
     */
    private void handlePacket(PacketBody packetBody) throws Exception {
        PacketBody packet;
        if (packetBody.getTaskId() != 0) {
            Task task = taskMap.get(packetBody.getTaskId());
            if (task == null) {
                switch (packetBody.getPayloadType()) {
                    case PayloadTypes.COMMAND:
                        task = new CommandTask();
                        break;
                    case PayloadTypes.LOGIN:
                        task = new UserLoginTask(this);
                        break;
                    // TODO: 2022/2/3
                    default:
                        throw new UnknowTaskException();
                }
                taskMap.put(packetBody.getTaskId(), task);
            }
            packet = task.handlePacket(packetBody);
            if (packet != null) {
                packet.setTaskId(packetBody.getTaskId());
                postPacket(packet);
            } else {
                taskMap.remove(packetBody.getTaskId());
            }
        } else {

        }
    }

    public void postPacket(PacketBody packetBody) {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Client client = (Client) o;
        return Objects.equals(channel, client.channel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(channel);
    }
}