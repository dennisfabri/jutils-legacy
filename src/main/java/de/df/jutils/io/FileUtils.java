/*
 * Created on 28.02.2006
 */
package de.df.jutils.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;

public final class FileUtils {

    private FileUtils() {
        // Hide constructor
    }

    public static final String[] EMPTY = new String[0];

    public static String[] readTextFile(String name) {
        return readTextFile(new File(name));
    }

    public static String[] readTextFile(File name) {
        FileInputStream is = null;
        BufferedReader br = null;
        try {
            is = new FileInputStream(name);
            br = new BufferedReader(new InputStreamReader(is, "Cp1252"));
            LinkedList<String> result = new LinkedList<String>();
            String data = br.readLine();
            while (data != null) {
                if (data.length() > 0) {
                    result.addLast(data);
                }
                data = br.readLine();
            }
            return result.toArray(new String[result.size()]);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    // Nothing to do
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    // Nothing to do
                }
            }
        }
    }

    public static String[] readTextFile(String name, String encoding) {
        return readTextFile(new File(name), encoding);
    }

    public static String[] readTextFile(File name, String encoding) {
        FileInputStream is = null;
        BufferedReader br = null;
        try {
            is = new FileInputStream(name);
            br = new BufferedReader(new InputStreamReader(is, encoding));
            LinkedList<String> result = new LinkedList<String>();
            String data = br.readLine();
            while (data != null) {
                if (data.length() > 0) {
                    result.addLast(data);
                }
                data = br.readLine();
            }
            return result.toArray(new String[result.size()]);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    // Nothing to do
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    // Nothing to do
                }
            }
        }
    }

    public static String[] readTextFile(InputStream is) {
        return readTextFile(is, "Cp1252");
    }

    public static String[] readTextFile(InputStream is, String encoding) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is, encoding));
            LinkedList<String> result = new LinkedList<String>();
            String data = br.readLine();
            while (data != null) {
                if (data.length() > 0) {
                    result.addLast(data);
                }
                data = br.readLine();
            }
            return result.toArray(new String[result.size()]);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] readFile(InputStream is) throws IOException {
        BytesOutputStream bos = new BytesOutputStream();
        try {
            byte[] temp = new byte[1024];
            int size = is.read(temp);
            while (size >= 0) {
                bos.write(temp, 0, size);
                size = is.read(temp);
            }
            return bos.getData();
        } finally {
            bos.close();
        }
    }
}
