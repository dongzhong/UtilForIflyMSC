package dongzhong.utilforiflymsc.configs;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;

/**
 * Created by dongzhong on 2018/1/17.
 */

public class Configs {
    private final static String SdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    private final static String filePath = "ifly";
    private final static String fileName = "appid.txt";

    private static File fileDir;
    private static File file;
    private static FileInputStream fileInputStream;
    static {
        fileDir = new File(SdPath, filePath);
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }
        file = new File(fileDir, fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            }
            catch (Exception e) {

            }
        }
        try {
            fileInputStream = new FileInputStream(file);
        }
        catch (Exception e) {

        }
    }

    public static String getAppId() {
        if (fileInputStream == null) {
            return null;
        }
        try {
            int length = fileInputStream.available();
            byte[] buffer = new byte[length];
            fileInputStream.read(buffer);
            String appid = new String(buffer);
            return appid;
        }
        catch (Exception e) {
            return null;
        }
    }
}
