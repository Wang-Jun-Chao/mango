package mango.util;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * IO工具
 */
public class IoUtils {

    /**
     * 关闭输入流
     *
     * @param input
     */
    public static void closeQuietly(InputStream input) {
        closeQuietly((Closeable) input);
    }

    /**
     * 关闭输出流
     *
     * @param output
     */
    public static void closeQuietly(OutputStream output) {
        closeQuietly((Closeable) output);
    }

    /**
     * 关闭流
     *
     * @param closeable
     */
    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭流
     *
     * @param closeables
     */
    public static void closeQuietly(Closeable... closeables) {
        for (Closeable closeable : closeables) {
            closeQuietly(closeable);
        }
    }

    /**
     * 将输入流的内容读取到输出流中
     *
     * @param in
     * @param out
     * @param bufferSize
     * @return
     * @throws IOException
     */
    public static long copy(InputStream in, OutputStream out, int bufferSize) throws IOException {
        byte[] buff = new byte[bufferSize];
        return copy(in, out, buff);
    }

    /**
     * 将输入流的内容读取到输出流中
     *
     * @param in
     * @param out
     * @return
     * @throws IOException
     */
    public static long copy(InputStream in, OutputStream out) throws IOException {
        byte[] buff = new byte[1024];
        return copy(in, out, buff);
    }

    /**
     * 将输入流的内容读取到输出流中
     *
     * @param in
     * @param out
     * @param buff
     * @return
     * @throws IOException
     */
    public static long copy(InputStream in, OutputStream out, byte[] buff) throws IOException {
        long count = 0;
        int len = -1;
        while ((len = in.read(buff, 0, buff.length)) != -1) {
            out.write(buff, 0, len);
            count += len;
        }
        return count;
    }
}
