package icu.xchat.server;

import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * 数据解压缩工具类
 *
 * @author shouchen
 */
public final class CompressionUtils {
    private static final int LEVEL = 9;
    private static final Map<Long, Deflater> DEFLATER_MAP = new ConcurrentHashMap<>();
    private static final Map<Long, Inflater> INFLATER_MAP = new ConcurrentHashMap<>();

    /**
     * 获取压缩器
     *
     * @return 压缩器
     */
    private static Deflater getDeflater() {
        long id = Thread.currentThread().getId();
        Deflater deflater = DEFLATER_MAP.get(id);
        if (deflater == null) {
            deflater = new Deflater(LEVEL);
            DEFLATER_MAP.put(id, deflater);
        }
        return deflater;
    }

    /**
     * 获取解压器
     *
     * @return 解压器
     */
    private static Inflater getInflater() {
        long id = Thread.currentThread().getId();
        Inflater inflater = INFLATER_MAP.get(id);
        if (inflater == null) {
            inflater = new Inflater();
            INFLATER_MAP.put(id, inflater);
        }
        return inflater;
    }

    /**
     * 压缩数据
     *
     * @param data 待压缩数据
     * @return 压缩后的数据
     */
    public static byte[] compress(byte[] data) {
        Deflater deflater = getDeflater();
        deflater.setInput(data);
        deflater.finish();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buf = new byte[16];
        int len;
        while (!deflater.finished()) {
            len = deflater.deflate(buf);
            outputStream.write(buf, 0, len);
        }
        deflater.reset();
        return outputStream.toByteArray();
    }

    /**
     * 解压数据
     *
     * @param data 待解压数据
     * @return 解压后的数据
     */
    public static byte[] deCompress(byte[] data) throws DataFormatException {
        Inflater inflater = getInflater();
        inflater.setInput(data);
        inflater.finished();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buf = new byte[16];
        int len;
        while (!inflater.finished()) {
            len = inflater.inflate(buf);
            outputStream.write(buf, 0, len);
        }
        return outputStream.toByteArray();
    }

    /**
     * 测试待数据的压缩率
     *
     * @param data 数据
     * @return 压缩率
     */
    public static double testCompressionRate(byte[] data) {
        byte[] dat1 = compress(data);
        return (double) dat1.length / data.length;
    }
}