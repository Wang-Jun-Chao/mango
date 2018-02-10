package mango.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.*;
import java.util.Enumeration;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 网络工具类
 */
public class NetUtils {
    /**
     * 本机地址
     */
    public static final String LOCALHOST = "127.0.0.1";
    /**
     * 任播地址
     */
    public static final String ANYHOST = "0.0.0.0";
    private static final Logger logger = LoggerFactory.getLogger(NetUtils.class);
    /**
     * 地址正则表达式
     */
    private static final Pattern ADDRESS_PATTERN = Pattern.compile("^\\d{1,3}(\\.\\d{1,3}){3}\\:\\d{1,5}$");
    /**
     * IP正则表达式
     */
    private static final Pattern IP_PATTERN = Pattern.compile("\\d{1,3}(\\.\\d{1,3}){3,5}$");
    /**
     * 本机地址
     */
    private static volatile InetAddress LOCAL_ADDRESS = null;

    /**
     * 根据地址字符串获取网络地址对象
     *
     * @param addr
     * @return
     */
    public static InetSocketAddress parseSocketAddress(final String addr) {
        String[] arr = addr.split(":");
        InetSocketAddress isa = new InetSocketAddress(arr[0], Integer.parseInt(arr[1]));
        return isa;
    }

    /**
     * 获取本机地址
     *
     * @return
     */
    public static InetAddress getLocalAddress() {
        return getLocalAddress(null);
    }

    /**
     * 获取本机地址
     *
     * @param destHostPorts
     * @return
     */
    public static InetAddress getLocalAddress(Map<String, Integer> destHostPorts) {
        if (LOCAL_ADDRESS != null) {
            return LOCAL_ADDRESS;
        }

        // 根据本地主机名获取
        InetAddress localAddress = getLocalAddressByHostname();
        if (!isValidAddress(localAddress)) {
            // 根据输入的ip和地址信息获取本地地址
            localAddress = getLocalAddressBySocket(destHostPorts);
        }

        // 根据网络接口获取地址
        if (!isValidAddress(localAddress)) {
            localAddress = getLocalAddressByNetworkInterface();
        }

        if (isValidAddress(localAddress)) {
            LOCAL_ADDRESS = localAddress;
        }

        return localAddress;
    }

    /**
     * 根据主机名获取网络地址对象
     *
     * @return
     */
    private static InetAddress getLocalAddressByHostname() {
        try {
            InetAddress localAddress = InetAddress.getLocalHost();
            if (isValidAddress(localAddress)) {
                return localAddress;
            }
        } catch (Throwable e) {
            logger.warn("Failed to retriving local address by hostname:" + e);
        }
        return null;
    }

    /**
     * 获取网络地址
     *
     * @param destHostPorts key:IP地址， value:端口号
     * @return
     */
    private static InetAddress getLocalAddressBySocket(Map<String, Integer> destHostPorts) {
        if (destHostPorts == null || destHostPorts.size() == 0) {
            return null;
        }

        // 找到一个地址就返回
        for (Map.Entry<String, Integer> entry : destHostPorts.entrySet()) {
            String host = entry.getKey();
            int port = entry.getValue();
            try {
                Socket socket = new Socket();
                try {
                    SocketAddress addr = new InetSocketAddress(host, port);
                    socket.connect(addr, 1000);
                    return socket.getLocalAddress();
                } finally {
                    try {
                        socket.close();
                    } catch (Throwable e) {
                    }
                }
            } catch (Exception e) {
                logger.warn(String.format("Failed to retriving local address by connecting to dest host:port(%s:%s) false, e=%s", host,
                        port, e));
            }
        }
        return null;
    }

    /**
     * 根据网络接口获取网络地址对象
     *
     * @return
     */
    private static InetAddress getLocalAddressByNetworkInterface() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            if (interfaces != null) {
                while (interfaces.hasMoreElements()) {
                    try {
                        NetworkInterface network = interfaces.nextElement();
                        Enumeration<InetAddress> addresses = network.getInetAddresses();
                        while (addresses.hasMoreElements()) {
                            try {
                                InetAddress address = addresses.nextElement();
                                if (isValidAddress(address)) {
                                    return address;
                                }
                            } catch (Throwable e) {
                                logger.warn("Failed to retriving ip address, " + e.getMessage(), e);
                            }
                        }
                    } catch (Throwable e) {
                        logger.warn("Failed to retriving ip address, " + e.getMessage(), e);
                    }
                }
            }
        } catch (Throwable e) {
            logger.warn("Failed to retriving ip address, " + e.getMessage(), e);
        }
        return null;
    }

    /**
     * 验证地址是否符格式
     *
     * @param address
     * @return
     */
    public static boolean isValidAddress(String address) {
        return ADDRESS_PATTERN.matcher(address).matches();
    }

    /**
     * 验证地址是否正确，地址非空，非127.0.0.1，非任播地址
     *
     * @param address
     * @return
     */
    public static boolean isValidAddress(InetAddress address) {
        if (address == null || address.isLoopbackAddress()) {
            return false;
        }
        String name = address.getHostAddress();
        return (name != null && !ANYHOST.equals(name) && !LOCALHOST.equals(name) && IP_PATTERN.matcher(name).matches());
    }
}
