package mango.util;

/**
 * 常量类
 */
public class Constants {

    /**
     * 框架名称
     */
    public static final String FRAMEWORK_NAME = "mango";

    /**
     * 协议分割符
     */
    public static final String PROTOCOL_SEPARATOR = "://";
    /**
     * 路径 分割符
     */
    public static final String PATH_SEPARATOR = "/";

    /**
     * zookeeper注策中收命名空间
     */
    public static final String ZOOKEEPER_REGISTRY_NAMESPACE = "/mango";

    /**
     * 头部信息的大小应该是 short+byte+long+int = 2+1+8+4 = 15
     * 不论是请求还是响应都需要写入一个short，btye，long，int类型的信息
     */
    public static final int HEADER_SIZE = 15;

    /**
     * Netty传输的魔法数
     */
    public static final short NETTY_MAGIC_TYPE = (short) 0x9F9F;

    /**
     * 请求标识符
     */
    public static final byte FLAG_REQUEST = 0x01;
    /**
     * 响应标识符
     */
    public static final byte FLAG_RESPONSE = 0x03;
    /**
     * 其他类型标识符
     */
    public static final byte FLAG_OTHER = (byte) 0xFF;


    /**
     * 单向请求
     */
    public static final byte REQUEST_ONEWAY = 0x03;
    /**
     * 同步请求
     */
    public static final byte REQUEST_SYNC = 0x05;
    /**
     * 异步请求
     */
    public static final byte REQUEST_ASYNC = 0x07;

    /**
     * 默认端口
     */
    public static final int DEFAULT_PORT = 21918;

    /**
     * 整形的默认值
     */
    public static final int DEFAULT_INT_VALUE = 0;

    /**
     * 本地注册协议
     */
    public static final String REGISTRY_PROTOCOL_LOCAL = "local";

    /**
     * 主机端口分割符
     */
    public static final String HOST_PORT_SEPARATOR = ":";

    /**
     * 默认值
     */
    public static final String DEFAULT_VALUE = "default";

    /**
     * 提供者
     */
    public static final String PROVIDER = "provider";
    /**
     * 消费者
     */
    public static final String CONSUMER = "consumer";

}
