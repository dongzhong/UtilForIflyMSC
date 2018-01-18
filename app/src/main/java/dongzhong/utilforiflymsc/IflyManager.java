package dongzhong.utilforiflymsc;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;

import java.io.File;
import java.io.FileInputStream;

import dongzhong.utilforiflymsc.configs.Configs;
import dongzhong.utilforiflymsc.exceptions.InitException;
import dongzhong.utilforiflymsc.util.Parser;

/**
 * Created by dongzhong on 2018/1/17.
 */

public class IflyManager {
    private static IflyManager instance;

    private Context context;
    private SpeechRecognizer speechRecognizer;

    private IflyRecognizeListener listener;

    private IflyManager(Context context) throws InitException {
        String appid = Configs.getAppId();
        if (appid == null) {
            throw new InitException("appid is null");
        }
        this.context = context;
        SpeechUtility.createUtility(context, SpeechConstant.APPID +"=" + appid + "," + SpeechConstant.ENGINE_MODE + "=" + SpeechConstant.MODE_MSC);
        speechRecognizer = SpeechRecognizer.createRecognizer(context, new InitListener() {
            @Override
            public void onInit(int i) {

            }
        });
        initSpeechRecognizer();
    }

    private void initSpeechRecognizer() {
        if (speechRecognizer != null) {
            // 清空参数
            speechRecognizer.setParameter(SpeechConstant.PARAMS, null);

            speechRecognizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
            speechRecognizer.setParameter(SpeechConstant.VAD_ENABLE, "1");
            speechRecognizer.setParameter(SpeechConstant.VAD_BOS, "8000");
            speechRecognizer.setParameter(SpeechConstant.VAD_EOS, "500");
            speechRecognizer.setParameter(SpeechConstant.ASR_PTT, "0");
            speechRecognizer.setParameter(SpeechConstant.RESULT_TYPE, "json");
            speechRecognizer.setParameter(SpeechConstant.MIXED_THRESHOLD, "60");
            speechRecognizer.setParameter(SpeechConstant.NET_TIMEOUT, "2000");
        }
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

    public void startRecognize(@NonNull String fileName) {
        speechRecognizer.setParameter(SpeechConstant.AUDIO_SOURCE, "-2");
        speechRecognizer.setParameter(SpeechConstant.ASR_SOURCE_PATH, fileName);
        File file = new File(fileName);
        if (!file.exists()) {
            return;
        }
        speechRecognizer.startListening(recognizerListener);
    }


    private RecognizerListener recognizerListener = new RecognizerListener() {
        @Override
        public void onVolumeChanged(int i, byte[] bytes) {

        }

        @Override
        public void onBeginOfSpeech() {

        }

        @Override
        public void onEndOfSpeech() {

        }

        @Override
        public void onResult(RecognizerResult recognizerResult, boolean isLast) {
            String iatReuslt = Parser.iatJsonResult2String(recognizerResult.getResultString());
            if (listener != null) {
                listener.onResult(iatReuslt);
                if (isLast) {
                    listener.onResultFinal(iatReuslt);
                }
            }
        }

        @Override
        public void onError(SpeechError speechError) {

        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };

    public void setRecognizeListener(IflyRecognizeListener listener) {
        this.listener = listener;
    }

    public interface IflyRecognizeListener {
        /**
         * 获取实时识别结果
         *
         * @param result
         */
        void onResult(String result);

        /**
         * 获取最终结果
         *
         * @param finalResult
         */
        void onResultFinal(String finalResult);

        /**
         * 识别异常
         *
         * @param errorCode
         * @param errorDes
         */
        void onError(int errorCode, String errorDes);
    }
}
