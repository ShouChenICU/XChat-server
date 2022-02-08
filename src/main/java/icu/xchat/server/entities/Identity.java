package icu.xchat.server.entities;

import org.bson.BSONObject;
import org.bson.BasicBSONEncoder;
import org.bson.BasicBSONObject;

import java.security.*;
import java.util.*;

/**
 * 身份
 *
 * @author shouchen
 */
public class Identity {
    private static final String SIGN_ALGORITHM = "SHA256withRSA";
    private String uidCode;
    private PublicKey publicKey;
    private PrivateKey privateKey;
    private Map<String, String> attributes;
    private String signature;
    private long timeStamp;

    public Identity() {
        attributes = new HashMap<>();
        timeStamp = System.currentTimeMillis();
    }

    public Identity setUidCode(String uidCode) {
        this.uidCode = uidCode;
        return this;
    }

    public String getUidCode() {
        return uidCode;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public Identity setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
        return this;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public Identity setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
        return this;
    }

    public Identity setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
        return this;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public Map<String, String> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }

    public Identity setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
        return this;
    }

    public Identity setAttribute(String key, String value) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        attributes.put(key, value);
        timeStamp = System.currentTimeMillis();
        return sign();
    }

    public Identity removeAttribute(String key) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        attributes.remove(key);
        timeStamp = System.currentTimeMillis();
        return sign();
    }

    public String getAttribute(String key) {
        return attributes.get(key);
    }

    public String getSignature() {
        return signature;
    }

    public Identity setSignature(String signature) {
        this.signature = signature;
        return this;
    }

    /**
     * 验签
     */
    public boolean checkSignature() {
        if (this.timeStamp > System.currentTimeMillis()) {
            return false;
        }
        try {
            BSONObject object = new BasicBSONObject();
            object.put("ATTRIBUTES", attributes);
            object.put("TIMESTAMP", timeStamp);
            byte[] dat = new BasicBSONEncoder().encode(object);
            Signature signature = Signature.getInstance(SIGN_ALGORITHM);
            signature.initVerify(this.publicKey);
            signature.update(dat);
            return signature.verify(Base64.getDecoder().decode(this.signature));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 使用当前身份的私钥签名属性集和时间戳
     */
    public Identity sign() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        BSONObject object = new BasicBSONObject();
        object.put("ATTRIBUTES", attributes);
        object.put("TIMESTAMP", timeStamp);
        byte[] dat = new BasicBSONEncoder().encode(object);
        Signature signature = Signature.getInstance(SIGN_ALGORITHM);
        signature.initSign(privateKey);
        signature.update(dat);
        this.signature = Base64.getEncoder().encodeToString(signature.sign());
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Identity identity = (Identity) o;
        return Objects.equals(uidCode, identity.uidCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uidCode);
    }

    @Override
    public String toString() {
        return "Identity{" +
                "uidCode='" + uidCode + '\'' +
                ", publicKey=" + (publicKey == null ? "null" : "***") +
                ", privateKey=" + (privateKey == null ? "null" : "***") +
                ", attributes=" + attributes +
                ", signature=" + (signature == null ? "null" : "***") +
                ", timeStamp=" + timeStamp +
                '}';
    }
}
