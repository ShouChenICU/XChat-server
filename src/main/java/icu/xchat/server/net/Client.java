package icu.xchat.server.net;

import icu.xchat.server.entities.UserInfo;
import icu.xchat.server.exceptions.PacketException;
import icu.xchat.server.exceptions.TaskException;
import icu.xchat.server.net.tasks.CommandTask;
import icu.xchat.server.net.tasks.IdentitySyncTask;
import icu.xchat.server.net.tasks.Task;
import icu.xchat.server.net.tasks.UserLoginTask;
import icu.xchat.server.utils.PackageUtils;
import icu.xchat.server.utils.TaskTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

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
    private final ByteBuffer writeBuffer;
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
        this.readBuffer = ByteBuffer.allocateDirect(512);
        this.writeBuffer = ByteBuffer.allocateDirect(512);
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
     */
    public void doRead() {
        try {
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
        } catch (Exception e) {
            LOGGER.warn("", e);
            DispatchCenter.getInstance().closeClient(this);
        }
    }

    /**
     * 处理一个包
     *
     * @param packetBody 包
     */
    private void handlePacket(PacketBody packetBody) throws Exception {
        this.heartTime = System.currentTimeMillis();
        if (packetBody.getTaskId() != 0) {
            Task task = taskMap.get(packetBody.getTaskId());
            if (task == null) {
                switch (packetBody.getTaskType()) {
                    case TaskTypes.COMMAND:
                        task = new CommandTask()
                                .setTaskId(packetBody.getTaskId());
                        break;
                    case TaskTypes.LOGIN:
                        task = new UserLoginTask(this)
                                .setTaskId(packetBody.getTaskId());
                        break;
                    case TaskTypes.IDENTITY_SYNC:
                        task = new IdentitySyncTask(this)
                                .setTaskId(packetBody.getTaskId());
                        break;
                    default:
                        throw new TaskException("未知任务类型");
                }
                taskMap.put(packetBody.getTaskId(), task);
            }
            task.handlePacket(packetBody);
        } else {
            if (Objects.equals(packetBody.getTaskType(), TaskTypes.LOGOUT)) {
                throw new Exception("");
            }
        }
    }

    /**
     * 添加一个任务
     *
     * @param task 任务
     */
    public void addTask(Task task) throws TaskException {
        PacketBody packetBody = task.startPacket();
        if (packetBody == null) {
            throw new TaskException("起步包为空");
        }
        int id;
        synchronized (taskMap) {
            id = this.taskId--;
            taskMap.put(id, task);
        }
        task.setTaskId(id);
        packetBody.setTaskId(id);
        WorkerThreadPool.execute(() -> postPacket(packetBody));
    }

    /**
     * 移除一个任务
     *
     * @param taskId 任务id
     */
    public void removeTask(int taskId) {
        this.taskMap.remove(taskId);
    }

    /**
     * 发送一个包
     *
     * @param packetBody 包
     */
    public void postPacket(PacketBody packetBody) {
        try {
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
        } catch (Exception e) {
            LOGGER.warn("", e);
            DispatchCenter.getInstance().closeClient(this);
        }
    }

    public long getHeartTime() {
        return heartTime;
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