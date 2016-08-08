package com.hit.wi.t9.functions;

import android.os.Environment;
import org.apache.http.util.EncodingUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by purebleusong on 2015/6/4.
 */
public class FileManager {
    //this maybe object to the law SRP todo: need you to clean
    private File folder;
    private String filePath;
    private String fileContentSeparator;
    private static FileManager fileManager = new FileManager();

    private FileManager() {
        //this break the law of demeter
        filePath = Environment.getExternalStorageDirectory().getPath() + File.separator + "WIinputmethod_VE";
        fileContentSeparator = "\n";
        folder = new File(filePath);
        if (!folder.exists()) {
            folder.mkdir();
        }
    }

    public static FileManager getInstance() {
        return fileManager;
    }

    private String readContentFromFile(String filename) throws IOException {
        File file = new File(
                filePath + File.separator
                        + filename
        );
        FileInputStream fis = new FileInputStream(file);
        int length = fis.available();
        byte[] buffer = new byte[length];
        fis.read(buffer);

        String contentString = EncodingUtils.getString(buffer, "UTF-8");
        fis.close();

        return contentString;
    }

    public List<Integer> readIntegerListFromFile(String filename) throws IOException {
        String contentString = readContentFromFile(filename);
        String[] strings = contentString.split(fileContentSeparator);

        List<Integer> valueList = new ArrayList<Integer>();
        for (String each : strings) {
            valueList.add(Integer.valueOf(each));
        }

        return valueList;
    }

    public int[] readIntArrayFromFile(String filename) throws IOException {
        String contentString = readContentFromFile(filename);

        String[] strings = contentString.split(fileContentSeparator);
        int[] values = new int[strings.length];
        for (int i = 0; i < strings.length; i++) {
            values[i] = Integer.parseInt(strings[i]);
        }
        return values;
    }

    public String[] readStringArrayFromFile(String filename) throws IOException {
        String contentString = readContentFromFile(filename);
        String[] strings = contentString.split(fileContentSeparator);
        return strings;
    }

    public void writeToFile(String filename, String texts) throws IOException {
        File file = new File(
                filePath + File.separator
                        + filename
        );

        FileOutputStream fos = new FileOutputStream(file);
        fos.write(texts.getBytes());
        fos.close();
    }

    public void writeToFile(String filename, String[] texts) throws IOException {
        File file = new File(
                filePath + File.separator
                        + filename
        );
        FileOutputStream fos = new FileOutputStream(file);

        String content = "";
        for (String each : texts) {
            content += each + fileContentSeparator;
        }
        if (content != "") content = content.substring(0, content.length() - 1);

        fos.write(content.getBytes());
        fos.close();
    }

    public void writeToFile(String filename, int[] values) throws IOException {
        File file = new File(
                filePath + File.separator
                        + filename
        );
        FileOutputStream fos = new FileOutputStream(file);

        String content = "";
        for (int each : values) {
            content += each + fileContentSeparator;
        }
        content = content.substring(0, content.length() - 1);// drop the enter

        fos.write(content.getBytes());
        fos.close();
    }

    public void writeToFile(String filename, List<Integer> values) throws IOException {
        File file = new File(
                filePath + File.separator
                        + filename
        );
        FileOutputStream fos = new FileOutputStream(file);

        String content = "";
        for (Integer each : values) {
            content += each + fileContentSeparator;
        }
        content = content.substring(0, content.length() - 1);// drop the enter

        fos.write(content.getBytes());
        fos.close();
    }

}
