/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.util;

import com.esotericsoftware.minlog.Log.Logger;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Orlof
 */
public class LogFormat extends Logger {
    
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss.SSS");
    
    public void log(int level, String category, String message, Throwable ex) {
        StackTraceElement caller = Thread.currentThread().getStackTrace()[3];
        
        StringBuilder builder = new StringBuilder(256);
        builder.append(sdf.format(new Date()));
        builder.append(" ");
        builder.append(level);
        builder.append(" [");
        
        String className = caller.getClassName();
        className = className.substring(className.lastIndexOf(".")+1);
        
        builder.append(className + "." + caller.getMethodName());
        builder.append("] ");
        builder.append(message);
        
        System.out.println(builder);
    }
}
