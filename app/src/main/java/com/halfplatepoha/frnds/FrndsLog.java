package com.halfplatepoha.frnds;

import android.util.Log;

import java.util.Locale;

/**
 * Created by surajkumarsau on 27/08/16.
 */
public class FrndsLog {
    public static final String LOG_TAG = "frnds";

    private static boolean DEBUG = true;
    private static final boolean VERBOSE = true;

    private static ErrorLogHandler mErrLogHandler = null;


    public static void enableDebugLog(boolean enable){
        DEBUG = enable;
    }

    public static void v(String msg) {
        if(VERBOSE) Log.v(LOG_TAG, buildMessage(msg));
    }

    public static void d(String msg) {
        if(DEBUG) Log.d(LOG_TAG, buildMessage(msg));
    }

    public static void e(String msg) {
        if(DEBUG)   Log.e(LOG_TAG, buildMessage(msg));
    }

    public static void e(String msg,Throwable tr) {
        Log.e(LOG_TAG, buildMessage(msg), tr);
        if(mErrLogHandler != null){
            mErrLogHandler.onErrorLogged(msg,tr);
        }
    }

    public static void wtf(String msg) {
        Log.wtf(LOG_TAG, buildMessage(msg));
    }

    public static void wtf(String msg, Throwable tr) {
        Log.wtf(LOG_TAG, buildMessage(msg), tr);
    }

    private static String buildMessage(String msg) {
        StackTraceElement[] trace = (new Throwable()).fillInStackTrace().getStackTrace();
        String caller = "<unknown>";

        for(int i = 2; i < trace.length; ++i) {
            Class clazz = trace[i].getClass();
            if(!clazz.equals(FrndsLog.class)) {
                String callingClass = trace[i].getClassName();
                callingClass = callingClass.substring(callingClass.lastIndexOf(46) + 1);
                callingClass = callingClass.substring(callingClass.lastIndexOf(36) + 1);
                caller = callingClass + "." + trace[i].getMethodName()+":"+trace[i].getLineNumber();
                break;
            }
        }

        return String.format(Locale.US, "[%d] %s: %s", new Object[]{Long.valueOf(Thread.currentThread().getId()), caller, msg});
    }


    public static void i(String s) {
        if(VERBOSE) Log.i(LOG_TAG, buildMessage(s));

    }

    public static void setErrorLogHandler(ErrorLogHandler handler) {
        mErrLogHandler = handler;
    }


    public interface ErrorLogHandler{

        void onErrorLogged(String msg,Throwable t);
    }

    public static boolean isLogEnabled() {
        return DEBUG;
    }
}
