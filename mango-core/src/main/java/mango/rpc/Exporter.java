package mango.rpc;

/**
 * 导出类，暴露者
 */
public interface Exporter<T> extends Node {

    /**
     * 获取提供者对象
     *
     * @return
     */
    Provider<T> getProvider();

    /**
     * 取消导出
     */
    void unexport();
}
