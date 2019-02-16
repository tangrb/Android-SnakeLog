package cn.trb.android.snakelog;

import android.os.Process;
import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * Author: Tang Rongbing
 * DateTime: 2019/2/16 9:37
 */
public class SnakeLog {
    private SnakeLog() {
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private static boolean printSimpleClassName = true;

    /**
     * 设置是否打印简单的类名
     *
     * @param printSimpleClassName true表示打印简单的类名，false表示打印完整的类名
     */
    public static void setPrintSimpleClassName(boolean printSimpleClassName) {
        SnakeLog.printSimpleClassName = printSimpleClassName;
    }

    private static boolean printV = false;
    private static boolean printD = false;
    private static boolean printI = false;
    private static boolean printW = false;
    private static boolean printE = false;

    /**
     * 设置打印的日志等级，每个参数对应一个级别的日志是否要被打印
     */
    public static void setPrintLogLevel(boolean v, boolean d, boolean i, boolean w, boolean e) {
        printV = v;
        printD = d;
        printI = i;
        printW = w;
        printE = e;
    }

    private static boolean saveV = false;
    private static boolean saveD = false;
    private static boolean saveI = false;
    private static boolean saveW = false;
    private static boolean saveE = false;

    /**
     * 设置保存的日志等级，每个参数对应一个级别的日志是否要被保存
     */
    public static void setSaveLogLevel(boolean v, boolean d, boolean i, boolean w, boolean e) {
        saveV = v;
        saveD = d;
        saveI = i;
        saveW = w;
        saveE = e;
    }

    private static boolean enableSaveLog = false;
    private static SaveLogThread saveLogThread;

    /**
     * 开始保存日志
     *
     * @param logFilePath 要保存的日志的绝对路径名，如果是相同名字的日志文件，那么新的文件将覆盖旧的文件
     * @param charset     日志文件的字符编码
     */
    public static synchronized void startSaveLog(String logFilePath, String charset) throws Exception {
        if (!enableSaveLog) {
            saveLogThread = new SaveLogThread(logFilePath, charset);
            saveLogThread.start();
            enableSaveLog = true;
        }
    }

    /**
     * 停止保存日志
     */
    public static synchronized void stopSaveLog() {
        if (enableSaveLog) {
            enableSaveLog = false;
            saveLogThread.stopSave();
            saveLogThread = null;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static void v(String msg) {
        if (printV) {
            LogTag tag = new LogTag();
            String printTag = buildPrintTag(tag);
            String printMsg = buildPrintMsg(tag, msg);
            Log.v(printTag, printMsg);
            if (saveV) {
                saveLog("V", printTag, printMsg);
            }
        }
    }

    public static void d(String msg) {
        if (printD) {
            LogTag tag = new LogTag();
            String printTag = buildPrintTag(tag);
            String printMsg = buildPrintMsg(tag, msg);
            Log.d(printTag, printMsg);
            if (saveD) {
                saveLog("D", printTag, printMsg);
            }
        }
    }

    public static void i(String msg) {
        if (printI) {
            LogTag tag = new LogTag();
            String printTag = buildPrintTag(tag);
            String printMsg = buildPrintMsg(tag, msg);
            Log.i(printTag, printMsg);
            if (saveI) {
                saveLog("I", printTag, printMsg);
            }
        }
    }

    public static void w(String msg) {
        if (printW) {
            LogTag tag = new LogTag();
            String printTag = buildPrintTag(tag);
            String printMsg = buildPrintMsg(tag, msg);
            Log.w(printTag, printMsg);
            if (saveW) {
                saveLog("W", printTag, printMsg);
            }
        }
    }

    public static void e(String msg) {
        if (printE) {
            LogTag tag = new LogTag();
            String printTag = buildPrintTag(tag);
            String printMsg = buildPrintMsg(tag, msg);
            Log.e(printTag, printMsg);
            if (saveE) {
                saveLog("E", printTag, printMsg);
            }
        }
    }

    public static void w(Throwable e) {
        if (printW) {
            LogTag tag = new LogTag();
            String printTag = buildPrintTag2(tag);
            String printMsg = buildPrintMsg(e);
            Log.w(printTag, printMsg);
            if (saveW) {
                saveLog("W", printTag, printMsg);
            }
        }
    }

    public static void e(Throwable e) {
        if (printE) {
            LogTag tag = new LogTag();
            String printTag = buildPrintTag2(tag);
            String printMsg = buildPrintMsg(e);
            Log.e(printTag, printMsg);
            if (saveE) {
                saveLog("E", printTag, printMsg);
            }
        }
    }

    public static void w(String msg, Throwable e) {
        if (printW) {
            LogTag tag = new LogTag();
            String printTag = buildPrintTag2(tag);
            Log.w(printTag, msg, e);
            if (saveW) {
                saveLog("W", printTag, msg + "\n" + buildPrintMsg(e));
            }
        }
    }

    public static void e(String msg, Throwable e) {
        if (printE) {
            LogTag tag = new LogTag();
            String printTag = buildPrintTag2(tag);
            Log.e(printTag, msg, e);
            if (saveE) {
                saveLog("E", printTag, msg + "\n" + buildPrintMsg(e));
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private static String buildPrintTag(LogTag tag) {
        return printSimpleClassName ? tag.simpleClassName : tag.className;
    }

    private static final String PRINT_TAG_FMT = "%s: %s:(Line:%d)";

    private static String buildPrintTag2(LogTag tag) {
        String printTag = printSimpleClassName ? tag.simpleClassName : tag.className;
        return String.format(Locale.getDefault(), PRINT_TAG_FMT, printTag, tag.methodName, tag.line);
    }

    private static final String PRINT_MSG_FMT = "%s:(Line:%d): %s";

    private static String buildPrintMsg(LogTag tag, String msg) {
        return String.format(Locale.getDefault(), PRINT_MSG_FMT, tag.methodName, tag.line, msg);
    }

    private static String buildPrintMsg(Throwable e) {
        if (e == null) {
            return "";
        } else {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            pw.flush();
            return sw.toString();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private static final String DATE_TIME_FMT = "%4d-%2d-%2d %2d:%2d:%2d:%3d";
    private static final String SAVE_LOG_FMT = "%s(PID=%5d)/%s/%s:%s\n";

    private static void saveLog(String level, String tag, String msg) {
        GregorianCalendar calendar = new GregorianCalendar();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int millisecond = calendar.get(Calendar.MILLISECOND);

        String dateTime = String.format(Locale.getDefault(), DATE_TIME_FMT, year, month, day, hour, minute, second, millisecond);
        String log = String.format(Locale.getDefault(), SAVE_LOG_FMT, dateTime, Process.myPid(), level, tag, msg);

        if (enableSaveLog) {
            saveLogThread.save(log);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private static class SaveLogThread extends Thread {
        private final String _LOCK = SaveLogThread.class.getName() + "._LOCK";
        private boolean isStopSave;
        private List<String> logList;
        private PrintWriter printWriter;

        private SaveLogThread(String logFilePath, String charset) throws Exception {
            isStopSave = false;
            logList = new LinkedList<>();
            printWriter = new PrintWriter(logFilePath, charset);
        }

        private void stopSave() {
            synchronized (_LOCK) {
                if (!isStopSave) {
                    isStopSave = true;
                    SaveLogThread.this.interrupt();
                }
            }
        }

        private void save(String log) {
            synchronized (_LOCK) {
                logList.add(log);
                _LOCK.notifyAll();
            }
        }

        @Override
        public void run() {
            try {
                while (!SaveLogThread.this.isInterrupted() && !isStopSave) {
                    String log;

                    synchronized (_LOCK) {
                        if (logList.isEmpty()) {
                            _LOCK.wait();
                        }
                        log = logList.remove(0);
                    }

                    if (null != log) {
                        printWriter.write(log);
                        printWriter.flush();
                    }
                }
            } catch (Exception e) {
                logList.clear();
                printWriter.flush();
                printWriter.close();
                if (!isStopSave) {
                    e(e);
                }
            }
        }
    }
}
