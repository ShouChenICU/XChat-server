package icu.xchat.server.net.tasks;

import icu.xchat.server.constants.TaskTypes;
import icu.xchat.server.database.DaoManager;
import icu.xchat.server.exceptions.TaskException;
import icu.xchat.server.net.PacketBody;
import icu.xchat.server.net.WorkerThreadPool;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 用户信息同步
 *
 * @author shouchen
 */
public class UserSyncTask extends AbstractTask {
    private CountDownLatch latch;
    private List<String> uidCodeList;

    /**
     * 处理一个包
     *
     * @param packetBody 包
     */
    @Override
    public void handlePacket(PacketBody packetBody) throws Exception {
        // 丢给线程池
        WorkerThreadPool.execute(() -> {
            // 获取需要同步的所有用户识别码
            uidCodeList = DaoManager.getUserDao().getUidCodeListAboutUser(client.getUserInfo().getUidCode());
            // 如果列表为空，直接结束
            if (uidCodeList.isEmpty()) {
                client.postPacket(new PacketBody()
                        .setTaskId(this.taskId));
                done();
                return;
            }
            // 新建一个计数器
            latch = new CountDownLatch(uidCodeList.size());
            for (String uidCode : uidCodeList) {
                try {
                    // 添加一个推送用户信息的任务
                    client.addTask(new PushTask(DaoManager.getUserDao().getUserInfoByUidCode(uidCode),
                            PushTask.TYPE_USER_INFO,
                            PushTask.ACTION_UPDATE,
                            new ProgressAdapter() {
                                @Override
                                public void completeProgress() {
                                    super.completeProgress();
                                    // 推送成功计数器减一
                                    latch.countDown();
                                }
                            })
                    );
                } catch (TaskException e) {
                    // 不应该触发的异常
                    client.postPacket(new PacketBody()
                            .setTaskId(this.taskId)
                            .setTaskType(TaskTypes.ERROR)
                            .setData(e.getMessage().getBytes(StandardCharsets.UTF_8)));
                    terminate(e.getMessage());
                }
            }
            try {
                // 等待计数器清零
                if (latch.await(30, TimeUnit.SECONDS)) {
                    // 告诉客户端同步成功
                    client.postPacket(new PacketBody()
                            .setTaskId(this.taskId)
                            .setId(1));
                    done();
                } else {
                    // 超时同步失败
                    client.postPacket(new PacketBody()
                            .setTaskId(this.taskId)
                            .setTaskType(TaskTypes.ERROR)
                            .setData("超时，用户信息同步失败！".getBytes(StandardCharsets.UTF_8)));
                    terminate("超时，用户信息同步失败！");
                }
            } catch (InterruptedException e) {
                client.postPacket(new PacketBody()
                        .setTaskId(this.taskId)
                        .setTaskType(TaskTypes.ERROR)
                        .setData(e.getMessage().getBytes(StandardCharsets.UTF_8)));
                terminate(e.getMessage());
            }
        });
    }

    /**
     * 起步包
     *
     * @return 第一个发送的包
     */
    @Override
    public PacketBody startPacket() {
        return null;
    }
}
