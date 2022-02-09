package icu.xchat.server.utils;

import org.bson.*;

import java.util.HashMap;
import java.util.Map;

/**
 * BSON工具类
 *
 * @author shouchen
 */
public final class BsonUtils {
    private static final Map<Long, BSONEncoder> encoderMap;
    private static final Map<Long, BSONDecoder> decoderMap;

    static {
        encoderMap = new HashMap<>();
        decoderMap = new HashMap<>();
    }

    private static BSONEncoder getEncoder() {
        long id = Thread.currentThread().getId();
        BSONEncoder encoder = encoderMap.get(id);
        if (encoder == null) {
            encoder = new BasicBSONEncoder();
            synchronized (encoderMap) {
                encoderMap.put(id, encoder);
            }
        }
        return encoder;
    }

    private static BSONDecoder getDecoder() {
        long id = Thread.currentThread().getId();
        BSONDecoder decoder = decoderMap.get(id);
        if (decoder == null) {
            decoder = new BasicBSONDecoder();
            synchronized (decoderMap) {
                decoderMap.put(id, decoder);
            }
        }
        return decoder;
    }

    public static byte[] encode(BSONObject object) {
        return getEncoder().encode(object);
    }

    public static BSONObject decode(byte[] data) {
        return getDecoder().readObject(data);
    }
}
