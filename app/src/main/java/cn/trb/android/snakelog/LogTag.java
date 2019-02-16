package cn.trb.android.snakelog;

/**
 * Author: Tang Rongbing
 * DateTime: 2019/2/16 11:36
 */
class LogTag {
    final String className;
    final String simpleClassName;
    final String methodName;
    final int line;

    LogTag() {
        StackTraceElement caller = new Throwable().getStackTrace()[2];
        className = caller.getClassName();
        simpleClassName = className.substring(className.lastIndexOf(".") + 1);
        methodName = caller.getMethodName();
        line = caller.getLineNumber();
    }
}
