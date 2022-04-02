package icu.xchat.server.utils;

import icu.xchat.server.entities.MessageInfo;
import org.bson.BSONObject;
import org.bson.BasicBSONObject;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Base64;
import java.util.Objects;

/**
 * 数字签名工具类
 *
 * @author shouchen
 */
public final class SignatureUtils {
    private static final String SIGN_ALGORITHM = "SHA256withRSA";

    /**
     * 签名消息
     *
     * @param messageInfo 消息实体
     * @param privateKey  私钥
     */
    public static void signMsg(MessageInfo messageInfo, PrivateKey privateKey) throws SignatureException {
        try {
            BSONObject object = new BasicBSONObject();
            object.put("SENDER", messageInfo.getSender());
            object.put("TYPE", messageInfo.getType());
            object.put("CONTENT", messageInfo.getContent());
            object.put("TIMESTAMP", messageInfo.getTimeStamp());
            byte[] bytes = BsonUtils.encode(object);
            Signature signature = Signature.getInstance(SIGN_ALGORITHM);
            signature.initSign(privateKey);
            signature.update(bytes);
            messageInfo.setSignature(Base64.getEncoder().encodeToString(signature.sign()));
        } catch (Exception e) {
            throw new SignatureException(e);
        }
    }

    /**
     * 验签消息
     *
     * @param messageInfo 消息实体
     * @param publicKey   公钥
     * @return 结果
     */
    public static boolean checkSign(MessageInfo messageInfo, PublicKey publicKey) {
        try {
            if (!Objects.equals(messageInfo.getSender(), IdentityUtils.getCodeByPublicKeyCode(publicKey.getEncoded()))) {
                return false;
            }
            BSONObject object = new BasicBSONObject();
            object.put("SENDER", messageInfo.getSender());
            object.put("TYPE", messageInfo.getType());
            object.put("CONTENT", messageInfo.getContent());
            object.put("TIMESTAMP", messageInfo.getTimeStamp());
            byte[] bytes = BsonUtils.encode(object);
            Signature signature = Signature.getInstance(SIGN_ALGORITHM);
            signature.initVerify(publicKey);
            signature.update(bytes);
            return signature.verify(Base64.getDecoder().decode(messageInfo.getSignature()));
        } catch (Exception e) {
            return false;
        }
    }
}
