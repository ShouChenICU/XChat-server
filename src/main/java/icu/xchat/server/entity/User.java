package icu.xchat.server.entity;

import java.util.Objects;

/**
 * 用户实体类
 *
 * @author shouchen
 */
public class User {
    private String uidCode;
    private String nick;
    private int tid;
    private int status;
    private long timeStamp;

    public String getUidCode() {
        return uidCode;
    }

    public void setUidCode(String uidCode) {
        this.uidCode = uidCode;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public int getTid() {
        return tid;
    }

    public void setTid(Integer tid) {
        this.tid = tid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return Objects.equals(uidCode, user.uidCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uidCode);
    }
}
