package icu.xchat.server.utils;

import icu.xchat.server.net.PacketBody;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

/**
 * 网络传输帧打包解包工具类
 *
 * @author shouchen
 */
public class PackageUtils {
    private SecretKey encryptKey;
    private Cipher encryptCipher;
    private Cipher decryptCipher;

    public PackageUtils setEncryptKey(SecretKey encryptKey) {
        this.encryptKey = encryptKey;
        return this;
    }

    public PackageUtils setEncryptCipher(Cipher encryptCipher) {
        this.encryptCipher = encryptCipher;
        return this;
    }

    public PackageUtils setDecryptCipher(Cipher decryptCipher) {
        this.decryptCipher = decryptCipher;
        return this;
    }

    public byte[] encodePacket(PacketBody packetBody) {
        // TODO: 2022/2/1
        return null;
    }

    public PacketBody decodePacket(byte[] data) {
        // TODO: 2022/2/1
        return null;
    }
}
