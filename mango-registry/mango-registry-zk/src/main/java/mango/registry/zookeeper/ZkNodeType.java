package mango.registry.zookeeper;

/**
 * Zookeeper结点类型
 */
public enum ZkNodeType {
    /**
     * 服务端
     */
    SERVER("providers"),
    /**
     * 客户端
     */
    CLIENT("consumers");

    private String value;

    ZkNodeType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
