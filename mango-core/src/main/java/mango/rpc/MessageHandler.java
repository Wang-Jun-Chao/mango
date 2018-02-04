package mango.rpc;

import mango.core.Request;
import mango.core.Response;

/**
 * @author Ricky Fung
 */
public interface MessageHandler {

    Response handle(Request request);

}
