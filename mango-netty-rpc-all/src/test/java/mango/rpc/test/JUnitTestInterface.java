package mango.rpc.test;

import java.util.List;

public interface JUnitTestInterface {
    String methodWithoutArg();

    String methodWithArgs(String arg1, int arg2);

    JUnitTestCustomObject methodWithCustomObject(JUnitTestCustomObject customObject);

    List<String> methodReturnList(String arg1, String arg2);

    void methodThrowException();

    void methodTimeOut();

    void methodReturnVoid();

    String methodDelayOneSecond();

    int methodForMultiThread(int threadId);

    String methodForPerformance();
}
