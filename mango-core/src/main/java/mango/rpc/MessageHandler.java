package mango.rpc;

import mango.core.Request;
import mango.core.Response;

/**
 * 消息处理器
 */
public interface MessageHandler {

    /**
     * 处理消息并返回处理结果
     *
     * @param request
     * @return
     */
    Response handle(Request request);

}
