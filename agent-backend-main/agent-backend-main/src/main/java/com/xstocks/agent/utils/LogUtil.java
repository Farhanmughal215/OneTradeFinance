package com.xstocks.agent.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

public class LogUtil {
    public static String getExceptionToString(Throwable e) {
        if (e == null) {
            return "";
        }
        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }
}
