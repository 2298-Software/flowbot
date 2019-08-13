package com.trite.apps.flowbot.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Created by joe on 8/13/2019.
 */
public class Util {
    public static String getStackTraceString(Exception e) {
        Writer writer = new StringWriter();
        e.printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }

}
