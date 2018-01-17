package dongzhong.utilforiflymsc;

import android.content.Context;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

import dongzhong.utilforiflymsc.configs.Configs;
import dongzhong.utilforiflymsc.exceptions.InitException;

/**
 * Created by dongzhong on 2018/1/17.
 */

public class IflyManager {
    private static IflyManager instance;

    private IflyManager(Context context) throws InitException {
        String appid = Configs.getAppId();
        if (appid == null) {
            throw new InitException("appid is null");
        }
        SpeechUtility.createUtility(context, SpeechConstant.APPID +"=" + appid + "," + SpeechConstant.ENGINE_MODE + "=" + SpeechConstant.MODE_MSC);
    }

    public static IflyManager getInstance(Context context) throws InitException {
        if (instance == null) {
            synchronized (IflyManager.class) {
                if (instance == null) {
                    instance = new IflyManager(context);
                }
            }
        }
        return instance;
    }
}
