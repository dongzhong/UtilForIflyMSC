package dongzhong.utilforiflymsc.util;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by dongzhong on 2018/1/18.
 */

public class LogUtil {
    private static String resultFileName = "result.txt";
    private static String filePathName = "/mnt/sdcard/ifly";
    private static FileOutputStream resultFileOutputStream;
    static {
        File fileDir = new File(filePathName);
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }
        File resultFile = new File(fileDir, resultFileName);
        if (!resultFile.exists()) {
            try {
                resultFile.createNewFile();
            }
            catch (Exception e) {

            }
        }
        try {
            resultFileOutputStream = new FileOutputStream(resultFile);
        }
        catch (Exception e) {

        }
    }

    /**
     * 将识别结果写入文件
     * 文件位置：/mnt/sdcard/ifly/result.txt
     *
     * @param content
     */
    public static void writeToResultFile(String content) {
        if (resultFileOutputStream != null) {
            try {
                resultFileOutputStream.write(content.getBytes());
            }
            catch (Exception e) {

            }
        }
    }
}
