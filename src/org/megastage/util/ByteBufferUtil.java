package org.megastage.util;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.CharArrayReader;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * MegaStage
 * User: Orlof
 * Date: 21.8.2013
 * Time: 23:00
 */
public class ByteBufferUtil {
    public static char[] toCharArray(ByteBuffer buffer) {
        char[] data = new char[buffer.remaining()];

        for(int index=0; buffer.remaining() > 0; index++) {
            data[index] = buffer.getChar();
        }

        return data;
    }
    
    public static Element createElement(ByteBuffer buffer) {
        try {
            char[] data = toCharArray(buffer);
            return new SAXBuilder().build(new CharArrayReader(data)).getRootElement();
        } catch (JDOMException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }
}
