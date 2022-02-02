package icu.xchat.server.net.tasks;

import icu.xchat.server.GlobalVariables;
import icu.xchat.server.exceptions.LoginFailException;
import icu.xchat.server.exceptions.RepeatLoginException;
import icu.xchat.server.net.Client;
import icu.xchat.server.net.PacketBody;
import icu.xchat.server.utils.EncryptUtils;
import icu.xchat.server.utils.SecurityKeyPairTool;
import org.bson.BSONObject;
import org.bson.BasicBSONDecoder;
import org.bson.BasicBSONEncoder;
import org.bson.BasicBSONObject;

import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class LoginTask extends AbstractTask {
    private final Client client;

    public LoginTask(Client client) throws RepeatLoginException {
        super(null, null);
        if (client.getUserInfo() != null) {
            throw new RepeatLoginException();
        }
        this.packetSum = 3;
        this.client = client;
    }

    @Override
    public PacketBody handlePacket(PacketBody packetBody) throws LoginFailException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        PacketBody packet = null;
        byte[] data = packetBody.getData();
        if (data == null) {
            throw new LoginFailException("null");
        }
        BSONObject bsonObject = new BasicBSONDecoder().readObject(data);
        switch (packetBody.getId()) {
            case 0:
                if (!Objects.equals(GlobalVariables.PROTOCOL_VERSION, bsonObject.get("PROTOCOL_VERSION"))) {
                    throw new LoginFailException("通讯协议版本错误");
                }
                bsonObject = new BasicBSONObject();
                bsonObject.put("PUBLIC_KEY", SecurityKeyPairTool.getPublicKey().getEncoded());
                packet = new PacketBody()
                        .setTaskId(packetBody.getTaskId())
                        .setId(++this.packetCount)
                        .setData(new BasicBSONEncoder().encode(bsonObject));
                client.getPackageUtils().setDecryptCipher(EncryptUtils.getDecryptCipher());
                break;
            case 1:

        }
        return packet;
    }

    @Override
    public PacketBody startPacket() {
        return null;
    }
}
