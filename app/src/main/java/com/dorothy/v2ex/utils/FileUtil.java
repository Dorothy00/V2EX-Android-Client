package com.dorothy.v2ex.utils;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by dorothy on 2016/11/9.
 */

public class FileUtil {
    private static final String NODE_FILE = "node.txt";

    public static boolean saveObject(Context context, Serializable ser) {
        FileOutputStream fos;
        fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = context.openFileOutput(NODE_FILE, context.MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(ser);
            oos.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                oos.close();
            } catch (Exception e) {
            }
            try {
                fos.close();
            } catch (Exception e) {
            }
        }
    }

    /**
     * 读取对象
     *
     * @return
     * @throws IOException
     */
    public static Serializable readObject(Context context) {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = context.openFileInput(NODE_FILE);
            ois = new ObjectInputStream(fis);
            return (Serializable) ois.readObject();
        } catch (FileNotFoundException e) {
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                ois.close();
            } catch (Exception e) {
            }
            try {
                fis.close();
            } catch (Exception e) {
            }
        }
        return null;
    }

    public static boolean deleteObject(Context context) {
        return context.deleteFile(NODE_FILE);
    }
}
