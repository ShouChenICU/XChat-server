package icu.xchat.server.utils;

import icu.xchat.server.configurations.ServerConfiguration;
import org.bson.BSONObject;
import org.bson.BasicBSONEncoder;
import org.bson.BasicBSONObject;

import java.security.MessageDigest;
import java.util.Base64;

/**
 * 服务器连接码工具类
 *
 * @author shouchen
 */
public final class ServerConnectCodeUtils {

    /**
     * 根据当前配置生成服务器连接码
     *
     * @return 连接码
     */
    public static String genServerCode() throws Exception {
        ServerConfiguration configuration = ServerConfiguration.load();
        SecurityKeyPairTool.loadKeyPair(configuration.getKeypairAlgorithm());
        byte[] digest = MessageDigest.getInstance("SHA-256").digest(SecurityKeyPairTool.getPublicKey().getEncoded());
        byte[] dat = new byte[12];
        System.arraycopy(digest, 0, dat, 0, dat.length);
        BSONObject object = new BasicBSONObject();
        object.put("SERVER_CODE", Base64.getEncoder().encodeToString(dat));
        object.put("AKE_ALGORITHM", configuration.getKeypairAlgorithm());
        object.put("HOST", configuration.getServerHost());
        object.put("PORT", configuration.getServerPort());
        byte[] data = new BasicBSONEncoder().encode(object);
        return Base64.getEncoder().encodeToString(CompressionUtils.compress(data));
    }
}
