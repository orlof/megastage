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
 * @author Teppo
 */
public class LogFormat extends Logger {
    
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss.S");
    
    public void log(int level, String category, String message, Throwable ex) {
        StringBuilder builder = new StringBuilder(256);
        builder.append(sdf.format(new Date()));
        builder.append(" ");
        builder.append(level);
        builder.append(" [");
        builder.append(category);
        builder.append("] ");
        builder.append(message);
        
        System.out.println(builder);
    }
}
