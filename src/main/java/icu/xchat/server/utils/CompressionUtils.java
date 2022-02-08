package icu.xchat.server.utils;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * 数据解压缩工具类
 *
 * @author shouchen
 */
public final class CompressionUtils {
    private static Map<Long, Deflater> DEFLATER_MAP = new HashMap<>();
    private static Map<Long, Inflater> INFLATER_MAP = new HashMap<>();
    private static Map<Long, ByteArrayOutputStream> OUTPUT_STREAM_MAP = new HashMap<>();

    /**
     * 获取压缩器
     *
     * @return 压缩器
     */
    private static Deflater getDeflater() {
        long id = Thread.currentThread().getId();
        Deflater deflater = DEFLATER_MAP.get(id);
        if (deflater == null) {
            synchronized (CompressionUtils.class) {
                deflater = new Deflater(Deflater.BEST_COMPRESSION);
                Map<Long, Deflater> map = new HashMap<>(DEFLATER_MAP);
                map.put(id, deflater);
                DEFLATER_MAP = map;
            }
        }
        deflater.reset();
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
            synchronized (CompressionUtils.class) {
                inflater = new Inflater();
                Map<Long, Inflater> map = new HashMap<>(INFLATER_MAP);
                map.put(id, inflater);
                INFLATER_MAP = map;
            }
        }
        inflater.reset();
        return inflater;
    }

    private static ByteArrayOutputStream getOutputStream() {
        long id = Thread.currentThread().getId();
        ByteArrayOutputStream outputStream = OUTPUT_STREAM_MAP.get(id);
        if (outputStream == null) {
            synchronized (CompressionUtils.class) {
                outputStream = new ByteArrayOutputStream();
                Map<Long, ByteArrayOutputStream> map = new HashMap<>(OUTPUT_STREAM_MAP);
                map.put(id, outputStream);
                OUTPUT_STREAM_MAP = map;
            }
        }
        outputStream.reset();
        return outputStream;
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
        ByteArrayOutputStream outputStream = getOutputStream();
        byte[] buf = new byte[64];
        int len;
        while (!deflater.finished()) {
            len = deflater.deflate(buf);
            outputStream.write(buf, 0, len);
        }
        return outputStream.toByteArray();
    }

    /**
     * 解压数据
     *
     * @param data 待解压数据
     * @return 解压后的数据
     */
    public static byte[] deCompress(byte[] data) throws DataFormatException {
        if (data.length == 0) {
            return null;
        }
        Inflater inflater = getInflater();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = getOutputStream();
        byte[] buf = new byte[64];
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