package org.megastage.components.dcpu;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FloppyDisk {

    public char[] data = new char[737280];
    private boolean writeProtected;

    public void load(File file) {
        try {
            DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
            int i = 0;
            for (; i < data.length; i++) {
                data[i] = dis.readChar();
            }
            dis.close();
        } catch (ArrayIndexOutOfBoundsException e) {
        } catch (IOException e) {
        }
    }

    public void save(File file) throws IOException {
        DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
        try {
            for (int i = 0; i < data.length; i++) {
                dos.writeChar(data[i]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        dos.close();
    }

    public boolean isWriteProtected() {
        return writeProtected;
    }

    public void setWriteProtected(boolean writeProtected) {
        this.writeProtected = writeProtected;
    }
}