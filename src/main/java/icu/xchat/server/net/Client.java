package icu.xchat.server.net;

import icu.xchat.server.constants.TaskTypes;
import icu.xchat.server.database.DaoManager;
import icu.xchat.server.entities.UserInfo;
import icu.xchat.server.exceptions.TaskException;
import icu.xchat.server.net.tasks.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.channels.ClosedChannelException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 网络客户端实体
 *
 * @author shouchen
 */
public abstract class Client extends NetNode {
    private static final Logger LOGGER = LoggerFactory.getLogger(Client.class);
    /**
     * 任务map
     */
    private final ConcurrentHashMap<Integer, Task> taskMap;
    /**
     * 用户加入的房间列表
     */
    private List<Integer> ridList;
    /**
     * 任务id计数器
     */
    private int taskId;

    public abstract UserInfo getUserInfo();

    public Client() {
        this.taskMap = new ConcurrentHashMap<>();
        this.ridList = new ArrayList<>();
        this.taskId = -1;
    }

    @Override
    public void update(AbstractNetIO abstractNetIO) {
        super.update(abstractNetIO);
        this.ridList = DaoManager.getRoomDao().getRoomIdListByUidCode(getUserInfo().getUidCode());
        ChatRoomManager.updateClient(this);
        // TODO: 2022/4/2
    }

    /**
     * 处理一个包
     *
     * @param packetBody 包
     */
    @Override
    protected void packageHandler(PacketBody packetBody) throws Exception {
        if (packetBody.getTaskId() != 0) {
            Task task = taskMap.get(packetBody.getTaskId());
            if (task == null) {
                switch (packetBody.getTaskType()) {
                    case TaskTypes.COMMAND:
                        task = new CommandTask();
                        break;
                    case TaskTypes.TRANSMIT:
                        task = new ReceiveTask();
                        break;
                    case TaskTypes.IDENTITY_SYNC:
                        task = new IdentitySyncTask();
                        break;
                    case TaskTypes.ROOM_SYNC:
                        task = new RoomSyncTask();
                        break;
                    case TaskTypes.USER_SYNC:
                        task = new UserSyncTask();
                        break;
                    case TaskTypes.MSG_SYNC:
                        task = new MessageSyncTask();
                        break;
                    default:
                        throw new TaskException("未知任务类型");
                }
                task.setTaskId(packetBody.getTaskId());
                ((AbstractTask) task).setClient(this);
                taskMap.put(packetBody.getTaskId(), task);
            }
            if (!Objects.equals(packetBody.getTaskType(), TaskTypes.ERROR)) {
                task.handlePacket(packetBody);
            } else {
                task.terminate(new String(packetBody.getData(), StandardCharsets.UTF_8));
            }
        } else {
            if (Objects.equals(packetBody.getTaskType(), TaskTypes.LOGOUT)) {
                logout();
            }
        }
    }

    /**
     * 添加一个任务
     *
     * @param task 任务
     */
    public void addTask(Task task) throws TaskException {
        ((AbstractTask) task).setClient(this);
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

    public Client setRidList(List<Integer> ridList) {
        this.ridList = ridList;
        return this;
    }

    public List<Integer> getRidList() {
        return Collections.unmodifiableList(ridList);
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
     * 登出
     */
    public void logout() {
        for (Task task : taskMap.values()) {
            task.terminate("Closed");
        }
        taskMap.clear();
        taskId = -1;
        ChatRoomManager.removeClient(this);
        ClientManager.unloadClient(getUserInfo().getUidCode());
        try {
            disconnect();
        } catch (Exception e) {
            LOGGER.warn("", e);
        }
    }

    @Override
    protected void exceptionHandler(Exception exception) {
        if (exception.getClass() != ClosedChannelException.class) {
            LOGGER.warn("", exception);
        }
        try {
//            disconnect();
            logout();
        } catch (Exception e) {
            LOGGER.warn("", e);
        }
        // TODO: 2022/3/31
    }
}